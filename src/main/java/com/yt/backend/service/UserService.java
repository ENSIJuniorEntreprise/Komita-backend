package com.yt.backend.service;

import com.yt.backend.model.user.Role;
import com.yt.backend.model.user.User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

import java.util.List;

@Service
public interface UserService {
    public User addUser (User user);
    public User getUserById(long id);
    public List<User> getUsers();
    public List<User> getCandidats(Role role);
    public void deleteUser(long id);
    public User updateUser (String email , User newUser);
    public User updateRole(Long id);
    public User updateUserProfileImage(Long userId, String profileImageURL);
    public User getLoggedInUserDetails(String email);
    public String getUserProfileImagePath(Long userId);
    public String getUserProfileImagePathByEmail(String email);
}
