package com.yan.crm_project.controller;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;

import com.yan.crm_project.model.*;
import com.yan.crm_project.service.*;
import com.yan.crm_project.util.*;

import static com.yan.crm_project.constant.AttributeConstant.*;
import static com.yan.crm_project.constant.TemplateConstant.*;
import static com.yan.crm_project.constant.ViewConstant.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
@RequestMapping(PROJECT_VIEW)
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    @Autowired
    private AuthenticationUtil authenticationUtil;

    // Fields
    private User mCurrentAccount;
    private Project mChoosenOne;
    private String mMsg;
    private boolean mIsByPass;
    private boolean mIsMsgShow;

    // Load project list
    @GetMapping("")
    public ModelAndView project() {
        // check current account still valid
        if (!isValidAccount()) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(PROJECT_TEMP);
            mav.addObject(USER_PARAM, mCurrentAccount);
            mav.addObject(PROJECTS_PARAM, projectService.getProjects());
            showMessageBox(mav);
            mIsByPass = false;
            return mav;
        }
    }

    // Load new project input form
    @GetMapping(ADD_VIEW)
    public ModelAndView projectAdd() {
        // check current account still valid
        if (!isValidAccount()) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(PROJECT_ADD_TEMP);
            mav.addObject(USER_PARAM, mCurrentAccount);
            mIsByPass = false;
            return mav;
        }
    }

    // Add new project
    @PostMapping(ADD_VIEW + SAVE_VIEW)
    public String projectAddSave(Project project) {
        // check current account still valid
        if (!isValidAccount()) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            project.setOriginatorId(mCurrentAccount.getId());
            projectService.saveProject(project);
            mIsMsgShow = true;
            mMsg = "Thêm dự án thành công!";
            mIsByPass = true;
            return REDIRECT_PREFIX + PROJECT_VIEW;
        }
    }

    // Get project
    @RequestMapping(FIND_VIEW)
    public String findProject(int id) {
        // check current account still valid
        if (!isValidAccount()) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            mChoosenOne = projectService.getProject(id);
            mIsByPass = true;
            // check if project is exist
            if (mChoosenOne == null) {
                mIsMsgShow = true;
                mMsg = "Dự án không tồn tại!";
                return REDIRECT_PREFIX + PROJECT_VIEW;
            } else {
                return REDIRECT_PREFIX + PROJECT_VIEW + EDIT_VIEW;
            }
        }
    }

    // Load edit project input form
    @GetMapping(EDIT_VIEW)
    public ModelAndView projectEdit() {
        // check current account still valid
        if (!isValidAccount()) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(PROJECT_EDIT_TEMP);
            mav.addObject(USER_PARAM, mCurrentAccount);
            mav.addObject(PROJECT_PARAM, mChoosenOne);
            mIsByPass = false;
            return mav;
        }
    }

    // Edit project
    @RequestMapping(value = EDIT_VIEW + SAVE_VIEW, method = { GET, PUT })
    public String projectEditSave(Project project) {
        // check current account still valid
        if (!isValidAccount()) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            mIsMsgShow = true;
            // check project is exist
            if (!isAliveChoosenOne()) {
                mMsg = "Dự án không tồn tại!";
            } else {
                project.setId(mChoosenOne.getId());
                project.setOriginatorId(mChoosenOne.getOriginatorId());
                projectService.saveProject(project);
                mMsg = "Cập nhật dự án thành công!";
            }
            mIsByPass = true;
            return REDIRECT_PREFIX + PROJECT_VIEW;
        }
    }

    // Delete project
    @RequestMapping(value = DELETE_VIEW, method = { GET, DELETE })
    public String projectDelete(int id) {
        // check current account still valid
        if (!isValidAccount()) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            mChoosenOne = projectService.getProject(id);
            mIsMsgShow = true;
            // check if project is exist
            if (mChoosenOne == null) {
                mMsg = "Dự án không tồn tại!";
            } else {
                // check project disconnect
                if (mChoosenOne.getTasks().size() > 0) {
                    mMsg = "Dự án đang có công việc, không thể xóa!";
                } else {
                    projectService.deleteProject(id);
                    mMsg = "Xóa dự án thành công!";
                }
            }
            mIsByPass = true;
            return REDIRECT_PREFIX + PROJECT_VIEW;
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
        // check the project has been declared
        if (mChoosenOne == null) {
            return false;
        } else {
            mChoosenOne = projectService.getProject(mChoosenOne.getId());
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
