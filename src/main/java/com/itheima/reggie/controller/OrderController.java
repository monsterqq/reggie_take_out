package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.OrdersDto;
import com.itheima.reggie.entity.OrderDetail;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.service.OrderDetailService;
import com.itheima.reggie.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    /** 结算
     * @Description:
     * @param orders
     * @return: com.itheima.reggie.common.R<java.lang.String>
     * @Author: Jingq
     * @Date: 2023/6/1 21:55
     */

    @PostMapping("/submit")
    public R<String>  submit(@RequestBody Orders orders){
        orderService.submit(orders);
        return R.success("下单成功");
    }

    /**
     * @Description: 订单明细
     * @param page
     * @param pageSize
     * @return: com.itheima.reggie.common.R<com.baomidou.mybatisplus.extension.plugins.pagination.Page>
     * @Author: Jingq
     * @Date: 2023/6/1 21:54
     */

@GetMapping("/userPage")
    public R<Page> page(int page,int pageSize){
    Page<Orders> pageInfo = new Page<>();
    Page<OrdersDto> pageDto = new Page<>();
    LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(Orders::getUserId,BaseContext.getCurrentId());
    queryWrapper.orderByDesc(Orders::getOrderTime);
    orderService.page(pageInfo,queryWrapper);

    LambdaQueryWrapper<OrderDetail> queryWrapper2 = new LambdaQueryWrapper<>();
    List<Orders> records = pageInfo.getRecords();

    List<OrdersDto> ordersDtoList = records.stream().map((item) -> {
        OrdersDto ordersDto = new OrdersDto();
        //通过订单id查询order_id订单明细，得到一个订单明细的集合
        List<OrderDetail> orderDetailListByOrderId = orderService.getOrderDetailListByOrderId(item.getId());
        BeanUtils.copyProperties(item, ordersDto);
        ordersDto.setOrderDetails(orderDetailListByOrderId);
        return ordersDto;
    }).collect(Collectors.toList());

    BeanUtils.copyProperties(pageInfo,pageDto,"records");
    pageDto.setRecords(ordersDtoList);
    return R.success(pageDto);
}

/**
 * @Description: 后台订单明细
 * @param page
 * @param pageSize
 * @param number
 * @param beginTime
 * @param endTime
 * @return: com.itheima.reggie.common.R<com.baomidou.mybatisplus.extension.plugins.pagination.Page>
 * @Author: Jingq
 * @Date: 2023/6/1 22:43
 */

@GetMapping("/page")
    public R<Page> page(int page,int pageSize,String number,String beginTime,String endTime){
    Page<Orders> pageInfo = new Page<>();
    LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
    //查询
    queryWrapper.like(number!=null,Orders::getNumber,number);
    queryWrapper.gt(StringUtils.isNotEmpty(beginTime),Orders::getOrderTime,beginTime);
    queryWrapper.lt(StringUtils.isNotEmpty(endTime),Orders::getOrderTime,endTime);
    orderService.page(pageInfo,queryWrapper);
    return R.success(pageInfo);
}






}
