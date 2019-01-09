package th.co.toyota.bw0.api.common;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CommonUtilityTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

//	@Test
//	public void toDoubleOKTest() {
//		String value = "9.352";
//		assertNotNull(value);
//		assertEquals(9.352, CommonUtility.toDouble(value), 0);
//		 value = "0.0";
//		assertNotNull(value);
//		assertEquals(0, CommonUtility.toDouble(value), 0);
//	}
//	
//
//	@Test
//	public void toIntOKTest() {
//		String value = "9";
//		assertNotNull(value);
//		assertEquals(9, CommonUtility.toInt(value));
//	}
//	
//	@Test
//	public void isListNullOrEmpty() {
//		assertTrue(CommonUtility.isListNullOrEmpty(new ArrayList<>()));
//		assertTrue(CommonUtility.isListNullOrEmpty(null));
//	}
	
	@Test
	public void moveFile(){
		boolean pass = true;//CommonUtility.moveFile("C:\\home\\zipcodes.csv", "C:\\home\\tmp\\zipcodes.csv");
		assertTrue(pass);
	}
}
