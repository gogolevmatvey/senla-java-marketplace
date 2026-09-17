package org.example.service;

import org.example.dto.CommentDto;
import org.example.exceptions.CommentNotFoundException;
import org.example.mapper.CommentMapper;
import org.example.model.*;
import org.example.repository.AdsDao;
import org.example.repository.CommentDao;
import org.example.repository.UserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    @Mock
    private AdsDao adsDao;
    @Mock
    private UserDao userDao;
    @Mock
    private CommentDao commentDao;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    private CommentService commentService;
    private User testUser;
    private User sellerUser;
    private Ads testAds;
    private Comment testComment;
    private CommentDto testCommentDto;

    @BeforeEach
    void setUp() {
        commentService = new CommentService(adsDao, userDao, commentDao, commentMapper);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("buyer");
        testUser.setRole(UserRole.USER);

        sellerUser = new User();
        sellerUser.setId(2L);
        sellerUser.setUsername("seller");

        testAds = new Ads();
        testAds.setId(1L);
        testAds.setUser(sellerUser);
        testAds.setStatus(AdsStatus.SOLD);
        testAds.setBuyer(testUser);
        testAds.setComments(new ArrayList<>());

        testComment = new Comment();
        testComment.setId(1L);
        testComment.setUser(testUser);
        testComment.setAds(testAds);
        testComment.setRating(5);
        testComment.setContent("Great product!");

        testCommentDto = new CommentDto();
        testCommentDto.setRating(5);
        testCommentDto.setContent("Great product!");
    }

    private void setupSecurityContext() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("buyer");
    }

    @Test
    void addComment_ValidComment_Success() {
        when(adsDao.read(1L)).thenReturn(testAds);
        when(userDao.findUserByUsername("buyer")).thenReturn(testUser);
        when(commentMapper.toEntity(testCommentDto)).thenReturn(testComment);
        when(commentMapper.toDto(any(Comment.class))).thenReturn(testCommentDto);
        when(commentDao.hasUserCommented(1L, 1L)).thenReturn(false);

        CommentDto result = commentService.addComment(1L, testCommentDto);

        assertNotNull(result);
        assertEquals(5, result.getRating());
        assertEquals("Great product!", result.getContent());
        verify(adsDao).update(testAds);
    }

    @Test
    void addComment_NonExistentAd_ThrowsException() {
        when(adsDao.read(1L)).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> commentService.addComment(1L, testCommentDto));
    }

    @Test
    void addComment_UnsoldAd_ThrowsException() {
        testAds.setStatus(AdsStatus.ACTIVE);
        when(adsDao.read(1L)).thenReturn(testAds);

        assertThrows(IllegalStateException.class, () -> commentService.addComment(1L, testCommentDto));
    }

    @Test
    void editComment_ValidEdit_Success() {
        when(commentDao.read(1L)).thenReturn(testComment);
        when(userDao.findUserByUsername("buyer")).thenReturn(testUser);
        when(commentMapper.toDto(any(Comment.class))).thenReturn(testCommentDto);

        CommentDto updatedDto = new CommentDto();
        updatedDto.setRating(4);
        updatedDto.setContent("Updated content");

        CommentDto result = commentService.editComment(1L, updatedDto);

        assertNotNull(result);
        verify(commentDao).update(testComment);
    }

    @Test
    void editComment_NonExistentComment_ThrowsException() {
        when(commentDao.read(1L)).thenReturn(null);

        assertThrows(CommentNotFoundException.class, () -> commentService.editComment(1L, testCommentDto));
    }

    @Test
    void deleteComment_ValidDelete_Success() {
        when(commentDao.read(1L)).thenReturn(testComment);
        when(userDao.findUserByUsername("buyer")).thenReturn(testUser);

        assertDoesNotThrow(() -> commentService.deleteComment(1L));
        verify(commentDao).delete(1L);
    }

    @Test
    void deleteComment_UnauthorizedUser_ThrowsException() {
        User unauthorizedUser = new User();
        unauthorizedUser.setId(3L);
        unauthorizedUser.setUsername("unauthorized");
        unauthorizedUser.setRole(UserRole.USER);

        when(commentDao.read(1L)).thenReturn(testComment);
        when(userDao.findUserByUsername("buyer")).thenReturn(unauthorizedUser);

        assertThrows(IllegalStateException.class,
                () -> commentService.deleteComment(1L));
    }

    @Test
    void addComment_InvalidRating_ThrowsException() {
        setupSecurityContext();

        when(adsDao.read(1L)).thenReturn(testAds);
        when(userDao.findUserByUsername("buyer")).thenReturn(testUser);

        testCommentDto.setRating(6);

        assertThrows(IllegalArgumentException.class, () -> commentService.addComment(1L, testCommentDto));
    }

    @Test
    void addComment_DuplicateComment_ThrowsException() {
        when(adsDao.read(1L)).thenReturn(testAds);
        when(userDao.findUserByUsername("buyer")).thenReturn(testUser);
        when(commentDao.hasUserCommented(1L, 1L)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> commentService.addComment(1L, testCommentDto));
    }
}
