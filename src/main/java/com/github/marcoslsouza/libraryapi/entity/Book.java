package com.github.marcoslsouza.libraryapi.entity;

import javax.persistence.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Book {
	
	private Long id;
	private String title;
	private String author;
	private String isbn;
}
