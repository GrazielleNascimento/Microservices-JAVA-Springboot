package br.com.petsgft.integration.dogsapi.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DogApiAuthentication {

    String token;
    LocalDateTime createdAt;

}
