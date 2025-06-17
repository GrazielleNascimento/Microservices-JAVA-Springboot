package com.dogs.gft.api.dog;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ImageDto {

    @JsonProperty("id")
    private String id;

    @JsonProperty("width")
    private int width;

    @JsonProperty("height")
    private int height;

    @JsonProperty("url")
    private String url;

}
