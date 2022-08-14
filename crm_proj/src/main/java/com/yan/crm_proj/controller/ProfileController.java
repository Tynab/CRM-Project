package com.yan.crm_proj.controller;

import java.io.*;
import java.util.*;

import javax.servlet.http.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;

import com.fasterxml.jackson.core.exc.*;
import com.fasterxml.jackson.databind.*;
import com.yan.crm_proj.model.*;
import com.yan.crm_proj.model.Role;
import com.yan.crm_proj.service.*;
import com.yan.crm_proj.util.*;

import lombok.*;

import static com.auth0.jwt.JWT.*;
import static com.auth0.jwt.algorithms.Algorithm.*;
import static com.yan.crm_proj.constant.ApplicationConstant.*;
import static com.yan.crm_proj.constant.ApplicationConstant.TaskStatus.*;
import static com.yan.crm_proj.constant.AttributeConstant.*;
import static com.yan.crm_proj.constant.TemplateConstant.*;
import static com.yan.crm_proj.constant.ViewConstant.*;
import static java.lang.System.*;
import static java.util.Collections.*;
import static java.util.stream.Collectors.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.util.StringUtils.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
@RequestMapping(PROFILE_VIEW)
@RequiredArgsConstructor
public class ProfileController {
    @Autowired
    private final UserService userService;

    @Autowired
    private final TaskService taskService;

    @Autowired
    private final TaskStatusService taskStatusService;

    @Autowired
    private final ApplicationUtil applicationUtil;

    @Autowired
    private final UserUtil userUtil;

    // Fields
    private User mCurrentAccount;
    private Task mChoosenOne;
    private boolean mIsByPass;

    // Load profile page
    @GetMapping("")
    public ModelAndView profile() {
        // check current account still valid
        if (!isValidAccount()) {
            return new ModelAndView(REDIRECT + LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(PROFILE_TEMP);
            var email = mCurrentAccount.getEmail();
            var tasks = taskService.getTasksByDoer(email);
            var taskTotal = tasks.size();
            mav.addObject(T_TS_IP_PARAM, taskTotal == 0 ? 0
                    : (taskService.getTasksByDoerAndTaskStatus(email, NOT_STARTED)).size() * 100 / taskTotal);
            mav.addObject(T_TS_PP_PARAM, taskTotal == 0 ? 0
                    : (taskService.getTasksByDoerAndTaskStatus(email, IN_PROGRESS)).size() * 100 / taskTotal);
            mav.addObject(T_TS_OP_PARAM, taskTotal == 0 ? 0
                    : (taskService.getTasksByDoerAndTaskStatus(email, COMPLETED)).size() * 100 / taskTotal);
            mav.addObject(USER_PARAM, mCurrentAccount);
            mav.addObject(TASKS_PARAM, tasks);
            // reset bypass
            mIsByPass = false;
            return mav;
        }
    }

    // Load edit info form
    @GetMapping(EDIT_VIEW + INFO_VIEW)
    public ModelAndView profileEditInfo() {
        // check current account still valid
        if (!isValidAccount()) {
            return new ModelAndView(REDIRECT + LOGOUT_VIEW);
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
            return REDIRECT + LOGOUT_VIEW;
        } else {
            // redirect instantly
            mIsByPass = true;
            user.setId(mCurrentAccount.getId());
            user.setImage(mCurrentAccount.getImage());
            user.setRoleId(mCurrentAccount.getRoleId());
            // check new password
            if (!hasText(user.getPassword())) {
                userService.saveUserWithoutPassword(user);
            } else {
                userService.saveUser(user);
            }
            return REDIRECT + PROFILE_VIEW + applicationUtil.urlMsgSuccess();
        }
    }

    // Get task by id
    @RequestMapping(FIND_VIEW)
    public String findTaskById(int id) {
        // check current account still valid
        if (!isValidAccount()) {
            return REDIRECT + LOGOUT_VIEW;
        } else {
            mChoosenOne = taskService.getTask(id);
            // redirect instantly
            mIsByPass = true;
            // check if task is exist
            if (mChoosenOne == null) {
                return REDIRECT + PROFILE_VIEW + applicationUtil.urlMsgError();
            } else {
                return REDIRECT + PROFILE_VIEW + EDIT_VIEW + TASK_VIEW;
            }
        }
    }

    // Load edit task form
    @GetMapping(EDIT_VIEW + TASK_VIEW)
    public ModelAndView profileEditTask() {
        // check current account still valid
        if (!isValidAccount()) {
            return new ModelAndView(REDIRECT + LOGOUT_VIEW);
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
            return REDIRECT + LOGOUT_VIEW;
        } else {
            // redirect instantly
            mIsByPass = true;
            // check task still exist
            if (!isAliveChoosenOne()) {
                return REDIRECT + PROFILE_VIEW + applicationUtil.urlMsgError();
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
            return REDIRECT + PROFILE_VIEW + applicationUtil.urlMsgSuccess();
        }
    }

    // Refresh current account token
    @GetMapping(API_VIEW + REFRESH_VIEW)
    public String refreshToken(HttpServletRequest request, HttpServletResponse response)
            throws StreamWriteException, DatabindException, IOException {
        final var header = request.getHeader(AUTHORIZATION);
        // check token format in header
        if (header != null && header.startsWith(TOKEN_PREFIX)) {
            // get token from header
            try {
                final var refreshToken = header.substring(TOKEN_PREFIX.length());
                final var algorithm = HMAC256(SECRET_KEY.getBytes());
                final var user = userService.getUser(require(algorithm).build().verify(refreshToken).getSubject());
                var tokens = new HashMap<>();
                tokens.put(ACCESS_TOKEN_KEY,
                        create().withSubject(user.getEmail())
                                .withExpiresAt(new Date(currentTimeMillis() + EXPIRATION_TIME))
                                .withIssuer(request.getRequestURL().toString())
                                .withClaim(ROLE_CLAIM_KEY,
                                        singleton(new Role(ROLE_KEY + user.getRole().getName().toUpperCase())).stream()
                                                .map(Role::getName).collect(toList()))
                                .sign(algorithm));
                tokens.put(REFRESH_TOKEN_KEY, refreshToken);
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);
            } catch (Exception e) {
                response.setHeader(ERROR_HEADER_KEY, e.getMessage());
                response.setStatus(FORBIDDEN.value());
                var error = new HashMap<>();
                error.put(ERROR_MESSAGE_KEY, e.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        } else {
            throw new RuntimeException("Refresh token is missing");
        }
        // redirect instantly
        mIsByPass = true;
        return REDIRECT + PROFILE_VIEW + applicationUtil.urlMsgSuccess();
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
