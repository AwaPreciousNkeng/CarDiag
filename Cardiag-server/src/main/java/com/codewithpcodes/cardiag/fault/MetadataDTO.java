package com.codewithpcodes.cardiag.fault;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class MetadataDTO {

    @JsonProperty("title")
    private String title;

    @JsonProperty("version")
    private String version;

    @JsonProperty("totalCodes")
    private int totalCodes;

    @JsonProperty("schema")
    private String schema;

    @JsonProperty("categories")
    private List<String> categories;

    @JsonProperty("urgencyLevels")
    private List<String> urgencyLevels;

    @JsonProperty("warningLights")
    private List<String> warningLights;
}
