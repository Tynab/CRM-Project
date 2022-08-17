package com.yan.crm_proj.controller;

import org.springframework.beans.factory.annotation.*;
import org.springframework.security.access.prepost.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;

import com.yan.crm_proj.model.*;
import com.yan.crm_proj.service.*;
import com.yan.crm_proj.util.*;

import static com.yan.crm_proj.constant.AppConstant.*;
import static com.yan.crm_proj.constant.AppConstant.TaskStatus.*;
import static com.yan.crm_proj.constant.AttributeConstant.*;
import static com.yan.crm_proj.constant.TemplateConstant.*;
import static com.yan.crm_proj.constant.ViewConstant.*;
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
    private UserUtil userUtil;

    // Fields
    private User mCurrentAccount;
    private User mChoosenOne;
    private String mMessage;
    private boolean mIsByPass;
    private boolean mIsFlag;

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
            mav.addObject("defaultRoleId", roleService.getRole(DEFAULT_ROLE).getId());
            mav.addObject(ROLES_PARAM, roleService.getRoles());
            mav.addObject(USER_PARAM, mCurrentAccount);
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
            mIsFlag = true;
            mIsByPass = true;
            // check email is already exist
            if (userService.getUser(user.getEmail()) != null) {
                mMessage = "Tài khoản email này đã được đăng ký!";
                return REDIRECT_PREFIX + USER_VIEW + ADD_VIEW;
            } else {
                user.setImage(DEFAULT_AVATAR);
                userService.saveUser(user);
                mMessage = "Tài khoản đã được tạo thành công!";
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
                mIsFlag = true;
                mMessage = "Tài khoản không tồn tại!";
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
            mIsFlag = true;
            mIsByPass = true;
            // check user still exist
            if (!isAliveChoosenOne()) {
                mMessage = "Tài khoản không tồn tại!";
                return REDIRECT_PREFIX + USER_VIEW;
            } else {
                user.setId(mChoosenOne.getId());
                user.setImage(mChoosenOne.getImage());
                // check new password
                if (!hasText(user.getPassword())) {
                    userService.saveUserWithoutPassword(user);
                } else {
                    userService.saveUser(user);
                }
                mMessage = "Tài khoản đã được cập nhật thành công!";
                return REDIRECT_PREFIX + USER_VIEW;
            }
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
            mIsFlag = true;
            mIsByPass = true;
            // check if user is exist
            if (mChoosenOne == null) {
                mMessage = "Tài khoản không tồn tại!";
                return REDIRECT_PREFIX + USER_VIEW;
            } else {
                userService.deleteUser(mChoosenOne.getId());
                mMessage = "Tài khoản đã được xóa thành công!";
                return REDIRECT_PREFIX + USER_VIEW;
            }
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
            var choosenOneEmail = mChoosenOne.getEmail();
            var tasksNotStarted = taskService.getTasksByDoerAndStatus(choosenOneEmail, NOT_STARTED);
            var tasksInProgress = taskService.getTasksByDoerAndStatus(choosenOneEmail, IN_PROGRESS);
            var tasksCompleted = taskService.getTasksByDoerAndStatus(choosenOneEmail, COMPLETED);
            var tasksCount = taskService.getTasksByDoer(choosenOneEmail).size();
            mav.addObject(NOT_STARTED_PERCENT_PARAM, tasksCount == 0 ? 0 : tasksNotStarted.size() * 100 / tasksCount);
            mav.addObject(IN_PROGRESS_PERCENT_PARAM, tasksCount == 0 ? 0 : tasksInProgress.size() * 100 / tasksCount);
            mav.addObject(COMPLETED_PERCENT_PARAM, tasksCount == 0 ? 0 : tasksCompleted.size() * 100 / tasksCount);
            mav.addObject("taskNotStarted", tasksNotStarted);
            mav.addObject("taskInProgress", tasksInProgress);
            mav.addObject("taskCompleted", tasksCompleted);
            mav.addObject(USER_PARAM, mCurrentAccount);
            mav.addObject(PERSON_PARAM, mChoosenOne);
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
            mCurrentAccount = userUtil.getAccount();
            return mCurrentAccount != null;
        }
    }

    // Re-check choosen one
    private boolean isAliveChoosenOne() {
        // check the user has been declared
        if (mChoosenOne == null) {
            return false;
        } else {
            mChoosenOne = userService.getUser(mChoosenOne.getEmail());
            return mChoosenOne != null;
        }
    }

    // Show message
    private void showMessageBox(ModelAndView mav) {
        // check flag
        if (mIsFlag) {
            mav.addObject(FLAG_PARAM, true);
            mav.addObject(MESSAGE_PARAM, mMessage);
            mIsFlag = false;
        }
    }
}
