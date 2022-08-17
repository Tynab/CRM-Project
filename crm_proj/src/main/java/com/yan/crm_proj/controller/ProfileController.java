package com.yan.crm_proj.controller;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.*;
import org.springframework.web.servlet.*;

import com.yan.crm_proj.model.*;
import com.yan.crm_proj.service.*;
import com.yan.crm_proj.util.*;

import static com.yan.crm_proj.constant.AppConstant.TaskStatus.*;
import static com.yan.crm_proj.constant.AttributeConstant.*;
import static com.yan.crm_proj.constant.TemplateConstant.*;
import static com.yan.crm_proj.constant.ViewConstant.*;
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
    private UserUtil userUtil;

    // Fields
    private User mCurrentAccount;
    private Task mChoosenOne;
    private String mMessage;
    private boolean mIsByPass;
    private boolean mIsFlag;

    // Load profile page
    @GetMapping("")
    public ModelAndView profile() {
        // check current account still valid
        if (!isValidAccount()) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(PROFILE_TEMP);
            var email = mCurrentAccount.getEmail();
            var tasks = taskService.getTasksByDoer(email);
            var tasksCount = tasks.size();
            mav.addObject(NOT_STARTED_PERCENT_PARAM, tasksCount == 0 ? 0
                    : taskService.getTasksByDoerAndStatus(email, NOT_STARTED).size() * 100 / tasksCount);
            mav.addObject(IN_PROGRESS_PERCENT_PARAM, tasksCount == 0 ? 0
                    : taskService.getTasksByDoerAndStatus(email, IN_PROGRESS).size() * 100 / tasksCount);
            mav.addObject(COMPLETED_PERCENT_PARAM, tasksCount == 0 ? 0
                    : taskService.getTasksByDoerAndStatus(email, COMPLETED).size() * 100 / tasksCount);
            mav.addObject(USER_PARAM, mCurrentAccount);
            mav.addObject(TASKS_PARAM, tasks);
            showMessageBox(mav);
            mIsByPass = false;
            return mav;
        }
    }

    // Upload new avatar
    @PostMapping(EDIT_VIEW + AVATAR_VIEW)
    public String profileEditAvatar(MultipartFile avatar) {
        // check current account still valid
        if (!isValidAccount()) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            mIsFlag = true;
            mIsByPass = true;
            // check file is valid
            if (avatar.isEmpty()) {
                mMessage = "Tệp không xác định!";
                return REDIRECT_PREFIX + PROFILE_VIEW;
            } else {
                fileUploadService.init();
                var file = fileUploadService.upload(avatar);
                // check file is image
                if (file == null) {
                    mMessage = "Tệp không hợp lệ!";
                    return REDIRECT_PREFIX + PROFILE_VIEW;
                } else {
                    // try to modify avatar
                    if (!imageService.resizeImage(file, mCurrentAccount.getId() + ".jpg")) {
                        mMessage = "Có lỗi xảy ra khi cập nhật ảnh đại diện!";
                        return REDIRECT_PREFIX + PROFILE_VIEW;
                    } else {
                        // clean up template image
                        if (!mCurrentAccount.getImage().equals(file.getName())) {
                            fileUploadService.remove(file.getName());
                        }
                        mCurrentAccount.setImage(mCurrentAccount.getId() + ".jpg");
                        userService.saveUserWithoutPassword(mCurrentAccount);
                        mMessage = "Cập nhật ảnh đại diện thành công!";
                        return REDIRECT_PREFIX + PROFILE_VIEW;
                    }
                }
            }
        }
    }

    // Load edit info form
    @GetMapping(EDIT_VIEW + INFO_VIEW)
    public ModelAndView profileEditInfo() {
        // check current account still valid
        if (!isValidAccount()) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(PROFILE_INFO_TEMP);
            mav.addObject(USER_PARAM, mCurrentAccount);
            mIsByPass = false;
            return mav;
        }
    }

    // Edit user
    @RequestMapping(value = EDIT_VIEW + INFO_VIEW + SAVE_VIEW, method = { GET, PUT })
    public String profileEditInfoSave(User user) {
        // check current account still valid
        if (!isValidAccount()) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            user.setId(mCurrentAccount.getId());
            user.setImage(mCurrentAccount.getImage());
            user.setRoleId(mCurrentAccount.getRoleId());
            // check new password
            if (!hasText(user.getPassword())) {
                userService.saveUserWithoutPassword(user);
            } else {
                userService.saveUser(user);
            }
            mIsFlag = true;
            mIsByPass = true;
            mMessage = "Cập nhật thông tin thành công!";
            return REDIRECT_PREFIX + PROFILE_VIEW;
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
                mIsFlag = true;
                mMessage = "Không tìm thấy công việc!";
                return REDIRECT_PREFIX + PROFILE_VIEW;
            } else {
                return REDIRECT_PREFIX + PROFILE_VIEW + EDIT_VIEW + TASK_VIEW;
            }
        }
    }

    // Load edit task form
    @GetMapping(EDIT_VIEW + TASK_VIEW)
    public ModelAndView profileEditTask() {
        // check current account still valid
        if (!isValidAccount()) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(PROFILE_TASK_TEMP);
            mav.addObject(USER_PARAM, mCurrentAccount);
            mav.addObject(TASK_PARAM, mChoosenOne);
            mav.addObject(TASKSTATUSES_PARAM, taskStatusService.getTaskStatuses());
            mIsByPass = false;
            return mav;
        }
    }

    // Edit task
    @RequestMapping(value = EDIT_VIEW + TASK_VIEW + SAVE_VIEW, method = { GET, PUT })
    public String profileEditTaskSave(int statusId) {
        // check current account still valid
        if (!isValidAccount()) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            mIsFlag = true;
            mIsByPass = true;
            // check task still exist
            if (!isAliveChoosenOne()) {
                mMessage = "Không tìm thấy công việc!";
                return REDIRECT_PREFIX + PROFILE_VIEW;
            } else {
                mChoosenOne.setId(mChoosenOne.getId());
                mChoosenOne.setName(mChoosenOne.getName());
                mChoosenOne.setDescription(mChoosenOne.getDescription());
                mChoosenOne.setStartDate(mChoosenOne.getStartDate());
                mChoosenOne.setEndDate(mChoosenOne.getEndDate());
                mChoosenOne.setDoerId(mChoosenOne.getDoerId());
                mChoosenOne.setProjectId(mChoosenOne.getProjectId());
                mChoosenOne.setStatusId(statusId);
                taskService.saveTask(mChoosenOne);
                mMessage = "Cập nhật trạng thái công việc thành công!";
                return REDIRECT_PREFIX + PROFILE_VIEW;
            }
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
        if (mIsFlag) {
            mav.addObject(FLAG_PARAM, true);
            mav.addObject(MESSAGE_PARAM, mMessage);
            mIsFlag = false;
        }
    }
}
