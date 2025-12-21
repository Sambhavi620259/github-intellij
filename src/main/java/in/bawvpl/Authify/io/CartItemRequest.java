package in.bawvpl.Authify.io;

import lombok.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemRequest {

    @NotBlank
    private String productId;

    @NotBlank
    private String productName;

    @NotNull
    @Min(0)
    private Double price;

    @NotNull
    @Min(1)
    private Integer quantity;
}
