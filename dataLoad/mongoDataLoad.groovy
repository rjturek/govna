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
    static Logger logger = Logger.getLogger( "MongoDataLoad" )

    /* we are going to need database access */
    static DependencyDao dependencyDao = new DependencyDao()


    private static GroupRestrictions convertObjectToGroupRestriction( Object group ){
        logger.info( "convertObjectToGroupRestriction()" )

        GroupRestrictions groupRestrictions = new GroupRestrictions()
        groupRestrictions.groupName = group.group

    }

    /**
     * check to see if a document already exists in the db for a particular group name.
     *
     * @param groupName
     * @return true/false
     */
    private static boolean checkGroupNameExistenceInDb( String groupName ){
        logger.info( "checkGroupNameExistenceInDb()" )

        def groupRestrictions = dependencyDao.getGroupRestrictions( groupName )

        if( !groupRestrictions ){
            logger.info( "Group Restrictions do not exist for: ${groupName}" )
            return false
        }
        logger.info( "Found Group Restrictions for: ${groupName}" )

        return true
    }


    /**
     * Create a Restriction object to be applied at any level (group, artifact, version)
     *
     * @param isDeprecated
     * @param message
     * @param permittedConsumers
     * @return Restriction
     */
    private static Object createRestriction( boolean isDeprecated, String  message, List<String> permittedConsumers ){
        logger.info( "createRestriction()" )
        Restriction restriction =  new Restriction()
        restriction.isDeprecated = isDeprecated
        restriction.message = message
        restriction.exemptConsumers = permittedConsumers

        return restriction
    }


    /**
     * create a GroupOnly GroupRestriction Object
     *
     * @param groupId
     * @param restriction
     * @return GroupRestriction
     *
     */
    private static Object createGroupRestriction( String groupName, List<String> permittedConsumers, String restrictionMessage,  Restriction restriction ){
        logger.info( "createGroupRestriction()" )

        if (!checkGroupNameExistenceInDb( groupName ) ) {
            logger.info( "Group Name: ${groupName} does not exist in database.  Creating new group restriction." )

            GroupRestrictions groupRestrictions = new GroupRestrictions()

            groupRestrictions.groupName   = groupName
            groupRestrictions.restriction = restriction

            return groupRestrictions

        } else {
            logger.info( "Group Name: ${groupName} exists in database.  Adding consumer groups to the restriction." )

            GroupRestrictions groupRestrictions = dependencyDao.getGroupRestrictions( groupName )

            /* if it's not there add the entire list, else just add to the list */
            if ( !groupRestrictions.restriction.exemptConsumers ){
                /*  if there isn't a exemptConsumers List for a group restriction type,
                 *  that probably means the data file has a wrong entry in it.
                 */
                logger.severe( "Restriction does not contain Permitted Consumers list for : ${groupName}" )
                logger.severe( "I really was expecting one!" )

                groupRestrictions.restriction.exemptConsumers = permittedConsumers
                groupRestrictions.restriction.message = restrictionMessage

            }else {
                logger.info( " Group Restriction contains an permitted Consumers List. Adding to it." )

                groupRestrictions.restriction.exemptConsumers.add( permittedConsumers )

                logger.warning( "Overwriting the deprecation" )
            }

            return groupRestrictions

        }
    }


    /**
     * create restrictions object on a group and an artifact.
     *
     * @param groupId
     * @param artifactId
     * @param restriction
     * @return GroupRestriction
     *
     */
    private static Object createGroupAndArtifactRestriction( String groupName, String artifactId, Restriction restriction ) {
        logger.info("createGroupAndArtifactRestriction()")

        /* create an artifact restriction with what we have in hand and throw it into a list */
        ArtifactRestriction artifactRestriction = new ArtifactRestriction()
        artifactRestriction.artifactId  = artifactId
        artifactRestriction.restriction = restriction

        List<ArtifactRestriction> artifactRestrictionList = Lists.newArrayList( artifactRestriction )

        if (!checkGroupNameExistenceInDb( groupName ) ) {
            logger.info( "Group Name: ${groupName} does not exist in database.  Creating new group and artifact restriction." )

            GroupRestrictions groupRestrictions = new GroupRestrictions()
            groupRestrictions.groupName = groupName

            /* add the artifact restriction list to the Group Restriction */
            groupRestrictions.artifactRestrictions = artifactRestrictionList

            return groupRestrictions

        } else {
            logger.info( "Group Name: ${groupName} exists in database.  Adding new group and artifact restriction." )

            GroupRestrictions groupRestrictions = dependencyDao.getGroupRestrictions( groupName )

            /* if it's not there, add the entire list, else just add to the list */
            if ( !groupRestrictions.artifactRestrictions ){
                logger.info( "Group Restriction does not contain an artifact restriction." )

                groupRestrictions.artifactRestrictions = artifactRestrictionList

            }else {
                logger.info( "Group Restriction contains an artifact restriction." )

                groupRestrictions.artifactRestrictions.add(artifactRestriction)
            }

            groupRestrictions
        }
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
        logger.info( "createGroupVersionRestriction" )

        /* generate the restriction and throw it in a List */
        VersionRestriction versionRestriction = new VersionRestriction()
        versionRestriction.versionLow = versionLow
        versionRestriction.versionHigh = versionHigh
        versionRestriction.restriction = restriction

        List<VersionRestriction> versionRestrictionList = Lists.newArrayList( versionRestriction )

        if (!checkGroupNameExistenceInDb( groupName ) ) {
            logger.info( "Group Name: ${groupName} does not exist in database.  Creating new group version restriction." )

            GroupRestrictions groupRestrictions = new GroupRestrictions()
            groupRestrictions.groupName = groupName
            groupRestrictions.versionRestrictions = versionRestrictionList

            return groupRestrictions

        } else {
            logger.info("Group Name: ${groupName} exists in database.  Adding new group version restriction.")

            GroupRestrictions groupRestrictions = dependencyDao.getGroupRestrictions(groupName)

            /* if it's not there add the entire list, else just add to the list */
            if ( !groupRestrictions.versionRestrictions ){
                logger.info( "Group Restriction does not contain a version restriction." )

                groupRestrictions.versionRestrictions = versionRestrictionList

            }else {
                logger.info( "Group Restriction contains a version restriction." )

                groupRestrictions.versionRestrictions.add(versionRestriction)
            }

            return groupRestrictions
        }
    }

    /**
     * create a restriction for a group artifact and version
     *
     * @param groupName
     * @param artifactId
     * @param versionLow
     * @param versionHigh
     * @param restriction
     * @return
     */
    private static Object createGroupArtifactVersionRestriction( String groupName, String artifactId, String versionLow, String versionHigh, Restriction restriction ){
        logger.info( "createGroupArtifactVersionRestriction" )

        /* generate the nested restrictions and throw it into a List */
        VersionRestriction versionRestriction = new VersionRestriction()
        versionRestriction.versionLow = versionLow
        versionRestriction.versionHigh = versionHigh
        versionRestriction.restriction = restriction

        ArtifactVersionRestriction artifactVersionRestriction = new ArtifactVersionRestriction()
        artifactVersionRestriction.artifactId = artifactId
        artifactVersionRestriction.versionRestriction = versionRestriction

        List<ArtifactVersionRestriction> artifactVersionRestrictionList = Lists.newArrayList( artifactVersionRestriction )

        /* do we add the restriction (if the group name already exists) or create a new one*/
        if ( !checkGroupNameExistenceInDb( groupName ) ) {
            logger.info( "Group Name: ${groupName} does not exist in database.  Creating new group artifact version restriction." )

            GroupRestrictions groupRestrictions = new GroupRestrictions()
            groupRestrictions.groupName = groupName
            groupRestrictions.artifactVersionRestrictions = artifactVersionRestrictionList

            return groupRestrictions

        } else {
            logger.info( "Group Name: ${groupName} exists in database.  Adding new group artifact version restriction." )

            GroupRestrictions groupRestrictions = dependencyDao.getGroupRestrictions(groupName)

            /* if it's not there add the entire list, else just add to the list */
            if ( !groupRestrictions.artifactVersionRestrictions ){
                logger.info( "Group Restriction does not contain an artifact version restriction." )

                groupRestrictions.artifactVersionRestrictions = artifactVersionRestrictionList

            }else{
                logger.info("Group Restriction contains an artifact version restriction.")

                groupRestrictions.artifactVersionRestrictions.add(artifactVersionRestriction)
            }

            return groupRestrictions
        }

    }

    static void main(String[] args) {


        /* !!the following line will wipe out all documents in the restrictions collection.  AKA wiping out the database.  Use with Caution!! */
        dependencyDao.wipeRestrictionsDocuments()

        def loadFile = new File( "mongoDataLoad.data" )

        loadFile.eachLine { line ->
            if( line.startsWith("#") ) { return }

            def ( t, g, a, h, l, d, p, m ) = line.split( "\\|")

            /* clean up the variables we just received from the split and name them properly */
            String restrictionType          = t.replaceAll( "\\s", "" )
            String groupName                = g.replaceAll( "\\s", "" )
            String artifactName             = a.replaceAll( "\\s", "" )
            String versionHigh              = h.replaceAll( "\\s", "" )
            String versionLow               = l.replaceAll( "\\s", "" )
            boolean isDeprecated            = d.replaceAll( "\\s", "" )
            List<String> permittedConsumers = p.replaceAll( "\\s", "" ).split( "\\," )
            String restrictionMessage       = m

            /* create a restriction to use */
            Restriction restriction = createRestriction( isDeprecated, restrictionMessage, permittedConsumers )

            switch ( restrictionType ) {

                case "g":
                    logger.info( "Group Only Restriction: ${restrictionType}" )

                    GroupRestrictions groupRestrictions = createGroupRestriction(groupName, permittedConsumers, restrictionMessage, restriction)
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