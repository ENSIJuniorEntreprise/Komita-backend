package com.yt.backend.controller;

import com.yt.backend.model.Adress;
import com.yt.backend.model.Keyword;
import com.yt.backend.model.Links;
import com.yt.backend.model.category.Category;
import com.yt.backend.model.category.Subcategory;
import com.yt.backend.model.user.Role;
import com.yt.backend.model.user.User;
import com.yt.backend.repository.CategoryRepository;
import com.yt.backend.repository.SubcategoryRepository;
import com.yt.backend.repository.UserRepository;
import com.yt.backend.repository.KeywordRepository;
import com.yt.backend.service.ServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.codehaus.plexus.resource.loader.ResourceNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;
import com.yt.backend.repository.ServiceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.yt.backend.model.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequestMapping("/api/v1/auth")
@RestController
public class ServiceController {

    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final SubcategoryRepository subcategoryRepository;
    private final KeywordRepository keywordRepository ;


    private final ServiceService serviceService;


    public ServiceController(ServiceRepository serviceRepository, UserRepository userRepository, CategoryRepository categoryRepository, SubcategoryRepository subcategoryRepository, ServiceService serviceService,KeywordRepository keywordRepository ) {
        this.serviceRepository = serviceRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.subcategoryRepository = subcategoryRepository;
        this.keywordRepository = keywordRepository;
        this.serviceService = serviceService;

    }

