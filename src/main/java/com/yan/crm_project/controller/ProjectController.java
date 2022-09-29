package com.yan.crm_project.controller;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;

import com.yan.crm_project.model.*;
import com.yan.crm_project.service.*;
import com.yan.crm_project.util.*;

import static com.yan.crm_project.common.Bean.*;
import static com.yan.crm_project.constant.ApplicationConstant.Role.*;
import static com.yan.crm_project.constant.AttributeConstant.*;
import static com.yan.crm_project.constant.TemplateConstant.*;
import static com.yan.crm_project.constant.ViewConstant.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
@RequestMapping(PROJECT_VIEW)
public class ProjectController {
    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ApplicationUtil applicationUtil;

    @Autowired
    private AuthenticationUtil authenticationUtil;

    // Load project list
    @GetMapping("")
    public ModelAndView project() {
        var account = authenticationUtil.getAccount();
        // check current account still valid
        if (account == null) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(PROJECT_TEMP);
            mav.addObject(ACCOUNT_PARAM, account);
            mav.addObject(PROJECTS_PARAM, projectService.getProjects());
            _isMsgShow = applicationUtil.showMessageBox(mav);
            return mav;
        }
    }

    // Load new project input form
    @GetMapping(ADD_VIEW)
    public ModelAndView projectAdd() {
        var account = authenticationUtil.getAccount();
        // check current account still valid
        if (account == null) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(PROJECT_ADD_TEMP);
            mav.addObject(ACCOUNT_PARAM, account);
            return mav;
        }
    }

    // Add new project
    @PostMapping(ADD_VIEW + SAVE_VIEW)
    public String projectAddSave(Project project) {
        var account = authenticationUtil.getAccount();
        // check current account still valid
        if (account == null) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            project.setOriginatorId(account.getId());
            projectService.saveProject(project);
            _isMsgShow = true;
            _msg = "Thêm dự án thành công!";
            return REDIRECT_PREFIX + PROJECT_VIEW;
        }
    }

    // Load edit project input form
    @GetMapping(EDIT_VIEW)
    public ModelAndView projectEdit(int id) {
        var account = authenticationUtil.getAccount();
        // check current account still valid
        if (account == null) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var project = projectService.getProject(id);
            // check if project is exist
            if (project == null) {
                _isMsgShow = true;
                _msg = "Dự án không tồn tại!";
                return new ModelAndView(REDIRECT_PREFIX + PROJECT_VIEW);
            } else {
                // check permission
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

    // Edit project
    @RequestMapping(value = EDIT_VIEW + SAVE_VIEW, method = { GET, PUT })
    public String projectEditSave(Project project) {
        var account = authenticationUtil.getAccount();
        // check current account still valid
        if (account == null) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            // check permission
            if (!isPermissionLeader(account, project.getOriginatorId())) {
                return FORWARD_PREFIX + FORBIDDEN_VIEW;
            } else {
                projectService.saveProject(project);
                _isMsgShow = true;
                _msg = "Cập nhật dự án thành công!";
                return REDIRECT_PREFIX + PROJECT_VIEW;
            }
        }
    }

    // Delete project
    @RequestMapping(value = DELETE_VIEW, method = { GET, DELETE })
    public String projectDelete(int id) {
        var account = authenticationUtil.getAccount();
        // check current account still valid
        if (account == null) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            var project = projectService.getProject(id);
            // check if project is exist
            if (project == null) {
                _isMsgShow = true;
                _msg = "Dự án không tồn tại!";
                return REDIRECT_PREFIX + PROJECT_VIEW;
            } else {
                // check project disconnect
                if (project.getTasks().size() > 0) {
                    _isMsgShow = true;
                    _msg = "Dự án đang có công việc, không thể xóa!";
                    return REDIRECT_PREFIX + PROJECT_VIEW;
                } else {
                    // check permission
                    if (!isPermissionLeader(account, project.getOriginatorId())) {
                        return FORWARD_PREFIX + FORBIDDEN_VIEW;
                    } else {
                        projectService.deleteProject(id);
                        _isMsgShow = true;
                        _msg = "Xóa dự án thành công!";
                        return REDIRECT_PREFIX + PROJECT_VIEW;
                    }
                }
            }
        }
    }

    // Get role of current account
    private String getCurrentAccountRole(int roleId) {
        return roleService.getRole(roleId).getName().toUpperCase();
    }

    // Check permission leader for project
    private boolean isPermissionLeader(User user, int originatorId) {
        var currentAccountRole = getCurrentAccountRole(user.getRoleId());
        return currentAccountRole.equals(LEADER) && userService.getUser(originatorId).getId() == user.getId()
                || currentAccountRole.equals(ADMIN);
    }
}
