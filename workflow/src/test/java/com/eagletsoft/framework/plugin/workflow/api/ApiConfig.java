package com.eagletsoft.framework.plugin.workflow.api;

import com.eagletsoft.framework.plugin.workflow.WorkflowContext;
import com.eagletsoft.framework.plugin.workflow.data.ProcessInstanceBo;
import com.eagletsoft.framework.plugin.workflow.data.TaskBo;
import com.eagletsoft.framework.plugin.workflow.integrate.ProcessListener;
import com.eagletsoft.framework.plugin.workflow.integrate.UserProvider;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.impl.identity.Authentication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Configuration
public class ApiConfig extends SpringBootServletInitializer implements WebMvcConfigurer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return super.configure(builder);
    }


    @Order(Ordered.LOWEST_PRECEDENCE)
    @Bean
    public FilterRegistrationBean shiroFilterBean(){
        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setFilter(authFilter());
        bean.addInitParameter("targetFilterLifecycle","true");
        bean.addUrlPatterns("/*");
        return bean;
    }

    @Bean
    public Filter authFilter() {
        return new Filter() {
            @Override
            public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
                String userId = ((HttpServletRequest)servletRequest).getParameter("user");

                if (StringUtils.isEmpty(userId)) {
                    userId = "MARK";
                }
                WorkflowContext.init("bms", userId);
                WorkflowContext.get().setSuperRight(true);
                filterChain.doFilter(servletRequest, servletResponse);
            }
        };
    }

    @Bean
    public ProcessListener listener() {
        return new ProcessListener() {
            @Override
            public void onProcess(String event, DelegateExecution execution) {
                System.out.println("PROCESS: " + event);
            }

            @Override
            public void onTask(String event, DelegateTask task) {
                System.out.println("TASK: " +event);
            }
        };
    }

    @Bean
    public UserProvider userProvider() {
        return new UserProvider() {
            @Override
            public List<String> withGroup(ProcessInstanceBo pi, String... groups) {
                System.out.println("GROUPS: " + groups[0]);
                return Arrays.asList(groups);
            }

            @Override
            public List<String> withName(ProcessInstanceBo pi, String... names) {
                System.out.println("NAMES: " + names[0] + "");
                names[0] = names[0] + "PPP";
                return Arrays.asList(names);
            }
        };
    }
}