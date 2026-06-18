package com.velazco.velazco_back.dto.product.requests;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductUpdateActiveRequestDto {

    @NotNull(message = "Active status must be provided")
    private Boolean active;

    // GETTER MANUAL - Agrega esto
    public Boolean getActive() {
        return this.active;
    }
}