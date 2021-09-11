package com.xiaoju.uemc.tinyid.client.utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author du_imba
 * 属性加载器
 */
public class PropertiesLoader {
    private static final Logger logger = Logger.getLogger(PropertiesLoader.class.getName());

    private PropertiesLoader() {

    }

    /**
     * 加载properties文件
     * 通过getResourceAsStream加载classpath路径的文件
     * @param location 本地文件地址
     * @return
     */
    public static Properties loadProperties(String location) {
        Properties props = new Properties();
        logger.info("Loading properties file from path:" + location);
        InputStreamReader in = null;
        try {
            in = new InputStreamReader(PropertiesLoader.class.getClassLoader().getResourceAsStream(location), "UTF-8");
            props.load(in);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            if(in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.log(Level.WARNING, "error close inputstream", e);
                }
            }
        }
        return props;

    }

}
