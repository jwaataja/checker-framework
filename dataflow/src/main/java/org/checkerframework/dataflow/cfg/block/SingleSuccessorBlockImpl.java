package org.checkerframework.dataflow.cfg.block;

import java.util.LinkedHashSet;
import java.util.Set;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.analysis.Store;

/**
 * A basic block that has at most one successor. SpecialBlockImpl extends this, but exit blocks have
 * no successor.
 */
public abstract class SingleSuccessorBlockImpl extends BlockImpl implements SingleSuccessorBlock {

    /** Internal representation of the successor. */
    protected @Nullable BlockImpl successor;

    /**
     * The initial value for the rule below says that EACH store at the end of a single successor
     * block flows to the corresponding store of the successor.
     */
    protected Store.FlowRule flowRule = Store.FlowRule.EACH_TO_EACH;

    protected SingleSuccessorBlockImpl(BlockType type) {
        super(type);
    }

    @Override
    public @Nullable Block getSuccessor() {
        return successor;
    }

    @Override
    public Set<Block> getSuccessors() {
        @Det Set<Block> result = new LinkedHashSet<>();
        if (successor != null) {
            result.add(successor);
        }
        return result;
    }

    /**
     * Set a basic block as the successor of this block.
     *
     * @param successor the block that will be the successor of this
     */
    public void setSuccessor(BlockImpl successor) {
        this.successor = successor;
        successor.addPredecessor(this);
    }

    @Override
    public Store.FlowRule getFlowRule() {
        return flowRule;
    }

    @Override
    public void setFlowRule(Store.FlowRule rule) {
        flowRule = rule;
    }
}
