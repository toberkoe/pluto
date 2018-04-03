package de.toberkoe.pluto.extensions.integration.persistence.config.log;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.Optional;

public class LogConfig {

    public static void configure(Log annotation) {
        Log.Level logLevel = Optional.ofNullable(annotation).map(Log::value).orElse(Log.Level.ERROR);
        Level level = Level.toLevel(logLevel.name());
        configure(level);
    }

    private static void configure(Level logLevel) {
        Logger.getLogger("org.hibernate").setLevel(logLevel);
        Logger.getLogger("org.hibernate.SQL").setLevel(logLevel);
        Logger.getLogger("org.jboss.logging").setLevel(logLevel);
        Logger.getLogger("de.pluto.config").setLevel(logLevel);
    }
}
