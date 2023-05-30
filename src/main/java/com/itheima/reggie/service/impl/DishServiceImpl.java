package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.jnlp.ServiceManager;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;
    /**
     * @Description: 新增菜品，同时保存对应的口味数据
     * @param dishDto
     * @return: void
     * @Author: Jingq
     * @Date: 2023/5/30 10:35
     */
    @Override
    @Transactional//涉及到多张表，需要这个注解，启动类需要开启事务的注解
    public void saveWithFlavor(DishDto dishDto) {

        //保存菜品的基本信息到菜品表dish
        this.save(dishDto);//继承
        Long dishId = dishDto.getId();
        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);//将flavors加入id
            return item;
        }).collect(Collectors.toList());
        //保存数据到菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);//保存集合用saveBatch

    }
}
