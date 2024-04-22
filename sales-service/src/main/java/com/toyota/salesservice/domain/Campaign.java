package com.toyota.salesservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

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

    @Column(name = "campaign_number", unique = true, nullable = false)
    private String campaignNumber = UUID.randomUUID().toString().substring(0, 8);

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @OneToMany(mappedBy = "campaign", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    List<Sales> salesList;
}
