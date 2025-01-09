package org.example.service;

import org.example.dto.UserDto;
import org.example.exceptions.UserAlreadyExistsException;
import org.example.mapper.UserMapper;
import org.example.model.User;
import org.example.model.UserRole;
import org.example.repository.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.regex.Pattern;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class UserService {
    private final UserDao userDao;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    // RFC 5322 Official Standard
    private static final Pattern EMAIL_PATTERN = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");

    public UserService(UserDao userDao, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public User addUser(String username, String email, String password, String role) throws UserAlreadyExistsException {
        validateUserCredentials(username, email);
        password = passwordEncoder.encode(password);
        User user = new User(username, email, password, UserRole.valueOf(role.toUpperCase()));
        userDao.create(user);
        logger.info("User is {} added to DB.", user);
        return user;
    }

    public UserDto changeUsername(String newUsername) throws UserAlreadyExistsException {
        User currentUser = getCurrentUser();
        User userWithNewUsername = userDao.findUserByUsername(newUsername);
        if (userWithNewUsername != null) {
            throw new UserAlreadyExistsException("Username " + newUsername + " is already taken");
        }
        String oldUsername = currentUser.getUsername();
        currentUser.setUsername(newUsername);
        userDao.update(currentUser);
        logger.info("Username changed: {} -> {}", oldUsername, newUsername);
        return userMapper.toDto(currentUser);
    }

    public UserDto changeEmail(String newEmail) throws UserAlreadyExistsException {
        User currentUser = getCurrentUser();
        validateEmail(newEmail);
        String oldEmail = currentUser.getEmail();
        currentUser.setEmail(newEmail);
        userDao.update(currentUser);
        logger.info("Email changed for user {}: {} -> {}", currentUser.getUsername(), oldEmail, newEmail);
        return userMapper.toDto(currentUser);
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userDao.findUserByUsername(username);
    }

    private void validateEmail(String email) throws UserAlreadyExistsException {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Incorrect email.");
        }
        if (userDao.findUserByEmail(email) != null) {
            throw new UserAlreadyExistsException("Email " + email + " is already registered");
        }
    }

    private void validateUserCredentials(String username, String email) throws IllegalArgumentException,
            UserAlreadyExistsException{
        validateEmail(email);
        if (userDao.findUserByUsername(username) != null) {
            throw new UserAlreadyExistsException("User " + username + " already exists");
        }
    }

    public User findUserById(long id) {
        User user = userDao.read(id);
        return user;
    }

    public UserDto setAvatar(MultipartFile avatar) {
        validateImageFormat(avatar);
        User currentUser = getCurrentUser();
        try {
            byte[] avatarBytes = avatar.getBytes();
            currentUser.setProfilePicture(avatarBytes);
            userDao.update(currentUser);
            logger.info("Avatar updated for user: {}", currentUser.getUsername());
        } catch (IOException e) {
            logger.error("Failed to set avatar file for user {}: {}", currentUser.getUsername(), e.getMessage());
        }
        return userMapper.toDto(currentUser);
    }

    private void validateImageFormat(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Avatar file cannot be empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
            throw new IllegalArgumentException("Only JPEG and PNG image formats are allowed");
        }
    }



}
