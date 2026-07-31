package com.pm.authservice.dto;

public class LoginResponseDTO {
    private final String token;

    public LoginResponseDTO(String token) {
        this.token = token;
    }

    //getter to serialize object into JSON
    public String getToken() {
        return token;
    }
}
