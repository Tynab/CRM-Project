package com.yan.crm_project.controller;

import org.springframework.beans.factory.annotation.*;
import org.springframework.security.access.prepost.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.mvc.support.*;

import com.yan.crm_project.model.*;
import com.yan.crm_project.service.*;
import com.yan.crm_project.util.*;

import static com.yan.crm_project.constant.ApplicationConstant.*;
import static com.yan.crm_project.constant.ApplicationConstant.TaskStatus.*;
import static com.yan.crm_project.constant.AttributeConstant.*;
import static com.yan.crm_project.constant.TemplateConstant.*;
import static com.yan.crm_project.constant.ViewConstant.*;
import static org.springframework.util.StringUtils.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
@RequestMapping(USER_VIEW)
/**
 * Tom tat: Quan ly danh sach thanh vien, tao/sua/xoa tai khoan va xem thong ke
 * cong viec theo tung user.
 */
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private ApplicationUtil applicationUtil;

    @Autowired
    private AuthenticationUtil authenticationUtil;

    /**
     * Nap danh sach tat ca thanh vien.
     */
    @GetMapping("")
    public ModelAndView user() {
        var account = authenticationUtil.getAccount();
        if (account == null) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(USER_TEMP);
            mav.addObject(USERS_PARAM, userService.getUsers());
            mav.addObject(ACCOUNT_PARAM, account);
            return mav;
        }
    }

    /**
     * Nap form tao thanh vien moi cho admin.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(ADD_VIEW)
    public ModelAndView userAdd() {
        var account = authenticationUtil.getAccount();
        if (account == null) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(USER_ADD_TEMP);
            mav.addObject(ACCOUNT_PARAM, account);
            mav.addObject(ROLES_PARAM, roleService.getRoles());
            mav.addObject(DEFAULT_ROLE_ID_PARAM, DEFAULT_ROLE);
            return mav;
        }
    }

    /**
     * Luu thanh vien moi va gan avatar mac dinh.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(ADD_VIEW + SAVE_VIEW)
    public String userAddSave(User user, RedirectAttributes redirectAttributes) {
        if (authenticationUtil.getAccount() == null) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            if (userService.getUser(user.getEmail()) != null) {
                applicationUtil.addFlashMessage(redirectAttributes, "Tài khoản email này đã được đăng ký!");
                return REDIRECT_PREFIX + USER_VIEW + ADD_VIEW;
            } else {
                user.setImage(DEFAULT_AVATAR);
                userService.saveUser(user);
                applicationUtil.addFlashMessage(redirectAttributes, "Tài khoản đã được tạo thành công!");
                return REDIRECT_PREFIX + USER_VIEW;
            }
        }
    }

    /**
     * Nap form cap nhat thanh vien.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(EDIT_VIEW)
    public ModelAndView userEdit(int id, RedirectAttributes redirectAttributes) {
        var account = authenticationUtil.getAccount();
        if (account == null) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var user = userService.getUser(id);
            if (user == null) {
                applicationUtil.addFlashMessage(redirectAttributes, "Tài khoản không tồn tại!");
                return new ModelAndView(REDIRECT_PREFIX + USER_VIEW);
            } else {
                var mav = new ModelAndView(USER_EDIT_TEMP);
                mav.addObject(ACCOUNT_PARAM, account);
                mav.addObject(USER_PARAM, user);
                mav.addObject(ROLES_PARAM, roleService.getRoles());
                return mav;
            }
        }
    }

    /**
     * Cap nhat thong tin thanh vien, chi doi mat khau khi field password co gia
     * tri.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(EDIT_VIEW + SAVE_VIEW)
    public String userEditSave(User user, RedirectAttributes redirectAttributes) {
        if (authenticationUtil.getAccount() == null) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            if (userService.getUser(user.getId()) == null) {
                applicationUtil.addFlashMessage(redirectAttributes, "Tài khoản không tồn tại!");
                return REDIRECT_PREFIX + USER_VIEW;
            }

            if (hasText(user.getPassword())) {
                userService.saveUser(user);
            } else {
                userService.saveUserWithoutPassword(user);
            }
            applicationUtil.addFlashMessage(redirectAttributes, "Tài khoản đã được cập nhật thành công!");
            return REDIRECT_PREFIX + USER_VIEW;
        }
    }

    /**
     * Xoa thanh vien neu khong con lien ket du an hoac cong viec.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = DELETE_VIEW, method = { GET, DELETE })
    public String userDelete(int id, RedirectAttributes redirectAttributes) {
        if (authenticationUtil.getAccount() == null) {
            return REDIRECT_PREFIX + LOGOUT_VIEW;
        } else {
            var user = userService.getUser(id);
            if (user == null) {
                applicationUtil.addFlashMessage(redirectAttributes, "Tài khoản không tồn tại!");
            } else {
                if (user.getProjects().size() > 0) {
                    applicationUtil.addFlashMessage(redirectAttributes, "Tài khoản này đang có dự án, không thể xóa!");
                } else if (user.getTasks().size() > 0) {
                    applicationUtil.addFlashMessage(redirectAttributes,
                            "Tài khoản này đang có công việc, không thể xóa!");
                } else {
                    userService.deleteUser(id);
                    fileUploadService.remove(id + ".jpg");
                    applicationUtil.addFlashMessage(redirectAttributes, "Xóa tài khoản thành công!");
                }
            }
            return REDIRECT_PREFIX + USER_VIEW;
        }
    }

    /**
     * Hien thi thong tin chi tiet va thong ke cong viec cua mot thanh vien.
     */
    @GetMapping(DETAILS_VIEW)
    public ModelAndView userDetails(int id, RedirectAttributes redirectAttributes) {
        var account = authenticationUtil.getAccount();
        if (account == null) {
            return new ModelAndView(REDIRECT_PREFIX + LOGOUT_VIEW);
        } else {
            var user = userService.getUser(id);
            if (user == null) {
                applicationUtil.addFlashMessage(redirectAttributes, "Tài khoản không tồn tại!");
                return new ModelAndView(REDIRECT_PREFIX + USER_VIEW);
            } else {
                var mav = new ModelAndView(USER_DETAILS_TEMP);
                var tasks = user.getTasks();
                var tasksNotStarted = applicationUtil.splitTasksByStatus(tasks, NOT_STARTED);
                var tasksInProgress = applicationUtil.splitTasksByStatus(tasks, IN_PROGRESS);
                var tasksCompleted = applicationUtil.splitTasksByStatus(tasks, COMPLETED);
                var tasksCount = tasks.size();
                mav.addObject(ACCOUNT_PARAM, account);
                mav.addObject(USER_PARAM, user);
                mav.addObject(TASKS_NOT_STARTED_PARAM, tasksNotStarted);
                mav.addObject(TASKS_IN_PROGRESS_PARAM, tasksInProgress);
                mav.addObject(TASKS_COMPLETED_PARAM, tasksCompleted);
                mav.addObject(NOT_STARTED_PERCENT_PARAM, applicationUtil.calculatePercent(tasksNotStarted.size(), tasksCount));
                mav.addObject(IN_PROGRESS_PERCENT_PARAM, applicationUtil.calculatePercent(tasksInProgress.size(), tasksCount));
                mav.addObject(COMPLETED_PERCENT_PARAM, applicationUtil.calculatePercent(tasksCompleted.size(), tasksCount));
                return mav;
            }
        }
    }
}
