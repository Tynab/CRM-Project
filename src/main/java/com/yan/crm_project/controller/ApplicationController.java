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
@RequestMapping("")
public class ApplicationController {
    @Autowired
    public UserController userController;

    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private AuthenticationUtil authenticationUtil;

    // Fields
    private User mCurrentAccount;
    private String mMsg;
    private boolean mIsByPass;
    private boolean mIsMsgShow;

    // Check login
    @GetMapping(LOGIN_VIEW)
    public String login() {
        // check current account still valid
        if(!isValidAccount()) {
            return LOGIN_TEMP;
        }else{
            return REDIRECT_PREFIX + INDEX_VIEW;
        }
    }
    // public ModelAndView login(boolean error) {
    //     // check current account still valid
    //     if (!isValidAccount()) {
    //         var mav = new ModelAndView(LOGIN_VIEW);
    //         if (error) {
    //             mIsMsgShow = true;
    //             mMsg = "Tài khoản đăng nhập chưa đúng!";
    //             showMessageBox(mav);
    //         }
    //         return mav;
    //     } else {
    //         mIsByPass = true;
    //         return new ModelAndView(REDIRECT_PREFIX + INDEX_VIEW);
    //     }
    // }

    // Search user
    @GetMapping(SEARCH_VIEW)
    public String search(String name) {
        if (!isValidAccount()) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            var users = userService.getUsers(name);
            mIsByPass = true;
            if (users.size() > 0) {
                return userController.findUser(users.get(0).getId(), DETAILS_VIEW);
            } else {
                return REDIRECT_PREFIX + BLANK_VIEW;
            }
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
            var tasks = taskService.getTasks();
            var tasksCount = tasks.size();
            var tasksNotStartedCount = tasks.stream().filter(task -> task.getStatus().getId() == NOT_STARTED).count();
            var tasksInProgressCount = tasks.stream().filter(task -> task.getStatus().getId() == IN_PROGRESS).count();
            var tasksCompletedCount = tasks.stream().filter(task -> task.getStatus().getId() == COMPLETED).count();
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

    // Load blank page
    @GetMapping(BLANK_VIEW)
    public ModelAndView blank() {
        // check current account still valid
        if (!isValidAccount()) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(BLANK_TEMP);
            mav.addObject(USER_PARAM, mCurrentAccount);
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
            mCurrentAccount = authenticationUtil.getAccount();
            return mCurrentAccount != null;
        }
    }

    // Show message
    private void showMessageBox(ModelAndView mav) {
        // check flag
        if (mIsMsgShow) {
            mav.addObject(FLAG_MSG_PARAM, true);
            mav.addObject(MSG_PARAM, mMsg);
            mIsMsgShow = false;
        }
    }
}
