package com.yan.crm_proj.controller;

import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;

import lombok.*;

@Controller
@RequestMapping("/role")
public class RoleController {
    @GetMapping("")
    public ModelAndView role() {
        var mav = new ModelAndView("role");
        return mav;
    }

    @GetMapping("/add")
    public ModelAndView roleAdd() {
        var mav = new ModelAndView("role-add");
        return mav;
    }
}
