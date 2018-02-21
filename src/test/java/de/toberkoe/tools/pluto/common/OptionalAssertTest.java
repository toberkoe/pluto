package de.toberkoe.tools.pluto.common;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static de.toberkoe.tools.pluto.common.MoreAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OptionalAssertTest {

    @Test
    void successIfEmptyOptional() {
        assertThat(Optional.empty()).isAbsent();
    }

    @Test
    void failIfNotEmptyOptional() {
        assertThrows(AssertionError.class, () -> assertThat(Optional.of(1)).isAbsent());
    }

    @Test
    void successIfPresentOptional() {
        assertThat(Optional.of(1)).isPresent();
    }

    @Test
    void failIfAbsentOptional() {
        assertThrows(AssertionError.class, () -> assertThat(Optional.empty()).isPresent());
    }

    @Test
    void mapToString() {
        Optional<String> optional = Optional.of("text");
        assertThat(optional).mapToString().isNotEmpty();
    }

    @Test
    void mapToLong() {
        Optional<Long> optional = Optional.of(1L);
        assertThat(optional).mapToLong().isPositive();
    }

    @Test
    void mapToDouble() {
        Optional<Double> optional = Optional.of(1.0);
        assertThat(optional).mapToDouble().isPositive();
    }

    @Test
    void mapToBigDecimal() {
        Optional<BigDecimal> optional = Optional.of(BigDecimal.ONE);
        assertThat(optional).mapToBigDecimal().isPositive();
    }

    @Test
    void mapToThrowable() {
        Optional<Throwable> optional = Optional.of(new IllegalArgumentException());
        assertThat(optional).mapToThrowable().hasNoCause();
    }

    @Test
    void mapToBoolean() {
        Optional<Boolean> optional = Optional.of(true);
        assertThat(optional).mapToBoolean().isTrue();
    }

    @Test
    void mapToObject() {
        Optional<LocalDate> optional = Optional.of(LocalDate.now());
        assertThat(optional).mapToObject().isNotNull();
    }

}