package th.co.toyota.bw0.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.time.Year;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FormatUtilTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void convertStringToDateAndDateToStringFormatTest() {
		String value = "12/11/2018";
		String stFormat = "dd/MM/yyyy";
		Date value2 = FormatUtil.convertStringToDate(value, stFormat);
		String result = FormatUtil.convertDateToString(value2, stFormat);
		assertEquals("Result",result , value);
		//assertNotNull("Result",result);
	}
	@Test
	public void convertBigDecimalToStringFormatTest() {
		BigDecimal value = new BigDecimal(100000);
		String result = FormatUtil.convertBigDecimalToString(value, 2 , true);
		assertEquals("Result",result , "100,000.00");
	}
	@Test
	public void convertStringToBigDecimalFormatTest() {
		BigDecimal value = new BigDecimal(100000);
		BigDecimal result = FormatUtil.convertStringToBigDecimal("100000");
		assertEquals("Result",result , value);
	}
	@Test
	public void convertStringToLongFormatTest() {
		Long value = new Long(100000);
		Long result = FormatUtil.convertStringToLong("100000");
		assertEquals("Result",result , value);
	}
	@Test
	public void convertStringToLongRoundUpFormatTest() {
		Long value = new Long(100000);
		Long result = FormatUtil.convertStringToLongRoundUp("100000");
		assertEquals("Result",result , value);
	}
	@Test
	public void validateBigDecimalFormatTest() {
		boolean result = FormatUtil.validateBigDecimal("100000","100000");
		assertEquals("Result",result , true);
	}
	@Test
	public void validateBigDecimalMinMaxFormatTest() {
		BigDecimal value = new BigDecimal(100000);
		boolean result = FormatUtil.validateBigDecimal(value ,"0","100000");
		assertEquals("Result",result , true);
	}
	@Test
	public void convertStringToDoubleFormatTest() {
		Double value = new Double(100000.0);
		Double result = FormatUtil.convertStringToDouble("100,000" ,6,0);
		assertEquals("Result",result , value);
	}
	@Test
	public void convertStringToBigDecimalMaxlenFormatTest() {
		BigDecimal value = new BigDecimal(100000);
		BigDecimal result = FormatUtil.convertStringToBigDecimal("100,000" ,6,0);
		assertEquals("Result",result , value);
	}
	@Test
	public void convertStringToCorrectNumberFormatTest() {
		String result = FormatUtil.convertStringToCorrectNumber("100,000" ,6,0,true);
		assertEquals("Result",result , "100,000");
	}
	@Test
	public void convertStringToDoubleMaxlenFormatTest() {
		Double value = new Double(100000.0);
		Double result = FormatUtil.convertStringToDouble("100,000" ,6,0,true);
		assertEquals("Result",result , value);
	}
	@Test
	public void trimValueFormatTest() {
		String result = FormatUtil.trimValue("test ",false);
		assertEquals("Result",result , "test");
	}
	@Test
	public void nullToZeroFormatTest() {
		String result = FormatUtil.nullToZero(null);
		assertEquals("Result",result , "0");
	}
	@Test
	public void isNullorZeroOrLessthanZeroFormatTest() {
		boolean result = FormatUtil.isNullorZeroOrLessthanZero(null);
		assertEquals("Result",result , true);
	}
	@Test
	public void isValidDateFormatTest() {
		String value = "12/11/2018";
		String stFormat = "dd/MM/yyyy";
		boolean result = FormatUtil.isValidDate(value,stFormat);
		assertEquals("Result",result , true);
	}
	@Test
	public void isValidDate2FormatTest() {
		String value = "12/11/2018";
		boolean result = FormatUtil.isValidDate(value);
		assertEquals("Result",result , false);
	}
	@Test
	public void isValidByPatternFormatTest() {
		String value = "12/11/2018";
		boolean result = FormatUtil.isValidByPattern("test","test");
		assertEquals("Result",result , true);
	}
	@Test
	public void compareDateTimeFormatTest() {
		int result = FormatUtil.compareDateTime(new Date(),new Date());
		assertEquals("Result",result , 0);
	}
	@Test
	public void compareDateFormatTest() {
		String value = "12/11/2018";
		int result = FormatUtil.compareDate(new Date(),new Date());
		assertEquals("Result",result , 0);
	}
	@Test
	public void getCurrentDateFormatTest() {
		Date value = new Date();
		Date result = FormatUtil.getCurrentDate();
		value.setHours(0);
		value.setSeconds(0);
		value.setMinutes(0);
		assertEquals("Result",result.toString() , value.toString());
	}
	@Test
	public void getCurrentMonthFormatTest() {
		Date value = new Date();
		Date result = FormatUtil.getCurrentMonth();
		value.setHours(0);
		value.setSeconds(0);
		value.setMinutes(0);
		value.setDate(1);
		assertEquals("Result",result.toString() , value.toString());
	}
	@Test
	public void getMaxDatyOfMonthFormatTest() {
		Date value = new Date();
		Date result = FormatUtil.getMaxDatyOfMonth(value);
		value.setDate(31);
		assertEquals("Result",result.toString() , value.toString());
	}
	@Test
	public void getCurrentDatehFormatTest() {
		Date value = new Date();
		Date result = FormatUtil.getCurrentDate(2);
		value.setDate(9);
		value.setHours(0);
		value.setSeconds(0);
		value.setMinutes(0);
		assertEquals("Result",result.toString() , value.toString());
	}
	@Test
	public void getCurrentYearFormatTest() {
		int value = Year.now().getValue();
		String result = FormatUtil.getCurrentYear();
		assertEquals("Result",result , String.valueOf(value));
	}
	@Test
	public void removeDashFromPartNoFormatTest() {
		String value = "0231233434";
		String result = FormatUtil.removeDashFromPartNo("02-3123-3434");
		assertEquals("Result",result , value);
	}
	@Test
	public void addDashToPartNoFormatTest() {
		String value = "02312-33434";
		String result = FormatUtil.addDashToPartNo("0231233434");
		assertEquals("Result",result , value);
	}
	@Test
	public void convertObjIdJSONFormatTest() {
		Map<String, String> value = new HashMap<String, String>();
		value.put("test1", "a");
		value.put("test2", "b");
		String result = FormatUtil.convertObjIdJSON(value);
		assertEquals("Result",result , "{\"test2\":\"b\",\"test1\":\"a\"}");
	}
	@Test
	public void addFormatTest() {
		BigDecimal value = new BigDecimal(100000);
		BigDecimal value2 = new BigDecimal(200000);
		BigDecimal result = FormatUtil.add(value,value);
		assertEquals("Result",result , value2);
	}
	@Test
	public void minusFormatTest() {
		BigDecimal value = new BigDecimal(100000);
		BigDecimal value2 = new BigDecimal(99990);
		BigDecimal result = FormatUtil.minus(value,new BigDecimal(10));
		assertEquals("Result",result , value2);
	}
	@Test
	public void dateDiffFormatTest() {
		Long result = FormatUtil.dateDiff(new Date(),new Date());
		assertEquals("Result",result.longValue() , 0);
	}
	@Test
	public void checkOverMonthFormatTest() {
		Date value = new Date();
		value.setMonth(1);
		Date value2 = new Date();
		value2.setMonth(5);
		boolean result = FormatUtil.checkOverMonth(value,value2,2);
		assertEquals("Result",result , true);
	}
	@Test
	public void padFormatTest() {
		char[] value = new char[2];
		value[0] = 1;
		value[1] = 2;
		String result = FormatUtil.pad("test",1,1,value[0]);
		assertEquals("Result",result , "test");
	}
	@Test
	public void convertFormatTest() {
		Date result = FormatUtil.convert(new Date());
		assertEquals("Result",result.toString() , "2019-01-07");
	}
	@Test
	public void convertTimestampFormatTest() {
		Date result = FormatUtil.convertTimestamp(new Date());
		assertNotNull("Result",result);
		//assertEquals("Result",result.toString() , "2019-01-07");
	}
	@Test
	public void addMonthFormatTest() {
		Date result = FormatUtil.addMonth(new Date(),1);
		assertNotNull("Result",result);
	}
	@Test
	public void nextStringFormatTest() {
		String result = FormatUtil.nextString();
		assertNotNull("Result",result);
	}
	@Test
	public void convertDateToOracleDBFormatTest() {
		String value = "12/11/2018";
		String stFormat = "dd/MM/yyyy";
		Date result = FormatUtil.convertDateToOracleDB(value,stFormat);
		assertEquals("Result",result.toString() , "2018-11-12");
	}
	@Test
	public void convertDateToOracleDB2FormatTest() {
		String value = "jan-18";
		Date result = FormatUtil.convertDateToOracleDB(value);
		assertEquals("Result",result.toString() , "2018-01-01");
	}
	@Test
	public void currentTimestampToOracleDBFormatTest() {
		Date result = FormatUtil.currentTimestampToOracleDB();
		assertNotNull("Result",result);
	}
	@Test
	public void currentTimestampToOracleDB2FormatTest() {
		String value = "jan-18";
		Date result = FormatUtil.convertTimestampToOracleDB("10000");
		assertNotNull("Result",result);
	}
	@Test
	public void compareDateTime2FormatTest() {
		String value = "01/11/2018";
		String value2 = "01/11/2018";
		String stFormat = "dd/MM/yyyy";
		int result = FormatUtil.compareDateTime(value,value2,stFormat);
		assertEquals("Result",result , 0);
	}
}
