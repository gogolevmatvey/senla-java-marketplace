package org.example.service;

import org.example.dto.CommentDto;
import org.example.exceptions.CommentNotFoundException;
import org.example.mapper.CommentMapper;
import org.example.model.*;
import org.example.repository.AdsDao;
import org.example.repository.CommentDao;
import org.example.repository.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
public class CommentService {
    private final AdsDao adsDao;
    private final UserDao userDao;
    private final CommentDao commentDao;
    private final CommentMapper commentMapper;
    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);

    public CommentService(AdsDao adsDao, UserDao userDao, CommentDao commentDao, CommentMapper commentMapper) {
        this.adsDao = adsDao;
        this.userDao = userDao;
        this.commentDao = commentDao;
        this.commentMapper = commentMapper;
    }

    public CommentDto addComment(Long adsId, CommentDto commentDto) {
        Ads ads = adsDao.read(adsId);
        isAdsExist(adsId, ads);
        isAdsSold(ads);
        validateCommentPermissions(ads);
        validateCommentRating(commentDto.getRating());
        validateSingleCommentPerUser(ads);
        validateBuyerPermissions(ads);

        Comment comment = commentMapper.toEntity(commentDto);
        User currentUser = getCurrentUser();
        comment.setAds(ads);
        comment.setUser(currentUser);
        comment.setCreationDate(LocalDate.now());

        ads.getComments().add(comment);
        adsDao.update(ads);

        User seller = ads.getUser();
        updateSellerRating(seller);

        logger.info("New comment added to ad {} by user {}", adsId, currentUser.getUsername());

        Comment savedComment = ads.getComments().get(ads.getComments().size() - 1);
        return commentMapper.toDto(savedComment);
    }

    private void isAdsExist(Long adsId, Ads ads) {
        if (ads == null) {
            throw new IllegalArgumentException("Advertisement not found with id: " + adsId);
        }
    }

    private void isAdsSold(Ads ads) {
        if (ads.getStatus() != AdsStatus.SOLD) {
            throw new IllegalStateException("Comments can only be added to purchased items");
        }
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userDao.findUserByUsername(username);
    }

    private void validateCommentPermissions(Ads ads) {
        User currentUser = getCurrentUser();

        if (currentUser.getRole() == UserRole.ADMIN) {
            return;
        }

        if (ads.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalStateException("You can't comment on your own advertisement");
        }
    }

    private void validateCommentRating(Integer rating) {
        if (rating == null) {
            throw new IllegalArgumentException("Rating value is required");
        }

        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
    }

    private void validateSingleCommentPerUser(Ads ads) {
        User currentUser = getCurrentUser();

        if (currentUser.getRole() == UserRole.ADMIN) {
            return;
        }

        if (commentDao.hasUserCommented(ads.getId(), currentUser.getId())) {
            throw new IllegalStateException("You have already commented on this advertisement");
        }
    }

    private void validateBuyerPermissions(Ads ads) {
        User currentUser = getCurrentUser();

        if (currentUser.getRole() == UserRole.ADMIN) {
            return;
        }

        if (!currentUser.getId().equals(ads.getBuyer().getId())) {
            throw new IllegalStateException("Only the buyer can leave a rating comment");
        }
    }

    private void updateSellerRating(User seller) {
        Double newRating = userDao.calculateAverageSellerRating(seller.getId());
        seller.setSellerRating(newRating);
        userDao.update(seller);
        logger.info("Seller {} rating updated to {}", seller.getUsername(), newRating);
    }

    public CommentDto getCommentById(Long commentId) {
        Comment comment = commentDao.read(commentId);
        isCommentExists(commentId, comment);
        return commentMapper.toDto(comment);
    }

    public CommentDto editComment(Long commentId, CommentDto updatedCommentDto) {
        Comment comment = commentDao.read(commentId);
        isCommentExists(commentId, comment);

        User currentUser = getCurrentUser();
        validateCommentOwnership(comment);

        validateCommentRating(updatedCommentDto.getRating());

        comment.setRating(updatedCommentDto.getRating());
        comment.setContent(updatedCommentDto.getContent());

        User seller = comment.getAds().getUser();
        updateSellerRating(seller);

        commentDao.update(comment);
        logger.info("Comment {} updated by user {}", commentId, currentUser.getUsername());

        return commentMapper.toDto(comment);
    }

    private void isCommentExists(Long commentId, Comment comment) {
        if (comment == null) {
            throw new CommentNotFoundException("Comment not found with id: " + commentId);
        }
    }

    private void validateCommentOwnership(Comment comment) {
        User currentUser = getCurrentUser();

        if (currentUser.getRole() == UserRole.ADMIN) {
            return;
        }

        if (!comment.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalStateException("You can only edit your own comments");
        }
    }

    public void deleteComment(Long commentId) {
        Comment comment = commentDao.read(commentId);
        isCommentExists(commentId, comment);
        validateCommentOwnership(comment);

        User seller = comment.getAds().getUser();
        commentDao.delete(comment.getId());
        updateSellerRating(seller);

        logger.info("Comment {} deleted by user {}", commentId, getCurrentUser().getUsername());
    }
}
