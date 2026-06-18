package com.yan.crm_project;

import org.springframework.boot.builder.*;
import org.springframework.boot.web.servlet.support.*;

/**
 * Tom tat: Cho phep dong goi ung dung thanh WAR va chay tren servlet container
 * ben ngoai.
 */
public class ServletInitializer extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(CrmProjApplication.class);
    }
}
