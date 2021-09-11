package com.xiaoju.uemc.tinyid.client.config;

import java.util.List;

/**
 * @author du_imba
 *
 * 客户端配置
 */
public class TinyIdClientConfig {

    /**
     * 令牌
     */
    private String tinyIdToken;

    /**
     * 服务器地址
     * 多个用逗号隔开
     */
    private String tinyIdServer;

    /**
     * 服务器地址列表
     */
    private List<String> serverList;

    /**
     * 读超时
     * 单位：毫秒
     */
    private Integer readTimeout;

    /**
     * 连接超时
     * 单位：毫秒
     */
    private Integer connectTimeout;

    private volatile static TinyIdClientConfig tinyIdClientConfig;

    private TinyIdClientConfig() {
    }

    /**
     * 单例 双重检锁
     * @return
     */
    public static TinyIdClientConfig getInstance() {
        if (tinyIdClientConfig != null) {
            return tinyIdClientConfig;
        }
        synchronized (TinyIdClientConfig.class) {
            if (tinyIdClientConfig != null) {
                return tinyIdClientConfig;
            }
            tinyIdClientConfig = new TinyIdClientConfig();
        }
        return tinyIdClientConfig;
    }

    public String getTinyIdToken() {
        return tinyIdToken;
    }

    public void setTinyIdToken(String tinyIdToken) {
        this.tinyIdToken = tinyIdToken;
    }

    public String getTinyIdServer() {
        return tinyIdServer;
    }

    public void setTinyIdServer(String tinyIdServer) {
        this.tinyIdServer = tinyIdServer;
    }

    public List<String> getServerList() {
        return serverList;
    }

    public void setServerList(List<String> serverList) {
        this.serverList = serverList;
    }

    public Integer getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Integer readTimeout) {
        this.readTimeout = readTimeout;
    }

    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }
}
