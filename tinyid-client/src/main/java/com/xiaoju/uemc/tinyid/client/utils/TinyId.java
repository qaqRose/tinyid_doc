package com.xiaoju.uemc.tinyid.client.utils;

import com.xiaoju.uemc.tinyid.client.factory.impl.IdGeneratorFactoryClient;
import com.xiaoju.uemc.tinyid.base.generator.IdGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author du_imba
 *
 * 微id工具
 */
public class TinyId {
    /**
     * id生成器工厂
     */
    private static IdGeneratorFactoryClient client = IdGeneratorFactoryClient.getInstance(null);

    private TinyId() {

    }

    /**
     * 根据业务类型获取下一个id
     * client主要使用方式
     * @param bizType
     * @return
     */
    public static Long nextId(String bizType) {
        if(bizType == null) {
            throw new IllegalArgumentException("type is null");
        }
        IdGenerator idGenerator = client.getIdGenerator(bizType);
        return idGenerator.nextId();
    }

    /**
     * 批量获取下一个id
     * @param bizType
     * @param batchSize
     * @return
     */
    public static List<Long> nextId(String bizType, Integer batchSize) {
        if(batchSize == null) {
            Long id = nextId(bizType);
            List<Long> list = new ArrayList<>();
            list.add(id);
            return list;
         }
        IdGenerator idGenerator = client.getIdGenerator(bizType);
        return idGenerator.nextId(batchSize);
    }

}
