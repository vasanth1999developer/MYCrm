package com.microservices.authorizationservice.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * A simpler, stable JSON shape for one page of results, instead of serializing Spring's
 * {@link Page} directly.
 *
 * <p>JSON keys: {@code data}, {@code currentPage}, {@code totalPages}, {@code totalItems},
 * {@code first}, {@code last}, {@code hasNext}, {@code hasPrevious}. (Lombok turns the boolean
 * fields {@code isFirst}/{@code isLast} into {@code isFirst()}/{@code isLast()} getters, which
 * Jackson names {@code first}/{@code last}.)
 *
 * @param <T> item type
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedResponse<T> {

    /** The items on this page. */
    private List<T> data;
    /** Zero-based number of this page. */
    private int currentPage;
    /** Total number of pages. */
    private int totalPages;
    /** Total number of items across all pages. */
    private long totalItems;
    /** Whether this is the first page (JSON key {@code first}). */
    private boolean isFirst;
    /** Whether this is the last page (JSON key {@code last}). */
    private boolean isLast;
    /** Whether a next page exists. */
    private boolean hasNext;
    /** Whether a previous page exists. */
    private boolean hasPrevious;


    /**
     * Copies the content and paging metadata of a Spring {@link Page}.
     *
     * @param page the page returned by a repository
     * @param <T>  item type
     * @return the response
     */
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
