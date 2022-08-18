package com.yan.crm_project.controller;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;

import com.yan.crm_project.model.*;
import com.yan.crm_project.service.*;
import com.yan.crm_project.util.*;

import lombok.*;

import static com.yan.crm_project.constant.AppConstant.*;
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
    private ProjectService projectService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskStatusService taskStatusService;

    @Autowired
    private UserUtil userUtil;

    // Fields
    private User mCurrentAccount;
    private Task mChoosenOne;
    private String mMsg;
    private boolean mIsByPass;
    private boolean mIsMsgShow;

    // Load task list
    @GetMapping("")
    public ModelAndView task() {
        // check current account still valid
        if (!isValidAccount()) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(TASK_TEMP);
            mav.addObject(USER_PARAM, mCurrentAccount);
            mav.addObject(TASKS_PARAM, taskService.getTasks());
            showMessageBox(mav);
            mIsByPass = false;
            return mav;
        }
    }

    // Load new task input form
    @GetMapping(ADD_VIEW)
    public ModelAndView taskAdd() {
        // check current account still valid
        if (!isValidAccount()) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(TASK_ADD_TEMP);
            mav.addObject(USERS_PARAM, userService.getUsers());
            mav.addObject(USER_PARAM, mCurrentAccount);
            mav.addObject(PROJECTS_PARAM, projectService.getProjects());
            mIsByPass = false;
            return mav;
        }
    }

    // Add new task
    @PostMapping(ADD_VIEW + SAVE_VIEW)
    public String taskAddSave(Task task) {
        // check current account still valid
        if (!isValidAccount()) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            task.setStatusId(taskStatusService.getTaskStatus(DEFAULT_STATUS).getId());
            taskService.saveTask(task);
            mIsMsgShow = true;
            mMsg = "Thêm công việc thành công!";
            mIsByPass = true;
            return REDIRECT_PREFIX + TASK_VIEW;
        }
    }

    // Get task
    @RequestMapping(FIND_VIEW)
    public String findTask(int id) {
        // check current account still valid
        if (!isValidAccount()) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            mChoosenOne = taskService.getTask(id);
            mIsByPass = true;
            // check if task is exist
            if (mChoosenOne == null) {
                mIsMsgShow = true;
                mMsg = "Không tìm thấy công việc!";
                return REDIRECT_PREFIX + TASK_VIEW;
            } else {
                return REDIRECT_PREFIX + TASK_VIEW + EDIT_VIEW;
            }
        }
    }

    // Load edit task input form
    @GetMapping(EDIT_VIEW)
    public ModelAndView taskEdit() {
        // check current account still valid
        if (!isValidAccount()) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(TASK_EDIT_TEMP);
            mav.addObject(USERS_PARAM, userService.getUsers());
            mav.addObject(USER_PARAM, mCurrentAccount);
            mav.addObject(PROJECTS_PARAM, projectService.getProjects());
            mav.addObject(TASK_PARAM, mChoosenOne);
            mIsByPass = false;
            return mav;
        }
    }

    // Edit task
    @RequestMapping(value = EDIT_VIEW + SAVE_VIEW, method = { GET, PUT })
    public String taskEditSave(Task task) {
        // check current account still valid
        if (!isValidAccount()) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            mIsMsgShow = true;
            // check task is exist
            if (!isAliveChoosenOne()) {
                mMsg = "Công việc không tồn tại!";
            } else {
                task.setId(mChoosenOne.getId());
                task.setStatusId(mChoosenOne.getStatusId());
                taskService.saveTask(task);
                mMsg = "Cập nhật công việc thành công!";
            }
            mIsByPass = true;
            return REDIRECT_PREFIX + TASK_VIEW;
        }
    }

    // Delete task
    @RequestMapping(value = DELETE_VIEW, method = { GET, DELETE })
    public String taskDelete(int id) {
        // check current account still valid
        if (!isValidAccount()) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            mChoosenOne = taskService.getTask(id);
            mIsMsgShow = true;
            // check task is exist
            if (mChoosenOne == null) {
                mMsg = "Công việc không tồn tại!";
            } else {
                taskService.deleteTask(id);
                mMsg = "Xóa công việc thành công!";
            }
            mIsByPass = true;
            return REDIRECT_PREFIX + TASK_VIEW;
        }
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
        // check the task has been declared
        if (mChoosenOne == null) {
            return false;
        } else {
            mChoosenOne = taskService.getTask(mChoosenOne.getId());
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
