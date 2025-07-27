package com.chessgrinder.chessgrinder.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public final class ListDto<T> {
    private List<T> values;

    public Integer getCount(){
        return values.size();
    }

    public static <T> ListDto<T> of(List<T> values){
        return ListDto.<T>builder().values(values).build();
    }
}
