package com.rjturek.amp.govna.dataobj

class Restriction {
    static final String TYPE_DEPRECATED = "D"
    static final String TYPE_PROHIBITED = "P"
    String type
    String message
    String artifactId
    String versionLow
    String versionHigh
    List<String> exemptConsumers
}
