package de.toberkoe.pluto.extensions.internal;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static de.toberkoe.fluentassertions.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ThrowablesTest {

    @Test
    void testCauseFound() {
        Throwable e = new IllegalArgumentException(new NullPointerException());
        Optional<NullPointerException> found = Throwables.extractCause(e, NullPointerException.class);
        assertThat(found).isNotEmpty();
        assertThat(found.get()).isInstanceOf(NullPointerException.class);
    }

    @Test
    void testNothingFound() {
        Optional<NullPointerException> found = Throwables.extractCause(new IllegalArgumentException(), NullPointerException.class);
        assertThat(found).isEmpty();
    }

    @Test
    void testCauseFoundInSecondLayer() {
        Throwable e = new RuntimeException(new IllegalArgumentException(new NullPointerException()));
        Optional<NullPointerException> found = Throwables.extractCause(e, NullPointerException.class);
        assertThat(found).isNotEmpty();
        assertThat(found.get()).isInstanceOf(NullPointerException.class);
    }

}