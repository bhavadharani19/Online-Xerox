package com.xeroxx.backend.dto;

import com.xeroxx.backend.entity.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderRequest {
    @NotBlank
    private String fileKey;

    @NotBlank
    private String fileName;

    @Valid
    @NotNull
    private PrintOptionsDto printOptions;

    @NotNull
    private PaymentMethod paymentMethod;

    private String shopName;
    private Double shopLatitude;
    private Double shopLongitude;
}



