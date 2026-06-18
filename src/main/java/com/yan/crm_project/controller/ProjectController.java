package com.yan.crm_project.controller;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.mvc.support.*;

import com.yan.crm_project.model.*;
import com.yan.crm_project.service.*;
import com.yan.crm_project.util.*;

import static com.yan.crm_project.constant.ApplicationConstant.Role.*;
import static com.yan.crm_project.constant.AttributeConstant.*;
import static com.yan.crm_project.constant.TemplateConstant.*;
import static com.yan.crm_project.constant.ViewConstant.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
@RequestMapping(PROJECT_VIEW)
/**
 * Tom tat: Quan ly du an va ap dung rule admin/leader theo nguoi tao du an.
 */
public class ProjectController {
    @Autowired
    private RoleService roleService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ApplicationUtil applicationUtil;

    @Autowired
    private AuthenticationUtil authenticationUtil;

    /**
     * Nap danh sach du an.
     */
    @GetMapping("")
    public ModelAndView project() {
        var account = authenticationUtil.getAccount();
        if (account == null) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(PROJECT_TEMP);
            mav.addObject(ACCOUNT_PARAM, account);
            mav.addObject(PROJECTS_PARAM, projectService.getProjects());
            return mav;
        }
    }

    /**
     * Nap form tao du an.
     */
    @GetMapping(ADD_VIEW)
    public ModelAndView projectAdd() {
        var account = authenticationUtil.getAccount();
        if (account == null) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(PROJECT_ADD_TEMP);
            mav.addObject(ACCOUNT_PARAM, account);
            return mav;
        }
    }

    /**
     * Tao du an va gan nguoi tao la account hien tai.
     */
    @PostMapping(ADD_VIEW + SAVE_VIEW)
    public String projectAddSave(Project project, RedirectAttributes redirectAttributes) {
        var account = authenticationUtil.getAccount();
        if (account == null) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            project.setOriginatorId(account.getId());
            projectService.saveProject(project);
            applicationUtil.addFlashMessage(redirectAttributes, "Thêm dự án thành công!");
            return REDIRECT_PREFIX + PROJECT_VIEW;
        }
    }

    /**
     * Nap form cap nhat du an neu account co quyen voi originator hien tai.
     */
    @GetMapping(EDIT_VIEW)
    public ModelAndView projectEdit(int id, RedirectAttributes redirectAttributes) {
        var account = authenticationUtil.getAccount();
        if (account == null) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var project = projectService.getProject(id);
            if (project == null) {
                applicationUtil.addFlashMessage(redirectAttributes, "Dự án không tồn tại!");
                return new ModelAndView(REDIRECT_PREFIX + PROJECT_VIEW);
            } else {
                if (!isPermissionLeader(account, project.getOriginatorId())) {
                    return new ModelAndView(FORWARD_PREFIX + FORBIDDEN_VIEW);
                } else {
                    var mav = new ModelAndView(PROJECT_EDIT_TEMP);
                    mav.addObject(ACCOUNT_PARAM, account);
                    mav.addObject(PROJECT_PARAM, project);
                    return mav;
                }
            }
        }
    }

    /**
     * Luu cap nhat du an, khong tin originatorId gui len tu form.
     */
    @PostMapping(EDIT_VIEW + SAVE_VIEW)
    public String projectEditSave(Project project, RedirectAttributes redirectAttributes) {
        var account = authenticationUtil.getAccount();
        if (account == null) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            var existingProject = projectService.getProject(project.getId());
            if (existingProject == null) {
                applicationUtil.addFlashMessage(redirectAttributes, "Dự án không tồn tại!");
                return REDIRECT_PREFIX + PROJECT_VIEW;
            }

            if (!isPermissionLeader(account, existingProject.getOriginatorId())) {
                return FORWARD_PREFIX + FORBIDDEN_VIEW;
            } else {
                project.setOriginatorId(existingProject.getOriginatorId());
                projectService.saveProject(project);
                applicationUtil.addFlashMessage(redirectAttributes, "Cập nhật dự án thành công!");
                return REDIRECT_PREFIX + PROJECT_VIEW;
            }
        }
    }

    /**
     * Xoa du an neu khong co cong viec va account co quyen.
     */
    @RequestMapping(value = DELETE_VIEW, method = { GET, DELETE })
    public String projectDelete(int id, RedirectAttributes redirectAttributes) {
        var account = authenticationUtil.getAccount();
        if (account == null) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            var project = projectService.getProject(id);
            if (project == null) {
                applicationUtil.addFlashMessage(redirectAttributes, "Dự án không tồn tại!");
                return REDIRECT_PREFIX + PROJECT_VIEW;
            } else {
                if (project.getTasks().size() > 0) {
                    applicationUtil.addFlashMessage(redirectAttributes, "Dự án đang có công việc, không thể xóa!");
                    return REDIRECT_PREFIX + PROJECT_VIEW;
                } else {
                    if (!isPermissionLeader(account, project.getOriginatorId())) {
                        return FORWARD_PREFIX + FORBIDDEN_VIEW;
                    } else {
                        projectService.deleteProject(id);
                        applicationUtil.addFlashMessage(redirectAttributes, "Xóa dự án thành công!");
                        return REDIRECT_PREFIX + PROJECT_VIEW;
                    }
                }
            }
        }
    }

    /**
     * Lay role cua account hien tai de so quyen.
     */
    private String getCurrentAccountRole(int roleId) {
        var role = roleService.getRole(roleId);
        return role == null ? "" : role.getName().toUpperCase();
    }

    /**
     * Admin co toan quyen; leader chi thao tac du an do chinh minh tao.
     */
    private boolean isPermissionLeader(User user, int originatorId) {
        var currentAccountRole = getCurrentAccountRole(user.getRoleId());
        return currentAccountRole.equals(ADMIN) || currentAccountRole.equals(LEADER) && originatorId == user.getId();
    }
}
