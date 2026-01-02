package com.rodgers.fines.web.authentication;

import com.rodgers.fines.web.authentication.vo.LoginRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class CustomAuthenticationProvider implements AuthenticationProvider {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    public static final String VALID_LOGIN = "http://localhost:8081/users/validLogin";

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        final String name = authentication.getName();
        final String password = Objects.requireNonNull(authentication.getCredentials()).toString();
        HttpResponse<String> resp = getLoginResponse(name, password);
        if(resp == null || resp.statusCode() == HttpStatus.BAD_REQUEST.value()) {
            log.info("Invalid Login attempt for user {}",name);
            return  null;
        }
        log.info("Valid login attempt granting user role for {}",name);
        return authenticateAgainstThirdPartyAndGetAuthentication(name, password);
    }

    private static HttpResponse<String> getLoginResponse(String name, String password) {
        try {
            String body = MAPPER.writeValueAsString(new LoginRequest(name, password));
            HttpRequest request = HttpRequest.newBuilder(new URI(VALID_LOGIN)).header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body)).build();
            return CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (URISyntaxException | IOException | InterruptedException e) {
            log.error("Error while calling login service", e);
        }
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    private static UsernamePasswordAuthenticationToken authenticateAgainstThirdPartyAndGetAuthentication(String name, String password) {
        final List<GrantedAuthority> grantedAuths = new ArrayList<>();
        grantedAuths.add(new SimpleGrantedAuthority("ROLE_USER"));
        final UserDetails principal = new User(name, password, grantedAuths);
        return new UsernamePasswordAuthenticationToken(principal, password, grantedAuths);
    }
}
