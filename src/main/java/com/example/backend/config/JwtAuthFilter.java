package com.example.backend.config;

import com.example.backend.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String servletPath = request.getServletPath();
        if(servletPath.matches("^/api/auth/.*$") || !servletPath.contains("/api/")){
            filterChain.doFilter(request,response);
            return;
        }
        System.out.println(request.getServletPath());
        final String authHeader = request.getHeader("Authorization");
        String jwtToken = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwtToken = authHeader.substring(7);
        } else if(request.getCookies() != null ){
            Optional<Cookie> cookies = Arrays.stream(request.getCookies()).filter(cookie -> cookie.getName().equals("y-access-token")).findAny();
            if (cookies.isPresent())
                jwtToken = cookies.get().getValue();
        } else{
            filterChain.doFilter(request, response);
            return;
        }

        final String login;

        try{
            login = jwtService.extractLogin(jwtToken);
        }catch (ExpiredJwtException e){
            response.setStatus(423);
            response.setContentType(APPLICATION_JSON_VALUE);
            response.getOutputStream().print(e.getMessage());
            return;
        }
        if (login != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = userDetailsService.loadUserByUsername(login);

            if (jwtService.isTokenValid(jwtToken,userDetails)){
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
