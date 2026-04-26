package com.example.backend.testsupport;

import com.example.backend.product.dto.request.CreateProductRequest;
import com.example.backend.product.dto.response.ProductResponse;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import static com.example.backend.testsupport.SecurityTestUtils.adminUser;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProductApiTestClient {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    public ProductApiTestClient(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    public ProductResponse createProduct(CreateProductRequest request) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/products")
                        .with(adminUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readValue(
                result.getResponse().getContentAsString(),
                ProductResponse.class
        );
    }
}
