package com.chessgrinder.chessgrinder.security;

import com.chessgrinder.chessgrinder.entities.UserEntity;

public interface AuthorizedUserEntityProvider {

    UserEntity getUserEntity();

}
