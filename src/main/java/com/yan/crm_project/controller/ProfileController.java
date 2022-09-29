package com.yan.crm_project.controller;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.*;
import org.springframework.web.servlet.*;

import com.yan.crm_project.model.*;
import com.yan.crm_project.service.*;
import com.yan.crm_project.util.*;

import lombok.*;

import static com.yan.crm_project.common.Bean.*;
import static com.yan.crm_project.constant.ApplicationConstant.TaskStatus.*;
import static com.yan.crm_project.constant.AttributeConstant.*;
import static com.yan.crm_project.constant.TemplateConstant.*;
import static com.yan.crm_project.constant.ViewConstant.*;
import static org.springframework.util.StringUtils.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
@RequestMapping(PROFILE_VIEW)
public class ProfileController {
    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskStatusService taskStatusService;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private ApplicationUtil applicationUtil;

    @Autowired
    private AuthenticationUtil authenticationUtil;

    // Load profile page
    @GetMapping("")
    public ModelAndView profile() {
        var account = authenticationUtil.getAccount();
        // check current account still valid
        if (account == null) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(PROFILE_TEMP);
            var tasks = account.getTasks();
            var tasksCount = tasks.size();
            mav.addObject(ACCOUNT_PARAM, account);
            mav.addObject(NOT_STARTED_PERCENT_PARAM, tasksCount == 0 ? 0
                    : applicationUtil.splitTasksByStatus(tasks, NOT_STARTED).size() * 100 / tasksCount);
            mav.addObject(IN_PROGRESS_PERCENT_PARAM, tasksCount == 0 ? 0
                    : applicationUtil.splitTasksByStatus(tasks, IN_PROGRESS).size() * 100 / tasksCount);
            mav.addObject(COMPLETED_PERCENT_PARAM, tasksCount == 0 ? 0
                    : applicationUtil.splitTasksByStatus(tasks, COMPLETED).size() * 100 / tasksCount);
            _isMsgShow = applicationUtil.showMessageBox(mav);
            return mav;
        }
    }

    // Upload new avatar
    @PostMapping(EDIT_VIEW + AVATAR_VIEW)
    public String profileAvatarEdit(MultipartFile avatar) {
        var account = authenticationUtil.getAccount();
        // check current account still valid
        if (account == null) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            _isMsgShow = true;
            // check file is valid
            if (avatar.isEmpty()) {
                _msg = "Tệp không xác định!";
            } else {
                fileUploadService.init();
                var file = fileUploadService.upload(avatar);
                // check file is image
                if (file == null) {
                    _msg = "Tệp không hợp lệ!";
                } else {
                    var defaultAvatarName = account.getId() + ".jpg";
                    // try to modify avatar
                    if (!imageService.resizeImage(file, defaultAvatarName)) {
                        _msg = "Cập nhật ảnh đại diện thất bại!";
                    } else {
                        account.setImage(defaultAvatarName);
                        userService.saveUserWithoutPassword(account);
                        fileUploadService.remove(file.getName());
                        _msg = "Cập nhật ảnh đại diện thành công!";
                    }
                }
            }
            return REDIRECT_PREFIX + PROFILE_VIEW;
        }
    }

    // Load edit info input form
    @GetMapping(EDIT_VIEW + INFO_VIEW)
    public ModelAndView profileInfoEdit() {
        var account = authenticationUtil.getAccount();
        // check current account still valid
        if (account == null) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(PROFILE_INFO_TEMP);
            mav.addObject(ACCOUNT_PARAM, account);
            return mav;
        }
    }

    // Edit user
    @RequestMapping(value = EDIT_VIEW + INFO_VIEW + SAVE_VIEW, method = { GET, PUT })
    public String profileInfoEditSave(User user) {
        var account = authenticationUtil.getAccount();
        // check current account still valid
        if (account == null) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            // check new password
            if (hasText(user.getPassword())) {
                userService.saveUser(user);
            } else {
                userService.saveUserWithoutPassword(user);
            }
            _isMsgShow = true;
            _msg = "Cập nhật thông tin thành công!";
            return REDIRECT_PREFIX + PROFILE_VIEW;
        }
    }

    // Load edit task input form
    @GetMapping(EDIT_VIEW + TASK_VIEW)
    public ModelAndView profileTaskEdit(int id) {
        var account = authenticationUtil.getAccount();
        // check current account still valid
        if (account == null) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var task = taskService.getTask(id);
            // check if task is exist
            if (task == null) {
                _isMsgShow = true;
                _msg = "Công việc không tồn tại!";
                return new ModelAndView(REDIRECT_PREFIX + PROFILE_VIEW);
            } else {
                var mav = new ModelAndView(PROFILE_TASK_TEMP);
                mav.addObject(ACCOUNT_PARAM, account);
                mav.addObject(TASK_PARAM, task);
                mav.addObject(TASKSTATUSES_PARAM, taskStatusService.getTaskStatuses());
                return mav;
            }
        }
    }

    // Edit task
    @RequestMapping(value = EDIT_VIEW + TASK_VIEW + SAVE_VIEW, method = { GET, PUT })
    public String profileTaskEditSave(Task task) {
        // check current account still valid
        if (authenticationUtil.getAccount() == null) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            taskService.saveTask(task);
            _isMsgShow = true;
            _msg = "Cập nhật trạng thái công việc thành công!";
            return REDIRECT_PREFIX + PROFILE_VIEW;
        }
    }
}
