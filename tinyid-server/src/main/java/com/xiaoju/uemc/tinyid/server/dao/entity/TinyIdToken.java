package com.xiaoju.uemc.tinyid.server.dao.entity;

import java.util.Date;

/**
 * @author du_imba
 */
public class TinyIdToken {
    /**
     * id 自增
     */
    private Integer id;
    /**
     * 令牌
     */
    private String token;

    /**
     * 业务类型
     * 表示此token的权限
     * 一个token可以有多个bizType，对应多条记录
     */
    private String bizType;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


    // ========================  getter setter ToString 分割线 ========================


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token == null ? null : token.trim();
    }

    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType == null ? null : bizType.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
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
}