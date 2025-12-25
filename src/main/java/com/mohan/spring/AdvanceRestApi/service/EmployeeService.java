package com.mohan.spring.AdvanceRestApi.service;

import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mohan.spring.AdvanceRestApi.exception.EmployeeNotFoundException;
import com.mohan.spring.AdvanceRestApi.model.Employee;
import com.mohan.spring.AdvanceRestApi.repository.EmployeeRepository;
import com.mohan.spring.AdvanceRestApi.exception.IllegalDeptException;
import com.mohan.spring.AdvanceRestApi.service.EmployeeService;
import com.mohan.spring.AdvanceRestApi.model.NameSalaryDTO;


@Service
public class EmployeeService {

	private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    @Autowired
    EmployeeRepository employeeRepository;

   

    // Reusable validation + normalization
    private Employee validateAndNormalize(Employee employee) {
        logger.debug("Validating and normalizing employee: {}", employee);

        // Name normalization
        String name = employee.getName().trim();
        name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
        employee.setName(name);

     // Department normalization (NULL SAFE)
        String dept = employee.getDept();

        if (dept == null) {
            throw new IllegalDeptException("Department is required");
        }

        // Remove ALL whitespace (including unicode)
        dept = dept.replaceAll("\\s+", "").toUpperCase();

        switch (dept) {
            case "HR":
                employee.setDept("HR");
                break;

            case "DEVELOPER":
            case "DEV":
                employee.setDept("Developer");
                break;

            case "TESTER":
            case "TEST":
                employee.setDept("Tester");
                break;

            default:
                logger.error("Invalid department after normalization: {}", dept);
                throw new IllegalDeptException(
                    "Department is not valid. Allowed: HR, Developer, Tester"
                );
        }


        // Salary rules
        Double salary = employee.getSalary();
        if (salary > 30000) {
            salary = salary + (salary * 0.10);
            logger.info("Salary > 30000, added 10% bonus: {}", salary);
        }
        salary = salary - (salary * 0.05); // PF
        salary = salary - (salary * 0.30); // Tax
        salary = Math.round(salary * 100.0) / 100.0;
        employee.setSalary(salary);

        // Gender normalization
        String gender = employee.getGender().trim();
        if (gender.equalsIgnoreCase("Male") || gender.equalsIgnoreCase("M")) {
            employee.setGender("M");
        } else if (gender.equalsIgnoreCase("Female") || gender.equalsIgnoreCase("F")) {
            employee.setGender("F");
        } else if (gender.equalsIgnoreCase("Other")) {
            employee.setGender("Other");
        } else {
            logger.error("Invalid gender: {}", gender);
            throw new RuntimeException("Gender is not valid. Allowed: Male/Female/Other or M/F");
        }

        logger.debug("Employee normalized: {}", employee);
        return employee;
    }

    
    public int deleteByDeptAndGender1(String dept, String gender) {
        return employeeRepository.deleteByDeptAndGender(dept, gender);
    }
    
    public Employee saveEmployeeData(Employee employee) {
        logger.info("Saving employee data: {}", employee);
        validateAndNormalize(employee);

        String customId = generateCustomId(employee.getDept());
        employee.setId(customId);

        Employee savedEmployee = employeeRepository.save(employee);
        logger.info("Employee saved successfully with ID: {}", savedEmployee.getId());
        return savedEmployee;
    }

    // Custom ID generator based on department + year + count
    private String generateCustomId(String dept) {
        String prefix;

        switch (dept.toLowerCase()) {
            case "hr":
                prefix = "HR";
                break;
            case "developer":
            case "dev":
                prefix = "DEV";
                break;
            case "tester":
            case "test":
                prefix = "TEST";
                break;
            default:
                prefix = "GEN";
        }

        long count = employeeRepository.count() + 1; // simple sequence
        String year = String.valueOf(Year.now().getValue());
        return String.format("%s%s-%03d", prefix, year, count); // e.g. HR2025-001
    }
    
    

    // Get All Employees
    public List<Employee> getAllEmployees() {
        logger.info("Fetching all employees...");
        return employeeRepository.findAll();
    }

