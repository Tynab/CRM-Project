package com.yan.crm_proj.controller;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;

import com.yan.crm_proj.model.*;
import com.yan.crm_proj.service.*;
import com.yan.crm_proj.util.*;

import static com.yan.crm_proj.constant.AttributeConstant.*;
import static com.yan.crm_proj.constant.TemplateConstant.*;
import static com.yan.crm_proj.constant.ViewConstant.*;

@Controller
@RequestMapping("")
public class ApplicationController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserUtil userUtil;

    // Fields
    private User mCurrentAccount;
    private User mChoosenOne;
    private boolean mIsByPass;

    @GetMapping(LOGIN_VIEW)
    public String login() {
        // check current account still valid
        if (!isValidAccount()) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            return REDIRECT_PREFIX + INDEX_VIEW;
        }
    }

    @GetMapping(INDEX_VIEW)
    public ModelAndView index() {
        var mav = new ModelAndView(INDEX_TEMP);
        return mav;
    }

    @GetMapping(FORBIDDEN_VIEW)
    public String error() {
        return FORBIDDEN_TEMP;
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
}
