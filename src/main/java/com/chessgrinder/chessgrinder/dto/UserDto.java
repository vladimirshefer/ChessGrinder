package com.chessgrinder.chessgrinder.dto;

import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class UserDto {

    /**
     * UUID string. Unique across all users.
     */
    private String id;

    /**
     * User login (email). Visible only to admins in API responses.
     */
    private String username;

    /**
     * Public user tag/handle used in shareable profile URLs. E.g. "vshefer".
     */
    @Nullable
    private String usertag;

    /**
     * User e-mail hash in MD-5. Unique across all users.
     * Could be null for "guest" users.
     */
    @Nullable
    private String emailHash;

    /**
     * User full name. E.g. "Vladimir Shefer"
     */
    private String name;

    /**
     * List of Badges (i.e. Achievements) of the user.
     */
    private List<BadgeDto> badges;

    private int eloPoints;

    private List<String> roles;

    private int reputation;

}
