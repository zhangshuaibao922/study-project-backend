package com.example.config;

import com.example.interceptor.Authorizeinterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {
    @Resource
    Authorizeinterceptor authorizeinterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authorizeinterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/api/auth/**");
    }
}