package org.example.controller;

import org.example.dto.*;
import org.example.model.AdsStatus;
import org.example.service.AdsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    @PatchMapping("/{adsId}/status")
    public ResponseEntity<?> changeStatus(@PathVariable("adsId") Long adsId, @RequestBody StatusDto statusDto) {
        AdsDto updatedAds = adsService.changeStatus(adsId, statusDto.getStatus());
        return ResponseEntity.ok(updatedAds);
    }

    @DeleteMapping("/{adsId}")
    public ResponseEntity<?> deleteAds(@PathVariable("adsId") Long adsId) {
        adsService.deleteAds(adsId);
        return ResponseEntity.ok(new MessageResponse("Advertisement successfully deleted"));
    }

    @PostMapping("/{adsId}/mark-sold")
    public ResponseEntity<?> markAsSold(@PathVariable("adsId") Long adsId, @RequestBody UsernameDto usernameDto) {
        AdsDto updatedAds = adsService.markAsSold(adsId, usernameDto.getUsername());
        return ResponseEntity.ok(updatedAds);
    }

    @GetMapping("/search")
    public ResponseEntity<List<AdsDto>> searchAds(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "minPrice", required = false) Double minPrice,
            @RequestParam(name = "maxPrice", required = false) Double maxPrice,
            @RequestParam(name = "status", required = false) AdsStatus status) {

        List<AdsDto> ads = adsService.searchAds(keyword, category, minPrice, maxPrice, status);
        return ResponseEntity.ok(ads);
    }

    @PostMapping("/{adsId}/comment")
    public ResponseEntity<?> addComment(@PathVariable("adsId") Long adsId, @RequestBody CommentDto commentDto) {
        CommentDto createdComment = adsService.addComment(adsId, commentDto);
        return ResponseEntity.ok(createdComment);
    }

    @PostMapping("/{adsId}/promote")
    public ResponseEntity<?> promoteAds(@PathVariable("adsId") Long adsId,
                                        @RequestBody PromotionDaysDto promotionDaysDto) {
        AdsDto promotedAds = adsService.promoteAds(adsId, promotionDaysDto.getPromotionDays());
        return ResponseEntity.ok(promotedAds);
    }
}
