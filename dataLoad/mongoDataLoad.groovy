/**
 * Created by ckell on 10/16/14.
 * mongoDataLoad.groovy - groovy script to read in mongoDataLoad.txt and create the restrictions collection in the
 *                        'govna' mongo database.  This provides the ability for all developers working on the project
 *                        to see the same dataset.
 */
import com.rjturek.amp.govna.db.DependencyDao
import com.rjturek.amp.govna.dataobj.*
import jersey.repackaged.com.google.common.collect.Lists

import java.util.logging.Logger

class MongoDataLoad {
    static Logger logger = Logger.getLogger("DataObjectTester")

    /**
     * Create a Restriction object to be applied at any level (group, artifact, version)
     *
     * @param isDeprecated
     * @param message
     * @param permittedConsumers
     * @return Restriction
     */
    private static Object createRestriction(boolean isDeprecated, String  message, List<String> permittedConsumers){
        logger.info("createRestriction()")
        Restriction restriction =  new Restriction()
        restriction.isDeprecated = isDeprecated
        restriction.message = message
        restriction.permittedConsumers = permittedConsumers

        restriction
    }

    /**
     * create a GroupOnly GroupRestriction Object
     *
     * @param groupId
     * @param restriction
     * @return GroupRestriction
     */
    private static Object createGroupOnlyRestriction(String groupName, Restriction restriction){
        logger.info("createGroupOnlyRestriction()")

        GroupRestrictions groupRestrictions = new GroupRestrictions()

        groupRestrictions.groupName = groupName
        groupRestrictions.restriction = restriction

        groupRestrictions

    }

    /**
     * create a json object that contains restrictions on a group and an artifact.
     *
     * @param groupId
     * @param artifactId
     * @param restriction
     * @return GroupRestriction
     *
     * TODO: make multiple artifact restrictions.  (AKA list)
     */
    private static Object createGroupAndArtifactRestriction(String groupName, String artifactId, Restriction restriction) {
        logger.info("createGroupAndArtifactRestriction()")

        GroupRestrictions groupRestrictions = new GroupRestrictions()
        groupRestrictions.groupName = groupName

        ArtifactRestriction artifactRestriction = new ArtifactRestriction()
        artifactRestriction.artifactId = artifactId
        artifactRestriction.restriction = restriction

        /* add the artifactRestriction to a List */
        List<ArtifactRestriction> artifactRestrictionList = Lists.newArrayList(artifactRestriction)

        /* add the artifact restriction list to the Group Restriction */
        groupRestrictions.artifactRestrictions = artifactRestrictionList

        groupRestrictions

    }

    /**
     *   create a restriction for a version rnge at the group level.
     *
     * @param groupName
     * @param versionLow
     * @param versionHigh
     * @param restriction
     * @return
     */
    private static Object createGroupVersionRestriction( String groupName, String versionLow, String versionHigh, Restriction restriction ){
        logger.info("createGroupVersionRestriction")

        GroupRestrictions groupRestrictions = new GroupRestrictions()
        groupRestrictions.groupName = groupName

        VersionRestriction versionRestriction = new VersionRestriction()
        versionRestriction.versionLow = versionLow
        versionRestriction.versionHigh = versionHigh
        versionRestriction.restriction = restriction

        List<VersionRestriction> versionRestrictionList = Lists.newArrayList(versionRestriction)

        groupRestrictions.versionRestrictions = versionRestrictionList

        groupRestrictions
    }

    private static Object createGroupArtifactVersionRestriction(String groupName, String artifactId, String versionLow, String versionHigh, Restriction restriction){
        logger.info("createGroupArtifactVersionRestriction")

        GroupRestrictions groupRestrictions = new GroupRestrictions()
        groupRestrictions.groupName = groupName

        ArtifactVersionRestriction artifactVersionRestriction = new ArtifactVersionRestriction()
        artifactVersionRestriction.artifactId = artifactId

        VersionRestriction versionRestriction = new VersionRestriction()
        versionRestriction.versionLow = versionLow
        versionRestriction.versionHigh = versionHigh
        versionRestriction.restriction = restriction

        artifactVersionRestriction.versionRestriction = versionRestriction
        List<ArtifactVersionRestriction> artifactVersionRestrictionList = Lists.newArrayList(artifactVersionRestriction)

        groupRestrictions.artifactVersionRestrictions = artifactVersionRestrictionList

        groupRestrictions


    }

    static void main(String[] args) {

        /* we are going to need database access */
        DependencyDao dependencyDao = new DependencyDao()
        /* !!the following line will wipe out all documents in the restrictions collection.  AKA wiping out the database.  Use with Caution!! */
        dependencyDao.wipeRestrictionsDocuments()

        def loadFile = new File("mongoDataLoad.data")

        loadFile.eachLine { line ->
            if(line.startsWith("#")) {return}

            def (t, g, a, h, l, d, p, m ) = line.split("\\|")

            /* clean up the variables we just received from the split and name them properly */
            String restrictionType          = t.replaceAll("\\s", "")
            String groupName                = g.replaceAll("\\s", "")
            String artifactName             = a.replaceAll("\\s", "")
            String versionHigh              = h.replaceAll("\\s", "")
            String versionLow               = l.replaceAll("\\s", "")
            boolean isDeprecated            = d.replaceAll("\\s", "")
            List<String> permittedConsumers = p.replaceAll("\\s", "").split("\\,")
            String restrictionMessage       = m

            /* create a restriction to use */
            Restriction restriction = createRestriction(isDeprecated, restrictionMessage, permittedConsumers)

            switch ( restrictionType ) {

                case "g":
                    logger.info("Group Only Restriction: ${restrictionType}")

                    GroupRestrictions groupRestrictions = createGroupOnlyRestriction(groupName, restriction)
                    dependencyDao.upsertGroupRestrictions(groupRestrictions)

                    break

                case "ga":
                    logger.info("Group Artifact Restriction: ${restrictionType}")

                    GroupRestrictions groupRestrictions = createGroupAndArtifactRestriction(groupName, artifactName, restriction)
                    dependencyDao.upsertGroupRestrictions(groupRestrictions)

                    break

                case "gv":
                    logger.info("Group Version Restriction: ${restrictionType}")

                    GroupRestrictions groupRestrictions = createGroupVersionRestriction(groupName, versionLow, versionHigh, restriction)
                    dependencyDao.upsertGroupRestrictions(groupRestrictions)

                    break

                case "gav":
                    logger.info("Group Artifact Version Restriction: ${restrictionType}")

                    GroupRestrictions groupRestrictions = createGroupArtifactVersionRestriction(groupName, artifactName, versionLow, versionHigh, restriction)
                    dependencyDao.upsertGroupRestrictions(groupRestrictions)

                    break

                default:
                    logger.warning("Could not process the following line correctly.")
                    logger.warning(line)
            }

        }
    }
}