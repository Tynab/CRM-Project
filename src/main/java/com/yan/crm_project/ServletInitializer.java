package com.yan.crm_project;

import org.springframework.boot.builder.*;
import org.springframework.boot.web.servlet.support.*;

public class ServletInitializer extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(CrmProjApplication.class);
    }
}
