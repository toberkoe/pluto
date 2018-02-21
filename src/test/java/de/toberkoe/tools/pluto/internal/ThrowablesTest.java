package de.toberkoe.tools.pluto.internal;

import de.toberkoe.tools.pluto.common.MoreAssertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

class ThrowablesTest {

    @Test
    void testIsEmpty() {
        Optional<RuntimeException> optional = Throwables.extractThrowable(null, RuntimeException.class);
        MoreAssertions.assertThat(optional).isEmpty();
    }

    @Test
    void testIsInstance() {
        Optional<RuntimeException> optional = Throwables.extractThrowable(new RuntimeException(), RuntimeException.class);
        MoreAssertions.assertThat(optional)
                .isNotEmpty()
                .mapToThrowable()
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void testIsInCause() {
        RuntimeException exception = new RuntimeException(new NullPointerException());
        Optional<NullPointerException> optional = Throwables.extractThrowable(exception, NullPointerException.class);
        MoreAssertions.assertThat(optional)
                .isNotEmpty()
                .mapToThrowable()
                .isInstanceOf(NullPointerException.class);
    }

}