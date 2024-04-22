package com.toyota.salesservice.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "sales")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Sales {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    private Long id;

    @Column(name = "sales_number", unique = true, nullable = false)
    private String salesNumber = UUID.randomUUID().toString().substring(0, 8);

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price", columnDefinition = "numeric")
    private Double price;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Column(name = "sales_date")
    private LocalDateTime salesDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;
}
