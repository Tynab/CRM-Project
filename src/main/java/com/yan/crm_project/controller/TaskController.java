package com.yan.crm_project.controller;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;

import com.yan.crm_project.model.*;
import com.yan.crm_project.service.*;
import com.yan.crm_project.util.*;

import static com.yan.crm_project.common.Bean.*;
import static com.yan.crm_project.constant.ApplicationConstant.*;
import static com.yan.crm_project.constant.ApplicationConstant.Role.*;
import static com.yan.crm_project.constant.AttributeConstant.*;
import static com.yan.crm_project.constant.TemplateConstant.*;
import static com.yan.crm_project.constant.ViewConstant.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
@RequestMapping(TASK_VIEW)
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

    // Load task list
    @GetMapping("")
    public ModelAndView task() {
        var account = authenticationUtil.getAccount();
        // check current account still valid
        if (account == null) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(TASK_TEMP);
            mav.addObject(ACCOUNT_PARAM, account);
            mav.addObject(TASKS_PARAM, taskService.getTasks());
            _isMsgShow = applicationUtil.showMessageBox(mav);
            return mav;
        }
    }

    // Load new task input form
    @GetMapping(ADD_VIEW)
    public ModelAndView taskAdd() {
        var account = authenticationUtil.getAccount();
        // check current account still valid
        if (account == null) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(TASK_ADD_TEMP);
            mav.addObject(USERS_PARAM, userService.getUsers());
            mav.addObject(ACCOUNT_PARAM, account);
            // check admin
            if (getCurrentAccountRole(account.getRoleId()).equals(ADMIN)) {
                mav.addObject(PROJECTS_PARAM, projectService.getProjects());
            } else {
                mav.addObject(PROJECTS_PARAM, account.getProjects());
            }
            return mav;
        }
    }

    // Add new task
    @PostMapping(ADD_VIEW + SAVE_VIEW)
    public String taskAddSave(Task task) {
        // check current account still valid
        if (authenticationUtil.getAccount() == null) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            task.setStatusId(DEFAULT_STATUS);
            taskService.saveTask(task);
            _isMsgShow = true;
            _msg = "Thêm công việc thành công!";
            return REDIRECT_PREFIX + TASK_VIEW;
        }
    }

    // Load edit task input form
    @GetMapping(EDIT_VIEW)
    public ModelAndView taskEdit(int id) {
        var account = authenticationUtil.getAccount();
        // check current account still valid
        if (account == null) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var task = taskService.getTask(id);
            // check if task is exist
            if (task == null) {
                _isMsgShow = true;
                _msg = "Không tìm thấy công việc!";
                return new ModelAndView(REDIRECT_PREFIX + TASK_VIEW);
            } else {
                // check permission
                if (!isPermissionLeader(account, task.getProject().getOriginatorId())) {
                    return new ModelAndView(FORWARD_PREFIX + FORBIDDEN_VIEW);
                } else {
                    var mav = new ModelAndView(TASK_EDIT_TEMP);
                    mav.addObject(USERS_PARAM, userService.getUsers());
                    mav.addObject(ACCOUNT_PARAM, account);
                    mav.addObject(PROJECTS_PARAM, projectService.getProjects());
                    mav.addObject(TASK_PARAM, task);
                    return mav;
                }
            }
        }
    }

    // Edit task
    @RequestMapping(value = EDIT_VIEW + SAVE_VIEW, method = { GET, PUT })
    public String taskEditSave(Task task, int originatorId) {
        var account = authenticationUtil.getAccount();
        // check current account still valid
        if (account == null) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            // check permission
            if (!isPermissionLeader(account, originatorId)) {
                return FORWARD_PREFIX + FORBIDDEN_VIEW;
            } else {
                taskService.saveTask(task);
                _isMsgShow = true;
                _msg = "Cập nhật công việc thành công!";
                return REDIRECT_PREFIX + TASK_VIEW;
            }
        }
    }

    // Delete task
    @RequestMapping(value = DELETE_VIEW, method = { GET, DELETE })
    public String taskDelete(int id) {
        var account = authenticationUtil.getAccount();
        // check current account still valid
        if (account == null) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            var task = taskService.getTask(id);
            // check task is exist
            if (task == null) {
                _isMsgShow = true;
                _msg = "Công việc không tồn tại!";
                return REDIRECT_PREFIX + TASK_VIEW;
            } else {
                // check permission
                if (!isPermissionLeader(account, task.getProject().getOriginatorId())) {
                    return FORWARD_PREFIX + FORBIDDEN_VIEW;
                } else {
                    taskService.deleteTask(id);
                    _isMsgShow = true;
                    _msg = "Xóa công việc thành công!";
                    return REDIRECT_PREFIX + TASK_VIEW;
                }
            }
        }
    }

    // Get role of current account
    private String getCurrentAccountRole(int roleId) {
        return roleService.getRole(roleId).getName().toUpperCase();
    }

    // Check permission leader for task
    private boolean isPermissionLeader(User user, int originatorId) {
        var currentAccountRole = getCurrentAccountRole(user.getRoleId());
        return currentAccountRole.equals(LEADER) && userService.getUser(originatorId).getId() == user.getId()
                || currentAccountRole.equals(ADMIN);
    }
}
