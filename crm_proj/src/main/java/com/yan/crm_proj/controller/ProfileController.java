package com.yan.crm_proj.controller;

import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;

import static com.yan.crm_proj.common.TemplateConstant.*;
import static com.yan.crm_proj.common.ViewConstant.*;

@Controller
@RequestMapping(PROFILE_VIEW)
public class ProfileController {
    @GetMapping("")
    public ModelAndView profile() {
        var mav = new ModelAndView(PROFILE_TEMP);
        return mav;
    }

    @GetMapping(EDIT_VIEW + INFO_VIEW)
    public ModelAndView profileEditInfo() {
        var mav = new ModelAndView(PROFILE_INFO_TEMP);
        return mav;
    }

    @GetMapping(EDIT_VIEW + TASK_VIEW)
    public ModelAndView profileEditTask() {
        var mav = new ModelAndView(PROFILE_TASK_TEMP);
        return mav;
    }
}
