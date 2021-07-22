package com.hoangducduy.airbnb.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private List<String> roles;

    public JwtResponse(String accessToken, Long id, String username, String fullName, String email, List<String> roles) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.roles = roles;
    }
}
