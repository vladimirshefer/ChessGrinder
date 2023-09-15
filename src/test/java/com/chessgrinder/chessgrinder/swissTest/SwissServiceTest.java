package com.chessgrinder.chessgrinder.swissTest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import com.chessgrinder.chessgrinder.service.*;
import lombok.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.junit.jupiter.*;


@ExtendWith(MockitoExtension.class)
public class SwissServiceTest {

    SwissService swissService;

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
}

