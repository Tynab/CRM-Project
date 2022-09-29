package com.yan.crm_project.controller;

import org.springframework.beans.factory.annotation.*;
import org.springframework.security.access.prepost.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;

import com.yan.crm_project.model.*;
import com.yan.crm_project.service.*;
import com.yan.crm_project.util.*;

import static com.yan.crm_project.common.Bean.*;
import static com.yan.crm_project.constant.ApplicationConstant.*;
import static com.yan.crm_project.constant.ApplicationConstant.TaskStatus.*;
import static com.yan.crm_project.constant.AttributeConstant.*;
import static com.yan.crm_project.constant.TemplateConstant.*;
import static com.yan.crm_project.constant.ViewConstant.*;
import static org.springframework.util.StringUtils.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
@RequestMapping(USER_VIEW)
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private ApplicationUtil applicationUtil;

    @Autowired
    private AuthenticationUtil authenticationUtil;

    // Load user list
    @GetMapping("")
    public ModelAndView user() {
        var account = authenticationUtil.getAccount();
        // check current account still valid
        if (account == null) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(USER_TEMP);
            mav.addObject(USERS_PARAM, userService.getUsers());
            mav.addObject(ACCOUNT_PARAM, account);
            _isMsgShow = applicationUtil.showMessageBox(mav);
            return mav;
        }
    }

    // Load new user input form
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(ADD_VIEW)
    public ModelAndView userAdd() {
        var account = authenticationUtil.getAccount();
        // check current account still valid
        if (account == null) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(USER_ADD_TEMP);
            mav.addObject(ACCOUNT_PARAM, account);
            mav.addObject(ROLES_PARAM, roleService.getRoles());
            mav.addObject(DEFAULT_ROLE_ID_PARAM, DEFAULT_ROLE);
            _isMsgShow = applicationUtil.showMessageBox(mav);
            return mav;
        }
    }

    // Add new user
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(ADD_VIEW + SAVE_VIEW)
    public String userAddSave(User user) {
        // Check current account still valid
        if (authenticationUtil.getAccount() == null) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            _isMsgShow = true;
            // check email is already exist
            if (userService.getUser(user.getEmail()) != null) {
                _msg = "Tài khoản email này đã được đăng ký!";
                return REDIRECT_PREFIX + USER_VIEW + ADD_VIEW;
            } else {
                user.setImage(DEFAULT_AVATAR);
                userService.saveUser(user);
                _msg = "Tài khoản đã được tạo thành công!";
                return REDIRECT_PREFIX + USER_VIEW;
            }
        }
    }

    // Load edit user input form
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(EDIT_VIEW)
    public ModelAndView userEdit(int id) {
        var account = authenticationUtil.getAccount();
        // check current account still valid
        if (account == null) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var user = userService.getUser(id);
            // check user is exist
            if (user == null) {
                _isMsgShow = true;
                _msg = "Tài khoản không tồn tại!";
                return new ModelAndView(REDIRECT_PREFIX + USER_VIEW);
            } else {
                var mav = new ModelAndView(USER_EDIT_TEMP);
                mav.addObject(ACCOUNT_PARAM, account);
                mav.addObject(USER_PARAM, user);
                mav.addObject(ROLES_PARAM, roleService.getRoles());
                return mav;
            }
        }
    }

    // Edit user
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(EDIT_VIEW + SAVE_VIEW)
    public String userEditSave(User user) {
        // check current account still valid
        if (authenticationUtil.getAccount() == null) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            // check new password
            if (hasText(user.getPassword())) {
                userService.saveUser(user);
            } else {
                userService.saveUserWithoutPassword(user);
            }
            _isMsgShow = true;
            _msg = "Tài khoản đã được cập nhật thành công!";
            return REDIRECT_PREFIX + USER_VIEW;
        }
    }

    // Delete user
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = DELETE_VIEW, method = { GET, DELETE })
    public String userDelete(int id) {
        // check current account still valid
        if (authenticationUtil.getAccount() == null) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            var user = userService.getUser(id);
            _isMsgShow = true;
            // check if user is exist
            if (user == null) {
                _msg = "Tài khoản không tồn tại!";
            } else {
                // check user disconnect
                if (user.getProjects().size() > 0) {
                    _msg = "Tài khoản này đang có dự án, không thể xóa!";
                } else if (user.getTasks().size() > 0) {
                    _msg = "Tài khoản này đang có công việc, không thể xóa!";
                } else {
                    userService.deleteUser(id);
                    fileUploadService.remove(id + ".jpg");
                    _msg = "Xóa tài khoản thành công!";
                }
            }
            return REDIRECT_PREFIX + USER_VIEW;
        }
    }

    // Load user detail page
    @GetMapping(DETAILS_VIEW)
    public ModelAndView userDetails(int id) {
        var account = authenticationUtil.getAccount();
        // check current account still valid
        if (account == null) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var user = userService.getUser(id);
            // check user is exist
            if (user == null) {
                _isMsgShow = true;
                _msg = "Tài khoản không tồn tại!";
                return new ModelAndView(REDIRECT_PREFIX + USER_VIEW);
            } else {
                var mav = new ModelAndView(USER_DETAILS_TEMP);
                var tasks = user.getTasks();
                var tasksNotStarted = applicationUtil.splitTasksByStatus(tasks, NOT_STARTED);
                var tasksInProgress = applicationUtil.splitTasksByStatus(tasks, IN_PROGRESS);
                var tasksCompleted = applicationUtil.splitTasksByStatus(tasks, COMPLETED);
                var tasksCount = tasks.size();
                mav.addObject(ACCOUNT_PARAM, account);
                mav.addObject(USER_PARAM, user);
                mav.addObject(TASKS_NOT_STARTED_PARAM, tasksNotStarted);
                mav.addObject(TASKS_IN_PROGRESS_PARAM, tasksInProgress);
                mav.addObject(TASKS_COMPLETED_PARAM, tasksCompleted);
                mav.addObject(NOT_STARTED_PERCENT_PARAM,
                        tasksCount == 0 ? 0 : tasksNotStarted.size() * 100 / tasksCount);
                mav.addObject(IN_PROGRESS_PERCENT_PARAM,
                        tasksCount == 0 ? 0 : tasksInProgress.size() * 100 / tasksCount);
                mav.addObject(COMPLETED_PERCENT_PARAM, tasksCount == 0 ? 0 : tasksCompleted.size() * 100 / tasksCount);
                return mav;
            }
        }
    }
}
