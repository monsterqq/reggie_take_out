package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.AddressBook;
import com.itheima.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description: 地址簿管理
 * @param
 * @return:
 * @Author: Jingq
 * @Date: 2023/6/1 9:03
 */
@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;
    /**
     * @Description: 新增地址
     * @param addressBook
     * @return:
     * @Author: Jingq
     * @Date: 2023/6/1 9:05
     */
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook){
       addressBook.setUserId(BaseContext.getCurrentId());//addressBook的userid和user表的id一样
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }

/**
 * @Description: 设置默认地址,自己改的
 * @param
 * @return:
 * @Author: Jingq
 * @Date: 2023/6/1 9:25
 */
@PutMapping("default")
public R<AddressBook> setDefault(@RequestBody AddressBook addressBook){
    LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
    /*addressBook.setUserId(BaseContext.getCurrentId());
    wrapper.eq(addressBook.getUserId()!=null,AddressBook::getUserId,addressBook.getUserId());*/
    wrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
    wrapper.set(AddressBook::getIsDefault,0);
    //update address_book set is_default=0 where use_id=?
    addressBookService.update(wrapper);
    addressBook.setIsDefault(1);
    //update address_book set is_default=0 where id=?
    addressBookService.updateById(addressBook);
        return R.success(addressBook);

}

/**
 * @Description: 根据id查询地址
 * @param
 * @return:
 * @Author: Jingq
 * @Date: 2023/6/1 10:21
 */

@GetMapping("/{id}")
public R<AddressBook> get(@PathVariable Long id){
    AddressBook addressBook = addressBookService.getById(id);
    if(addressBook!=null) {
        return R.success(addressBook);
    }else{
        return R.error("没有找到该对象");
    }
}
/**
 * @Description: 查询默认地址
 * @param
 * @return: com.itheima.reggie.common.R<com.itheima.reggie.entity.AddressBook>
 * @Author: Jingq
 * @Date: 2023/6/1 10:31
 */

@GetMapping("default")
public R<AddressBook> getDefault(){
    //select * from address_book where user_id=? and is_default=1
    LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
    queryWrapper.eq(AddressBook::getIsDefault,1);
    AddressBook one = addressBookService.getOne(queryWrapper);
    if(one==null){
        return R.error("没有找到该对象");
    }else{
        return R.success(one);
    }
}





/**
 * @Description: 查询指定用户的全部地址
 * @param addressBook
 * @return:
 * @Author: Jingq
 * @Date: 2023/6/1 9:31
 */
@GetMapping("/list")
    public R<List<AddressBook>> list(AddressBook addressBook){
    addressBook.setUserId(BaseContext.getCurrentId());
    //条件构造器
    LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(addressBook.getUserId()!=null,AddressBook::getUserId,addressBook.getUserId());//拿到刚才设置的userId
    queryWrapper.orderByDesc(AddressBook::getUpdateTime);
    List<AddressBook> list = addressBookService.list(queryWrapper);//查到的结果
    return R.success(list);
}

/**
 * @Description: 保存地址
 * @param addressBook
 * @return: com.itheima.reggie.common.R<java.lang.String>
 * @Author: Jingq
 * @Date: 2023/6/1 10:38
 */

@PutMapping
    public R<String> put(@RequestBody AddressBook addressBook){
    addressBookService.updateById(addressBook);
    return R.success("信息保存成功!");
}
/**
 * @Description: 删除地址
 * @param ids
 * @return: com.itheima.reggie.common.R<java.lang.String>
 * @Author: Jingq
 * @Date: 2023/6/1 10:46
 */

@DeleteMapping()
    public R<String> delete(Long ids){
    //AddressBook byId = addressBookService.getById(ids);
    addressBookService.removeById(ids);
    return R.success("删除成功!");
}











}
