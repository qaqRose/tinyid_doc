package com.xiaoju.uemc.tinyid.base.factory;

import com.xiaoju.uemc.tinyid.base.generator.IdGenerator;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author du_imba
 * id生成器抽象工厂
 */
public abstract class AbstractIdGeneratorFactory implements IdGeneratorFactory {

    /**
     * 缓存多个id生成器
     * key: bizType
     */
    private static ConcurrentHashMap<String, IdGenerator> generators = new ConcurrentHashMap<>();

    @Override
    public IdGenerator getIdGenerator(String bizType) {
        // 是否存在 bizType
        if (generators.containsKey(bizType)) {
            return generators.get(bizType);
        }
        // 第一次获取，加锁
        synchronized (this) {
            // 双重检锁
            if (generators.containsKey(bizType)) {
                return generators.get(bizType);
            }
            IdGenerator idGenerator = createIdGenerator(bizType);
            generators.put(bizType, idGenerator);
            return idGenerator;
        }
    }

    /**
     * 根据bizType创建id生成器
     *【设计模式】模板方法
     * @param bizType
     * @return
     */
    protected abstract IdGenerator createIdGenerator(String bizType);
}
