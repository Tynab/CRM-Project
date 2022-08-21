package com.yan.crm_project.controller;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.*;
import org.springframework.web.servlet.*;

import com.yan.crm_project.model.*;
import com.yan.crm_project.service.*;
import com.yan.crm_project.util.*;

import static com.yan.crm_project.constant.AttributeConstant.*;
import static com.yan.crm_project.constant.TemplateConstant.*;
import static com.yan.crm_project.constant.ViewConstant.*;
import static java.util.stream.Stream.*;
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
    private AuthenticationUtil authenticationUtil;

    // Fields
    private User mCurrentAccount;
    private Task mChoosenOne;
    private String mMsg;
    private boolean mIsByPass;
    private boolean mIsMsgShow;

    // Load profile page
    @GetMapping("")
    public ModelAndView profile() {
        // check current account still valid
        if (!isValidAccount()) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(PROFILE_TEMP);
            var tasksNotStarted = mCurrentAccount.getTasksNotStarted();
            var tasksInProgress = mCurrentAccount.getTasksInProgress();
            var tasksCompleted = mCurrentAccount.getTasksCompleted();
            var tasks = concat(concat(tasksCompleted.stream(), tasksInProgress.stream()), tasksNotStarted.stream())
                    .toList();
            var tasksCount = tasks.size();
            mav.addObject(USER_PARAM, mCurrentAccount);
            mav.addObject(TASKS_PARAM, tasks);
            mav.addObject(NOT_STARTED_PERCENT_PARAM, tasksCount == 0 ? 0 : tasksNotStarted.size() * 100 / tasksCount);
            mav.addObject(IN_PROGRESS_PERCENT_PARAM, tasksCount == 0 ? 0 : tasksInProgress.size() * 100 / tasksCount);
            mav.addObject(COMPLETED_PERCENT_PARAM, tasksCount == 0 ? 0 : tasksCompleted.size() * 100 / tasksCount);
            showMessageBox(mav);
            mIsByPass = false;
            return mav;
        }
    }

    // Upload new avatar
    @PostMapping(EDIT_VIEW + AVATAR_VIEW)
    public String profileAvatarEdit(MultipartFile avatar) {
        // check current account still valid
        if (!isValidAccount()) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            mIsMsgShow = true;
            // check file is valid
            if (avatar.isEmpty()) {
                mMsg = "Tệp không xác định!";
            } else {
                fileUploadService.init();
                var file = fileUploadService.upload(avatar);
                // check file is image
                if (file == null) {
                    mMsg = "Tệp không hợp lệ!";
                } else {
                    var defaultAvatarName = mCurrentAccount.getId() + ".jpg";
                    // try to modify avatar
                    if (!imageService.resizeImage(file, defaultAvatarName)) {
                        mMsg = "Cập nhật ảnh đại diện thất bại!";
                    } else {
                        mCurrentAccount.setImage(defaultAvatarName);
                        userService.saveUserWithoutPassword(mCurrentAccount);
                        fileUploadService.remove(file.getName());
                        mMsg = "Cập nhật ảnh đại diện thành công!";
                    }
                }
            }
            mIsByPass = true;
            return REDIRECT_PREFIX + PROFILE_VIEW;
        }
    }

    // Load edit info input form
    @GetMapping(EDIT_VIEW + INFO_VIEW)
    public ModelAndView profileInfoEdit() {
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
    public String profileInfoEditSave(User user) {
        // check current account still valid
        if (!isValidAccount()) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            user.setId(mCurrentAccount.getId());
            user.setImage(mCurrentAccount.getImage());
            user.setRoleId(mCurrentAccount.getRoleId());
            // check new password
            if (hasText(user.getPassword())) {
                userService.saveUser(user);
            } else {
                userService.saveUserWithoutPassword(user);
            }
            mIsMsgShow = true;
            mMsg = "Cập nhật thông tin thành công!";
            mIsByPass = true;
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
                mIsMsgShow = true;
                mMsg = "Công việc không tồn tại!";
                return REDIRECT_PREFIX + PROFILE_VIEW;
            } else {
                return REDIRECT_PREFIX + PROFILE_VIEW + EDIT_VIEW + TASK_VIEW;
            }
        }
    }

    // Load edit task input form
    @GetMapping(EDIT_VIEW + TASK_VIEW)
    public ModelAndView profileTaskEdit() {
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
    public String profileTaskEditSave(int statusId) {
        // check current account still valid
        if (!isValidAccount()) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            mIsMsgShow = true;
            // check task is exist
            if (!isAliveChoosenOne()) {
                mMsg = "Công việc không tồn tại!";
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
                mMsg = "Cập nhật trạng thái công việc thành công!";
            }
            mIsByPass = true;
            return REDIRECT_PREFIX + PROFILE_VIEW;
        }
    }

    // Check valid account
    private boolean isValidAccount() {
        // check bypass
        if (mIsByPass) {
            return true;
        } else {
            mCurrentAccount = authenticationUtil.getAccount();
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
