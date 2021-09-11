package com.xiaoju.uemc.tinyid.base.exception;

/**
 * @author du_imba
 *
 * 基础异常
 * （没有其他异常了）
 */
public class TinyIdSysException extends RuntimeException {

    public TinyIdSysException() {
        super();
    }

    public TinyIdSysException(String message) {
        super(message);
    }

    public TinyIdSysException(String message, Throwable cause) {
        super(message, cause);
    }

    public TinyIdSysException(Throwable cause) {
        super(cause);
    }
}
