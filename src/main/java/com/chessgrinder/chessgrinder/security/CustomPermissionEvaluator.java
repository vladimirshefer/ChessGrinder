package com.chessgrinder.chessgrinder.security;

import com.chessgrinder.chessgrinder.ChessGrinderApplication;
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
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private final List<EntityPermissionEvaluator<?>> entityPermissionEvaluators;
    private final UserRepository userRepository;
    private final EntityScanner entityScanner = new EntityScanner();
    private final Set<Class<?>> entityClasses = entityScanner.findEntityClasses(ChessGrinderApplication.class.getPackageName());

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
        return evaluateAll(targetType,
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
        return evaluateAll(targetType.toUpperCase(),
                evaluator -> evaluator.hasPermission(getUserEntity(auth), targetId.toString(), permission.toString())
        );
    }

    @SneakyThrows
    private boolean evaluateAll(String targetType, Predicate<EntityPermissionEvaluator<Object>> predicate) {
        Set<Class<?>> matchClasses = entityClasses.stream().filter(
                        it -> it.getCanonicalName().equalsIgnoreCase(targetType) ||
                                it.getName().equalsIgnoreCase(targetType) ||
                                it.getSimpleName().equalsIgnoreCase(targetType)
                )
                .collect(Collectors.toSet());
        if (matchClasses.size() > 1) {
            throw new IllegalStateException("Several entity has matching name " + targetType + " in " + matchClasses);
        }
        if (matchClasses.isEmpty()) {
            try {
                matchClasses = Set.of(Class.forName(targetType));
            } catch (NoClassDefFoundError e) {
                // do nothing
            }
        }
        Class<?> entityClass = matchClasses.stream().findFirst().orElseThrow();
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
