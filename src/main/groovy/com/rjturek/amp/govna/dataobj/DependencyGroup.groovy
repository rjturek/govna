package com.rjturek.amp.govna.dataobj

class DependencyGroup {
    String groupName  // unique identifier
    Restriction restriction
    List<Artifact> artifactRestrictions
    List<Version>  versionRestrictions
    List<ArtifactVersion> artifactVersionRestrictions
}


