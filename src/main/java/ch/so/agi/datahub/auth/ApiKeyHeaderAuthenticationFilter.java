package ch.so.agi.datahub.auth;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ApiKeyHeaderAuthenticationFilter extends OncePerRequestFilter {

    private AuthenticationManager authenticationManager;
    
    private final String headerName;

    public ApiKeyHeaderAuthenticationFilter(AuthenticationManager authenticationManager, final String headerName) {
        this.authenticationManager = authenticationManager;
        this.headerName = headerName;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String apiKey = request.getHeader(headerName);
        if(apiKey == null) {
          this.logger.warn("Did not find api key header in request");
          filterChain.doFilter(request, response);
          return;
        }
        
        try {
            Authentication authentication = this.authenticationManager.authenticate(new ApiKeyAuthenticationToken(apiKey));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
          } catch (Exception e) {
            this.logger.error("Api Key Authentication failed");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            // If you want to immediatelly return an error response, you can do it like this:
            response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
            // but you can also just let the request go on and let the next filter handle it
            //filterChain.doFilter(request, response);
          }


    }

}
