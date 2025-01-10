package org.example.controller;

import org.example.dto.*;
import org.example.model.Ads;
import org.example.service.AdsService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/ads")
public class AdsController {
    private AdsService adsService;

    public AdsController(AdsService adsService) {
        this.adsService = adsService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createAds(@RequestBody AdsDto adsDto) {
        AdsDto createdAds = adsService.createAds(adsDto);
        return ResponseEntity.ok(createdAds);
    }

    @PatchMapping("/{adsId}/main-image")
    public ResponseEntity<?> setMainImage(@PathVariable("adsId") Long id, @RequestParam("image") MultipartFile image) {
        AdsDto adsDto = adsService.setMainImage(id, image);
        return ResponseEntity.ok(adsDto);
    }

    @PatchMapping("/{adsId}/title")
    public ResponseEntity<?> changeTitle(@PathVariable("adsId") Long adsId, @RequestBody TitleDto titleDto) {
        AdsDto updatedAds = adsService.changeTitle(adsId, titleDto.getTitle());
        return ResponseEntity.ok(updatedAds);
    }

    @PatchMapping("/{adsId}/category")
    public ResponseEntity<?> changeCategory(@PathVariable("adsId") Long adsId, @RequestBody CategoryDto categoryDto) {
        AdsDto updatedAds = adsService.changeCategory(adsId, categoryDto.getCategory());
        return ResponseEntity.ok(updatedAds);
    }

    @PatchMapping("/{adsId}/description")
    public ResponseEntity<?> updateDescription(@PathVariable("adsId") Long adsId, @RequestBody DescriptionDto descriptionDto) {
        AdsDto updatedAds = adsService.changeDescription(adsId, descriptionDto.getDescription());
        return ResponseEntity.ok(updatedAds);
    }

    @PatchMapping("/{adsId}/price")
    public ResponseEntity<?> changePrice(@PathVariable("adsId") Long adsId, @RequestBody PriceDto priceDto) {
        AdsDto updatedAds = adsService.changePrice(adsId, priceDto.getPrice());
        return ResponseEntity.ok(updatedAds);
    }
}
