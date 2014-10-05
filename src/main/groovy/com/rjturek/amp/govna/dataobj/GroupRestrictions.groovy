package com.rjturek.amp.govna.dataobj

class GroupRestrictions {
    String _id        // auto assigned by mongoDB
    String groupName  // unique identifier
    Restriction restriction
    List<ArtifactRestriction> artifactRestrictions
    List<VersionRestriction>  versionRestrictions
    List<ArtifactVersionRestriction> artifactVersionRestrictions

    GroupRestrictions() { }
}


