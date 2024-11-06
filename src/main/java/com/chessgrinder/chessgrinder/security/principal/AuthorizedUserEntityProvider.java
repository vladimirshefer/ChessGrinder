package com.chessgrinder.chessgrinder.security.principal;

import com.chessgrinder.chessgrinder.entities.UserEntity;
import jakarta.annotation.Nullable;

public interface AuthorizedUserEntityProvider {

    @Nullable
    UserEntity getUserEntity();

}
