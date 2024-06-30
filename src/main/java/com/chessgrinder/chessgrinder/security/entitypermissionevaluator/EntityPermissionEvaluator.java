package com.chessgrinder.chessgrinder.security.entitypermissionevaluator;

import com.chessgrinder.chessgrinder.entities.UserEntity;
import jakarta.annotation.Nullable;
import lombok.SneakyThrows;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.UUID;
import java.lang.reflect.Field;
import java.util.function.Predicate;

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

    default boolean hasPermission(@Nullable UUID userId, @Nullable T entity, @Nullable String permission){
        Object id = getIdField(entity);
        return hasPermission(userId, id != null ? String.valueOf(id) : null, permission);
    }

    boolean hasPermission(@Nullable UUID userId, @Nullable String entityId, @Nullable String permission);

    /**
     *  gets the entity type from {@code implements EntityPermissionEvaluator<MyEntity>}
     * @param obj this
     * @return target entity type.
     */
    static Class<?> getGenericType(Object obj) {
        for (Type type : obj.getClass().getGenericInterfaces()) {
            if (type instanceof ParameterizedType) {
                Type[] parameterizedType = ((ParameterizedType) type).getActualTypeArguments();
                return (Class<?>) parameterizedType[0];
            }
        }
        return null;
    }

    @Nullable
    private static <T> Object getIdField(@Nullable T entity) {
        if (entity == null) return null;
        Predicate<Annotation> predicate = (a -> a.annotationType().getSimpleName().equals("Id"));

        try {
            Field[] fields = entity.getClass().getDeclaredFields();
            Field idField = null;
            for (Field field : fields) {
                Annotation[] annotations = field.getDeclaredAnnotations();
                for (Annotation annotation : annotations) {
                    if (predicate.test(annotation)) {
                        idField = field;
                        break;
                    }
                }
                if (idField != null) {
                    break;
                }
            }
            if (idField == null) {
                throw new UnsupportedOperationException("Entity does not have a field annotated with @Id " + entity.getClass().getName());
            }
            idField.setAccessible(true);
            return idField.get(entity);
        } catch (IllegalAccessException e) {
            throw new UnsupportedOperationException("Access not allowed to read entity's @Id field value", e);
        }
    }
}
