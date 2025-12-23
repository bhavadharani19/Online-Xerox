package com.xeroxx.backend.service;

import com.xeroxx.backend.dto.OrderRequest;
import com.xeroxx.backend.dto.OrderSummaryResponse;
import com.xeroxx.backend.dto.PrintOptionsDto;
import com.xeroxx.backend.entity.*;
import com.xeroxx.backend.repository.OrderRepository;
import com.xeroxx.backend.repository.UserRepository;  
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static com.xeroxx.backend.util.PricingCalculator.calculate;


@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final PaymentService paymentService;
    private final NotificationService notificationService;

    public OrderService(OrderRepository orderRepository,
                        UserRepository userRepository,
                        PaymentService paymentService,
                        NotificationService notificationService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.paymentService = paymentService;
        this.notificationService = notificationService;
    }

    @Transactional
    public OrderSummaryResponse createOrder(OrderRequest request, String username) {

        User user = loadUser(username);

        PrintOptions printOptions = mapOptions(request.getPrintOptions());
        BigDecimal price = calculate(printOptions);

        PrintOrder order = PrintOrder.builder()
                .user(user)
                .fileName(request.getFileName())
                .storageKey(request.getFileKey())
                .fileUrl("file://" + request.getFileKey())
                .printOptions(printOptions)
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(PaymentStatus.PENDING)
                .status(OrderStatus.PENDING)
                .price(price)
                .shopName(request.getShopName())
                .shopLatitude(request.getShopLatitude())
                .shopLongitude(request.getShopLongitude())
                .build();

        PrintOrder saved = orderRepository.save(order);

        // paymentService.initiatePayment(saved);
        notificationService.notifyStatus(saved);

        return OrderSummaryResponse.from(saved);
    }

    public List<OrderSummaryResponse> list(String username) {
        User user = loadUser(username);
        return orderRepository.findByUser(user)
                .stream()
                .map(OrderSummaryResponse::from)
                .toList();
    }

    public OrderSummaryResponse getById(Long orderId, String username) {
        User user = loadUser(username);

        PrintOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Access denied");
        }

        return OrderSummaryResponse.from(order);
    }

    @Transactional
    public OrderSummaryResponse updateStatus(Long orderId, OrderStatus status) {
        PrintOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        order.setStatus(status);
        PrintOrder saved = orderRepository.save(order);

        notificationService.notifyStatus(saved);

        return OrderSummaryResponse.from(saved);
    }


    private User loadUser(String identifier) {
        return userRepository.findByEmail(identifier)
                .or(() -> userRepository.findByMobile(identifier))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private PrintOptions mapOptions(PrintOptionsDto dto) {
        return PrintOptions.builder()
                .paperSize(dto.getPaperSize())
                .printType(dto.getPrintType())
                .copies(dto.getCopies())
                .binding(dto.isBinding())
                .lamination(dto.isLamination())
                .urgent(dto.isUrgent())
                .duplex(dto.isDuplex())
                .additionalNotes(dto.getAdditionalNotes())
                .build();
    }
}



