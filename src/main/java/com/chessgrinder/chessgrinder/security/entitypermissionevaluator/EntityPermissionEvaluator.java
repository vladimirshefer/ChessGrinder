package com.chessgrinder.chessgrinder.security.entitypermissionevaluator;

import com.chessgrinder.chessgrinder.entities.UserEntity;
import lombok.SneakyThrows;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.UUID;

public interface EntityPermissionEvaluator<T> {

    @SneakyThrows
    default Class<T> getEntityType(){
        return (Class<T>) getGenericType(this);
    };

    default boolean isMatchingType(Class<?> entityClass) {
        return entityClass.equals(getEntityType());
    }

    default boolean hasPermission(UserEntity user, T entity, String permission){
        return hasPermission(user.getId(), entity, permission);
    }

    default boolean hasPermission(UserEntity user, String entityId, String permission) {
        return hasPermission(user.getId(), entityId, permission);
    }

    boolean hasPermission(UUID userId, T entity, String permission);

    boolean hasPermission(UUID userId, String entityId, String permission);

    static Class<?> getGenericType(Object obj) {
        for(Type type : obj.getClass().getGenericInterfaces()) {
            if(type instanceof ParameterizedType) {
                Type[] parameterizedType = ((ParameterizedType)type).getActualTypeArguments();
                return (Class<?>) parameterizedType[0];
            }
        }
        return null;
    }
}
