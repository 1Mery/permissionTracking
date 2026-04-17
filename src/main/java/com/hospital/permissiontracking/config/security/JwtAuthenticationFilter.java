package com.hospital.permissiontracking.config.security;

import com.hospital.permissiontracking.entity.User;
import com.hospital.permissiontracking.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // header yoksa devam et
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // token al
        String token = authHeader.substring(7);

        // email çıkar
        String email = jwtService.extractEmail(token);

        // user bul
        User user = userRepository.findByEmail(email)
                .orElse(null);

        // user varsa ve token geçerliyse
        if (user != null && jwtService.isTokenValid(token, user.getEmail())) {

            UsernamePasswordAuthenticationToken authToken;
            authToken = new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    List.of(
                    new SimpleGrantedAuthority(user.getRole().name()))
            );

            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }
}
