package io.github.flowrapp.infrastructure.input.rest.config.security;

import static org.springframework.security.config.Customizer.withDefaults;

import java.util.Base64;

import io.github.flowrapp.config.JwtTokenSettings;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration(proxyBeanMethods = false)
@EnableMethodSecurity
public class MainApiSecurityConfig {

  @Bean
  public SecurityFilterChain mainApiSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
    return httpSecurity
        .formLogin(AbstractHttpConfigurer::disable) // No Form Login
        .logout(AbstractHttpConfigurer::disable) // No Logout
        .csrf(AbstractHttpConfigurer::disable) // No CSRF
        .cors(AbstractHttpConfigurer::disable) // No CORS
        .httpBasic(withDefaults()) // Enable HTTP Basic Authentication
        .oauth2ResourceServer(it -> it.jwt(withDefaults())) // Enable Bearer Auth
        // No Session pls
        .sessionManagement(it -> it.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .securityMatcher("/api/**")
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/v1/ping").permitAll()
            .requestMatchers("/api/v1/auth/**").permitAll()
            .requestMatchers("/api/v1/invitations/register").permitAll()
            .anyRequest().authenticated())
        .build();
  }

  /** Password encoder instance */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /** JWT source acts as a holder for the access secretKey */
  @Bean
  public JWKSource<SecurityContext> jwkAccessSource(JwtTokenSettings jwtTokenSettings) {
    return new ImmutableSecret<>(
        Base64.getDecoder().decode(
            jwtTokenSettings.getAccessToken().getSecretKey()));
  }

  /** JWT source acts as a holder for the refresh secretKey */
  @Bean
  public JWKSource<SecurityContext> jwkRefreshSource(JwtTokenSettings jwtTokenSettings) {
    return new ImmutableSecret<>(
        Base64.getDecoder().decode(
            jwtTokenSettings.getRefreshToken().getSecretKey()));
  }

  /** JWT decoder for the access token and used by Oauth filter. */
  @Bean
  @Primary
  public JwtDecoder jwtAccessDecoder(JWKSource<SecurityContext> jwkAccessSource) {
    return NimbusJwtDecoder
        .withSecretKey(((ImmutableSecret<SecurityContext>) jwkAccessSource).getSecretKey())
        .build();
  }

  /** JWT decoder for the refresh token */
  @Bean
  public JwtDecoder jwtRefreshDecoder(JWKSource<SecurityContext> jwkRefreshSource) {
    return NimbusJwtDecoder
        .withSecretKey(((ImmutableSecret<SecurityContext>) jwkRefreshSource).getSecretKey())
        .build();
  }

  /** JWT encoder for the access token */
  @Bean
  public JwtEncoder jwtAccessEncoder(JWKSource<SecurityContext> jwkAccessSource) {
    return new NimbusJwtEncoder(jwkAccessSource);
  }

  /** JWT encoder for the refresh token */
  @Bean
  public JwtEncoder jwtRefreshEncoder(JWKSource<SecurityContext> jwkRefreshSource) {
    return new NimbusJwtEncoder(jwkRefreshSource);
  }

}
