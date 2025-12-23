package com.xeroxx.backend.controller;

import com.xeroxx.backend.entity.Shop;
import com.xeroxx.backend.service.ShopService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shops")
public class ShopController {

    private final ShopService shopService;

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<Shop>> nearby(@RequestParam double lat,
                                             @RequestParam double lon,
                                             @RequestParam(defaultValue = "10") double radiusKm) {
        return ResponseEntity.ok(shopService.getNearby(lat, lon, radiusKm));
    }
}



