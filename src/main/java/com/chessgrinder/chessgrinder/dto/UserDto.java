package com.chessgrinder.chessgrinder.dto;

import java.math.BigDecimal;
import java.util.*;

import lombok.*;

@Data
@Builder
public class UserDto {
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
     * User e-mail hash in MD-5. Unique across all users.
     * Could be empty string "" for "guest" users.
     */
    private String emailHash;
    /**
     * User full name. E.g. "Vladimir Shefer"
     */
    private String name;

    /**
     * List of Badges (i.e. Achievements) of the user.
     */
    private List<BadgeDto> badges;

    private List<String> roles;

    private int reputation;

    private BigDecimal globalScore;

}
