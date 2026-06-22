package com.banking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CustomerRequestDto {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Email(message = "Invalid email format")
    private String email;

    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Mobile must contain 10 digits"
    )
    private String mobileNumber;
}