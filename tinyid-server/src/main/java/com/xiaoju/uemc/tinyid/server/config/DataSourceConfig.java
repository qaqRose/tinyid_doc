package com.xiaoju.uemc.tinyid.server.config;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author du_imba
 *
 * 数据源配置
 */
@Configuration
public class DataSourceConfig {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);

    @Autowired
    private Environment environment;
    private static final String SEP = ",";

    private static final String DEFAULT_DATASOURCE_TYPE = "org.apache.tomcat.jdbc.pool.DataSource";

    /**
     * 数据源配置
     * 动态数据源，如果有多个的话
     * @return
     */
    @Bean
    public DataSource getDynamicDataSource() {
        DynamicDataSource routingDataSource = new DynamicDataSource();
        List<String> dataSourceKeys = new ArrayList<>();

        // 获取spring环境配置
        RelaxedPropertyResolver propertyResolver = new RelaxedPropertyResolver(environment, "datasource.tinyid.");
        // names 为数据源名称， 多个用逗号隔开
        String names = propertyResolver.getProperty("names");
        // 数据源类型
        String dataSourceType = propertyResolver.getProperty("type");

        Map<Object, Object> targetDataSources = new HashMap<>(4);
        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.setDataSourceKeys(dataSourceKeys);
        // 多个数据源
        for (String name : names.split(SEP)) {
            // 获取数据库连接配置
            Map<String, Object> dsMap = propertyResolver.getSubProperties(name + ".");
            // 构造数据源
            DataSource dataSource = buildDataSource(dataSourceType, dsMap);
            // 完善数据源配置
            buildDataSourceProperties(dataSource, dsMap);
            // 添加到数据源map中
            targetDataSources.put(name, dataSource);
            // key
            dataSourceKeys.add(name);
        }
        return routingDataSource;
    }

    /**
     * 完善数据源配置
     * 设置更多的配置，当然前提是application.properties有配置
     * @param dataSource
     * @param dsMap
     */
    private void buildDataSourceProperties(DataSource dataSource, Map<String, Object> dsMap) {
        try {
            // 此方法性能差，慎用
            BeanUtils.copyProperties(dataSource, dsMap);
        } catch (Exception e) {
            logger.error("error copy properties", e);
        }
    }

    /**
     * 构造数据源
     * 只设置基本的参数：驱动、地址、账号密码
     * @param dataSourceType
     * @param dsMap
     * @return
     */
    private DataSource buildDataSource(String dataSourceType, Map<String, Object> dsMap) {
        try {
            String className = DEFAULT_DATASOURCE_TYPE;
            if (dataSourceType != null && !"".equals(dataSourceType.trim())) {
                className = dataSourceType;
            }
            // 获取application.properties的配置信息
            Class<? extends DataSource> type = (Class<? extends DataSource>) Class.forName(className);
            String driverClassName = dsMap.get("driver-class-name").toString();
            String url = dsMap.get("url").toString();
            String username = dsMap.get("username").toString();
            String password = dsMap.get("password").toString();

            return DataSourceBuilder.create()
                    .driverClassName(driverClassName)
                    .url(url)
                    .username(username)
                    .password(password)
                    .type(type)
                    .build();

        } catch (ClassNotFoundException e) {
            logger.error("buildDataSource error", e);
            throw new IllegalStateException(e);
        }
    }


}
