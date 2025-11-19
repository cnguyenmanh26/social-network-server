package com.social.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. QUAN TRỌNG: Bật tính năng CORS và trỏ nó vào cấu hình bên dưới
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // <--- BẠN ĐANG THIẾU DÒNG NÀY
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // 1. Cho phép Auth API đi qua tự do
                        .requestMatchers("/api/auth/**").permitAll()
                        // 2. Tất cả các request khác phải có Token hợp lệ
                        .anyRequest().authenticated()
                )
                // 3. Tắt Session (Vì dùng JWT là Stateless, không lưu session trên server)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 4. Set Provider
                .authenticationProvider(authenticationProvider)
                // 5. Thêm Filter của mình vào TRƯỚC filter mặc định của Spring
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    // 2. THÊM BEAN NÀY: Cấu hình chi tiết CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Cho phép Frontend (localhost:3000) gọi vào
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));

        // Cho phép các method này
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Cho phép mọi header (Authorization, Content-Type...)
        configuration.setAllowedHeaders(List.of("*"));

        // Cho phép gửi Credentials (nếu sau này dùng Cookie)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Áp dụng cho tất cả đường dẫn
        return source;
    }
}

