package com.yt.backend.controller;

import com.yt.backend.model.user.Role;
import com.yt.backend.model.user.User;

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
@RequestMapping("/api/v1/auth")
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final ServiceService serviceService;

    @Operation(summary = "Add a new user", description = "Allows authorized users to add a new user")
    @ApiResponse(responseCode = "200", description = "User added successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @PostMapping("/user/addUser")
    public String addUser(@RequestBody User user, Authentication authentication) {
        boolean isAdmin = serviceService.isAdmin(authentication);
        if (isAdmin) {
            userService.addUser(user);
            return "User Added successfully!";
        } else {
            return "Unauthorized!";
        }
    }

    @Operation(summary = "Get user by ID", description = "Retrieves a user by their ID")
    @ApiResponse(responseCode = "200", description = "User retrieved successfully", content = @Content(schema = @Schema(implementation = User.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") long id, Authentication authentication) {
        boolean isAdmin = serviceService.isAdmin(authentication);
        if (isAdmin) {
            User user = userService.getUserById(id);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @Operation(summary = "Get all users", description = "Retrieves all users")
    @ApiResponse(responseCode = "200", description = "List of users retrieved successfully", content = @Content(schema = @Schema(implementation = User.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers(Authentication authentication) {
        boolean isAdmin = serviceService.isAdmin(authentication);
        if (isAdmin) {
            List<User> users = userService.getUsers();
            return new ResponseEntity<>(users, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @Operation(summary = "Update user", description = "Allows authorized users to update a user")
    @ApiResponse(responseCode = "200", description = "User updated successfully", content = @Content(schema = @Schema(implementation = User.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @PutMapping("/user/updateUser/{email}")
    public ResponseEntity<User> updateUser(@PathVariable("email") String email, Authentication authentication,
            @RequestBody User newUser) {
        boolean isAdmin = serviceService.isAdmin(authentication);
        if (isAdmin) {
            User user = userService.updateUser(email, newUser);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @Operation(summary = "Get user by email", description = "Retrieves a user by their email address")
    @ApiResponse(responseCode = "200", description = "User retrieved successfully", content = @Content(schema = @Schema(implementation = User.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email, Authentication authentication,
            @RequestBody User newUser) {
        boolean isAdmin = serviceService.isAdmin(authentication);
        if (isAdmin) {
            User user = userRepository.findByEmail(email);
            if (user != null) {
                return ResponseEntity.ok(user);
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

    @PatchMapping("/{userId}/UpdateUserRole")
    @Operation(summary = "Update user role to PROFESSIONAL by ID", description = "Updates the role of a user based on the provided user ID to Professional.")
    @ApiResponse(responseCode = "200", description = "User role updated successfully", content = @Content(schema = @Schema(implementation = User.class)))
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<User> updateUserRole(
            @Parameter(description = "User ID", example = "123") @PathVariable("userId") long userId) {
        User user = userService.updateRole(userId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }


    @Operation(summary = "Update profile image for the logged-in user", description = "Allows the logged-in user to update their profile image.")
    @ApiResponse(responseCode = "200", description = "Profile image updated successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @PostMapping("/users/{userId}/uploadProfileImage")
    public ResponseEntity<User> uploadProfileImage(
            @PathVariable("userId") Long userId,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) throws IOException {

        // Check if the user is authorized
        boolean role = serviceService.isAdminOrProfessional(authentication);
        if (!role) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // Check if the file is not empty
        if (file.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Generate a unique filename for the uploaded image
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        // Save the file in the default images directory (this assumes a folder named
        // 'images' in 'src/main/resources/static')
        Path path = Paths.get("src/main/resources/static/images", fileName);
        Files.createDirectories(path.getParent()); // Ensure the directory exists
        Files.copy(file.getInputStream(), path);

        // File URL is relative to the static folder
        String fileURL = "/images/" + fileName;

        // Update user with the new profile image URL
        User updatedUser = userService.updateUserProfileImage(userId, fileURL);

        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @GetMapping("/users/{userId}/profileImage")
    public ResponseEntity<Resource> getProfileImage(@PathVariable("userId") Long userId) throws IOException {
        // Récupération du chemin de l'image à partir de la base de données ou d'une logique métier
        String imagePath = userService.getUserProfileImagePath(userId);
        
        // Construction du chemin absolu de l'image
        Path path = Paths.get("src/main/resources/static", imagePath);
        
        if (!Files.exists(path)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();  // Retourner 404 si l'image n'existe pas
        }
    
        // Créez un objet Resource pour l'image
        Resource resource = new UrlResource(path.toUri());
        
        // Détecter le type MIME du fichier en fonction de son extension
        String contentType = Files.probeContentType(path);
        if (contentType == null) {
            contentType = "application/octet-stream";  // Si le type MIME ne peut pas être déterminé
        }
    
        // Retourner l'image avec le type MIME approprié
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))  // Utilise le type MIME détecté
                .body(resource);
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
}
