package com.xeroxx.backend.util;

import com.xeroxx.backend.entity.PaperSize;
import com.xeroxx.backend.entity.PrintOptions;
import com.xeroxx.backend.entity.PrintType;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class PricingCalculator {

    private PricingCalculator() {
    }

    public static BigDecimal calculate(PrintOptions options) {
        BigDecimal base = basePrice(options.getPaperSize(), options.getPrintType());
        BigDecimal price = base
                .multiply(BigDecimal.valueOf(options.getCopies()));
        if (options.isBinding()) {
            price = price.add(BigDecimal.valueOf(20));
        }
        if (options.isLamination()) {
            price = price.add(BigDecimal.valueOf(15));
        }
        if (options.isUrgent()) {
            price = price.multiply(BigDecimal.valueOf(1.25));
        }
        if (options.isDuplex()) {
            price = price.multiply(BigDecimal.valueOf(0.9));
        }
        return price.setScale(2, RoundingMode.HALF_UP);
    }

    private static BigDecimal basePrice(PaperSize size, PrintType type) {
        double base = switch (size) {
            case A3 -> 6.0;
            case A5 -> 2.0;
            default -> 3.0;
        };
        if (type == PrintType.COLOR) {
            base += 4.0;
        }
        return BigDecimal.valueOf(base);
    }
}



