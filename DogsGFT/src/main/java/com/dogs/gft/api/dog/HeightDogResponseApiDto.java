package com.dogs.gft.api.dog;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class HeightDogResponseApiDto {

    @JsonProperty("imperial")
    private String imperial;

    @JsonProperty("metric")
    private String metric;
}
