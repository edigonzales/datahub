package ch.so.agi.datahub;

import java.util.Collections;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TokenService {
    //@Value("${oauth2.token.endpoint}")
    private String tokenEndpoint = "https://test-sogis.auth.eu-central-1.amazoncognito.com/oauth2/token";

    //@Value("${oauth2.client.id}")
    private String clientId = "xxxx";

    //@Value("${oauth2.client.secret}")
    private String clientSecret = "yyyy";

    //@Value("${oauth2.username}")
    private String username = "dddddd";

    //@Value("${oauth2.password}")
    private String password = "fffffff";

    //@Value("${oauth2.scope}")
    private String scope = "test-foo/read";

    public String getAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String credentials = clientId + ":" + clientSecret;
        String base64Credentials = new String(java.util.Base64.getEncoder().encode(credentials.getBytes()));
                
        headers.add("Authorization", "Basic " + base64Credentials);

        HttpEntity<String> request = new HttpEntity<>(
                "grant_type=client_credentials&scope=" + scope,
                headers);

        System.out.println("request: " + request.toString());
        
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(
                tokenEndpoint,
                HttpMethod.POST,
                request,
                String.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new RuntimeException("Error: " + response.getStatusCode());
        }
    }
}
