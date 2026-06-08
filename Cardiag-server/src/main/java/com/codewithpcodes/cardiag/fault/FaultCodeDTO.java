package com.codewithpcodes.cardiag.fault;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FaultCodeDTO {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("category")
    private String category;

    @JsonProperty("description")
    private String description;

    @JsonProperty("symptoms")
    private List<String> symptoms;

    @JsonProperty("causes")
    private List<String> causes;

    @JsonProperty("repairTips")
    private List<String> repairTips;

    @JsonProperty("urgency")
    private String urgency;

    @JsonProperty("warningLight")
    private String warningLight;
}

