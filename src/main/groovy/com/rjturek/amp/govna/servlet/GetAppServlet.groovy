package com.rjturek.amp.govna.servlet

import javax.servlet.ServletConfig;
import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.ws.rs.Path
import java.io.IOException;

public class GetAppServlet extends HttpServlet {

    @Override
    public void init(ServletConfig config) throws ServletException {
        println "SERVLET INIT....................."
        config.initParameterNames.each {
            println "servlet init parms key: ${it}  val: ${config.getInitParameter(it)}"
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        throw new ServletException("Do not use POST on GetAppServlet")
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        response.outputStream << "Here, we will set up the user session and serve up the UI"
    }

}



