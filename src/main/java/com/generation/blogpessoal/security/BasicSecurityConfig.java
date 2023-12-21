package com.generation.blogpessoal.security;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration//definicoes de beans || injecao de dependencia que pode ser acessada por qualquer classe
@EnableWebSecurity//habilita essa config como a padrao do projeto || substitui a padrao do spring
public class BasicSecurityConfig {

    @Autowired
    private JwtAuthFilter authFilter;

    @Bean // usar o bean ja torna ele public 
    UserDetailsService userDetailsService() {

        return new UserDetailsServiceImpl();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();//ela criptpgrafa a senha
    }

    @Bean
    AuthenticationProvider authenticationProvider() {//responsavel por fazer authenticascvao pelo banco
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());//criptografa a senha 
        return authenticationProvider;
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)//gerenyte de authenticacao || mostra a mais ativa
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();//que e da do banco
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {//o mais importante

    	http // como a aplicacao funciona
	        .sessionManagement(management -> management
	                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))//API e stateless || semelhante a um cookies || session
	        		.csrf(csrf -> csrf.disable())////uma geracao de token automatica do spring || tem um problema que bloqueia o put e o post || desabilita o token 
	        		.cors(withDefaults());//se nao habilitar ele so aceita do proprio servidor || ou seja a parte do front nao funciona
    				//crossorigin

    	http
	        .authorizeHttpRequests((auth) -> auth //
	                .requestMatchers("/usuarios/logar").permitAll()//permite logar sem o token
	                .requestMatchers("/usuarios/cadastrar").permitAll()//permite cadastrar sem o token
	                .requestMatchers("/error/**").permitAll()//seria pra liberar todas a mensagens de erro
	                .requestMatchers(HttpMethod.OPTIONS).permitAll()//libera o cabecalho
	                .anyRequest().authenticated())//fora as permisoes acima todas as outras precisam de validacao
	        .authenticationProvider(authenticationProvider())
	        .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)//aqui habilita o filter do JWT
	        .httpBasic(withDefaults());

		return http.build();

    }

}