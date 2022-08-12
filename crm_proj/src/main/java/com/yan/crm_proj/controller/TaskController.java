package com.yan.crm_proj.controller;

import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;

import lombok.*;

import static com.yan.crm_proj.common.TemplateConstant.*;
import static com.yan.crm_proj.common.ViewConstant.*;

@Controller
@RequestMapping(TASK_VIEW)
public class TaskController {
    @GetMapping("")
    public ModelAndView task() {
        var mav = new ModelAndView(TASK_TEMP);
        return mav;
    }

    @GetMapping(ADD_VIEW)
    public ModelAndView taskAdd() {
        var mav = new ModelAndView(TASK_ADD_TEMP);
        return mav;
    }

    @GetMapping(EDIT_VIEW)
    public ModelAndView taskEdit() {
        var mav = new ModelAndView(TASK_EDIT_TEMP);
        return mav;
    }

    @GetMapping(DETAILS_VIEW)
    public ModelAndView taskDetail() {
        var mav = new ModelAndView(TASK_DETAILS_TEMP);
        return mav;
    }
}
