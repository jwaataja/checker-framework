package org.checkerframework.dataflow.cfg.builder;

import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.dataflow.cfg.builder.ExtendedNode.ExtendedNodeType;
import org.checkerframework.dataflow.cfg.node.Node;

/** An extended node of type {@code NODE}. */
class NodeHolder extends ExtendedNode {

    /** The node to hold. */
    protected final Node node;

    /**
     * Construct a NodeHolder for the given Node.
     *
     * @param node the node to hold
     */
    public NodeHolder(Node node) {
        super(ExtendedNodeType.NODE);
        this.node = node;
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    public @PolyDet String toString(@PolyDet NodeHolder this) {
        return "NodeHolder(" + node + ")";
    }

    @Override
    public @PolyDet String toStringDebug() {
        return "NodeHolder(" + node.toStringDebug() + ")";
    }
}
