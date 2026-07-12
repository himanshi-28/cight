package com.cight.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            @Value("${cight.security.enabled:false}") boolean securityEnabled
    ) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        if (!securityEnabled) {
            http.authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll());
            return http.build();
        }

        http.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/webhook/github",
                                "/actuator/health",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/**")
                        .hasAuthority("SCOPE_builds:read")
                        .requestMatchers(HttpMethod.POST, "/api/builds/*/analyses")
                        .hasAuthority("SCOPE_analyses:write")
                        .requestMatchers(HttpMethod.POST, "/api/analyses/*/retry")
                        .hasAuthority("SCOPE_analyses:write")
                        .requestMatchers(HttpMethod.POST, "/api/builds")
                        .hasAuthority("SCOPE_builds:write")
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }
}
