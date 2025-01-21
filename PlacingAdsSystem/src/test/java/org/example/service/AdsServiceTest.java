package org.example.service;

import org.example.dto.AdsDto;
import org.example.mapper.AdsMapper;
import org.example.model.*;
import org.example.repository.AdsDao;
import org.example.repository.SaleHistoryDao;
import org.example.repository.UserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdsServiceTest {
    @Mock
    private AdsDao adsDao;
    @Mock
    private UserDao userDao;
    @Mock
    private SaleHistoryDao saleHistoryDao;
    @Mock
    private AdsMapper adsMapper;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    // @InjectMocks
    private AdsService adsService;
    private User testUser;
    private Ads testAds;

    @BeforeEach
    void setUp() {
        adsService = new AdsService(adsDao, userDao, saleHistoryDao, adsMapper);
        ReflectionTestUtils.setField(adsService, "titleMaxLength", 100);
        ReflectionTestUtils.setField(adsService, "descriptionMaxLength", 1000);
        ReflectionTestUtils.setField(adsService, "promotionDailyPrice", 10.0);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");
        testUser.setBalance(1000.0);
        testUser.setRole(UserRole.USER);

        testAds = new Ads();
        testAds.setId(1L);
        testAds.setTitle("Test Ad");
        testAds.setDescription("Test Description");
        testAds.setPrice(100.0);
        testAds.setStatus(AdsStatus.ACTIVE);
        testAds.setUser(testUser);

        SecurityContextHolder.setContext(securityContext);
//        when(securityContext.getAuthentication()).thenReturn(authentication);
//        when(authentication.getName()).thenReturn("testUser");
//        when(userDao.findUserByUsername("testUser")).thenReturn(testUser);
    }

    @Test
    void purchaseAds_SuccessfulPurchase() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("buyer");

        User buyer = new User();
        buyer.setId(2L);
        buyer.setUsername("buyer");
        buyer.setBalance(200.0);

        when(adsDao.read(1L)).thenReturn(testAds);
        when(userDao.findUserByUsername("buyer")).thenReturn(buyer);

        adsService.purchaseAds(1L);

        assertEquals(AdsStatus.SOLD, testAds.getStatus());
        assertEquals(buyer, testAds.getBuyer());
        assertEquals(100.0, buyer.getBalance());
        assertEquals(1100.0, testUser.getBalance());
        verify(saleHistoryDao).create(any(SaleHistory.class));
    }

    @Test
    void purchaseAds_InsufficientFunds() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("buyer");

        User buyer = new User();
        buyer.setId(2L);
        buyer.setBalance(50.0);
        when(userDao.findUserByUsername("buyer")).thenReturn(buyer);

        when(adsDao.read(1L)).thenReturn(testAds);

        assertThrows(IllegalStateException.class, () -> adsService.purchaseAds(1L));
    }

    @Test
    void promoteAds_SuccessfulPromotion() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testUser");
        when(userDao.findUserByUsername("testUser")).thenReturn(testUser);

        when(adsDao.read(1L)).thenReturn(testAds);

        adsService.promoteAds(1L, 5);

        assertTrue(testAds.isPromoted());
        assertNotNull(testAds.getPromotionStartDate());
        assertNotNull(testAds.getPromotionEndDate());
        assertEquals(950.0, testUser.getBalance()); // 1000 - (5 * 10)
        verify(adsDao).update(testAds);
    }

    @Test
    void promoteAds_InvalidDuration() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testUser");
        when(userDao.findUserByUsername("testUser")).thenReturn(testUser);

        when(adsDao.read(1L)).thenReturn(testAds);

        assertThrows(IllegalArgumentException.class, () -> adsService.promoteAds(1L, 0));
        assertThrows(IllegalArgumentException.class, () -> adsService.promoteAds(1L, 31));
    }

    @Test
    void changeStatus_AdminCanChangeAnyAd() {
        User adminUser = new User();
        adminUser.setRole(UserRole.ADMIN);

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin");
        when(userDao.findUserByUsername("admin")).thenReturn(adminUser);

        when(adsDao.read(1L)).thenReturn(testAds);

        adsService.changeStatus(1L, AdsStatus.INACTIVE);

        assertEquals(AdsStatus.INACTIVE, testAds.getStatus());
        verify(adsDao).update(testAds);
    }

    @Test
    void updateAds_ValidationFailure() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testUser");
        when(userDao.findUserByUsername("testUser")).thenReturn(testUser);

        when(adsDao.read(1L)).thenReturn(testAds);

        AdsDto invalidDto = new AdsDto();
        invalidDto.setPrice(-100.0);

        assertThrows(IllegalArgumentException.class, () -> adsService.updateAds(1L, invalidDto));
    }

    @Test
    void deleteAds_MarksAsDeleted() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testUser");
        when(userDao.findUserByUsername("testUser")).thenReturn(testUser);

        when(adsDao.read(1L)).thenReturn(testAds);

        adsService.deleteAds(1L);

        assertEquals(AdsStatus.DELETED, testAds.getStatus());
        verify(adsDao).update(testAds);
    }

    @Test
    void searchAds_ValidatesSearchParameters() {
        assertThrows(IllegalArgumentException.class,
                () -> adsService.searchAds(null, null, 100.0, 50.0, null, null, 1, 10));
        assertThrows(IllegalArgumentException.class,
                () -> adsService.searchAds(null, null, -10.0, 50.0, null, null, 1, 10));
        assertThrows(IllegalArgumentException.class,
                () -> adsService.searchAds(null, null, null, null, null, null, 0, 10));
    }
}
