package com.rjturek.amp.govna.dataobj

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown=true)

class Restriction {
    @JsonIgnore
    static final String TYPE_DEPRECATED = "D"
    @JsonIgnore
    static final String TYPE_PROHIBITED = "P"
    String type
    String message
    String artifactId
    String versionLow
    String versionHigh
    List<String> exemptConsumers
}
