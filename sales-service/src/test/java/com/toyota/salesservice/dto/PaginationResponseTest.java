package com.toyota.salesservice.dto;

import com.toyota.salesservice.dto.responses.CustomPageable;
import com.toyota.salesservice.dto.responses.GetAllSalesResponse;
import com.toyota.salesservice.dto.responses.PaginationResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PaginationResponseTest {
    @Test
    void getContent() {
        CustomPageable pageable = new CustomPageable(1, 3, 5, 20);
        List<GetAllSalesResponse> content = List.of(new GetAllSalesResponse());
        PaginationResponse<GetAllSalesResponse> page = new PaginationResponse<>(content, pageable);
        assertEquals(content, page.getContent());
    }

    @Test
    void getPageable() {
        CustomPageable pageable = new CustomPageable(1, 3, 5, 20);
        List<GetAllSalesResponse> content = List.of(new GetAllSalesResponse());
        PaginationResponse<GetAllSalesResponse> page = new PaginationResponse<>(content, pageable);
        assertEquals(pageable, page.getPageable());
    }

    @Test
    void setContent() {
        List<GetAllSalesResponse> content = List.of(new GetAllSalesResponse());
        PaginationResponse<GetAllSalesResponse> page = new PaginationResponse<>();
        page.setContent(content);
        assertEquals(content, page.getContent());
    }

    @Test
    void setPageable() {
        CustomPageable pageable = new CustomPageable(1, 3, 5, 20);
        PaginationResponse<GetAllSalesResponse> page = new PaginationResponse<>();
        page.setPageable(pageable);
        assertEquals(pageable, page.getPageable());
    }
}
