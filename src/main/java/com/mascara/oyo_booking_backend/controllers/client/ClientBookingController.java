package com.mascara.oyo_booking_backend.controllers.client;

import com.mascara.oyo_booking_backend.dtos.BaseMessageData;
import com.mascara.oyo_booking_backend.dtos.request.booking.BookingRequest;
import com.mascara.oyo_booking_backend.dtos.response.BaseResponse;
import com.mascara.oyo_booking_backend.services.booking.BookingService;
import com.mascara.oyo_booking_backend.utils.AppContants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * Created by: IntelliJ IDEA
 * User      : boyng
 * Date      : 28/11/2023
 * Time      : 7:53 CH
 * Filename  : ClientBookingController
 */
@Tag(name = "Client booking", description = "CLient booking APIs")
@RestController
@RequestMapping("/api/v1/client/booking")
@RequiredArgsConstructor
public class ClientBookingController {
    @Autowired
    private BookingService bookingService;

    @Operation(summary = "Check accom place is in wish list or not", description = "Client Api for check accom place is in wish list or not")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = BaseResponse.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})})
    @PostMapping("/create")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<?> createOrderBooking(@RequestBody @Valid BookingRequest request) {
        Principal principal = SecurityContextHolder.getContext().getAuthentication();
        String userMail = principal.getName();
        BaseMessageData response = bookingService.createOrderBookingAccom(request, userMail);
        if (response.getMessage().equals(AppContants.BOOKING_NOT_AVAILABLE(
                request.getAccomId(),
                request.getCheckIn().toString(),
                request.getCheckOut().toString())))
            return ResponseEntity.status(410).body(new BaseResponse<>(true, 410, response));
        return ResponseEntity.ok(new BaseResponse<>(true, 200, response));
    }
}
