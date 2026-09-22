package com.tekram.config;

import com.tekram.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                // 路径匹配不区分 HTTP 方法，GET /items 在 preHandle 中放行。
                .addPathPatterns("/users/**", "/auth/logout",
                        "/items/me", "/items", "/items/{itemId}/**",
                        "messages/**",
                        "/orders/**");
    }
}
