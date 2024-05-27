package com.toyota.salesservice.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SalesItems class represents a sales_items entity in the database.
 */

@Entity
@Table(name = "sales_items")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesItems {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    private Long id;

    @Column(name = "barcode_number", nullable = false)
    private String barcodeNumber;

    @Column(name = "name")
    private String name;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "unit_price", columnDefinition = "numeric")
    private Double unitPrice;

    @Column(name = "state", nullable = false)
    private Boolean state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;

    @Column(name = "total_price")
    private Double totalPrice;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "payment_type")
    private PaymentType paymentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_id")
    private Sales sales;
}
