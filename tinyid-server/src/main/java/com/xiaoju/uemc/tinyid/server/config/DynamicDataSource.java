package com.xiaoju.uemc.tinyid.server.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.List;
import java.util.Random;

/**
 * @author du_imba
 *
 * 动态数据源
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    private static final Logger logger = LoggerFactory.getLogger(DynamicDataSource.class);

    private List<String> dataSourceKeys;

    /**
     * 获取动态数据源的key
     * @return
     */
    @Override
    protected Object determineCurrentLookupKey() {

        if(dataSourceKeys.size() == 1) {
            return dataSourceKeys.get(0);
        }
        Random r = new Random();
        String key = dataSourceKeys.get(r.nextInt(dataSourceKeys.size()));
        logger.info("dataSource Key:{}", key);
        return key;
    }

    public List<String> getDataSourceKeys() {
        return dataSourceKeys;
    }

    public void setDataSourceKeys(List<String> dataSourceKeys) {
        this.dataSourceKeys = dataSourceKeys;
    }
}
