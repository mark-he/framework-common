package com.eagletsoft.framework;

import com.eagletsoft.boot.framework.common.utils.ApplicationUtils;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@ComponentScan({ "com.eagletsoft" })
@PropertySource(value = "classpath:config/application.properties", encoding = "UTF-8", ignoreResourceNotFound = true)
public class App extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication APP = new SpringApplication(App.class);
        APP.setBannerMode(Banner.Mode.OFF);
        APP.setLogStartupInfo(false);
        ApplicationUtils.CONTEXT = APP.run(args);
    }
}
