package org.checkerframework.dataflow.cfg.node;

import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.Tree.Kind;
import java.util.Objects;
import org.checkerframework.checker.determinism.qual.*;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A node for an equality check:
 *
 * <pre>
 *   <em>expression</em> == <em>expression</em>
 * </pre>
 */
public class EqualToNode extends BinaryOperationNode {

    /**
     * Create a new EqualToNode object.
     *
     * @param tree the tree for this node
     * @param left the first argument
     * @param right the second argument
     */
    public EqualToNode(BinaryTree tree, Node left, Node right) {
        super(tree, left, right);
        assert tree.getKind() == Kind.EQUAL_TO;
    }

    @Override
    public <R, P> R accept(NodeVisitor<R, P> visitor, P p) {
        return visitor.visitEqualTo(this, p);
    }

    @Override
    public @PolyDet String toString(@PolyDet EqualToNode this) {
        return "(" + getLeftOperand() + " == " + getRightOperand() + ")";
    }

    @Override
    public @PolyDet boolean equals(@PolyDet EqualToNode this, @PolyDet @Nullable Object obj) {
        if (!(obj instanceof EqualToNode)) {
            return false;
        }
        EqualToNode other = (EqualToNode) obj;
        return getLeftOperand().equals(other.getLeftOperand())
                && getRightOperand().equals(other.getRightOperand());
    }

    @Override
    public @NonDet int hashCode(@PolyDet EqualToNode this) {
        return Objects.hash(getLeftOperand(), getRightOperand());
    }
}
