package com.rodgers.fines.web.webclient;

import com.rodgers.fines.web.common.vo.User;
import com.rodgers.fines.web.webclient.response.FinesInternalErrorResponse;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
public class HttpHelper {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    // TODO - base URL should be pulled from config
    public static final String VALID_LOGIN = "http://localhost:8081/users/validLogin";


    public static HttpResponse<String> getLoginResponse(String name, String password) {
        try {
            log.info("Got log on request for {}",name);
            HttpRequest request = getJsonRequest(VALID_LOGIN, MAPPER.writeValueAsString(new User(name, password)));
            if(request != null) {
                return CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            }
        } catch(ConnectException connectException) {
            log.error("Issue with login API {}", connectException.getMessage());
            return new FinesInternalErrorResponse("API Service unavailable");
        } catch (IOException | InterruptedException e) {
            log.error("Error while calling login service", e);
        }
        return new FinesInternalErrorResponse("Issue in Request/Response to login service");
    }


    private static HttpRequest getJsonRequest(String uri, String body) {
        try {
            return HttpRequest.newBuilder(new URI(uri)).header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body)).build();
        } catch (URISyntaxException uriSyntaxException) {
            log.error("Could not create Http Request", uriSyntaxException);
        }
        return null;
    }
}
