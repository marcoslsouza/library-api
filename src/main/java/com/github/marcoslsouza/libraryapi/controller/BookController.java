package com.github.marcoslsouza.libraryapi.controller;

import java.net.URI;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.github.marcoslsouza.libraryapi.dto.BookDTO;
import com.github.marcoslsouza.libraryapi.entity.Book;
import com.github.marcoslsouza.libraryapi.service.BookService;

@RestController
@RequestMapping("/api/books")
public class BookController {
	
	private BookService service;
	
	// Precisa criar um bean na "LibraryApplication", porque nao e gerenciado pelo spring
	private ModelMapper mapper;

	@Autowired
	public BookController(BookService service, ModelMapper mapper) {
		this.service = service;
		this.mapper = mapper;
	}

	@PostMapping
	public ResponseEntity<BookDTO> create(@RequestBody BookDTO dto, UriComponentsBuilder uriBuilder) {
		
		// Passando de dto para book
		Book book = this.mapper.map(dto, Book.class);
		Book bookSave = this.service.save(book);
		
		// Passando de "bookSave" para "BookDTO"
		dto = this.mapper.map(bookSave, BookDTO.class);
		
		// Retorna o endereco do recurso e um json de dto e status 201 (created)
		URI uri = uriBuilder.path("/api/books/{id}").buildAndExpand(dto.getId()).toUri();
		return ResponseEntity.created(uri).body(dto);
	}
}
