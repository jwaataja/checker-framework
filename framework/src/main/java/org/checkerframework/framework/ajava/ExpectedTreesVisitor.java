package org.checkerframework.framework.ajava;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.IfTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.NewArrayTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.VariableTree;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Stores each visited tree that should match with some JavaParser node if the same Java file was
 * parsed with both.
 */
public class ExpectedTreesVisitor extends TreeScannerWithDefaults {
    private Set<Tree> trees;

    public ExpectedTreesVisitor() {
        trees = new HashSet<>();
    }

    public Set<Tree> getTrees() {
        return trees;
    }

    @Override
    public void defaultAction(Tree tree) {
        trees.add(tree);
    }

    @Override
    public Void visitAnnotation(AnnotationTree tree, Void p) {
        // Skip annotations because ajava files are not required to have the same annotations as
        // their corresponding java files.
        return null;
    }

    @Override
    public Void visitClass(ClassTree tree, Void p) {
        defaultAction(tree);
        visit(tree.getModifiers(), p);
        visit(tree.getTypeParameters(), p);
        visit(tree.getExtendsClause(), p);
        visit(tree.getImplementsClause(), p);
        if (tree.getKind() == Kind.ENUM) {
            // Enum constants expand to a VariableTree like
            // public static final MY_ENUM_CONSTANT = new MyEnum(args ...)
            // The constructor invocation in the initializer has no corresponding JavaParser node.
            // Remove these invocations. This is safe because it's illegal to explicitly construct
            // an instance of an enum anyway.
            for (Tree member : tree.getMembers()) {
                member.accept(this, p);
                if (member.getKind() != Kind.VARIABLE) {
                    continue;
                }

                VariableTree variable = (VariableTree) member;
                ExpressionTree initializer = variable.getInitializer();
                if (initializer == null || initializer.getKind() != Kind.NEW_CLASS) {
                    continue;
                }

                NewClassTree constructor = (NewClassTree) initializer;
                if (constructor.getIdentifier().getKind() != Kind.IDENTIFIER) {
                    continue;
                }

                IdentifierTree name = (IdentifierTree) constructor.getIdentifier();
                if (name.getName().contentEquals(tree.getSimpleName())) {
                    trees.remove(variable.getType());
                    trees.remove(constructor);
                }
            }
        } else {
            visit(tree.getMembers(), p);
        }

        return null;
    }

    @Override
    public Void visitExpressionStatement(ExpressionStatementTree tree, Void p) {
        // Javac inserts calls to super() at the start of constructors with no this or super call.
        // These don't have matching JavaParser nodes.
        if (JointJavacJavaParserVisitor.isDefaultSuperConstructorCall(tree)) {
            return null;
        }

        return super.visitExpressionStatement(tree, p);
    }

    @Override
    public Void visitIf(IfTree tree, Void p) {
        // In an if statement, javac stores the condition as a parenthesized expression, which has
        // no corresponding JavaParserNode, so remove the parenthesized expression, but not its
        // child.
        Void result = super.visitIf(tree, p);
        trees.remove(tree.getCondition());
        return result;
    }

    @Override
    public Void visitImport(ImportTree tree, Void p) {
        // Javac stores an import like a.* as a member select, but JavaParser just stores "a", so
        // don't add the whole member select in that case.
        if (tree.getQualifiedIdentifier().getKind() == Kind.MEMBER_SELECT) {
            MemberSelectTree memberSelect = (MemberSelectTree) tree.getQualifiedIdentifier();
            if (memberSelect.getIdentifier().contentEquals("*")) {
                memberSelect.getExpression().accept(this, p);
                return null;
            }
        }

        return super.visitImport(tree, p);
    }

    @Override
    public Void visitMethod(MethodTree tree, Void p) {
        // Synthetic default constructors don't have matching JavaParser nodes. Consertaively skip
        // no argument constructor calls, even if they may not be synthetic.
        if (JointJavacJavaParserVisitor.isNoArgumentConstructor(tree)) {
            return null;
        }

        Void result = super.visitMethod(tree, p);
        // A varargs parameter like String... is converted to String[], where the array type doesn't
        // have a corresponding JavaParser node. Conservatively skip the array type (but not the
        // component type) if it's the last argument.
        if (!tree.getParameters().isEmpty()) {
            VariableTree last = tree.getParameters().get(tree.getParameters().size() - 1);
            if (last.getType().getKind() == Kind.ARRAY_TYPE) {
                trees.remove(last.getType());
            }
        }

        return result;
    }

    @Override
    public Void visitMethodInvocation(MethodInvocationTree tree, Void p) {
        Void result = super.visitMethodInvocation(tree, p);
        // In a method invocation like myObject.myMethod(), the method invocation stores
        // myObject.myMethod as its own MemberSelectTree which has no corresponding JavaParserNode.
        // This node should not be checked.
        if (tree.getMethodSelect().getKind() == Kind.MEMBER_SELECT) {
            trees.remove(tree.getMethodSelect());
        }

        return result;
    }

    @Override
    public Void visitModifiers(ModifiersTree tree, Void p) {
        // Don't add ModifierTrees or children because they have no corresponding JavaParser node.
        return null;
    }

    @Override
    public Void visitNewArray(NewArrayTree tree, Void p) {
        // Skip array initialization because it's not implemented yet.
        return null;
    }

    @Override
    public Void visitNewClass(NewClassTree tree, Void p) {
        defaultAction(tree);

        if (tree.getEnclosingExpression() != null) {
            tree.getEnclosingExpression().accept(this, p);
        }

        tree.getIdentifier().accept(this, p);
        for (Tree typeArgument : tree.getTypeArguments()) {
            typeArgument.accept(this, p);
        }

        for (Tree arg : tree.getTypeArguments()) {
            arg.accept(this, p);
        }

        if (tree.getClassBody() == null) {
            return null;
        }

        // Anonymous class bodies require special handling. There isn't a corresponding JavaParser
        // node, and synthetic constructors must be skipped.
        ClassTree body = tree.getClassBody();
        visit(body.getModifiers(), p);
        visit(body.getTypeParameters(), p);
        visit(body.getExtendsClause(), p);
        visit(body.getImplementsClause(), p);
        for (Tree member : body.getMembers()) {
            // Constructors cannot be declared in an anonymous class, so don't add them.
            if (member.getKind() == Kind.METHOD) {
                MethodTree methodTree = (MethodTree) member;
                if (methodTree.getName().contentEquals("<init>")) {
                    continue;
                }
            }

            member.accept(this, p);
        }

        return null;
    }

    private void visit(Tree tree, Void p) {
        if (tree != null) {
            tree.accept(this, p);
        }
    }

    private void visit(List<? extends Tree> trees, Void p) {
        if (trees == null) {
            return;
        }

        for (Tree tree : trees) {
            visit(tree, p);
        }
    }
}