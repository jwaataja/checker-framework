package org.checkerframework.dataflow.cfg.node;

import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.Tree.Kind;
import java.util.Objects;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A node for the numerical multiplication:
 *
 * <pre>
 *   <em>expression</em> * <em>expression</em>
 * </pre>
 */
public class NumericalMultiplicationNode extends BinaryOperationNode {

    public NumericalMultiplicationNode(BinaryTree tree, Node left, Node right) {
        super(tree, left, right);
        assert tree.getKind() == Kind.MULTIPLY;
    }

    @Override
    public <R, P> R accept(NodeVisitor<R, P> visitor, P p) {
        return visitor.visitNumericalMultiplication(this, p);
    }

    @Override
    public @PolyDet String toString(@PolyDet NumericalMultiplicationNode this) {
        return "(" + getLeftOperand() + " * " + getRightOperand() + ")";
    }

    @Override
    @SuppressWarnings("determinism") // calling equals on two @PolyDet returns @NonDet
    public @PolyDet boolean equals(
            @PolyDet NumericalMultiplicationNode this, @PolyDet @Nullable Object obj) {
        if (!(obj instanceof NumericalMultiplicationNode)) {
            return false;
        }
        NumericalMultiplicationNode other = (NumericalMultiplicationNode) obj;
        return getLeftOperand().equals(other.getLeftOperand())
                && getRightOperand().equals(other.getRightOperand());
    }

    @Override
    public @NonDet int hashCode(@PolyDet NumericalMultiplicationNode this) {
        return Objects.hash(getLeftOperand(), getRightOperand());
    }
}
