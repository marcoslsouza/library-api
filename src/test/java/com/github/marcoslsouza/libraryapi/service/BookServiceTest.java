package com.github.marcoslsouza.libraryapi.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.marcoslsouza.libraryapi.entity.Book;
import com.github.marcoslsouza.libraryapi.exception.BusinessException;
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
		Book book = this.cretateValidBook();
		Mockito.when(this.repository.existsByIsbn(Mockito.anyString())).thenReturn(false);
		
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
	
	@Test
	@DisplayName("Deve lancar erro de negocio ao tentar salvar um livro com isbn duplicado")
	public void shouldNotSaveBookWithDuplicatedTestISBN() {
		
		// Cenario
		Book book = this.cretateValidBook();
		
		// Quando for chamado o "this.repository.existsByIsbn(Mockito.anyString())" passando qualquer string,
		// entao sera retornado true. Esse tratamento foi necessario, porque estou passando o valor default 
		// em "setUp()" e assim no service em "if(repository.existsByIsbn(book.getIsbn()))" o valor padrao e 
		// "false" nao caindo na exception.
		Mockito.when(this.repository.existsByIsbn(Mockito.anyString())).thenReturn(true);
		
		// Verificacoes
		Throwable exception = Assertions.catchThrowable(() -> this.service.save(book));
		assertThat(exception)
			.isInstanceOf(BusinessException.class)
			.hasMessage("Isbn já cadastrado.");
		
		// Nao pode salvar.
		// "repository" nunca vai executar o "save" com o parametro "book".
		Mockito.verify(repository, Mockito.never()).save(book);
	}

	private Book cretateValidBook() {
		return Book.builder().isbn("123").author("Fulano").title("As aventuras").build();
	}
}
