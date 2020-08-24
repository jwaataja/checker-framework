package org.checkerframework.dataflow.cfg.node;

import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.Tree.Kind;
import java.util.Objects;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A node for bitwise right shift operations with zero extension:
 *
 * <pre>
 *   <em>expression</em> &gt;&gt;&gt; <em>expression</em>
 * </pre>
 */
public class UnsignedRightShiftNode extends BinaryOperationNode {

    public UnsignedRightShiftNode(BinaryTree tree, Node left, Node right) {
        super(tree, left, right);
        assert tree.getKind() == Kind.UNSIGNED_RIGHT_SHIFT;
    }

    @Override
    public <R, P> R accept(NodeVisitor<R, P> visitor, P p) {
        return visitor.visitUnsignedRightShift(this, p);
    }

    @Override
    public @PolyDet String toString(@PolyDet UnsignedRightShiftNode this) {
        return "(" + getLeftOperand() + " >>> " + getRightOperand() + ")";
    }

    @Override
    @SuppressWarnings("determinism") // calling equals on two @PolyDet returns @NonDet
    public @PolyDet boolean equals(@PolyDet @Nullable Object obj) {
        if (!(obj instanceof UnsignedRightShiftNode)) {
            return false;
        }
        UnsignedRightShiftNode other = (UnsignedRightShiftNode) obj;
        return getLeftOperand().equals(other.getLeftOperand())
                && getRightOperand().equals(other.getRightOperand());
    }

    @Override
    public @NonDet int hashCode(@PolyDet UnsignedRightShiftNode this) {
        return Objects.hash(getLeftOperand(), getRightOperand());
    }
}
