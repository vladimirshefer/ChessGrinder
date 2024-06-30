package com.chessgrinder.chessgrinder.comparator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ComparatorUtil {

    /**
     * First selects minimal candidate in the current entity, then compares entities by this candidate
     * @param <T> The element type
     * @param <R> The sub element type
     */
    public static <T, R> Comparator<T> compareRecursive(
            Function<T, List<R>> keyExtractor, Comparator<R> keyComparator
    ) {
        return Comparator.comparing(m -> findMin(keyComparator, keyExtractor.apply(m)), keyComparator);
    }

    private static <T> T findMin(Comparator<T> comparator, List<T> list) {
        var elements2 = new ArrayList<>(list);
        elements2.sort(comparator);
        return elements2.get(0);
    }

}
