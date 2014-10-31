package com.rjturek.amp.govnaclient.httpclient

import java.util.logging.Logger
import groovyx.net.http.HTTPBuilder
import groovy.json.internal.LazyMap
import static groovyx.net.http.Method.POST
import static groovyx.net.http.ContentType.JSON

/**
 * Created by ckell on 10/31/14.
 */
class GovnaClient {

    static Logger log = Logger.getLogger(this.class.name)
    def govnaUrl = "http://localhost:8080"

    def void displayResults(LazyMap json){

        if (json.validationResponseElements.size() > 0){
            json.validationResponseElements.each { element ->
                def type = "Deprecated"
                if (element.type == "P") {
                    type = "Prohibited"
                }

                if (type == "Prohibited") {
                    log.severe("***************************************************************************")
                    log.severe("*  A dependency has been found to be Prohibited for you Application.")
                    log.severe("*")
                    log.severe("*  Prohibited dependency:  ${element.dependency}")
                    log.severe("*  Prohibiton message:     ${element.message}")
                    log.severe("*")
                    log.severe("*  The prohibiton rules can be found: ${govnaUrl}")
                    log.severe("***************************************************************************")
                }else{
                    log.warning("**************************************************************************")
                    log.warning("*  A dependency for has been found to be Deprecated for you Application.")
                    log.warning("*")
                    log.warning("*  Deprecated Dependency:    ${element.dependency}")
                    log.warning("*  Deprecation Message:      ${element.message}")
                    log.warning("*")
                    log.warning("*  The deprecation rules can be found: ${govnaUrl}")
                    log.warning("**************************************************************************")
                }
            }
        }
    }

    static void main(args) {
        log.info("Starting Govna Client.")

        GovnaClient govnaClient = new GovnaClient()

        def server = "http://localhost:8080/api/validation/buildValidation"

        HTTPBuilder httpBuilder = new HTTPBuilder(server)

        try {
            httpBuilder.request(POST, JSON) {
                body = [
                        "consumerGroup" : "com.trp.sec.scanner",
                        "dependencyCoordinates": [
                                "log4j:log4j:1.2.17",
                                "com.trp.amp.afutil:AMPafutilUTIL:1.5.10",
                                "org.ow2.util.scan:wifiutil:3.0.2.",
                                "org.jdom:jdom:1.1.1"
                        ]
                ]

                response.success = { resp, json ->
                    log.info("Recieved success response from: ${server}")
                    if(json.failBuild){
                        log.info("build validation failed.")
                        govnaClient.displayResults(json)

                    }else{
                        log.info("build validation passed.")
                        govnaClient.displayResults(json)
                    }
                }

                response.failure = { resp ->
                    log.severe("Failed response from: ${server}")
                }
            }
        } catch (ConnectException e){
            log.severe(e.message)
        }
    }
}
