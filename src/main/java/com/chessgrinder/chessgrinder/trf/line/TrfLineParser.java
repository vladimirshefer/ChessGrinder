package com.chessgrinder.chessgrinder.trf.line;

import java.util.function.Consumer;

public interface TrfLineParser<T> {

    T tryParse(String line);

    void tryWrite(Consumer<String> trfConsumer, T line);

}
