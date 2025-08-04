package com.chessgrinder.chessgrinder.util;

import jakarta.annotation.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Graph<T> {
    private Map<T, Set<T>> graph = new HashMap<>();

    public Graph() {
    }

    public Graph(Iterable<? extends Map.Entry<T, T>> edges) {
        for (Map.Entry<T, T> vertex : edges) {
            put(vertex.getKey(), vertex.getValue());
        }
    }

    public void put(T from, T to) {
        if (!graph.containsKey(from)) {
            graph.put(from, new HashSet<>());
        }
        if (!graph.containsKey(to)) {
            graph.put(to, new HashSet<>());
        }
        graph.get(from).add(to);
    }

    public Set<T> neighbors(T from) {
        if (!graph.containsKey(from)) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(graph.get(from));
    }

    public Stream<T> dfs(T from, Predicate<T> predicate) {
        if (!predicate.test(from)) {
            return Stream.empty();
        }
        return GraphUtils.dfs(from, (it) -> neighbors(it).stream().filter(predicate).collect(Collectors.toSet()));
    }

    public Stream<T> dfs(T from) {
        return GraphUtils.dfs(from, this::neighbors);
    }

    public static class GraphUtils {
        public static <T> Stream<T> dfs(T from, Function<T, Iterable<T>> neighbors) {
            Iterator<T> iterator = new Iterator<>() {
                private final Deque<T> stack = new LinkedList<>();
                private final Set<T> visited = new HashSet<>();
                private T next;

                {
                    next = from;
                    stack.push(from);
                    visited.add(from);
                }

                @Nullable
                private T _next() {
                    while (!stack.isEmpty()) {
                        T current = stack.peek();
                        for (T neighbor : neighbors.apply(current)) {
                            if (!visited.contains(neighbor)) {
                                stack.push(neighbor);
                                visited.add(neighbor);
                                next = neighbor;
                                return next;
                            }
                        }
                        stack.pop();
                    }
                    return null;
                }

                @Override
                public boolean hasNext() {
                    if (next != null) {
                        return true;
                    }
                    next = _next();
                    return next != null;
                }

                @Override
                public T next() {
                    if (!hasNext()) {
                        throw new NoSuchElementException();
                    }
                    var curr = next;
                    next = null;
                    return curr;
                }
            };
            return StreamSupport.stream(((Iterable<T>) () -> iterator).spliterator(), false);
        }
    }
}
