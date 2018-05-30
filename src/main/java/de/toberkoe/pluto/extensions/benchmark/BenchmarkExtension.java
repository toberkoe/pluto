package de.toberkoe.pluto.extensions.benchmark;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashSet;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;

public class BenchmarkExtension implements Extension, BeforeAllCallback, BeforeEachCallback, AfterEachCallback, AfterAllCallback {

	private static final Map<String, Set<MeasureUnit>> testTimes = new ConcurrentHashMap<>();


	@Override
	public void beforeAll(ExtensionContext context) throws Exception {
		testTimes.clear();
	}

	@Override
	public void beforeEach(ExtensionContext context) throws Exception {
		Optional<String> methodName = context.getTestMethod().map(Method::getName);
		methodName.ifPresent(name -> {
			Set<MeasureUnit> units = testTimes.getOrDefault(name, new HashSet<>());
			units.add(MeasureUnit.start(context.getUniqueId()));
			testTimes.put(name, units);
		});
	}

	@Override
	public void afterEach(ExtensionContext context) throws Exception {
		String uniqueId = context.getUniqueId();

		Optional<String> methodName = context.getTestMethod().map(Method::getName);
		methodName.ifPresent(name -> {
			Set<MeasureUnit> units = testTimes.getOrDefault(name, new HashSet<>());
			MeasureUnit unit = units.stream()
					.filter(u -> u.getUniqueId().equals(uniqueId))
					.findAny().orElse(MeasureUnit.start(uniqueId));
			units.add(unit.stop());
			testTimes.put(name, units);
		});
	}

	@Override
	public void afterAll(ExtensionContext context) throws Exception {
		testTimes.keySet().stream().sorted().forEach(testMethod -> {
			LongSummaryStatistics statistics = testTimes.get(testMethod).stream()
					.map(MeasureUnit::getDuration)
					.mapToLong(Duration::toMillis)
					.summaryStatistics();
			String message = "%s: %d repetitions [avg = %f ms, min = %d ms, max = %d ms, total = %d ms]";
			System.out.println(String.format(message, testMethod, statistics.getCount(), statistics.getAverage(),
					statistics.getMin(), statistics.getMax(), statistics.getSum()));
		});
	}
}
