package com.example.testjwt.controller;

import com.example.testjwt.config.CustomUserDetails;
import com.example.testjwt.config.JwtTokenProvider;
import com.example.testjwt.model.LoginRequest;
import com.example.testjwt.model.User;
import com.example.testjwt.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
@AutoConfigureMockMvc
//@RunWith(SpringRunner.class)
@Transactional
public class TestController {
    private static final String USERNAME = "loda";
    private static final String PASSWORD = "loda";

    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;
    @Autowired
    private MockMvc mockMvc;
    @Test
    public void testLogin() throws Exception {



        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(USERNAME);
        loginRequest.setPassword(PASSWORD);
        ObjectMapper mapper = new ObjectMapper();
        String loginJson = mapper.writeValueAsString(loginRequest);
        mockMvc.perform(post("/api/login").contentType(MediaType.APPLICATION_JSON).content(loginJson)).andExpect(status().isOk());
    }

    @Test
    public void testRandomStuff() throws Exception {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        USERNAME,
                        PASSWORD
                )
        );
        // Nếu không xảy ra exception tức là thông tin hợp lệ
        // Set thông tin authentication vào Security Context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Trả về jwt cho người dùng.
        String jwt = tokenProvider.generateToken((CustomUserDetails) authentication.getPrincipal());
        mockMvc.perform(get("/api/random").header("Authorization","Bearer "+jwt))
                .andExpect(status().isOk())
                .andExpect(content().string("JWT Hợp lệ mới có thể thấy được message này"));
    }

    @Test
    public void testRollBack() {
        // Tạo một bản ghi mới
        User entity = new User();
        entity.setUsername("test1");
        entity.setPassword(passwordEncoder.encode("test1"));
        userRepository.save(entity);

        // Kiểm tra xem bản ghi đã được lưu vào cơ sở dữ liệu chính xác hay không
        User userOptional = userRepository.findByUsername("test1");
        assertNotNull(userOptional);
        assertEquals("test1", userOptional.getUsername());
    }
    @Test

    public void testUserRollback() throws Exception {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        USERNAME,
                        PASSWORD
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Trả về jwt cho người dùng.
        String jwt = tokenProvider.generateToken((CustomUserDetails) authentication.getPrincipal());
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("test2");
        loginRequest.setPassword("test2");
        ObjectMapper mapper = new ObjectMapper();
        String loginJson = mapper.writeValueAsString(loginRequest);
        mockMvc.perform(post("/api/test").contentType(MediaType.APPLICATION_JSON).content(loginJson).header("Authorization","Bearer "+jwt))
                .andExpect(status().isOk());
        User userOptional = userRepository.findByUsername("test2");
        assertNotNull(userOptional);
        assertEquals("test2", userOptional.getUsername());
    }


}
