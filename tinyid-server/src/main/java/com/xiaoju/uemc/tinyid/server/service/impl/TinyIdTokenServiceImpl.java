package com.xiaoju.uemc.tinyid.server.service.impl;

import com.xiaoju.uemc.tinyid.server.dao.TinyIdTokenDAO;
import com.xiaoju.uemc.tinyid.server.dao.entity.TinyIdToken;
import com.xiaoju.uemc.tinyid.server.service.TinyIdTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author du_imba
 *
 * token服务
 */
@Component
public class TinyIdTokenServiceImpl implements TinyIdTokenService {

    @Autowired
    private TinyIdTokenDAO tinyIdTokenDAO;

    /**
     * token biz类型 map
     * 缓存
     * 一个token可以有多个 bizType
     */
    private static Map<String, Set<String>> token2bizTypes = new HashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(TinyIdTokenServiceImpl.class);

    public List<TinyIdToken> queryAll() {
        return tinyIdTokenDAO.selectAll();
    }

    /**
     * 1分钟刷新一次token
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void refresh() {
        logger.info("refresh token begin");
        init();
    }

    /**
     * 更新token2bizTypes，与数据库同步
     * 初始化  同步
     * 启动时执行（预热）
     */
    @PostConstruct
    private synchronized void init() {
        logger.info("tinyId token init begin");
        List<TinyIdToken> list = queryAll();

        Map<String, Set<String>> map = converToMap(list);
        token2bizTypes = map;
        logger.info("tinyId token init success, token size:{}", list == null ? 0 : list.size());
    }

    /**
     * 是否有权限
     * @param bizType 业务类型
     * @param token 令牌
     * @return
     */
    @Override
    public boolean canVisit(String bizType, String token) {
        if (StringUtils.isEmpty(bizType) || StringUtils.isEmpty(token)) {
            return false;
        }
        // map是否存在token和bizType
        Set<String> bizTypes = token2bizTypes.get(token);
        return (bizTypes != null && bizTypes.contains(bizType));
    }

    /**
     * list转换成map
     * list                                          map
     * 1  TinyIdToken{ token:1 bizType: a}              key: 1  value: set(a, b, c)
     * 2  TinyIdToken{ token:1 bizType: b}    -->
     * 3  TinyIdToken{ token:1 bizType: c}
     * @param list
     * @return
     */
    public Map<String, Set<String>> converToMap(List<TinyIdToken> list) {
        Map<String, Set<String>> map = new HashMap<>(64);
        if (list != null) {
            for (TinyIdToken tinyIdToken : list) {
                if (!map.containsKey(tinyIdToken.getToken())) {
                    map.put(tinyIdToken.getToken(), new HashSet<String>());
                }
                map.get(tinyIdToken.getToken()).add(tinyIdToken.getBizType());
            }
        }
        return map;
    }

}
