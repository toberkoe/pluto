package de.toberkoe.tools.pluto.common;

import org.fest.assertions.api.*;

import java.util.Optional;

import static java.lang.String.format;

/**
 * Fluent assertions for {@link Optional}
 *
 * @param <T> type of optional value
 */
public class OptionalAssert<T> extends AbstractAssert<OptionalAssert<T>, Optional<T>> {

    OptionalAssert(Optional<T> actual) {
        super(actual, OptionalAssert.class);
    }

    public OptionalAssert<T> isEmpty() {
        isNotNull();

        if (actual.isPresent()) {
            throw new AssertionError(format("Expected Optional to be empty but was <%s>", actual));
        }
        return this;
    }

    public OptionalAssert<T> isNotEmpty() {
        isNotNull();

        if (!actual.isPresent()) {
            throw new AssertionError("Expected Optional to be present but was empty");
        }
        return this;
    }

    public OptionalAssert<T> isAbsent() {
        return isEmpty();
    }

    public OptionalAssert<T> isPresent() {
        return isNotEmpty();
    }

    public StringAssert mapToString() {
        return AssertConverter.ofOptional(actual).asStringAssert();
    }

    public LongAssert mapToLong() {
        return AssertConverter.ofOptional(actual).asLongAssert();
    }

    public DoubleAssert mapToDouble() {
        return AssertConverter.ofOptional(actual).asDoubleAssert();
    }

    public BigDecimalAssert mapToBigDecimal() {
        return AssertConverter.ofOptional(actual).asBigDecimalAssert();
    }

    public ThrowableAssert mapToThrowable() {
        return AssertConverter.ofOptional(actual).asThrowableAssert();
    }

    public BooleanAssert mapToBoolean() {
        return AssertConverter.ofOptional(actual).asBooleanAssert();
    }

    public ObjectAssert<T> mapToObject() {
        return AssertConverter.ofOptional(actual).asObjectAssert();
    }

}
