package com.mohan.spring.AdvanceRestApi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mohan.spring.AdvanceRestApi.model.Employee;
import com.mohan.spring.AdvanceRestApi.model.NameSalaryDTO;

import jakarta.transaction.Transactional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {

	 // Search by email
    Optional<Employee> findByEmail(String email);

    // Search by salary range
    List<Employee> findBySalaryBetween(double minSalary, double maxSalary);
    
    // 1. Find by department AND gender
    List<Employee> findByDeptAndGender(String dept, String gender);

    // 2. Find by department OR gender
    List<Employee> findByDeptOrGender(String dept, String gender);

    // 3. Find by gender
    List<Employee> findByGender(String gender);

    // 4. Find employees with salary greater than given amount
    List<Employee> findBySalaryGreaterThan(Double salary);

    // 5. Find employees with salary less than given amount
    List<Employee> findBySalaryLessThan(Double salary);

 // DTO queries
    @Query("select new com.mohan.spring.AdvanceRestApi.model.NameSalaryDTO(e.name, e.salary) from Employee e")
    List<NameSalaryDTO> findNameAndSalary();

    @Query("select new com.mohan.spring.AdvanceRestApi.model.NameSalaryDTO(e.name, e.salary) from Employee e where e.dept = :dept")
    List<NameSalaryDTO> findNameAndSalaryByDept(@Param("dept") String dept);

    // Update employee name by ID and old name (id type updated to String)
    @Modifying
    @Transactional
    @Query("UPDATE Employee e SET e.name = :newName WHERE e.id = :id AND e.name = :oldName")
    int updateEmployeeNameByIdAndOldName(@Param("id") String id,
                                         @Param("oldName") String oldName,
                                         @Param("newName") String newName);

    // Delete employees by dept and gender
    @Modifying
    @Transactional
    @Query("DELETE FROM Employee e WHERE e.dept = :dept AND e.gender = :gender")
    int deleteByDeptAndGender(@Param("dept") String dept,
                               @Param("gender") String gender);

    // Increase salary by dept
    @Modifying
    @Transactional
    @Query("UPDATE Employee e SET e.salary = e.salary + (e.salary * :percent / 100) WHERE e.dept = :dept")
    int increaseSalaryByDept(@Param("dept") String dept, @Param("percent") double percent);
}
