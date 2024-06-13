package com.chessgrinder.chessgrinder.security;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
    import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.core.userdetails.UserDetails;
    import org.springframework.security.oauth2.jwt.Jwt;

    public class CustomMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {
        @Override
        protected MethodSecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication, MethodInvocation invocation) {
            CustomMethodSecurityExpressionRoot root = new CustomMethodSecurityExpressionRoot(authentication);
            root.setPermissionEvaluator(getPermissionEvaluator());
            return root;
        }
    }
