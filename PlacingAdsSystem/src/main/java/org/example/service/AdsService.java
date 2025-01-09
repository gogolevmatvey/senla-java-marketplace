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

    public Ads getAdsById(int id) {
        return adsDao.read(id);
    }

    public void validateAds(Ads ads) {
        if (ads.getTitle() == null || ads.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Заголовок не может быть пустым.");
        }
        if (ads.getTitle().length() > titleMaxLength) {
            throw new IllegalArgumentException("Заголовок не може быть длиеннее " + titleMaxLength + " символов.");
        }
        if (ads.getDescription() == null || ads.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Описание не может быть пустым.");
        }
        if (ads.getDescription().length() > descriptionMaxLength) {
            throw new IllegalArgumentException("Описание не может быть длиннее " + descriptionMaxLength + " символов.");
        }
        if (ads.getPrice() < 0) {
            throw new IllegalArgumentException("Цена не может быть меньше нуля.");
        }
    }
}
