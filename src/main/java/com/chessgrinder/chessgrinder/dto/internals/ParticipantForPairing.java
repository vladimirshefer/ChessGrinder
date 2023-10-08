package com.chessgrinder.chessgrinder.dto.internals;

import com.chessgrinder.chessgrinder.dto.*;
import lombok.*;

@Data
public class ParticipantForPairing {
    ParticipantDto participant;
    boolean isBooked = false;
    int timesPlayedWhiteInTournament = 0;
}
