package org.checkerframework.dataflow.cfg.block;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.checkerframework.checker.determinism.qual.OrderNonDet;

/** Base class of the {@link Block} implementation hierarchy. */
public abstract class BlockImpl implements Block {

    /** A unique ID for this block. */
    protected final long id = BlockImpl.uniqueID();

    /** The last ID that has already been used. */
    protected static long lastId = 0;

    /** The type of this basic block. */
    protected final BlockType type;

    /** The set of predecessors. */
    protected final @OrderNonDet Set<BlockImpl> predecessors;

    /**
     * Returns a fresh identifier.
     *
     * @return a fresh identifier
     */
    private static long uniqueID() {
        return lastId++;
    }

    protected BlockImpl(BlockType type) {
        this.type = type;
        this.predecessors = new HashSet<>();
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public BlockType getType() {
        return type;
    }

    @Override
    public @OrderNonDet Set<Block> getPredecessors() {
        return Collections.unmodifiableSet(predecessors);
    }

    public void addPredecessor(BlockImpl pred) {
        predecessors.add(pred);
    }

    public void removePredecessor(BlockImpl pred) {
        predecessors.remove(pred);
    }
}
