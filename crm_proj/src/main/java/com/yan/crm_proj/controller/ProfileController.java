package com.yan.crm_proj.controller;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.*;
import org.springframework.web.servlet.*;

import com.yan.crm_proj.model.*;
import com.yan.crm_proj.service.*;
import com.yan.crm_proj.util.*;

import static com.yan.crm_proj.constant.ApplicationConstant.TaskStatus.*;
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
    private ApplicationUtil applicationUtil;

    @Autowired
    private UserUtil userUtil;

    // Fields
    private User mCurrentAccount;
    private Task mChoosenOne;
    private boolean mIsByPass;

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
            var taskTotal = tasks.size();
            mav.addObject(NOT_STARTED_PERCENT_PARAM, taskTotal == 0 ? 0
                    : (taskService.getTasksByDoerAndTaskStatus(email, NOT_STARTED)).size() * 100 / taskTotal);
            mav.addObject(IN_PROGRESS_PERCENT_PARAM, taskTotal == 0 ? 0
                    : (taskService.getTasksByDoerAndTaskStatus(email, IN_PROGRESS)).size() * 100 / taskTotal);
            mav.addObject(COMPLETED_PERCENT_PARAM, taskTotal == 0 ? 0
                    : (taskService.getTasksByDoerAndTaskStatus(email, COMPLETED)).size() * 100 / taskTotal);
            mav.addObject(USER_PARAM, mCurrentAccount);
            mav.addObject(TASKS_PARAM, tasks);
            // reset bypass
            mIsByPass = false;
            return mav;
        }
    }

    @PostMapping(EDIT_VIEW + AVATAR_VIEW)
    public String editAvatar(MultipartFile avatar) {
        // check current account still valid
        if (!isValidAccount()) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            // redirect instantly
            mIsByPass = true;
            // check file is valid
            if (avatar.isEmpty()) {
                return REDIRECT_PREFIX + PROFILE_VIEW + applicationUtil.sendMsgUrl("Tệp không xác định!");
            } else {
                fileUploadService.init();
                var file = fileUploadService.upload(avatar);
                // check file is image
                if (file == null) {
                    return REDIRECT_PREFIX + PROFILE_VIEW + applicationUtil.sendMsgUrl("Tệp không hợp lệ!");
                } else {
                    // try to modify avatar
                    if (!imageService.resizeImage(file, mCurrentAccount.getId() + ".jpg")) {
                        return REDIRECT_PREFIX + PROFILE_VIEW
                                + applicationUtil.sendMsgUrl("Cập nhật ảnh đại diện thất bại!");
                    } else {
                        // clean up template image
                        if (!mCurrentAccount.getImage().equals(file.getName())) {
                            fileUploadService.remove(file.getName());
                        }
                        mCurrentAccount.setImage(mCurrentAccount.getId() + ".jpg");
                        userService.saveUserWithoutPassword(mCurrentAccount);
                        return REDIRECT_PREFIX + PROFILE_VIEW
                                + applicationUtil.sendMsgUrl("Đã cập nhật ảnh đại diện thành công!");
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
            // reset bypass
            mIsByPass = false;
            return mav;
        }
    }

    // Edit user
    @RequestMapping(value = EDIT_VIEW + INFO_VIEW + SAVE_VIEW, method = { GET, PUT })
    public String userEditInfoSave(User user) {
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
            // redirect instantly
            mIsByPass = true;
            return REDIRECT_PREFIX + PROFILE_VIEW + applicationUtil.urlMsgSuccess();
        }
    }

    // Get task by id
    @RequestMapping(FIND_VIEW)
    public String findTaskById(int id) {
        // check current account still valid
        if (!isValidAccount()) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            mChoosenOne = taskService.getTask(id);
            // redirect instantly
            mIsByPass = true;
            // check if task is exist
            if (mChoosenOne == null) {
                return REDIRECT_PREFIX + PROFILE_VIEW + applicationUtil.urlMsgError();
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
            // reset bypass
            mIsByPass = false;
            return mav;
        }
    }

    // Edit task
    @RequestMapping(value = EDIT_VIEW + TASK_VIEW + SAVE_VIEW, method = { GET, PUT })
    public String taskEditSave(int projectId) {
        // check current account still valid
        if (!isValidAccount()) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            // redirect instantly
            mIsByPass = true;
            // check task still exist
            if (!isAliveChoosenOne()) {
                return REDIRECT_PREFIX + PROFILE_VIEW + applicationUtil.urlMsgError();
            } else {
                mChoosenOne.setId(mChoosenOne.getId());
                mChoosenOne.setName(mChoosenOne.getName());
                mChoosenOne.setDescription(mChoosenOne.getDescription());
                mChoosenOne.setStartDate(mChoosenOne.getStartDate());
                mChoosenOne.setEndDate(mChoosenOne.getEndDate());
                mChoosenOne.setDoerId(mChoosenOne.getDoerId());
                mChoosenOne.setProjectId(mChoosenOne.getProjectId());
                mChoosenOne.setStatusId(projectId);
                taskService.saveTask(mChoosenOne);
            }
            return REDIRECT_PREFIX + PROFILE_VIEW + applicationUtil.urlMsgSuccess();
        }
    }

    // Check valid account
    private boolean isValidAccount() {
        // check bypass
        if (mIsByPass) {
            return true;
        } else {
            mCurrentAccount = userUtil.getAccount();
            // check account login
            if (mCurrentAccount == null) {
                return false;
            } else {
                mCurrentAccount = userService.getUser(mCurrentAccount.getEmail());
                // check database has user
                if (mCurrentAccount == null) {
                    return false;
                } else {
                    return true;
                }
            }
        }
    }

    // Re-check choosen one
    private boolean isAliveChoosenOne() {
        // check the task has been declared
        if (mChoosenOne == null) {
            return false;
        } else {
            mChoosenOne = taskService.getTask(mChoosenOne.getId());
            // check database has task
            if (mChoosenOne == null) {
                return false;
            } else {
                return true;
            }
        }
    }
}
