// see http://blog.paulopoiati.com/2010/06/20/gmongo-0-5-released/

package com.rjturek.amp.govna.db

import com.gmongo.GMongo

class MongoData {

    GMongo mongo 
    def db

    // ctor
    def MongoData() {
        mongo = new GMongo("localhost", 27017)
        db = mongo.getDB("govna")
    }

    def Object getGroups() {

        // Insert a document
        //db.mnemonic.insert([_id: 'nne'])

        // Finding the first document
        return db.groups.find(_id: 'com.trp.amp.app')
    }
}
