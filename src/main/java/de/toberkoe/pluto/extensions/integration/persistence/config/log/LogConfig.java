package de.toberkoe.pluto.extensions.integration.persistence.config.log;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class LogConfig {

    public static void configure(Log annotation) {
        Level logLevel = Level.ERROR;
        if (annotation != null) {
            switch (annotation.value()) {
                case OFF:
                    logLevel = Level.OFF;
                    break;
                case FATAL:
                    logLevel = Level.FATAL;
                    break;
                case ERROR:
                    logLevel = Level.ERROR;
                    break;
                case WARN:
                    logLevel = Level.WARN;
                    break;
                case INFO:
                    logLevel = Level.INFO;
                    break;
                case DEBUG:
                    logLevel = Level.DEBUG;
                    break;
                case TRACE:
                    logLevel = Level.TRACE;
                    break;
            }
        }
        configure(logLevel);
    }

    private static void configure(Level logLevel) {
        Logger.getLogger("org.hibernate").setLevel(logLevel);
        Logger.getLogger("org.hibernate.SQL").setLevel(logLevel);
        Logger.getLogger("org.jboss.logging").setLevel(logLevel);
        Logger.getLogger("de.pluto.config").setLevel(logLevel);
    }
}
