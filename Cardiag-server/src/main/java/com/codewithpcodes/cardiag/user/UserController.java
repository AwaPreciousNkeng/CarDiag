package com.codewithpcodes.cardiag.user;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "User Management endpoints")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getProfile(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(userService.getProfile(currentUser.getId()));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateProfile(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        return ResponseEntity.ok(userService.updateProfile(currentUser.getId(), request));
    }

    @PostMapping("/me/profile-picture")
    public ResponseEntity<UserResponse> uploadProfilePicture(
            @AuthenticationPrincipal User currentUser,
            @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity.ok(userService.uploadProfilePicture(currentUser.getId(), file));
    }
}
