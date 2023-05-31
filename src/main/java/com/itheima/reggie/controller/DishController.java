package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    @Autowired
    private CategoryService categoryService;
/**
 * @Description: 新增菜品,数据库两张表操作案例
 * @param dishDto
 * @return: com.itheima.reggie.common.R<java.lang.String>
 * @Author: Jingq
 * @Date: 2023/5/30 10:21
 */

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
      //  log.info(dishDto.toString()+"哈哈哈");
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功!");
    }

/**
 * @Description: 菜品分页插查询
 * @param page
 * @param pageSize
 * @param name
 * @return: com.itheima.reggie.common.R<com.baomidou.mybatisplus.extension.plugins.pagination.Page>
 * @Author: Jingq
 * @Date: 2023/5/30 15:29
 */
//问题是我Dish表只有菜品分类名称id，菜品分类名称在DishDto或者是category里面(categoryName)
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //构造分页构造器对象
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name!=null,Dish::getName,name);//模糊查询匹配值‘%name%’
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //执行分页查询
        dishService.page(pageInfo,queryWrapper);



        //显示菜品分类名称
        //对象拷贝,pageInfo经过操作已经有值了，将pageInfo拷贝到dishDtoPage里面去（除了records）
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        //拿到records
        List<Dish> records = pageInfo.getRecords();//获取查询数据  Dish各个属性值的集合
        List<DishDto> list=records.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);//将item赋值给dishDto
            Long categoryId = item.getCategoryId();
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            if(category!=null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);//将菜品名称赋值给dishDto
            }
            return dishDto;
        }).collect(Collectors.toList());



        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }

/**
 * @Description: 根据id查询菜品信息和对应的口味信息
 * @param id
 * @return: com.itheima.reggie.common.R<com.itheima.reggie.dto.DishDto>
 * @Author: Jingq
 * @Date: 2023/5/30 22:14
 */

    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto dishDto= dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }


/**
 * @Description: 修改菜品
 * @param dishDto
 * @return: com.itheima.reggie.common.R<java.lang.String>
 * @Author: Jingq
 * @Date: 2023/5/30 22:15
 */

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){//保存和修改都用的saveWithFlavor这一个方法
         dishService.updateWithFlavor(dishDto);
        return  R.success("修改菜品成功!");
    }
/**
 * @Description: 删除与批量删除
 * @param ids
 * @return: com.itheima.reggie.common.R<java.lang.String>
 * @Author: Jingq
 * @Date: 2023/5/31 10:10
 */

@DeleteMapping
public R<String> delete(Long[] ids){
    List<Long> list = Arrays.asList(ids);
    dishService.removeByIds(list);
        return R.success("删除菜品成功!");
}
/**
 * @Description: 批量起售停售
 * @param status
 * @param ids
 * @return: com.itheima.reggie.common.R<java.lang.String>
 * @Author: Jingq
 * @Date: 2023/5/31 10:55
 */

@PostMapping("/status/{status}")
public R<String> updateMulStatus(@PathVariable Integer status, Long[] ids){
    List<Long> list = Arrays.asList(ids);
for(int i=0;i<ids.length;i++){
    Long id=ids[i];
    Dish dish = dishService.getById(id);
    dish.setStatus(status);
    dishService.updateById(dish);
}
    return R.success("修改菜品状态成功!");
}
/**
 * @Description: 新增套餐
 * @param dish
 * @return: com.itheima.reggie.common.R<java.util.List<com.itheima.reggie.entity.Dish>>
 * @Author: Jingq
 * @Date: 2023/5/31 11:19
 */

@GetMapping("/list")
    public R<List<Dish>> list(Dish dish){//传categoryId也可以，但是Dish更加通用,通过categoryId返回菜品名称的集合
    //添加菜品
    LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
    queryWrapper.eq(Dish::getStatus,1);//起售状态
    queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);//添加排序条件
    List<Dish> list = dishService.list(queryWrapper);

    //点击保存，将套餐以json形式提交到服务端

    return R.success(list);
}





}
















