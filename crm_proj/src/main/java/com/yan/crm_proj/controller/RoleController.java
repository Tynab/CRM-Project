package com.yan.crm_proj.controller;

import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;

import lombok.*;

import static com.yan.crm_proj.common.TemplateConstant.*;
import static com.yan.crm_proj.common.ViewConstant.*;

@Controller
@RequestMapping(ROLE_VIEW)
public class RoleController {
    @GetMapping("")
    public ModelAndView role() {
        var mav = new ModelAndView(ROLE_TEMP);
        return mav;
    }

    @GetMapping(ADD_VIEW)
    public ModelAndView roleAdd() {
        var mav = new ModelAndView(ROLE_ADD_TEMP);
        return mav;
    }

    @GetMapping(EDIT_VIEW)
    public ModelAndView roleEdit() {
        var mav = new ModelAndView(ROLE_EDIT_TEMP);
        return mav;
    }
}
