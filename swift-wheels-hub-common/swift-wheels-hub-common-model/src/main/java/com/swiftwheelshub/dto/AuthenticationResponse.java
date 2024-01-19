package com.swiftwheelshub.dto;

public record AuthenticationResponse(String token) {

    @Override
    public String toString() {
        return "AuthenticationResponse{" + "\n" +
                "token='" + token + "\n" +
                "}";
    }

}
