package org.example.controller;

import org.example.dto.AdsDto;
import org.example.model.Ads;
import org.example.service.AdsService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ads")
public class AdsController {
    private AdsService adsService;

    public AdsController(AdsService adsService) {
        this.adsService = adsService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createAds(@RequestBody AdsDto adsDto) {
        Ads createdAds = adsService.createAds(adsDto);
        return ResponseEntity.ok(createdAds);
    }
}
