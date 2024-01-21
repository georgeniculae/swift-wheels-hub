package com.swiftwheelshub.dto;

import lombok.Builder;

@Builder
public record AuthenticationResponse(String token) {

    @Override
    public String toString() {
        return "AuthenticationResponse{" + "\n" +
                "token='" + token + "\n" +
                "}";
    }

}
