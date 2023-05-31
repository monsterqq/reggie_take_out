package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
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
     * @Description: 新增菜品，同时保存对应的口味数据Dish和DishFlavor
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
    /**
     * @Description: 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return: com.itheima.reggie.dto.DishDto
     * @Author: Jingq
     * @Date: 2023/5/30 22:07
     */


    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品基本信息，从dish表查询
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        //查询当前菜品对应的口味信息，从dish_flavor表查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());//根据菜品id查询对应的口味
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);//把数据都列出来
        dishDto.setFlavors(flavors);
        return dishDto;
    }


    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表基本信息
        this.updateById(dishDto);
        //清理当前菜品对应的口味数据--dish_flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());//两张表通过dish的id进行关联
        dishFlavorService.remove(queryWrapper);
        //添加当前提交过来的口味数据  dish_flavor表的insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors=flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);

    }

}

