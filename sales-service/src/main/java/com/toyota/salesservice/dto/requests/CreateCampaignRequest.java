package com.toyota.salesservice.dto.requests;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class CreateCampaignRequest {
    @NotNull
    private String name;
    @NotNull
    private LocalDate startDate;
    @NotNull
    private LocalDate endDate;
}
