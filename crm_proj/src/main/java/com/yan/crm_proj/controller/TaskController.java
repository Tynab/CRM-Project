package com.yan.crm_proj.controller;

import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;

import lombok.*;

@Controller
@RequestMapping("/task")
public class TaskController {
    @GetMapping("")
    public ModelAndView task() {
        var mav = new ModelAndView("task");
        return mav;
    }

    @GetMapping("/add")
    public ModelAndView taskAdd() {
        var mav = new ModelAndView("task-add");
        return mav;
    }
}
