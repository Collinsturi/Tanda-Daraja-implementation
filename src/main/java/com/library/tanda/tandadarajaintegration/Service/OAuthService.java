package com.library.tanda.tandadarajaintegration.Service;

import com.library.tanda.tandadarajaintegration.Data.OAuthResponse;
import com.library.tanda.tandadarajaintegration.utility.MpesaConfig;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OAuthService {
    private final MpesaConfig mpesaConfiguration;
    private final RestTemplate restTemplate;
    private final Logger LOGGER = LoggerFactory.getLogger(OAuthService.class);

    public String getAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(mpesaConfiguration.getConsumerKey(), mpesaConfiguration.getConsumerSecret());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<OAuthResponse> response = restTemplate.exchange(
                mpesaConfiguration.getOauthEndpoint(),
                HttpMethod.GET,
                request,
                OAuthResponse.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            return Objects.requireNonNull(response.getBody()).getAccessToken();
        } else {
            LOGGER.error("Error getting access token");
            throw new RuntimeException("Error getting access token");
        }
    }
}
