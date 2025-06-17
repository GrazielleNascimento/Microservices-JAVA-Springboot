package br.com.msappointment.integrations.rabbitmq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetDTO {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("species")
    private String species;

    @JsonProperty("breed")
    private String breed;

    @JsonProperty("birthDate")
    private LocalDate birthDate;

    @JsonProperty("weight")
    private Double weight;

    @JsonProperty("color")
    private String color;

    @JsonProperty("description")
    private String description;

    @JsonProperty("tutor")
    private PetTutorDTO tutor;

    @JsonProperty("correlationId")
    private UUID correlationId;

}