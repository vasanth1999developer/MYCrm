package com.microservices.authorizationservice.common;

import lombok.Data;

import java.util.List;

@Data
public class CursorResponse<T> {

    private List<T> data;
    private Integer nextCursor;
    private boolean hasNext;

    public CursorResponse(List<T> data, Integer nextCursor, boolean hasNext, int size) {
        this.data = data;
        this.nextCursor = nextCursor;
        this.hasNext = hasNext;
    }


}
