package org.checkerframework.dataflow.cfg.node;

import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.Tree.Kind;
import java.util.Objects;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A node for the bitwise or logical (single bit) or operation:
 *
 * <pre>
 *   <em>expression</em> | <em>expression</em>
 * </pre>
 */
public class BitwiseOrNode extends BinaryOperationNode {

    public BitwiseOrNode(BinaryTree tree, Node left, Node right) {
        super(tree, left, right);
        assert tree.getKind() == Kind.OR;
    }

    @Override
    public <R, P> R accept(NodeVisitor<R, P> visitor, P p) {
        return visitor.visitBitwiseOr(this, p);
    }

    @Override
    public @PolyDet String toString(@PolyDet BitwiseOrNode this) {
        return "(" + getLeftOperand() + " | " + getRightOperand() + ")";
    }

    @Override
    public @PolyDet boolean equals(@PolyDet BitwiseOrNode this, @PolyDet @Nullable Object obj) {
        if (!(obj instanceof BitwiseOrNode)) {
            return false;
        }
        BitwiseOrNode other = (BitwiseOrNode) obj;
        return getLeftOperand().equals(other.getLeftOperand())
                && getRightOperand().equals(other.getRightOperand());
    }

    @Override
    public @PolyDet int hashCode(@PolyDet BitwiseOrNode this) {
        return Objects.hash(getLeftOperand(), getRightOperand());
    }
}
