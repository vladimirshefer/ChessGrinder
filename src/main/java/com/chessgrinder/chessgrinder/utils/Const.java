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
        public static final String DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm";
        public static final String DEFAULT_CLUB_ID = "d1dea6e7-a60f-41a5-b53b-bfb8bdc69b9d";
    }
}
