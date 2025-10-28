package com.medi.backend.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Spring Security 전역 설정
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Value("${cors.allowed-origins}")
    private String allowedOrigins;  // application.yml의 CORS 허용 출처
    
    /**
     * 비밀번호 암호화 Bean (BCrypt 알고리즘)
     * - 회원가입 시 비밀번호 암호화
     * - 로그인 시 비밀번호 검증
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * Security 필터 체인 설정
     * - 인증/인가 규칙
     * - CSRF, CORS 설정
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF 비활성화 (REST API는 stateless이므로 불필요)
            .csrf(csrf -> csrf.disable())
            
            // CORS 설정 적용
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // 인증 규칙 설정
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()       // 회원가입/로그인
                .requestMatchers("/swagger-ui/**").permitAll()      // Swagger UI
                .requestMatchers("/v3/api-docs/**").permitAll()     // Swagger API Docs
                .requestMatchers("/swagger-ui.html").permitAll()    // Swagger 메인
                .anyRequest().authenticated()                        // 나머지는 인증 필요
            )
            
            // 기본 폼 로그인 비활성화 (REST API 사용)
            .formLogin(form -> form.disable())
            
            // HTTP Basic 인증 비활성화
            .httpBasic(basic -> basic.disable());
        
        return http.build();
    }
    
    /**
     * CORS 설정 (프론트엔드와 통신하기 위해 필수)
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 허용할 출처 (프론트엔드 주소)
        List<String> origins = Arrays.asList(allowedOrigins.split(","));
        configuration.setAllowedOrigins(origins);
        
        // 허용할 HTTP 메서드
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // 허용할 헤더
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // 인증 정보 포함 여부 (쿠키 등)
        configuration.setAllowCredentials(false);
        
        // preflight 요청 캐시 시간 (1시간)
        configuration.setMaxAge(3600L);
        
        // 모든 경로에 CORS 설정 적용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
