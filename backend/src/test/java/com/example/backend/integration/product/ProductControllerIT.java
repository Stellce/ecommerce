package com.example.backend.integration.product;

import com.example.backend.common.dto.PageResponse;
import com.example.backend.integration.AbstractIntegrationTest;
import com.example.backend.product.ProductRepository;
import com.example.backend.product.dto.request.CreateProductRequest;
import com.example.backend.product.dto.request.PatchProductRequest;
import com.example.backend.product.dto.response.ProductResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
public class ProductControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void cleanUp() {
        productRepository.deleteAll();
    }

    @Test
    void shouldCreateProduct_whenValidRequest() throws Exception {
        CreateProductRequest request = new CreateProductRequest(
                "Test product",
                "Test description",
                new BigDecimal("10.99"),
                5
        );
        ProductResponse response = createProduct(request);

        assertNotNull(response.id());
        assertDoesNotThrow(() -> UUID.fromString(response.id().toString()));
        assertEquals("Test product", response.name());
        assertEquals("Test description", response.description());
        assertEquals(new BigDecimal("10.99"), response.price());
        assertEquals(5, response.stock());

        assertEquals(1, productRepository.count());
    }

    @Test
    void shouldReturnBadRequest_whenCreateRequestIsInvalid() throws Exception {
        CreateProductRequest request = new CreateProductRequest(
                "Test product",
                null,
                new BigDecimal("10.99"),
                5
                );

        mockMvc.perform(post("/api/products").with(adminUser())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldPatchProduct_whenValidRequest() throws Exception {
        ProductResponse product = createProduct(validCreateProductRequest());

        PatchProductRequest request = new PatchProductRequest("Test product 1", null, null, null);

        MvcResult result = mockMvc.perform(patch("/api/products/{id}", product.id())
                .with(adminUser())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        ProductResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), ProductResponse.class);

        assertEquals(product.id(), response.id());
        assertEquals("Test product 1", response.name());
        assertEquals(product.description(), response.description());
        assertEquals(product.price(), response.price());
        assertEquals(product.stock(), response.stock());
    }

    @Test
    void shouldReturnNotFound_whenPatchProductDoesNotExist() throws Exception {
        PatchProductRequest request = new PatchProductRequest("Test product 1", null, null, null);
        mockMvc.perform(patch("/api/products/{id}", UUID.randomUUID()).with(adminUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequest_whenPatchRequestIsInvalid() throws Exception {
        ProductResponse createdProduct = createProduct(validCreateProductRequest());
        String json = """
                {
                    "stock": -1
                }
                """;
        mockMvc.perform(patch("/api/products/{id}", createdProduct.id()).with(adminUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetProductById_whenProductExists() throws Exception {
        ProductResponse product = createProduct(validCreateProductRequest());
        MvcResult result = mockMvc.perform(get("/api/products/{id}", product.id())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        ProductResponse response = objectMapper.readValue(result.getResponse().getContentAsString(),  ProductResponse.class);

        assertEquals(response.id(), product.id());
        assertEquals(response.name(), product.name());
        assertEquals(response.description(), product.description());
        assertEquals(response.price(), product.price());
        assertEquals(response.stock(), product.stock());
    }

    @Test
    void shouldReturnNotFound_whenProductDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/products/{id}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    void shouldGetAllProducts_whenProductsExist() throws Exception {
        ArrayList<UUID> productsIds = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            MvcResult result = mockMvc.perform(post("/api/products").with(adminUser())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validCreateProductRequest())))
                    .andExpect(status().isCreated())
                    .andReturn();

            ProductResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), ProductResponse.class);
            productsIds.add(response.id());
        }

        MvcResult result = mockMvc.perform(get("/api/products")).andExpect(status().isOk()).andReturn();
        PageResponse<ProductResponse> page = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<PageResponse<ProductResponse>>() {}
        );
        assertEquals(productsIds.size(), page.content().size());
    }

    @Test
    void shouldDeleteProduct_whenProductExists() throws Exception {
        ProductResponse product = createProduct(validCreateProductRequest());
        mockMvc.perform(delete("/api/products/{id}", product.id()).with(adminUser())).andExpect(status().isNoContent());
        assertFalse(productRepository.existsById(product.id()));
    }

    @Test
    void shouldReturnNotFound_whenDeleteProductDoesNotExist() throws Exception {
        mockMvc.perform(delete("/api/products/{id}", UUID.randomUUID()).with(adminUser())).andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnUnauthorized_whenAnonymousUserCreatesProduct() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCreateProductRequest())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnForbidden_whenRegularUserCreatesProduct() throws Exception {
        mockMvc.perform(post("/api/products")
                        .with(user("USER").authorities(new SimpleGrantedAuthority("USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCreateProductRequest())))
                .andExpect(status().isForbidden());
    }

    private ProductResponse createProduct(CreateProductRequest request) throws Exception {
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

    private CreateProductRequest validCreateProductRequest() {
        return new CreateProductRequest(
                "Test product",
                "Test description",
                new BigDecimal("10.99"),
                5
        );
    }

    private UserRequestPostProcessor adminUser() {
        return user("admin").authorities(new SimpleGrantedAuthority("ADMIN"));
    }
}
