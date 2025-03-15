package com.yt.backend.service;

import com.yt.backend.model.user.Role;
import com.yt.backend.model.user.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.server.ResponseStatusException;
import com.yt.backend.repository.UserRepository;
import com.yt.backend.repository.TokenRepository;
import com.yt.backend.repository.VerificationTokenRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final VerificationTokenRepository verificationTokenRepository;

    public UserServiceImpl(UserRepository userRepository, 
                         TokenRepository tokenRepository,
                         VerificationTokenRepository verificationTokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.verificationTokenRepository = verificationTokenRepository;
    }

    @Override
    public User addUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User getUserById(long id) {
        return userRepository.findById(id).get();
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<User> getCandidats(Role role) {
        return userRepository.findByRole(role);
    }

    @Override
    @Transactional
    public void deleteUser(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EmptyResultDataAccessException("User not found with id: " + id, 1));

        // Delete all verification tokens associated with the user
        verificationTokenRepository.deleteByUser(user);

        // Delete all authentication tokens associated with the user
        tokenRepository.deleteByUser(user);

        // Finally delete the user
        userRepository.delete(user);
    }

    @Override
    public User updateUser(String email, User newUser) {
        Optional<User> existingUserOptional = Optional.ofNullable(userRepository.findByEmail(email));
        if (existingUserOptional.isPresent()) {
            User existingUser = existingUserOptional.get();
            // Update the properties of the existing user based on the input user
            existingUser.setFirstname(newUser.getFirstname());
            existingUser.setLastname(newUser.getLastname());
            existingUser.setEmail(newUser.getEmail());
            existingUser.setPassword(newUser.getPassword());
            existingUser.setUserAddress(newUser.getUserAddress());
            existingUser.setStatus(true);
            // Add other properties that you want to update
            return userRepository.save(existingUser);
        } else {
            return null; // User not found, return null to indicate the update failure
        }
    }

    @Override
    public User updateRole(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            User actual_user = user.get();
            actual_user.setRole(Role.valueOf("PROFESSIONAL"));
            userRepository.save(actual_user);
            return actual_user;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }


    @Override
    public User getLoggedInUserDetails(String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            return user;
        } else {
            throw new EmptyResultDataAccessException("User not found with email: " + email, 1);
        }
    }

    @Override
    @Transactional
    public User updateUserProfileImage(Long userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EmptyResultDataAccessException("User not found with id: " + userId, 1));

        // Validate file size and type if needed
        if (file.getSize() > 5 * 1024 * 1024) { // 5MB limit
            throw new IllegalArgumentException("File size too large. Maximum size allowed is 5MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed");
        }

        user.setProfileImage(file.getBytes());
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] getUserProfileImageBytes(Long userId) {
        return userRepository.findById(userId)
                .map(User::getProfileImage)
                .orElseThrow(() -> new EmptyResultDataAccessException("User not found with id: " + userId, 1));
    }

    @Override
    @Transactional
    public User updateUserProfileImage(Long userId, String profileImageURL) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EmptyResultDataAccessException("User not found with id: " + userId, 1));
        
        // For now, we don't support URL-based profile images
        throw new UnsupportedOperationException("URL-based profile images are not supported");
    }

    // @Override
    // public User updateUserProfileImage(Long userId, String profileImageURL) {
    //     // TODO Auto-generated method stub
    //     throw new UnsupportedOperationException("Unimplemented method 'updateUserProfileImage'");
    // }

    // public String getUserProfileImagePathByEmail(String email) {
    //     // Logique pour récupérer le chemin de l'image à partir de l'email
    //     User user = userRepository.findByEmail(email);
    //     return user != null ? user.getProfileImage() : null;
    // }
    

}
