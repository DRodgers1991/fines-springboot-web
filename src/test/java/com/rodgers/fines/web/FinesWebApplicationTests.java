package com.rodgers.fines.web;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.FormLoginRequestBuilder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FinesWebApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void loginWithValidUserThenAuthenticated() throws Exception {
		try (MockedStatic<com.rodgers.fines.web.webclient.HttpHelper> mockedHttpHelper = Mockito.mockStatic(com.rodgers.fines.web.webclient.HttpHelper.class)) {
			HttpResponse<String> mockResponse = Mockito.mock(HttpResponse.class);
			Mockito.when(mockResponse.statusCode()).thenReturn(200);
			Mockito.when(mockResponse.body()).thenReturn("success");
			mockedHttpHelper.when(() -> com.rodgers.fines.web.webclient.HttpHelper.getLoginResponse(eq("user"), eq("password"))).thenReturn(mockResponse);

			FormLoginRequestBuilder login = formLogin()
				.user("user")
				.password("password");

			mockMvc.perform(login)
				.andExpect(authenticated().withUsername("user"));
		}
	}

	@Test
	public void loginWithInvalidUserThenUnauthenticated() throws Exception {
		try (MockedStatic<com.rodgers.fines.web.webclient.HttpHelper> mockedHttpHelper = Mockito.mockStatic(com.rodgers.fines.web.webclient.HttpHelper.class)) {
			HttpResponse<String> mockResponse = Mockito.mock(HttpResponse.class);
			Mockito.when(mockResponse.statusCode()).thenReturn(400);
			Mockito.when(mockResponse.body()).thenReturn("invalid");
			mockedHttpHelper.when(() -> com.rodgers.fines.web.webclient.HttpHelper.getLoginResponse(anyString(), anyString())).thenReturn(mockResponse);

			FormLoginRequestBuilder login = formLogin()
				.user("invalid")
				.password("invalidpassword");

			mockMvc.perform(login)
				.andExpect(unauthenticated());
		}
	}

	@Test
	public void accessUnsecuredResourceThenOk() throws Exception {
		mockMvc.perform(get("/"))
			.andExpect(status().isOk());
	}

	@Test
	public void accessSecuredResourceUnauthenticatedThenRedirectsToLogin() throws Exception {
		mockMvc.perform(get("/hello"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/login"));
	}

	@Test
	@WithMockUser
	public void accessSecuredResourceAuthenticatedThenOk() throws Exception {
		MvcResult mvcResult = mockMvc.perform(get("/hello"))
				.andExpect(status().isOk())
				.andReturn();

		assertThat(mvcResult.getResponse().getContentAsString()).contains("Hello user!");
	}
}
