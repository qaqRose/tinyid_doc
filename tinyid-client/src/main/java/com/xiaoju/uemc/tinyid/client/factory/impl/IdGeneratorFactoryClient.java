package com.xiaoju.uemc.tinyid.client.factory.impl;

import com.xiaoju.uemc.tinyid.base.factory.AbstractIdGeneratorFactory;
import com.xiaoju.uemc.tinyid.base.generator.IdGenerator;
import com.xiaoju.uemc.tinyid.base.generator.impl.CachedIdGenerator;
import com.xiaoju.uemc.tinyid.client.config.TinyIdClientConfig;
import com.xiaoju.uemc.tinyid.client.service.impl.HttpSegmentIdServiceImpl;
import com.xiaoju.uemc.tinyid.client.utils.PropertiesLoader;
import com.xiaoju.uemc.tinyid.client.utils.TinyIdNumberUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author du_imba
 * 客户端id生成器工厂
 * 单例
 */
public class IdGeneratorFactoryClient extends AbstractIdGeneratorFactory {

    private static final Logger logger = Logger.getLogger(IdGeneratorFactoryClient.class.getName());

    private static IdGeneratorFactoryClient idGeneratorFactoryClient;

    /**
     * 默认配置文件
     * 当前classpath加载
     */
    private static final String DEFAULT_PROP = "tinyid_client.properties";

    private static final int DEFAULT_TIME_OUT = 5000;

    /**
     * 服务端地址
     */
    private static String serverUrl = "http://{0}/tinyid/id/nextSegmentIdSimple?token={1}&bizType=";

    private IdGeneratorFactoryClient() {

    }

    /**
     * 单例 双重检锁
     * @param location 配置文件地址
     * @return
     */
    public static IdGeneratorFactoryClient getInstance(String location) {
        if (idGeneratorFactoryClient == null) {
            synchronized (IdGeneratorFactoryClient.class) {
                if (idGeneratorFactoryClient == null) {
                    if (location == null || "".equals(location)) {
                        init(DEFAULT_PROP);
                    } else {
                        init(location);
                    }
                }
            }
        }
        return idGeneratorFactoryClient;
    }

    /**
     * 初始化
     * 1.实例化id生成器工厂
     * 2.获取classpath文件配置，实例化TinyIdClientConfig
     * @param location 配置文件地址
     */
    private static void init(String location) {
        idGeneratorFactoryClient = new IdGeneratorFactoryClient();
        // 加载文件
        Properties properties = PropertiesLoader.loadProperties(location);

        String tinyIdToken = properties.getProperty("tinyid.token");
        String tinyIdServer = properties.getProperty("tinyid.server");
        String readTimeout = properties.getProperty("tinyid.readTimeout");
        String connectTimeout = properties.getProperty("tinyid.connectTimeout");

        if (tinyIdToken == null || "".equals(tinyIdToken.trim())
                || tinyIdServer == null || "".equals(tinyIdServer.trim())) {
            throw new IllegalArgumentException("cannot find tinyid.token and tinyid.server config in:" + location);
        }

        // 获取客户配置类(单例)， 并设置值
        TinyIdClientConfig tinyIdClientConfig = TinyIdClientConfig.getInstance();
        tinyIdClientConfig.setTinyIdServer(tinyIdServer);
        tinyIdClientConfig.setTinyIdToken(tinyIdToken);
        tinyIdClientConfig.setReadTimeout(TinyIdNumberUtils.toInt(readTimeout, DEFAULT_TIME_OUT));
        tinyIdClientConfig.setConnectTimeout(TinyIdNumberUtils.toInt(connectTimeout, DEFAULT_TIME_OUT));


        String[] tinyIdServers = tinyIdServer.split(",");
        List<String> serverList = new ArrayList<>(tinyIdServers.length);
        for (String server : tinyIdServers) {
            // 解析请求地址，添加到服务列表
            String url = MessageFormat.format(serverUrl, server, tinyIdToken);
            serverList.add(url);
        }
        logger.info("init tinyId client success url info:" + serverList);
        tinyIdClientConfig.setServerList(serverList);
    }

    /**
     * 创建id生成器
     * @param bizType 业务类型
     * @return
     */
    @Override
    protected IdGenerator createIdGenerator(String bizType) {
        // 这里使用http方法获取号段
        return new CachedIdGenerator(bizType, new HttpSegmentIdServiceImpl());
    }

}
