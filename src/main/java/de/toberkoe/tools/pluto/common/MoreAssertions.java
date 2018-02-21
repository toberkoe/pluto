package de.toberkoe.tools.pluto.common;

import org.fest.assertions.api.Assertions;

import java.util.Optional;

public class MoreAssertions extends Assertions {

    public static <T> OptionalAssert<T> assertThat(Optional<T> actual) {
        return new OptionalAssert<>(actual);
    }
}
