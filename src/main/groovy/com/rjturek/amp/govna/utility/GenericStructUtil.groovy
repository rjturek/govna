package com.rjturek.amp.govna.utility

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.rjturek.amp.govna.dataobj.GroupRestrictions
import groovy.json.JsonBuilder

import java.lang.reflect.Type
import java.util.logging.Level
import java.util.logging.Logger

class GenericStructUtil {
    static Logger logger = Logger.getLogger("sharedLogger")

    static def log(msg) {
        logger.log(Level.FINE, msg)
    }

    // It's a shame that we have to turn the generic structure (list of map of lists of maps)
    // into JSON, just to marshall it into our real structures.  Not too bad, I suppose.
    static List<GroupRestrictions> convertGroupsList(groups) {
        logger.fine( "GenericStructUtil.convertGroupsList() ")

        JsonBuilder jsonBuilder = new JsonBuilder(groups)
        def theJson = jsonBuilder.toPrettyString()

        def gson
        gson = new GsonBuilder().create()

        Type collectionType = new TypeToken<Collection<GroupRestrictions>>(){}.getType()

        List<GroupRestrictions> groupRestrictionsList = gson.fromJson(theJson, collectionType)

        return groupRestrictionsList
    }

    static GroupRestrictions convertGroup(group) {
        logger.info( "convertGroup" )

        JsonBuilder jsonBuilder = new JsonBuilder(group)
        def theJson = jsonBuilder.toString()

        Gson gson = new GsonBuilder().create()

        GroupRestrictions groupRestrictions = gson.fromJson(theJson, GroupRestrictions.class)

        return groupRestrictions
    }
}
