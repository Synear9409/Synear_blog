package com.cxzjava.blog.util.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 注册拦截器
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     *
     * @param registry 指定访问模板的路径
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("").setViewName("admin/login");
        registry.addViewController("/admin/main").setViewName("admin/index");
    }

    /**
     *
     * @param registry
 *            addPathPatterns：将需要拦截的请求路径拦截
 *            excludePathPatterns：排除不需要拦截的
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin","/admin/login");
    }
}
