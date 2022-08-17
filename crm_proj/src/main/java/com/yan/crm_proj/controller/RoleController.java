package com.yan.crm_proj.controller;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;

import com.yan.crm_proj.model.*;
import com.yan.crm_proj.service.*;
import com.yan.crm_proj.util.*;

import lombok.*;
import static com.yan.crm_proj.constant.AttributeConstant.*;
import static com.yan.crm_proj.constant.TemplateConstant.*;
import static com.yan.crm_proj.constant.ViewConstant.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;
import static com.yan.crm_proj.constant.TemplateConstant.*;
import static com.yan.crm_proj.constant.ViewConstant.*;

@Controller
@RequestMapping(ROLE_VIEW)
public class RoleController {
    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserUtil userUtil;

    // Fields
    private User mCurrentAccount;
    private Role mChoosenOne;
    private boolean mIsByPass;

    // Load role list
    @GetMapping("")
    public ModelAndView role() {
        // check current account still valid
        if (!isValidAccount()) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(ROLE_TEMP);
            mav.addObject(USER_PARAM, mCurrentAccount);
            mav.addObject(ROLES_PARAM, roleService.getRoles());
            // reset bypass
            mIsByPass = false;
            return mav;
        }
    }

    @GetMapping(ADD_VIEW)
    public ModelAndView roleAdd() {
        var mav = new ModelAndView(ROLE_ADD_TEMP);
        return mav;
    }

    @GetMapping(EDIT_VIEW)
    public ModelAndView roleEdit() {
        var mav = new ModelAndView(ROLE_EDIT_TEMP);
        return mav;
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
        // check the role has been declared
        if (mChoosenOne == null) {
            return false;
        } else {
            mChoosenOne = roleService.getRole(mChoosenOne.getName());
            return mChoosenOne != null;
        }
    }
}
