package com.github.marcoslsouza.libraryapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
// Para criar um build para as instancias. Ex. Requerido na classe de teste
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {

	private Long id;
	private String title;
	private String author;
	private String isbn;
}
