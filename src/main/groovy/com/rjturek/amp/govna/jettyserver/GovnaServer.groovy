package com.rjturek.amp.govna.jettyserver

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.webapp.WebAppContext

class GovnaServer {

    public static void main(String[] args) throws Exception {

        println "..... Starting Govna Server"

        Server server = new Server(8080);

        WebAppContext context = new WebAppContext();
//        context.setDescriptor("../WEB-INF/web.xml");
//        context.setResourceBase("../src/webapp");
//        context.setContextPath("/");
//        context.setParentLoaderPriority(true);



        server.setHandler(context);

        server.start();
        server.join();
    }
}