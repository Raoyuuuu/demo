package com.example.demo.common.exception;

/**
 * @auther: raohr
 * @Title:
 * @Description:
 * @Date: 2019/11/25 10:49
 * @param:
 * @return:
 * @throws:
 */
public class AppException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public AppException() {
    }

    public AppException(Throwable cause) {
        super(cause);
    }

    public AppException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppException(String message) {
        super(message);
    }
}
