package com.github.marcoslsouza.libraryapi.controller;

import java.net.URI;
import java.util.Optional;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import com.github.marcoslsouza.libraryapi.api.exceptions.APIErrors;
import com.github.marcoslsouza.libraryapi.dto.BookDTO;
import com.github.marcoslsouza.libraryapi.entity.Book;
import com.github.marcoslsouza.libraryapi.exception.BusinessException;
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
	@Transactional
	public ResponseEntity<BookDTO> create(@RequestBody @Valid BookDTO dto, UriComponentsBuilder uriBuilder) {
		
		// Passando de dto para book
		Book book = this.mapper.map(dto, Book.class);
		Book bookSave = this.service.save(book);
		
		// Passando de "bookSave" para "BookDTO"
		dto = this.mapper.map(bookSave, BookDTO.class);
		
		// Retorna o endereco do recurso e um json de dto e status 201 (created)
		URI uri = uriBuilder.path("/api/books/{id}").buildAndExpand(dto.getId()).toUri();
		return ResponseEntity.created(uri).body(dto);
	}
	
	@GetMapping("/{id}")
	public BookDTO getBooks(@PathVariable Long id) {
		
		Optional<Book> book = this.service.getById(id);
		
		return book.map(b -> this.mapper.map(b, BookDTO.class))
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}
	
	@DeleteMapping("{id}")
	@Transactional
	public ResponseEntity<?> delete(@PathVariable Long id) {
		
		return this.service.getById(id)
				.map(book -> {
					this.service.delete(book);
					return ResponseEntity.noContent().build();
				}).orElse( ResponseEntity.notFound().build() );
	}
	
	// Erro de validacao
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<APIErrors> handleValidationExceptions(MethodArgumentNotValidException ex) {
		
		// Verifica objetos validados com "@Valid". "bindingResult" tem todas as mensagens de erro.
		BindingResult bindingResult = ex.getBindingResult();
		
		return ResponseEntity.badRequest().body(new APIErrors(bindingResult));
	}
	
	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<APIErrors> handleBusinessExceptions(BusinessException ex) {
		return ResponseEntity.badRequest().body(new APIErrors(ex));
	}
}
