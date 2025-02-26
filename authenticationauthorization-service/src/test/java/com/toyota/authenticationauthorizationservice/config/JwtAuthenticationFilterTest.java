package com.toyota.authenticationauthorizationservice.config;

import com.toyota.authenticationauthorizationservice.dao.TokenRepository;
import com.toyota.authenticationauthorizationservice.domain.Token;
import com.toyota.authenticationauthorizationservice.service.abstracts.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private JwtService jwtService;
    @Mock
    private UserDetails userDetails;
    @Mock
    private TokenRepository tokenRepository;
    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void doFilterInternal() throws ServletException, IOException {
        // Given
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        FilterChain filterChain = Mockito.mock(FilterChain.class);
        String jwt = "Token";
        String username = "username";
        Date currentDate = new Date();
        Token token = new Token("1", false, new Date(currentDate.getTime() + 60000), null);
        String jti = "jti";

        // When
        Mockito.when(request.getHeader("Authorization")).thenReturn("Bearer Token");
        Mockito.when(jwtService.extractUsername(anyString())).thenReturn(username);
        Mockito.when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        Mockito.when(jwtService.isTokenValid(anyString(), any())).thenReturn(true);
        Mockito.when(jwtService.extractTokenId(anyString())).thenReturn(jti);
        Mockito.when(tokenRepository.findById(anyString())).thenReturn(Optional.of(token));
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        Mockito.verify(request, times(1)).getHeader("Authorization");
        Mockito.verify(jwtService, times(1)).extractUsername(jwt);
        Mockito.verify(jwtService, times(1)).isTokenValid(jwt, userDetails);
        Mockito.verify(userDetailsService, times(1)).loadUserByUsername(username);
        Mockito.verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_InvalidBearer() throws ServletException, IOException {
        // Given
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        FilterChain filterChain = Mockito.mock(FilterChain.class);

        // When
        Mockito.when(request.getHeader("Authorization")).thenReturn("Token");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        Mockito.verify(filterChain, times(1)).doFilter(request, response);
    }
}
