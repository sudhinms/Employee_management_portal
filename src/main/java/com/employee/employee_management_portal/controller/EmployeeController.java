package com.employee.employee_management_portal.controller;

import com.employee.employee_management_portal.dto.ApiResponse;
import com.employee.employee_management_portal.dto.EmployeeRequestDTO;
import com.employee.employee_management_portal.dto.EmployeeResponseDTO;
import com.employee.employee_management_portal.dto.ErrorResponse;
import com.employee.employee_management_portal.dto.PagedResponse;
import com.employee.employee_management_portal.dto.ValidationErrorResponse;
import com.employee.employee_management_portal.entity.EmployeeStatus;
import com.employee.employee_management_portal.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@Tag(name = "Employee Management", description = "Operations to manage employee records, search and filter")
@Slf4j
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    @Operation(summary = "Create an employee", description = "Adds a new employee record")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Employee successfully created",
                    content = @Content(schema = @Schema(implementation = EmployeeResponseDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid input or validation error",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Employee with same email already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<EmployeeResponseDTO> createEmployee(
            @Valid @RequestBody EmployeeRequestDTO requestDTO) {
        log.info("request to create employee with email: {}", requestDTO.getEmail());
        EmployeeResponseDTO created = employeeService.createEmployee(requestDTO);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    @Operation(
            summary = "View all employees",
            description = "Retrieves all employees with optional pagination, sorting, search keyword, department and status filters"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Employees retrieved successfully")
    })
    public ResponseEntity<?> getAllEmployees(
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0", required = false) int page,

            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10", required = false) int size,

            @Parameter(description = "Field to sort by", example = "id")
            @RequestParam(defaultValue = "id", required = false) String sortBy,

            @Parameter(description = "Sort direction: asc or desc", example = "asc")
            @RequestParam(defaultValue = "asc", required = false) String sortDir,

            @Parameter(description = "Search keyword (matches name, email, department, job title)")
            @RequestParam(required = false) String keyword,

            @Parameter(description = "Filter by department")
            @RequestParam(required = false) String department,

            @Parameter(description = "Filter by status: ACTIVE, INACTIVE, ON_LEAVE, TERMINATED")
            @RequestParam(required = false) EmployeeStatus status,

            @Parameter(description = "Whether to return paginated response (default: true)")
            @RequestParam(defaultValue = "true", required = false) boolean paged
    ) {
        log.info("request to fetch employees - is paged: {}, page: {}, size: {}", paged, page, size);
        if (!paged) {
            List<EmployeeResponseDTO> allList = employeeService.getAllEmployeesList();
            return ResponseEntity.ok(allList);
        }

        PagedResponse<EmployeeResponseDTO> pagedResponse = employeeService.getAllEmployees(
                page, size, sortBy, sortDir, keyword, department, status);
        return ResponseEntity.ok(pagedResponse);
    }

    @GetMapping("/{id}")
    @Operation(summary = "View an employee by ID", description = "Retrieves a single employee record by ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Employee found",
                    content = @Content(schema = @Schema(implementation = EmployeeResponseDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Employee not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<EmployeeResponseDTO> getEmployeeById(
            @Parameter(description = "Employee unique ID", example = "1")
            @PathVariable Long id) {
        log.info("request to get employee by ID: {}", id);
        EmployeeResponseDTO employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update employee details", description = "Updates an existing employee record completely")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Employee updated successfully",
                    content = @Content(schema = @Schema(implementation = EmployeeResponseDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid input or validation error",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Employee not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Email conflict with another existing employee",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<EmployeeResponseDTO> updateEmployee(
            @Parameter(description = "Employee unique ID", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequestDTO requestDTO) {
        log.info("request to update employee with ID: {}", id);
        EmployeeResponseDTO updated = employeeService.updateEmployee(id, requestDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an employee", description = "Removes an employee record by ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Employee deleted successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Employee not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ApiResponse> deleteEmployee(
            @Parameter(description = "Employee unique ID", example = "1")
            @PathVariable Long id) {
        log.info("request to delete employee with ID: {}", id);
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok(new ApiResponse(true, "Employee with id " + id + " has been successfully deleted"));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update employee status", description = "Updates only the employment status of an employee")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Employee status updated successfully",
                    content = @Content(schema = @Schema(implementation = EmployeeResponseDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Employee not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<EmployeeResponseDTO> updateEmployeeStatus(
            @Parameter(description = "Employee ID", example = "1")
            @PathVariable Long id,
            @Parameter(description = "New status", example = "INACTIVE")
            @RequestParam EmployeeStatus status) {
        log.info("request to update employee status: {} for ID: {}", status, id);
        EmployeeResponseDTO updated = employeeService.updateEmployeeStatus(id, status);
        return ResponseEntity.ok(updated);
    }
}
