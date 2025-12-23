package com.xeroxx.backend.dto;

import com.xeroxx.backend.entity.PaymentMethod;
import com.xeroxx.backend.entity.PrintOptions;
import com.xeroxx.backend.entity.PrintOrder;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class OrderSummaryResponse {
    private Long orderId;
    private String fileName;
    private PrintOptions printOptions;
    private BigDecimal price;
    private PaymentMethod paymentMethod;
    private String status;
    private String shopName;

    public static OrderSummaryResponse from(PrintOrder order) {
        return new OrderSummaryResponse(
                order.getId(),
                order.getFileName(),
                order.getPrintOptions(),
                order.getPrice(),
                order.getPaymentMethod(),
                order.getStatus().name(),
                order.getShopName()
        );
    }
}



