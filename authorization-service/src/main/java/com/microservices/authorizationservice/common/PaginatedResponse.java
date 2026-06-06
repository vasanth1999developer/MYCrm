package com.microservices.authorizationservice.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedResponse<T> {

    private List<T> data;
    private int currentPage;
    private int totalPages;
    private long totalItems;
    private boolean isFirst;
    private boolean isLast;
    private boolean hasNext;
    private boolean hasPrevious;


    public static <T> PaginatedResponse<T> from(Page<T> page) {
        PaginatedResponse<T> res = new PaginatedResponse<>();
        res.setData(page.getContent());
        res.setCurrentPage(page.getNumber());
        res.setTotalPages(page.getTotalPages());
        res.setTotalItems(page.getTotalElements());
        res.setFirst(page.isFirst());
        res.setLast(page.isLast());
        res.setHasNext(page.hasNext());
        res.setHasPrevious(page.hasPrevious());
        return res;
    }
}