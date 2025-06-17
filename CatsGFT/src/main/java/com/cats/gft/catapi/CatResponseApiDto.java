package com.cats.gft.catapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CatResponseApiDto {
    @JsonProperty("weight")
    private WeightCatResponseApiDto weight;

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("cfa_url")
    private String cfaUrl;

    @JsonProperty("vetstreet_url")
    private String vetstreetUrl;

    @JsonProperty("vcahospitals_url")
    private String vcahospitalsUrl;

    @JsonProperty("temperament")
    private String temperament;

    @JsonProperty("origin")
    private String origin;

    @JsonProperty("country_codes")
    private String countryCodes;

    @JsonProperty("country_code")
    private String countryCode;

    @JsonProperty("description")
    private String description;

    @JsonProperty("life_span")
    private String lifeSpan;

    @JsonProperty("indoor")
    private int indoor;

    @JsonProperty("lap")
    private int lap;

    @JsonProperty("alt_names")
    private String altNames;

    @JsonProperty("adaptability")
    private int adaptability;

    @JsonProperty("affection_level")
    private int affectionLevel;

    @JsonProperty("child_friendly")
    private int childFriendly;

    @JsonProperty("dog_friendly")
    private int dogFriendly;

    @JsonProperty("energy_level")
    private int energyLevel;

    @JsonProperty("grooming")
    private int grooming;

    @JsonProperty("health_issues")
    private int healthIssues;

    @JsonProperty("intelligence")
    private int intelligence;

    @JsonProperty("shedding_level")
    private int sheddingLevel;

    @JsonProperty("social_needs")
    private int socialNeeds;

    @JsonProperty("stranger_friendly")
    private int strangerFriendly;

    @JsonProperty("vocalisation")
    private int vocalisation;

    @JsonProperty("experimental")
    private int experimental;

    @JsonProperty("hairless")
    private int hairless;

    @JsonProperty("natural")
    private int natural;

    @JsonProperty("rare")
    private int rare;

    @JsonProperty("rex")
    private int rex;

    @JsonProperty("suppressed_tail")
    private int suppressedTail;

    @JsonProperty("short_legs")
    private int shortLegs;

    @JsonProperty("wikipedia_url")
    private String wikipediaUrl;

    @JsonProperty("hypoallergenic")
    private int hypoallergenic;

    @JsonProperty("reference_image_id")
    private String referenceImageId;
}