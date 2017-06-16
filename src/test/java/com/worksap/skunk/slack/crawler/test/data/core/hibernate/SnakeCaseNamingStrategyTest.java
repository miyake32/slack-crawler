package com.worksap.skunk.slack.crawler.test.data.core.hibernate;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;

import org.junit.Test;

import skunk.slack.crawler.data.core.hibernate.SnakeCaseNamingStrategy;

public class SnakeCaseNamingStrategyTest {
	@Test
	public void startWithUpperCase() {
		String camelCase = "ThisIsTestCamelCaseStringStartWithUpperCase";
		String snakeCase = toSnakeCase(camelCase);
		assertEquals("this_is_test_camel_case_string_start_with_upper_case", snakeCase);
	}

	@Test
	public void startWithLowerCase() {
		String camelCase = "thisIsTestCamelCaseStringStartWithLowerCase";
		String snakeCase = toSnakeCase(camelCase);
		assertEquals("this_is_test_camel_case_string_start_with_lower_case", snakeCase);
	}

	@Test
	public void continuousUpperCaseAtStart() {
		String camelCase = "IRStandsForInfraRed";
		String snakeCase = toSnakeCase(camelCase);
		assertEquals("ir_stands_for_infra_red", snakeCase);
	}

	@Test
	public void continuousUpperCaseInMiddle() {
		String camelCase = "compatibleWithIEEEFormat";
		String snakeCase = toSnakeCase(camelCase);
		assertEquals("compatible_with_ieee_format", snakeCase);
	}

	@Test
	public void continuousUpperCaseAtEnd() {
		String camelCase = "fetchFromNASA";
		String snakeCase = toSnakeCase(camelCase);
		assertEquals("fetch_from_nasa", snakeCase);
	}

	@Test
	public void numberAtStart1() {
		String camelCase = "1stStep";
		String snakeCase = toSnakeCase(camelCase);
		assertEquals("1st_step", snakeCase);
	}

	@Test
	public void numberAtStart2() {
		String camelCase = "123Step";
		String snakeCase = toSnakeCase(camelCase);
		assertEquals("123_step", snakeCase);
	}

	@Test
	public void numberInMiddle1() {
		String camelCase = "mid12thCentury";
		String snakeCase = toSnakeCase(camelCase);
		assertEquals("mid_12th_century", snakeCase);
	}

	@Test
	public void numberInMiddle2() {
		String camelCase = "during1212AtJapan";
		String snakeCase = toSnakeCase(camelCase);
		assertEquals("during_1212_at_japan", snakeCase);
	}
	
	@Test
	public void numberAtEnd() {
		String camelCase = "oneIsSameAs1";
		String snakeCase = toSnakeCase(camelCase);
		assertEquals("one_is_same_as_1", snakeCase);
	}

	private String toSnakeCase(String camelCase) {
		Method method = null;
		try {
			method = SnakeCaseNamingStrategy.class.getDeclaredMethod("toSnakeCase", String.class);
			method.setAccessible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			return (String) method.invoke(new SnakeCaseNamingStrategy(), camelCase);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
