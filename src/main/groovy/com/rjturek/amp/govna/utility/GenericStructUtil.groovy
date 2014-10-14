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

    static List<GroupRestrictions> convertGroups(groups) {
        JsonBuilder jsonBuilder = new JsonBuilder(groups)
        def theJson = jsonBuilder.toString()
        log(theJson)
        Gson gson = new GsonBuilder().create()
        Type collectionType = new TypeToken<Collection<GroupRestrictions>>(){}.getType();
        List<GroupRestrictions> groupRestrictionsList = gson.fromJson(theJson, collectionType)
        return groupRestrictionsList
    }

}
