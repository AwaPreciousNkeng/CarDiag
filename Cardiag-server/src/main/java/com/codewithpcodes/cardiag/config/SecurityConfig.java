package com.codewithpcodes.cardiag.config;

import com.codewithpcodes.cardiag.auth.oauth2.CustomOAuth2UserService;
import com.codewithpcodes.cardiag.auth.oauth2.OAuth2AuthenticationFailureHandler;
import com.codewithpcodes.cardiag.auth.oauth2.OAuth2AuthenticationSuccessHandler;
import com.codewithpcodes.cardiag.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

import static org.springframework.http.HttpHeaders.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private static final String[] AUTH_WHITELIST = {
            "/api/v1/auth/**",
            "/uploads/users/**",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/v3/api-docs/**",
            "/webjars/**",
            "/v3/api-docs",
            "/v3/api-docs.yaml",
            "/swagger-resources",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html"
    };
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authProvider;
    private final LogoutHandler logoutHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler,
            OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler,
            CustomOAuth2UserService customOAuth2UserService
    ) {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req ->
                        req.requestMatchers(AUTH_WHITELIST)
                                .permitAll()
                                .requestMatchers("/api/v1/admin/**").hasAnyRole(Role.ADMIN.name())
                                .anyRequest()
                                .authenticated()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .defaultAuthenticationEntryPointFor(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                                apiRequestMatcher()
                        )
                )
                .authenticationProvider(authProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(endpoint ->
                                endpoint.baseUri("/oauth2/authorization"))
                        .userInfoEndpoint(userInfo ->
                                userInfo.userService(customOAuth2UserService))
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        .failureHandler(oAuth2AuthenticationFailureHandler)
                )
                .logout(logout ->
                        logout.logoutUrl("/api/v1/auth/logout")
                                .addLogoutHandler(logoutHandler)
                                .logoutSuccessHandler(
                                        (request, response, authentication) ->
                                                SecurityContextHolder.clearContext()
                                )
                );
        return http.build();
    }

    private RequestMatcher apiRequestMatcher() {
        return request -> request.getRequestURI().startsWith("/api/");
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
        config.setAllowedHeaders(Arrays.asList(
                ORIGIN,
                CONTENT_TYPE,
                ACCEPT,
                AUTHORIZATION
        ));
        config.setAllowedMethods(Arrays.asList(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS",
                "PATCH"
        ));
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
