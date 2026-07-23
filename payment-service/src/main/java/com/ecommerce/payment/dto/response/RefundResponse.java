package com.ecommerce.payment.dto.response;

import com.ecommerce.payment.enums.RefundStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO representing refund details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundResponse {

    private UUID id;
    private UUID paymentId;
    private BigDecimal amount;
    private RefundStatus status;
    private String reason;
    private String refundTransactionId;
    private LocalDateTime createdAt;
}
