package com.yt.backend.controller;

import com.yt.backend.dto.UserDto;
import com.yt.backend.dto.AddressDto;
import com.yt.backend.model.user.Role;
import com.yt.backend.model.user.User;
import org.apache.tika.Tika;
import org.springframework.dao.EmptyResultDataAccessException;
import com.yt.backend.exception.ValidationException;
import com.yt.backend.repository.UserRepository;
import com.yt.backend.service.ServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataAccessException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;

import com.yt.backend.service.UserService;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final ServiceService serviceService;

    @Operation(summary = "Add a new user", description = "Allows authorized users to add a new user")
    @ApiResponse(responseCode = "200", description = "User added successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "400", description = "Invalid user data")
    @PostMapping("/user/addUser")
    public ResponseEntity<?> addUser(@RequestBody User user, Authentication authentication) {
        boolean isAdmin = serviceService.isAdmin(authentication);
        if (!isAdmin) {
            return new ResponseEntity<>("Unauthorized!", HttpStatus.UNAUTHORIZED);
        }
        
        // Validate user data
        Map<String, String> validationErrors = validateUser(user);
        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Erreurs de validation lors de l'ajout d'un utilisateur", validationErrors);
        }
        
        try {
            userService.addUser(user);
            return ResponseEntity.ok("User Added successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error adding user: " + e.getMessage());
        }
    }

    private Map<String, String> validateUser(User user) {
        Map<String, String> errors = new HashMap<>();
        
        // Validate firstname
        if (user.getFirstname() == null || user.getFirstname().trim().isEmpty()) {
            errors.put("firstname", "Le prénom est obligatoire");
        } else if (user.getFirstname().length() < 2 || user.getFirstname().length() > 50) {
            errors.put("firstname", "Le prénom doit contenir entre 2 et 50 caractères");
        }
        
        // Validate lastname
        if (user.getLastname() == null || user.getLastname().trim().isEmpty()) {
            errors.put("lastname", "Le nom est obligatoire");
        } else if (user.getLastname().length() < 2 || user.getLastname().length() > 50) {
            errors.put("lastname", "Le nom doit contenir entre 2 et 50 caractères");
        }
        
        // Validate email
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            errors.put("email", "L'email est obligatoire");
        } else if (!isValidEmail(user.getEmail())) {
            errors.put("email", "Format d'email invalide");
        } else if (userRepository.findByEmail(user.getEmail()) != null) {
            errors.put("email", "Cet email est déjà utilisé");
        }
        
        // // Validate username
        // if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
        //     errors.put("username", "Le nom d'utilisateur est obligatoire");
        // } else if (user.getUsername().length() < 3 || user.getUsername().length() > 30) {
        //     errors.put("username", "Le nom d'utilisateur doit contenir entre 3 et 30 caractères");
        // }
        
        // Validate password
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            errors.put("password", "Le mot de passe est obligatoire");
        } else if (user.getPassword().length() < 8) {
            errors.put("password", "Le mot de passe doit contenir au moins 8 caractères");
        } else if (!isStrongPassword(user.getPassword())) {
            errors.put("password", "Le mot de passe doit contenir au moins une lettre majuscule, une lettre minuscule, un chiffre et un caractère spécial");
        }
        
        // Validate role
        if (user.getRole() == null) {
            errors.put("role", "Le rôle est obligatoire");
        }
        
        // Remove phone number validation for professionals
        // if (user.getRole() == Role.PROFESSIONAL) {
        //     if (user.getPhoneNumber() == null || user.getPhoneNumber().trim().isEmpty()) {
        //         errors.put("phoneNumber", "Le numéro de téléphone est obligatoire pour les professionnels");
        //     } else if (!isValidPhoneNumber(user.getPhoneNumber())) {
        //         errors.put("phoneNumber", "Format de numéro de téléphone invalide");
        //     }
        // }
        
        return errors;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    private boolean isStrongPassword(String password) {
        // Password should contain at least one digit, one lowercase, one uppercase, one special character and be at least 8 characters long
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
        return password.matches(passwordRegex);
    }

    @Operation(summary = "Get user by ID", description = "Retrieves a user by their ID")
    @ApiResponse(responseCode = "200", description = "User retrieved successfully", content = @Content(schema = @Schema(implementation = User.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @GetMapping("/user/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") long id, Authentication authentication) {
        boolean isAdmin = serviceService.isAdmin(authentication);
        if (isAdmin) {
            User user = userService.getUserById(id);
            return new ResponseEntity<>(mapToResponseDto(user), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    // Update the mapToResponseDto method to include phoneNumber
    private UserDto mapToResponseDto(User user) {
        if (user == null) {
            return null;
        }

        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setCustomIdentifier(user.getCustomIdentifier());
        userDto.setFirstname(user.getFirstname());
        userDto.setLastname(user.getLastname());
        userDto.setEmail(user.getEmail());
        userDto.setRole(user.getRole() != null ? user.getRole().name() : null);
        userDto.setStatus(user.isStatus());
        userDto.setPhoneNumber(user.getPhoneNumber());
        
        // Only include phone number for professionals
        if (user.getRole() == Role.PROFESSIONAL) {
            userDto.setPhoneNumber(user.getPhoneNumber());
        }

        // Handle address if present
        if (user.getUserAddress() != null) {
            AddressDto addressDto = new AddressDto();
            String street = "";
            if (user.getUserAddress().getStreetName() != null) {
                street = user.getUserAddress().getStreetName();
                if (user.getUserAddress().getStreetNumber() != null) {
                    street += " " + user.getUserAddress().getStreetNumber();
                }
                if (user.getUserAddress().getStreetType() != null) {
                    street += " " + user.getUserAddress().getStreetType();
                }
            }
            addressDto.setStreet(street);
            addressDto.setCity(user.getUserAddress().getCity());
            addressDto.setState(user.getUserAddress().getProvinceName());
            addressDto.setZipCode(user.getUserAddress().getPostalCode());
            addressDto.setCountry(user.getUserAddress().getCountry());
            userDto.setUserAddress(addressDto);
        }

        // Handle profile image if present
        if (user.getProfileImage() != null) {
            try {
                String profileImageStr = java.util.Base64.getEncoder().encodeToString(user.getProfileImage());
                userDto.setProfileImage(profileImageStr);
            } catch (Exception e) {
                System.err.println("Error converting profile image: " + e.getMessage());
            }
        }

        return userDto;
    }

    @Operation(summary = "Get all users", description = "Retrieves all users")
    @ApiResponse(responseCode = "200", description = "List of users retrieved successfully", content = @Content(schema = @Schema(implementation = UserDto.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getUsers(Authentication authentication) {
        boolean isAdmin = serviceService.isAdmin(authentication);
        if (isAdmin) {
            List<User> users = userService.getUsers();
            List<UserDto> userDtos = users.stream().map(this::mapToResponseDto).toList();
            return new ResponseEntity<>(userDtos, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @Operation(summary = "Update user", description = "Allows authorized users to update a user")
    @ApiResponse(responseCode = "200", description = "User updated successfully", content = @Content(schema = @Schema(implementation = User.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @PutMapping("/user/updateUser/{email}")
    public ResponseEntity<UserDto> updateUser(@PathVariable("email") String email, Authentication authentication,
            @RequestBody User newUser) {
        boolean isAdminOrProfessional = serviceService.isAdminOrProfessional(authentication);
        if (isAdminOrProfessional) {
            // If admin, allow any phone number update
            boolean isAdmin = serviceService.isAdmin(authentication);
            if (isAdmin) {
                // Optionally, you can add phone number format validation here if needed
                // Example:
                // if (newUser.getPhoneNumber() != null && !isValidPhoneNumber(newUser.getPhoneNumber())) {
                //     return ResponseEntity.badRequest().body(null);
                // }
                User user = userService.updateUser(email, newUser);
                return new ResponseEntity<>(mapToResponseDto(user), HttpStatus.OK);
            } else {
                // For professionals, you may keep your existing logic or add restrictions if needed
                User user = userService.updateUser(email, newUser);
                return new ResponseEntity<>(mapToResponseDto(user), HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @Operation(summary = "Get user by email", description = "Retrieves a user by their email address")
    @ApiResponse(responseCode = "200", description = "User retrieved successfully", content = @Content(schema = @Schema(implementation = User.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @GetMapping("/user/email/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable String email, Authentication authentication) {
        boolean isAdmin = serviceService.isAdmin(authentication);
        if (isAdmin) {
            User user = userRepository.findByEmail(email);
            if (user != null) {
                return ResponseEntity.ok(mapToResponseDto(user));
            } else {
                return ResponseEntity.notFound().build();
            }
        } else
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @Operation(summary = "Delete user by ID", description = "Allows authorized users to delete a user by their ID")
    @ApiResponse(responseCode = "204", description = "User deleted successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @DeleteMapping("/user/deleteUser/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable("id") long id, Authentication authentication) {
        boolean isAdmin = serviceService.isAdmin(authentication);
        if (isAdmin) {
            userService.deleteUser(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/{userId}/UpdateUserRole")
    @Operation(summary = "Update user role to PROFESSIONAL by ID", description = "Updates the role of a user based on the provided user ID to Professional.")
    @ApiResponse(responseCode = "200", description = "User role updated successfully", content = @Content(schema = @Schema(implementation = User.class)))
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<UserDto> updateUserRole(
            @Parameter(description = "User ID", example = "123") @PathVariable("userId") long userId) {
        User user = userService.updateRole(userId);
        return new ResponseEntity<>(mapToResponseDto(user), HttpStatus.OK);
    }

    // @Operation(summary = "Update profile image for the logged-in user",
    // description = "Allows the logged-in user to update their profile image.")
    // @ApiResponse(responseCode = "200", description = "Profile image updated
    // successfully")
    // @ApiResponse(responseCode = "401", description = "Unauthorized")
    // @PostMapping("/users/{userId}/uploadProfileImage")
    // public ResponseEntity<?> uploadProfileImage(@PathVariable Long userId,
    // @RequestParam MultipartFile file, Authentication authentication)
    // throws IOException {

    // if (file.isEmpty()) {
    // return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    // }

    // String fileName = UUID.randomUUID().toString() + "_" +
    // file.getOriginalFilename();
    // Path path = Paths.get("src/main/resources/static/images", fileName);
    // Files.createDirectories(path.getParent()); // Ensure the directory exists
    // Files.copy(file.getInputStream(), path);
    // String fileURL = "/images/" + fileName;
    // User updatedUser = userService.updateUserProfileImage(userId, fileURL);

    // return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    // }

    @GetMapping("/user/{userId}/profileImage")
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> getProfileImage(@PathVariable Long userId) {
        try {
            byte[] imageBytes = userService.getUserProfileImageBytes(userId);
            if (imageBytes == null || imageBytes.length == 0) {
                return ResponseEntity.notFound().build();
            }

            // Use Apache Tika to determine the content type of the image
            Tika tika = new Tika();
            String contentType = tika.detect(imageBytes);

            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf(contentType))
                    .body(imageBytes);
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/user/{userId}/uploadProfileImage")
    @Operation(summary = "Update profile image")
    @Transactional
    public ResponseEntity<?> uploadProfileImageController(
            @PathVariable("userId") Long userId,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        try {
            if (!serviceService.isAdminOrProfessional(authentication)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty");
            }

            // File type validation
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body("Only image files are allowed");
            }

            User updatedUser = userService.updateUserProfileImage(userId, file);
            return ResponseEntity.ok(mapToResponseDto(updatedUser));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error uploading profile image: " + e.getMessage());
        }
    }

    @GetMapping("/user/details")
    public ResponseEntity<?> getUserDetails(Authentication authentication) {
        String authenticatedEmail = authentication.getName();

        User user = userService.getLoggedInUserDetails(authenticatedEmail);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        // Prepare the response with the required fields from User
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("firstname", user.getFirstname());
        response.put("lastname", user.getLastname());
        response.put("email", user.getEmail());
        response.put("role", user.getRole().name()); // Enum to String
        response.put("profileImage", user.getProfileImage());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/user/profile")
@Operation(summary = "Get current user profile", description = "Retrieves the profile of the currently authenticated user")
@ApiResponse(responseCode = "200", description = "Profile retrieved successfully")
@ApiResponse(responseCode = "401", description = "Unauthorized")
public ResponseEntity<UserDto> getCurrentUserProfile(Authentication authentication) {
    if (authentication != null) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email);
        if (user != null) {
            return ResponseEntity.ok(mapToResponseDto(user));
        } else {
            return ResponseEntity.notFound().build();
        }
    } else {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}

    // Add a new endpoint to update phone number (only for professionals)
    @Operation(summary = "Update phone number", description = "Allows professionals to update their phone number")
    @ApiResponse(responseCode = "200", description = "Phone number updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid phone number format")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden - User is not a professional")
    @PutMapping("/user/{userId}/updatePhoneNumber")
    public ResponseEntity<?> updatePhoneNumber(
            @PathVariable("userId") Long userId,
            @RequestParam("phoneNumber") String phoneNumber,
            Authentication authentication) {
        
        // Only check if user is authorized (admin or the user themselves)
        boolean isAdmin = serviceService.isAdmin(authentication);
        String currentUserEmail = authentication.getName();
        User currentUser = userRepository.findByEmail(currentUserEmail);
        
        if (!isAdmin && (currentUser == null || !currentUser.getId().equals(userId))) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        
        try {
            // No role or format validation
            User updatedUser = userService.updatePhoneNumber(userId, phoneNumber);
            return ResponseEntity.ok(mapToResponseDto(updatedUser));
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Could not update phone number: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error updating phone number: " + e.getMessage());
        }
    }
    
    // Remove the isValidPhoneNumber method if not used elsewhere
    private boolean isValidPhoneNumber(String phoneNumber) {
        // Basic validation: Allow +, digits, spaces, hyphens, and parentheses
        String phoneRegex = "^[+]?[(]?[0-9]{1,4}[)]?[-\\s\\./0-9]*$";
        return phoneNumber != null && phoneNumber.matches(phoneRegex);
    }
}