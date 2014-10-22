package com.rjturek.amp.govna.utility

import java.util.logging.LogRecord
import java.util.logging.Formatter


/**
 * Created by ckell on 10/9/14.
 */
public class CustomLogFormatter  extends Formatter  {

    public String format(LogRecord log) {
        //Date date = new Date(log.getMillis())
        String level = log.getLevel().getName()
        //String message = "[custom] " + " " + date.toString() + " " + level.toUpperCase() + " "
        //String message = "[custom] " + level.toUpperCase() + " " + log.getSourceClassName() + " "
        String message = "[custom] " + level.toUpperCase() + " "
        message = message + log.getMessage() + "\n";

        Throwable thrown = log.getThrown();
        if (thrown != null) {
            message = message + thrown.toString();
        }
        return message;
    }
}
