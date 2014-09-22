package com.rjturek.amp.govna.service

import javax.ws.rs.ApplicationPath
import javax.ws.rs.core.Application

@ApplicationPath("/rest-prefix")
class JaxRsApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        return new HashSet<Class<?>>(Arrays.asList(
                com.rjturek.amp.govna.service.Consumers.class,
                com.rjturek.amp.govna.service.Consumers.class));
    }

}
