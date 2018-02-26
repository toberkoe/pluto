package de.toberkoe.pluto.extensions.integration.persistence.config.log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Log {

    Level value() default Level.OFF;

    enum Level {
        OFF, FATAL, ERROR, WARN, INFO, DEBUG, TRACE
    }


}
