package com.rjturek.amp.govna.dataobj

class Restriction {
    boolean isDeprecated
    String  message
    String artifactId
    String versionLow
    String versionHigh
    List<String> exemptConsumers
}
