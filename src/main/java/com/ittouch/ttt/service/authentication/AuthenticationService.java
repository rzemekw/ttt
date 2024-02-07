package com.ittouch.ttt.service.authentication;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService implements AuthenticationManager {
    private final SecurityContextRepository securityContextRepository;

    @SneakyThrows
    public void login(String username, HttpServletRequest request, HttpServletResponse response) {
        var authentication = new AuthenticatedAuthentication(username);

        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);
        securityContextRepository.saveContext(SecurityContextHolder.getContext(), request, response);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("Authenticating user: {}", authentication.getName());
        return null;
    }

    public String getCurrentUserName() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        return (String) authentication.getPrincipal();
    }
}