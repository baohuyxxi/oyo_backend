package com.mascara.oyo_booking_backend.entities;

import com.mascara.oyo_booking_backend.enums.CommonStatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.math.BigDecimal;
import java.util.Set;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "accom_place")
public class AccomPlace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(name = "accom_name", columnDefinition = "NVARCHAR(255) NOT NULL")
    private String accomName;

    @Column(name = "description")
    private String description;

    @Column(name = "address_detail")
    private String addressDetail;

    @Column(name = "grade_rate")
    private Float gradeRate;

    @Column(name = "num_review")
    private Long numReview;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_association_accom_user"),
            nullable = false,
            insertable = false,
            updatable = false
    )
    private User user;

    @Column(name = "user_id")
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "accom_cate_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_association_accom_accom_cate"),
            nullable = false,
            insertable = false,
            updatable = false
    )
    private AccommodationCategories accommodationCategories;

    @Column(name = "accom_cate_id")
    private Long accomCateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "province_code",
            referencedColumnName = "province_code",
            foreignKey = @ForeignKey(name = "fk_association_accom_province"),
            nullable = false,
            insertable = false,
            updatable = false
    )
    private Province province;

    @Column(name = "province_code")
    private String provinceCode;

    @Column(name = "district_code")
    private String districtCode;

    @Column(name = "ward_code")
    private String wardCode;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "accomPlace")
    @Fetch(FetchMode.SUBSELECT)
    private Set<ImageAccom> imageAccoms;

    @Column(name = "acreage")
    private Float acreage;

    @Column(name = "num_people")
    private Integer numPeople;

    @Column(name = "num_bathroom")
    private Integer numBathRoom;

    @Column(name = "num_bed")
    private Integer numBed;

    @Column(name = "price_per_night")
    private BigDecimal pricePerNight;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CommonStatusEnum status;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "accomPlace")
    @Fetch(FetchMode.SUBSELECT)
    private Set<Review> reviewSet;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "accomPlace")
    @Fetch(FetchMode.SUBSELECT)
    private Set<WishItem> wishItemSet;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "accomPlace")
    @Fetch(FetchMode.SUBSELECT)
    private Set<Booking> bookingSet;

    @OneToOne(mappedBy = "accomPlace", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CartItem cartItem;

    @ManyToMany(mappedBy = "accomPlaceSet", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Facility> facilitySet;
}
