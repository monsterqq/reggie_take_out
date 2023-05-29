package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    /**
     * @Description: 新增分类
     * @param category
     * @return: com.itheima.reggie.common.R<java.lang.String>
     * @Author: Jingq
     * @Date: 2023/5/29 11:18
     */

    @PostMapping
    public R<String> save(@RequestBody Category category){ //返回类型要结合前端页面，看看具体用到了哪些数据
        log.info("category:{}",category);
    categoryService.save(category);
    return R.success("新增分类成功!");
}
/**
 * @Description: 分页查询
 * @param page
 * @param pageSize
 * @return: com.itheima.reggie.common.R<com.baomidou.mybatisplus.extension.plugins.pagination.Page>
 * @Author: Jingq
 * @Date: 2023/5/29 15:10
 */


@GetMapping("/page")
    public R<Page> page(int page,int pageSize){
     //分页构造器
    Page<Category> pageInfo = new Page<>();
    //条件构造器
    LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
    //添加排序条件，根据sort进行排序
    queryWrapper.orderByAsc(Category::getSort);
    //分页查询
    categoryService.page(pageInfo,queryWrapper);
    return R.success(pageInfo);
}

/**
 * @Description: 根据id删除分类
 * @param
 * @return: com.itheima.reggie.common.R<java.lang.String>
 * @Author: Jingq
 * @Date: 2023/5/29 15:20
 */

@DeleteMapping
    public R<String> delete(Long ids){
        log.info("删除分类，ids为：{}",ids);
        //categoryService.removeById(ids);
          categoryService.remove(ids);
        return R.success("分类信息删除成功");
    }
    @PutMapping
    public R<String> update(@RequestBody Category category){
    log.info("修改分类信息:{}",category);
          categoryService.updateById(category);
        return R.success("修改分类信息成功!");
    }

}
