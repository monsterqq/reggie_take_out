package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;

public interface DishService extends IService<Dish> {
    //新增菜品，同时插入对应的口味数据，dish，dish——flavor
    public void saveWithFlavor(DishDto dishDto);
}
