package com.yt.backend.service;

import com.yt.backend.model.user.Role;
import com.yt.backend.model.user.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.server.ResponseStatusException;
import com.yt.backend.repository.UserRepository;
import com.yt.backend.repository.TokenRepository;
import com.yt.backend.repository.VerificationTokenRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, 
                         TokenRepository tokenRepository,
                         VerificationTokenRepository verificationTokenRepository,
                         PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User addUser(User user) {
        // Encode the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User getUserById(long id) {
        return userRepository.findById(id).get();
    }

    @Override
    public List<User> getUsers() {
        try {
            List<User> users = userRepository.findAll();
            return users != null ? users : List.of();
        } catch (Exception e) {
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error retrieving users: " + e.getMessage()
            );
        }
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
    
            // Check for phone number uniqueness for ALL users (admin or not)
            if (newUser.getPhoneNumber() != null && !newUser.getPhoneNumber().equals(existingUser.getPhoneNumber())) {
                User userWithSamePhone = userRepository.findByPhoneNumber(newUser.getPhoneNumber());
                if (userWithSamePhone != null && !userWithSamePhone.getId().equals(existingUser.getId())) {
                    throw new org.springframework.dao.DataIntegrityViolationException("Phone number already in use by another user");
                }
            }
    
            // Update the properties of the existing user based on the input user
            existingUser.setFirstname(newUser.getFirstname());
            existingUser.setLastname(newUser.getLastname());
            existingUser.setEmail(newUser.getEmail());
            existingUser.setPhoneNumber(newUser.getPhoneNumber());
            if (newUser.getPassword() != null && !newUser.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
            }

            // Fix: Update address fields instead of replacing the address object
            if (newUser.getUserAddress() != null) {
                if (existingUser.getUserAddress() != null) {
                    // Update fields of the existing address
                    existingUser.getUserAddress().setStreetNumber(newUser.getUserAddress().getStreetNumber());
                    existingUser.getUserAddress().setStreetName(newUser.getUserAddress().getStreetName());
                    existingUser.getUserAddress().setStreetType(newUser.getUserAddress().getStreetType());
                    existingUser.getUserAddress().setCity(newUser.getUserAddress().getCity());
                    existingUser.getUserAddress().setProvinceName(newUser.getUserAddress().getProvinceName());
                    existingUser.getUserAddress().setPostalCode(newUser.getUserAddress().getPostalCode());
                    existingUser.getUserAddress().setCountry(newUser.getUserAddress().getCountry());
                } else {
                    // New address: ensure id is null so Hibernate creates a new row
                    newUser.getUserAddress().setId(null);
                    existingUser.setUserAddress(newUser.getUserAddress());
                }
            }

            existingUser.setStatus(true);
            return userRepository.save(existingUser);
        } else {
            return null;
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

        try {
            byte[] imageBytes = file.getBytes();
            user.setProfileImage(imageBytes);
            User savedUser = userRepository.save(user);
            userRepository.flush();
            return savedUser;
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to process image file: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] getUserProfileImageBytes(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EmptyResultDataAccessException("User not found with id: " + userId, 1));
        return user.getProfileImage();
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
    
    @Override
    @Transactional
    public User saveUser(User user) {
        // Check if the user already exists in the database
        if (user.getId() != null) {
            Optional<User> existingUser = userRepository.findById(user.getId());
            if (existingUser.isPresent()) {
                return existingUser.get(); // User already exists, return it
            }
        }
        
        // If user has an email, check if a user with that email already exists
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            User existingUser = userRepository.findByEmail(user.getEmail());
            if (existingUser != null) {
                return existingUser; // User with this email already exists, return it
            }
        }
        
        // If the user is new and has a password that needs encoding
        if (user.getPassword() != null && !user.getPassword().isEmpty() 
                && !user.getPassword().startsWith("$2a$")) { // Check if password is not already encoded
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        
        // Save the user to the database
        return userRepository.save(user);
    }
    
    @Override
    @Transactional
    public User updatePhoneNumber(Long userId, String phoneNumber) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EmptyResultDataAccessException("User not found with id: " + userId, 1));
        
        // Remove role and format validation
        user.setPhoneNumber(phoneNumber);
        return userRepository.save(user);
    }
}
