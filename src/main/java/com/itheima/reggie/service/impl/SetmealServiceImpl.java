package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;


   /**
    * @Description: 新增套餐，同时需要保存套餐和菜品地关联关系
    * @param setmealDto
    * @return: void
    * @Author: Jingq
    * @Date: 2023/5/31 11:39
    */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {  //setmealDto=Setmeal+SetmealDish
        this.save(setmealDto);//保存除了菜品以外套餐的基本信息
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();//拿到菜品集合
        setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());//数据库中setmeal_dish的setmeal_id与setmeal的id是同一个id
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
    }











}
