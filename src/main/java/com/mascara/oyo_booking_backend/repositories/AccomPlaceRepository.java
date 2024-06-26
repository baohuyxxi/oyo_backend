package com.mascara.oyo_booking_backend.repositories;

import com.mascara.oyo_booking_backend.entities.accommodation.AccomPlace;
import com.mascara.oyo_booking_backend.enums.AccomStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Created by: IntelliJ IDEA
 * User      : boyng
 * Date      : 01/10/2023
 * Time      : 3:32 CH
 * Filename  : AccomPlaceRepository
 */
@Repository
public interface AccomPlaceRepository extends JpaRepository<AccomPlace, Long>, JpaSpecificationExecutor<AccomPlace> {

    @Query(value = "SELECT distinct ap.* FROM accom_place ap LEFT JOIN facility_accom fa " +
            "ON ap.id = fa.accom_id LEFT JOIN facility f ON fa.facility_id = f.id LEFT JOIN accommodation_categories ac on  ap.accom_cate_id = ac.id" +
            " where (:accomCateName is null or ac.accom_cate_name = :accomCateName) and (:provinceCode is null or ap.province_code = :provinceCode) AND " +
            "(:districtCode is null or ap.district_code = :districtCode) " +
            "AND (:wardCode is null or ap.ward_code = :wardCode) AND (:priceFrom is null or :priceTo is null or " +
            "(ap.price_per_night BETWEEN :priceFrom AND :priceTo)) " +
            "AND IF(:size > 0, f.faci_code IN :facilityCode,true) AND " +
            "(:numBathroom is null or ap.num_bathroom = :numBathroom) " +
            "AND (:numPeople is null or ap.num_people = :numPeople) AND " +
            "(:numBedRoom is null or ap.num_bed_room = :numBedRoom) and ap.status = 'APPROVED' and ap.deleted = false group by ap.id having (:size > 0 AND COUNT(DISTINCT f.faci_code) = :size) OR (:size = 0)",
            countQuery = "SELECT distinct count(ap.id) FROM accom_place ap LEFT JOIN facility_accom fa ON " +
                    "ap.id = fa.accom_id LEFT JOIN facility f ON " + "fa.facility_id = f.id " +
                    "WHERE (:provinceCode is null or ap.province_code = :provinceCode) AND " +
                    "(:districtCode is null or ap.district_code = :districtCode) " +
                    "AND (:wardCode is null or ap.ward_code = :wardCode) AND (:priceFrom is null or " +
                    ":priceTo is null or (ap.price_per_night BETWEEN :priceFrom AND :priceTo)) " +
                    "AND IF(:size > 0, f.faci_code IN :facilityCode,true) AND (:numBathroom is " +
                    "null or ap.num_bathroom = :numBathroom) AND (:numPeople is null or ap.num_people = :numPeople) " +
                    "AND (:numBedRoom is null or ap.num_bed_room = :numBedRoom) and ap.status = 'APPROVED' and ap.deleted = false group by ap.id having (:size > 0 AND COUNT(DISTINCT f.faci_code) = :size) OR (:size = 0)",
            nativeQuery = true)
    Page<AccomPlace> getPageWithFullFilter(
            @Param("accomCateName") String accomCateName,
            @Param("provinceCode") String provinceCode,
            @Param("districtCode") String districtCode,
            @Param("wardCode") String wardCode,
            @Param("priceFrom") Double priceFrom,
            @Param("priceTo") Double priceTo,
            @Param("facilityCode") List<String> facilityName,
            @Param("size") Integer size,
            @Param("numBathroom") Integer numBathroom,
            @Param("numPeople") Integer numPeople,
            @Param("numBedRoom") Integer numBedRoom,
            Pageable pageable);

    @Query(value = "select ap.* from accom_place ap, province p where ap.province_code = p.province_code " +
            "and (upper(ap.accom_name) like upper(CONCAT('%', :keyword,'%')) OR " +
            "upper(p.province_name) like upper(CONCAT('%', :keyword,'%')))",
            countQuery = "select count(ap.id) from accom_place ap, province p where ap.province_code = p.province_code " +
                    "and (upper(ap.accom_name) like upper(CONCAT('%', :keyword,'%')) OR " +
                    "upper(p.province_name) like upper(CONCAT('%', :keyword,'%')))", nativeQuery = true)
    Page<AccomPlace> getFilterByKeyWord(@Param("keyword") String keyword, Pageable pageable);

    @Query(value = "select ap.* from accom_place ap where ap.id = :id and ap.deleted = false", nativeQuery = true)
    Optional<AccomPlace> findById(@Param("id") Long id);

    @Query(value = "select ap.* from accom_place ap limit 1", nativeQuery = true)
    List<AccomPlace> checkExistData();

    @Query(value = "select ap.* from accom_place ap where ap.deleted = false and ap.status = 'APPROVED'",
            countQuery = "select count(id) from accom_place ap where ap.deleted = false and ap.status = 'APPROVED'",
            nativeQuery = true)
    Page<AccomPlace> getAllWithPaging(Pageable pageable);

    @Query(value = "select ap.* from accom_place ap where ap.user_id = :host_id and ap.deleted = false and ap.status= :status",
            countQuery = "select count(id) from accom_place ap where ap.user_id = :host_id and ap.deleted = false and ap.status= :status",
            nativeQuery = true)
    Page<AccomPlace> getListAccomPlaceOfPartner(@Param("host_id") Long hostId,
                                                @Param("status") String status,
                                                Pageable pageable);


    @Query(value = "select ap.* from accom_place ap join wish_item wi on ap.id = wi.accom_id where wi.wish_id = :wish_id and wi.deleted is false",
            countQuery = "select count(ap.id) from accom_place ap join wish_item wi on ap.id = wi.accom_id where wi.wish_id = :wish_id and wi.deleted is false",
            nativeQuery = true)
    Page<AccomPlace> getListAccomPlaceOfWishListUser(@Param("wish_id") Long wishListId, Pageable pageable);

    @Modifying
    @Query(value = "update accom_place ap set ap.status = :status where ap.id = :id", nativeQuery = true)
    void changeStatusAccomPlace(@Param("id") Long id, @Param("status") String status);
}
