package com.yan.crm_proj.common;

public class ApplicationConstant {
    public static final long EXPIRATION_TIME = 60 * 60 * 1000;
    public static final String SECRET_KEY = "secret";

    public static final String DEFAULT_ROLE = "member";
    public static final String ADMIN_ROLE = "admin";

    public static final String DEFAULT_AVATAR = "0.jpg";

    public static final String REDIRECT = "redirect:";

    public class TaskStatus {
        public static final String NOT_STARTED = "chưa bắt đầu";
        public static final String IN_PROGRESS = "đang thực hiện";
        public static final String COMPLETED = "đã hoàn thành";
    }
}
