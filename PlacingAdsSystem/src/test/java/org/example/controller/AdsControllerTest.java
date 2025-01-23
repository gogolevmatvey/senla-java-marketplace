package org.example.controller;

import org.example.dto.AdsDto;
import org.example.dto.PageResponse;
import org.example.model.AdsStatus;
import org.example.service.AdsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AdsControllerTest {
    private MockMvc mockMvc;

    @Mock
    private AdsService adsService;

    @InjectMocks
    private AdsController adsController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adsController).build();
    }

    @Test
    void getAdsById_ShouldReturnAds() throws Exception {
        AdsDto adsDto = new AdsDto();
        adsDto.setId(1L);
        adsDto.setTitle("Test Ad");

        when(adsService.getAdsById(1L)).thenReturn(adsDto);

        mockMvc.perform(get("/ads/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Ad"));
    }

    @Test
    void createAds_ShouldReturnCreatedAds() throws Exception {
        AdsDto inputDto = new AdsDto();
        inputDto.setTitle("New Ad");

        AdsDto createdDto = new AdsDto();
        createdDto.setId(1L);
        createdDto.setTitle("New Ad");

        when(adsService.createAds(any(AdsDto.class))).thenReturn(createdDto);

        mockMvc.perform(post("/ads/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"New Ad\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void setMainImage_ShouldUpdateImage() throws Exception {
        AdsDto updatedDto = new AdsDto();
        updatedDto.setId(1L);

        MockMultipartFile file = new MockMultipartFile(
                "image",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        when(adsService.setMainImage(eq(1L), any())).thenReturn(updatedDto);

        mockMvc.perform(multipart("/ads/1/main-image")
                        .file(file)
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        })
                )
                .andExpect(status().isOk());
    }

    @Test
    void searchAds_ShouldReturnPagedResults() throws Exception {
        PageResponse<AdsDto> pageResponse = new PageResponse<>();
        pageResponse.setContent(new ArrayList<>());
        pageResponse.setTotalPages(1);
        pageResponse.setTotalElements(0L);

        when(adsService.searchAds(
                eq("test"),
                eq("electronics"),
                eq(10.0),
                eq(100.0),
                eq(AdsStatus.ACTIVE),
                isNull(),
                eq(1),
                eq(10)
        )).thenReturn(pageResponse);

        mockMvc.perform(get("/ads/search")
                        .param("keyword", "test")
                        .param("category", "electronics")
                        .param("minPrice", "10.0")
                        .param("maxPrice", "100.0")
                        .param("status", "ACTIVE")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void promoteAds_ShouldReturnPromotedAds() throws Exception {
        AdsDto promotedDto = new AdsDto();
        promotedDto.setId(1L);
        promotedDto.setPromoted(true);

        when(adsService.promoteAds(eq(1L), anyInt())).thenReturn(promotedDto);

        mockMvc.perform(post("/ads/1/promote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"promotionDays\":7}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.promoted").value(true));
    }

    @Test
    void purchaseAds_ShouldReturnPurchasedAds() throws Exception {
        AdsDto purchasedDto = new AdsDto();
        purchasedDto.setId(1L);
        purchasedDto.setStatus(AdsStatus.SOLD);

        when(adsService.purchaseAds(1L)).thenReturn(purchasedDto);

        mockMvc.perform(post("/ads/1/purchase"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SOLD"));
    }

    @Test
    void deleteAds_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/ads/1"))
                .andExpect(status().isNoContent());
    }
}
