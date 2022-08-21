package com.yan.crm_project.constant;

public class ApplicationConstant {
    public static final int AVATAR_SIZE = 128;

    public static final long EXPIRATION_TIME = 60 * 60 * 1000;
    public static final String SECRET_KEY = "secret";

    public static final int DEFAULT_STATUS = 1;
    public static final int DEFAULT_ROLE = 3;
    public static final String DEFAULT_AVATAR = "0.jpg";

    public class Role {
        public static final String ADMIN = "ADMIN";
        public static final String LEADER = "LEADER";
        public static final String MEMBER = "MEMBER";
    }

    public class TaskStatus {
        public static final int NOT_STARTED = 1;
        public static final int IN_PROGRESS = 2;
        public static final int COMPLETED = 3;
    }
}
