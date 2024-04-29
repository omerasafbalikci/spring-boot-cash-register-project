package com.toyota.salesservice.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Column(name = "barcode_number", unique = true, nullable = false)
    private String barcodeNumber;

    @Column(name = "sku_code")
    private String skuCode;

    @Column(name = "quantity")
    private Integer quantity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sales_id", nullable = false)
    private Sales sales;
}
