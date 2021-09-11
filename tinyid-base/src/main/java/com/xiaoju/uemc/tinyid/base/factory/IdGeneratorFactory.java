package com.xiaoju.uemc.tinyid.base.factory;

import com.xiaoju.uemc.tinyid.base.generator.IdGenerator;

/**
 * @author du_imba
 *
 * id生成器工程
 * 有客户端和服务端两种实现
 */
public interface IdGeneratorFactory {
    /**
     * 根据bizType创建id生成器
     * @param bizType
     * @return
     */
    IdGenerator getIdGenerator(String bizType);
}
