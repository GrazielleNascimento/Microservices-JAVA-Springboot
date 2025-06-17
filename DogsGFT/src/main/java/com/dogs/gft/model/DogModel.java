package com.dogs.gft.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "tb_dogs")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DogModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Size(max = 250)
    private String name;

    @NotBlank
    @Size(max = 250)
    private String breed;

    private String imageUrl;
}
