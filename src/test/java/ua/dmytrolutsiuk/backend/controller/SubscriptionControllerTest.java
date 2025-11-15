package ua.dmytrolutsiuk.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ua.dmytrolutsiuk.backend.dto.SubscriptionRequest;
import ua.dmytrolutsiuk.backend.dto.SubscriptionResponse;
import ua.dmytrolutsiuk.backend.exception.GlobalExceptionHandler;
import ua.dmytrolutsiuk.backend.service.SubscriptionService;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({SubscriptionController.class, GlobalExceptionHandler.class})
class SubscriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SubscriptionService subscriptionService;

    @Test
    void subscribe_shouldCreateSubscription() throws Exception {
        SubscriptionRequest request = SubscriptionRequest.builder()
                .email("test@example.com")
                .currencyCode("USD")
                .build();

        SubscriptionResponse response = SubscriptionResponse.builder()
                .id(1L)
                .email("test@example.com")
                .currencyCode("USD")
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        when(subscriptionService.subscribe(any(SubscriptionRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.currencyCode").value("USD"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void subscribe_shouldReturnBadRequestForInvalidEmail() throws Exception {
        SubscriptionRequest request = SubscriptionRequest.builder()
                .email("invalid-email")
                .currencyCode("USD")
                .build();

        mockMvc.perform(post("/api/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(subscriptionService, never()).subscribe(any());
    }

    @Test
    void subscribe_shouldReturnBadRequestForInvalidCurrencyCode() throws Exception {
        SubscriptionRequest request = SubscriptionRequest.builder()
                .email("test@example.com")
                .currencyCode("US")
                .build();

        mockMvc.perform(post("/api/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(subscriptionService, never()).subscribe(any());
    }

    @Test
    void subscribe_shouldReturnBadRequestForMissingFields() throws Exception {
        SubscriptionRequest request = SubscriptionRequest.builder().build();

        mockMvc.perform(post("/api/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(subscriptionService, never()).subscribe(any());
    }

    @Test
    void unsubscribe_shouldRemoveSubscription() throws Exception {
        doNothing().when(subscriptionService).unsubscribe("test@example.com", "USD");

        mockMvc.perform(delete("/api/subscriptions")
                        .param("email", "test@example.com")
                        .param("currencyCode", "USD"))
                .andExpect(status().isNoContent());

        verify(subscriptionService, times(1)).unsubscribe("test@example.com", "USD");
    }

    @Test
    void unsubscribe_shouldReturn500WhenSubscriptionNotFound() throws Exception {
        doThrow(new RuntimeException("Subscription not found"))
                .when(subscriptionService).unsubscribe("test@example.com", "USD");

        mockMvc.perform(delete("/api/subscriptions")
                        .param("email", "test@example.com")
                        .param("currencyCode", "USD"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Subscription not found"));
    }
}
