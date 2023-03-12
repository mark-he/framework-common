package com.eagletsoft.framework.plugins.api;

import com.eagletsoft.boot.framework.data.json.context.ExtJsonMessageConverter;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ApiConfig extends SpringBootServletInitializer implements WebMvcConfigurer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return super.configure(builder);
    }

    @Bean
    public ServletRegistrationBean apiServlet() { 
        DispatcherServlet servlet = new ApiServlet();
        servlet.setThrowExceptionIfNoHandlerFound(true);

        AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
        servlet.setApplicationContext(applicationContext);

        ServletRegistrationBean bean = new ServletRegistrationBean(servlet, "/*");
        bean.setName("defaultApi");
        Map<String, String> params = new HashMap<>();
        params.put("detectAllHandlerExceptionResolvers", "true");
        params.put("contextConfigLocation", "classpath*:spring/defaultApi.xml");
        bean.setInitParameters(params);
        bean.setLoadOnStartup(1);
        return bean;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "OPTIONS", "DELETE", "PATCH");
    }


    @Bean
    public HandlerExceptionResolver apiExceptionResolver() {
        return new ProjectApiExceptionResolver();
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new ExtJsonMessageConverter();
        return converter;
    }
}
