package com.chessgrinder.chessgrinder.swissTest;

import static org.junit.jupiter.api.Assertions.*;

import java.math.*;
import java.util.*;

import com.chessgrinder.chessgrinder.dto.*;
import com.chessgrinder.chessgrinder.enums.*;
import com.chessgrinder.chessgrinder.service.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.junit.jupiter.*;


@ExtendWith(MockitoExtension.class)
public class SwissServiceTest {

    SwissService swissService = new SwissService();

    @Test
    public void testSplit() {

        List<Integer> inputList = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer>[] result = swissService.split(inputList);

        assertEquals(2, result.length);

        List<Integer> firstSublist = result[0];

        assertEquals(3, firstSublist.size());
        assertEquals(Arrays.asList(1, 2, 3), firstSublist);

        List<Integer> secondSublist = result[1];

        assertEquals(2, secondSublist.size());
        assertEquals(Arrays.asList(4, 5), secondSublist);
    }

    @Test
    public void test_4players() {

        ParticipantDto participant1 = createParticipant(10, 6, "user1");
        ParticipantDto participant2 = createParticipant(8, 6, "user2");
        ParticipantDto participant3 = createParticipant(6, 6, "user3");
        ParticipantDto participant4 = createParticipant(4, 6, "user4");

        List<ParticipantDto> participants = List.of(participant1, participant2, participant3 ,participant4);

        List<MatchDto> matches = swissService.makePairs(participants);
        MatchDto firstMatch = matches.get(0);
        MatchDto secondMatch = matches.get(1);

        assertEquals(2, matches.size());

        assertEquals(firstMatch.getWhite(), participant1);
        assertEquals(firstMatch.getBlack(), participant2);

        assertEquals(secondMatch.getWhite(), participant3);
        assertEquals(secondMatch.getBlack(), participant4);

    }

    @Test
    public void test_5players_with_buy() {

        ParticipantDto participant1 = createParticipant(10, 6, "user1");
        ParticipantDto participant2 = createParticipant(8, 6, "user2");
        ParticipantDto participant3 = createParticipant(6, 6, "user3");
        ParticipantDto participant4 = createParticipant(4, 6, "user4");
        ParticipantDto participant5 = createParticipant(4, 6, "user5");

        List<ParticipantDto> participants = List.of(participant1, participant2, participant3 ,participant4, participant5);

        List<MatchDto> matches = swissService.makePairs(participants);
        MatchDto firstMatch = matches.get(0);
        MatchDto secondMatch = matches.get(1);
        MatchDto buy = matches.get(2);

        assertEquals(3, matches.size());

        assertEquals(firstMatch.getWhite(), participant1);
        assertEquals(firstMatch.getBlack(), participant2);

        assertEquals(secondMatch.getWhite(), participant3);
        assertEquals(secondMatch.getBlack(), participant4);

        assertEquals(buy.getWhite(), participant5);
        assertNull(buy.getBlack());
        assertEquals(buy.getResult(), MatchResult.BUY);
    }

    private ParticipantDto createParticipant(int val, int buchholz, String name) {
        return ParticipantDto.builder()
                .name(name)
                .score(BigDecimal.valueOf(val))
                .buchholz(BigDecimal.valueOf(buchholz))
                .build();
    }


}

