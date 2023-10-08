package com.chessgrinder.chessgrinder.dto.internals;

import java.math.*;
import java.util.*;

import lombok.*;

@Data
@Builder
public class ScoreModel {

    BigDecimal score;
    List<ParticipantForPairing> participants;
}
