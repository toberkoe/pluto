package de.toberkoe.pluto.extensions.internal;

import java.util.Optional;

public class Throwables {

    public static <T extends Throwable> Optional<T> extractCause(Throwable exception, Class<?> causeClass) {
        if (exception == null) {
            return Optional.empty();
        } else if (causeClass.isInstance(exception)) {
            return Optional.ofNullable((T) exception);
        } else {
            return extractCause(exception.getCause(), causeClass);
        }
    }

}
