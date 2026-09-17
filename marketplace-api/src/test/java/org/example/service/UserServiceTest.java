package org.example.service;

import org.example.dto.UserDto;
import org.example.exceptions.UserAlreadyExistsException;
import org.example.mapper.SaleHistoryMapper;
import org.example.mapper.UserMapper;
import org.example.model.User;
import org.example.model.UserRole;
import org.example.repository.SaleHistoryDao;
import org.example.repository.UserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.mock.web.MockMultipartFile;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserDao userDao;
    @Mock
    private SaleHistoryDao saleHistoryDao;
    @Mock
    private UserMapper userMapper;
    @Mock
    private SaleHistoryMapper saleHistoryMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userDao, saleHistoryDao, userMapper, saleHistoryMapper, passwordEncoder);
    }

    @Test
    void addUser_ValidCredentials_Success() throws UserAlreadyExistsException {
        String username = "testUser";
        String email = "test@example.com";
        String password = "password123";
        String role = "USER";

        when(userDao.findUserByEmail(email)).thenReturn(null);
        when(userDao.findUserByUsername(username)).thenReturn(null);
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");

        User result = userService.addUser(username, email, password, role);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(email, result.getEmail());
        assertEquals(UserRole.USER, result.getRole());
        verify(userDao).create(any(User.class));
    }

    @Test
    void addBalance_ValidAmount_Success() {
        String username = "testUser";
        Double amount = 100.0;
        User user = new User();
        user.setBalance(50.0);
        UserDto expectedDto = new UserDto();
        expectedDto.setBalance(150.0);

        when(userDao.findUserByUsername(username)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(expectedDto);

        UserDto result = userService.addBalance(username, amount);

        assertEquals(150.0, result.getBalance());
        verify(userDao).update(user);
    }

    @Test
    void addBalance_NegativeAmount_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> userService.addBalance("testUser", -100.0));
    }

    @Test
    void setAvatar_ValidImage_Success() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testUser");

        User currentUser = new User();
        currentUser.setUsername("testUser");
        UserDto expectedDto = new UserDto();
        MockMultipartFile avatarFile = new MockMultipartFile(
                "avatar",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        when(userDao.findUserByUsername("testUser")).thenReturn(currentUser);
        when(userMapper.toDto(currentUser)).thenReturn(expectedDto);

        UserDto result = userService.setAvatar(avatarFile);

        assertNotNull(result);
        verify(userDao).update(currentUser);
    }

    @Test
    void setAvatar_InvalidFormat_ThrowsException() {
        MockMultipartFile invalidFile = new MockMultipartFile(
                "avatar",
                "test.txt",
                "text/plain",
                "test content".getBytes()
        );

        assertThrows(IllegalArgumentException.class, () -> userService.setAvatar(invalidFile));
    }

    @Test
    void changeEmail_ValidEmail_Success() throws UserAlreadyExistsException {
        String newEmail = "newemail@example.com";
        User currentUser = new User();
        currentUser.setEmail("old@example.com");
        UserDto expectedDto = new UserDto();
        expectedDto.setEmail(newEmail);

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testUser");
        when(userDao.findUserByUsername("testUser")).thenReturn(currentUser);
        when(userMapper.toDto(currentUser)).thenReturn(expectedDto);

        UserDto result = userService.changeEmail(newEmail);

        assertEquals(newEmail, result.getEmail());
        verify(userDao).update(currentUser);
    }

    @Test
    void changeEmail_InvalidFormat_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> userService.changeEmail("invalid-email"));
    }
}
