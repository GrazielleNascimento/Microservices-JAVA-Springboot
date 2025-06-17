package br.com.petsgft.api.pets.dto;

import br.com.petsgft.api.pets.model.PetSpeciesEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class PetDTO {

    @JsonProperty("id")
    private Integer id;

    @NotBlank(message = "Name is mandatory")
    @JsonProperty("name")
    private String name;

    @NotNull(message = "Species is mandatory")
    @Enumerated(EnumType.STRING)
    @JsonProperty("species")
    private PetSpeciesEnum species;

    @NotBlank(message = "Breed is mandatory")
    @JsonProperty("breed")
    private String breed;

    @NotNull(message = "Age is mandatory")
    @JsonProperty("birthDate")
    private LocalDate birthDate;

    @NotNull(message = "Weight is mandatory")
    @JsonProperty("weight")
    private double weight;

    @NotBlank(message = "Color is mandatory")
    @JsonProperty("color")
    private String color;

    @JsonProperty("description")
    private String description;

    @JsonProperty("imageUrl")
    private String imageUrl;

    @JsonProperty("tutor")
    private PetTutorDTO tutor;

    @JsonProperty("correlationId")
    private UUID correlationId;

    @JsonProperty("createdAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonProperty("updatedAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

}