package com.rjturek.amp.govnaclient.httpclient

import java.util.logging.Logger
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.Method.POST
import static groovyx.net.http.ContentType.JSON


/**
 * Created by ckell on 10/31/14.
 */
class GovnaClient {

       static Logger log = Logger.getLogger(this.class.name)

    static void main(args) {
        log.info("Starting Govna Client.")

        def server = "http://localhost:8080/api/validation/buildValidation"

        HTTPBuilder httpBuilder = new HTTPBuilder(server)

        try {
            httpBuilder.request(POST, JSON) {
                body = [
                        "consumerGroup" : "com.trp.sec.scanner",
                        "dependencyCoordinates": [
                                "log4j:log4j:1.2.17",
                                "com.trp.amp.afutil:AMPafutilUTIL:1.5.10",
                                "org.ow2.util.scan:wifiutil:2.0.2.",
                                "org.jdom:jdom:1.1.1"
                        ]
                ]

                response.success = { resp, json ->
                    log.info("Recieved success response from: ${server}")
                    if(json.failBuild){
                        log.info("build validation failed.")
                        if (json.validationResponseElements.size() > 0){
                            json.validationResponseElements.each{ element ->
                                log.severe("${element.dependency}")
                                log.severe("${element.type}")
                                log.severe("${element.message}")
                            }
                        }

                    }else{
                        log.info("build validation passed.")
                        if (json.validationResponseElements.size() > 0){
                            json.validationResponseElements.each{ element ->
                                log.warning("${element.dependency}")
                                log.warning("${element.type}")
                                log.warning("${element.message}")
                            }
                        }
                    }
                }

                response.failure = { resp ->
                    log.severe("Failed response from: ${server}")
                }
            }
        } catch (java.net.ConnectException e){
            log.severe(${e.message})
        }
    }
}
