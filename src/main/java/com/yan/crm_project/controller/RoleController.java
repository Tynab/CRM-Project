package com.yan.crm_project.controller;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;

import com.yan.crm_project.model.*;
import com.yan.crm_project.service.*;
import com.yan.crm_project.util.*;

import static com.yan.crm_project.constant.AttributeConstant.*;
import static com.yan.crm_project.constant.TemplateConstant.*;
import static com.yan.crm_project.constant.ViewConstant.*;
import static org.springframework.util.StringUtils.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
@RequestMapping(ROLE_VIEW)
public class RoleController {
    @Autowired
    private RoleService roleService;

    @Autowired
    private AuthenticationUtil authenticationUtil;

    @Autowired
    private StringUtil stringUtil;

    // Fields
    private User mCurrentAccount;
    private Role mChoosenOne;
    private String mMsg;
    private boolean mIsByPass;
    private boolean mIsMsgShow;

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
            showMessageBox(mav);
            mIsByPass = false;
            return mav;
        }
    }

    // Load new role input form
    @GetMapping(ADD_VIEW)
    public ModelAndView roleAdd() {
        // Check current account still valid
        if (!isValidAccount()) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(ROLE_ADD_TEMP);
            mav.addObject(USER_PARAM, mCurrentAccount);
            showMessageBox(mav);
            mIsByPass = false;
            return mav;
        }
    }

    // Add new role
    @PostMapping(ADD_VIEW + SAVE_VIEW)
    public String roleAddSave(Role role) {
        // Check current account still valid
        if (!isValidAccount()) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            mIsMsgShow = true;
            mIsByPass = true;
            var trueName = capitalize(
                    stringUtil.replaceMultiBySingleWhitespace(stringUtil.removeSpCharsBeginAndEnd(role.getName())));
            // check name is already exist
            if (roleService.getRole(trueName) != null) {
                mMsg = "Tên quyền này đã tồn tại";
                return REDIRECT_PREFIX + ROLE_VIEW + ADD_VIEW;
            } else {
                role.setName(trueName);
                roleService.saveRole(role);
                mMsg = "Thêm quyền thành công";
                return REDIRECT_PREFIX + ROLE_VIEW;
            }
        }
    }

    // Get role
    @RequestMapping(FIND_VIEW)
    public String findRole(int id) {
        // check current account still valid
        if (!isValidAccount()) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            mChoosenOne = roleService.getRole(id);
            mIsByPass = true;
            // check if role is exist
            if (mChoosenOne == null) {
                mIsMsgShow = true;
                mMsg = "Quyền không tồn tại";
                return REDIRECT_PREFIX + ROLE_VIEW;
            } else {
                return REDIRECT_PREFIX + ROLE_VIEW + EDIT_VIEW;
            }
        }
    }

    // Load edit role input form
    @GetMapping(EDIT_VIEW)
    public ModelAndView roleEdit() {
        // check current account still valid
        if (!isValidAccount()) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(ROLE_EDIT_TEMP);
            mav.addObject(USER_PARAM, mCurrentAccount);
            mav.addObject(ROLE_PARAM, mChoosenOne);
            mIsByPass = false;
            return mav;
        }
    }

    // Edit role
    @RequestMapping(value = EDIT_VIEW + SAVE_VIEW, method = { GET, PUT })
    public String roleEditSave(Role role) {
        // check current account still valid
        if (!isValidAccount()) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            mIsMsgShow = true;
            // check project is exist
            if (!isAliveChoosenOne()) {
                mMsg = "Quyền không tồn tại";
            } else {
                role.setId(mChoosenOne.getId());
                roleService.saveRole(role);
                mMsg = "Cập nhật quyền thành công";
            }
            mIsByPass = true;
            return REDIRECT_PREFIX + ROLE_VIEW;
        }
    }

    // Delete role
    @RequestMapping(value = DELETE_VIEW, method = { GET, DELETE })
    public String roleDelete(int id) {
        // check current account still valid
        if (!isValidAccount()) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            mChoosenOne = roleService.getRole(id);
            mIsMsgShow = true;
            // check role is exist
            if (mChoosenOne == null) {
                mMsg = "Quyền không tồn tại";
            } else {
                // check role disconnect
                if (mChoosenOne.getUsers().size() > 0) {
                    mMsg = "Quyền này đang được sử dụng, không thể xóa";
                } else {
                    roleService.deleteRole(id);
                    mMsg = "Xóa quyền thành công";
                }
            }
            mIsByPass = true;
            return REDIRECT_PREFIX + ROLE_VIEW;
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
        // check the role has been declared
        if (mChoosenOne == null) {
            return false;
        } else {
            mChoosenOne = roleService.getRole(mChoosenOne.getName());
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
