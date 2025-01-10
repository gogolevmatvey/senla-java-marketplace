package org.example.service;

import org.example.dto.AdsDto;
import org.example.mapper.AdsMapper;
import org.example.model.Ads;
import org.example.model.AdsStatus;
import org.example.model.User;
import org.example.repository.AdsDao;
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

@Service
@Transactional
public class AdsService {
    private AdsDao adsDao;
    private UserDao userDao;
    private AdsMapper adsMapper;
    private static final Logger logger = LoggerFactory.getLogger(AdsService.class);

    @Value("${ads.title.max-length}")
    private int titleMaxLength;
    @Value("${ads.description.max-length}")
    private int descriptionMaxLength;

    public AdsService(AdsDao adsDao, UserDao userDao, AdsMapper adsMapper) {
        this.adsDao = adsDao;
        this.userDao = userDao;
        this.adsMapper = adsMapper;
    }

    public void createAds(Ads ads) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userDao.findUserByUsername(username);

        Ads newAds = new Ads(ads.getTitle(), ads.getCategory(), ads.getDescription(), ads.getPrice(), currentUser);
        adsDao.create(newAds);
        logger.info("Объявление {} создано пользователем {}.", newAds, currentUser.getUsername());
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
}
