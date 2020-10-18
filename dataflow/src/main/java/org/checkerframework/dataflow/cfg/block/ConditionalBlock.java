package org.checkerframework.dataflow.cfg.block;

import org.checkerframework.checker.determinism.qual.*;
import org.checkerframework.dataflow.analysis.Store;
import org.checkerframework.dataflow.analysis.Store.FlowRule;

// Werner believes that a ConditionalBlock has to have exactly one RegularBlock (?) predecessor and
// the last node of that predecessor has to be a node of boolean type. He's not totally sure,
// though.  We should check whether that property holds.

/** Represents a conditional basic block. */
public interface ConditionalBlock extends Block {

    /**
     * Returns the entry block of the then branch.
     *
     * @return the entry block of the then branch
     */
    @Det Block getThenSuccessor(@Det ConditionalBlock this);

    /**
     * Returns the entry block of the else branch.
     *
     * @return the entry block of the else branch
     */
    @Det Block getElseSuccessor(@Det ConditionalBlock this);

    /**
     * Returns the flow rule for information flowing from this block to its then successor.
     *
     * @return the flow rule for information flowing from this block to its then successor
     */
    @Det FlowRule getThenFlowRule(@Det ConditionalBlock this);

    /**
     * Returns the flow rule for information flowing from this block to its else successor.
     *
     * @return the flow rule for information flowing from this block to its else successor
     */
    @Det FlowRule getElseFlowRule(@Det ConditionalBlock this);

    /** Set the flow rule for information flowing from this block to its then successor. */
    void setThenFlowRule(@Det ConditionalBlock this, Store.FlowRule rule);

    /** Set the flow rule for information flowing from this block to its else successor. */
    void setElseFlowRule(@Det ConditionalBlock this, Store.FlowRule rule);
}
