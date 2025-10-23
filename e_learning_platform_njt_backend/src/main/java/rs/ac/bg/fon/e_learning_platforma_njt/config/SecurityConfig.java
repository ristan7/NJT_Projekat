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

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
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
                // AUTH
                .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/register").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/auth/me", "/api/auth/users").authenticated()
                // Notifications
                .requestMatchers(HttpMethod.GET, "/api/notifications/**").authenticated()
                // mark single as read (/api/notifications/{id}/read)
                .requestMatchers(HttpMethod.PATCH, "/api/notifications/*/read").authenticated()
                // mark all as read (/api/notifications-read-all?userId=...)
                .requestMatchers(HttpMethod.PATCH, "/api/notifications-read-all/**").authenticated()
                // allow admin to create new notifications
                .requestMatchers(HttpMethod.POST, "/api/notifications/**").hasRole("ADMIN")
                // allow any authenticated user to delete their own notifications
                .requestMatchers(HttpMethod.DELETE, "/api/notifications/**").authenticated()
                // Courses
                .requestMatchers(HttpMethod.GET, "/api/courses/**").hasAnyRole("STUDENT", "TEACHER", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/courses/**").hasRole("TEACHER")
                .requestMatchers(HttpMethod.PUT, "/api/courses/**").hasRole("TEACHER")
                .requestMatchers(HttpMethod.PATCH, "/api/courses/**").hasRole("TEACHER")
                .requestMatchers(HttpMethod.DELETE, "/api/courses/**").hasRole("TEACHER")
                // Lessons
                .requestMatchers(HttpMethod.GET, "/api/lessons/**").hasAnyRole("STUDENT", "TEACHER", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/courses/{courseId}/lessons/**").hasRole("TEACHER")
                .requestMatchers(HttpMethod.PUT, "/api/lessons/**").hasRole("TEACHER")
                .requestMatchers(HttpMethod.PATCH, "/api/lessons/**").hasRole("TEACHER")
                .requestMatchers(HttpMethod.DELETE, "/api/lessons/**").hasRole("TEACHER")
                // Materials
                .requestMatchers(HttpMethod.GET, "/api/lessons/{lessonId}/materials/**").hasAnyRole("STUDENT", "TEACHER", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/lessons/{lessonId}/materials/**").hasRole("TEACHER")
                .requestMatchers(HttpMethod.PUT, "/api/lessons/{lessonId}/materials/**").hasRole("TEACHER")
                .requestMatchers(HttpMethod.DELETE, "/api/lessons/{lessonId}/materials/**").hasRole("TEACHER")
                .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        var provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(uds);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            Map<String, Object> body = Map.of(
                    "error", "Unauthorized",
                    "message", "You must be authenticated to access this resource",
                    "path", request.getRequestURI()
            );
            new ObjectMapper().writeValue(response.getOutputStream(), body);
        };
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, ex) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            Map<String, Object> body = Map.of(
                    "error", "Forbidden",
                    "message", ex.getMessage() != null ? ex.getMessage() : "Access is denied",
                    "path", request.getRequestURI()
            );
            new ObjectMapper().writeValue(response.getOutputStream(), body);
        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var c = new CorsConfiguration();
        c.setAllowedOrigins(List.of("http://localhost:3000"));
        c.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        c.setAllowedHeaders(List.of("*"));
        c.setExposedHeaders(List.of("Authorization"));
        c.setAllowCredentials(true);

        var s = new UrlBasedCorsConfigurationSource();
        s.registerCorsConfiguration("/**", c);
        return s;
    }
}
