package br.com.alura.forum.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.alura.forum.config.security.TokenService;
import br.com.alura.forum.controller.dto.TokenDto;
import br.com.alura.forum.controller.form.LoginForm;

@RestController
@RequestMapping("/auth")
public class AutenticacaoController {
	
	@Autowired
	private AuthenticationManager authManager; // esta classe dispara o processo de autenticacao do spring
	
	@Autowired
	private TokenService tokenService;
	
	@PostMapping
	public ResponseEntity<TokenDto> autenticar(@RequestBody @Valid LoginForm form) {
		
		UsernamePasswordAuthenticationToken dadosLogin = form.converter();
		
		try {
			
			Authentication authentication = authManager.authenticate(dadosLogin); // nesse momento, o spring chama a classe AutenticacaoService.java e faz a parte de autenticacao no banco de dados
			
			String token = tokenService.gerarToken(authentication); 
			
			return ResponseEntity.ok(new TokenDto(token, "Bearer")); // o cliente, ao receber esse token, pode armazena-lo num cookie, num session storage ou na memoria, por exemplo
		} catch (AuthenticationException e) {
			
			return ResponseEntity.badRequest().build();
		}
	}
	
}
