package de.toberkoe.tools.pluto.common;

import org.junit.jupiter.api.Test;

import java.util.Optional;

class MoreAssertionsTest {

    @Test
    void testOptionalAssert() {
        MoreAssertions.assertThat(Optional.empty()).isEmpty();
    }

}