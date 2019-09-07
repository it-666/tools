package com.wzb.tools.id;

import com.wzb.tools.sequence.SequenceManager;
import lombok.Getter;
import lombok.Setter;

/**
 * the id generator base class
 */
@Setter
public class IdGenerator {
    /**
     * the sequence name
     */
    @Getter
    private String sequenceName = "default";
    /**
     * the sequence manager
     */
    private SequenceManager sequenceManager;

    /**
     * 构造函数
     *
     * @param sequenceName
     */
    public IdGenerator(String sequenceName) {
        this.sequenceName = sequenceName;
    }

    /**
     * get next id
     *
     * @return
     */
    public long getNextId() {
        return sequenceManager.nextId(sequenceName);
    }

    /**
     * get next id
     *
     * @return
     */
    public String getNextStringId() {
        return sequenceManager.nextStringId(sequenceName);
    }
}
