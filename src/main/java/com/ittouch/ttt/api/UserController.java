package com.ittouch.ttt.api;

import com.ittouch.ttt.service.authentication.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final AuthenticationService authenticationService;

    @GetMapping
    public String getCurrentUsername() {
        return authenticationService.getCurrentUserName();
    }

    @PostMapping("/login")
    public void login(@RequestBody String username, HttpServletRequest request, HttpServletResponse response) {
        authenticationService.login(username, request, response);
    }
}
