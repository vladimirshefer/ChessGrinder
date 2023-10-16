package com.chessgrinder.chessgrinder.dto.internals;

import com.chessgrinder.chessgrinder.dto.*;
import lombok.*;

@Data
public class ParticipantForPairing {
    ParticipantDto participant;
    boolean isBooked = false;
    int timesPlayedWhiteInTournament = 0;

    public static ParticipantForPairing of(ParticipantDto participant) {
        ParticipantForPairing result = new ParticipantForPairing();
        result.setParticipant(participant);
        return result;
    }
}
