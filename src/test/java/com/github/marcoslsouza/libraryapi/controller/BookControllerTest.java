package com.github.marcoslsouza.libraryapi.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	
	@Test
	@DisplayName("Deve criar um livro com sucesso")
	public void createBookTest() throws Exception {
		
		// Recebe um objeto e transforma em um json
		String json = new ObjectMapper().writeValueAsString(null);
		
		// Definir uma requisicao
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
			.post(BOOK_API)
			.contentType(MediaType.APPLICATION_ATOM_XML.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_ATOM_XML.APPLICATION_JSON)
			.content(json);
		
		// Obs: Fazemos uma requisicao e recebemos uma resposta com o status 201 (created).
		// E assim recebemos uma resposta em JSON, onde fazemos uma verificacao se o JSON de 
		// resposta retorna as informacoes citadas Ex. andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
		mvc.perform(request)
			.andExpect(MockMvcResultMatchers.status().isCreated())
			.andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
			.andExpect(jsonPath("title").value("Meu Livro"))
			.andExpect(jsonPath("author").value("Autor"))
			.andExpect(jsonPath("isbn").value("1213212"));
			
	}
	
	@Test
	@DisplayName("Deve lancar erro de validacao quando nao houver dados suficientes para criacao do livro")
	public void createInvalidBookTest() {
		
	}
}
