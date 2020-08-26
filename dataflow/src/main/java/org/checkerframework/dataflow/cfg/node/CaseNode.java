package org.checkerframework.dataflow.cfg.node;

import com.sun.source.tree.CaseTree;
import com.sun.source.tree.Tree.Kind;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Types;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A node for a case in a switch statement. Although a case has no abstract value, it can imply
 * facts about the abstract values of its operands.
 *
 * <pre>
 *   case <em>constant</em>:
 * </pre>
 */
public class CaseNode extends Node {

    /** The tree for this node. */
    protected final CaseTree tree;
    /** The switch expression. */
    protected final Node switchExpr;
    /** The case expression to match the switch expression against. */
    protected final Node caseExpr;

    /**
     * Create a new CaseNode.
     *
     * @param tree the tree for this node
     * @param switchExpr the switch expression
     * @param caseExpr the case expression to match the switch expression against
     * @param types a factory of utility methods for operating on types
     */
    public CaseNode(CaseTree tree, Node switchExpr, Node caseExpr, Types types) {
        super(types.getNoType(TypeKind.NONE));
        assert tree.getKind() == Kind.CASE;
        this.tree = tree;
        this.switchExpr = switchExpr;
        this.caseExpr = caseExpr;
    }

    public @PolyDet Node getSwitchOperand(@PolyDet CaseNode this) {
        return switchExpr;
    }

    public @PolyDet Node getCaseOperand(@PolyDet CaseNode this) {
        return caseExpr;
    }

    @Override
    public @PolyDet CaseTree getTree(@PolyDet CaseNode this) {
        return tree;
    }

    @Override
    public <R, P> R accept(NodeVisitor<R, P> visitor, P p) {
        return visitor.visitCase(this, p);
    }

    @Override
    public @PolyDet String toString(@PolyDet CaseNode this) {
        return "case " + getCaseOperand() + ":";
    }

    @Override
    public @PolyDet boolean equals(@PolyDet CaseNode this, @PolyDet @Nullable Object obj) {
        if (!(obj instanceof CaseNode)) {
            return false;
        }
        CaseNode other = (CaseNode) obj;
        return getSwitchOperand().equals(other.getSwitchOperand())
                && getCaseOperand().equals(other.getCaseOperand());
    }

    @Override
    public @NonDet int hashCode(@PolyDet CaseNode this) {
        return Objects.hash(getSwitchOperand(), getCaseOperand());
    }

    @Override
    public Collection<Node> getOperands() {
        ArrayList<Node> list = new ArrayList<>(2);
        list.add(getSwitchOperand());
        list.add(getCaseOperand());
        return list;
    }
}
