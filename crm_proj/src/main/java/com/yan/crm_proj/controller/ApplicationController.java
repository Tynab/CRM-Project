package com.yan.crm_proj.controller;

import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;

@Controller
@RequestMapping("")
public class ApplicationController {
    @GetMapping("/login")
    public ModelAndView login() {
        var mav = new ModelAndView("login");
        return mav;
    }

    @GetMapping("/index")
    public ModelAndView index() {
        var mav = new ModelAndView("index");
        return mav;
    }

    @GetMapping("/403")
    public ModelAndView error() {
        var mav = new ModelAndView("403");
        return mav;
    }
}
