package com.toyota.salesservice.dto.requests;

import java.time.LocalDate;

public class UpdateCampaignRequest {
    private Long id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
}
