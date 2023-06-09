package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SetmealDishService setmealDishService;


    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {//前端除了提交过来Setmeal还提交了setmealDishes，所以选择SetmealDto
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功!");

    }

    /**
     * @Description: 套餐信息的分页查询 records在前端response查看（setmeal）
     * @param page
     * @param pageSize
     * @param name
     * @return: com.itheima.reggie.common.R<com.baomidou.mybatisplus.extension.plugins.pagination.Page>
     * @Author: Jingq
     * @Date: 2023/5/31 15:39
     */

@GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //构造分页构造器对象
    Page<Setmeal> pageInfo = new Page<>(page,pageSize);
    Page<SetmealDto> setmealDishPage = new Page<>();
    //构造条件构造器
    LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.like(name!=null,Setmeal::getName,name);//模糊查询
    queryWrapper.orderByDesc(Setmeal::getUpdateTime);//排序
    setmealService.page(pageInfo,queryWrapper);


    //显示分类菜品名称
//对象拷贝
    BeanUtils.copyProperties(pageInfo,setmealDishPage,"records");//只拷贝对象，不拷贝数据
    List<Setmeal> records = pageInfo.getRecords();
    /*[Setmeal(id=1663757351408943105, categoryId=1413342269393674242, name=B套餐, price=88800.00, status=1, code=, description=无无,
     image=43a368f6-81b3-4e90-8cc0-6c92638f947a.jpg, createTime=2023-05-31T12:00:38, updateTime=2023-05-31T12:00:38, createUser=1,
     updateUser=1),
     Setmeal(id=1415580119015145474, categoryId=1413386191767674881, name=儿童套餐A计划, price=4000.00, status=1, code=, description=,
     image=61d20592-b37f-4d72-a864-07ad5bb8f3bb.jpg, createTime=2021-07-15T15:52:55, updateTime=2021-07-15T15:52:55,
     createUser=1415576781934608386, updateUser=1415576781934608386)]*////records就是页面上展示数据的集合
    List<SetmealDto> list = records.stream().map((item) -> {
        SetmealDto setmealDto = new SetmealDto();
        //对象拷贝
        BeanUtils.copyProperties(item, setmealDto);
        //分类id
        Long categoryId = item.getCategoryId();
        Category category = categoryService.getById(categoryId);
        if (category != null) {
            setmealDto.setCategoryName(category.getName());
        }
        return setmealDto;
    }).collect(Collectors.toList());
    setmealDishPage.setRecords(list);


    return R.success(setmealDishPage);
}

    /**
     * @Description: 删除套餐及其相关数据  停售才可以删除
     * @param ids
     * @return: void
     * @Author: Jingq
     * @Date: 2023/5/31 16:24
     */
@DeleteMapping
public R<String> delete(@RequestParam List<Long> ids){
    setmealService.removeWishDish(ids);
    return R.success("套餐数据删除成功!");
}

/**
 * @Description: 修改状态
 * @param status
 * @param ids
 * @return: com.itheima.reggie.common.R<java.lang.String>
 * @Author: Jingq
 * @Date: 2023/5/31 17:26
 */

    @PostMapping("/status/{status}")
    public R<String> updateMulStatus(@PathVariable Integer status,Long[] ids){
    for(int i=0;i<ids.length;i++) {
        Long id = ids[i];
        Setmeal setmeal = setmealService.getById(id);
        setmeal.setStatus(status);
        setmealService.updateById(setmeal);

    }
        return R.success("修改套餐状态成功!");
    }

/**
 * @Description: 修改时获取信息
 * @param id
 * @return: com.itheima.reggie.common.R<com.itheima.reggie.dto.SetmealDto>
 * @Author: Jingq
 * @Date: 2023/5/31 17:52
 */

@GetMapping("/{id}")
    public R<SetmealDto> get(@PathVariable Long id){
    SetmealDto setmealDto=setmealService.getByIdWithDish(id);
    return R.success(setmealDto);
}
/**
 * @Description: 修改后进行保存
 * @param setmealDto
 * @return: com.itheima.reggie.common.R<java.lang.String>
 * @Author: Jingq
 * @Date: 2023/5/31 17:52
 */

@PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        setmealService.updateWithDish(setmealDto);
        return R.success("修改套餐成功!");
}



@GetMapping("/list")
public R<List<Setmeal>> list(Setmeal setmeal){//key-value不需要注解
    LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(Setmeal::getStatus,setmeal.getStatus());
    queryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
    queryWrapper.orderByDesc(Setmeal::getUpdateTime);
    List<Setmeal> list = setmealService.list(queryWrapper);
    System.out.println(list+"hhhhh");
    return R.success(list);
}








}