package com.employee.employee_management_portal.dto;

import com.employee.employee_management_portal.entity.EmployeeStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "Response of an employee data")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeResponseDTO {

    @Schema(description = "Unique employee identifier", example = "1")
    private Long id;

    @Schema(description = "Employee's first name", example = "John")
    private String firstName;

    @Schema(description = "Employee's last name", example = "Doe")
    private String lastName;

    @Schema(description = "Employee's full name", example = "John Doe")
    private String fullName;

    @Schema(description = "Employee's corporate email address", example = "john.doe@example.com")
    private String email;

    @Schema(description = "Department name", example = "Engineering")
    private String department;

    @Schema(description = "Job title / Designation", example = "Senior Software Engineer")
    private String jobTitle;

    @Schema(description = "Monthly / Annual salary", example = "85000.00")
    private BigDecimal salary;

    @Schema(description = "Date of hiring (YYYY-MM-DD)", example = "2024-01-15")
    private LocalDate hireDate;

    @Schema(description = "Employee employment status", example = "ACTIVE")
    private EmployeeStatus status;

    @Schema(description = "Timestamp when the employee record was created")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the employee record was last updated")
    private LocalDateTime updatedAt;
}
