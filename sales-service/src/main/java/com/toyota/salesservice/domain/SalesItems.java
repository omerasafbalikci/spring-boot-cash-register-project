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
    @Column(name = "sku_code")
    private String skuCode;
    @Column(name = "price", columnDefinition = "numeric")
    private Double price;
    @Column(name = "quantity")
    private Integer quantity;
}
