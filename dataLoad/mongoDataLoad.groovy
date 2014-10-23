/**
 * Created by ckell on 10/16/14.
 * mongoDataLoad.groovy - groovy script to read in mongoDataLoad.txt and create the restrictions collection in the
 *                        'govna' mongo database.  This provides the ability for all developers working on the project
 *                        to see the same dataset.
 */
import com.rjturek.amp.govna.db.DependencyDao
import com.rjturek.amp.govna.dataobj.*
import com.rjturek.amp.govna.utility.GenericStructUtil
import jersey.repackaged.com.google.common.collect.Lists

import java.util.logging.Logger

class MongoDataLoad {
    static Logger logger = Logger.getLogger( "MongoDataLoad" )

    /* we are going to need database access */
    static DependencyDao dependencyDao = new DependencyDao()

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
     * @param type
     * @param message
     * @param permittedConsumers
     * @return Restriction
     */
    private static Restriction createRestriction( String type, String artifactId, String versionLow, String versionHigh, String message, List<String> exemptConsumers ){
        logger.info( "MongoDataLoad.createRestriction()" )

        if (versionLow == null){
            logger.fine ( "low version is null. setting it to 0")
            versionLow = "0.0.0"
        }

        if (versionHigh == null){
            logger.fine("high version is null. setting it to Integer.MAX_VALUE")
            versionHigh = Integer.MAX_VALUE + "." + Integer.MAX_VALUE + "." + Integer.MAX_VALUE
        }

        Restriction restriction =  new Restriction()

        if (type == 'P'){
            restriction.type = Restriction.TYPE_PROHIBITED
        }

        if (type == 'D'){
            restriction.type = Restriction.TYPE_DEPRECATED
        }

        restriction.artifactId = artifactId
        restriction.versionLow = versionLow
        restriction.versionHigh = versionHigh
        restriction.message = message
        restriction.exemptConsumers = exemptConsumers

        return restriction
    }


    /**
     * create a GroupRestriction Object
     *
     * @param groupId
     * @param restriction
     * @return GroupRestriction
     *
     */
    private static GroupRestrictions createGroupRestriction( String groupName, Restriction restriction ){
        logger.info( "MongoDataLoad.createGroupRestriction()" )

        List<Restriction> restrictionList = Lists.newArrayList( restriction )

        if (!checkGroupNameExistenceInDb( groupName ) ) {
            logger.info( "Group Name: ${groupName} does not exist in database.  Creating new group restriction." )

            GroupRestrictions groupRestrictions = new GroupRestrictions()

            groupRestrictions.groupName    = groupName
            groupRestrictions.restrictions = restrictionList

            return groupRestrictions

        } else {
            logger.info( "Group Name: ${groupName} exists in database.  Adding additional restriction." )

            def groupRestrictionsObject = dependencyDao.getGroupRestrictions( groupName )
            GroupRestrictions groupRestrictions = GenericStructUtil.convertGroup(groupRestrictionsObject)

            /* if it's not there add the entire list, else just add to the list */
            if ( !groupRestrictions.restrictions ){

                logger.severe( "No restrictions for : ${groupName}" )
                logger.severe( "I really was expecting one!" )
                logger.severe( "Why would there be a group but no restrictions?")

                groupRestrictions.restrictions = restrictionList

            }else {
                logger.info( " Group Restriction contains an permitted Consumers List. Adding to it." )

                groupRestrictions.restrictions.add(restriction)

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

            def ( g, a, h, l, t, e, m ) = line.split( "\\|")

            /* clean up the variables we just received from the split and name them properly */
            String groupName                = g.replaceAll( "\\s", "" )
            String artifactId               = a.replaceAll( "\\s", "" )
            String versionHigh              = h.replaceAll( "\\s", "" )
            String versionLow               = l.replaceAll( "\\s", "" )
            String type                     = t.replaceAll( "\\s", "" )
            List<String> exemptConsumers    = e.replaceAll( "\\s", "" ).split( "\\," )
            String message                  = m.replaceAll( "^\\s", "")

            /* create a restriction to use */
            if ( artifactId.contains("null") ){
                logger.info("artifactId found to be a string null.")
                artifactId = null
            }

            if ( versionHigh.contains("null") ){
                logger.info("versionHigh found to be a string null.")
                versionHigh = null
            }

            if ( versionLow.contains("null") ){
                logger.info("versionLow found to be a string null.")
                versionLow = null
            }

            if ( e.contains("null") ) {
                logger.info("exemptConsumers found to be a string null.")
                exemptConsumers = null
            }

            if ( message.contains("null") ){
                logger.info("message found to be a string null.")
                message = null
            }
            Restriction restriction = createRestriction( type, artifactId, versionLow, versionHigh, message, exemptConsumers )

            logger.info("========================================================")
            logger.info("restriction created with values:")
            logger.info("type:             ${restriction.type}")
            logger.info("artifactId:       ${restriction.artifactId}")
            logger.info("versionLow:       ${restriction.versionLow}")
            logger.info("versionHigh:      ${restriction.versionHigh}")
            logger.info("message:          ${restriction.message}")
            logger.info("exemptConsumers:  ${restriction.exemptConsumers}")
            logger.info("========================================================")

            GroupRestrictions groupRestrictions = createGroupRestriction(groupName, restriction)

            dependencyDao.upsertGroupRestrictions(groupRestrictions)
        }
    }
}