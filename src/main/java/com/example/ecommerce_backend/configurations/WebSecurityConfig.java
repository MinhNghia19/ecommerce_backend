package com.example.ecommerce_backend.configurations;

import com.example.ecommerce_backend.filters.JwtTokenFilter;
import com.example.ecommerce_backend.services.user.CustomOAuth2UserService;
//import com.example.ecommerce_backend.handlers.CustomAuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.springframework.http.HttpMethod.GET;

@Configuration
//@EnableMethodSecurity
@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebMvc
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;
    private final CustomOAuth2UserService oauth2UserService;

    @Value("${api.prefix}")
    private String apiPrefix;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF if using stateless authentication like JWT
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(
                                String.format("%s/users/register", apiPrefix),
                                String.format("%s/users/google-signin", apiPrefix),
                                String.format("%s/users/login", apiPrefix),
                                String.format("%s/auth/google/code", apiPrefix),
                                String.format("%s/user/details", apiPrefix),

                                // Healthcheck
                                String.format("%s/healthcheck/**", apiPrefix),
                                // Swagger
                                "/api-docs",
                                "/api-docs/**",
                                "/swagger-resources",
                                "/swagger-resources/**",
                                "/configuration/ui",
                                "/configuration/security",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/webjars/swagger-ui/**",
                                "/swagger-ui/index.html"
                        ).permitAll()
                        .requestMatchers(GET,
                                String.format("%s/subcategories/**", apiPrefix),
                                String.format("%s/categories/**", apiPrefix),
                                String.format("%s/products/**", apiPrefix)
                        ).permitAll()
                        .anyRequest().authenticated()
                )
//                .oauth2Login(oauth2Login -> oauth2Login
//                        .defaultSuccessUrl("/", true)
//                        .failureUrl("/login?error=true")
//                )
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        // Ensure any endpoints can be matched
        http.securityMatcher(String.valueOf(EndpointRequest.toAnyEndpoint()));
        return http.build();
    }
}
