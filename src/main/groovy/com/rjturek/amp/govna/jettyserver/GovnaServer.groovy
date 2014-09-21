package com.rjturek.amp.govna.jettyserver

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.webapp.WebAppContext

class GovnaServer {

    public static void main(String[] args) throws Exception {

        println "..... Starting Govna Server"

        Server server = new Server(8080)

        WebAppContext context = new WebAppContext()
        context.setDescriptor("src/main/webapp/WEB-INF/web.xml")
        context.setResourceBase("src/main/webapp")
        context.setContextPath("/")
        context.setParentLoaderPriority(true)

        server.setHandler(context)

        println new File(context.descriptor).getAbsolutePath()
        println new File(".").getAbsolutePath()

        server.start();
        server.join();
    }
}