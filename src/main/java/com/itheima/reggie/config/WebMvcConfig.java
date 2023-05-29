package com.itheima.reggie.config;

import com.itheima.reggie.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

@Slf4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {
    /*
     * @Description: 设置静态资源映射
     * @param registry
     * @return: void
     * @Author: Jingq
     * @Date: 2023/5/26 23:03
     */

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始进行静态资源映射...");
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
    }
    /**
     * @Description: 扩展mvc框架的消息转换器，将Long型数据id转换成String,提供了对象转换器进行java对象到json数据的转换
     * @param converters
     * @return: void
     * @Author: Jingq
     * @Date: 2023/5/28 21:31
     */

    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("自己设置的扩展消息转换器。。。。。");
        //创建消息转换器
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置具体的对象映射器
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //通过设置索引，让自己的转换器放在最前面，否则默认的jackson转换器会在前面，用不上我们设置的转换器
        converters.add(0,messageConverter);
    }



}
