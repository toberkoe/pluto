package de.toberkoe.pluto.extensions.integration.persistence.config.discovery;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface EntityClassProvider {

    String forPersistenceUnit() default "";

}
