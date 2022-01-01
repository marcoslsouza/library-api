package com.github.marcoslsouza.libraryapi.model;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.marcoslsouza.libraryapi.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long> {

}
