package ch.so.agi.datahub.auth;

import org.springframework.stereotype.Service;

@Service
public class ApiKeyHeaderAuthService {
    public ApiKeyHeaderAuthenticationToken authenticate(ApiKeyHeaderAuthenticationToken apiKeyAuthenticationToken) {
        
        System.out.println("******* auth service");
        
        // You would usually verify the token, fetch the user details based on the token and set it to the user object
        // but for this demo, we will just populate the user object with dummy data
        if (apiKeyAuthenticationToken.getApiKey().equals("valid-token")) {
            AppUser authenticatedUser = new AppUser("John Doe");
            return new ApiKeyHeaderAuthenticationToken(apiKeyAuthenticationToken.getApiKey(), authenticatedUser);
        }

        return apiKeyAuthenticationToken;
    }

}