    // Get Employee by ID
    public Employee getEmployeeById(String id) {
        logger.info("Fetching employee by ID: {}", id);
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("The employee with " + id + " not available"));
    }

    // Delete Employee By ID
    public void deleteEmpById(String id) {
        logger.info("Deleting employee by ID: {}", id);
        if (employeeRepository.existsById(id)) {
            employeeRepository.deleteById(id);
            logger.info("Employee deleted with id {}", id);
        } else {
            throw new EmployeeNotFoundException("Employee not found with id " + id);
        }
    }

    // Put method (update)
    public Employee updateEmployeeById(String id, Employee updateEmployeeDetails) {

        String trimmedId = id.trim(); // ✅ new variable

        logger.info("Updating employee with id [{}]", trimmedId);

        Employee existEmp = employeeRepository.findById(trimmedId)
                .orElseThrow(() ->
                    new EmployeeNotFoundException(
                        "Employee not found with id " + trimmedId
                    )
                );

        existEmp.setName(updateEmployeeDetails.getName());
        existEmp.setSalary(updateEmployeeDetails.getSalary());
        existEmp.setDept(updateEmployeeDetails.getDept());
        existEmp.setGender(updateEmployeeDetails.getGender());

        validateAndNormalize(existEmp);
        return employeeRepository.save(existEmp);
    }


    // Patch method
    public Employee partiallyUpdateEmployeeById(String id, Map<String, Object> updateEmployeeDetails) {
        logger.info("Partially updating employee by ID: {} | Updates: {}", id, updateEmployeeDetails);
        Optional<Employee> optionalEmployee = employeeRepository.findById(id);
        Employee existingEmployee = optionalEmployee.orElseThrow(() -> {
            logger.error("Employee not found with ID: {}", id);
            return new EmployeeNotFoundException("Employee not found with id : " + id);
        });

        updateEmployeeDetails.forEach((k, v) -> {
            switch (k) {
                case "name":
                    existingEmployee.setName((String) v);
                    break;
                case "salary":
                	existingEmployee.setSalary(((Number)v).doubleValue());
                    break;
                case "dept":
                    existingEmployee.setDept((String) v);
                    break;
                case "gender":
                    existingEmployee.setGender((String) v);
                    break;
                default:
                    logger.error("Field {} is not patchable", k);
                    throw new IllegalArgumentException("Field " + k + " is not patchable");
            }
        });

        validateAndNormalize(existingEmployee);

        Employee savedEmp = employeeRepository.save(existingEmployee);
        logger.info("Employee partially updated successfully with ID: {}", savedEmp.getId());
        return savedEmp;
    }

    // Save bulk data
    public List<Employee> saveEmpDetails(List<Employee> empList) {
        logger.info("Saving bulk employee data. Count: {}", empList.size());
        empList.forEach(this::validateAndNormalize);
        empList.forEach(e -> e.setId(generateCustomId(e.getDept())));
        List<Employee> savedEmpList = employeeRepository.saveAll(empList);
        logger.info("Bulk save completed. Saved {} employees", savedEmpList.size());
        return savedEmpList;
    }

    // Count
    public Long noOfAllEmps() {
        return employeeRepository.count();
    }
	
	public Employee getEmployeeByEmail(String email) {
        return employeeRepository.findByEmail(email)
                .orElseThrow(() -> new EmployeeNotFoundException(email));
    }

    public List<Employee> getEmployeesBySalaryBetween(double minSalary, double maxSalary) {
        return employeeRepository.findBySalaryBetween(minSalary, maxSalary);
    }
    
 // Delete all
    public void deleteAllEmps() {
        employeeRepository.deleteAll();
    }
    
    public List<Employee> findByDeptAndGender(String dept, String gender) {
        return employeeRepository.findByDeptAndGender(dept, gender);
    }

    public List<Employee> findByDeptOrGender(String dept, String gender) {
        return employeeRepository.findByDeptOrGender(dept, gender);
    }

    public List<Employee> findByGender(String gender) {
        return employeeRepository.findByGender(gender);
    }

    public List<Employee> findBySalaryGreaterThan(Double salary) {
        return employeeRepository.findBySalaryGreaterThan(salary);
    }

    public List<Employee> findBySalaryLessThan(Double salary) {
        return employeeRepository.findBySalaryLessThan(salary);
    }
    
    public List<NameSalaryDTO> getNameSalary() {
        return employeeRepository.findNameAndSalary();
    }

    public List<NameSalaryDTO> getNameSalaryByDept(String dept) {
        return employeeRepository.findNameAndSalaryByDept(dept);
    }

    public void updateWithNewName(String id, String oldName, String newName) {

        String trimmedId = id.trim();
        String oldNameTrimmed = oldName.trim();
        String newNameTrimmed = newName.trim();

        Employee emp = employeeRepository.findById(trimmedId)
                .orElseThrow(() ->
                    new EmployeeNotFoundException("Employee not found with id " + trimmedId)
                );

        if (!emp.getName().equalsIgnoreCase(oldNameTrimmed)) {
            throw new IllegalArgumentException("Old name does not match");
        }

        emp.setName(newNameTrimmed);
        employeeRepository.save(emp);
    }


    public void deleteByDeptAndGender(String dept, String gender) {
        employeeRepository.deleteByDeptAndGender(dept, gender);
    }

    public int increaseSalaryByDept(String dept, double percent) {
        return employeeRepository.increaseSalaryByDept(dept, percent);
    }
}
