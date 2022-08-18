package com.yan.crm_project.config;

import org.springframework.boot.web.server.*;
import org.springframework.boot.web.servlet.server.*;
import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.*;

import static com.yan.crm_project.constant.AttributeConstant.*;
import static com.yan.crm_project.constant.ViewConstant.*;
import static org.springframework.http.HttpStatus.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController(NOT_FOUND_VIEW).setViewName(FORWARD_PREFIX + INDEX_VIEW);
    }

    // Add error page to container
    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> containerCustomizer() {
        return container -> {
            container.addErrorPages(new ErrorPage(NOT_FOUND, NOT_FOUND_VIEW));
        };
    }
}
