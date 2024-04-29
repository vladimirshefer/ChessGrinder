package com.chessgrinder.chessgrinder.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Pair <A, B>{
    private final A first;
    private final B second;
}
