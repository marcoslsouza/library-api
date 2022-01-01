package com.github.marcoslsouza.libraryapi.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.github.marcoslsouza.libraryapi.dto.BookDTO;

@RestController
@RequestMapping("/api/books")
public class BookController {

	@PostMapping
	public ResponseEntity<BookDTO> create(UriComponentsBuilder uriBuilder) {
		
		BookDTO dto = BookDTO.builder().author("Artur").title("As aventuras").isbn("001").id(11L).build();
		//dto.setAuthor("Autor");
		//dto.setTitle("Meu Livro");
		//dto.setIsbn("1213212");
		//dto.setId(11L);
		
		// Retorna o endereco do recurso e um json de dto e status 201 (created)
		URI uri = uriBuilder.path("/api/books/{id}").buildAndExpand(dto.getId()).toUri();
		return ResponseEntity.created(uri).body(dto);
	}
}
