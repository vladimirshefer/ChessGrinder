package com.chessgrinder.chessgrinder.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GraphTest {

    @Test
    void testDfs() {
        Graph<Integer> graph = new Graph<>();
        graph.put(1, 2);
        graph.put(2, 3);
        graph.put(3, 1);
        graph.put(4, 5);
        graph.put(5, 4);
        graph.put(6, 7);

        assertEquals(List.of(1, 2, 3), graph.dfs(1, ___ -> true).toList());
        assertEquals(List.of(1, 2), graph.dfs(1, it -> it <= 2).toList());
        assertEquals(List.of(4, 5), graph.dfs(4).toList());
        assertEquals(List.of(5, 4), graph.dfs(5).toList());
        assertEquals(List.of(6, 7), graph.dfs(6).toList());
        assertEquals(List.of(7), graph.dfs(7).toList());
    }
}
