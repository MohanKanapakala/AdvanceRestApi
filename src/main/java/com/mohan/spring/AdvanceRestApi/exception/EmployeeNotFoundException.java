package com.mohan.spring.AdvanceRestApi.exception;

public class EmployeeNotFoundException extends RuntimeException
{
  public EmployeeNotFoundException(String message)
  {
	  super(message);
  }
  
  
}
