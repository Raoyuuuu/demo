package com.example.demo.common.uuid;

import java.util.Random;
import java.util.UUID;

/**
 * @auther: raohr
 * @Title:
 * @Description:
 * @Date: 2019/11/25 10:52
 * @param:
 * @return:
 * @throws:
 */
public class UUIDUtil {
    private static SnowFlakeIdWorker snowFlakeIdWorker = new SnowFlakeIdWorker();

    private static String NUMBER_SOURCES = "0123456789";

    private static String LETTER_SOURCES = "QWERTYUIOPASDFGHJKLZXCVBNM";

    public static String get32UUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * @auther: qiuqinghua
     * @Title: getSpecifyId
     * @Description: 指定id生成
     * @Date: 2018/11/29 9:43
     * @param: [fixed:固定值, place:指定值位置（l.左，r.右,m.中间）, len：随机数字长度]
     * @return: java.lang.String
     * @throws:
     */
    public static String getSpecifyId(String fixed, String place, int len) throws Exception {
        switch (place) {
            case "l":
                return fixed + getBlend(len);
            case "r":
                return getBlend(len) + fixed;
            case "m":
                return getBlend(len / 2) + fixed + getBlend(len - len / 2);
            default:
                return "";
        }
    }


    /**
     * @auther: qiuqinghua
     * @Title: getSixNum
     * @Description: 随机n位数字
     * @Date: 2018/11/27 13:56
     * @param: []
     * @return: java.lang.String
     * @throws:
     */
    public static String getNum(int num) {
        return getRandomStr(num, NUMBER_SOURCES);
    }

    /**
     * @auther: qiuqinghua
     * @Title: getStr
     * @Description: 随机n位字母
     * @Date: 2018/11/29 13:10
     * @param: [num]
     * @return: java.lang.String
     * @throws:
     */
    public static String getStr(int num) {
        return getRandomStr(num, LETTER_SOURCES);
    }

    /**
     * @auther: qiuqinghua
     * @Title: getBlend
     * @Description: 随机n位数字+字母
     * @Date: 2018/11/29 13:10
     * @param: [num]
     * @return: java.lang.String
     * @throws:
     */
    public static String getBlend(int num) {
        return getRandomStr(num, NUMBER_SOURCES + LETTER_SOURCES);
    }

    /**
     * @auther: qiuqinghua
     * @Title: 获取数字自增id
     * @Description:
     * @Date: 2019/9/2 8:30
     * @param:
     * @return:
     * @throws:
     */
    public static Long getSnowFlakeId() {
        return snowFlakeIdWorker.nextId();
    }


    public static String getRandomStr(int num, String sources) {
        Random rand = new Random();
        StringBuffer flag = new StringBuffer();
        for (int j = 0; j < num; j++)
            flag.append(sources.charAt(rand.nextInt(sources.length())) + "");
        return flag.toString();

    }
}
