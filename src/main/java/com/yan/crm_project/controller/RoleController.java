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
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
@RequestMapping(ROLE_VIEW)
public class RoleController {
    @Autowired
    private RoleService roleService;

    @Autowired
    private ApplicationUtil applicationUtil;

    @Autowired
    private AuthenticationUtil authenticationUtil;

    // Fields
    private String mMsg;
    private boolean mIsMsgShow;

    // Load role list
    @GetMapping("")
    public ModelAndView role() {
        var account = authenticationUtil.getAccount();
        // check current account still valid
        if (account == null) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(ROLE_TEMP);
            mav.addObject(ACCOUNT_PARAM, account);
            mav.addObject(ROLES_PARAM, roleService.getRoles());
            mIsMsgShow = applicationUtil.showMessageBox(mav, mIsMsgShow, mMsg);
            return mav;
        }
    }

    // Load new role input form
    @GetMapping(ADD_VIEW)
    public ModelAndView roleAdd() {
        var account = authenticationUtil.getAccount();
        // check current account still valid
        if (account == null) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(ROLE_ADD_TEMP);
            mav.addObject(ACCOUNT_PARAM, account);
            mIsMsgShow = applicationUtil.showMessageBox(mav, mIsMsgShow, mMsg);
            return mav;
        }
    }

    // Add new role
    @PostMapping(ADD_VIEW + SAVE_VIEW)
    public String roleAddSave(Role role) {
        // Check current account still valid
        if (authenticationUtil.getAccount() == null) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            mIsMsgShow = true;
            // check name is already exist
            if (roleService.getRole(role.getName()) != null) {
                mMsg = "Tên quyền này đã tồn tại";
                return REDIRECT_PREFIX + ROLE_VIEW + ADD_VIEW;
            } else {
                roleService.saveRole(role);
                mMsg = "Thêm quyền thành công";
                return REDIRECT_PREFIX + ROLE_VIEW;
            }
        }
    }

    // Load edit role input form
    @GetMapping(EDIT_VIEW)
    public ModelAndView roleEdit(int id) {
        var account = authenticationUtil.getAccount();
        // check current account still valid
        if (account == null) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var role = roleService.getRole(id);
            // check if role is exist
            if (role == null) {
                mIsMsgShow = true;
                mMsg = "Quyền không tồn tại";
                return new ModelAndView(REDIRECT_PREFIX + ROLE_VIEW);
            } else {
                var mav = new ModelAndView(ROLE_EDIT_TEMP);
                mav.addObject(ACCOUNT_PARAM, account);
                mav.addObject(ROLE_PARAM, role);
                return mav;
            }
        }
    }

    // Edit role
    @RequestMapping(value = EDIT_VIEW + SAVE_VIEW, method = { GET, PUT })
    public String roleEditSave(Role role) {
        // check current account still valid
        if (authenticationUtil.getAccount() == null) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            roleService.saveRole(role);
            mIsMsgShow = true;
            mMsg = "Cập nhật quyền thành công";
            return REDIRECT_PREFIX + ROLE_VIEW;
        }
    }

    // Delete role
    @RequestMapping(value = DELETE_VIEW, method = { GET, DELETE })
    public String roleDelete(int id) {
        // check current account still valid
        if (authenticationUtil.getAccount() == null) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            var role = roleService.getRole(id);
            mIsMsgShow = true;
            // check role is exist
            if (role == null) {
                mMsg = "Quyền không tồn tại";
            } else {
                // check role disconnect
                if (role.getUsers().size() > 0) {
                    mMsg = "Quyền này đang được sử dụng, không thể xóa";
                } else {
                    roleService.deleteRole(id);
                    mMsg = "Xóa quyền thành công";
                }
            }
            return REDIRECT_PREFIX + ROLE_VIEW;
        }
    }
}
