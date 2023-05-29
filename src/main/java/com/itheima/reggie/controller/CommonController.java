package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;


/**
 * @Description: 文件上传和下载
 * @param
 * @return:
 * @Author: Jingq
 * @Date: 2023/5/29 20:35
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {
    @Value("${reggie.path}")
    private String basePath;





    /**
     * @Description: 文件上传
     * @param file
     * @return: com.itheima.reggie.common.R<java.lang.String>
     * @Author: Jingq
     * @Date: 2023/5/29 20:40
     */

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){//客户端会传送name=file，所以这里必须是file
        //file是临时文件，需要转存到指定位置，否则本次请求完成后临时文件会删除。
        log.info(file.toString()+"文件。。");
        String originalFilename = file.getOriginalFilename();//获取原始文件名hello.jpg
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));//拿到.jpg
        //使用UUID重新生成文件名，防止文件名称重复造成文件覆盖
        String fileName = UUID.randomUUID().toString()+suffix;//abc.jpg

        //创建一个目录对象，如果路径不存在，则自动创建
        File dir = new File(basePath);
        //判断当前目录是否存在
        if(!dir.exists()){
            //目录不存在，需要创建
            dir.mkdir();
        }



        try {
            file.transferTo(new File(basePath+fileName));//转存到
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName);//为了完成新增菜品，文件名需要保存到菜品里面去。所以需要存到数据库当中
    }









/**
 * @Description:文件下载
 * @param name
 * @param response
 * @return: void
 * @Author: Jingq
 * @Date: 2023/5/29 22:08
 */
@GetMapping("/download")
public void download(String name, HttpServletResponse response){

    try {
        //输入流，通过输入流读取文件内容
        FileInputStream fileInputStream = new FileInputStream(new File(basePath+name));//在这个文件夹中找到这个文件
        //输出流，通过输出流将文件写回浏览器，在浏览器中展示图片
        ServletOutputStream outputStream = response.getOutputStream();
        response.setContentType("image/jpeg");//需要的文件格式
        int len=0;
        byte[] bytes = new byte[1024];
       while( -1!=(len= fileInputStream.read(bytes))){//len==-1表示读完
           outputStream.write(bytes,0,len);
           outputStream.flush();
       }
        //关闭资源
        fileInputStream.close();
        outputStream.close();
    } catch (Exception e) {
        e.printStackTrace();
    }



}

}
