package br.com.msnotificationemail.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Response<T> {

    @JsonProperty("data")
    private T data;

    @JsonProperty("errors")
    private String errors;

}
