package com.yan.crm_project.controller;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.mvc.support.*;

import com.yan.crm_project.model.*;
import com.yan.crm_project.service.*;
import com.yan.crm_project.util.*;

import static com.yan.crm_project.constant.ApplicationConstant.*;
import static com.yan.crm_project.constant.ApplicationConstant.Role.*;
import static com.yan.crm_project.constant.AttributeConstant.*;
import static com.yan.crm_project.constant.TemplateConstant.*;
import static com.yan.crm_project.constant.ViewConstant.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
@RequestMapping(TASK_VIEW)
/**
 * Tom tat: Quan ly cong viec trong du an, bao gom tao/sua/xoa va kiem tra quyen
 * theo owner cua du an.
 */
public class TaskController {
    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ApplicationUtil applicationUtil;

    @Autowired
    private AuthenticationUtil authenticationUtil;

    /**
     * Nap danh sach cong viec.
     */
    @GetMapping("")
    public ModelAndView task() {
        var account = authenticationUtil.getAccount();
        if (account == null) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(TASK_TEMP);
            mav.addObject(ACCOUNT_PARAM, account);
            mav.addObject(TASKS_PARAM, taskService.getTasks());
            return mav;
        }
    }

    /**
     * Nap form tao cong viec; admin thay tat ca du an, leader chi thay du an cua
     * minh.
     */
    @GetMapping(ADD_VIEW)
    public ModelAndView taskAdd() {
        var account = authenticationUtil.getAccount();
        if (account == null) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(TASK_ADD_TEMP);
            mav.addObject(USERS_PARAM, userService.getUsers());
            mav.addObject(ACCOUNT_PARAM, account);
            mav.addObject(PROJECTS_PARAM, getManageableProjects(account));
            return mav;
        }
    }

    /**
     * Tao cong viec voi trang thai mac dinh la chua bat dau.
     */
    @PostMapping(ADD_VIEW + SAVE_VIEW)
    public String taskAddSave(Task task, RedirectAttributes redirectAttributes) {
        var account = authenticationUtil.getAccount();
        if (account == null) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            var project = projectService.getProject(task.getProjectId());
            if (project == null) {
                applicationUtil.addFlashMessage(redirectAttributes, "Dự án không tồn tại!");
                return REDIRECT_PREFIX + TASK_VIEW + ADD_VIEW;
            }

            if (!isPermissionLeader(account, project.getOriginatorId())) {
                return FORWARD_PREFIX + FORBIDDEN_VIEW;
            }

            task.setStatusId(DEFAULT_STATUS);
            taskService.saveTask(task);
            applicationUtil.addFlashMessage(redirectAttributes, "Thêm công việc thành công!");
            return REDIRECT_PREFIX + TASK_VIEW;
        }
    }

    /**
     * Nap form cap nhat cong viec neu account quan ly du an chua task.
     */
    @GetMapping(EDIT_VIEW)
    public ModelAndView taskEdit(int id, RedirectAttributes redirectAttributes) {
        var account = authenticationUtil.getAccount();
        if (account == null) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var task = taskService.getTask(id);
            if (task == null) {
                applicationUtil.addFlashMessage(redirectAttributes, "Không tìm thấy công việc!");
                return new ModelAndView(REDIRECT_PREFIX + TASK_VIEW);
            } else {
                if (!isPermissionLeader(account, task.getProject().getOriginatorId())) {
                    return new ModelAndView(FORWARD_PREFIX + FORBIDDEN_VIEW);
                } else {
                    var mav = new ModelAndView(TASK_EDIT_TEMP);
                    mav.addObject(USERS_PARAM, userService.getUsers());
                    mav.addObject(ACCOUNT_PARAM, account);
                    mav.addObject(PROJECTS_PARAM, getManageableProjects(account));
                    mav.addObject(TASK_PARAM, task);
                    return mav;
                }
            }
        }
    }

    /**
     * Luu cap nhat cong viec, doc lai task/project tu DB de tranh hidden input bi
     * sua.
     */
    @PostMapping(EDIT_VIEW + SAVE_VIEW)
    public String taskEditSave(Task task, RedirectAttributes redirectAttributes) {
        var account = authenticationUtil.getAccount();
        if (account == null) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            var existingTask = taskService.getTask(task.getId());
            if (existingTask == null) {
                applicationUtil.addFlashMessage(redirectAttributes, "Công việc không tồn tại!");
                return REDIRECT_PREFIX + TASK_VIEW;
            }

            var selectedProject = projectService.getProject(task.getProjectId());
            if (selectedProject == null) {
                applicationUtil.addFlashMessage(redirectAttributes, "Dự án không tồn tại!");
                return REDIRECT_PREFIX + TASK_VIEW;
            }

            if (!isPermissionLeader(account, existingTask.getProject().getOriginatorId())
                    || !isPermissionLeader(account, selectedProject.getOriginatorId())) {
                return FORWARD_PREFIX + FORBIDDEN_VIEW;
            } else {
                task.setStatusId(existingTask.getStatusId());
                taskService.saveTask(task);
                applicationUtil.addFlashMessage(redirectAttributes, "Cập nhật công việc thành công!");
                return REDIRECT_PREFIX + TASK_VIEW;
            }
        }
    }

    /**
     * Xoa cong viec neu account quan ly du an chua task.
     */
    @RequestMapping(value = DELETE_VIEW, method = { GET, DELETE })
    public String taskDelete(int id, RedirectAttributes redirectAttributes) {
        var account = authenticationUtil.getAccount();
        if (account == null) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            var task = taskService.getTask(id);
            if (task == null) {
                applicationUtil.addFlashMessage(redirectAttributes, "Công việc không tồn tại!");
                return REDIRECT_PREFIX + TASK_VIEW;
            } else {
                if (!isPermissionLeader(account, task.getProject().getOriginatorId())) {
                    return FORWARD_PREFIX + FORBIDDEN_VIEW;
                } else {
                    taskService.deleteTask(id);
                    applicationUtil.addFlashMessage(redirectAttributes, "Xóa công việc thành công!");
                    return REDIRECT_PREFIX + TASK_VIEW;
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
     * Chon danh sach du an ma account co quyen tao/sua task.
     */
    private Iterable<Project> getManageableProjects(User account) {
        return getCurrentAccountRole(account.getRoleId()).equals(ADMIN) ? projectService.getProjects()
                : account.getProjects();
    }

    /**
     * Admin co toan quyen; leader chi thao tac task thuoc du an do minh tao.
     */
    private boolean isPermissionLeader(User user, int originatorId) {
        var currentAccountRole = getCurrentAccountRole(user.getRoleId());
        return currentAccountRole.equals(ADMIN) || currentAccountRole.equals(LEADER) && originatorId == user.getId();
    }
}
