package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.utils.MailUtils;
import com.itheima.reggie.utils.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    /*
     * 向邮箱发送验证码
     * */
    @PostMapping("/sendMsg")
    public R<String> sendCodeToMail(@RequestBody User user, HttpSession session) {
        String mail = user.getPhone();

        if (StringUtils.isNotEmpty(mail)) {
            //生成随机的4位验证码
            String code = RandomUtil.getCode();
            log.info("code={}",code);
            //qq邮箱
            try {
               // SendMailUtil.sendEmail(mail,"外卖管理系统",code);
              //  MailUtils.sendMail(mail,"外卖管理系统",code);
            } catch (Exception e) {
                throw new CustomException("邮箱短信发送失败了!");
            }
            //将生成的验证码保存到session
            session.setAttribute(mail, code);
            return R.success("邮箱验证码短信发送成功!");
        }
        return R.error("邮箱短信发送失败!");

    }


    @PostMapping("/login")
    public R<User> login(@RequestBody Map map,HttpSession session){//前端返回phone和code
        String phone = map.get("phone").toString();//获取手机号
        String code = map.get("code").toString();//获取验证码
        Object codeInSession = session.getAttribute(phone);//从session中获取验证码

        if(codeInSession!=null&&codeInSession.equals(code)){//验证码对比
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);
            if(user==null){//新用户自动完成注册
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            return R.success(user);
        }
        return R.error("登录失败");
    }

/**
 * @Description: 退出登录
 * @param request
 * @return: com.itheima.reggie.common.R<java.lang.String>
 * @Author: Jingq
 * @Date: 2023/6/1 21:54
 */

    @PostMapping("/loginout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("user");
        return R.success("退出成功");
    }

}