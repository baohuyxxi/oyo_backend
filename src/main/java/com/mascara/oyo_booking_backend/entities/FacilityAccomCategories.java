package com.mascara.oyo_booking_backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.UUID;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name="facility_accom_categories")
public class FacilityAccomCategories {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "faci_accom_cate_name")
    private String faciAccomCateName;
}