    @Operation(summary = "Get all services", description = "Retrieve a list of all services")
    @GetMapping("/services")
    public ResponseEntity<List<Service>> getAllServices() {
        List<Service> services = new ArrayList<>(serviceRepository.findAll());
        if (services.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(services, HttpStatus.OK);
    }


    @Operation(summary = "Get service by ID", description = "Retrieve a service by its unique identifier")
    @GetMapping("/services/{id}")
    public ResponseEntity<Service> getServiceById(@PathVariable("id") long id) throws ResourceNotFoundException {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Service with id = " + id));

        return new ResponseEntity<>(service, HttpStatus.OK);
    }


    @Operation(summary = "Create a new service", description = "Allows authorized users to create a new service")
    @ApiResponse(responseCode = "201", description = "Service created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body or insufficient privileges")
    @PostMapping("/services/createService")
    public ResponseEntity<?> createService(@RequestBody Service service) {
        User professional = service.getProfessional();
        Category category = service.getCategory();
        Subcategory subcategory = service.getSubcategory();
        Adress serviceAdress = service.getAdress();
        Links serviceLinks = service.getLinks();
        //boolean role = serviceService.isAdminOrProfessional(authentication);
        Optional<Category> existingCategory = categoryRepository.findById(category.getId());
        Optional<User> existingProfessional = userRepository.findById(professional.getId());
//        if (role) {
            if (existingProfessional.isPresent() && existingCategory.isPresent()) {
                professional = existingProfessional.get();
                category = existingCategory.get();
                service.setProfessional(professional);
                service.setCategory(category);
                service.setSubcategory(subcategory);
                service.setAdress(serviceAdress);
                service.setLinks(serviceLinks);
                if (Role.valueOf("ADMIN").equals(professional.getRole()) || Role.valueOf("PROFESSIONAL").equals(professional.getRole())) {
                    Service _service = serviceRepository.save(new Service(service.getName(), service.getDescription(), professional, category, subcategory, Boolean.TRUE, serviceAdress, serviceLinks));
                    return new ResponseEntity<>(_service, HttpStatus.CREATED);
                } else {
                    return ResponseEntity.badRequest().body("the user is not a professional or an admin, please upgrade your privileges and try again!");
                }
            } else {
                return ResponseEntity.badRequest().body("Professional with ID " + professional.getId() + " not found" + " or category with ID " + category.getId() + "not found");
            }
//        } else {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Insufficient privileges to create a service");
//        }
    }

    @Operation(summary = "Update an existing service", description = "Allows authorized users to update an existing service")
    @ApiResponse(responseCode = "200", description = "Service updated successfully")
    @ApiResponse(responseCode = "403", description = "Insufficient privileges")
    @PutMapping("/services/updateService/{id}")
    public ResponseEntity<?> updateService(@PathVariable("id") long id, @RequestBody Service service, Authentication authentication) throws ResourceNotFoundException {
        boolean role = serviceService.isAdminOrProfessional(authentication);
        if (role) {
            Service _service = serviceRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Not found Service with id = " + id));
            _service.setName(service.getName());
            _service.setDescription(service.getDescription());
            _service.setState(service.getState());
            return new ResponseEntity<>(serviceRepository.save(_service), HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Insufficient privileges");
        }
    }


    @Operation(summary = "Delete a service by ID", description = "Allows authorized users to delete a service by its ID")
    @ApiResponse(responseCode = "204", description = "Service deleted successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @DeleteMapping("/services/deleteService/{id}")
    public ResponseEntity<HttpStatus> deleteService(@PathVariable("id") long id, Authentication authentication) {
        boolean role = serviceService.isAdminOrProfessional(authentication);
        if (role) {
            serviceRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @Operation(summary = "Delete all services", description = "Allows authorized users to delete all services")
    @ApiResponse(responseCode = "204", description = "All services deleted successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @DeleteMapping("/services/deleteAllServices")
    public ResponseEntity<HttpStatus> deleteAllServices(Authentication authentication) {
        boolean role = serviceService.isAdminOrProfessional(authentication);
        if (role) {
            serviceRepository.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }


    @Operation(summary = "Get recent professionals",
            description = "Get a list of recent professionals with an optional limit on the number of rows returned Max-Rows.")
    @GetMapping("/services/recentProfessionals")
    public ResponseEntity<List<Service>> getRecentProfessionals(@RequestHeader(value = "Max-Rows", required = false) Integer maxRows) {
        List<Service> recentServices = serviceRepository.findAllByOrderByCreatedAtDesc();

        // Check if Max-Rows header is present and valid
        if (maxRows != null && maxRows > 0 && maxRows < recentServices.size()) {
            recentServices = recentServices.subList(0, maxRows);
        }

        if (recentServices.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(recentServices, HttpStatus.OK);
    }

    @Operation(summary = "Get popular professionals", description = "Retrieve a list of popular professionals with an optional limit on the number of rows returned Max-Rows.")
    @GetMapping("/services/popularProfessionals")
    public ResponseEntity<List<Service>> getPopularProfessionals(@RequestHeader(value = "Max-Rows", required = false) Integer maxRows) {
        List<Service> popularProfessionals = serviceRepository.findAllByOrderByNbrConsultationsDesc();

        // Check if Max-Rows header is present and valid
        if (maxRows != null && maxRows > 0 && maxRows < popularProfessionals.size()) {
            popularProfessionals = popularProfessionals.subList(0, maxRows);
        }

        if (popularProfessionals.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(popularProfessionals, HttpStatus.OK);
    }


    public SubcategoryRepository getSubcategoryRepository() {
        return subcategoryRepository;
    }

    @Operation(summary = "Get services by category and subcategory", description = "Retrieve a list of services by category and subcategory IDs")
    @GetMapping("/services/byCategoryAndSubcategory/{categoryId}/{subcategoryId}")
    public ResponseEntity<? extends Object> getServicesByCategoryAndSubcategory(@PathVariable("categoryId") long categoryId, @PathVariable("subcategoryId") long subcategoryId) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        Optional<Subcategory> subcategoryOptional = subcategoryRepository.findById(subcategoryId);

        if (categoryOptional.isPresent() && subcategoryOptional.isPresent()) {
            Category category = categoryOptional.get();
            Subcategory subcategory = subcategoryOptional.get();

            if (!category.getSubcategories().contains(subcategory)) {
                return new ResponseEntity<String>("Subcategory with ID " + subcategoryId + " does not belong to category with ID " + categoryId, HttpStatus.BAD_REQUEST);
            }

            List<Service> services = serviceRepository.findByCategoryAndSubcategory(category, subcategory);
            if (services.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<List<Service>>(services, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @Operation(summary = "Get services by category", description = "Retrieve a list of services by category ID")
    @GetMapping("/services/byCategory/{categoryId}")
    public ResponseEntity<? extends Object> getServicesByCategory(@PathVariable("categoryId") long categoryId) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);

        if (categoryOptional.isPresent()) {
            Category category = categoryOptional.get();


            List<Service> services = serviceRepository.findByCategory(category);
            if (services.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<List<Service>>(services, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @Operation(summary = "Get services by user", description = "Retrieve a list of services by user ID")
    @GetMapping("/services/byUser/{userId}")
    public ResponseEntity<?> getServicesByUser(@PathVariable("userId") long userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            List<Service> services = serviceRepository.findByProfessional(user);
            if (services.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(services, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User with ID " + userId + " not found", HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Get services by keyword", description = "Retrieve a list of services matching the provided keywords")
    @GetMapping("/services/byKeyword")
    public ResponseEntity<?> getServicesByKeyword(@RequestParam List<String> keywords) {
        List<Keyword> keywordList = new ArrayList<>();
        for (String keyword : keywords) {
            // Assuming you have a method in KeywordRepository to find by keywordName
            keywordList.addAll(keywordRepository.findByKeywordNameIgnoreCase(keyword));
        }
        if (keywordList.isEmpty()) {
            return new ResponseEntity<>("No services found for the provided keywords", HttpStatus.NOT_FOUND);
        }
        List<Service> services = serviceRepository.findByKeywordListIn(keywordList);
        if (services.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(services, HttpStatus.OK);
    }


}
