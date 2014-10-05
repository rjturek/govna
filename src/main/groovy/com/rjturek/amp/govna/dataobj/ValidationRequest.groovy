package com.rjturek.amp.govna.dataobj


class ValidationRequest {
    String consumerGroup
    List<String> dependencyCoordinates // stated as group:artifactId:version
}
