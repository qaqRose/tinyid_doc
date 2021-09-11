package com.xiaoju.uemc.tinyid.base.entity;

/**
 * @author du_imba
 *
 * id结果
 */
public class Result {
    /**
     * 传递号段状态
     * @see ResultCode
     */
    private int code;
    private long id;

    public Result(int code, long id) {
        this.code = code;
        this.id = id;
    }

    // ========================  getter setter ToString 分割线 ========================

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "[id:" + id + ",code:" + code + "]";
    }
}
