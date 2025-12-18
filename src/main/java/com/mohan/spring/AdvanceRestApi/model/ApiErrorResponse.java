package com.mohan.spring.AdvanceRestApi.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiErrorResponse {

	private String message;
	private LocalDateTime dateTime;
	private int statusCode;
	
}

