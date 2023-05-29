package com.itheima.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * @Description: 全局异常处理
 * @param
 * @return:
 * @Author: Jingq
 * @Date: 2023/5/27 22:28
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
    log.error(ex.getMessage());
    //Duplicate entry 'Jingq' for key 'employee.idx_username'
    if(ex.getMessage().contains("Duplicate entry")){
        String[] split = ex.getMessage().split(" ");//将注释部分按照空格进行分割成数组
        String msg = split[2]+"已存在";//将主键id重复的找出来
        return R.error(msg);
    }
    return R.error("未知错误");
}

    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex){
        log.error(ex.getMessage());

        return R.error(ex.getMessage());//界面上输出这个错误
    }
}
