package com.arunx.boxapi.exception;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class RestExceptionHandler { 

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ResourceNotFoundDetails> handleResourceNotFoundException(ResourceNotFoundException e) {
		return new ResponseEntity<>(
			ResourceNotFoundDetails.builder()
				.title("Resource not found")
				.status(HttpStatus.NOT_FOUND.value())
				.detail(e.getMessage())
				.timestamp(LocalDateTime.now())
				.developerMessage(e.getClass().getName())
				.build(),
				HttpStatus.NOT_FOUND
		);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ValidationExceptionDetails> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		
		log.info("Fields error {}", e.getBindingResult().getFieldErrors());
		
		List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
		
		String fields = fieldErrors.stream().map(FieldError::getField).collect(Collectors.joining(", "));
		String fieldsMessage = fieldErrors.stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(", "));
		
		return new ResponseEntity<>(
			ValidationExceptionDetails.builder()
				.title("Fields Validation Error")
				.status(HttpStatus.BAD_REQUEST.value())
				.detail("Check the field(s) below")
				.timestamp(LocalDateTime.now())
				.developerMessage(e.getClass().getName())
				.fields(fields)
				.fieldsMessage(fieldsMessage)
				.build(),
				HttpStatus.BAD_REQUEST
		);
	}

}
