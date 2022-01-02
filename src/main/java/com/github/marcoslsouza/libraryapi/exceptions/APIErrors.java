package com.github.marcoslsouza.libraryapi.exceptions;

import java.util.ArrayList;
import java.util.List;

import org.springframework.validation.BindingResult;


public class APIErrors {

	private List<String> errors;

	public APIErrors(BindingResult bindResult) {
		
		this.errors = new ArrayList<>();
		bindResult.getAllErrors().forEach(error -> this.errors.add(error.getDefaultMessage()));
	}
	
	public List<String> getErrors() {
		return errors;
	}
}
