package com.dogs.gft.api.dog;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DogResponseApiDto {

    @JsonProperty("width")
    private WeightDogResponseApiDto width;

    @JsonProperty("height")
    private HeightDogResponseApiDto height;

    @JsonProperty("id")
    private int id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("bred_for")
    private String bredFor;

    @JsonProperty("breed_group")
    private String breedGroup;

    @JsonProperty("life_span")
    private String lifeSpan;

    @JsonProperty("temperament")
    private String temperament;

    @JsonProperty("origin")
    private String origin;

    @JsonProperty("reference_image_id")
    private String referenceImageId;

    @JsonProperty("image")
    private ImageDto image;



}
