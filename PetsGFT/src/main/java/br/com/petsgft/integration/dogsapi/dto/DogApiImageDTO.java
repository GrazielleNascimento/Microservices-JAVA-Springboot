package br.com.petsgft.integration.dogsapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DogApiImageDTO {

    @JsonProperty("breed")
    private String breed;

    @JsonProperty("imageUrl")
    private String imageUrl;

}
