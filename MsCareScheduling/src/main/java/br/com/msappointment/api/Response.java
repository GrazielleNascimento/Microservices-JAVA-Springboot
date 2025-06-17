package br.com.msappointment.api;

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
