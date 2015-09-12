package com.stolser.user;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JUnitShouldWorkTest {
	private static final Logger logger = LoggerFactory.getLogger(LoginBean.class);

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		logger.trace("setUpBeforeClass()...");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		logger.trace("tearDownAfterClass()...");
	}

	@Before
	public void setUp() throws Exception {
		logger.trace("setUp()...");
	}

	@After
	public void tearDown() throws Exception {
		logger.trace("tearDown()...");
	}

	@Test
	public void test() {
		//setup
		Object object = null;
		//Object object = new Object;
		//execute
		//assert
		assertEquals("Hello World", "Hello World");
		assertNull("Message for assertNull.", object);
	}

}
