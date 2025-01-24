package org.example.controller;


import org.example.config.GlobalExceptionHandler;
import org.example.dto.CommentDto;
import org.example.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class CommentControllerTest {
    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(commentController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getCommentById_ShouldReturnComment() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setContent("Test comment");
        commentDto.setRating(5);

        when(commentService.getCommentById(1L)).thenReturn(commentDto);

        mockMvc.perform(get("/comments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("Test comment"))
                .andExpect(jsonPath("$.rating").value(5));
    }

    @Test
    void addComment_ShouldCreateComment() throws Exception {
        CommentDto inputDto = new CommentDto();
        inputDto.setContent("New comment");
        inputDto.setRating(4);

        CommentDto responseDto = new CommentDto();
        responseDto.setId(1L);
        responseDto.setContent("New comment");
        responseDto.setRating(4);

        when(commentService.addComment(eq(1L), any(CommentDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/ads/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"New comment\",\"rating\":4}")
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("New comment"))
                .andExpect(jsonPath("$.rating").value(4));
    }

    @Test
    void editComment_ShouldUpdateComment() throws Exception {
        CommentDto updatedDto = new CommentDto();
        updatedDto.setId(1L);
        updatedDto.setContent("Updated comment");
        updatedDto.setRating(3);

        when(commentService.editComment(eq(1L), any(CommentDto.class))).thenReturn(updatedDto);

        mockMvc.perform(patch("/ads/1/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"Updated comment\",\"rating\":3}")
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("Updated comment"))
                .andExpect(jsonPath("$.rating").value(3));
    }

    @Test
    void deleteComment_ShouldReturnNoContent() throws Exception {
        doNothing().when(commentService).deleteComment(1L);

        mockMvc.perform(delete("/ads/1/comments/1"))
                .andExpect(status().isNoContent());

        verify(commentService, times(1)).deleteComment(1L);
    }

    @Test
    void addComment_WithInvalidRating_ShouldReturnBadRequest() throws Exception {
        when(commentService.addComment(eq(1L), any(CommentDto.class)))
                .thenThrow(new IllegalArgumentException("Rating must be between 1 and 5"));

        mockMvc.perform(post("/ads/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"New comment\",\"rating\":6}")
                        .characterEncoding("utf-8"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Rating must be between 1 and 5")));
    }
}
