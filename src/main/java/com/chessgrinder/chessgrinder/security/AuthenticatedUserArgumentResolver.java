package com.chessgrinder.chessgrinder.security;

import com.chessgrinder.chessgrinder.entities.UserEntity;
import com.chessgrinder.chessgrinder.repositories.UserRepository;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.server.ResponseStatusException;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Component
@RequiredArgsConstructor
public class AuthenticatedUserArgumentResolver implements HandlerMethodArgumentResolver {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticatedUserArgumentResolver.class);

    private final UserRepository userRepository;

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.hasParameterAnnotation(AuthenticatedUser.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        var parameterType = parameter.getParameterType();

        if (!UserEntity.class.equals(parameterType)) {
            throw new IllegalArgumentException("Token auth could not resolve parameter " + parameter);
        }

        var accessToken = getAuthenticatedUser();

        if (accessToken == null) {
            var annotation = parameter.getParameterAnnotation(AuthenticatedUser.class);
            if (annotation == null) {
                throw new IllegalArgumentException("Parameter has no @AuthenticatedUser annotation. Parameter: " + parameter);
            }
            var requiredParameter = annotation.required();

            if (requiredParameter) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No bearer token present");
            }

            return null;
        }

        return accessToken;
    }

    private UserEntity getAuthenticatedUser() {
        var username = getAuthenticatedUsername();
        if (username == null) {
            return null;
        }
        UserEntity userByEmail = userRepository.findByUsername(username);
        return userByEmail != null ? userByEmail : userRepository.findByUsername(username);
    }

    @Nullable
    public String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() == null) {
            return null;
        }

        return authentication.getName();
    }

    /**
     * Annotation for argument in RestController method.
     * Argument must be of UserEntity type.
     * Injects the current authenticated user.
     * Always goes to database and fetches latest user data.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    public @interface AuthenticatedUser {
        boolean required() default true;
    }

}
