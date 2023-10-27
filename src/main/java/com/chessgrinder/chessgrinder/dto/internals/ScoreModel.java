package com.chessgrinder.chessgrinder.dto.internals;

import java.math.*;
import java.util.*;

import com.chessgrinder.chessgrinder.dto.ParticipantDto;
import lombok.*;

@Data
@Builder
public class ScoreModel {

    BigDecimal score;
    List<ParticipantDto> participants;
}
