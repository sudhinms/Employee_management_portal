package com.employee.employee_management_portal.service;

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
import com.employee.employee_management_portal.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmployeeService Unit Tests")
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
                .hireDate(LocalDate.of(2023, 1, 15))
                .status(EmployeeStatus.ACTIVE)
                .build();

        requestDTO = EmployeeRequestDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .department("Engineering")
                .jobTitle("Developer")
                .salary(new BigDecimal("75000.00"))
                .hireDate(LocalDate.of(2023, 1, 15))
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
                .hireDate(LocalDate.of(2023, 1, 15))
                .status(EmployeeStatus.ACTIVE)
                .build();
    }

    @Nested
    @DisplayName("createEmployee()")
    class CreateEmployeeTests {

        @Test
        @DisplayName("Should create employee successfully when email is unique")
        void shouldCreateEmployeeSuccessfully() {
            when(employeeRepository.existsByEmail("john.doe@example.com")).thenReturn(false);
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
        @DisplayName("Should throw DuplicateResourceException when email already exists")
        void shouldThrowExceptionWhenEmailExists() {
            when(employeeRepository.existsByEmail("john.doe@example.com")).thenReturn(true);

            assertThatThrownBy(() -> employeeService.createEmployee(requestDTO))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessageContaining("already exists");

            verify(employeeRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("getEmployeeById()")
    class GetEmployeeByIdTests {

        @Test
        @DisplayName("Should return employee response when found by id")
        void shouldReturnEmployeeWhenFound() {
            when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
            when(employeeConverter.toResponseDTO(employee)).thenReturn(responseDTO);

            EmployeeResponseDTO result = employeeService.getEmployeeById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getFirstName()).isEqualTo("John");
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when employee id does not exist")
        void shouldThrowExceptionWhenNotFound() {
            when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> employeeService.getEmployeeById(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Employee not found with id: '99'");
        }
    }

    @Nested
    @DisplayName("getAllEmployees() & getAllEmployeesList()")
    class GetAllEmployeesTests {

        @Test
        @DisplayName("Should return paged response with correct metadata")
        void shouldReturnPagedEmployees() {
            Page<Employee> page = new PageImpl<>(List.of(employee));
            when(employeeRepository.searchEmployees(eq(null), eq(null), eq(null), any(Pageable.class)))
                    .thenReturn(page);
            when(employeeConverter.toResponseDTO(employee)).thenReturn(responseDTO);

            PagedResponse<EmployeeResponseDTO> result = employeeService.getAllEmployees(
                    0, 10, "id", "asc", null, null, null);

            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getPageNumber()).isZero();
            assertThat(result.isFirst()).isTrue();
        }

        @Test
        @DisplayName("Should throw BadRequestException when page index is negative")
        void shouldThrowExceptionWhenPageIndexNegative() {
            assertThatThrownBy(() -> employeeService.getAllEmployees(-1, 10, "id", "asc", null, null, null))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("Page index cannot be negative");
        }

        @Test
        @DisplayName("Should throw BadRequestException when page size is zero or less")
        void shouldThrowExceptionWhenPageSizeZeroOrLess() {
            assertThatThrownBy(() -> employeeService.getAllEmployees(0, 0, "id", "asc", null, null, null))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("Page size must be greater than zero");
        }

        @Test
        @DisplayName("Should return list of all employees unpaged")
        void shouldReturnAllEmployeesList() {
            when(employeeRepository.findAll()).thenReturn(List.of(employee));
            when(employeeConverter.toResponseDTO(employee)).thenReturn(responseDTO);

            List<EmployeeResponseDTO> result = employeeService.getAllEmployeesList();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getEmail()).isEqualTo("john.doe@example.com");
        }
    }

    @Nested
    @DisplayName("updateEmployee()")
    class UpdateEmployeeTests {

        @Test
        @DisplayName("Should update employee successfully")
        void shouldUpdateEmployeeSuccessfully() {
            when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
            when(employeeRepository.existsByEmailAndIdNot("john.doe@example.com", 1L)).thenReturn(false);
            doNothing().when(employeeConverter).updateEntityFromDTO(employee, requestDTO);
            when(employeeRepository.save(employee)).thenReturn(employee);
            when(employeeConverter.toResponseDTO(employee)).thenReturn(responseDTO);

            EmployeeResponseDTO result = employeeService.updateEmployee(1L, requestDTO);

            assertThat(result).isNotNull();
            verify(employeeConverter).updateEntityFromDTO(employee, requestDTO);
            verify(employeeRepository).save(employee);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when updating non-existent employee")
        void shouldThrowExceptionWhenEmployeeToUpdateNotFound() {
            when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> employeeService.updateEmployee(99L, requestDTO))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(employeeRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw DuplicateResourceException when updating email to another existing employee's email")
        void shouldThrowExceptionWhenDuplicateEmailOnUpdate() {
            when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
            when(employeeRepository.existsByEmailAndIdNot("john.doe@example.com", 1L)).thenReturn(true);

            assertThatThrownBy(() -> employeeService.updateEmployee(1L, requestDTO))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessageContaining("already exists");

            verify(employeeRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("deleteEmployee()")
    class DeleteEmployeeTests {

        @Test
        @DisplayName("Should delete employee when found by id")
        void shouldDeleteEmployeeSuccessfully() {
            when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
            doNothing().when(employeeRepository).delete(employee);

            employeeService.deleteEmployee(1L);

            verify(employeeRepository).delete(employee);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when deleting non-existent employee")
        void shouldThrowExceptionWhenDeletingNonExistentEmployee() {
            when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> employeeService.deleteEmployee(99L))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(employeeRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("updateEmployeeStatus()")
    class UpdateEmployeeStatusTests {

        @Test
        @DisplayName("Should update status to INACTIVE successfully")
        void shouldUpdateStatusSuccessfully() {
            when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
            when(employeeRepository.save(employee)).thenReturn(employee);

            responseDTO.setStatus(EmployeeStatus.INACTIVE);
            when(employeeConverter.toResponseDTO(employee)).thenReturn(responseDTO);

            EmployeeResponseDTO result = employeeService.updateEmployeeStatus(1L, EmployeeStatus.INACTIVE);

            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo(EmployeeStatus.INACTIVE);
            assertThat(employee.getStatus()).isEqualTo(EmployeeStatus.INACTIVE);
            verify(employeeRepository).save(employee);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when updating status for non-existent employee")
        void shouldThrowExceptionWhenNotFound() {
            when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> employeeService.updateEmployeeStatus(99L, EmployeeStatus.INACTIVE))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(employeeRepository, never()).save(any());
        }
    }
}
