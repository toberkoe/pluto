package de.toberkoe.tools.pluto.internal;

import java.util.Optional;

/**
 * Utility class for throwables
 *
 * @author t.bertram-koehler
 */
public class Throwables {

    /**
     * Extracts the requested cause from given {@link Throwable}.
     *
     * @param exception  the complete exception stack
     * @param causeClass the requested cause to search for
     * @param <T>        anything that extends {@link Throwable}
     * @return {@link Optional} of found cause or empty
     */
    public static <T extends Throwable> Optional<T> extractCause(Throwable exception, Class<T> causeClass) {
        if (exception == null) {
            return Optional.empty();
        } else if (causeClass.isInstance(exception)) {
            return Optional.of((T) exception);
        } else {
            return extractCause(exception.getCause(), causeClass);
        }
    }
}
