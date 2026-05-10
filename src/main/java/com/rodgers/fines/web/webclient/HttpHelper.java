package com.rodgers.fines.web.webclient;

import com.rodgers.fines.web.vo.User;
import com.rodgers.fines.web.vo.SignUpVo;
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

    public static HttpResponse<String> getLoginResponse(String name, String password) {
            log.info("Got log on request for {}",name);
            return getResponse(getJsonPostRequest("http://localhost:8081/users/validLogin", MAPPER.writeValueAsString(new User(name, password))));
    }

    public static HttpResponse<String> createUser(SignUpVo signup) {
        log.info("Got user create request for {}",signup.getUsername());
        return getResponse(getJsonPutRequest("http://localhost:8081/users/addUser", MAPPER.writeValueAsString(signup)));
    }

    private static HttpResponse<String> getResponse(HttpRequest request) {
        try {
            if(request != null) {
                return CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            }
        } catch(ConnectException connectException) {
            log.error("Issue with login API {}", connectException.getMessage());
            return new FinesInternalErrorResponse("API Service unavailable");
        } catch (IOException | InterruptedException e) {
            log.error("Error while calling user service", e);
        }
        return new FinesInternalErrorResponse("Issue in Request/Response to user service");
    }

    private static HttpRequest getJsonPostRequest(String uri, String body) {
        try {
            return getHeader(uri).POST(HttpRequest.BodyPublishers.ofString(body)).build();
        } catch (URISyntaxException uriSyntaxException) {
            log.error("Could not create Http Request", uriSyntaxException);
        }
        return null;
    }

    private static HttpRequest getJsonPutRequest(String uri, String body) {
        try {
            return getHeader(uri).PUT(HttpRequest.BodyPublishers.ofString(body)).build();
        } catch (URISyntaxException uriSyntaxException) {
            log.error("Could not create Http Request", uriSyntaxException);
        }
        return null;
    }

    private static HttpRequest.Builder getHeader(String uri) throws URISyntaxException {
        return HttpRequest.newBuilder(new URI(uri)).header("Content-Type", "application/json");
    }
}
