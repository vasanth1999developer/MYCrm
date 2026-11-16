package com.microservices.authorizationservice.common;

import lombok.Data;

import java.util.List;

/**
 * One page of a cursor-paged list: {@code {"data": [...], "nextCursor": 42, "hasNext": true}}.
 *
 * <p>To get the next page, send {@code nextCursor} back as the "last id". Returned by
 * {@code AccessService.listAccesses}, which no endpoint exposes yet. Lombok {@code @Data}
 * generates getters, setters, {@code equals}, {@code hashCode} and {@code toString}.
 *
 * @param <T> item type
 */
@Data
public class CursorResponse<T> {

    /** The items on this page. */
    private List<T> data;
    /** Id to pass for the next page, or {@code null} when this is the last page. */
    private Integer nextCursor;
    /** Whether another page exists. */
    private boolean hasNext;

    /**
     * @param data       the items
     * @param nextCursor cursor for the next page, or {@code null}
     * @param hasNext    whether another page exists
     * @param size       the requested page size (currently not stored)
     */
    public CursorResponse(List<T> data, Integer nextCursor, boolean hasNext, int size) {
        this.data = data;
        this.nextCursor = nextCursor;
        this.hasNext = hasNext;
    }


}
