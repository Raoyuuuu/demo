package com.example.demo.util;

import com.example.demo.common.exception.AppException;
import org.apache.commons.lang3.StringUtils;

/**
 * @auther: raohr
 * @Title: 用于做必填数据的校验
 * @Description:
 * @Date: 2019/11/25 10:48
 * @param:
 * @return:
 * @throws:
 */
public class AssertUtil {

    /**
     * 若为真，则抛出业务异常
     * @param expression
     * @param message
     * @throws AppException
     */
    public static void check(final boolean expression, final String message) throws AppException {
        if (expression) {
            throw new AppException(message);
        }
    }

    /**
     * 若为真，则抛出业务异常
     * @param expression
     * @param message
     * @param args
     * @throws AppException
     */
    public static void check(final boolean expression, final String message, final Object... args) throws AppException {
        if (expression) {
            throw new AppException(String.format(message, args));
        }
    }

    /**
     * 若为真，则抛出业务异常
     * @param expression
     * @param message
     * @param arg
     * @throws AppException
     */
    public static void check(final boolean expression, final String message, final Object arg) throws AppException {
        if (expression) {
            throw new AppException(String.format(message, arg));
        }
    }

    /**
     * 若为null，则抛出业务异常
     * @param object
     * @param message
     * @throws AppException
     */
    public static void isNull(final Object object, final String message) throws AppException {
        if (object == null) {
            throw new AppException(message);
        }
    }


    /**
     *  若为null，则抛出业务异常
     *      由于与其他方法语义不一致，废弃，请改用isNull
     *
     * @param object
     * @param message
     * @throws AppException
     */
    @Deprecated
    public static void notNull(final Object object, final String message) throws AppException {
        isNull(object,message);
    }

    /**
     * 若为null 或 空串，则抛出业务异常
     * @param s
     * @param message
     * @throws AppException
     */
    public static void isEmpty(final CharSequence s, final String message) throws AppException {
        if (StringUtils.isEmpty(s)) {
            throw new AppException(message);
        }
    }

    /**
     *  若为null 或 空串，则抛出业务异常
     *      由于与其他方法语义不一致，废弃，请改用isEmpty
     * @param s
     * @param message
     * @throws AppException
     */
    @Deprecated
    public static void notEmpty(final CharSequence s, final String message) throws AppException {
        isEmpty(s,message);
    }

    /**
     * 若为null 、空串、空白串，则抛出业务异常
     * @param s
     * @param message
     * @throws AppException
     */
    public static void isBlank(final CharSequence s, final String message) throws AppException {
        if (StringUtils.isBlank(s)) {
            throw new AppException(message);
        }
    }

    /**
     * 若为null 、空串、空白串，则抛出业务异常
     *      由于与其他方法语义不一致，废弃，请改用isBlank
     *
     * @param s
     * @param message
     * @throws AppException
     */
    @Deprecated
    public static void notBlank(final CharSequence s, final String message) throws AppException {
        isBlank(s,message);
    }

    /**
     * 检查字符创是否为指定长度
     * @param s
     * @param length 指定长度，最小值为1
     * @param message
     * @throws AppException
     */
    public static void notLength(final CharSequence s, int length, final String message) throws AppException {
        if(length < 1){
            throw new IllegalArgumentException("length cannot less than 1");
        }
        if(s == null || s.length() != length){
            throw new AppException(message);
        }
    }
}
