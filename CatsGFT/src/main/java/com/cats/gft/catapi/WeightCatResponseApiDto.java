package com.cats.gft.catapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WeightCatResponseApiDto {

    @JsonProperty("imperial")
    private String imperial;

    @JsonProperty("metric")
    private String metric;
}
