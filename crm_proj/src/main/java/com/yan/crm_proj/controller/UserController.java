package com.yan.crm_proj.controller;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;

import com.yan.crm_proj.model.*;
import com.yan.crm_proj.service.*;
import com.yan.crm_proj.util.*;

import lombok.*;

import static com.yan.crm_proj.constant.ApplicationConstant.*;
import static com.yan.crm_proj.constant.ApplicationConstant.TaskStatus.*;
import static com.yan.crm_proj.constant.AttributeConstant.*;
import static com.yan.crm_proj.constant.TemplateConstant.*;
import static com.yan.crm_proj.constant.ViewConstant.*;
import static org.springframework.util.StringUtils.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
@RequestMapping(USER_VIEW)
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private final UserService userService;

    @Autowired
    private final RoleService roleService;

    @Autowired
    private final TaskService taskService;

    @Autowired
    private final ApplicationUtil applicationUtil;

    @Autowired
    private final UserUtil userUtil;

    // Fields
    private User mCurrentAccount;
    private User mChoosenOne;
    private boolean mIsByPass;

    // Load user list
    @GetMapping("")
    public ModelAndView user() {
        // check current account still valid
        if (!isValidAccount()) {
            return new ModelAndView(REDIRECT + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(USER_TEMP);
            mav.addObject(USERS_PARAM, userService.getUsers());
            mav.addObject(USER_PARAM, mCurrentAccount);
            // reset bypass
            mIsByPass = false;
            return mav;
        }
    }

    // Load new user input form
    @GetMapping(ADD_VIEW)
    public ModelAndView userAdd() {
        // Check current account still valid
        if (!isValidAccount()) {
            return new ModelAndView(REDIRECT + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(USER_ADD_TEMP);
            mav.addObject(USER_PARAM, mCurrentAccount);
            // reset bypass
            mIsByPass = false;
            return mav;
        }
    }

    // Add new user
    @PostMapping(ADD_VIEW + SAVE_VIEW)
    public String userAddSave(User user) {
        // Check current account still valid
        if (!isValidAccount()) {
            return REDIRECT + LOGOUT_VIEW;
        } else {
            // redirect instantly
            mIsByPass = true;
            // check email is already exist
            if (userService.getUser(user.getEmail()) != null) {
                return REDIRECT + USER_VIEW + ADD_VIEW
                        + applicationUtil.sendMsgUrl("Tài khoản email này đã được đăng ký!");
            } else {
                user.setImage(DEFAULT_AVATAR);
                user.setRoleId(roleService.getRole(DEFAULT_ROLE).getId());
                userService.saveUser(user);
                return REDIRECT + USER_VIEW + applicationUtil.urlMsgSuccess();
            }
        }
    }

    // Get user by email and define action
    @RequestMapping(FIND_VIEW)
    public String findUserByEmail(String email, String action) {
        // check current account still valid
        if (!isValidAccount()) {
            return REDIRECT + LOGOUT_VIEW;
        } else {
            mChoosenOne = userService.getUser(email);
            // redirect instantly
            mIsByPass = true;
            // check if user is exist
            if (mChoosenOne == null) {
                return REDIRECT + USER_VIEW + applicationUtil.urlMsgError();
            } else {
                // check action
                switch (action) {
                    case EDIT_VIEW: {
                        return REDIRECT + USER_VIEW + EDIT_VIEW;
                    }
                    case DETAILS_VIEW: {
                        return REDIRECT + USER_VIEW + DETAILS_VIEW;
                    }
                    default: {
                        return REDIRECT + USER_VIEW;
                    }
                }
            }
        }
    }

    // Load edit user input form
    @GetMapping(EDIT_VIEW)
    public ModelAndView userEdit() {
        // check current account still valid
        if (!isValidAccount()) {
            return new ModelAndView(REDIRECT + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(USER_EDIT_TEMP);
            mav.addObject(USER_PARAM, mCurrentAccount);
            mav.addObject(PERSON_PARAM, mChoosenOne);
            // reset bypass
            mIsByPass = false;
            return mav;
        }
    }

    // Edit user
    @RequestMapping(value = EDIT_VIEW + SAVE_VIEW, method = { GET, PUT })
    public String userEditSave(User user) {
        // check current account still valid
        if (!isValidAccount()) {
            return REDIRECT + LOGOUT_VIEW;
        } else {
            // redirect instantly
            mIsByPass = true;
            // check user still exist
            if (!isAliveChoosenOne()) {
                return REDIRECT + USER_VIEW + applicationUtil.urlMsgError();
            } else {
                user.setId(mChoosenOne.getId());
                user.setImage(mChoosenOne.getImage());
                user.setRoleId(mChoosenOne.getRoleId());
                // check new password
                if (!hasText(user.getPassword())) {
                    userService.saveUserWithoutPassword(user);
                } else {
                    userService.saveUser(user);
                }
                return REDIRECT + USER_VIEW + applicationUtil.urlMsgSuccess();
            }
        }
    }

    // Delete user
    @RequestMapping(value = DELETE_VIEW, method = { GET, DELETE })
    public String userDelete(String email) {
        // check current account still valid
        if (!isValidAccount()) {
            return REDIRECT + LOGOUT_VIEW;
        } else {
            mChoosenOne = userService.getUser(email);
            // check if user is exist
            if (mChoosenOne == null) {
                return REDIRECT + USER_VIEW + applicationUtil.urlMsgError();
            } else {
                userService.deleteUser(mChoosenOne.getId());
                // redirect instantly
                mIsByPass = true;
                return REDIRECT + USER_VIEW + applicationUtil.urlMsgSuccess();
            }
        }
    }

    // Load user detail page
    @GetMapping(DETAILS_VIEW)
    public ModelAndView userDetails() {
        // check current account still valid
        if (!isValidAccount()) {
            return new ModelAndView(REDIRECT + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(USER_DETAILS_TEMP);
            var choosenOneEmail = mChoosenOne.getEmail();
            var taskNotStarted = taskService.getTasksByDoerAndTaskStatus(choosenOneEmail, NOT_STARTED);
            var taskInProgress = taskService.getTasksByDoerAndTaskStatus(choosenOneEmail, IN_PROGRESS);
            var taskCompleted = taskService.getTasksByDoerAndTaskStatus(choosenOneEmail, COMPLETED);
            var taskTotal = taskService.getTasksByDoer(choosenOneEmail).size();
            mav.addObject(T_TS_IP_PARAM, taskTotal == 0 ? 0 : taskNotStarted.size() * 100 / taskTotal);
            mav.addObject(T_TS_PP_PARAM, taskTotal == 0 ? 0 : taskInProgress.size() * 100 / taskTotal);
            mav.addObject(T_TS_OP_PARAM, taskTotal == 0 ? 0 : taskCompleted.size() * 100 / taskTotal);
            mav.addObject("taskNotStarted", taskNotStarted);
            mav.addObject("taskInProgress", taskInProgress);
            mav.addObject("taskCompleted", taskCompleted);
            mav.addObject(USER_PARAM, mCurrentAccount);
            mav.addObject(PERSON_PARAM, mChoosenOne);
            // reset bypass
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
            // check account login
            if (mCurrentAccount == null) {
                return false;
            } else {
                mCurrentAccount = userService.getUser(mCurrentAccount.getEmail());
                // check database has user
                if (mCurrentAccount == null) {
                    return false;
                } else {
                    return true;
                }
            }
        }
    }

    // Re-check choosen one
    private boolean isAliveChoosenOne() {
        // check the user has been declared
        if (mChoosenOne == null) {
            return false;
        } else {
            mChoosenOne = userService.getUser(mChoosenOne.getEmail());
            // check database has user
            if (mChoosenOne == null) {
                return false;
            } else {
                return true;
            }
        }
    }
}
