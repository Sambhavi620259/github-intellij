package in.bawvpl.Authify.config;

import in.bawvpl.Authify.filter.JwtRequestFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // ✅ NEW WAY (no deprecation warning)
                .csrf(csrf -> csrf.disable())

                // ✅ Stateless (JWT)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // ✅ Authorization rules
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1.0/register",
                                "/api/v1.0/login",
                                "/api/v1.0/login/verify-otp",
                                "/api/v1.0/send-otp",
                                "/api/v1.0/send-reset-otp",
                                "/api/v1.0/reset-password",
                                "/v3/api-docs/**",
                                "/swagger-ui/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                // ✅ JWT filter
                .addFilterBefore(
                        jwtRequestFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
