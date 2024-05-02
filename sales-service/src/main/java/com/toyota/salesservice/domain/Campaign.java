package com.toyota.salesservice.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "campaign")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Campaign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    private Long id;

    @Column(name = "barcode_number", unique = true, nullable = false)
    private String campaignNumber;

    @Column(name = "name")
    private String name;

    @Column(name = "buy_pay")
    private String buyPay;

    @Column(name = "percent")
    private String percent;

    @Column(name = "money_discount")
    private String moneyDiscount;

    @Column(name = "state", columnDefinition = "BOOLEAN DEFAULT false", nullable = false)
    private Boolean state;

    @Column(name = "created_by")
    private String createdBy;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "campaign", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<SalesItems> salesItemsList;
}
