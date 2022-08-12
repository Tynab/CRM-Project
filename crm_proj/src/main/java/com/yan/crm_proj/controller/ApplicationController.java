package com.yan.crm_proj.controller;

import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;

import static com.yan.crm_proj.common.TemplateConstant.*;
import static com.yan.crm_proj.common.ViewConstant.*;

@Controller
@RequestMapping("")
public class ApplicationController {
    @GetMapping(LOGIN_VIEW)
    public ModelAndView login() {
        var mav = new ModelAndView(LOGIN_TEMP);
        return mav;
    }

    @GetMapping(INDEX_VIEW)
    public ModelAndView index() {
        var mav = new ModelAndView(INDEX_TEMP);
        return mav;
    }

    @GetMapping(FORBIDDEN_VIEW)
    public ModelAndView error() {
        var mav = new ModelAndView(FORBIDDEN_TEMP);
        return mav;
    }
}
