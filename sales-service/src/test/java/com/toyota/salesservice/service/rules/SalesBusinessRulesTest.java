package com.toyota.salesservice.service.rules;

import com.toyota.salesservice.dao.CampaignRepository;
import com.toyota.salesservice.domain.Campaign;
import com.toyota.salesservice.domain.PaymentType;
import com.toyota.salesservice.domain.Sales;
import com.toyota.salesservice.domain.SalesItems;
import com.toyota.salesservice.dto.requests.CreateSalesItemsRequest;
import com.toyota.salesservice.dto.requests.CreateSalesRequest;
import com.toyota.salesservice.dto.requests.InventoryRequest;
import com.toyota.salesservice.utilities.exceptions.PaymentTypeNotEnteredException;
import com.toyota.salesservice.utilities.exceptions.ReturnPeriodExpiredException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class SalesBusinessRulesTest {
    @Mock
    private CampaignRepository campaignRepository;
    @Mock
    private WebClient.Builder webClientBuilder;
    @InjectMocks
    private SalesBusinessRules salesBusinessRules;
    private Sales sales;
    private CreateSalesRequest createSalesRequest;

    @BeforeEach
    void setUp() {
        salesBusinessRules = new SalesBusinessRules(campaignRepository, webClientBuilder);
        sales = new Sales();
        createSalesRequest = new CreateSalesRequest();
    }

    @Test
    public void testValidatePaymentType_WithPaymentTypeInRequest() {
        createSalesRequest.setPaymentType(PaymentType.CASH);
        createSalesRequest.setCreateSalesItemsRequests(Collections.emptyList());

        salesBusinessRules.validatePaymentType(sales, createSalesRequest);

        assertEquals(PaymentType.CASH, sales.getPaymentType());
    }

    @Test
    public void testValidatePaymentType_WithPaymentTypeInItems() {
        CreateSalesItemsRequest itemRequest = new CreateSalesItemsRequest();
        itemRequest.setPaymentType(PaymentType.CARD);
        createSalesRequest.setCreateSalesItemsRequests(Collections.singletonList(itemRequest));
        createSalesRequest.setPaymentType(null);

        salesBusinessRules.validatePaymentType(sales, createSalesRequest);

        assertNull(sales.getPaymentType());
    }

    @Test
    public void testValidatePaymentType_ThrowsPaymentTypeNotEnteredException_WhenNoPaymentType() {
        createSalesRequest.setPaymentType(null);
        CreateSalesItemsRequest createSalesItemsRequest = new CreateSalesItemsRequest();
        createSalesItemsRequest.setPaymentType(null);
        createSalesRequest.setCreateSalesItemsRequests(Collections.singletonList(createSalesItemsRequest));

        Exception exception = assertThrows(PaymentTypeNotEnteredException.class, () -> salesBusinessRules.validatePaymentType(sales, createSalesRequest));

        assertEquals("Payment type not entered", exception.getMessage());
    }

    @Test
    public void testValidatePaymentType_DoesNotThrowException_WhenPaymentTypeIsPresentInRequest() {
        createSalesRequest.setPaymentType(PaymentType.CARD);
        createSalesRequest.setCreateSalesItemsRequests(Collections.emptyList());

        assertDoesNotThrow(() -> salesBusinessRules.validatePaymentType(sales, createSalesRequest));
    }

    @Test
    public void testExtractProductBarcodeNumber_Success() {
        String errorBody = "{\"message\": \"Product not found: 1234567890123\"}";
        String expectedBarcode = "1234567890123";

        String actualBarcode = salesBusinessRules.extractProductBarcodeNumber(errorBody);

        assertEquals(expectedBarcode, actualBarcode);
    }

    @Test
    public void testExtractProductBarcodeNumber_UnknownProduct() {
        String errorBody = "{\"message\": \"Some other error\"}";
        String expectedBarcode = "Unknown Product";

        String actualBarcode = salesBusinessRules.extractProductBarcodeNumber(errorBody);

        assertEquals(expectedBarcode, actualBarcode);
    }

    @Test
    public void testExtractProductBarcodeNumber_InvalidJson() {
        String errorBody = "Invalid JSON";
        String expectedBarcode = "Unknown Product";

        String actualBarcode = salesBusinessRules.extractProductBarcodeNumber(errorBody);

        assertEquals(expectedBarcode, actualBarcode);
    }

    @Test
    public void testUpdateSalesItems() {
        Sales sales = new Sales();
        sales.setSalesNumber("12345");
        sales.setPaymentType(PaymentType.CARD);

        List<SalesItems> salesItems = getSalesItemsList();

        List<InventoryRequest> inventoryRequests = Collections.singletonList(new InventoryRequest("barcode1", 5));

        salesBusinessRules.updateSalesItems(sales, salesItems, inventoryRequests);

        assertEquals(PaymentType.CARD, salesItems.get(0).getPaymentType());
        assertEquals(PaymentType.CARD, salesItems.get(1).getPaymentType());
    }

    private static List<SalesItems> getSalesItemsList() {
        SalesItems salesItem1 = new SalesItems();
        salesItem1.setBarcodeNumber("barcode1");
        salesItem1.setName("Product 1");
        salesItem1.setQuantity(5);
        salesItem1.setUnitPrice(10.0);
        salesItem1.setState(true);

        SalesItems salesItem2 = new SalesItems();
        salesItem2.setBarcodeNumber("barcode2");
        salesItem2.setName("Product 2");
        salesItem2.setQuantity(8);
        salesItem2.setUnitPrice(15.0);
        salesItem2.setState(true);

        return Arrays.asList(salesItem1, salesItem2);
    }

    @Test
    public void testApplyCampaignDiscount_BuyXPayY() {
        SalesItems salesItem = new SalesItems();
        salesItem.setName("Product 1");
        salesItem.setQuantity(6);
        salesItem.setUnitPrice(10.0);

        Campaign campaign = new Campaign();
        campaign.setCampaignType(1);
        campaign.setBuyPayPartOne(3);
        campaign.setBuyPayPartTwo(2);

        salesItem.setCampaign(campaign);

        salesBusinessRules.applyCampaignDiscount(salesItem);

        assertEquals(40.0, salesItem.getTotalPrice());
    }

    @Test
    public void testApplyCampaignDiscount_PercentageDiscount() {
        SalesItems salesItem = new SalesItems();
        salesItem.setName("Product 2");
        salesItem.setQuantity(5);
        salesItem.setUnitPrice(20.0);

        Campaign campaign = new Campaign();
        campaign.setCampaignType(2);
        campaign.setPercent(20);

        salesItem.setCampaign(campaign);

        salesBusinessRules.applyCampaignDiscount(salesItem);

        assertEquals(80.0, salesItem.getTotalPrice());
    }

    @Test
    public void testApplyCampaignDiscount_MoneyDiscount() {
        SalesItems salesItem = new SalesItems();
        salesItem.setName("Product 3");
        salesItem.setQuantity(3);
        salesItem.setUnitPrice(50.0);

        Campaign campaign = new Campaign();
        campaign.setCampaignType(3);
        campaign.setMoneyDiscount(30);

        salesItem.setCampaign(campaign);

        salesBusinessRules.applyCampaignDiscount(salesItem);

        assertEquals(120.0, salesItem.getTotalPrice());
    }

    @Test
    public void testApplyCampaignDiscount_UnknownCampaignType() {
        SalesItems salesItem = new SalesItems();
        salesItem.setName("Product 4");
        salesItem.setQuantity(4);
        salesItem.setUnitPrice(25.0);

        Campaign campaign = new Campaign();
        campaign.setCampaignType(4);

        salesItem.setCampaign(campaign);

        salesBusinessRules.applyCampaignDiscount(salesItem);

        assertNull(salesItem.getTotalPrice());
    }

    @Test
    public void testCalculateTotalMoney() {
        CreateSalesRequest createSalesRequest = new CreateSalesRequest();
        createSalesRequest.setCreatedBy("Asaf");
        createSalesRequest.setMoney(100.0);

        SalesItems cardPaymentItem = new SalesItems();
        cardPaymentItem.setName("Product 1");
        cardPaymentItem.setTotalPrice(50.0);
        cardPaymentItem.setPaymentType(PaymentType.CARD);

        SalesItems cashPaymentItem = new SalesItems();
        cashPaymentItem.setName("Product 2");
        cashPaymentItem.setPaymentType(PaymentType.CASH);

        List<SalesItems> salesItems = Arrays.asList(cardPaymentItem, cashPaymentItem);

        List<InventoryRequest> inventoryRequests = Collections.singletonList(new InventoryRequest("barcode1", 5));

        double totalMoney = salesBusinessRules.calculateTotalMoney(createSalesRequest, salesItems, inventoryRequests);

        assertEquals(150.0, totalMoney);
    }

    @Test
    public void testValidateReturnPeriod_ReturnPeriodExpired() {
        LocalDateTime salesDate = LocalDateTime.of(2024, Month.JUNE, 1, 10, 0);
        LocalDateTime returnDate = LocalDateTime.of(2024, Month.JUNE, 17, 10, 0);

        assertThrows(ReturnPeriodExpiredException.class, () -> salesBusinessRules.validateReturnPeriod(salesDate, returnDate));
    }
}
