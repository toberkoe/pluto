package de.toberkoe.pluto.extensions.benchmark;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.Objects;

public final class MeasureUnit implements Comparable<MeasureUnit> {

	private final String uniqueId;
	private final Instant start;
	private Duration duration;

	private MeasureUnit(String uniqueId) {
		this.uniqueId = uniqueId;
		this.start = Instant.now();
	}

	public static MeasureUnit start(String uniqueId) {
		return new MeasureUnit(uniqueId);
	}

	public MeasureUnit stop() {
		duration = Duration.between(start, Instant.now());
		return this;
	}

	public Duration getDuration() {
		return duration;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		MeasureUnit unit = (MeasureUnit) o;
		return Objects.equals(uniqueId, unit.uniqueId);
	}

	@Override
	public int hashCode() {

		return Objects.hash(uniqueId);
	}

	@Override
	public int compareTo(MeasureUnit o) {
		return Objects.compare(this, o, Comparator.comparing(MeasureUnit::getUniqueId));
	}
}
