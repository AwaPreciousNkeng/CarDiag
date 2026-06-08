package com.codewithpcodes.cardiag.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserResponse {

    private Integer id;
    private String email;
    private String fullName;
    private String profilePictureUrl;
    private Role role;
    private OAuthProvider oAuthProvider;
    private OffsetDateTime createdAt;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .profilePictureUrl(user.getProfilePictureUrl())
                .role(user.getRole())
                .oAuthProvider(user.getOAuthProvider())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
