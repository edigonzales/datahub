package ch.so.agi.datahub.auth;

import java.io.IOException;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.so.agi.datahub.model.GenericResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ApiKeyHeaderAuthenticationFilter extends OncePerRequestFilter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

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
            logger.warn("Did not find api key header in request");
            //filterChain.doFilter(request, response);
            
            // Fall filterChain.doFilter() nicht gesetzt wird,
            // wird bereits hier abgebrochen. Es kann so keine
            // zweite Authentifizierungsmethode verwendet werden.
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            ServletOutputStream responseStream = response.getOutputStream();
//            ObjectMapper mapper =  new ObjectMapper();
//            mapper.writeValue(responseStream, new GenericResponse(this.getClass().getCanonicalName(), "Did not find api key header in request.", Instant.now()));
//            responseStream.flush();

            return;
        }
        
        try {
            Authentication authentication = this.authenticationManager.authenticate(new ApiKeyHeaderAuthenticationToken(apiKey));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
          } catch (Exception e) {
            logger.error("Api Key Authentication failed");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            // If you want to immediatelly return an error response, you can do it like this:
            response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
            // but you can also just let the request go on and let the next filter handle it
            //filterChain.doFilter(request, response);
          }


    }

}
