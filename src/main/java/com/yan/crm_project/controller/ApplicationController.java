package com.yan.crm_project.controller;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;

import com.yan.crm_project.service.*;
import com.yan.crm_project.util.*;

import static com.yan.crm_project.common.Bean.*;
import static com.yan.crm_project.constant.ApplicationConstant.TaskStatus.*;
import static com.yan.crm_project.constant.AttributeConstant.*;
import static com.yan.crm_project.constant.TemplateConstant.*;
import static com.yan.crm_project.constant.ViewConstant.*;

@Controller
@RequestMapping("")
public class ApplicationController {
    @Autowired
    public UserController userController;

    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ApplicationUtil applicationUtil;

    @Autowired
    private AuthenticationUtil authenticationUtil;

    // Check login
    @GetMapping(LOGIN_VIEW)
    public ModelAndView login(boolean error) {
        // check current account still valid
        if (authenticationUtil.getAccount() == null) {
            var mav = new ModelAndView(LOGIN_TEMP);
            // login failed
            if (error) {
                _isMsgShow = true;
                _msg = "Tài khoản đăng nhập chưa đúng!";
                _isMsgShow = applicationUtil.showMessageBox(mav);
            }
            return mav;
        } else {
            return new ModelAndView(REDIRECT_PREFIX + INDEX_VIEW);
        }
    }

    // Load dashboard
    @GetMapping(value = { INDEX_VIEW, "/", "" })
    public ModelAndView index() {
        var account = authenticationUtil.getAccount();
        // check current account still valid
        if (account == null) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(INDEX_TEMP);
            var tasks = taskService.getTasks();
            var tasksCount = tasks.size();
            var tasksNotStartedCount = applicationUtil.splitTasksByStatus(tasks, NOT_STARTED).size();
            var tasksInProgressCount = applicationUtil.splitTasksByStatus(tasks, IN_PROGRESS).size();
            var tasksCompletedCount = applicationUtil.splitTasksByStatus(tasks, COMPLETED).size();
            mav.addObject(ACCOUNT_PARAM, account);
            mav.addObject(NOT_STARTED_SIZE_PARAM, tasksNotStartedCount);
            mav.addObject(IN_PROGRESS_SIZE_PARAM, tasksInProgressCount);
            mav.addObject(COMPLETED_SIZE_PARAM, tasksCompletedCount);
            mav.addObject(NOT_STARTED_PERCENT_PARAM, tasksCount == 0 ? 0 : tasksNotStartedCount * 100 / tasksCount);
            mav.addObject(IN_PROGRESS_PERCENT_PARAM, tasksCount == 0 ? 0 : tasksInProgressCount * 100 / tasksCount);
            mav.addObject(COMPLETED_PERCENT_PARAM, tasksCount == 0 ? 0 : tasksCompletedCount * 100 / tasksCount);
            return mav;
        }
    }

    // Search user
    @GetMapping(SEARCH_VIEW)
    public String search(String name) {
        // check current account still valid
        if (authenticationUtil.getAccount() == null) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            var users = userService.getUsers(name);
            // match user
            if (users.size() > 0) {
                return REDIRECT_PREFIX + USER_VIEW + DETAILS_VIEW + "?id=" + users.get(0).getId();
            } else {
                return REDIRECT_PREFIX + BLANK_VIEW;
            }
        }
    }

    // Load blank page
    @GetMapping(BLANK_VIEW)
    public ModelAndView blank() {
        var account = authenticationUtil.getAccount();
        // check current account still valid
        if (account == null) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(BLANK_TEMP);
            mav.addObject(ACCOUNT_PARAM, account);
            return mav;
        }
    }

    // Load forbidden page
    @GetMapping(FORBIDDEN_VIEW)
    public String forbidden() {
        return FORBIDDEN_TEMP;
    }
}
