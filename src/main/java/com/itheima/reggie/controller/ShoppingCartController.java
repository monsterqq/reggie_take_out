package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;


    /**
     * @Description: 添加购物车
     * @param shoppingCart
     * @return: com.itheima.reggie.common.R<com.itheima.reggie.entity.ShoppingCart>
     * @Author: Jingq
     * @Date: 2023/6/1 17:12
     */

@PostMapping("/add")
public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
    log.info("购物车数据:{}",shoppingCart);

    //设置用户id，指定当前是哪个用户的购物车数据
    Long currentId = BaseContext.getCurrentId();
    shoppingCart.setUserId(currentId);

    Long dishId = shoppingCart.getDishId();

    LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(ShoppingCart::getUserId,currentId);

    if(dishId != null){
        //添加到购物车的是菜品
        queryWrapper.eq(ShoppingCart::getDishId,dishId);

    }else{
        //添加到购物车的是套餐
        queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
    }

    //查询当前菜品或者套餐是否在购物车中
    //SQL:select * from shopping_cart where user_id = ? and dish_id/setmeal_id = ?
    ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);

    if(cartServiceOne != null){
        //如果已经存在，就在原来数量基础上加一
        Integer number = cartServiceOne.getNumber();
        cartServiceOne.setNumber(number + 1);
        shoppingCartService.updateById(cartServiceOne);
    }else{
        //如果不存在，则添加到购物车，数量默认就是一
        shoppingCart.setNumber(1);
        shoppingCart.setCreateTime(LocalDateTime.now());
        shoppingCartService.save(shoppingCart);
        cartServiceOne = shoppingCart;
    }

    return R.success(cartServiceOne);
}
/**
 * @Description: 查看购物车
 * @param
 * @return: com.itheima.reggie.common.R<java.util.List<com.itheima.reggie.entity.ShoppingCart>>
 * @Author: Jingq
 * @Date: 2023/6/1 17:21
 */
@GetMapping("/list")
public R<List<ShoppingCart>>  list(){
    log.info("查看购物车");
    LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
    queryWrapper.orderByDesc(ShoppingCart::getCreateTime);
    List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
    return R.success(list);
}



/**
 * @Description: 清空购物车
 * @param
 * @return: com.itheima.reggie.common.R<java.lang.String>
 * @Author: Jingq
 * @Date: 2023/6/1 17:31
 */

@DeleteMapping("/clean")
public R<String> clean(){
    LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
    shoppingCartService.remove(queryWrapper);
    return R.success("购物车清空成功!");
}

@PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
    LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
    if(shoppingCart.getDishId()!=null) {
        queryWrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
    }else {
        queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
    }
    ShoppingCart shoppingCartServiceOne = shoppingCartService.getOne(queryWrapper);
    if(shoppingCartServiceOne!=null){
        if(shoppingCartServiceOne.getNumber()>1) {
            shoppingCartServiceOne.setNumber(shoppingCartServiceOne.getNumber() - 1);
            shoppingCartService.updateById(shoppingCartServiceOne);//有多件，减少一件
        } else if(shoppingCartServiceOne.getNumber() == 1) {
                shoppingCartService.removeById(shoppingCartServiceOne.getId());//只有一件，直接移除购物车
            }

    }
return R.success(shoppingCartServiceOne);
}



















}
