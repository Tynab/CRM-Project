package com.yan.crm_project.constant;

public class ApplicationConstant {
    public static final int AVATAR_SIZE = 128;

    public static final long EXPIRATION_TIME = 60 * 60 * 1000;
    public static final String SECRET_KEY = "secret";

    public static final String DEFAULT_AVATAR = "0.jpg";
    public static final String DEFAULT_ROLE = "MEMBER";
    public static final String DEFAULT_STATUS = "Chưa bắt đầu";

    public class Role {
        public static final String ADMIN = "ADMIN";
        public static final String LEADER = "LEADER";
        public static final String MEMBER = "MEMBER";
    }

    public class TaskStatus {
        public static final String NOT_STARTED = "Chưa bắt đầu";
        public static final String IN_PROGRESS = "Đang thực hiện";
        public static final String COMPLETED = "Đã hoàn thành";
    }
}
