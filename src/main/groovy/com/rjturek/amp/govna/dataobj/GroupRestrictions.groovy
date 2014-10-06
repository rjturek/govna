package com.rjturek.amp.govna.dataobj

class GroupRestrictions {
    String groupName  // unique identifier
    Restriction restriction
    List<ArtifactRestriction> artifactRestrictions
    List<VersionRestriction>  versionRestrictions
    List<ArtifactVersionRestriction> artifactVersionRestrictions

    GroupRestrictions() { }
}


