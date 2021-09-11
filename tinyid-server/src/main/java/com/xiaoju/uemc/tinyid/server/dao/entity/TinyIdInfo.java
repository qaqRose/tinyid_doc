package com.xiaoju.uemc.tinyid.server.dao.entity;

import java.util.Date;

/**
 * @author du_imba
 *
 * 数据表 tiny_id_info实体
 */
public class TinyIdInfo {
    /**
     * 主键
     */
    private Long id;

    /**
     * 业务类型，唯一
     */
    private String bizType;

    /**
     * 开始id
     * 记录作用
     */
    private Long beginId;

    /**
     * 当前最大id
     */
    private Long maxId;

    /**
     * 步长
     */
    private Integer step;

    /**
     * 每次id增量
     * 可能id按 1 3 5 获取, 这样delta=2
     */
    private Integer delta;

    /**
     * 余数
     */
    private Integer remainder;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 版本号
     * 乐观锁
     */
    private Long version;


    // ========================  getter setter ToString 分割线 ========================


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }

    public Long getBeginId() {
        return beginId;
    }

    public void setBeginId(Long beginId) {
        this.beginId = beginId;
    }

    public Long getMaxId() {
        return maxId;
    }

    public void setMaxId(Long maxId) {
        this.maxId = maxId;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }

    public Integer getDelta() {
        return delta;
    }

    public void setDelta(Integer delta) {
        this.delta = delta;
    }

    public Integer getRemainder() {
        return remainder;
    }

    public void setRemainder(Integer remainder) {
        this.remainder = remainder;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}