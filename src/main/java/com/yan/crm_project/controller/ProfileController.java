package com.yan.crm_project.controller;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.*;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.mvc.support.*;

import com.yan.crm_project.model.*;
import com.yan.crm_project.service.*;
import com.yan.crm_project.util.*;

import static com.yan.crm_project.constant.ApplicationConstant.TaskStatus.*;
import static com.yan.crm_project.constant.AttributeConstant.*;
import static com.yan.crm_project.constant.TemplateConstant.*;
import static com.yan.crm_project.constant.ViewConstant.*;
import static org.springframework.util.StringUtils.*;

@Controller
@RequestMapping(PROFILE_VIEW)
/**
 * Tom tat: Quan ly trang ca nhan, upload avatar, cap nhat thong tin va trang
 * thai cong viec cua account dang dang nhap.
 */
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

    /**
     * Nap profile va thong ke tien do cong viec cua chinh account.
     */
    @GetMapping("")
    public ModelAndView profile() {
        var account = authenticationUtil.getAccount();
        if (account == null) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(PROFILE_TEMP);
            var tasks = account.getTasks();
            var tasksCount = tasks.size();
            mav.addObject(ACCOUNT_PARAM, account);
            mav.addObject(NOT_STARTED_PERCENT_PARAM,
                    applicationUtil.calculatePercent(applicationUtil.splitTasksByStatus(tasks, NOT_STARTED).size(),
                            tasksCount));
            mav.addObject(IN_PROGRESS_PERCENT_PARAM,
                    applicationUtil.calculatePercent(applicationUtil.splitTasksByStatus(tasks, IN_PROGRESS).size(),
                            tasksCount));
            mav.addObject(COMPLETED_PERCENT_PARAM,
                    applicationUtil.calculatePercent(applicationUtil.splitTasksByStatus(tasks, COMPLETED).size(),
                            tasksCount));
            return mav;
        }
    }

    /**
     * Upload, resize va gan avatar moi cho account hien tai.
     */
    @PostMapping(EDIT_VIEW + AVATAR_VIEW)
    public String profileAvatarEdit(MultipartFile avatar, RedirectAttributes redirectAttributes) {
        var account = authenticationUtil.getAccount();
        if (account == null) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            if (avatar == null || avatar.isEmpty()) {
                applicationUtil.addFlashMessage(redirectAttributes, "Tệp không xác định!");
            } else {
                fileUploadService.init();
                var file = fileUploadService.upload(avatar);
                if (file == null) {
                    applicationUtil.addFlashMessage(redirectAttributes, "Tệp không hợp lệ!");
                } else {
                    var defaultAvatarName = account.getId() + ".jpg";
                    var resized = imageService.resizeImage(file, defaultAvatarName);
                    fileUploadService.remove(file.getName());
                    if (!resized) {
                        applicationUtil.addFlashMessage(redirectAttributes, "Cập nhật ảnh đại diện thất bại!");
                    } else {
                        account.setImage(defaultAvatarName);
                        userService.saveUserWithoutPassword(account);
                        applicationUtil.addFlashMessage(redirectAttributes, "Cập nhật ảnh đại diện thành công!");
                    }
                }
            }
            return REDIRECT_PREFIX + PROFILE_VIEW;
        }
    }

    /**
     * Nap form cap nhat thong tin ca nhan.
     */
    @GetMapping(EDIT_VIEW + INFO_VIEW)
    public ModelAndView profileInfoEdit() {
        var account = authenticationUtil.getAccount();
        if (account == null) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(PROFILE_INFO_TEMP);
            mav.addObject(ACCOUNT_PARAM, account);
            return mav;
        }
    }

    /**
     * Cap nhat thong tin ca nhan, khong tin id/email/role/image gui tu form.
     */
    @PostMapping(EDIT_VIEW + INFO_VIEW + SAVE_VIEW)
    public String profileInfoEditSave(User user, RedirectAttributes redirectAttributes) {
        var account = authenticationUtil.getAccount();
        if (account == null) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            user.setId(account.getId());
            user.setEmail(account.getEmail());
            user.setImage(account.getImage());
            user.setRoleId(account.getRoleId());
            if (hasText(user.getPassword())) {
                userService.saveUser(user);
            } else {
                userService.saveUserWithoutPassword(user);
            }
            applicationUtil.addFlashMessage(redirectAttributes, "Cập nhật thông tin thành công!");
            return REDIRECT_PREFIX + PROFILE_VIEW;
        }
    }

    /**
     * Nap form cap nhat trang thai task duoc giao cho account hien tai.
     */
    @GetMapping(EDIT_VIEW + TASK_VIEW)
    public ModelAndView profileTaskEdit(int id, RedirectAttributes redirectAttributes) {
        var account = authenticationUtil.getAccount();
        if (account == null) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var task = taskService.getTask(id);
            if (task == null) {
                applicationUtil.addFlashMessage(redirectAttributes, "Công việc không tồn tại!");
                return new ModelAndView(REDIRECT_PREFIX + PROFILE_VIEW);
            } else if (task.getDoerId() != account.getId()) {
                return new ModelAndView(FORWARD_PREFIX + FORBIDDEN_VIEW);
            } else {
                var mav = new ModelAndView(PROFILE_TASK_TEMP);
                mav.addObject(ACCOUNT_PARAM, account);
                mav.addObject(TASK_PARAM, task);
                mav.addObject(TASKSTATUSES_PARAM, taskStatusService.getTaskStatuses());
                return mav;
            }
        }
    }

    /**
     * Chi cap nhat statusId cua task hien co, giu nguyen cac field khac trong DB.
     */
    @PostMapping(EDIT_VIEW + TASK_VIEW + SAVE_VIEW)
    public String profileTaskEditSave(Task task, RedirectAttributes redirectAttributes) {
        var account = authenticationUtil.getAccount();
        if (account == null) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            var existingTask = taskService.getTask(task.getId());
            if (existingTask == null) {
                applicationUtil.addFlashMessage(redirectAttributes, "Công việc không tồn tại!");
                return REDIRECT_PREFIX + PROFILE_VIEW;
            }

            if (existingTask.getDoerId() != account.getId()) {
                return FORWARD_PREFIX + FORBIDDEN_VIEW;
            }

            existingTask.setStatusId(task.getStatusId());
            taskService.saveTask(existingTask);
            applicationUtil.addFlashMessage(redirectAttributes, "Cập nhật trạng thái công việc thành công!");
            return REDIRECT_PREFIX + PROFILE_VIEW;
        }
    }
}
