package com.toyota.salesservice.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @param <T> response for pagination
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginationResponse<T> {
    private List<T> content;
    private CustomPageable pageable;

    public PaginationResponse(List<T> content, Page<T> page) {
        this.content = content;
        this.pageable = new CustomPageable(
                page.getPageable().getPageNumber(),
                page.getPageable().getPageSize(),
                page.getTotalPages(),
                page.getTotalElements());
    }
}
