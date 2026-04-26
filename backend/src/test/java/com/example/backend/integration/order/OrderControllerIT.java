package com.example.backend.integration.order;


import com.example.backend.auth.dto.response.AuthResponse;
import com.example.backend.integration.AbstractIntegrationTest;
import com.example.backend.order.OrderRepository;
import com.example.backend.order.OrderStatus;
import com.example.backend.order.dto.request.CreateOrderRequest;
import com.example.backend.order.dto.request.PatchOrderStatusRequest;
import com.example.backend.order.dto.response.OrderResponse;
import com.example.backend.order.item.dto.request.OrderItemRequest;
import com.example.backend.product.dto.response.ProductResponse;
import com.example.backend.testsupport.ProductApiTestClient;
import com.example.backend.user.Role;
import com.example.backend.user.RoleRepository;
import com.example.backend.user.User;
import com.example.backend.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Set;

import static com.example.backend.testsupport.ProductTestData.validCreateProductRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OrderControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    private ProductApiTestClient productApi;

    private String userJwt;
    private String adminJwt;

    @BeforeEach
    void setUp() throws Exception {
        productApi = new ProductApiTestClient(mockMvc, objectMapper);

        createUsersIfMissing();
        userJwt = getUserJwt();
        adminJwt = getAdminJwt();
    }

    @Test
    void createOrder_whenValidRequest() throws Exception {
        createOrder(validCreateOrderRequest());
    }

    @Test
    void patchOrderStatus_whenValidRequest() throws Exception {
        OrderResponse order = createOrder(validCreateOrderRequest());
        PatchOrderStatusRequest request = new PatchOrderStatusRequest(OrderStatus.PAID);

        mockMvc.perform(patch("/api/orders/status/{id}", order.id())
                        .header("Authorization", "Bearer " + adminJwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(OrderStatus.PAID.name()));
    }

    @Test
    void cancelOrder_whenValidRequest() throws Exception {
        OrderResponse order = createOrder(validCreateOrderRequest());

        mockMvc.perform(patch("/api/orders/{id}/cancel", order.id())
                        .header("Authorization", "Bearer " + userJwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(OrderStatus.CANCELLED.name()));
        orderRepository.findById(order.id()).ifPresent(order1 -> assertEquals(OrderStatus.CANCELLED.name(), order1.getStatus().name()));
    }

    private OrderResponse createOrder(CreateOrderRequest createOrderRequest) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + userJwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOrderRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readValue(
                result.getResponse().getContentAsString(),
                OrderResponse.class
        );
    }

    private CreateOrderRequest validCreateOrderRequest() throws Exception {
        List<OrderItemRequest> orderItemRequestList = List.of(validOrderItemRequest());
        return new CreateOrderRequest(orderItemRequestList);
    }

    private OrderItemRequest validOrderItemRequest() throws Exception {
        ProductResponse product = productApi.createProduct(validCreateProductRequest());
        return new OrderItemRequest(product.id(), 1);
    }

    private void createUsersIfMissing() {
        if (!userRepository.existsByEmail("testuser@test.com")) {
            Role role = roleRepository.findByName("USER").orElseThrow();
            User user = new User("testuser@test.com", passwordEncoder.encode("password"), Set.of(role));
            userRepository.save(user);
        }

        if (!userRepository.existsByEmail("admin@test.com")) {
            Role adminRole = roleRepository.findByName("ADMIN").orElseThrow();
            User admin = new User("admin@test.com", passwordEncoder.encode("password"), Set.of(adminRole));
            userRepository.save(admin);
        }
    }

    private String getUserJwt() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "email": "testuser@test.com",
                            "password": "password"
                        }
                        """))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readValue(loginResult.getResponse().getContentAsString(), AuthResponse.class).jwt();
    }

    private String getAdminJwt() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "email": "admin@test.com",
                            "password": "password"
                        }
                        """))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readValue(loginResult.getResponse().getContentAsString(), AuthResponse.class).jwt();
    }
}
