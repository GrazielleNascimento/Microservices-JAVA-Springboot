package com.dogs.gft.api.dog;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ResponseBreedsDto {

    @JsonProperty("breeds")
    private List<BreedsDto> breeds;

    @JsonProperty("id")
    private String id;

    @JsonProperty("url")
    private String url;

    @JsonProperty("width")
    private String width;

    @JsonProperty("height")
    private String height;
}
