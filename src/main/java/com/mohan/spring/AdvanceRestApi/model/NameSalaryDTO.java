package com.mohan.spring.AdvanceRestApi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NameSalaryDTO {
	private String name;
	private double salary;

}

