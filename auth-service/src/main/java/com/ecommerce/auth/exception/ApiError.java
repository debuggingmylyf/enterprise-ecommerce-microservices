package com.ecommerce.auth.exception;

import com.ecommerce.auth.constants.ErrorCode;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiError {

    private LocalDateTime timestamp;

    private int status;

    private ErrorCode errorCode;

    private String message;

    private String path;
}
