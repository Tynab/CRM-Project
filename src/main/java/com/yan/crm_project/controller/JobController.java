package com.yan.crm_project.controller;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;

import com.yan.crm_project.service.*;
import com.yan.crm_project.util.*;

import static com.yan.crm_project.constant.ApplicationConstant.TaskStatus.*;
import static com.yan.crm_project.constant.AttributeConstant.*;
import static com.yan.crm_project.constant.TemplateConstant.*;
import static com.yan.crm_project.constant.ViewConstant.*;

@Controller
@RequestMapping(JOB_VIEW)
public class JobController {
    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ApplicationUtil applicationUtil;

    @Autowired
    private AuthenticationUtil authenticationUtil;

    // Load job page
    @GetMapping("")
    public ModelAndView job() {
        var account = authenticationUtil.getAccount();
        // check current account still valid
        if (account == null) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(JOB_TEMP);
            var tasks = taskService.getTasks();
            var tasksCount = tasks.size();
            mav.addObject(USERS_PARAM, userService.getUsers());
            mav.addObject(ACCOUNT_PARAM, account);
            mav.addObject(NOT_STARTED_PERCENT_PARAM, tasksCount == 0 ? 0
                    : applicationUtil.splitTasksByStatus(tasks, NOT_STARTED).size() * 100 / tasksCount);
            mav.addObject(IN_PROGRESS_PERCENT_PARAM, tasksCount == 0 ? 0
                    : applicationUtil.splitTasksByStatus(tasks, IN_PROGRESS).size() * 100 / tasksCount);
            mav.addObject(COMPLETED_PERCENT_PARAM, tasksCount == 0 ? 0
                    : applicationUtil.splitTasksByStatus(tasks, COMPLETED).size() * 100 / tasksCount);
            return mav;
        }
    }
}
