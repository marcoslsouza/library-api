package com.github.marcoslsouza.libraryapi.api.exceptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.validation.BindingResult;

import com.github.marcoslsouza.libraryapi.exception.BusinessException;


public class APIErrors {

	private List<String> errors;

	public APIErrors(BindingResult bindResult) {
		
		this.errors = new ArrayList<>();
		bindResult.getAllErrors().forEach(error -> this.errors.add(error.getDefaultMessage()));
	}
	
	public APIErrors(BusinessException ex) {
		
		// ArrayList contendo a mensagem que vem do BusinessException. Essa mensagem esta sendo enviada 
		// da BookController
		this.errors = Arrays.asList(ex.getMessage());
		
	}
	
	public List<String> getErrors() {
		return errors;
	}
}
