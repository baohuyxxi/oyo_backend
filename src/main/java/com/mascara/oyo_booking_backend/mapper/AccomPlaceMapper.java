package com.mascara.oyo_booking_backend.mapper;

import com.mascara.oyo_booking_backend.dtos.response.accommodation.GetAccomPlaceResponse;
import com.mascara.oyo_booking_backend.dtos.response.facility.GetFacilityCategoryResponse;
import com.mascara.oyo_booking_backend.dtos.response.facility.GetFacilityResponse;
import com.mascara.oyo_booking_backend.entities.*;
import com.mascara.oyo_booking_backend.exceptions.ResourceNotFoundException;
import com.mascara.oyo_booking_backend.repositories.AccomPlaceRepository;
import com.mascara.oyo_booking_backend.repositories.DistrictRepository;
import com.mascara.oyo_booking_backend.repositories.FacilityCategoriesRepository;
import com.mascara.oyo_booking_backend.repositories.ProvinceRepository;
import com.mascara.oyo_booking_backend.utils.AppContants;
import jakarta.annotation.PostConstruct;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by: IntelliJ IDEA
 * User      : boyng
 * Date      : 27/11/2023
 * Time      : 5:53 CH
 * Filename  : AccomPlaceMapper
 */
@Component
public class AccomPlaceMapper {
    @Autowired
    private ModelMapper mapper;

    @Autowired
    private AccomPlaceRepository accomPlaceRepository;

    @Autowired
    private ProvinceRepository provinceRepository;

    @Autowired
    private DistrictRepository districtRepository;

    @Autowired
    private FacilityCategoriesRepository facilityCategoriesRepository;

    //    Covert Address General
    private final Converter<Long, String> idAccomPlaceToAddressGeneral = context -> {
        Long id = context.getSource();
        AccomPlace accomPlace = accomPlaceRepository.findById(id).get();
        Province province = provinceRepository.findByProvinceCode(accomPlace.getProvinceCode())
                .orElseThrow(() -> new ResourceNotFoundException(AppContants.NOT_FOUND_MESSAGE("province")));
        District district = districtRepository.findByDistrictCode(accomPlace.getDistrictCode())
                .orElseThrow(() -> new ResourceNotFoundException(AppContants.NOT_FOUND_MESSAGE("district")));
        String addressGeneral = district.getDistrictName() + ", " + province.getProvinceName();
        return id != null ? addressGeneral : null;
    };

    //    Covert Image Accom
    private final Converter<Set<ImageAccom>, List<String>> imageAccomToImageAccomUrl = context -> {
        Set<ImageAccom> imageAccomSet = context.getSource();
        if (imageAccomSet != null) {
            List<String> imageAccomUrl = new ArrayList();
            for (ImageAccom imgAccom : imageAccomSet) {
                imageAccomUrl.add(imgAccom.getImgAccomLink());
            }
            return imageAccomUrl;
        }
        return null;
    };

    //    Convert accom category
    private final Converter<AccommodationCategories, String> accomCategoryToAccomCategoryName = context -> {
        AccommodationCategories accommodationCategories = context.getSource();
        return accommodationCategories != null ? accommodationCategories.getAccomCateName() : null;
    };

    //    Convert Set Facility
    private final Converter<Set<Facility>, List<GetFacilityCategoryResponse>> setFacilityToFacilityCateDetails = context -> {
        Set<Facility> facilitySet = context.getSource();
        if (facilitySet != null) {
            List<GetFacilityResponse> facilityList = new ArrayList<>();
            List<GetFacilityCategoryResponse> facilityCategoryList = new ArrayList();
            for (Facility facility : facilitySet) {
                GetFacilityResponse facilityResponse = mapper.map(facility, GetFacilityResponse.class);
                facilityResponse.setStatus(facility.getStatus().toString());
                facilityList.add(facilityResponse);
            }
            for (GetFacilityResponse facilityResponse : facilityList) {
                boolean addCate = true;
                for (GetFacilityCategoryResponse facilityCategoryResponse : facilityCategoryList) {
                    if (facilityResponse.getFacilityCateCode().equals(facilityCategoryResponse.getFaciCateCode())) {
                        addCate = false;
                        break;
                    }
                }
                if (addCate == true) {
                    FacilityCategories facilityCategories = facilityCategoriesRepository.findByFaciCateCode(facilityResponse
                            .getFacilityCateCode()).get();
                    GetFacilityCategoryResponse faciCateResponse = mapper.map(facilityCategories, GetFacilityCategoryResponse.class);
                    faciCateResponse.setStatus(facilityCategories.getStatus().toString());
                    facilityCategoryList.add(faciCateResponse);
                }
            }

            for (GetFacilityCategoryResponse faciCate : facilityCategoryList) {
                List<GetFacilityResponse> facilityResponseList = new ArrayList<>();
                for (GetFacilityResponse facility : facilityList) {
                    if (faciCate.getFaciCateCode().equals(facility.getFacilityCateCode()))
                        facilityResponseList.add(facility);
                }
                faciCate.setInfoFacilityList(facilityResponseList);
            }
            return facilityCategoryList;
        }
        return null;
    };

    @PostConstruct
    public void init() {
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        mapper.createTypeMap(AccomPlace.class, GetAccomPlaceResponse.class)
                .addMappings(mapper -> mapper.using(idAccomPlaceToAddressGeneral)
                        .map(AccomPlace::getId, GetAccomPlaceResponse::setAddressGeneral))

                .addMappings(mapper -> mapper.using(imageAccomToImageAccomUrl)
                        .map(AccomPlace::getImageAccoms, GetAccomPlaceResponse::setImageAccomsUrls))

                .addMappings(mapper -> mapper.using(setFacilityToFacilityCateDetails)
                        .map(AccomPlace::getFacilitySet, GetAccomPlaceResponse::setFacilityCategoryList))

                .addMappings(mapper -> mapper.using(accomCategoryToAccomCategoryName)
                        .map(AccomPlace::getAccommodationCategories, GetAccomPlaceResponse::setAccomCateName));

    }

    public GetAccomPlaceResponse toGetAccomPlaceResponse(AccomPlace accomPlace) {
        GetAccomPlaceResponse accomPlaceResponse;
        accomPlaceResponse = mapper.map(accomPlace, GetAccomPlaceResponse.class);
        return accomPlaceResponse;
    }
}
