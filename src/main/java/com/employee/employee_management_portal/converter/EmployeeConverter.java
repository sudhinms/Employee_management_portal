package com.employee.employee_management_portal.converter;

import com.employee.employee_management_portal.dto.EmployeeRequestDTO;
import com.employee.employee_management_portal.dto.EmployeeResponseDTO;
import com.employee.employee_management_portal.entity.Employee;
import com.employee.employee_management_portal.entity.EmployeeStatus;
import org.springframework.stereotype.Component;

@Component
public class EmployeeConverter {

    public Employee toEntity(EmployeeRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Employee employee = new Employee();
        employee.setFirstName(dto.getFirstName() != null ? dto.getFirstName().trim() : null);
        employee.setLastName(dto.getLastName() != null ? dto.getLastName().trim() : null);
        employee.setEmail(dto.getEmail() != null ? dto.getEmail().trim().toLowerCase() : null);
        employee.setDepartment(dto.getDepartment() != null ? dto.getDepartment().trim() : null);
        employee.setJobTitle(dto.getJobTitle() != null ? dto.getJobTitle().trim() : null);
        employee.setSalary(dto.getSalary());
        employee.setHireDate(dto.getHireDate());
        employee.setStatus(dto.getStatus() != null ? dto.getStatus() : EmployeeStatus.ACTIVE);
        return employee;
    }

    public EmployeeResponseDTO toResponseDTO(Employee employee) {
        if (employee == null) {
            return null;
        }

        String fullName = ((employee.getFirstName() != null ? employee.getFirstName() : "") + " "
                + (employee.getLastName() != null ? employee.getLastName() : "")).trim();

        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setId(employee.getId());
        dto.setFirstName(employee.getFirstName());
        dto.setLastName(employee.getLastName());
        dto.setFullName(fullName);
        dto.setEmail(employee.getEmail());
        dto.setDepartment(employee.getDepartment());
        dto.setJobTitle(employee.getJobTitle());
        dto.setSalary(employee.getSalary());
        dto.setHireDate(employee.getHireDate());
        dto.setStatus(employee.getStatus());
        dto.setCreatedAt(employee.getCreatedAt());
        dto.setUpdatedAt(employee.getUpdatedAt());
        return dto;
    }

    public void updateEntityFromDTO(Employee employee, EmployeeRequestDTO dto) {
        if (employee == null || dto == null) {
            return;
        }

        if (dto.getFirstName() != null) {
            employee.setFirstName(dto.getFirstName().trim());
        }
        if (dto.getLastName() != null) {
            employee.setLastName(dto.getLastName().trim());
        }
        if (dto.getEmail() != null) {
            employee.setEmail(dto.getEmail().trim().toLowerCase());
        }
        if (dto.getDepartment() != null) {
            employee.setDepartment(dto.getDepartment().trim());
        }
        if (dto.getJobTitle() != null) {
            employee.setJobTitle(dto.getJobTitle().trim());
        }
        if (dto.getSalary() != null) {
            employee.setSalary(dto.getSalary());
        }
        if (dto.getHireDate() != null) {
            employee.setHireDate(dto.getHireDate());
        }
        if (dto.getStatus() != null) {
            employee.setStatus(dto.getStatus());
        }
    }
}
