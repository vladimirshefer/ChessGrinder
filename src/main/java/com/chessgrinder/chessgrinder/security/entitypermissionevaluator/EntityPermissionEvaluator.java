package com.chessgrinder.chessgrinder.security.entitypermissionevaluator;

import com.chessgrinder.chessgrinder.entities.UserEntity;
import jakarta.annotation.Nullable;
import lombok.SneakyThrows;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.UUID;

public interface EntityPermissionEvaluator<T> {

    @SneakyThrows
    default Class<T> getEntityType() {
        return (Class<T>) getGenericType(this);
    }

    default boolean isMatchingType(Class<?> entityClass) {
        return entityClass.equals(getEntityType());
    }

    default boolean hasPermission(@Nullable UserEntity user, @Nullable T entity, @Nullable String permission) {
        return hasPermission(user != null ? user.getId() : null, entity, permission);
    }

    default boolean hasPermission(@Nullable UserEntity user, @Nullable String entityId, @Nullable String permission) {
        return hasPermission(user != null ? user.getId() : null, entityId, permission);
    }

    boolean hasPermission(@Nullable UUID userId, @Nullable T entity, @Nullable String permission);

    boolean hasPermission(@Nullable UUID userId, @Nullable String entityId, @Nullable String permission);

    static Class<?> getGenericType(Object obj) {
        for (Type type : obj.getClass().getGenericInterfaces()) {
            if (type instanceof ParameterizedType) {
                Type[] parameterizedType = ((ParameterizedType) type).getActualTypeArguments();
                return (Class<?>) parameterizedType[0];
            }
        }
        return null;
    }
}
