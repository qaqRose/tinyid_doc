package com.xiaoju.uemc.tinyid.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.InetAddress;

/**
 * @author du_imba
 */
@EnableAsync
@SpringBootApplication
@EnableScheduling
public class TinyIdServerApplication {

    private static final Logger logger = LoggerFactory.getLogger(TinyIdServerApplication.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(TinyIdServerApplication.class, args);
        accessUrl(context);
    }

    /**
     * web地址
     * @param context
     */
    public static void accessUrl(ConfigurableApplicationContext context) {
        try {
            Environment env = context.getEnvironment();
            String ip = InetAddress.getLocalHost().getHostAddress();
            String port = env.getProperty("server.port");
            String path = env.getProperty("server.context-path");
            logger.info("\n----------------------------------------------------------\n\t" +
                    "Application is running! Access URLs:\n\t" +
                    "Local: \t\thttp://localhost:" + port + path + "/\n\t" +
                    "External: \thttp://" + ip + ":" + port + path + "/\n\t" +
                    "knife4j文档: \thttp://" + ip + ":" + port + path + "/doc.html\n" +
                    "----------------------------------------------------------");
        } catch (Throwable e) {
            logger.warn("打印启动地址错误", e);
        }


    }
}
