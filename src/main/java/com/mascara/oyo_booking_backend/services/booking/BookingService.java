package com.mascara.oyo_booking_backend.services.booking;

import com.mascara.oyo_booking_backend.dtos.base.BaseMessageData;
import com.mascara.oyo_booking_backend.dtos.booking.request.BookingRequest;
import com.mascara.oyo_booking_backend.dtos.booking.request.CancelBookingRequest;
import com.mascara.oyo_booking_backend.dtos.booking.request.CheckBookingRequest;
import com.mascara.oyo_booking_backend.dtos.booking.response.CheckBookingResponse;
import com.mascara.oyo_booking_backend.dtos.booking.response.GetBookingResponse;
import com.mascara.oyo_booking_backend.dtos.booking.response.GetHistoryBookingResponse;
import com.mascara.oyo_booking_backend.dtos.base.BasePagingData;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by: IntelliJ IDEA
 * User      : boyng
 * Date      : 22/10/2023
 * Time      : 7:44 CH
 * Filename  : BookingService
 */
public interface BookingService {
    @Transactional
    BaseMessageData createOrderBookingAccom(BookingRequest request, String userMail);

    @Transactional
    CheckBookingResponse checkBookingToGetPrice(CheckBookingRequest request);

    @Transactional
    BasePagingData<GetBookingResponse> getListBookingOfPartner(String hostMail,
                                                              String status,
                                                              Integer pageNum,
                                                              Integer pageSize,
                                                              String sortType,
                                                              String field);

    @Transactional
    BasePagingData<GetHistoryBookingResponse> getHistoryBookingUser(String userMail,
                                                                    Integer pageNum,
                                                                    Integer pageSize,
                                                                    String sortType,
                                                                    String field);

    @Transactional
    BaseMessageData changeStatusBookingByHost(String hostMail, String bookingCode, String status);

    @Transactional
    BaseMessageData cancelBooking(String userMail, CancelBookingRequest request);

//    @Transactional
//    BaseMessageData changeStatusBookingByUser(String userMail, String bookingCode, String status);
}
