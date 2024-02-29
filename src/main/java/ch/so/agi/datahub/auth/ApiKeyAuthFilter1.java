package ch.so.agi.datahub.auth;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ApiKeyAuthFilter1 extends OncePerRequestFilter {

    @Autowired
    private ApiKeyAuthExtractor extractor;
    
//    public ApiKeyAuthFilter(ApiKeyAuthExtractor extractor) {
//        this.extractor = extractor;
//    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        extractor.extract(request).ifPresent(SecurityContextHolder.getContext()::setAuthentication);
        
        filterChain.doFilter(request, response);
    }
}
