package com.xiaoju.uemc.tinyid.server.controller;

import com.xiaoju.uemc.tinyid.base.entity.SegmentId;
import com.xiaoju.uemc.tinyid.base.generator.IdGenerator;
import com.xiaoju.uemc.tinyid.base.service.SegmentIdService;
import com.xiaoju.uemc.tinyid.server.factory.impl.IdGeneratorFactoryServer;
import com.xiaoju.uemc.tinyid.server.service.TinyIdTokenService;
import com.xiaoju.uemc.tinyid.server.vo.ErrorCode;
import com.xiaoju.uemc.tinyid.server.vo.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author du_imba
 */
@Api(tags = "id接口")
@RestController
@RequestMapping("/id/")
public class IdController {

    private static final Logger logger = LoggerFactory.getLogger(IdController.class);
    @Autowired
    private IdGeneratorFactoryServer idGeneratorFactoryServer;
    @Autowired
    private SegmentIdService segmentIdService;
    @Autowired
    private TinyIdTokenService tinyIdTokenService;
    @Value("${batch.size.max}")
    private Integer batchSizeMax;

    /**
     * 获取下一个id
     * @param bizType 业务类型
     * @param batchSize 批量数量
     * @param token 令牌
     * @return
     */
    @ApiOperation("获取下一个id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bizType", value = "业务类型", defaultValue = "test", required = true),
            @ApiImplicitParam(name = "batchSize", value = "id数量", defaultValue = "10000"),
            @ApiImplicitParam(name = "token", value = "令牌", defaultValue = "0f673adf80504e2eaa552f5d791b644c", required = true)
    })
    @RequestMapping(value = "nextId")
    public Response<List<Long>> nextId(String bizType, Integer batchSize, String token) {
        Response<List<Long>> response = new Response<>();
        Integer newBatchSize = checkBatchSize(batchSize);
        // 校验token
        if (!tinyIdTokenService.canVisit(bizType, token)) {
            response.setCode(ErrorCode.TOKEN_ERR.getCode());
            response.setMessage(ErrorCode.TOKEN_ERR.getMessage());
            return response;
        }
        try {
            // 获取id生成器
            IdGenerator idGenerator = idGeneratorFactoryServer.getIdGenerator(bizType);
            // 批量获取id
            List<Long> ids = idGenerator.nextId(newBatchSize);
            response.setData(ids);
        } catch (Exception e) {
            response.setCode(ErrorCode.SYS_ERR.getCode());
            response.setMessage(e.getMessage());
            logger.error("nextId error", e);
        }
        return response;
    }

    /**
     * 检查批次数量
     * 控制范围在 [1, batchSizeMax]
     * @param batchSize
     * @return
     */
    private Integer checkBatchSize(Integer batchSize) {
        if (batchSize == null) {
            batchSize = 1;
        }
        if (batchSize > batchSizeMax) {
            batchSize = batchSizeMax;
        }
        return batchSize;
    }

    /**
     * 与nextId类似，这是返回结果不用Response包装
     * 方便业务直接使用， 不用json解析
     * @param bizType 业务类型
     * @param batchSize 批量数量
     * @param token 令牌
     * @return
     */
    @ApiOperation("获取下一个id(直接)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bizType", value = "业务类型", defaultValue = "test", required = true),
            @ApiImplicitParam(name = "batchSize", value = "id数量", defaultValue = "10000"),
            @ApiImplicitParam(name = "token", value = "令牌", defaultValue = "0f673adf80504e2eaa552f5d791b644c", required = true)
    })
    @RequestMapping("nextIdSimple")
    public String nextIdSimple(String bizType, Integer batchSize, String token) {
        Integer newBatchSize = checkBatchSize(batchSize);
        // 验证token
        if (!tinyIdTokenService.canVisit(bizType, token)) {
            return "";
        }
        String response = "";
        try {
            IdGenerator idGenerator = idGeneratorFactoryServer.getIdGenerator(bizType);
            if (newBatchSize == 1) {
                Long id = idGenerator.nextId();
                response = id + "";
            } else {
                List<Long> idList = idGenerator.nextId(newBatchSize);
                // 批量id用逗号分隔
                StringBuilder sb = new StringBuilder();
                for (Long id : idList) {
                    sb.append(id).append(",");
                }
                response = sb.deleteCharAt(sb.length() - 1).toString();
            }
        } catch (Exception e) {
            logger.error("nextIdSimple error", e);
        }
        return response;
    }

    /**
     * 获取号段id
     * @param bizType
     * @param token
     * @return
     */
    @ApiOperation("获取号段")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bizType", value = "业务类型", defaultValue = "test", required = true),
            @ApiImplicitParam(name = "token", value = "令牌", defaultValue = "0f673adf80504e2eaa552f5d791b644c", required = true)
    })
    @RequestMapping("nextSegmentId")
    public Response<SegmentId> nextSegmentId(String bizType, String token) {
        Response<SegmentId> response = new Response<>();
        // 验证token
        if (!tinyIdTokenService.canVisit(bizType, token)) {
            response.setCode(ErrorCode.TOKEN_ERR.getCode());
            response.setMessage(ErrorCode.TOKEN_ERR.getMessage());
            return response;
        }
        try {
            // 获取数据库的下一个号段信息
            SegmentId segmentId = segmentIdService.getNextSegmentId(bizType);
            response.setData(segmentId);
        } catch (Exception e) {
            response.setCode(ErrorCode.SYS_ERR.getCode());
            response.setMessage(e.getMessage());
            logger.error("nextSegmentId error", e);
        }
        return response;
    }


    @ApiOperation("获取号段(直接，无封装)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bizType", value = "业务类型", defaultValue = "test", required = true),
            @ApiImplicitParam(name = "token", value = "令牌", defaultValue = "0f673adf80504e2eaa552f5d791b644c", required = true)
    })
    @RequestMapping("nextSegmentIdSimple")
    public String nextSegmentIdSimple(String bizType, String token) {
        if (!tinyIdTokenService.canVisit(bizType, token)) {
            return "";
        }
        String response = "";
        try {
            SegmentId segmentId = segmentIdService.getNextSegmentId(bizType);
            response = segmentId.getCurrentId() + "," + segmentId.getLoadingId() + "," + segmentId.getMaxId()
                    + "," + segmentId.getDelta() + "," + segmentId.getRemainder();
        } catch (Exception e) {
            logger.error("nextSegmentIdSimple error", e);
        }
        return response;
    }

}
