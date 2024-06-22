package com.yt.backend.service;
import com.yt.backend.model.user.Role;
import com.yt.backend.model.user.User;

import com.yt.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User addUser(User user) {
        return userRepository.save(user);
    }
    @Override
    public User getUserById(long id) {
        return userRepository.findById(id).get() ;
    }
    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }
    @Override
    public List<User> getCandidats(Role role) {return userRepository.findByRole(role);
    }
    @Override
    public void deleteUser(long id) {
        userRepository.deleteById(id);
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
        if(user.isPresent()){
            User actual_user = user.get();
                actual_user.setRole(Role.valueOf("PROFESSIONAL"));
                userRepository.save(actual_user);
                return actual_user;
        }
        else {
            throw new EntityNotFoundException("User not found with ID: " + id);
        }
    }
}
