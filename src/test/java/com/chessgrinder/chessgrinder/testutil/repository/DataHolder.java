package com.chessgrinder.chessgrinder.testutil.repository;

import java.util.Map;

public interface DataHolder<T, ID> {
    Map<ID, T> getData();
}
