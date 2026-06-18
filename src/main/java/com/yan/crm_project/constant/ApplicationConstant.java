package com.yan.crm_project.constant;

/**
 * Tom tat: Gom cac hang so nghiep vu dung chung cho kich thuoc avatar, JWT,
 * gia tri mac dinh va ten vai tro/trang thai cong viec.
 */
public final class ApplicationConstant {
    private ApplicationConstant() {
    }

    public static final int AVATAR_SIZE = 128;

    public static final long EXPIRATION_TIME = 60 * 60 * 1000;
    public static final String SECRET_KEY = System.getenv().getOrDefault("CRM_JWT_SECRET", "change-me-in-local-dev");

    public static final int DEFAULT_STATUS = 1;
    public static final int DEFAULT_ROLE = 3;
    public static final String DEFAULT_AVATAR = "0.jpg";

    /**
     * Tom tat: Ten vai tro duoc Spring Security so khop voi ROLE_* authority.
     */
    public static final class Role {
        private Role() {
        }

        public static final String ADMIN = "ADMIN";
        public static final String LEADER = "LEADER";
        public static final String MEMBER = "MEMBER";
    }

    /**
     * Tom tat: ID trang thai mac dinh trong bang trang_thai_cong_viec.
     */
    public static final class TaskStatus {
        private TaskStatus() {
        }

        public static final int NOT_STARTED = 1;
        public static final int IN_PROGRESS = 2;
        public static final int COMPLETED = 3;
    }
}
