package com.ittouch.ttt.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        this.configureHttpSecurity(http);

        http
                .sessionManagement(s -> s
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                )
                .authorizeHttpRequests(a -> a
                        .requestMatchers("/user/login").permitAll()
                        .anyRequest().authenticated()

                )
                .logout(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @SneakyThrows
    private void configureHttpSecurity(HttpSecurity http) {

        http.cors(Customizer.withDefaults());
        log.warn("Disabling CSRF protection");
        disableCsrf(http);

        http.exceptionHandling(e -> e
                .defaultAuthenticationEntryPointFor(new ApiAuthenticationEntryPoint(), new AntPathRequestMatcher("/api/**")));
    }

    @SneakyThrows
    private void enableCsrf(HttpSecurity http) {
        http.csrf(c -> c.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));
    }

    @SneakyThrows
    private void disableCsrf(HttpSecurity http) {
        http.csrf(AbstractHttpConfigurer::disable);
    }

    private static class ApiAuthenticationEntryPoint implements AuthenticationEntryPoint {
        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        }
    }

}
