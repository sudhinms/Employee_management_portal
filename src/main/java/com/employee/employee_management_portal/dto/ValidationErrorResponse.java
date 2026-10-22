package com.employee.employee_management_portal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Map;

@Schema(description = "Validation error response representation")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ValidationErrorResponse extends ErrorResponse {

    @Schema(description = "Map of invalid field names to error messages")
    private Map<String, String> fieldErrors;

    public ValidationErrorResponse(int status, String error, String message, String path, Map<String, String> fieldErrors) {
        super(status, error, message, path);
        this.fieldErrors = fieldErrors;
    }
}
