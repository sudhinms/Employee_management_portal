package com.employee.employee_management_portal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Schema(description = "Standard error response representation")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {

    @Schema(description = "HTTP status code", example = "404")
    private int status;

    @Schema(description = "HTTP error phrase", example = "Not Found")
    private String error;

    @Schema(description = "Detailed error message", example = "Employee not found with id: '12'")
    private String message;

    @Schema(description = "Request URI path", example = "/api/v1/employees/12")
    private String path;

    @Schema(description = "Error occurrence timestamp")
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    public ErrorResponse(int status, String error, String message, String path) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }
}
