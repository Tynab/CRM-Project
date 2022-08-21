package com.yan.crm_project.controller;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;

import com.yan.crm_project.model.*;
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
    private AuthenticationUtil authenticationUtil;

    // Fields
    private User mCurrentAccount;

    // Load job page
    @GetMapping("")
    public ModelAndView job() {
        // check current account still valid
        if (!isValidAccount()) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(JOB_TEMP);
            var tasksNotStartedCount = taskService.getTasksByStatus(NOT_STARTED).size();
            var tasksInProgressCount = taskService.getTasksByStatus(IN_PROGRESS).size();
            var tasksCompletedCount = taskService.getTasksByStatus(COMPLETED).size();
            var tasksCount = tasksNotStartedCount + tasksInProgressCount + tasksCompletedCount;
            mav.addObject(USERS_PARAM, userService.getUsers());
            mav.addObject(USER_PARAM, mCurrentAccount);
            mav.addObject(NOT_STARTED_PERCENT_PARAM, tasksCount == 0 ? 0 : tasksNotStartedCount * 100 / tasksCount);
            mav.addObject(IN_PROGRESS_PERCENT_PARAM, tasksCount == 0 ? 0 : tasksInProgressCount * 100 / tasksCount);
            mav.addObject(COMPLETED_PERCENT_PARAM, tasksCount == 0 ? 0 : tasksCompletedCount * 100 / tasksCount);
            return mav;
        }
    }

    // Check valid account
    private boolean isValidAccount() {
        mCurrentAccount = authenticationUtil.getAccount();
        return mCurrentAccount != null;
    }
}
