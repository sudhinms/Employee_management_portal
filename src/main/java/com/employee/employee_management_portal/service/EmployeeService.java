package com.employee.employee_management_portal.service;

import com.employee.employee_management_portal.dto.EmployeeRequestDTO;
import com.employee.employee_management_portal.dto.EmployeeResponseDTO;
import com.employee.employee_management_portal.dto.PagedResponse;
import com.employee.employee_management_portal.entity.EmployeeStatus;

import java.util.List;

public interface EmployeeService {

    EmployeeResponseDTO createEmployee(EmployeeRequestDTO requestDTO);

    PagedResponse<EmployeeResponseDTO> getAllEmployees(
            int page,
            int size,
            String sortBy,
            String sortDir,
            String keyword,
            String department,
            EmployeeStatus status);

    List<EmployeeResponseDTO> getAllEmployeesList();

    EmployeeResponseDTO getEmployeeById(Long id);

    EmployeeResponseDTO updateEmployee(Long id, EmployeeRequestDTO requestDTO);

    void deleteEmployee(Long id);

    EmployeeResponseDTO updateEmployeeStatus(Long id, EmployeeStatus status);
}
