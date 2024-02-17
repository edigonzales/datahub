package ch.so.agi.datahub.filter;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.firewall.FirewalledRequest;
import org.springframework.security.web.header.HeaderWriterFilter;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

public class AuthoritiesLoggingAfterFilter extends GenericFilterBean {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
//        logger.info("{}",request.getContentLength());
//        logger.info("{}",request.getClass());
//        
//        logger.info("{}",foo.getClass());
//        logger.info("{}",foo.getParameter("files"));
//        logger.info("{}",foo.getContentType());
        
        StandardServletMultipartResolver multipartResolver = new StandardServletMultipartResolver();
        MultipartHttpServletRequest multipartRequest = multipartResolver.resolveMultipart((HttpServletRequest) request);
        
        logger.info("{}",multipartRequest.getFile("files"));
        
        MultipartFile multipartFile = multipartRequest.getFile("files");
        logger.info("Original fileName - " + multipartFile.getOriginalFilename());
//      logger.info("fileName - " + fileName);

//        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) foo;
        //Map<String, MultipartFile> multiParts =  multipartHttpServletRequest.getFileMap();

        
        
//        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
//
//        Set set = multipartRequest.getFileMap().entrySet(); 
//        Iterator i = set.iterator(); 
//        while(i.hasNext()) { 
//            Map.Entry me = (Map.Entry)i.next(); 
//            String fileName = (String)me.getKey();
//            MultipartFile multipartFile = (MultipartFile)me.getValue();
//            logger.info("Original fileName - " + multipartFile.getOriginalFilename());
//            logger.info("fileName - " + fileName);
//            //writeToDisk(fileName, multipartFile);
//        } 

        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (null != authentication) {
            logger.info("User: " + authentication.getName() + " is successfully authenticated and "
                    + "has the authorities " + authentication.getAuthorities().toString());
        }
        
        chain.doFilter(request, response);
    }

}
