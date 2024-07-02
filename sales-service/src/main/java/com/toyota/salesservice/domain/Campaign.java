package com.toyota.salesservice.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Campaign class represents a campaign entity in the database.
 */

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
    private String campaignNumber;

    @Column(name = "name")
    private String name;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "campaign_category")
    private CampaignType campaignCategory;

    @Column(name = "campaign_key((b,p), %, tl)")
    private String campaignKey;

    @Column(name = "campaign_type")
    private Integer campaignType;

    @Column(name = "state", columnDefinition = "BOOLEAN DEFAULT false", nullable = false)
    private Boolean state;

    @Column(name = "created_by")
    private String createdBy;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "campaign", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<SalesItems> salesItemsList;
}
