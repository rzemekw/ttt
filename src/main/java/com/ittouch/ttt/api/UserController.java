package com.ittouch.ttt.api;

import com.ittouch.ttt.service.authentication.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
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
    public void login(@RequestBody String username) {
        authenticationService.login(username);
    }
}
