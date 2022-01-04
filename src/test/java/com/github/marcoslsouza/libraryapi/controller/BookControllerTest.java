package com.github.marcoslsouza.libraryapi.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.Optional;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.marcoslsouza.libraryapi.dto.BookDTO;
import com.github.marcoslsouza.libraryapi.entity.Book;
import com.github.marcoslsouza.libraryapi.exception.BusinessException;
import com.github.marcoslsouza.libraryapi.service.BookService;

// Cria um mini contexto de dependencias para rodar o teste
@ExtendWith(SpringExtension.class)
// Rodar somente no ambiente de teste
@ActiveProfiles("test")
@WebMvcTest
// Configurar um objeto para fazer as requisicoes
@AutoConfigureMockMvc
public class BookControllerTest {
	
	// Definir a rota
	static String BOOK_API = "/api/books";

	// Simula como se fosse uma requisicao para a API
	@Autowired
	MockMvc mvc;
	
	// Cria uma instancia mocada
	@MockBean
	BookService service;
	
	@Test
	@DisplayName("Deve criar um livro com sucesso")
	public void createBookTest() throws Exception {
		
		// Passa os valores para o controller
		BookDTO dto = this.createNewBook();
		
		// Independente do livro que for salvo, sera retornado "saveBook"
		Book saveBook = Book.builder().id(10L).author("Artur").title("As aventuras").isbn("001").build();
		BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(saveBook);
		
		// Recebe um objeto e transforma em um json
		String json = new ObjectMapper().writeValueAsString(dto);
		
		// Definir uma requisicao
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
			.post(BOOK_API)
			.contentType(MediaType.APPLICATION_ATOM_XML.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_ATOM_XML.APPLICATION_JSON)
			.content(json);
		
		// Recebe os valores do controller
		// Obs: Fazemos uma requisicao e recebemos uma resposta com o status 201 (created).
		// E assim recebemos uma resposta em JSON, onde fazemos uma verificacao se o JSON de 
		// resposta retorna as informacoes citadas Ex. andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
		mvc.perform(request)
			.andExpect(MockMvcResultMatchers.status().isCreated())
			.andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
			.andExpect(jsonPath("title").value(dto.getTitle()))
			.andExpect(jsonPath("author").value(dto.getAuthor()))
			.andExpect(jsonPath("isbn").value(dto.getIsbn()));
			
	}
	
	// Validacao de integridade
	@Test
	@DisplayName("Deve lancar erro de validacao quando nao houver dados suficientes para criacao do livro")
	public void createInvalidBookTest() throws Exception {
		
		// Cria um json com "BookDTO()" null
		String json = new ObjectMapper().writeValueAsString(new BookDTO());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.post(BOOK_API)
				.contentType(MediaType.APPLICATION_ATOM_XML.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_ATOM_XML.APPLICATION_JSON)
				.content(json);
		
		// No Json teremos a propriedade "errors" com 3 mensagens de erro "Matchers.hasSize(3)" para title author e isbn
		mvc.perform(request)
			.andExpect(MockMvcResultMatchers.status().isBadRequest())
			.andExpect(jsonPath("errors", Matchers.hasSize(3)));
	}
	
	// Validacao de regra de negocio
	@Test
	@DisplayName("Deve lancar erro ao tentar cadastrar um livro com isbn já utilizado por outro.")
	public void createBookWithDuplicatedIsbn() throws Exception {
		
		BookDTO dto = this.createNewBook();
		
		// Cria um json com "BookDTO()" null
		String json = new ObjectMapper().writeValueAsString(dto);
		
		// Simular que o servico enviou a mensagem de erro
		String mensagemErro = "Isbn já cadastrado.";
		BDDMockito.given(this.service.save(Mockito.any(Book.class)))
				.willThrow(new BusinessException(mensagemErro));
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.post(BOOK_API)
				.contentType(MediaType.APPLICATION_ATOM_XML.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_ATOM_XML.APPLICATION_JSON)
				.content(json);
		
		mvc.perform(request)
			.andExpect(MockMvcResultMatchers.status().isBadRequest())
			// Tenha apenas uma mensagem de erro
			.andExpect(jsonPath("errors", Matchers.hasSize(1)))
			// Na posicao "0" do array errors tera o valor da mensagem "Isbn já cadastrado."
			.andExpect(jsonPath("errors[0]").value(mensagemErro));
	}
	
	@Test
	@DisplayName("Deve obter informacoes de um livro")
	public void getBookDetailsTest() throws Exception {
		
		// cenario (given)
		Long id = 1L;
		
		Book book = Book.builder()
				.id(id)
				.title(this.createNewBook().getTitle())
				.author(this.createNewBook().getAuthor())
				.isbn(this.createNewBook().getIsbn())
				.build();
		
		BDDMockito.given(service.getById(id)).willReturn(Optional.of(book));
		
		// execucao (when)
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
			.get(BOOK_API.concat("/"+id))
			.accept(MediaType.APPLICATION_JSON);
		
		mvc
			.perform(request)
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
			.andExpect(jsonPath("title").value(this.createNewBook().getTitle()))
			.andExpect(jsonPath("author").value(this.createNewBook().getAuthor()))
			.andExpect(jsonPath("isbn").value(this.createNewBook().getIsbn()));
	}
	
	@Test
	@DisplayName("Deve retornar resource not found quando o livro procurado nao existir.")
	public void bookNotFoundTest() throws Exception {
		
		// Tanto faz o id que for passado, ele sempre retorna nao encontrado (empty)
		BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.get(BOOK_API.concat("/"+1))
				.accept(MediaType.APPLICATION_JSON);
		
		mvc
		.perform(request)
		.andExpect(MockMvcResultMatchers.status().isNotFound());
	}
	
	@Test
	@DisplayName("Deve deletar um livro")
	public void deleteBookTest() throws Exception {
		
		BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.of(Book.builder().id(11L).build()));
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.delete(BOOK_API.concat("/"+1))
				.accept(MediaType.APPLICATION_JSON);
		
		mvc
		.perform(request)
		.andExpect(MockMvcResultMatchers.status().isNoContent());
	}
	
	@Test
	@DisplayName("Deve retornar not found quando nao encontrar o livro para deletar")
	public void deleteInexistentBookTest() throws Exception {
		
		BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.delete(BOOK_API.concat("/"+1))
				.accept(MediaType.APPLICATION_JSON);
		
		mvc
		.perform(request)
		.andExpect(MockMvcResultMatchers.status().isNotFound());
	}
	
	private BookDTO createNewBook() {
		return BookDTO.builder().author("Artur").title("As aventuras").isbn("001").build();
	}
}
