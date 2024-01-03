package com.generation.blogpessoal.controller;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.service.UsuarioService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.	PER_CLASS)
public class UsuarioControllerTest {

	@Autowired
	private TestRestTemplate testRestTemplate;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	
	@BeforeAll
	void start() {
		usuarioRepository.deleteAll();
			
		usuarioService.cadastrarUsuario(new Usuario(0L,
				"Root", "Root@gmail.com", "rootroot", " "));
	}
	
	@Test
	@DisplayName("cadastrar usuario")
	public void devCriarUmUsuario() {
		
		
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario (0L,
				"cleito" , "cleito@gmail.com", "12345678", ""));
		
		ResponseEntity <Usuario> corpoResposta = testRestTemplate
				.exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);
		
		
		assertEquals(HttpStatus.CREATED, corpoResposta.getStatusCode());
	}
	
	@Test
	@DisplayName("logar usuario")
	public void devLogarUmUsuario() {
		
		
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario (0L,
				"cleito" , "cleito@gmail.com", "12345678", ""));
		
		ResponseEntity <String> corpoResposta = testRestTemplate
				.withBasicAuth("cleito@gmail.com", "12345678")
				.exchange("/usuarios/all", HttpMethod.GET,null, String.class);
		
		//esse assert e a condicao que voce deseja que retorne
		assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());
	}
	
	
	@Test
	@DisplayName("nao deve duplicar usuario")
	public void naoDevDuplicarUsuario() {
		
		usuarioService.cadastrarUsuario(new Usuario (0L,
				"cleitin" , "cleitin@gmail.com", "12345678", ""));
		
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario (0L,
				"cleitin" , "cleitin@gmail.com", "12345678", ""));
		
		ResponseEntity <Usuario> corpoResposta = testRestTemplate
				.exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);
		
		//esse assert e a condicao que voce deseja que retorne
		assertEquals(HttpStatus.BAD_REQUEST, corpoResposta.getStatusCode());
	}
	
	@Test
	@DisplayName("deve Atualiza usuario")
	public void devAtualizarUsuario() {
		
		Optional<Usuario> usuarioCadastrado =
		usuarioService.cadastrarUsuario(new Usuario (0L,
				"cleitao" , "cleitao@gmail.com", "12345678", ""));
		
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario (usuarioCadastrado.get().getId(),
				"cleitao da" , "cleitao.as@gmail.com", "87654321", ""));
		
		ResponseEntity <Usuario> corpoResposta = testRestTemplate
				.withBasicAuth("Root@gmail.com", "rootroot")
				.exchange("/usuarios/atualizar", HttpMethod.PUT, corpoRequisicao, Usuario.class);
		
		//esse assert e a condicao que voce deseja que retorne
		assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());
	}
	
	@Test
	@DisplayName("deve Listar Todos usuario 📋")
	public void devListarTodosUsuarios() {
		
		usuarioService.cadastrarUsuario(new Usuario (0L,
				"cleiclei" , "cleiclei@gmail.com", "12345678", ""));
		
		usuarioService.cadastrarUsuario(new Usuario (0L,
				"cleisto" , "cleisto@gmail.com", "12345678", ""));
		
		ResponseEntity <String> corpoResposta = testRestTemplate
				.withBasicAuth("Root@gmail.com", "rootroot")
				.exchange("/usuarios/all", HttpMethod.GET,null, String.class);
		
		//esse assert e a condicao que voce deseja que retorne
		assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());
	}
	
	@Test
	@DisplayName("deve Buscar usuario Por Id 📋")
	public void devBuscarUsuariosPorId() {
		
		Optional<Usuario> usuarioCadastrado =
				usuarioService.cadastrarUsuario(new Usuario (0L,
				"cleiclei" , "cleiclei@gmail.com", "12345678", ""));
		
		ResponseEntity <String> corpoResposta = testRestTemplate
				.withBasicAuth("Root@gmail.com", "rootroot")
				.exchange("/usuarios/", HttpMethod.GET,null, String.class,usuarioCadastrado.get().getId());
		
		//esse assert e a condicao que voce deseja que retorne
		assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());
	}
	
	@Test
	@DisplayName("deve Buscar usuario Por Usuario 📋")
	public void devBuscarUsuariosPorUsuario() {
		
		Optional<Usuario> usuarioCadastrado =
				usuarioService.cadastrarUsuario(new Usuario (0L,
				"cleiclei" , "cleiclei@gmail.com", "12345678", ""));
		
		ResponseEntity <String> corpoResposta = testRestTemplate
				.withBasicAuth("Root@gmail.com", "rootroot")
				.exchange("/usuarios/find/", HttpMethod.GET, null, String.class,usuarioCadastrado.get().getUsuario());
		
		//esse assert e a condicao que voce deseja que retorne
		assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());
	}
}
