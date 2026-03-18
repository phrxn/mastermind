package com.quazzom.mastermind.unit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

import com.quazzom.mastermind.Application;

class ApplicationTests {

	@Test
	void applicationClassShouldBeInstantiable() {
		assertDoesNotThrow(Application::new);
	}

}
