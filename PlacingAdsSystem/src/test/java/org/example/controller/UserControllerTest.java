package org.example.controller;

import org.example.config.GlobalExceptionHandler;
import org.example.dto.SaleHistoryDto;
import org.example.dto.UserDto;
import org.example.exceptions.UserAlreadyExistsException;
import org.example.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getCurrentUserInfo_ShouldReturnUserInfo() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setUsername("testUser");
        userDto.setEmail("test@example.com");

        when(userService.getCurrentUserInfo()).thenReturn(userDto);

        mockMvc.perform(get("/user/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void updateUserData_ShouldUpdateUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setUsername("newUsername");
        userDto.setEmail("new@example.com");

        when(userService.updateUserData(any(UserDto.class))).thenReturn(userDto);

        mockMvc.perform(patch("/user/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"newUsername\",\"email\":\"new@example.com\"}")
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("newUsername"))
                .andExpect(jsonPath("$.email").value("new@example.com"));
    }

    @Test
    void changeUsername_ShouldUpdateUsername() throws Exception {
        UserDto updatedUser = new UserDto();
        updatedUser.setUsername("newUsername");

        when(userService.changeUsername("newUsername")).thenReturn(updatedUser);

        mockMvc.perform(patch("/user/username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"newUsername\"}")
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("newUsername"));
    }

    @Test
    void changeEmail_ShouldUpdateEmail() throws Exception {
        UserDto updatedUser = new UserDto();
        updatedUser.setEmail("new@example.com");

        when(userService.changeEmail("new@example.com")).thenReturn(updatedUser);

        mockMvc.perform(patch("/user/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"newEmail\":\"new@example.com\"}")
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new@example.com"));
    }

    @Test
    void uploadAvatar_ShouldUpdateAvatar() throws Exception {
        UserDto updatedUser = new UserDto();
        updatedUser.setUsername("testUser");

        MockMultipartFile avatarFile = new MockMultipartFile(
                "avatar",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        when(userService.setAvatar(any())).thenReturn(updatedUser);

        mockMvc.perform(multipart("/user/avatar")
                                .file(avatarFile)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                                .characterEncoding("UTF-8")
                                .with(request -> {
                                    request.setMethod("PATCH");
                                    return request;
                                })
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testUser"));
    }

    @Test
    void getUserSales_ShouldReturnSales() throws Exception {
        List<SaleHistoryDto> sales = Arrays.asList(
                new SaleHistoryDto(),
                new SaleHistoryDto()
        );

        when(userService.getUserSales("testUser")).thenReturn(sales);

        mockMvc.perform(get("/user/testUser/sales"))
                .andExpect(status().isOk());
    }

    @Test
    void getUserSales_WithNonexistentUser_ShouldReturnNotFound() throws Exception {
        when(userService.getUserSales("nonexistent"))
                .thenThrow(new UsernameNotFoundException("User not found"));

        mockMvc.perform(get("/user/nonexistent/sales"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("User not found")));
    }

    @Test
    void updateUserData_WithExistingUsername_ShouldReturnConflict() throws Exception {
        when(userService.updateUserData(any(UserDto.class)))
                .thenThrow(new UserAlreadyExistsException("Username already exists"));

        mockMvc.perform(patch("/user/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"existingUser\"}")
                        .characterEncoding("utf-8"))
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString("Username already exists")));
    }

}
