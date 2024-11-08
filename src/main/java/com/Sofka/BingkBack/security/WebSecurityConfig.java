package com.Sofka.BingkBack.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final KeyCloakJwtAuthenticationConverter keyCloakJwtAuthenticationConverter;

    public WebSecurityConfig(KeyCloakJwtAuthenticationConverter keyCloakJwtAuthenticationConverter) {
        this.keyCloakJwtAuthenticationConverter = keyCloakJwtAuthenticationConverter;
    }

    @Bean
    public SecurityFilterChain setupOAuth(HttpSecurity http) throws Exception {
        http.cors().and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .csrf().disable()
                .oauth2ResourceServer().jwt().jwtAuthenticationConverter(keyCloakJwtAuthenticationConverter).and().and()
                .authorizeHttpRequests(authz -> authz
//                        .requestMatchers(new AntPathRequestMatcher("/admin/**")).hasAnyAuthority( "ROLE_admin")
                                .anyRequest().permitAll()
                );
        return http.build();
    }

}

