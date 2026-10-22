package com.employee.employee_management_portal.repository;

import com.employee.employee_management_portal.entity.Employee;
import com.employee.employee_management_portal.entity.EmployeeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    Optional<Employee> findByEmail(String email);

    @Query("SELECT e FROM Employee e WHERE " +
           "(:keyword IS NULL OR " +
           " LOWER(e.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(e.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(e.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(e.department) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(e.jobTitle) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:department IS NULL OR LOWER(e.department) = LOWER(:department)) " +
           "AND (:status IS NULL OR e.status = :status)")
    Page<Employee> searchEmployees(
            @Param("keyword") String keyword,
            @Param("department") String department,
            @Param("status") EmployeeStatus status,
            Pageable pageable);
}
