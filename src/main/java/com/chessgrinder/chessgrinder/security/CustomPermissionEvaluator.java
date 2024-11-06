package com.chessgrinder.chessgrinder.security;

import com.chessgrinder.chessgrinder.ChessGrinderApplication;
import com.chessgrinder.chessgrinder.entities.UserEntity;
import com.chessgrinder.chessgrinder.repositories.UserRepository;
import com.chessgrinder.chessgrinder.security.entitypermissionevaluator.EntityPermissionEvaluator;
import com.chessgrinder.chessgrinder.util.EntityScanner;
import com.chessgrinder.chessgrinder.security.util.SecurityUtil;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * This is an adapter from Spring Security's {@link PermissionEvaluator} to more useful {@link EntityPermissionEvaluator}.
 * Create a bean of type {@link EntityPermissionEvaluator}.
 * Allows to check if authenticated user has access to the specified entity by id.
 * <p/>
 * Used to support method-security for application REST controllers. Example:
 * <pre>{@code
 * @PreAuthorize("hasPermission(#id, 'FooEntity', 'ADMIN')")
 * @GetMapping("/id")
 * public FooDto getFoo(@PathVariable UUID id){...}
 * }</pre>
 *
 * @see EntityPermissionEvaluator
 */
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
        return evaluateAll(targetType,
                evaluator -> evaluator.hasPermission(getUserEntity(auth), targetId != null ? targetId.toString() : null, permission.toString())
        );
    }

    public <T> boolean hasPermission(
            @Nullable
            UserEntity user,
            @Nullable
            UUID targetId,
            @Nullable
            Class<T> entityType,
            @Nullable
            String permission
    ) {
        if (user == null || targetId == null || entityType == null || permission == null) {
            return false;
        }

        return evaluateAll(entityType,
                evaluator -> evaluator.hasPermission(user, targetId.toString(), permission)
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
        return evaluateAll(entityClass, predicate);
    }

    private boolean evaluateAll(Class<?> targetType, Predicate<EntityPermissionEvaluator<Object>> predicate) {
        List<EntityPermissionEvaluator<Object>> entityPermissionEvaluators1 = ((List<EntityPermissionEvaluator<Object>>) (Object) entityPermissionEvaluators);
        for (EntityPermissionEvaluator<Object> entityPermissionEvaluator : entityPermissionEvaluators1) {
            if (entityPermissionEvaluator.isMatchingType(targetType)) {
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
