package com.xeroxx.backend.dto;

import com.xeroxx.backend.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderStatusUpdateRequest {
    @NotNull
    private OrderStatus status;
}



