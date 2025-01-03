package org.example.service;

import org.example.exceptions.UserAlreadyExistsException;
import org.example.model.User;
import org.example.model.UserRole;
import org.example.repository.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class UserService {
    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(UserDao userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    public User addUser(String username, String password, String role) throws UserAlreadyExistsException {
        if (userDao.findUserByUsername(username) != null)
            throw new UserAlreadyExistsException("Пользователь " + username + " уже существует.");

        password = passwordEncoder.encode(password);
        User user = new User(username, password, UserRole.valueOf(role.toUpperCase()));
        userDao.create(user);
        logger.info("Пользователь {} добавлен в БД.", user);
        return user;
    }
}
