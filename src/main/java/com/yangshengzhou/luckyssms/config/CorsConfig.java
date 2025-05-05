package com.yangshengzhou.luckyssms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 配置类用于设置跨域资源共享（CORS）规则。
 * 在Spring Boot应用中，通过实现WebMvcConfigurer接口，
 * 可以自定义HTTP请求的处理方式，包括如何处理跨域请求。
 */
@Configuration // 标记该类为配置类，Spring Boot在启动时会自动加载此类中的配置。
public class CorsConfig implements WebMvcConfigurer {

    /**
     * 重写addCorsMappings方法来定义具体的CORS策略。
     *
     * @param registry CorsRegistry对象，用于注册CORS映射。
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 添加一个映射规则，匹配所有以/api开头的URL路径。
        // 这意味着任何对这些路径的请求都将受到下面指定的CORS规则的影响。
        registry.addMapping("/api/**")
                // 设置允许访问资源的源（即前端应用所在的地址）。这里只允许来自http://localhost:8080的请求。
                .allowedOrigins("http://localhost:8080")

                // 允许的HTTP方法。这里列出了GET、POST、PUT、DELETE和OPTIONS方法。
                // OPTIONS方法通常用于预检请求（preflight request），浏览器会在实际请求之前发送这个请求来确认服务器是否接受真正的请求。
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")

                // 允许的所有请求头。使用'*'表示接受所有自定义请求头。
                // 如果有特定的请求头需要被允许，可以在这里列出它们的名字。
                .allowedHeaders("*")

                // 暴露给前端的响应头。同样使用'*'表示暴露所有可能的响应头。
                // 注意：出于安全考虑，默认情况下不是所有的响应头都会被暴露给前端应用。
                .exposedHeaders("*")

                // 是否支持凭证（如Cookies, HTTP认证等）。如果设置为true，则Access-Control-Allow-Origin不能使用通配符'*'。
                // 因为这可能会导致安全风险，所以这里明确指定了允许的源。
                .allowCredentials(true);
    }
}