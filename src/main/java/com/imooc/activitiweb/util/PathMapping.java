package com.imooc.activitiweb.util;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 路径
 *
 * @author jzwu
 * @since 2021/3/16 0016
 */
@Configuration
public class PathMapping implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        /* 原来默认映射 */
        registry.addResourceHandler("/**").addResourceLocations("classpath:/resources/");
        registry.addResourceHandler("/bpmn/**")
                .addResourceLocations(GlobaConfig.BPMN_MAPPING);
    }
}