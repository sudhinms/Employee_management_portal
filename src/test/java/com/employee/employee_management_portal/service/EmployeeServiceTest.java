package com.employee.employee_management_portal.service;

import com.employee.employee_management_portal.converter.EmployeeConverter;
import com.employee.employee_management_portal.dto.EmployeeRequestDTO;
import com.employee.employee_management_portal.dto.EmployeeResponseDTO;
import com.employee.employee_management_portal.dto.PagedResponse;
import com.employee.employee_management_portal.entity.Employee;
import com.employee.employee_management_portal.entity.EmployeeStatus;
import com.employee.employee_management_portal.exception.DuplicateResourceException;
import com.employee.employee_management_portal.exception.ResourceNotFoundException;
import com.employee.employee_management_portal.repository.EmployeeRepository;
import com.employee.employee_management_portal.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeConverter employeeConverter;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee;
    private EmployeeRequestDTO requestDTO;
    private EmployeeResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        employee = Employee.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .department("Engineering")
                .jobTitle("Developer")
                .salary(new BigDecimal("75000.00"))
                .hireDate(LocalDate.of(2024, 1, 15))
                .status(EmployeeStatus.ACTIVE)
                .build();

        requestDTO = EmployeeRequestDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .department("Engineering")
                .jobTitle("Developer")
                .salary(new BigDecimal("75000.00"))
                .hireDate(LocalDate.of(2024, 1, 15))
                .status(EmployeeStatus.ACTIVE)
                .build();

        responseDTO = EmployeeResponseDTO.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .fullName("John Doe")
                .email("john.doe@example.com")
                .department("Engineering")
                .jobTitle("Developer")
                .salary(new BigDecimal("75000.00"))
                .hireDate(LocalDate.of(2024, 1, 15))
                .status(EmployeeStatus.ACTIVE)
                .build();
    }

    @Test
    void testCreateEmployee() {
        when(employeeRepository.existsByEmail(requestDTO.getEmail())).thenReturn(false);
        when(employeeConverter.toEntity(requestDTO)).thenReturn(employee);
        when(employeeRepository.save(employee)).thenReturn(employee);
        when(employeeConverter.toResponseDTO(employee)).thenReturn(responseDTO);

        EmployeeResponseDTO result = employeeService.createEmployee(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("john.doe@example.com");
        verify(employeeRepository).save(employee);
    }

    @Test
    void testCreateEmployeeThrowsWhenEmailExists() {
        when(employeeRepository.existsByEmail(requestDTO.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> employeeService.createEmployee(requestDTO))
                .isInstanceOf(DuplicateResourceException.class);

        verify(employeeRepository, never()).save(any());
    }

    @Test
    void testGetEmployeeById() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeConverter.toResponseDTO(employee)).thenReturn(responseDTO);

        EmployeeResponseDTO result = employeeService.getEmployeeById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getFullName()).isEqualTo("John Doe");
    }

    @Test
    void testGetEmployeeByIdThrowsWhenNotFound() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.getEmployeeById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void testGetAllEmployees() {
        when(employeeRepository.searchEmployees(eq(null), eq(null), eq(null), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(employee)));
        when(employeeConverter.toResponseDTO(employee)).thenReturn(responseDTO);

        PagedResponse<EmployeeResponseDTO> result = employeeService.getAllEmployees(
                0, 10, "id", "asc", null, null, null);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void testGetAllEmployeesList() {
        when(employeeRepository.findAll()).thenReturn(List.of(employee));
        when(employeeConverter.toResponseDTO(employee)).thenReturn(responseDTO);

        List<EmployeeResponseDTO> result = employeeService.getAllEmployeesList();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    void testUpdateEmployee() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.existsByEmailAndIdNot(requestDTO.getEmail(), 1L)).thenReturn(false);
        doNothing().when(employeeConverter).updateEntityFromDTO(employee, requestDTO);
        when(employeeRepository.save(employee)).thenReturn(employee);
        when(employeeConverter.toResponseDTO(employee)).thenReturn(responseDTO);

        EmployeeResponseDTO result = employeeService.updateEmployee(1L, requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(employeeRepository).save(employee);
    }

    @Test
    void testDeleteEmployee() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        doNothing().when(employeeRepository).delete(employee);

        employeeService.deleteEmployee(1L);

        verify(employeeRepository).delete(employee);
    }

    @Test
    void testUpdateEmployeeStatus() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(employee)).thenReturn(employee);

        responseDTO.setStatus(EmployeeStatus.INACTIVE);
        when(employeeConverter.toResponseDTO(employee)).thenReturn(responseDTO);

        EmployeeResponseDTO result = employeeService.updateEmployeeStatus(1L, EmployeeStatus.INACTIVE);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(EmployeeStatus.INACTIVE);
        verify(employeeRepository).save(employee);
    }
}
