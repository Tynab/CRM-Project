package com.yan.crm_proj.controller;

import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;

@Controller
@RequestMapping("/profile")
public class ProfileController {
    @GetMapping("")
    public ModelAndView profile() {
        var mav = new ModelAndView("profile");
        return mav;
    }

    @GetMapping("/edit")
    public ModelAndView profileEdit() {
        var mav = new ModelAndView("profile-edit");
        return mav;
    }
}
