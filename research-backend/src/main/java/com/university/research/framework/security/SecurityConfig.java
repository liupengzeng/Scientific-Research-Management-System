package com.zhiye.backend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final UserDetailsServiceImpl userDetailsService;

        /**
     * 配置Spring Security过滤器链
     *
     * @param http HttpSecurity对象，用于配置安全策略
     * @return SecurityFilterChain 安全过滤器链
     * @throws Exception 配置过程中可能抛出的异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 禁用CSRF保护、表单登录和HTTP基本认证
        http
                // ✅ 开启 CORS 支持
                .cors(Customizer.withDefaults())
                // ✅ 关闭 CSRF（因为我们使用 JWT）
                .csrf(AbstractHttpConfigurer::disable)
                // ✅ 禁用表单登录（避免 302）
                .formLogin(AbstractHttpConfigurer::disable)
                // ✅ 禁用 session
                .sessionManagement(AbstractHttpConfigurer::disable)
                // ✅ 放行登录、注册、刷新 token 接口
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1/auth/login",
                                "/api/v1/auth/register",
                                "/api/v1/auth/refresh",
                                "/actuator/**",
                                "/api/v1/user/bookshelf/**",
                                "/api/v1/books/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                // 添加 JWT 过滤器
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);;

        return http.build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // ✅ 允许的前端源（开发时是 http://localhost:5173）
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        // ✅ 允许携带 cookie 或 Authorization header
        config.setAllowCredentials(true);
        // ✅ 允许的 HTTP 方法
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // ✅ 允许的请求头
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
        // ✅ 暴露的响应头
        config.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    /**
     * 创建并配置BCrypt密码编码器Bean
     *
     * @return BCryptPasswordEncoder实例，用于密码加密和验证
     */

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
