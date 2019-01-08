package th.co.toyota.bw0.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import th.co.toyota.bw0.api.config.AppTestConfig;
import th.co.toyota.bw0.util.JsonStringToObjectConversionException;
import th.co.toyota.bw0.util.JsonStringToObjectConverter;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppTestConfig.class)
public class TBW0JsontStringToObjectConverter {

	@Autowired
	private JsonStringToObjectConverter converter;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testConverJsonStringToObject() {
		try {
			Object object = converter
					.converJsonStringToObject(
							"th.co.toyota.bw0.util.ObjectDetailsTest",
							"{\"pvNo\":\"10\", \"revision\":\"111\", \"company\":\"Toyota\", \"employee\":\" EmployeeOne\", \"employeeCD\":\"1234\",\"coaId\":\"23232323\", \"coaYearMonth\":\"201505\" }");
			assertNotNull(object);
			assertTrue(object instanceof ObjectDetailsTest);
			ObjectDetailsTest testObject = (ObjectDetailsTest) object;
			assertEquals("10", testObject.getPvNo());
			assertEquals("Toyota", testObject.getCompany());
		} catch (JsonStringToObjectConversionException e) {
			fail("Test case failed due to JsonStringToObjectConversionException");
		}
	}

	@Test
	public void testConverJsonStringToObjectNullCheckOnClassName() {
		try {
			converter
					.converJsonStringToObject(
							null,
							"{\"pvNo\":\"10\", \"revision\":\"111\", \"company\":\"Toyota\", \"employee\":\" EmployeeOne\", \"employeeCD\":\"1234\",\"coaId\":\"23232323\", \"coaYearMonth\":\"201505\" }");
			fail("JsonStringToObjectConversionException was expected here");
		} catch (JsonStringToObjectConversionException e) {
			// nothing to do here as JsonStringToObjectConversionException is
			// expected
		}
	}

	@Test
	public void testConverJsonStringToObjectNullCheckOnString() {
		try {
			converter.converJsonStringToObject(
					"th.co.toyota.bw0.util.ObjectDetailsTest", null);
			fail("JsonStringToObjectConversionException was expected here");
		} catch (JsonStringToObjectConversionException e) {
			// nothing to do here as JsonStringToObjectConversionException is
			// expected
		}
	}

	@Test
	public void testConverJsonStringToObjectNonJSONString() {
		try {
			Object object = converter.converJsonStringToObject(
					"th.co.toyota.bw0.util.ObjectDetailsTest", "");
			assertNull(object);
		} catch (JsonStringToObjectConversionException e) {
			fail("Test case failed due to JsonStringToObjectConversionException");
		}
	}

}
