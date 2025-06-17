package com.dogs.gft.api.dog;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;


@Data
public class DogResponseImageApiDto {


  @JsonProperty("breeds")
  private List<BreedsDto> breeds;

  @JsonProperty("id")
  private String id;

  @JsonProperty("url")
  private String url;

  @JsonProperty("width")
  private int width;

  @JsonProperty("height")
  private int height;


}
