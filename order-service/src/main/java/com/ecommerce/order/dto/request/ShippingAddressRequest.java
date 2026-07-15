package com.ecommerce.order.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Request DTO representing a shipping address.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingAddressRequest {

    @NotBlank(message = "Shipping name is required")
    private String shippingName;

    @NotBlank(message = "Shipping phone number is required")
    private String shippingPhone;

    @NotBlank(message = "Address line 1 is required")
    private String addressLine1;

    private String addressLine2;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Country is required")
    private String country;

    @NotBlank(message = "Postal code is required")
    private String postalCode;
}
