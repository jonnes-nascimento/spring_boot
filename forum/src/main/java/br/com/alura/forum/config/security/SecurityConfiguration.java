package br.com.alura.forum.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import br.com.alura.forum.repository.UsuarioRepository;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private AutenticacaoService autenticacaoService;
	
	@Autowired
	private TokenService tokenService;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Override
	@Bean // essa anotacao faz o spring saber que esse metodo e o responsavel por devolver um authentication manager para outras classes que fizerem injecao dele (ex.: a classe AutenticacaoController.java)
	protected AuthenticationManager authenticationManager() throws Exception {
		
		return super.authenticationManager();
	}
	
	// Configuracoes de autenticacao (controle de acesso - login)
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		auth.userDetailsService(autenticacaoService).passwordEncoder(new BCryptPasswordEncoder());
	}
	
	// Configuracoes de autorizacao (quem pode acessar qual url, perfis de acesso)
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		// como a autenticacao nao e mais feita pelo formulario de login qeu o spring security gera automaticamente,
		// a aplicação cliente deve ter uma página de login, que faz a chamada ao endpoint de autenticação da API.
		
		http.authorizeRequests()
		.antMatchers(HttpMethod.GET,"/topicos").permitAll() // libera acesso somente ao metodo GET na url especificada - caso nao seja especificado o metodo http, todos seriam liberados
		.antMatchers(HttpMethod.GET,"/topicos/*").permitAll()
		.antMatchers(HttpMethod.POST,"/auth").permitAll() // libera o envio das informacoes de login
		.antMatchers(HttpMethod.GET,"/actuator/**").permitAll() // libera o acesso para o spring actuator - ATENCAO com o permitAll - em operacao, deve ser restrito!
		.anyRequest().authenticated() // qualquer outra requisicao, o cliente tem que estar autenticado
		//.and().formLogin(); // cria uma sessao para autenticar o usuario - nao e usado em ambientes com authenticacao stateless
		.and().csrf().disable() // csrf = tipo de ataque - desabilita essa verificacao pois a api ja e livre desse tipo de ataque
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // avisa ao spring security para nao criar session - a autenticacao sera feita por tolkens
		.and().addFilterBefore(new AutenticacaoViaTokenFilter(tokenService, usuarioRepository), UsernamePasswordAuthenticationFilter.class); // diz ao spring para executar o filtro que pega o token no cabecalho do request e so depois executa o filtro da autenticacao do usuario
	}
	
	// Configuracoes de recursos estaticos (requisicoes para arquivos css, js, imagens, etc)
	@Override
	public void configure(WebSecurity web) throws Exception {
	    web.ignoring()
	        .antMatchers("/**.html", "/v2/api-docs", "/webjars/**", "/configuration/**", "/swagger-resources/**");
	}
	
}
