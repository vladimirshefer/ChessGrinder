package com.chessgrinder.chessgrinder.security.entitypermissionevaluator;

import com.chessgrinder.chessgrinder.entities.UserEntity;

public interface EntityPermissionEvaluator<T> {

    boolean isMatchingType();

    boolean hasPermission(UserEntity user, T entity);

}
