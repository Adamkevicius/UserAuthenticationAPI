package com.example.userauthenticationapi.config;

import com.example.userauthenticationapi.security.filter.JwtFilter;
import com.example.userauthenticationapi.service.MyUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//TODO IMPL OAUTH2
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final MyUserDetailsService userDetailsService;

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) {
        try {
            return httpSecurity
                    .csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(request -> request
                            .requestMatchers(
                                    "/authentication/login",
                                    "/authentication/signup",
                                    "/authentication/verification-code/resend",
                                    "/authentication/verification-code/verify",
                                    "/oauth2/authentication").permitAll()
                            .anyRequest().authenticated()
                    )
                    .httpBasic(Customizer.withDefaults())
                    .sessionManagement(session ->
                            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
                "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**"
        );
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) {
        try {
            return authConfig.getAuthenticationManager();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
