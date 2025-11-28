package ua.dmytrolutsiuk.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response containing subscription details")
public class SubscriptionResponse {

    @Schema(description = "Unique subscription identifier", example = "1")
    private Long id;

    @Schema(description = "Subscriber's email address", example = "user@example.com")
    private String email;

    @Schema(description = "Currency code for the subscription", example = "USD")
    private String currencyCode;

    @Schema(description = "Indicates if the subscription is active", example = "true")
    private Boolean active;

    @Schema(description = "Timestamp when the subscription was created", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;
}
