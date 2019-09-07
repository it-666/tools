package com.wzb.tools.sequence;

import com.wzb.tools.sequence.impl.SnowflakeSequenceImpl;

/**
 * the factory for sequence
 */
public class SequenceFactory {
    /**
     * the maximum work id of sequence algorithm supported (snow flake)
     *
     * @return
     */
    public int getMaxWordId() {
        return SnowflakeSequenceImpl.getMaxWorkerId();
    }

    /**
     * create sequence based on work id Support different sequence generation algorithms
     * (for now only snow flake)
     *
     * @param workerId work id
     * @return the sequence object
     */
    public Sequence createSequences(int workerId) {
        return SnowflakeSequenceImpl.build(workerId);
    }
}
