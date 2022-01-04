package com.github.marcoslsouza.libraryapi.service;

import java.util.Optional;

import com.github.marcoslsouza.libraryapi.entity.Book;

public interface BookService {

	Book save(Book book);

	Optional<Book> getById(Long id);

	void delete(Book book);
	
}
