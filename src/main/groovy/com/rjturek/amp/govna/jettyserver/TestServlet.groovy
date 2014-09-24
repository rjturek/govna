package com.rjturek.amp.govna.jettyserver

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class TestServlet extends HttpServlet{
    public void doGet(HttpServletRequest req, HttpServletResponse resp){
        def ver = "version " +
                    req.session.servletContext.effectiveMajorVersion +
                    " " +
                    req.session.servletContext.effectiveMinorVersion
        resp.outputStream << ver
    }
}
