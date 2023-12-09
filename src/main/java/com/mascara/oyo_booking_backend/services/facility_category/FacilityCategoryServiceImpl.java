package com.mascara.oyo_booking_backend.services.facility_category;

import com.mascara.oyo_booking_backend.dtos.BaseMessageData;
import com.mascara.oyo_booking_backend.dtos.request.facility_category.AddFacilityCategoryRequest;
import com.mascara.oyo_booking_backend.dtos.request.facility_category.UpdateFacilityCategoryRequest;
import com.mascara.oyo_booking_backend.dtos.response.facility.GetFacilityResponse;
import com.mascara.oyo_booking_backend.dtos.response.facility_category.GetFacilityCategorWithFacilityListResponse;
import com.mascara.oyo_booking_backend.dtos.response.facility_category.GetFacilityCategoryResponse;
import com.mascara.oyo_booking_backend.dtos.response.paging.BasePagingData;
import com.mascara.oyo_booking_backend.entities.Facility;
import com.mascara.oyo_booking_backend.entities.FacilityCategories;
import com.mascara.oyo_booking_backend.enums.CommonStatusEnum;
import com.mascara.oyo_booking_backend.exceptions.ResourceNotFoundException;
import com.mascara.oyo_booking_backend.repositories.FacilityCategoriesRepository;
import com.mascara.oyo_booking_backend.utils.AliasUtils;
import com.mascara.oyo_booking_backend.utils.AppContants;
import com.mascara.oyo_booking_backend.utils.GenerateCodeUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by: IntelliJ IDEA
 * User      : boyng
 * Date      : 22/10/2023
 * Time      : 7:37 CH
 * Filename  : FacilAccomCategoryServiceImpl
 */
@Service
public class FacilityCategoryServiceImpl implements FacilityCategoryService {
    @Autowired
    private FacilityCategoriesRepository facilityCategoriesRepository;

    @Autowired
    private ModelMapper mapper;

    @Override
    @Transactional
    public BasePagingData<GetFacilityCategorWithFacilityListResponse> getAllFacilityCategoryWithPaging(Integer pageNum, Integer pageSize, String sortType, String field) {
        Pageable paging = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.valueOf(sortType), field));
        Page<FacilityCategories> facilityCategoriesPage = facilityCategoriesRepository.getAllWithPaging(paging);

        List<FacilityCategories> facilityCategoriesList = facilityCategoriesPage.stream().toList();
        List<GetFacilityCategorWithFacilityListResponse> responses = facilityCategoriesList.stream()
                .map(facilityCate -> mapper.map(facilityCate,
                        GetFacilityCategorWithFacilityListResponse.class)).collect(Collectors.toList());
        for (int i = 0; i < facilityCategoriesList.size(); i++) {
            List<Facility> facilities = facilityCategoriesList.get(i).getFacilitySet().stream().toList();
            List<GetFacilityResponse> infoFacilityResponseList = facilities.stream().map(facility -> mapper.map(facility, GetFacilityResponse.class))
                    .collect(Collectors.toList());
            responses.get(i).setInfoFacilityList(infoFacilityResponseList);
        }
        return new BasePagingData<>(responses,
                facilityCategoriesPage.getNumber(),
                facilityCategoriesPage.getSize(),
                facilityCategoriesPage.getTotalElements());
    }

    @Override
    @Transactional
    public List<GetFacilityCategorWithFacilityListResponse> getAllDataFacility() {
        List<FacilityCategories> facilityCategoriesList = facilityCategoriesRepository.findAll();
        List<GetFacilityCategorWithFacilityListResponse> facilityCategoryResponses = facilityCategoriesList.stream()
                .map(facilityCate -> mapper.map(facilityCate, GetFacilityCategorWithFacilityListResponse.class))
                .collect(Collectors.toList());
        for (int i = 0; i < facilityCategoriesList.size(); i++) {
            List<Facility> facilities = facilityCategoriesList.get(i).getFacilitySet().stream().toList();
            List<GetFacilityResponse> infoFacilityResponseList = facilities.stream().map(facility -> mapper.map(facility, GetFacilityResponse.class))
                    .collect(Collectors.toList());
            facilityCategoryResponses.get(i).setInfoFacilityList(infoFacilityResponseList);
        }
        return facilityCategoryResponses;
    }

    @Override
    @Transactional
    public GetFacilityCategoryResponse addFacilityCategory(AddFacilityCategoryRequest request) {
        int count = (int) facilityCategoriesRepository.count();
        FacilityCategories facilityCategories = FacilityCategories.builder()
                .faciCateName(request.getFaciCateName())
                .faciCateCode(GenerateCodeUtils.generateCode(AliasUtils.FACILITY_CATEGORY, count))
                .description(request.getDescription())
                .status(CommonStatusEnum.valueOf(request.getStatus()))
                .build();
        facilityCategories = facilityCategoriesRepository.save(facilityCategories);
        return mapper.map(facilityCategories, GetFacilityCategoryResponse.class);
    }


    @Override
    @Transactional
    public GetFacilityCategoryResponse updateFacilityCategory(UpdateFacilityCategoryRequest request, String facilCateCode) {
        FacilityCategories facilityCategories = facilityCategoriesRepository.findByFaciCateCode(facilCateCode)
                .orElseThrow(() -> new ResourceNotFoundException(AppContants.NOT_FOUND_MESSAGE("Facility category")));
        facilityCategories.setFaciCateName(request.getFaciCateName());
        facilityCategories.setStatus(CommonStatusEnum.valueOf(request.getStatus()));
        facilityCategories.setDescription(request.getDescription());
        facilityCategories = facilityCategoriesRepository.save(facilityCategories);
        return mapper.map(facilityCategories, GetFacilityCategoryResponse.class);
    }

    @Override
    @Transactional
    public BaseMessageData changeStatusFacilityCategory(String facilCateCode, String status) {
        FacilityCategories facilityCategories = facilityCategoriesRepository.findByFaciCateCode(facilCateCode)
                .orElseThrow(() -> new ResourceNotFoundException(AppContants.NOT_FOUND_MESSAGE("Facility category")));
        facilityCategories.setStatus(CommonStatusEnum.valueOf(status));
        facilityCategoriesRepository.save(facilityCategories);
        return new BaseMessageData(AppContants.UPDATE_SUCCESS_MESSAGE("Facility category"));
    }

    @Override
    @Transactional
    public BaseMessageData deletedFacilityCategory(String facilCateCode) {
        FacilityCategories facilityCategories = facilityCategoriesRepository.findByFaciCateCode(facilCateCode)
                .orElseThrow(() -> new ResourceNotFoundException(AppContants.NOT_FOUND_MESSAGE("Facility category")));
        facilityCategories.setDeleted(true);
        facilityCategoriesRepository.save(facilityCategories);
        return new BaseMessageData(AppContants.DELETE_SUCCESS_MESSAGE("Facility category"));
    }
}