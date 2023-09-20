package com.chessgrinder.chessgrinder.dto;

import java.util.*;

import lombok.*;

@Data
@Builder
public class MemberDto {
    /**
     * UUID string. Unique across all users.
     */
    private String id;
    /**
     * User login. Unique across all users. E.g. "vshefer".
     * Could be null for "guest" users.
     */
    private String username;
    /**
     * User full name. E.g. "Vladimir Shefer"
     */
    private String name;

    /**
     * List of Badges (i.e. Achievements) of the user.
     */
    private List<BadgeDto> badges;

    private List<String> roles;
}
