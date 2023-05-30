package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * @Description: 菜品管理
 * @param
 * @return:
 * @Author: Jingq
 * @Date: 2023/5/30 9:05
 */

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
/**
 * @Description: 新增菜品,数据库两张表操作案例
 * @param dishDto
 * @return: com.itheima.reggie.common.R<java.lang.String>
 * @Author: Jingq
 * @Date: 2023/5/30 10:21
 */

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString()+"哈哈哈");
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功!");
    }












}
















