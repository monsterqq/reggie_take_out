package com.itheima.reggie.common;
/**
 * @Description: 基于ThreadLocal封装工具类，用户保存和获取当前登录用户id
 * @param
 * @return:
 * @Author: Jingq
 * @Date: 2023/5/29 10:12
 */

public class BaseContext {
    private static ThreadLocal<Long> threadLocal=new ThreadLocal<>();
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }
    public static Long getCurrentId(){
       return  threadLocal.get();
    }
}
