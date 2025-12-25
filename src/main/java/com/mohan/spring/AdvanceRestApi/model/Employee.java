package com.mohan.spring.AdvanceRestApi.model;



import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;

import jakarta.persistence.Id;
import jakarta.validation.constraints.DecimalMin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Employee {
	
	@Id
	private String id;
	
	
	 // Name cannot be blank, must have 2–50 characters
    @NotBlank(message = "Name is mandatory")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;
    
    
    // Salary cannot be null, must be > 0 and up to 2 decimal places
    @NotNull(message = "Salary is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Salary must be greater than 0")
    private Double salary;
    
    // Department cannot be blank, must be 2–30 characters
    @NotBlank(message = "Department is mandatory")
    @Size(min = 2, max = 30, message = "Department must be between 2 and 30 characters")
    private String dept;
    
 // Gender must be Male, Female, or Other
    @NotBlank(message = "Gender is mandatory")
    @Pattern(regexp = "Male|Female|Other|M|F", message = "Gender must be Male, Female, or Other")
    private String gender;
    
    private String email;
	

}
