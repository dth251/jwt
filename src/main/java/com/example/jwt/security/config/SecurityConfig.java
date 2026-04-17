package com.example.jwt.security.config;

import com.example.jwt.security.jwt.JWTAuthTokenFilter;
import com.example.jwt.security.principle.UserDetailsServiceCustom;
import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity   // Bật @PreAuthorize, @PostAuthorize
@RequiredArgsConstructor
public class SecurityConfig {

    private final JWTAuthTokenFilter jwtAuthenticationFilter;
    private final UserDetailsServiceCustom userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration auth) throws Exception {
        return auth.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Cấu hình CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // Tắt CSRF (REST API stateless không cần)
            .csrf(AbstractHttpConfigurer::disable)
            // Stateless session
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            // Xử lý lỗi xác thực và phân quyền
            .exceptionHandling(ex -> ex
                    .accessDeniedHandler(new AccessDeniedExceptionHandler())       // 403
                    .authenticationEntryPoint(new AuthenticationEntryPointHandler()) // 401
            )
            // Phân quyền theo endpoint
            .authorizeHttpRequests(auth -> auth
                    .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                    .requestMatchers("/error").permitAll()
                    // Public endpoints – đăng ký, đăng nhập
                    .requestMatchers("/api/v1/auth/**").permitAll()

                    // Chỉ ADMIN mới được truy cập
                    .requestMatchers("/api/v1/admin/**").hasAuthority("ADMIN")

                    // Chỉ MANAGER mới được truy cập
                    .requestMatchers("/api/v1/manager/**").hasAuthority("MANAGER")

                    // USER hoặc MANAGER
                    .requestMatchers("/api/v1/user-manager/**").hasAnyAuthority("USER", "MANAGER")

                    // USER đã đăng nhập
                    .requestMatchers("/api/v1/user/**").hasAuthority("USER")

                    // Tất cả các request khác phải được xác thực
                    .anyRequest().authenticated()
            );

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
