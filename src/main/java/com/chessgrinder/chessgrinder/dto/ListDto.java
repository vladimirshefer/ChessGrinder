package com.chessgrinder.chessgrinder.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public final class ListDto<T> {
    private List<T> values;
}
