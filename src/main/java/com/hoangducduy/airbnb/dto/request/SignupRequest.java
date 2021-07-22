package com.hoangducduy.airbnb.dto.request;

import lombok.Data;

import java.util.Set;

@Data
public class SignupRequest {

    private String username;

    private String fullName;

    private String phone;

    private String email;

    private String password;

    private Set<String> role;

}
