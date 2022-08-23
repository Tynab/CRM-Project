package com.yan.crm_project.controller;

import org.springframework.beans.factory.annotation.*;
import org.springframework.security.access.prepost.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;

import com.yan.crm_project.model.*;
import com.yan.crm_project.service.*;
import com.yan.crm_project.util.*;

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
    private TaskService taskService;

    @Autowired
    private ApplicationUtil applicationUtil;

    @Autowired
    private AuthenticationUtil authenticationUtil;

    @Autowired
    private StringUtil stringUtil;

    // Fields
    private User mCurrentAccount;
    private User mChoosenOne;
    private String mMsg;
    private boolean mIsByPass;
    private boolean mIsMsgShow;

    // Load user list
    @GetMapping("")
    public ModelAndView user() {
        // check current account still valid
        if (!isValidAccount()) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(USER_TEMP);
            mav.addObject(USERS_PARAM, userService.getUsers());
            mav.addObject(USER_PARAM, mCurrentAccount);
            showMessageBox(mav);
            mIsByPass = false;
            return mav;
        }
    }

    // Load new user input form
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(ADD_VIEW)
    public ModelAndView userAdd() {
        // Check current account still valid
        if (!isValidAccount()) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(USER_ADD_TEMP);
            mav.addObject(USER_PARAM, mCurrentAccount);
            mav.addObject(ROLES_PARAM, roleService.getRoles());
            mav.addObject(DEFAULT_ROLE_ID_PARAM, DEFAULT_ROLE);
            showMessageBox(mav);
            mIsByPass = false;
            return mav;
        }
    }

    // Add new user
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(ADD_VIEW + SAVE_VIEW)
    public String userAddSave(User user) {
        // Check current account still valid
        if (!isValidAccount()) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            mIsMsgShow = true;
            mIsByPass = true;
            var trueEmail = stringUtil.removeSpCharsBeginAndEnd(user.getEmail()).toLowerCase();
            // check email is already exist
            if (userService.getUser(trueEmail) != null) {
                mMsg = "Tài khoản email này đã được đăng ký!";
                return REDIRECT_PREFIX + USER_VIEW + ADD_VIEW;
            } else {
                user.setEmail(trueEmail);
                user.setImage(DEFAULT_AVATAR);
                userService.saveUser(user);
                mMsg = "Tài khoản đã được tạo thành công!";
                return REDIRECT_PREFIX + USER_VIEW;
            }
        }
    }

    // Get user and define action
    @RequestMapping(FIND_VIEW)
    public String findUser(int id, String action) {
        // check current account still valid
        if (!isValidAccount()) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            mChoosenOne = userService.getUser(id);
            mIsByPass = true;
            // check if user is exist
            if (mChoosenOne == null) {
                mIsMsgShow = true;
                mMsg = "Tài khoản không tồn tại!";
                return REDIRECT_PREFIX + USER_VIEW;
            } else {
                // check action
                switch (action) {
                    case EDIT_VIEW: {
                        return REDIRECT_PREFIX + USER_VIEW + EDIT_VIEW;
                    }
                    case DETAILS_VIEW: {
                        return REDIRECT_PREFIX + USER_VIEW + DETAILS_VIEW;
                    }
                    default: {
                        return REDIRECT_PREFIX + USER_VIEW;
                    }
                }
            }
        }
    }

    // Load edit user input form
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(EDIT_VIEW)
    public ModelAndView userEdit() {
        // check current account still valid
        if (!isValidAccount()) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(USER_EDIT_TEMP);
            mav.addObject(USER_PARAM, mCurrentAccount);
            mav.addObject(PERSON_PARAM, mChoosenOne);
            mav.addObject(ROLES_PARAM, roleService.getRoles());
            mIsByPass = false;
            return mav;
        }
    }

    // Edit user
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = EDIT_VIEW + SAVE_VIEW, method = { GET, PUT })
    public String userEditSave(User user) {
        // check current account still valid
        if (!isValidAccount()) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            mIsMsgShow = true;
            // check user is exist
            if (!isAliveChoosenOne()) {
                mMsg = "Tài khoản không tồn tại!";
            } else {
                user.setId(mChoosenOne.getId());
                user.setImage(mChoosenOne.getImage());
                // check new password
                if (hasText(user.getPassword())) {
                    userService.saveUser(user);
                } else {
                    userService.saveUserWithoutPassword(user);
                }
                mMsg = "Tài khoản đã được cập nhật thành công!";
            }
            mIsByPass = true;
            return REDIRECT_PREFIX + USER_VIEW;
        }
    }

    // Delete user
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = DELETE_VIEW, method = { GET, DELETE })
    public String userDelete(int id) {
        // check current account still valid
        if (!isValidAccount()) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            mChoosenOne = userService.getUser(id);
            mIsMsgShow = true;
            // check if user is exist
            if (mChoosenOne == null) {
                mMsg = "Tài khoản không tồn tại!";
            } else {
                // check user disconnect
                if (mChoosenOne.getProjects().size() > 0) {
                    mMsg = "Tài khoản này đang có dự án, không thể xóa!";
                } else if (mChoosenOne.getTasks().size() > 0) {
                    mMsg = "Tài khoản này đang có công việc, không thể xóa!";
                } else {
                    userService.deleteUser(id);
                    mMsg = "Xóa tài khoản thành công!";
                }
            }
            mIsByPass = true;
            return REDIRECT_PREFIX + USER_VIEW;
        }
    }

    // Load user detail page
    @GetMapping(DETAILS_VIEW)
    public ModelAndView userDetails() {
        // check current account still valid
        if (!isValidAccount()) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(USER_DETAILS_TEMP);
            var tasks = taskService.getTasksByDoer(mChoosenOne.getId());
            var tasksNotStarted = applicationUtil.splitTasksByStatus(tasks, NOT_STARTED);
            var tasksInProgress = applicationUtil.splitTasksByStatus(tasks, IN_PROGRESS);
            var tasksCompleted = applicationUtil.splitTasksByStatus(tasks, COMPLETED);
            var tasksCount = tasks.size();
            mav.addObject(USER_PARAM, mCurrentAccount);
            mav.addObject(PERSON_PARAM, mChoosenOne);
            mav.addObject(TASKS_NOT_STARTED_PARAM, tasksNotStarted);
            mav.addObject(TASKS_IN_PROGRESS_PARAM, tasksInProgress);
            mav.addObject(TASKS_COMPLETED_PARAM, tasksCompleted);
            mav.addObject(NOT_STARTED_PERCENT_PARAM, tasksCount == 0 ? 0 : tasksNotStarted.size() * 100 / tasksCount);
            mav.addObject(IN_PROGRESS_PERCENT_PARAM, tasksCount == 0 ? 0 : tasksInProgress.size() * 100 / tasksCount);
            mav.addObject(COMPLETED_PERCENT_PARAM, tasksCount == 0 ? 0 : tasksCompleted.size() * 100 / tasksCount);
            mIsByPass = false;
            return mav;
        }
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

    // Re-check choosen one
    private boolean isAliveChoosenOne() {
        // check the user has been declared
        if (mChoosenOne == null) {
            return false;
        } else {
            mChoosenOne = userService.getUser(mChoosenOne.getId());
            return mChoosenOne != null;
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
