package com.rjturek.amp.govna.service

import javax.ws.rs.ApplicationPath
import javax.ws.rs.core.Application

@ApplicationPath("/api")
class JaxRsApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        println "MUTHA FUCKERRRRRRRRR"
        return new HashSet<Class<?>>(Arrays.asList(
                com.rjturek.amp.govna.service.Consumers.class,
                com.rjturek.amp.govna.service.Providers.class));
    }

}
