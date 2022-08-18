package com.yan.crm_proj.controller;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;

import com.yan.crm_proj.model.*;
import com.yan.crm_proj.service.*;
import com.yan.crm_proj.util.*;

import static com.yan.crm_proj.constant.AppConstant.TaskStatus.*;
import static com.yan.crm_proj.constant.AttributeConstant.*;
import static com.yan.crm_proj.constant.TemplateConstant.*;
import static com.yan.crm_proj.constant.ViewConstant.*;

@Controller
@RequestMapping("")
public class AppController {
    @Autowired
    private UserUtil userUtil;

    @Autowired
    private TaskService taskService;

    // Fields
    private User mCurrentAccount;
    private boolean mIsByPass;

    // Check login
    @GetMapping(LOGIN_VIEW)
    public String login() {
        // check current account still valid
        if (!isValidAccount()) {
            return LOGIN_TEMP;
        } else {
            mIsByPass = true;
            return REDIRECT_PREFIX + INDEX_VIEW;
        }
    }

    // Load dashboard
    @GetMapping(value = { INDEX_VIEW, "/", "" })
    public ModelAndView index() {
        // check current account still valid
        if (!isValidAccount()) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(INDEX_TEMP);
            var tasksNotStartedCount = taskService.getTasksByStatus(NOT_STARTED).size();
            var tasksInProgressCount = taskService.getTasksByStatus(IN_PROGRESS).size();
            var tasksCompletedCount = taskService.getTasksByStatus(COMPLETED).size();
            var tasksCount = taskService.getTasks().size();
            mav.addObject(USER_PARAM, mCurrentAccount);
            mav.addObject(NOT_STARTED_SIZE_PARAM, tasksNotStartedCount);
            mav.addObject(IN_PROGRESS_SIZE_PARAM, tasksInProgressCount);
            mav.addObject(COMPLETED_SIZE_PARAM, tasksCompletedCount);
            mav.addObject(NOT_STARTED_PERCENT_PARAM, tasksCount == 0 ? 0 : tasksNotStartedCount * 100 / tasksCount);
            mav.addObject(IN_PROGRESS_PERCENT_PARAM, tasksCount == 0 ? 0 : tasksInProgressCount * 100 / tasksCount);
            mav.addObject(COMPLETED_PERCENT_PARAM, tasksCount == 0 ? 0 : tasksCompletedCount * 100 / tasksCount);
            return mav;
        }
    }

    // Load forbidden page
    @GetMapping(FORBIDDEN_VIEW)
    public String forbidden() {
        return FORBIDDEN_TEMP;
    }

    // Check valid account
    private boolean isValidAccount() {
        // check bypass
        if (mIsByPass) {
            return true;
        } else {
            mCurrentAccount = userUtil.getAccount();
            return mCurrentAccount != null;
        }
    }
}
