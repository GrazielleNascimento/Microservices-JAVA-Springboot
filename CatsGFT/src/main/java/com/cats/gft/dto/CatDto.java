package com.cats.gft.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CatDto {

    @NotBlank(message = "The 'name' field is required and cannot be empty.")
    @Size(max = 250, message = "The 'name' field must have a maximum of 250 characters.")
    String name;

    @NotBlank(message = "The 'breed' field is required and cannot be empty.")
    @Size(max = 250, message = "The 'breed' field must have a maximum of 250 characters.")
    String breed;

    String imageUrl;

    public CatDto(String name, String breed, String imageUrl) {
        this.name = name;
        this.breed = breed;
        this.imageUrl = imageUrl;
    }

}
