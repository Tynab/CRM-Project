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
@RequestMapping("")
/**
 * Tom tat: Dieu huong cac trang chung cua ung dung nhu login, dashboard, search
 * va trang loi don gian.
 */
public class ApplicationController {
    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ApplicationUtil applicationUtil;

    @Autowired
    private AuthenticationUtil authenticationUtil;

    /**
     * Hien thi trang dang nhap hoac day nguoi dung da dang nhap ve dashboard.
     */
    @GetMapping(LOGIN_VIEW)
    public ModelAndView login(boolean error) {
        if (authenticationUtil.getAccount() == null) {
            var mav = new ModelAndView(LOGIN_TEMP);
            if (error) {
                applicationUtil.addMessage(mav, "Tài khoản đăng nhập chưa đúng!");
            }
            return mav;
        } else {
            return new ModelAndView(REDIRECT_PREFIX + INDEX_VIEW);
        }
    }

    /**
     * Nap dashboard tong quan tien do tat ca cong viec.
     */
    @GetMapping(value = { INDEX_VIEW, "/", "" })
    public ModelAndView index() {
        var account = authenticationUtil.getAccount();
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
            mav.addObject(NOT_STARTED_PERCENT_PARAM, applicationUtil.calculatePercent(tasksNotStartedCount, tasksCount));
            mav.addObject(IN_PROGRESS_PERCENT_PARAM, applicationUtil.calculatePercent(tasksInProgressCount, tasksCount));
            mav.addObject(COMPLETED_PERCENT_PARAM, applicationUtil.calculatePercent(tasksCompletedCount, tasksCount));
            return mav;
        }
    }

    /**
     * Tim user theo ten va chuyen den trang chi tiet ket qua dau tien.
     */
    @GetMapping(SEARCH_VIEW)
    public String search(String name) {
        if (authenticationUtil.getAccount() == null) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            var users = userService.getUsers(name);
            if (users.size() > 0) {
                return REDIRECT_PREFIX + USER_VIEW + DETAILS_VIEW + "?id=" + users.get(0).getId();
            } else {
                return REDIRECT_PREFIX + BLANK_VIEW;
            }
        }
    }

    /**
     * Trang trong khi search khong co ket qua.
     */
    @GetMapping(BLANK_VIEW)
    public ModelAndView blank() {
        var account = authenticationUtil.getAccount();
        if (account == null) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(BLANK_TEMP);
            mav.addObject(ACCOUNT_PARAM, account);
            return mav;
        }
    }

    /**
     * Trang 403 cho request bi tu choi quyen.
     */
    @GetMapping(FORBIDDEN_VIEW)
    public String forbidden() {
        return FORBIDDEN_TEMP;
    }
}
