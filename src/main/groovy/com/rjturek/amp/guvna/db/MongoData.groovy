//http://blog.paulopoiati.com/2010/06/20/gmongo-0-5-released/

package com.rjturek.amp.guvna.db

import com.gmongo.GMongo

class MongoData {

    GMongo mongo 
    def db

    // ctor
    def MongoData() {
        mongo = new GMongo("fedora", 27017)
        db = mongo.getDB("guvna")
    }


    def Object getMne(String mne) {

        // Collections can be accessed as a db property (like the javascript API)
        // assert db.mmnemonic instanceof com.mongodb.DBCollection
        // They also can be accessed with array notation 
        // assert db['mnemonic'] instanceof com.mongodb.DBCollection

        // Insert a document
        //db.mnemonic.insert([_id: 'nne'])

        // Finding the first document
        return db.mnemonic.find([_id: mne])
    }



}
