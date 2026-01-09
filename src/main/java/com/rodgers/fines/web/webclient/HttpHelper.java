package com.rodgers.fines.web.webclient;

import com.rodgers.fines.web.common.vo.User;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
public class HttpHelper {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    public static final String VALID_LOGIN = "http://localhost:8081/users/validLogin";


    public static HttpResponse<String> getLoginResponse(String name, String password) {
        try {
            String body = MAPPER.writeValueAsString(new User(name, password));
            HttpRequest request = HttpRequest.newBuilder(new URI(VALID_LOGIN)).header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body)).build();
            return CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (URISyntaxException | IOException | InterruptedException e) {
            log.error("Error while calling login service", e);
        }
        return null;
    }
}
