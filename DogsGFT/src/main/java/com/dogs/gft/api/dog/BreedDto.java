package com.dogs.gft.api.dog;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BreedDto {

    @JsonProperty("weight")
    private WeightDogResponseApiDto weightDogResponseApiDto;

    @JsonProperty("height")
    private HeightDogResponseApiDto heightDogResponseApiDto;

    @JsonProperty("id")
    private int id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("bred_for")
    private String bred_for;

    @JsonProperty("breed_group")
    private String breed_group;

    @JsonProperty("life_span")
    private String life_span;

    @JsonProperty("temperament")
    private String temperament;

    @JsonProperty("reference_image_id")
    private String reference_image_id;

}
