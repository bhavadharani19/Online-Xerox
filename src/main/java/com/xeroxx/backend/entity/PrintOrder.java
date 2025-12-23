package com.xeroxx.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "print_orders")
public class PrintOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String fileName;
    private String storageKey;
    private String fileUrl;

    @Embedded
    private PrintOptions printOptions;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private BigDecimal price;

    private String shopName;
    private Double shopLatitude;
    private Double shopLongitude;

    private Instant createdAt;
    private Instant updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = Instant.now();
        this.status = this.status == null ? OrderStatus.PENDING : this.status;
        this.paymentStatus = this.paymentStatus == null ? PaymentStatus.PENDING : this.paymentStatus;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = Instant.now();
    }
}



