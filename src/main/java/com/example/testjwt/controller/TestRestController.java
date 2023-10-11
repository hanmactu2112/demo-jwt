package com.example.testjwt.controller;

import com.example.testjwt.config.CustomUserDetails;
import com.example.testjwt.config.JwtTokenProvider;
import com.example.testjwt.model.LoginRequest;
import com.example.testjwt.model.User;
import com.example.testjwt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class TestRestController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;


    @PostMapping(value = "/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

        // Xác thực từ username và password.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        // Nếu không xảy ra exception tức là thông tin hợp lệ
        // Set thông tin authentication vào Security Context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Trả về jwt cho người dùng.
        String jwt = tokenProvider.generateToken((CustomUserDetails) authentication.getPrincipal());
        return new ResponseEntity<>(jwt, HttpStatus.OK);
    }

    // Api /api/random yêu cầu phải xác thực mới có thể request
    @GetMapping("/random")
    public ResponseEntity<?> randomStuff(){
        return new ResponseEntity("JWT Hợp lệ mới có thể thấy được message này",HttpStatus.OK);
    }
    @GetMapping("/test")
    public ResponseEntity<?> insertDb(){

        User user = new User();
        user.setUsername("loda");
        user.setPassword(passwordEncoder.encode("loda"));
        userRepository.save(user);
        System.out.println(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
