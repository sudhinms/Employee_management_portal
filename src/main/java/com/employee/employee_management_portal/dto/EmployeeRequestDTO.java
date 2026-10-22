package com.employee.employee_management_portal.dto;

import com.employee.employee_management_portal.entity.EmployeeStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Request payload for creating or updating an employee")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class EmployeeRequestDTO {

    @Schema(description = "Employee's first name", example = "John")
    @NotBlank(message = "First name is mandatory")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @Schema(description = "Employee's last name", example = "Doe")
    @NotBlank(message = "Last name is mandatory")
    @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
    private String lastName;

    @Schema(description = "Employee's email address", example = "john.doe@example.com")
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email must be a valid email address")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;

    @Schema(description = "Department name", example = "Engineering")
    @NotBlank(message = "Department is mandatory")
    @Size(max = 50, message = "Department cannot exceed 50 characters")
    private String department;

    @Schema(description = "Job title / Designation", example = "Senior Software Engineer")
    @NotBlank(message = "Job title is mandatory")
    @Size(max = 50, message = "Job title cannot exceed 50 characters")
    private String jobTitle;

    @Schema(description = "Monthly / Annual salary", example = "85000.00")
    @NotNull(message = "Salary is mandatory")
    @DecimalMin(value = "0.0", inclusive = false, message = "Salary must be greater than 0")
    private BigDecimal salary;

    @Schema(description = "Date of hiring (YYYY-MM-DD)", example = "2024-01-15")
    @NotNull(message = "Hire date is mandatory")
    @PastOrPresent(message = "Hire date cannot be in the future")
    private LocalDate hireDate;

    @Schema(description = "Employee employment status", example = "ACTIVE")
    private EmployeeStatus status;
}
