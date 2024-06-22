package com.yt.backend.controller;

import com.yt.backend.model.Links;
import com.yt.backend.service.LinksService;
import com.yt.backend.service.ServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/auth/link")
@AllArgsConstructor
public class LinksController {

    private final LinksService linksService;
    private final ServiceService serviceService;

    @Operation(summary = "Get all links",
            description = "Retrieve all links")
    @GetMapping("/get")
    public ResponseEntity<List<Links>> getAllLinks() {
        List<Links> allLinks = linksService.getAllLinks();
        return ResponseEntity.ok(allLinks);
    }


    @Operation(summary = "Create a link",
            description = "Allows authorized users to create a new link")
    @ApiResponse(responseCode = "201", description = "Link created successfully",
            content = @Content(schema = @Schema(implementation = Links.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @PostMapping("/addLink")
    public ResponseEntity<Links> createLink(@RequestBody Links link, Authentication authentication) {
        boolean role = serviceService.isAdminOrProfessional(authentication);
        if (role) {
            Links createdLink = linksService.createLink(link);
            return new ResponseEntity<>(createdLink, HttpStatus.CREATED);
        }else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }


    @Operation(summary = "Update a link",
            description = "Allows authorized users to update an existing link")
    @ApiResponse(responseCode = "200", description = "Link updated successfully",
            content = @Content(schema = @Schema(implementation = Links.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @PutMapping("/updateLink/{id}")
    public ResponseEntity<Links> updateLink(@PathVariable Long id, @RequestBody Links link,Authentication authentication) {
        boolean role = serviceService.isAdminOrProfessional(authentication);
        if(role){
            Links updatedLink = linksService.updateLink(id, link);
            return ResponseEntity.ok(updatedLink);
        }
        else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @Operation(summary = "Delete a link",
            description = "Allows authorized users to delete a link")
    @ApiResponse(responseCode = "204", description = "Link deleted successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @DeleteMapping("/deleteLink/{id}")
    public ResponseEntity<Void> deleteLink(@PathVariable Long id,Authentication authentication) {
        boolean role = serviceService.isAdminOrProfessional(authentication);
        if(role){
            linksService.deleteLink(id);
            return ResponseEntity.noContent().build();
        }else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}
