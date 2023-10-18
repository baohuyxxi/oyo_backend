package com.mascara.oyo_booking_backend.dtos.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by: IntelliJ IDEA
 * User      : boyng
 * Date      : 17/10/2023
 * Time      : 9:39 CH
 * Filename  : CheckMailRequest
 */
@Setter
@Getter
public class CheckMailRequest {
    @NotBlank
    @Size(max = 50)
    @Email
    private String email;
}
