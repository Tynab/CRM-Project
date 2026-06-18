package com.yan.crm_project.controller;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.mvc.support.*;

import com.yan.crm_project.model.*;
import com.yan.crm_project.service.*;
import com.yan.crm_project.util.*;

import static com.yan.crm_project.constant.AttributeConstant.*;
import static com.yan.crm_project.constant.TemplateConstant.*;
import static com.yan.crm_project.constant.ViewConstant.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
@RequestMapping(ROLE_VIEW)
/**
 * Tom tat: Quan ly danh muc vai tro, chi duoc truy cap qua rule security cua
 * nhom ADMIN.
 */
public class RoleController {
    @Autowired
    private RoleService roleService;

    @Autowired
    private ApplicationUtil applicationUtil;

    @Autowired
    private AuthenticationUtil authenticationUtil;

    /**
     * Nap danh sach vai tro.
     */
    @GetMapping("")
    public ModelAndView role() {
        var account = authenticationUtil.getAccount();
        if (account == null) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(ROLE_TEMP);
            mav.addObject(ACCOUNT_PARAM, account);
            mav.addObject(ROLES_PARAM, roleService.getRoles());
            return mav;
        }
    }

    /**
     * Nap form tao vai tro moi.
     */
    @GetMapping(ADD_VIEW)
    public ModelAndView roleAdd() {
        var account = authenticationUtil.getAccount();
        if (account == null) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(ROLE_ADD_TEMP);
            mav.addObject(ACCOUNT_PARAM, account);
            return mav;
        }
    }

    /**
     * Luu vai tro moi sau khi kiem tra trung ten.
     */
    @PostMapping(ADD_VIEW + SAVE_VIEW)
    public String roleAddSave(Role role, RedirectAttributes redirectAttributes) {
        if (authenticationUtil.getAccount() == null) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            if (roleService.getRole(role.getName()) != null) {
                applicationUtil.addFlashMessage(redirectAttributes, "Tên quyền này đã tồn tại");
                return REDIRECT_PREFIX + ROLE_VIEW + ADD_VIEW;
            } else {
                roleService.saveRole(role);
                applicationUtil.addFlashMessage(redirectAttributes, "Thêm quyền thành công");
                return REDIRECT_PREFIX + ROLE_VIEW;
            }
        }
    }

    /**
     * Nap form cap nhat vai tro.
     */
    @GetMapping(EDIT_VIEW)
    public ModelAndView roleEdit(int id, RedirectAttributes redirectAttributes) {
        var account = authenticationUtil.getAccount();
        if (account == null) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var role = roleService.getRole(id);
            if (role == null) {
                applicationUtil.addFlashMessage(redirectAttributes, "Quyền không tồn tại");
                return new ModelAndView(REDIRECT_PREFIX + ROLE_VIEW);
            } else {
                var mav = new ModelAndView(ROLE_EDIT_TEMP);
                mav.addObject(ACCOUNT_PARAM, account);
                mav.addObject(ROLE_PARAM, role);
                return mav;
            }
        }
    }

    /**
     * Cap nhat mo ta vai tro.
     */
    @PostMapping(EDIT_VIEW + SAVE_VIEW)
    public String roleEditSave(Role role, RedirectAttributes redirectAttributes) {
        if (authenticationUtil.getAccount() == null) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            if (roleService.getRole(role.getId()) == null) {
                applicationUtil.addFlashMessage(redirectAttributes, "Quyền không tồn tại");
                return REDIRECT_PREFIX + ROLE_VIEW;
            }

            roleService.saveRole(role);
            applicationUtil.addFlashMessage(redirectAttributes, "Cập nhật quyền thành công");
            return REDIRECT_PREFIX + ROLE_VIEW;
        }
    }

    /**
     * Xoa vai tro neu khong con user nao dang su dung.
     */
    @RequestMapping(value = DELETE_VIEW, method = { GET, DELETE })
    public String roleDelete(int id, RedirectAttributes redirectAttributes) {
        if (authenticationUtil.getAccount() == null) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            var role = roleService.getRole(id);
            if (role == null) {
                applicationUtil.addFlashMessage(redirectAttributes, "Quyền không tồn tại");
            } else {
                if (role.getUsers().size() > 0) {
                    applicationUtil.addFlashMessage(redirectAttributes, "Quyền này đang được sử dụng, không thể xóa");
                } else {
                    roleService.deleteRole(id);
                    applicationUtil.addFlashMessage(redirectAttributes, "Xóa quyền thành công");
                }
            }
            return REDIRECT_PREFIX + ROLE_VIEW;
        }
    }
}
