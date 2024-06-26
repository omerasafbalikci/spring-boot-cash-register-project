package com.toyota.salesservice.service.concretes;

import com.toyota.salesservice.dao.SalesItemRepository;
import com.toyota.salesservice.dao.SalesRepository;
import com.toyota.salesservice.domain.PaymentType;
import com.toyota.salesservice.domain.Sales;
import com.toyota.salesservice.domain.SalesItems;
import com.toyota.salesservice.dto.requests.CreateReturnRequest;
import com.toyota.salesservice.dto.responses.GetAllSalesItemsResponse;
import com.toyota.salesservice.dto.responses.GetAllSalesResponse;
import com.toyota.salesservice.dto.responses.PaginationResponse;
import com.toyota.salesservice.service.rules.SalesBusinessRules;
import com.toyota.salesservice.utilities.exceptions.SalesNotFoundException;
import com.toyota.salesservice.utilities.mappers.ModelMapperService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SalesManagerTest {
    @Mock
    private SalesRepository salesRepository;
    @Mock
    private SalesItemRepository salesItemRepository;
    @Mock
    private ModelMapperService modelMapperService;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private SalesBusinessRules salesBusinessRules;
    private SalesManager salesManager;

    @BeforeEach
    void setUp() {
        modelMapperService = mock(ModelMapperService.class);
        salesManager = new SalesManager(salesRepository, salesItemRepository, modelMapperService, salesBusinessRules);
    }

    @Test
    public void testToReturn_SuccessfulReturn() {
        List<CreateReturnRequest> createReturnRequests = new ArrayList<>();
        createReturnRequests.add(new CreateReturnRequest("123", "ABC123", 2, LocalDateTime.now()));
        Sales sales1 = Sales.builder()
                .id(1L)
                .salesNumber("123")
                .salesDate(LocalDateTime.now())
                .createdBy("Asaf")
                .paymentType(PaymentType.CASH)
                .totalPrice(200.0)
                .money(200.0)
                .change(0.0)
                .salesItemsList(new ArrayList<>())
                .build();

        SalesItems salesItem1 = new SalesItems();
        salesItem1.setBarcodeNumber("ABC123");
        salesItem1.setQuantity(5);
        salesItem1.setTotalPrice(100.0);
        sales1.getSalesItemsList().add(salesItem1);

        when(modelMapperService.forResponse()).thenReturn(modelMapper);

        when(salesRepository.findBySalesNumber("123")).thenReturn(Optional.of(sales1));

        GetAllSalesItemsResponse mockResponse = new GetAllSalesItemsResponse();
        when(modelMapperService.forResponse().map(any(SalesItems.class), eq(GetAllSalesItemsResponse.class))).thenReturn(mockResponse);

        List<GetAllSalesItemsResponse> responses = salesManager.toReturn(createReturnRequests);

        verify(salesRepository, times(1)).findBySalesNumber("123");
        verify(salesRepository, times(1)).save(any(Sales.class));


        assertEquals(1, responses.size());
        assertSame(mockResponse, responses.get(0));
    }

    @Test
    public void testToReturn_SalesNotFoundException() {
        List<CreateReturnRequest> createReturnRequests = new ArrayList<>();
        createReturnRequests.add(new CreateReturnRequest("456", "DEF456", 3, LocalDateTime.now()));

        when(salesRepository.findBySalesNumber("456")).thenReturn(Optional.empty());

        assertThrows(SalesNotFoundException.class, () -> salesManager.toReturn(createReturnRequests));

        verify(salesRepository, times(1)).findBySalesNumber("456");
    }

    @Test
    public void testDeleteSales_SuccessfulDeletion() {
        String salesNumber = "123";

        Sales sales = Sales.builder()
                .id(1L)
                .salesNumber(salesNumber)
                .salesItemsList(new ArrayList<>())
                .build();

        Optional<Sales> optionalSales = Optional.of(sales);

        List<SalesItems> salesItems = new ArrayList<>();
        salesItems.add(new SalesItems());

        GetAllSalesResponse mockResponse = new GetAllSalesResponse();

        when(salesRepository.findBySalesNumber(salesNumber)).thenReturn(optionalSales);
        when(modelMapperService.forResponse()).thenReturn(modelMapper);
        when(modelMapperService.forResponse().map(sales, GetAllSalesResponse.class)).thenReturn(mockResponse);

        doNothing().when(salesBusinessRules).updateInventory(anyList());

        GetAllSalesResponse response = salesManager.deleteSales(salesNumber);

        verify(salesRepository, times(1)).findBySalesNumber(salesNumber);
        verify(salesRepository, times(1)).deleteById(sales.getId());
        verify(salesItemRepository, times(1)).deleteBySalesId(sales.getId());

        verify(salesBusinessRules, times(1)).updateInventory(anyList());

        assertNotNull(response);
        assertEquals(mockResponse, response);
    }

    @Test
    public void testDeleteSales_SalesNotFoundException() {
        String salesNumber = "456";

        Optional<Sales> optionalSales = Optional.empty();

        when(salesRepository.findBySalesNumber(salesNumber)).thenReturn(optionalSales);

        assertThrows(SalesNotFoundException.class, () -> salesManager.deleteSales(salesNumber));

        verify(salesRepository, times(1)).findBySalesNumber(salesNumber);
        verify(salesRepository, never()).deleteById(anyLong());
        verify(salesItemRepository, never()).deleteBySalesId(anyLong());
        verify(salesBusinessRules, never()).updateInventory(anyList());
    }

    @Test
    public void testGetSalesStatistics() {
        LocalDateTime startDate = LocalDateTime.of(2024, 6, 25, 0, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 6, 25, 23, 59, 59);

        List<Sales> salesList = new ArrayList<>();
        salesList.add(Sales.builder().totalPrice(100.0).build());
        salesList.add(Sales.builder().totalPrice(200.0).build());

        when(salesRepository.findBySalesDateBetween(startDate, endDate)).thenReturn(salesList);

        TreeMap<String, Object> salesStatistics = salesManager.getSalesStatistics(startDate, endDate);

        assertNotNull(salesStatistics);
        assertEquals(300.0, salesStatistics.get("totalSales"));
        assertEquals(150.0, salesStatistics.get("averageSales"));
        assertEquals(2L, salesStatistics.get("totalSalesCount"));

        verify(salesRepository, times(1)).findBySalesDateBetween(startDate, endDate);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetSalesFiltered() {
        // Given
        int page = 0;
        int size = 3;
        String[] sort = {"salesNumber,asc", "salesDate,desc"};
        Long id = 1L;
        String salesNumber = "12345";
        String salesDate = "2024-06-01";
        String createdBy = "Asaf";
        String paymentType = "CASH";
        Double totalPrice = 100.0;
        Double money = 120.0;
        Double change = 20.0;

        Sort.Direction direction1 = Sort.Direction.ASC;
        Sort.Direction direction2 = Sort.Direction.DESC;

        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(direction1, "salesNumber"));
        orders.add(new Sort.Order(direction2, "salesDate"));

        Pageable pageable = PageRequest.of(page, size, Sort.by(orders));

        List<Sales> mockSales = List.of(new Sales());
        Page<Sales> pageMock = new PageImpl<>(mockSales, pageable, 1);

        // When
        when(modelMapperService.forResponse()).thenReturn(modelMapper);
        when(salesRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(pageMock);

        PaginationResponse<GetAllSalesResponse> result = salesManager.getSalesFiltered(page, size, sort, id, salesNumber,
                salesDate, createdBy, paymentType, totalPrice, money, change);

        // Then
        assertNotNull(result);
        assertEquals(mockSales.size(), result.getContent().size());
    }


}
