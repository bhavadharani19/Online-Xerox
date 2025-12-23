package com.xeroxx.backend.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.xeroxx.backend.entity.PaymentMethod;
import com.xeroxx.backend.entity.PrintOrder;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    @Value("${payment.razorpay.key:}")
    private String razorpayKey;

    @Value("${payment.razorpay.secret:}")
    private String razorpaySecret;

    public String initiatePayment(PrintOrder order) {
        if (order.getPaymentMethod() == PaymentMethod.RAZORPAY) {
            return createRazorpayOrder(order);
        }
        if (order.getPaymentMethod() == PaymentMethod.PAYTM) {
            log.info("Initiating Paytm payment for order {}", order.getId());
            return "paytm_txn_placeholder";
        }
        log.info("Pay-at-shop chosen for order {}", order.getId());
        return "pay_at_shop";
    }

    private String createRazorpayOrder(PrintOrder order) {
        if (razorpayKey.isBlank() || razorpaySecret.isBlank()) {
            log.warn("Razorpay keys not configured, skipping gateway call");
            return "razorpay_disabled";
        }

        try {
            RazorpayClient client = new RazorpayClient(razorpayKey, razorpaySecret);

            JSONObject options = new JSONObject();
            BigDecimal amount = order.getPrice()
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(0, RoundingMode.HALF_UP);

            options.put("amount", amount); // in paise
            options.put("currency", "INR");
            options.put("receipt", "order_" + order.getId());

            Order razorpayOrder = client.orders.create(options);

            String razorpayOrderId = razorpayOrder.get("id").toString();
            log.info("Razorpay order created with id {}", razorpayOrderId);

            return razorpayOrderId;

        } catch (Exception e) {
            log.error("Razorpay payment init failed", e);
            return "razorpay_error";
        }
    }
}
