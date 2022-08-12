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
import com.yan.crm_proj.service.*;

import lombok.*;

import static com.auth0.jwt.JWT.*;
import static com.auth0.jwt.algorithms.Algorithm.*;
import static com.yan.crm_proj.common.ApplicationConstant.*;
import static com.yan.crm_proj.common.ApplicationConstant.TaskStatus.*;
import static com.yan.crm_proj.common.AttributeConstant.*;
import static com.yan.crm_proj.common.TemplateConstant.*;
import static com.yan.crm_proj.common.ViewConstant.*;
import static java.lang.System.*;
import static java.util.Collections.*;
import static java.util.stream.Collectors.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
@RequestMapping(USER_VIEW)
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private final ApplicationService applicationService;

    @Autowired
    private final UserService userService;

    @Autowired
    private final RoleService roleService;

    @Autowired
    private final TaskService taskService;

    // Fields
    private User mCurrentAccount;
    private User mChoosenOne;

    // Load user list
    @GetMapping("")
    public ModelAndView user() {
        // Check current account still valid
        if (!isValidAccount()) {
            // TODO:toastr
            return new ModelAndView(LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(USER_TEMP);
            mav.addObject(USERS_PARAM, userService.getUsers());
            mav.addObject(USER_PARAM, mCurrentAccount);
            return mav;
        }
    }

    // Load new user input form
    @GetMapping(ADD_VIEW)
    public ModelAndView userAdd() {
        // Check current account still valid
        if (!isValidAccount()) {
            // TODO:toastr
            return new ModelAndView(LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(USER_ADD_TEMP);
            mav.addObject(USER_PARAM, mCurrentAccount);
            return mav;
        }
    }

    // Add new user
    @PostMapping(ADD_VIEW + SAVE_VIEW)
    public String userAddSave(User user) {
        // Check current account still valid
        if (!isValidAccount()) {
            // TODO:toastr
            return REDIRECT + LOGOUT_VIEW;
        } else {
            // Check email is already exist
            if (userService.getUser(user.getEmail()) != null) {
                // TODO:toastr
                return REDIRECT + USER_VIEW + ADD_VIEW;
            } else {
                user.setImage(DEFAULT_AVATAR);
                user.setRoleId(roleService.getRole(DEFAULT_ROLE).getId());
                userService.saveUser(user);
                return REDIRECT + USER_VIEW;
            }
        }
    }

    // Get user by email and define action
    @RequestMapping(FIND_VIEW)
    public String findByEmail(String email, String action) {
        // Check current account still valid
        if (!isValidAccount()) {
            // TODO:toastr
            return REDIRECT + LOGOUT_VIEW;
        } else {
            mChoosenOne = userService.getUser(email);
            // Check if user is exist
            if (mChoosenOne == null) {
                // TODO:toastr
                return REDIRECT + USER_VIEW;
            } else {
                // Check action
                switch (action) {
                    case EDIT_VIEW: {
                        return REDIRECT + USER_VIEW + EDIT_VIEW;
                    }
                    case DETAILS_VIEW: {
                        return REDIRECT + USER_VIEW + DETAILS_VIEW;
                    }
                    default: {
                        return REDIRECT + USER_VIEW;
                    }
                }
            }
        }
    }

    // Load edit user input form
    @GetMapping(EDIT_VIEW)
    public ModelAndView userEdit() {
        // Check current account still valid
        if (!isValidAccount()) {
            return new ModelAndView(LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(USER_EDIT_TEMP);
            mav.addObject(USER_PARAM, mCurrentAccount);
            mav.addObject(PERSON_PARAM, mChoosenOne);
            return mav;
        }
    }

    // Edit user
    @RequestMapping(value = EDIT_VIEW + SAVE_VIEW, method = { GET, PUT })
    public String userEditSave(User user) {
        // Check current account still valid
        if (!isValidAccount()) {
            // TODO:toastr
            return REDIRECT + LOGOUT_VIEW;
        } else {
            // Check user still exist
            if (!isAliveChoosenOne()) {
                // TODO:toastr
            } else {
                user.setId(mChoosenOne.getId());
                user.setImage(mChoosenOne.getImage());
                user.setRoleId(mChoosenOne.getRoleId());
                userService.saveUser(user);
            }
            return REDIRECT + USER_VIEW;
        }
    }

    // Delete user
    @RequestMapping(value = DELETE_VIEW, method = { GET, DELETE })
    public String userDelete(int id) {
        // Check current account still valid
        if (!isValidAccount()) {
            // TODO:toastr
            return REDIRECT + LOGOUT_VIEW;
        } else {
            userService.deleteUser(id);
            return REDIRECT + USER_VIEW;
        }
    }

    // Load user detail page
    @GetMapping(DETAILS_VIEW)
    public ModelAndView userDetails() {
        // Check current account still valid
        if (!isValidAccount()) {
            // TODO:toastr
            return new ModelAndView(LOGOUT_VIEW);
        } else {
            var mav = new ModelAndView(USER_DETAILS_TEMP);
            try {
                var choosenOneId = mChoosenOne.getId();
                var taskNotStarted = taskService.getTasksByDoerAndTaskStatus(choosenOneId, NOT_STARTED);
                var taskInProgress = taskService.getTasksByDoerAndTaskStatus(choosenOneId, IN_PROGRESS);
                var taskCompleted = taskService.getTasksByDoerAndTaskStatus(choosenOneId, COMPLETED);
                var taskTotal = taskService.getTasksByDoer(choosenOneId).size();
                var notStartedPecent = taskTotal == 0 ? 0 : taskNotStarted.size() * 100 / taskTotal;
                var inProgressPercent = taskTotal == 0 ? 0 : taskInProgress.size() * 100 / taskTotal;
                var completedPercent = taskTotal == 0 ? 0 : taskCompleted.size() * 100 / taskTotal;
                mav.addObject("notStartedPecent", notStartedPecent);
                mav.addObject("inProgressPecent", inProgressPercent);
                mav.addObject("completedPercent", completedPercent);
                mav.addObject("taskNotStarted", taskNotStarted);
                mav.addObject("taskInProgress", taskInProgress);
                mav.addObject("taskCompleted", taskCompleted);
            } catch (Exception e) {
                // TODO:toastr
            }
            mav.addObject(USER_PARAM, mCurrentAccount);
            mav.addObject(PERSON_PARAM, mChoosenOne);
            return mav;
        }
    }

    // Refresh current account token
    @GetMapping(REFRESH_VIEW)
    public String refreshToken(HttpServletRequest request, HttpServletResponse response)
            throws StreamWriteException, DatabindException, IOException {
        final var header = request.getHeader(AUTHORIZATION);
        if (header != null && header.startsWith(TOKEN_PREFIX)) {
            try {
                final var refreshToken = header.substring(TOKEN_PREFIX.length());
                final var algorithm = HMAC256(SECRET_KEY.getBytes());
                final var user = userService.getUser(require(algorithm).build().verify(refreshToken).getSubject());
                final var roles = singleton(new Role(ROLE_KEY + user.getRole().getName().toUpperCase()));
                final var accessToken = create().withSubject(user.getEmail())
                        .withExpiresAt(new Date(currentTimeMillis() + EXPIRATION_TIME / 2))
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim(ROLE_CLAIM_KEY, roles.stream().map(Role::getName).collect(toList())).sign(algorithm);
                var tokens = new HashMap<>();
                tokens.put(ACCESS_TOKEN_KEY, accessToken);
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
        return REDIRECT + USER_VIEW;
    }

    // Check valid account
    private boolean isValidAccount() {
        mCurrentAccount = applicationService.getAccount();
        if (mCurrentAccount == null) {
            return false;
        } else {
            mCurrentAccount = userService.getUser(mCurrentAccount.getEmail());
            if (mCurrentAccount == null) {
                return false;
            } else {
                return true;
            }
        }
    }

    // Re-check choosen one
    private boolean isAliveChoosenOne() {
        if (mChoosenOne == null) {
            return false;
        } else {
            mChoosenOne = userService.getUser(mChoosenOne.getEmail());
            if (mChoosenOne == null) {
                return false;
            } else {
                return true;
            }
        }
    }
}
