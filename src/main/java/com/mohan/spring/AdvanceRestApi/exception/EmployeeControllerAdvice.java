package com.mohan.spring.AdvanceRestApi.exception;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.mohan.spring.AdvanceRestApi.model.ApiErrorResponse;



@RestControllerAdvice
public class EmployeeControllerAdvice {
	
	@ExceptionHandler(EmployeeNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleNotFound(EmployeeNotFoundException ex)
	{
		ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
		apiErrorResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
		apiErrorResponse.setMessage(ex.getMessage());
		apiErrorResponse.setDateTime(LocalDateTime.now());
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				             .header("error info", "Employee not found")
				             .body(apiErrorResponse);
	}
}
	






























	
