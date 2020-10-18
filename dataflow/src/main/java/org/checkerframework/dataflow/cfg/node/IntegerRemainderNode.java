package org.checkerframework.dataflow.cfg.node;

import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.Tree.Kind;
import java.util.Objects;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A node for the integer remainder:
 *
 * <pre>
 *   <em>expression</em> % <em>expression</em>
 * </pre>
 */
public class IntegerRemainderNode extends BinaryOperationNode {

    public IntegerRemainderNode(BinaryTree tree, Node left, Node right) {
        super(tree, left, right);
        assert tree.getKind() == Kind.REMAINDER;
    }

    @Override
    public <R, P> R accept(NodeVisitor<R, P> visitor, P p) {
        return visitor.visitIntegerRemainder(this, p);
    }

    @Override
    public @PolyDet String toString(@PolyDet IntegerRemainderNode this) {
        return "(" + getLeftOperand() + " % " + getRightOperand() + ")";
    }

    @Override
    public @PolyDet boolean equals(
            @PolyDet IntegerRemainderNode this, @PolyDet @Nullable Object obj) {
        if (!(obj instanceof IntegerRemainderNode)) {
            return false;
        }
        IntegerRemainderNode other = (IntegerRemainderNode) obj;
        return getLeftOperand().equals(other.getLeftOperand())
                && getRightOperand().equals(other.getRightOperand());
    }

    @Override
    public @PolyDet int hashCode(@PolyDet IntegerRemainderNode this) {
        return Objects.hash(getLeftOperand(), getRightOperand());
    }
}
