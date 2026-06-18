package com.example.backend.integration.auth;


import com.example.backend.auth.dto.request.LoginRequest;
import com.example.backend.auth.dto.request.RegisterRequest;
import com.example.backend.auth.dto.response.AuthResponse;
import com.example.backend.integration.AbstractIntegrationTest;
import com.example.backend.order.dto.response.OrderResponse;
import com.example.backend.testsupport.EcommerceTestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerIT extends AbstractIntegrationTest {

    protected static final String TEST_USER_EMAIL = "testuser@test.com";
    protected static final String TEST_PASSWORD = "password";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private EcommerceTestClient api;

    @BeforeEach
    public void setup() {
        api = new EcommerceTestClient(mockMvc, objectMapper);
    }

    @Test
    void registerUser_returnsAuthResponse() throws Exception {
        AuthResponse response = registerUser();

        assertThat(response.jwt()).isNotBlank();
        assertThat(response.refreshToken()).isNotBlank();
    }

    @Test
    void protectedEndpoint_withTokenFromRegister_returnsOk() throws Exception {
        AuthResponse response = registerUser();

        mockMvc.perform(get("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + response.jwt()))
                .andExpect(status().isOk());
    }

    @Test
    void loginUser_withValidCredentials_returnsAuthResponse() throws Exception {
        registerUser();

        LoginRequest request =  new LoginRequest(TEST_USER_EMAIL, TEST_PASSWORD);

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        AuthResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), AuthResponse.class);
        assertThat(response.jwt()).isNotBlank();
        assertThat(response.refreshToken()).isNotBlank();
    }

    @Test
    void loginUser_withInvalidPassword_returnsUnauthorized() throws Exception {
        registerUser();
        LoginRequest request =  new LoginRequest(TEST_USER_EMAIL, "wrong-password");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpoint_withValidJwt_returnsOk() throws Exception {
        AuthResponse auth = registerUser();
        MvcResult orderResponse = mockMvc.perform(post("/api/orders")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + auth.jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(api.validCreateOrderRequest())))
                .andExpect(status().isCreated())
                .andReturn();
        OrderResponse order = objectMapper.readValue(orderResponse.getResponse().getContentAsString(), OrderResponse.class);

        mockMvc.perform(get("/api/orders/{id}", order.id())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + auth.jwt())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void protectedEndpoint_withTamperedJwt_returnsUnauthorized() throws Exception {
        AuthResponse auth = registerUser();

        String tamperedJwt = tamperSignature(auth.jwt());

        assertThat(tamperedJwt).isNotEqualTo(auth.jwt());

        mockMvc.perform(get("/api/orders")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tamperedJwt))
                .andExpect(status().isUnauthorized());
    }

    private String tamperSignature(String jwt) {
        int lastIndex = jwt.length() - 1;
        char replacement = jwt.charAt(lastIndex) == 'a' ? 'b' : 'a';
        return jwt.substring(0, lastIndex) + replacement;
    }

    private AuthResponse registerUser() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(TEST_USER_EMAIL, TEST_PASSWORD);
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readValue(result.getResponse().getContentAsString(), AuthResponse.class);
    }
}
