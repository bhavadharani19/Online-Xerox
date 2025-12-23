package com.xeroxx.backend.service;

import com.xeroxx.backend.entity.Shop;
import com.xeroxx.backend.repository.ShopRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShopService {

    private final ShopRepository shopRepository;

    public ShopService(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
    }

    public List<Shop> getNearby(double latitude, double longitude, double radiusKm) {
        return shopRepository.findAll().stream()
                .map(shop -> new ShopDistance(shop, distance(latitude, longitude, shop.getLatitude(), shop.getLongitude())))
                .filter(sd -> sd.distanceKm <= radiusKm)
                .sorted(Comparator.comparingDouble(sd -> sd.distanceKm))
                .map(sd -> sd.shop)
                .collect(Collectors.toList());
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private record ShopDistance(Shop shop, double distanceKm) {
    }
}



