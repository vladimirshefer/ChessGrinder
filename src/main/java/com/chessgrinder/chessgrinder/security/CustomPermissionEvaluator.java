package com.chessgrinder.chessgrinder.security;

import com.chessgrinder.chessgrinder.entities.UserEntity;
import com.chessgrinder.chessgrinder.repositories.UserRepository;
import com.chessgrinder.chessgrinder.security.entitypermissionevaluator.EntityPermissionEvaluator;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.List;
import java.util.function.Predicate;

@Component
@RequiredArgsConstructor
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private final List<EntityPermissionEvaluator<?>> entityPermissionEvaluators;
    private final UserRepository userRepository;

    @Override
    public boolean hasPermission(
            @Nullable
            Authentication auth,
            @Nullable
            Object targetDomainObject,
            @Nullable
            Object permission
    ) {
        if ((auth == null) || (targetDomainObject == null) || !(permission instanceof String)) {
            return false;
        }

        String targetType = targetDomainObject.getClass().getSimpleName().toUpperCase();

        UserEntity userEntity = getUserEntity(auth);
        return evaluateAll(auth, targetType, permission.toString().toUpperCase(),
                evaluator -> evaluator.hasPermission(userEntity, targetDomainObject, permission.toString())
        );
    }

    @Override
    public boolean hasPermission(
            @Nullable
            Authentication auth,
            @Nullable
            Serializable targetId,
            @Nullable
            String targetType,
            @Nullable
            Object permission) {
        if ((auth == null) || (targetType == null) || !(permission instanceof String)) {
            return false;
        }
        return evaluateAll(auth, targetType.toUpperCase(),
                permission.toString().toUpperCase(),
                evaluator -> evaluator.hasPermission(getUserEntity(auth), targetId.toString(), permission.toString())
        );
    }

    @SneakyThrows
    private boolean evaluateAll(Authentication auth, String targetType, String permission, Predicate<EntityPermissionEvaluator<Object>> predicate) {
        Class<?> entityClass = Class.forName(targetType);
        List<EntityPermissionEvaluator<Object>> entityPermissionEvaluators1 = ((List<EntityPermissionEvaluator<Object>>) (Object) entityPermissionEvaluators);
        for (EntityPermissionEvaluator<Object> entityPermissionEvaluator : entityPermissionEvaluators1) {
            if (entityPermissionEvaluator.isMatchingType(entityClass)) {
                if (predicate.test(entityPermissionEvaluator)) {
                    return true;
                }
            }
        }
        return false;
    }

    private UserEntity getUserEntity(Authentication auth) {
        UserEntity userEntity = SecurityUtil.tryGetUserEntity(auth);
        if (userEntity == null) userEntity = userRepository.findByUsername(auth.getName());
        return userEntity;
    }

}
