package com.yan.crm_proj.controller;

import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;

import static com.yan.crm_proj.constant.TemplateConstant.*;
import static com.yan.crm_proj.constant.ViewConstant.*;

@Controller
@RequestMapping(PROJECT_VIEW)
public class ProjectController {
    @GetMapping("")
    public ModelAndView project() {
        var mav = new ModelAndView(PROJECT_TEMP);
        return mav;
    }

    @GetMapping(ADD_VIEW)
    public ModelAndView projectAdd() {
        var mav = new ModelAndView(PROJECT_ADD_TEMP);
        return mav;
    }

    @GetMapping(EDIT_VIEW)
    public ModelAndView projectEdit() {
        var mav = new ModelAndView(PROJECT_EDIT_TEMP);
        return mav;
    }
}
