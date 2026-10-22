package com.employee.employee_management_portal.service.impl;

import com.employee.employee_management_portal.converter.EmployeeConverter;
import com.employee.employee_management_portal.dto.EmployeeRequestDTO;
import com.employee.employee_management_portal.dto.EmployeeResponseDTO;
import com.employee.employee_management_portal.dto.PagedResponse;
import com.employee.employee_management_portal.entity.Employee;
import com.employee.employee_management_portal.entity.EmployeeStatus;
import com.employee.employee_management_portal.exception.BadRequestException;
import com.employee.employee_management_portal.exception.DuplicateResourceException;
import com.employee.employee_management_portal.exception.ResourceNotFoundException;
import com.employee.employee_management_portal.repository.EmployeeRepository;
import com.employee.employee_management_portal.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeConverter employeeConverter;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, EmployeeConverter employeeConverter) {
        this.employeeRepository = employeeRepository;
        this.employeeConverter = employeeConverter;
    }

    @Override
    public EmployeeResponseDTO createEmployee(EmployeeRequestDTO requestDTO) {
        log.info("Creating new employee with email: {}", requestDTO.getEmail());

        if (employeeRepository.existsByEmail(requestDTO.getEmail())) {
            throw new DuplicateResourceException("Employee with email '" + requestDTO.getEmail() + "' already exists");
        }

        Employee employee = employeeConverter.toEntity(requestDTO);
        Employee savedEmployee = employeeRepository.save(employee);
        log.info("Employee created successfully with id: {}", savedEmployee.getId());

        return employeeConverter.toResponseDTO(savedEmployee);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<EmployeeResponseDTO> getAllEmployees(
            int page,
            int size,
            String sortBy,
            String sortDir,
            String keyword,
            String department,
            EmployeeStatus status) {

        if (page < 0) {
            throw new BadRequestException("Page index cannot be negative");
        }
        if (size <= 0) {
            throw new BadRequestException("Page size must be greater than zero");
        }

        String sortProperty = (sortBy != null && !sortBy.trim().isEmpty()) ? sortBy.trim() : "id";
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortProperty);

        Pageable pageable = PageRequest.of(page, size, sort);

        String cleanKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        String departmentName = (department != null && !department.trim().isEmpty()) ? department.trim() : null;

        log.debug("Searching employees - keyword: {}, department: {}, status: {}, page: {}, size: {}",
                cleanKeyword, departmentName, status, page, size);

        Page<Employee> employeePage = employeeRepository.searchEmployees(
                cleanKeyword, departmentName, status, pageable);

        List<EmployeeResponseDTO> content = employeePage.getContent()
                .stream()
                .map(employeeConverter::toResponseDTO)
                .collect(Collectors.toList());

        PagedResponse<EmployeeResponseDTO> response = new PagedResponse<>();
        response.setContent(content);
        response.setPageNumber(employeePage.getNumber());
        response.setPageSize(employeePage.getSize());
        response.setTotalElements(employeePage.getTotalElements());
        response.setTotalPages(employeePage.getTotalPages());
        response.setFirst(employeePage.isFirst());
        response.setLast(employeePage.isLast());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponseDTO> getAllEmployeesList() {
        log.debug("Fetching unpaged list of all employees");
        return employeeRepository.findAll()
                .stream()
                .map(employeeConverter::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponseDTO getEmployeeById(Long id) {
        log.info("Fetching employee with id: {}", id);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        return employeeConverter.toResponseDTO(employee);
    }

    @Override
    public EmployeeResponseDTO updateEmployee(Long id, EmployeeRequestDTO requestDTO) {
        log.info("Updating employee with id: {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));

        if (employeeRepository.existsByEmailAndIdNot(requestDTO.getEmail(), id)) {
            throw new DuplicateResourceException("Employee with email '" + requestDTO.getEmail() + "' already exists");
        }

        employeeConverter.updateEntityFromDTO(employee, requestDTO);

        Employee updatedEmployee = employeeRepository.save(employee);
        log.info("Employee with id: {} updated successfully", updatedEmployee.getId());

        return employeeConverter.toResponseDTO(updatedEmployee);
    }

    @Override
    public void deleteEmployee(Long id) {
        log.info("Deleting employee with id: {}", id);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));

        employeeRepository.delete(employee);
        log.info("Employee with id: {} deleted successfully", id);
    }

    @Override
    public EmployeeResponseDTO updateEmployeeStatus(Long id, EmployeeStatus status) {
        log.info("Updating employee status to {} for id: {}", status, id);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));

        employee.setStatus(status);
        Employee updatedEmployee = employeeRepository.save(employee);
        return employeeConverter.toResponseDTO(updatedEmployee);
    }
}
