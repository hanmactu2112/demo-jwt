package com.example.testjwt;

import com.example.testjwt.model.User;
import com.example.testjwt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@SpringBootApplication
public class TestJwtApplication {

	public static void main(String[] args) {
		SpringApplication.run(TestJwtApplication.class, args);
	}
	@Bean
	public CorsFilter corsFilter() {
		CorsConfiguration corsConfig = new CorsConfiguration();
		corsConfig.addAllowedOrigin("*"); // Cho phép tất cả các nguồn (origin)
		corsConfig.addAllowedMethod("*"); // Cho phép tất cả các phương thức HTTP
		corsConfig.addAllowedHeader("*"); // Cho phép tất cả các header

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfig);

		return new CorsFilter(source);
	}
}
