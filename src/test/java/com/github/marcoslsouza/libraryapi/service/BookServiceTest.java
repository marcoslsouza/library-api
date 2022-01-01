package com.github.marcoslsouza.libraryapi.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.marcoslsouza.libraryapi.entity.Book;
import com.github.marcoslsouza.libraryapi.model.BookRepository;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

	BookService service;
	
	@MockBean
	BookRepository repository;
	
	@BeforeEach
	public void setUp() {
		this.service = new BookServiceImpl(this.repository);
	}
	
	@Test
	@DisplayName("Deve salvar um livro")
	public void saveBookTest() {
		
		// Cenario
		Book book = Book.builder().isbn("123").author("Fulano").title("As aventuras").build();
		
		// "Mockito.when" e nao "BDDMockito.given" porque nao estamos definindo nenhum designer.
		Book build = Book.builder().id(11L).isbn("123").author("Fulano").title("As aventuras").build();
		Mockito.when(this.repository.save(book)).thenReturn(build);
		
		// Execucao
		Book savedBook = this.service.save(book);
		
		// Verificacao
		assertThat(savedBook.getId()).isNotNull();
		assertThat(savedBook.getIsbn()).isEqualTo("123");
		assertThat(savedBook.getTitle()).isEqualTo("As aventuras");
		assertThat(savedBook.getAuthor()).isEqualTo("Fulano");
	}
}
