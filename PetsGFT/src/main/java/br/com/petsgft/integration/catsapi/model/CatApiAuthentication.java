package br.com.petsgft.integration.catsapi.model;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString
public class CatApiAuthentication {

    private String token;
    private LocalDateTime createdAt;

}
