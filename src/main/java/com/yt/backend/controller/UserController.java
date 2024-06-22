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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.yt.backend.service.UserService;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class    UserController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final ServiceService serviceService;

    @Operation(summary = "Add a new user",
            description = "Allows authorized users to add a new user")
    @ApiResponse(responseCode = "200", description = "User added successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @PostMapping("/user/addUser")
    public String addUser(@RequestBody User user, Authentication authentication){
        boolean isAdmin = serviceService.isAdmin(authentication);
        if(isAdmin){
            userService.addUser(user);
            return "User Added successfully!";
        }else{
            return "Unauthorized!";
        }
    }

    @Operation(summary = "Get user by ID",
            description = "Retrieves a user by their ID")
    @ApiResponse(responseCode = "200", description = "User retrieved successfully",
            content = @Content(schema = @Schema(implementation = User.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @GetMapping ("/user/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id")long id,Authentication authentication){
        boolean isAdmin = serviceService.isAdmin(authentication);
        if (isAdmin){
            User user = userService.getUserById(id);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }




    @Operation(summary = "Get all users",
            description = "Retrieves all users")
    @ApiResponse(responseCode = "200", description = "List of users retrieved successfully",
            content = @Content(schema = @Schema(implementation = User.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers(Authentication authentication){
        boolean isAdmin = serviceService.isAdmin(authentication);
        if(isAdmin){
            List<User> users =  userService.getUsers();
            return new ResponseEntity<>(users,HttpStatus.OK);
        }else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }


    @Operation(summary = "Update user",
            description = "Allows authorized users to update a user")
    @ApiResponse(responseCode = "200", description = "User updated successfully",
            content = @Content(schema = @Schema(implementation = User.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @PutMapping("/user/updateUser/{email}")
    public ResponseEntity<User> updateUser(@PathVariable("email")String email,Authentication authentication,@RequestBody User newUser){
        boolean isAdmin = serviceService.isAdmin(authentication);
        if(isAdmin){
            User user =  userService.updateUser(email,newUser);
            return  new ResponseEntity<>(user,HttpStatus.OK);
        }else return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }



    @Operation(summary = "Get user by email",
            description = "Retrieves a user by their email address")
    @ApiResponse(responseCode = "200", description = "User retrieved successfully",
            content = @Content(schema = @Schema(implementation = User.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email,Authentication authentication,@RequestBody User newUser) {
        boolean isAdmin = serviceService.isAdmin(authentication);
        if(isAdmin){
            User user = userRepository.findByEmail(email);
            if (user != null) {
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.notFound().build();
            }
        }else return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }


    @Operation(summary = "Delete user by ID",
            description = "Allows authorized users to delete a user by their ID")
    @ApiResponse(responseCode = "204", description = "User deleted successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @DeleteMapping("/user/deleteUser/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable("id") long id,Authentication authentication){
        boolean isAdmin = serviceService.isAdmin(authentication);
        if (isAdmin){
            userService.deleteUser(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PatchMapping("/{userId}/UpdateUserRole")
    @Operation(summary = "Update user role to PROFESSIONAL by ID",
            description = "Updates the role of a user based on the provided user ID to Professional.")
    @ApiResponse(responseCode = "200", description = "User role updated successfully",
            content = @Content(schema = @Schema(implementation = User.class)))
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<User> updateUserRole(
            @Parameter(description = "User ID", example = "123") @PathVariable("userId") long userId) {
       User user = userService.updateRole(userId);
       return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
