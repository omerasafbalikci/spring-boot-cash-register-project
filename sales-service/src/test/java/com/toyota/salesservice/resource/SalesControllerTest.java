package com.toyota.salesservice.resource;

import com.toyota.salesservice.dto.requests.CreateReturnRequest;
import com.toyota.salesservice.dto.requests.CreateSalesRequest;
import com.toyota.salesservice.dto.responses.GetAllSalesItemsResponse;
import com.toyota.salesservice.dto.responses.GetAllSalesResponse;
import com.toyota.salesservice.dto.responses.PaginationResponse;
import com.toyota.salesservice.service.abstracts.SalesService;
import com.toyota.salesservice.utilities.exceptions.SalesNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SalesControllerTest {
    @Mock
    private SalesService salesService;
    @InjectMocks
    private SalesController salesController;

    @Test
    void testAddSales() {
        // Given
        CreateSalesRequest createSalesRequest = new CreateSalesRequest();
        GetAllSalesResponse mockResponse = new GetAllSalesResponse();
        when(salesService.addSales(any(CreateSalesRequest.class))).thenReturn(mockResponse);

        // When
        ResponseEntity<GetAllSalesResponse> responseEntity = salesController.addSales(createSalesRequest);

        // Then
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(mockResponse, responseEntity.getBody());
        verify(salesService, times(1)).addSales(any(CreateSalesRequest.class));
    }

    @Test
    void testToReturn() {
        // Given
        CreateReturnRequest createReturnRequest = new CreateReturnRequest("123", "ABC123", 2, LocalDateTime.now());
        List<CreateReturnRequest> createReturnRequests = Collections.singletonList(createReturnRequest);
        GetAllSalesItemsResponse mockResponse = new GetAllSalesItemsResponse();
        when(salesService.toReturn(anyList())).thenReturn(Collections.singletonList(mockResponse));

        // When
        ResponseEntity<List<GetAllSalesItemsResponse>> responseEntity = salesController.toReturn(createReturnRequests);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(Collections.singletonList(mockResponse), responseEntity.getBody());
        verify(salesService, times(1)).toReturn(anyList());
    }

    @Test
    void testDeleteSales_SuccessfulDeletion() {
        // Given
        String salesNumber = "123";
        GetAllSalesResponse mockResponse = new GetAllSalesResponse();
        when(salesService.deleteSales(salesNumber)).thenReturn(mockResponse);

        // When
        ResponseEntity<GetAllSalesResponse> responseEntity = salesController.deleteSales(salesNumber);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockResponse, responseEntity.getBody());
        verify(salesService, times(1)).deleteSales(salesNumber);
    }

    @Test
    void testDeleteSales_SalesNotFoundException() {
        // Given
        String salesNumber = "456";
        when(salesService.deleteSales(salesNumber)).thenThrow(new SalesNotFoundException("Sales not found"));

        // When
        ResponseEntity<GetAllSalesResponse> responseEntity = salesController.deleteSales(salesNumber);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        verify(salesService, times(1)).deleteSales(salesNumber);
    }

    @Test
    void testDeleteSales_InternalServerError() {
        // Given
        String salesNumber = "789";
        when(salesService.deleteSales(salesNumber)).thenThrow(new RuntimeException("Internal Server Error"));

        // When
        ResponseEntity<GetAllSalesResponse> responseEntity = salesController.deleteSales(salesNumber);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        verify(salesService, times(1)).deleteSales(salesNumber);
    }

    @Test
    void testGetSalesStatistics() {
        // Given
        LocalDateTime startDate = LocalDateTime.of(2024, 6, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 6, 30, 23, 59, 59);
        TreeMap<String, Object> mockStatistics = new TreeMap<>();
        mockStatistics.put("totalSales", 1000.0);
        mockStatistics.put("averageSales", 250.0);
        mockStatistics.put("totalSalesCount", 4L);
        when(salesService.getSalesStatistics(startDate, endDate)).thenReturn(mockStatistics);

        // When
        ResponseEntity<TreeMap<String, Object>> responseEntity = salesController.getSalesStatistics(startDate, endDate);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockStatistics, responseEntity.getBody());
        verify(salesService, times(1)).getSalesStatistics(startDate, endDate);
    }

    @Test
    void testGetSalesFiltered() {
        // Given
        PaginationResponse<GetAllSalesResponse> mockPaginationResponse = new PaginationResponse<>();
        when(salesService.getSalesFiltered(eq(0), eq(3), any(String[].class), eq(null), eq(""), eq(""), eq(""), eq(""), eq(null), eq(null), eq(null)
        )).thenReturn(mockPaginationResponse);

        // When
        PaginationResponse<GetAllSalesResponse> response = salesController.getSalesFiltered(0, 3, new String[]{"id", "asc"}, null, "", "", "", "", null, null, null);

        // Then
        assertEquals(mockPaginationResponse, response);
        verify(salesService, times(1)).getSalesFiltered(
                eq(0),
                eq(3),
                any(String[].class),
                eq(null),
                eq(""),
                eq(""),
                eq(""),
                eq(""),
                eq(null),
                eq(null),
                eq(null)
        );
    }
}
