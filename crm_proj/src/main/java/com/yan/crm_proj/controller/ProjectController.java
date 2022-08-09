package com.yan.crm_proj.controller;

import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;

@Controller
@RequestMapping("/project")
public class ProjectController {
    @GetMapping("")
    public ModelAndView project() {
        var mav = new ModelAndView("project");
        return mav;
    }

    @GetMapping("/add")
    public ModelAndView projectAdd() {
        var mav = new ModelAndView("project-add");
        return mav;
    }

    @GetMapping("/details")
    public ModelAndView projectDetails() {
        var mav = new ModelAndView("project-details");
        return mav;
    }
}
