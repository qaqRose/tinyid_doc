package com.xiaoju.uemc.tinyid.base.generator;

import java.util.List;

/**
 * @author du_imba
 * ID生成器接口
 */
public interface IdGenerator {
    /**
     * get next id
     * 获取下一个id
     * @return
     */
    Long nextId();

    /**
     * get next id batch
     * 批量获取下一个id
     * @param batchSize
     * @return
     */
    List<Long> nextId(Integer batchSize);
}
