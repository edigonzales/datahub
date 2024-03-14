package ch.so.agi.datahub.auth;

import java.io.IOException;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.so.agi.datahub.model.GenericResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RevokeApiKeyFilter extends OncePerRequestFilter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    ObjectMapper mapper;
    
//    @Value("${app.apiKeyHeaderName}")
//    private String apiKeyHeaderName;
    
    final String headerName;
    
    public RevokeApiKeyFilter(ObjectMapper mapper, final String headerName) {
        this.mapper = mapper;
        this.headerName = headerName;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String host = ServletUriComponentsBuilder.fromCurrentRequest().build().getHost();
        String proto = ServletUriComponentsBuilder.fromCurrentRequest().build().getScheme();
        String apiKey = request.getHeader(headerName);

        if (apiKey != null) {
            if (proto.equalsIgnoreCase("http") && !host.equalsIgnoreCase("localhost")) {
                logger.warn("REVOKE KEY");
                
                // TODO
                
                
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                ServletOutputStream responseStream = response.getOutputStream();
                mapper.writeValue(responseStream, new GenericResponse(this.getClass().getCanonicalName(),
                        "API key is revoked.", Instant.now()));
                responseStream.flush();
                return;
            }            
        }

        filterChain.doFilter(request, response);
    }
}
