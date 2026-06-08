package com.codewithpcodes.cardiag.user;

import com.codewithpcodes.cardiag.storage.FileService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final FileService fileService;

    @Transactional(readOnly = true)
    public UserResponse getProfile(Integer userId) {
        User user = findUserById(userId);
        return UserResponse.from(user);
    }

    public UserResponse updateProfile(Integer userId, UpdateProfileRequest request) {
        User user = findUserById(userId);

        if (request.firstName() != null) {
            user.setFirstName(request.firstName());
        }
        if (request.lastName() != null) {
            user.setLastName(request.lastName());
        }

        return UserResponse.from(userRepository.save(user));
    }

    public UserResponse uploadProfilePicture(Integer userId, MultipartFile file) {
        User user = findUserById(userId);
        String url = fileService.saveProfilePicture(file, userId);

        user.setProfilePictureUrl(url);
        return UserResponse.from(userRepository.save(user));
    }


    private User findUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new EntityNotFoundException("User not found"));
    }
}
