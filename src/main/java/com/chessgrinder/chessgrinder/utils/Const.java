package com.chessgrinder.chessgrinder.utils;

public class Const {
    public static class Roles {
        public static final String ADMIN = "ROLE_ADMIN";
        public static final String USER = "ROLE_USER";
    }

    public static class WebSecurity {
        public static final String HOME_PAGE = "/";
        public static final String OAUTH2_STATE_SEPARATOR = ",";
    }

    public static class UserRegex {
        public static final String USERNAME_REGEX = "^[a-zA-Z][a-zA-Z0-9]+$";
        public static final String DATE_FORMAT_STRING = "dd.MM.yyyy";
    }

    public static class Tournaments {
        public static final int DEFAULT_ROUNDS_NUMBER = 6;
        public static final int MIN_ROUNDS_NUMBER = 0;
        public static final int MAX_ROUNDS_NUMBER = 99;
    }

    public static class Javafo {
        public static final Object JAVAFO_MONITOR = new Object();
        public static final int DEFAULT_RATING = 1000;
        public static final String NEWLINE_REGEX = "\\r?\\n|\\r";
        public static final int STANDARD_PAIRING_CODE = 1000;
    }
}
