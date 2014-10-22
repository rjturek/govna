// see http://blog.paulopoiati.com/2010/06/20/gmongo-0-5-released/

package com.rjturek.amp.govna.db
import com.gmongo.GMongo
import com.rjturek.amp.govna.dataobj.GroupRestrictions
import com.rjturek.amp.govna.utility.GenericStructUtil
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

import java.util.logging.Level
import java.util.logging.Logger

class DependencyDao {

    static Logger logger = Logger.getLogger("sharedLogger")

    def log(msg) {
        logger.log(Level.FINE, msg)
    }

    GMongo mongo 
    def db
    def rstrColl

    // ctor
    def DependencyDao() {
        mongo = new GMongo("localhost", 27017)
        db = mongo.getDB("govna")
        rstrColl = db.getCollection("restrictions")
    }

    /**
     * query for all GroupRestrictions in the Database
     *
     * @return List<GroupRestrictions>
     *
     */
    def List<GroupRestrictions> getGroupRestrictionsList() {
        logger.fine("DependencyDao.getGroupRestrictionsList()")

        def groups = rstrColl.find()

        List<GroupRestrictions> groupRestrictionsList = GenericStructUtil.convertGroupsList(groups)

        return groupRestrictionsList
    }

    /**
     * Read entire database of group restrictions and places them into a map.
     *
     * @return Map (<groupName>,<GroupRestrictions>)
     */
    def Map<String,GroupRestrictions> getAllGroupRestrictionsMap(){
        logger.fine( "DependencyDao.getAllGroupRestrictionsMap()" )

        def groups = getGroupRestrictionsList()
        List<GroupRestrictions> groupRestrictionsList = GenericStructUtil.convertGroupsList(groups)

        Map groupRestrictionsMap = [:]

        /* create the map with no duplicate groupNames */
        groupRestrictionsList.each{
            /* db integrity check. groupNames are unique. */
            if (!groupRestrictionsMap.put(it.groupName, it ) == null){
                throw new Exception("GroupName: ${it.groupName} exists in Map.  More than 1 groupName returned from DB.")
            }
        }

        groupRestrictionsMap
    }

    def Object getGroupRestrictions(groupName) {
        log("getGroupRestrictions")
        def groupCursor = rstrColl.find(groupName: groupName)
        if (!groupCursor.hasNext()) {
            return null
        }
        def group = groupCursor.next()

        // group is the unique key, there should never be more than one found.
        assert !groupCursor.hasNext()

        return group
    }

    boolean groupRestrictionsExist(groupName) {
        log("groupRestrictionsExist")
        int count = rstrColl.count(groupName: groupName)
        log("the count is: $count")
        if (count > 1) {
            throw new Exception(
                 "Invariant violated: groupName found in the restrictions collection more than once. groupName: $groupName")
        }
        return count
    }

// Probably not needed due to upsert capability - below
//    def Object insertGroupRestrictions(group) {
//        log("insertGroupRestrictions")
//        JsonBuilder jsonBuilder = new JsonBuilder(group)
//        def theJson = jsonBuilder.toPrettyString()
//        log(theJson)
//        rstrColl.insert(new JsonSlurper().parseText(theJson))
//    }

    def Object upsertGroupRestrictions(GroupRestrictions group)  {
        log("updateGroupRestrictions")
        JsonBuilder jsonBuilder = new JsonBuilder(group)
        def theJson = jsonBuilder.toPrettyString()
        log(theJson)
        def genericStructure = new JsonSlurper().parseText(theJson)
        log(genericStructure.toString())
        rstrColl.update([groupName: group.groupName], [$set: new JsonSlurper().parseText(theJson)], true)
    }

    def Object removeGroupRestrictions(String groupName) {
        logger.info("removeGroupRestrictions()")

        logger.info( "removing group name: $groupName}")
        def result = rstrColl.remove([groupName: groupName])
        logger.info("Document Removal results: ${result}")
    }

    def Object wipeRestrictionsDocuments(){
        logger.info("wipeRestrictionsDocuments()")
        logger.warning("Wiping all documents in the restrictions collection.")

        def result = rstrColl.remove([:])

        logger.info("Restrictions collection document removal results: ${result}")
    }
}
