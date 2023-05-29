package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController//@Controller + @ResponseBody(解析跳转路径)。
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;
    /**
     * @Description: 员工登录
     * @param request
     * @param employee
     * @return: com.itheima.reggie.common.R<com.itheima.reggie.entity.Employee>
     * @Author: Jingq
     * @Date: 2023/5/27 9:56
     */
    @PostMapping("login")
    //返回json格式所以需要用RequestBody
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){//HttpServletRequest提供session
        //1、将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        // 2、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper = queryWrapper.eq(Employee::getUsername, employee.getUsername());//Username='老王'
        Employee emp = employeeService.getOne(queryWrapper);//eg：查询Username='老王'的一整条数据对象
        //3、如果没有查询到则返回登录失败结果
        if(emp==null){
            return R.error("用户信息不存在，登录失败!");
        }
        // 4、密码比对，如果不一致则返回登录失败结果
        if(!emp.getPassword().equals(password)){
            return R.error("密码错误，登录失败!");
        }
        // 5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if(emp.getStatus()==0){
            return R.error("该员工账号已禁用，登录失败!");
        }
        //6、登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);//emp就是R的data emp就是登录者的信息
    }
/**
 * @Description: 员工退出登录
 * @param
 * @return:
 * @Author: Jingq
 * @Date: 2023/5/27 11:50
 */
@PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){//参数可以参考浏览器的header
        //1、清理Session中的用户id
    request.getSession().removeAttribute("employee");//清除上个方法登录成功设置的session
    //2、返回结果
    return R.success("退出成功!");//前端会判断R.code==1,在清除登陆时在浏览器Local Storage保存的R.data的userinfo进行删除，
                                                                                      // (index.html)然后进行页面的跳转.

    }


    /**
     * @Description: 新增员工
     * @param employee
     * @return: com.itheima.reggie.common.R<java.lang.String>
     * @Author: Jingq
     * @Date: 2023/5/27 21:58
     */

@PostMapping//增
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){//employee由前端填写表单提供
    log.info("新增员工，员工信息：{}",employee.toString());
    //由于表单对employee的信息填写不全，需要补充信息
    employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
    /*employee.setCreateTime(LocalDateTime.now());
    employee.setUpdateTime(LocalDateTime.now());
    Long empId = (Long) request.getSession().getAttribute("employee");
    employee.setCreateUser(empId);
    employee.setUpdateUser(empId);*/
    employeeService.save(employee);
    return R.success("新增员工成功");
}

/**
 * @Description:员工信息分页查询,数据展示到页面上
 * @param
 * @return:
 * @Author: Jingq
 * @Date: 2023/5/28 9:47
 */
@GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
    log.info("page={},pageSize={},name={}",page,pageSize,name);
    //构造分页构造器
    Page pageInfo = new Page(page, pageSize);
    //构造条件构造器
    LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
    //添加过滤条件
    queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);//当传进来的name值不为空时，将值赋值给name
    //添加排序条件
    queryWrapper.orderByDesc(Employee::getUpdateTime);
    //执行查询
    employeeService.page(pageInfo,queryWrapper);
    return R.success(pageInfo);
}

/** 
 * @Description: 根据id修改员工信息
 * @param
 * @return:  
 * @Author: Jingq
 * @Date: 2023/5/28 15:58
 */
@PutMapping//改
public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
    log.info(employee.toString());
    long id = Thread.currentThread().getId();
    log.info("线程id为:{}",id);
   /* Long empId = (Long) request.getSession().getAttribute("employee");
    employee.setUpdateTime(LocalDateTime.now());
    employee.setUpdateUser(empId);*/
    employeeService.updateById(employee);
    return R.success("员工信息修改成功！");
    /*Js处理Long型的数据id会丢失精度，导致数据更新失败
    * 我们可以在服务端给页面响应json数据进行处理，将long型数据统一转换成String字符串*/
}

/**
 * @Description: 根据id查询员工信息
 * @param
 * @return:
 * @Author: Jingq
 * @Date: 2023/5/28 22:50
 */
@GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
    Employee byId = employeeService.getById(id);
    if(byId!=null){
        return R.success(byId);
    }
    return R.error("没有查询到员工信息！");
}
}
