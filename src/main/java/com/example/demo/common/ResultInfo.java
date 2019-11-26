package com.example.demo.common;

import java.util.HashMap;

/**
 * @auther: raohr
 * @Title:
 * @Description:
 * @Date: 2019/11/25 9:24
 * @param:
 * @return:
 * @throws:
 */
public class ResultInfo extends HashMap<String,Object> {

    public static final int SUCCESS = 200;//操作成功
    static final String SUCCESS_MSG = "操作成功";

    public ResultInfo(){
        put("resultCode", ResultInfo.SUCCESS);
        put("resultMsg", ResultInfo.SUCCESS_MSG);
    }

    /**
     * 成功
     * @return
     */
    public static ResultInfo success(){
        ResultInfo resultInfo = new ResultInfo();
        return resultInfo;
    }

    /**
     * 成功
     * @param data
     * @return
     */
    public static ResultInfo success(Object data){
        ResultInfo resultInfo = new ResultInfo();
        if (data != null && !data.equals("null"))
            resultInfo.put("data", data);
        return resultInfo;
    }
}
