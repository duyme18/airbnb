package com.hoangducduy.airbnb.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class LoginRequest {
    @NotNull(message = "username not null")
    @NotBlank(message = "username not blank")
    private String username;

    @NotNull(message = "password not null")
    @NotBlank(message = "password not blank")
    private String password;

}
