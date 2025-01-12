package org.example.service;

import org.example.dto.AdsDto;
import org.example.dto.CommentDto;
import org.example.mapper.AdsMapper;
import org.example.mapper.CommentMapper;
import org.example.model.Ads;
import org.example.model.AdsStatus;
import org.example.model.Comment;
import org.example.model.User;
import org.example.repository.AdsDao;
import org.example.repository.CommentDao;
import org.example.repository.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdsService {
    private AdsDao adsDao;
    private UserDao userDao;
    private CommentDao commentDao;
    private AdsMapper adsMapper;
    private CommentMapper commentMapper;
    private static final Logger logger = LoggerFactory.getLogger(AdsService.class);

    @Value("${ads.title.max-length}")
    private int titleMaxLength;
    @Value("${ads.description.max-length}")
    private int descriptionMaxLength;

    public AdsService(AdsDao adsDao, UserDao userDao, CommentDao commentDao, AdsMapper adsMapper, CommentMapper commentMapper) {
        this.adsDao = adsDao;
        this.userDao = userDao;
        this.commentDao = commentDao;
        this.adsMapper = adsMapper;
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

    private void isAdsSold(Ads ads) {
        if (ads.getStatus() != AdsStatus.SOLD) {
            throw new IllegalStateException("Comments can only be added to purchased items");
        }
    }

    private void validateCommentPermissions(Ads ads) {
        if (ads.getUser().getId().equals(getCurrentUser().getId())) {
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
        if (commentDao.hasUserCommented(ads.getId(), currentUser.getId())) {
            throw new IllegalStateException("You have already commented on this advertisement");
        }
    }

    private void validateBuyerPermissions(Ads ads) {
        User currentUser = getCurrentUser();
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

    public AdsDto createAds(AdsDto adsDto) {
        Ads newAds = adsMapper.toEntity(adsDto);
        validateAds(newAds);
        User currentUser = getCurrentUser();
        newAds.setUser(currentUser);
        newAds.setCreationDate(LocalDate.now());
        newAds.setStatus(AdsStatus.ACTIVE);
        adsDao.create(newAds);
        logger.info("Ads {} is added to DB.", newAds);
        return adsMapper.toDto(newAds);
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userDao.findUserByUsername(username);
    }

    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Main image is required");
        }

        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
            throw new IllegalArgumentException("Only JPEG and PNG image formats are allowed");
        }
    }

    private void validateAds(Ads ads) {
        if (ads.getTitle() == null || ads.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title can't be empty");
        }
        if (ads.getTitle().length() > titleMaxLength) {
            throw new IllegalArgumentException("Title can't be longer than " + titleMaxLength + " characters");
        }
        if (ads.getDescription() == null || ads.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Description can't be empty");
        }
        if (ads.getDescription().length() > descriptionMaxLength) {
            throw new IllegalArgumentException("Description can't be longer than " + descriptionMaxLength + " characters");
        }
        if (ads.getPrice() < 0) {
            throw new IllegalArgumentException("Цена не может быть меньше нуля.");
        }
    }

    public AdsDto changeTitle(Long adsId, String newTitle) {
        Ads ads = adsDao.read(adsId);
        isAdsExist(adsId, ads);

        validateUserPermissions(ads);
        validateTitle(newTitle);

        ads.setTitle(newTitle);
        adsDao.update(ads);
        logger.info("Title updated for advertisement id: {}", adsId);

        return adsMapper.toDto(ads);
    }

    private void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title can't be empty");
        }
        if (title.length() > titleMaxLength) {
            throw new IllegalArgumentException("Title can't be longer than " + titleMaxLength + " characters");
        }

    }

    private void isAdsExist(Long adsId, Ads ads) {
        if (ads == null) {
            throw new IllegalArgumentException("Advertisement not found with id: " + adsId);
        }
    }

    private void validateUserPermissions(Ads ads) {
        User currentUser = getCurrentUser();
        if (!ads.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalStateException("User doesn't have permission to modify this advertisement");
        }
    }

    public AdsDto changeDescription(Long adsId, String newDescription) {
        Ads ads = adsDao.read(adsId);
        isAdsExist(adsId, ads);

        validateUserPermissions(ads);
        validateDescription(newDescription);

        ads.setDescription(newDescription);
        adsDao.update(ads);
        logger.info("Description updated for advertisement id: {}", adsId);

        return adsMapper.toDto(ads);
    }

    private void validateDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description can't be empty");
        }
        if (description.length() > descriptionMaxLength) {
            throw new IllegalArgumentException("Description can't be longer than " + descriptionMaxLength + " characters");
        }
    }

    public AdsDto changeCategory(Long adsId, String newCategory) {
        Ads ads = adsDao.read(adsId);
        isAdsExist(adsId, ads);

        validateUserPermissions(ads);
        validateCategory(newCategory);

        ads.setCategory(newCategory);
        adsDao.update(ads);
        logger.info("Category updated for advertisement id: {}", adsId);

        return adsMapper.toDto(ads);
    }

    private void validateCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category can't be empty");
        }
    }

    public AdsDto changePrice(Long adsId, double newPrice) {
        Ads ads = adsDao.read(adsId);
        isAdsExist(adsId, ads);

        validateUserPermissions(ads);
        validatePrice(newPrice);

        ads.setPrice(newPrice);
        adsDao.update(ads);
        logger.info("Price updated for advertisement id: {}", adsId);

        return adsMapper.toDto(ads);
    }

    private void validatePrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Price can't be negative");
        }
    }

    public AdsDto changeStatus(Long adsId, AdsStatus newStatus) {
        Ads ads = adsDao.read(adsId);
        isAdsExist(adsId, ads);

        validateUserPermissions(ads);
        validateStatus(newStatus);

        ads.setStatus(newStatus);
        adsDao.update(ads);
        logger.info("Status updated to {} for advertisement id: {}", newStatus, adsId);

        return adsMapper.toDto(ads);
    }

    private void validateStatus(AdsStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status can't be null");
        }
        if (status != AdsStatus.ACTIVE && status != AdsStatus.INACTIVE && status != AdsStatus.SOLD) {
            throw new IllegalArgumentException("Wrong status");
        }
    }

    public void deleteAds(Long adsId) {
        Ads ads = adsDao.read(adsId);
        isAdsExist(adsId, ads);

        validateUserPermissions(ads);

        ads.setStatus(AdsStatus.DELETED);
        adsDao.update(ads);
        logger.info("Advertisement with id: {} has been marked as deleted", adsId);
    }

    public AdsDto markAsSold(Long adsId, String buyerName) {
        User currentUser = getCurrentUser();
        User buyer = userDao.findUserByUsername(buyerName);
        Ads ads = adsDao.read(adsId);

        if (buyer == null) {
            throw new IllegalArgumentException("Buyer not found");
        }

        isAdsExist(adsId, ads);
        validateUserPermissions(ads);

        if (!ads.getStatus().equals(AdsStatus.ACTIVE)) {
            throw new IllegalStateException("Only active advertisements can be marked as sold");
        }

        ads.setStatus(AdsStatus.SOLD);
        ads.setBuyer(buyer);
        adsDao.update(ads);

        logger.info("Advertisement ID: {} marked as SOLD. Buyer: {}", adsId, buyer.getUsername());
        return adsMapper.toDto(ads);
    }

    public Ads getAdsById(int id) {
        return adsDao.read(id);
    }

    public AdsDto setMainImage(Long adsId, MultipartFile image) {
        validateImage(image);
        Ads ads = adsDao.read(adsId);
        isAdsExist(adsId, ads);

        validateUserPermissions(ads);

        try {
            ads.setMainImage(image.getBytes());
            adsDao.update(ads);
            logger.info("Main image updated for advertisement id: {}", adsId);
        } catch (IOException e) {
            logger.error("Error processing image upload for advertisement id: {}", adsId, e);
            throw new RuntimeException("Failed to process image upload", e);
        }
        return adsMapper.toDto(ads);
    }

    public List<AdsDto> searchAds(String keyword, String category, Double minPrice, Double maxPrice, AdsStatus status) {
        if (status == null)
            status = AdsStatus.ACTIVE;
        validateSearchInput(minPrice, maxPrice);

        List<Ads> foundAds = adsDao.searchAds(keyword, category, minPrice, maxPrice, status);

        return foundAds.stream().map(adsMapper::toDto).collect(Collectors.toList());
    }

    private void validateSearchInput(Double minPrice, Double maxPrice) {
        if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            throw new IllegalArgumentException("Minimum price can't be greater than maximum price");
        }

        if (minPrice != null && minPrice < 0) {
            throw new IllegalArgumentException("Minimum price can't be negative");
        }

        if (maxPrice != null && maxPrice < 0) {
            throw new IllegalArgumentException("Maximum price can't be negative");
        }
    }

    public AdsDto promoteAds(Long adsId, int promotionDays) {
        Ads ads = adsDao.read(adsId);
        isAdsExist(adsId, ads);
        validateUserPermissions(ads);
        validatePromotionDays(promotionDays);

        LocalDate now = LocalDate.now();
        ads.setPromoted(true);
        ads.setPromotionStartDate(now);
        ads.setPromotionEndDate(now.plusDays(promotionDays));

        adsDao.update(ads);
        logger.info("Advertisement id: {} promoted for {} days", adsId, promotionDays);

        return adsMapper.toDto(ads);
    }

    private void validatePromotionDays(int days) {
        if (days <= 0) {
            throw new IllegalArgumentException("Promotion period must be positive");
        }
        if (days > 30) {
            throw new IllegalArgumentException("Maximum promotion period is 30 days");
        }
    }
}
