package de.toberkoe.tools.pluto.common;

import org.fest.assertions.api.*;

import java.math.BigDecimal;
import java.util.Optional;

class AssertConverter<T> {

    private final T value;

    private AssertConverter(T value) {
        this.value = value;
    }

    public static <T> AssertConverter<T> ofOptional(Optional<T> optional) {
        MoreAssertions.assertThat(optional)
                .isNotNull()
                .isNotEmpty();

        return optional.map(AssertConverter::new).orElseGet(() -> new AssertConverter<>(null));
    }

    public StringAssert asStringAssert() {
        Assertions.assertThat(value).isInstanceOf(String.class);
        return Assertions.assertThat((String) value);
    }

    public LongAssert asLongAssert() {
        Assertions.assertThat(value).isInstanceOf(Long.class);
        return Assertions.assertThat((Long) value);
    }

    public DoubleAssert asDoubleAssert() {
        Assertions.assertThat(value).isInstanceOf(Double.class);
        return Assertions.assertThat((Double) value);
    }

    public BigDecimalAssert asBigDecimalAssert() {
        Assertions.assertThat(value).isInstanceOf(BigDecimal.class);
        return Assertions.assertThat((BigDecimal) value);
    }

    public ThrowableAssert asThrowableAssert() {
        Assertions.assertThat(value).isInstanceOf(Throwable.class);
        return Assertions.assertThat((Throwable) value);
    }

    public BooleanAssert asBooleanAssert() {
        Assertions.assertThat(value).isInstanceOf(Boolean.class);
        return Assertions.assertThat((Boolean) value);
    }

    public ObjectAssert<T> asObjectAssert() {
        return Assertions.assertThat(value);
    }

}
