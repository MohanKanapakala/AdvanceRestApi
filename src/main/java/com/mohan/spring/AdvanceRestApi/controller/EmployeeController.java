package com.mohan.spring.AdvanceRestApi.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mohan.spring.AdvanceRestApi.service.EmployeeService;
import com.mohan.spring.AdvanceRestApi.model.NameSalaryDTO;
import com.mohan.spring.AdvanceRestApi.controller.EmployeeController;

import jakarta.validation.Valid;

import com.mohan.spring.AdvanceRestApi.model.Employee;


@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

	private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);
	

	@Autowired
	EmployeeService employeeService;
	
	 // Save Employee with HATEOAS links
	
    @PostMapping("/save")
    public ResponseEntity<EntityModel<Employee>> saveEmployee(@RequestBody @Valid Employee employee) {
        
        Employee savedEmployee = employeeService.saveEmployeeData(employee);

        EntityModel<Employee> entityModel = EntityModel.of(savedEmployee);

        entityModel.add(
            WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(EmployeeController.class)
                    .getEmployee(savedEmployee.getId())
            ).withSelfRel()
        );
        entityModel.add(
            WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(EmployeeController.class)
                    .deleteEmployee(savedEmployee.getId())
            ).withRel("Delete")
        );
        entityModel.add(
            WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(EmployeeController.class)
                    .updateEmployee(savedEmployee.getId(), savedEmployee)
            ).withRel("Update")
        );
        entityModel.add(
            WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(EmployeeController.class)
                    .patchEmployee(savedEmployee.getId(), new HashMap<>())
            ).withRel("Patch")
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                             .header("info", "Data saved successfully")
                             .body(entityModel);
    }
    
    // Get all employees
    @GetMapping("/getall")
    public ResponseEntity<List<Employee>> getEmployees() {
        logger.info("Request to fetch all employees");
        logger.info("It is Test commit for Jenkins automation!");
        List<Employee> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    // Get employee by ID
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployee(@PathVariable("id") String id) {
        logger.info("Request to fetch employee with ID: {}", id);
        Employee employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }
    
 // Delete employee by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable("id") String id) {
        logger.info("Request to delete employee with ID: {}", id);
        employeeService.deleteEmpById(id);
        return ResponseEntity.noContent().build();
    }

    // Update employee by ID
    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable("id") String id,
                                                   @RequestBody Employee updateEmployeeDetails) {
        logger.info("Request to update employee with ID: {}, Data: {}", id, updateEmployeeDetails);
        Employee employee = employeeService.updateEmployeeById(id, updateEmployeeDetails);
        return ResponseEntity.ok(employee);
    }

    // Patch employee by ID
    @PatchMapping("/{id}")
    public ResponseEntity<Employee> patchEmployee(@PathVariable("id") String id,
                                                  @RequestBody Map<String, Object> patchEmployeeDetails) {
        logger.info("Request to patch employee with ID: {}, Fields: {}", id, patchEmployeeDetails);
        Employee employee = employeeService.partiallyUpdateEmployeeById(id, patchEmployeeDetails);
        return ResponseEntity.ok(employee);
    }

    // Bulk save employees
    @PostMapping("/bulk")
    public ResponseEntity<List<Employee>> allEmployees(@RequestBody @Valid List<@Valid Employee> listEmployees) {
        logger.info("Request to save bulk employees, Count: {}", listEmployees.size());
        List<Employee> employees = employeeService.saveEmpDetails(listEmployees);
        return ResponseEntity.status(HttpStatus.CREATED)
                             .header("info", "Bulk data saved successfully")
                             .body(employees);
    }
    
 // Get employee count
    @GetMapping("/count")
    public ResponseEntity<Long> noOfEmps() {
        logger.info("Request to get employee count");
        Long noOfEmps = employeeService.noOfAllEmps();
        return ResponseEntity.ok(noOfEmps);
    }
    
    // Delete all employees
    @DeleteMapping("/deleteALl")
    public ResponseEntity<Void> deleteAll() {
        logger.info("Request to delete all employees");
        employeeService.deleteAllEmps();
        return ResponseEntity.noContent().build();
    }
    
    
    
    
    //CUSTOM METHODS
	
	 // Get employee by email
    @GetMapping("/search1")
    public ResponseEntity<Employee> getEmployeeByEmail(@RequestParam("email") String email) {
       Employee employee = employeeService.getEmployeeByEmail(email);
        return ResponseEntity.ok(employee);
    }

    // Get employees by salary range
    @GetMapping("/search2")
    public ResponseEntity<List<com.mohan.spring.AdvanceRestApi.model.Employee>> getEmployeesBySalaryBetween(@RequestParam double minSalary, @RequestParam double maxSalary) {
        List<Employee> employees = employeeService.getEmployeesBySalaryBetween(minSalary, maxSalary);
        return ResponseEntity.ok(employees);
    }
    
    // Find by dept AND gender
    @GetMapping("/findByDeptAndGender")
    public ResponseEntity<List<Employee>> getByDeptAndGender(@RequestParam String dept,
                                                             @RequestParam String gender) {
        List<Employee> result = employeeService.findByDeptAndGender(dept, gender);
        return ResponseEntity.ok(result);
    }

    // Find by dept OR gender
    @GetMapping("/findByDeptOrGender")
    public ResponseEntity<List<Employee>> getByDeptOrGender(@RequestParam String dept,
                                                            @RequestParam String gender) {
        List<Employee> result = employeeService.findByDeptOrGender(dept, gender);
        return ResponseEntity.ok(result);
    }

    // Find by gender
    @GetMapping("/findByGender")
    public ResponseEntity<List<Employee>> getByGender(@RequestParam String gender) {
        return ResponseEntity.ok(employeeService.findByGender(gender));
    }

    // Find by salary greater than
    @GetMapping("/findBySalaryGreaterThan")
    public ResponseEntity<List<Employee>> getBySalaryGreaterThan(@RequestParam Double salary) {
        return ResponseEntity.ok(employeeService.findBySalaryGreaterThan(salary));
    }

    // Find by salary less than
    @GetMapping("/findBySalaryLessThan")
    public ResponseEntity<List<Employee>> getBySalaryLessThan(@RequestParam Double salary) {
        return ResponseEntity.ok(employeeService.findBySalaryLessThan(salary));
    }

 // Name & Salary DTO
    @GetMapping("/nameandsalary")
    public ResponseEntity<List<NameSalaryDTO>> getNameSalary() {
        List<NameSalaryDTO> employees = employeeService.getNameSalary();
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/nameandsalarybydept")
    public ResponseEntity<List<NameSalaryDTO>> getNameSalaryByDept(@RequestParam("dept") String dept) {
        List<NameSalaryDTO> employees = employeeService.getNameSalaryByDept(dept);
        return ResponseEntity.ok(employees);
    }

    // Update name operation
    @PatchMapping("/{id}/update-name")
    public ResponseEntity<String> updateWithNewName(@PathVariable("id") String id,
                                                    @RequestParam("oldName") String oldName,
                                                    @RequestParam("newName") String newName) {
        int res = employeeService.updateWithNewName(id, oldName, newName);
        return ResponseEntity.ok(res == 1 ? "Data updated successfully" : "Not updated");
    }

    @DeleteMapping("delbydeptgender")
    public ResponseEntity<String> deleteByDeptAndGender(
            @RequestParam("dept") String dept,
            @RequestParam("gender") String gender) {

        int deletedCount = employeeService.deleteByDeptAndGender1(dept, gender);

        if (deletedCount == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body("No employees found to delete.");
        }

        return ResponseEntity.ok(deletedCount + " employee(s) deleted successfully.");
    }


    // Increase salary by dept
    @PatchMapping("/increase-salary")
    public ResponseEntity<String> increaseSalaryByDept(@RequestParam("dept") String dept,
                                                       @RequestParam("percent") double percent) {
        int updatedCount = employeeService.increaseSalaryByDept(dept, percent);
        return ResponseEntity.ok(updatedCount > 0
            ? "Salaries updated successfully for " + updatedCount + " employees"
            : "No employees found in department " + dept);
    }
	
    
}
