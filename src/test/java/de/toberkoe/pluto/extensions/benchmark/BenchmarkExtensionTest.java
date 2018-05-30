package de.toberkoe.pluto.extensions.benchmark;

import org.junit.jupiter.api.RepeatedTest;

@BenchmarkTest
class BenchmarkExtensionTest {

	@RepeatedTest(10)
	void test1() throws Exception {
		Thread.sleep(100);
	}

	@RepeatedTest(100)
	void test2() throws Exception {
		Thread.sleep(10);
	}

}