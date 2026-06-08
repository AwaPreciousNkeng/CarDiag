package com.codewithpcodes.cardiag.fault;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FaultDatabaseDTO {

    @JsonProperty("metadata")
    private MetadataDTO metadata;

    @JsonProperty("codes")
    private List<FaultCodeDTO> codes;
}
