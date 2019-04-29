package com.company.enroller.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@PropertySource("classpath:application.properties")
public class WebSecurity extends WebSecurityConfigurerAdapter {
	
	@Autowired
	ParticipantProvider participantProvider;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Value("${security.secret}")
	private String secret;
	
	@Value("${security.issuer}")
	private String issuer;
	
	@Value("${security.token_expiration_in_seconds}")
	private int tokenExpiration;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable()
			.authorizeRequests()
			.anyRequest().permitAll()
			.and()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		
		http.addFilterBefore(new JWTAuthenticationFilter(authenticationManager(), secret, issuer, tokenExpiration), UsernamePasswordAuthenticationFilter.class);
	}
	
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
	   auth.userDetailsService(participantProvider).passwordEncoder(passwordEncoder);
	}
}