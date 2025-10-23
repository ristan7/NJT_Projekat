package rs.ac.bg.fon.e_learning_platforma_njt.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import rs.ac.bg.fon.e_learning_platforma_njt.security.AppUserDetailsService;
import rs.ac.bg.fon.e_learning_platforma_njt.security.JwtAuthFilter;

import java.util.List;
import java.util.Map;
import org.springframework.core.annotation.Order;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final AppUserDetailsService uds;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter, AppUserDetailsService uds) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.uds = uds;
    }

    /* ====== API CHAIN (samo /api/**) ====== */
    @Bean
    @Order(0)
    SecurityFilterChain apiChain(HttpSecurity http) throws Exception {
        http
                // veži ovaj lanac SAMO za /api/**
                .securityMatcher("/api/**")
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                .authenticationEntryPoint(authenticationEntryPoint())
                .accessDeniedHandler(accessDeniedHandler())
                )
                .authorizeHttpRequests(auth -> auth
                // preflight
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // AUTH javno
                .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/register").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/logout").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/auth/me", "/api/auth/users").authenticated()
                // LOOKUPS javno (bez ograničenja na metodu – bezbedno je)
                .requestMatchers("/api/notification-types/**",
                        "/api/course-levels/**",
                        "/api/course-statuses/**",
                        "/api/lesson-types/**",
                        "/api/material-types/**").permitAll()
                // NOTIFICATIONS
                .requestMatchers(HttpMethod.GET, "/api/notifications/**").authenticated()
                .requestMatchers(HttpMethod.PATCH, "/api/notifications/*/read").authenticated()
                .requestMatchers(HttpMethod.PATCH, "/api/notifications/read-all").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/notifications/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/notifications/**").authenticated()
                // COURSES
                .requestMatchers(HttpMethod.GET, "/api/courses/**").hasAnyRole("STUDENT", "TEACHER", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/courses/**").hasRole("TEACHER")
                .requestMatchers(HttpMethod.PUT, "/api/courses/**").hasRole("TEACHER")
                .requestMatchers(HttpMethod.PATCH, "/api/courses/**").hasRole("TEACHER")
                .requestMatchers(HttpMethod.DELETE, "/api/courses/**").hasRole("TEACHER")
                // LESSONS
                .requestMatchers(HttpMethod.GET, "/api/lessons/**", "/api/courses/*/lessons/**").hasAnyRole("STUDENT", "TEACHER", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/courses/*/lessons/**").hasRole("TEACHER")
                .requestMatchers(HttpMethod.PUT, "/api/lessons/**").hasRole("TEACHER")
                .requestMatchers(HttpMethod.PATCH, "/api/lessons/**").hasRole("TEACHER")
                .requestMatchers(HttpMethod.DELETE, "/api/lessons/**").hasRole("TEACHER")
                .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /* ====== FALLBACK CHAIN (sve van /api/** je dozvoljeno) ====== */
    @Bean
    @Order(1)
    SecurityFilterChain appChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/**")
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(a -> a.anyRequest().permitAll());
        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(uds);
        p.setPasswordEncoder(passwordEncoder());
        return p;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration c) throws Exception {
        return c.getAuthenticationManager();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, ex) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            new ObjectMapper().writeValue(response.getOutputStream(), Map.of(
                    "error", "Unauthorized",
                    "message", "You must be authenticated to access this resource",
                    "path", request.getRequestURI()
            ));
        };
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, ex) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            new ObjectMapper().writeValue(response.getOutputStream(), Map.of(
                    "error", "Forbidden",
                    "message", ex.getMessage() != null ? ex.getMessage() : "Access is denied",
                    "path", request.getRequestURI()
            ));
        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration c = new CorsConfiguration();
        c.setAllowedOrigins(List.of("http://localhost:3000"));
        c.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        c.setAllowedHeaders(List.of("*"));
        c.setExposedHeaders(List.of("Authorization"));
        c.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource s = new UrlBasedCorsConfigurationSource();
        s.registerCorsConfiguration("/**", c);
        return s;
    }
}
