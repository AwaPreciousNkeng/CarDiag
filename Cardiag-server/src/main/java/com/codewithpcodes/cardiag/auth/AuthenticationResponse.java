package com.codewithpcodes.cardiag.auth;

import com.codewithpcodes.cardiag.user.UserResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthenticationResponse(
        @JsonProperty("access_token")
        String accessToken,

        UserResponse user,

        @JsonProperty("refresh_token")
        String refreshToken
) {

        public static AuthenticationResponse fromAuth(
                String accessToken,
                String refreshToken,
                UserResponse user
        ) {
                return new AuthenticationResponse(
                        accessToken,
                        user,
                        refreshToken
                );
        }
}
