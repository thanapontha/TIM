/******************************************************
 * Program History
 * 
 * Project Name	            :  GWRDS : 
 * Client Name				:  TMAP-EM
 * Package Name             :  th.co.toyota.bw0.util
 * Program ID 	            :  DateFormatUtil.java
 * Program Description	    :  Date converter string to date or date to string
 * Environment	 	        :  Java 7
 * Author					:  Thanapon T.
 * Version					:  1.0
 * Creation Date            :  July 14, 2016
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2013-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/

package th.co.toyota.bw0.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.jackson.map.ObjectMapper;

import th.co.toyota.bw0.api.constants.AppConstants;

import com.google.common.base.Strings;

public class FormatUtil {
	public static String START_PREFIX_YEAR = "20";
	public static String DATE_MMM_YYYY = "MMM-yyyy";
	public static String DASH = "-";
	
    public static Date convertStringToDate(String value, String stFormat) {
    	if((stFormat!=null && stFormat.trim().length() <= 0) || (stFormat == null)){
    		stFormat = AppConstants.DATE_SHOW_IN_SCREEN;
    	}
    	if(AppConstants.DATE_SHOW_IN_SCREEN.equals(stFormat)){
    		String[] tmp = value.split(DASH);
    		if(tmp!=null && tmp.length==2){
    			value = tmp[0]+DASH+START_PREFIX_YEAR+tmp[1];
    			stFormat = DATE_MMM_YYYY;
    		}
    	}
    	SimpleDateFormat formatter = new SimpleDateFormat(stFormat);
		Date date = null;
		try {
			if (!Strings.isNullOrEmpty(value)) {
				date = formatter.parse(value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}
    
    /**
	 * Format string to date
	 * @param value
	 * @param stFormat = AppConstants.DATE_SHOW_IN_SCREEN MMM-YY
	 * @return date
	 * @throws Exception 
	 */
    public static Date convertStringToDate(String value) {
		return convertStringToDate(value, AppConstants.DATE_SHOW_IN_SCREEN);
	}
    
    /**
	 * Format date to string
	 * @param dDate
	 * @param stFormat
	 * @return dateStr
	 * @throws Exception 
	 */
    public static String convertDateToString(Date dDate, String stFormat) {
    	if((stFormat!=null && stFormat.trim().length() <= 0) || (stFormat == null)){
    		stFormat = AppConstants.DATE_SHOW_IN_SCREEN;
    	}
		SimpleDateFormat dateFormat = new SimpleDateFormat(stFormat);
		String dateStr = "";
		try {
			dateStr = dateFormat.format(dDate);
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return dateStr;
	}
    
    /**
	 * Format date to string
	 * @param dDate
	 * @param stFormat
	 * @return dateStr
	 * @throws Exception 
	 */
    public static String convertDateToString(Date dDate) {
    	return convertDateToString(dDate, AppConstants.DATE_SHOW_IN_SCREEN);
    }
    
    /**
	 * Format BigDecimal to string
	 * @param value
	 * @param precision
	 * @param withcomma
	 * @return 
	 * @throws Exception 
	 */
    public static String convertBigDecimalToString(BigDecimal value, int precision, boolean withcomma) {
    	StringBuffer format = new StringBuffer();
    	if (value==null) {
    		value=BigDecimal.valueOf(0);
    	}
    	if(withcomma){
    		format.append("#,##0");
    		if(precision>0){
    			format.append(".");
    			for(int i=0;i<precision;i++){
        			format.append("0");
        		}	
    		}    		
    	}else{
    		format.append("0");
    		if(precision>0){
    			format.append(".");
    			for(int i=0;i<precision;i++){
        			format.append("0");
        		}	
    		}    		
    	}
    	DecimalFormat decimalFormat = new DecimalFormat(format.toString());
    	String numberAsString = decimalFormat.format(value);
		return numberAsString;
	}
    
    /**
	 * Format BigDecimal to string
	 * @param value
	 * @param precision
	 * @param withcomma
	 * @return 
	 * @throws Exception 
	 */
    public static BigDecimal convertStringToBigDecimal(String value) {
    	try{
    		if (!Strings.isNullOrEmpty(value)) {
		    	String valueStr = value;
		    	valueStr = valueStr.replaceAll(",", "");
		    	BigDecimal bigd = new BigDecimal(valueStr);
				return bigd;
    		} else {
    			return null;
    		}
    	}catch(Exception e){
//    		e.printStackTrace();
    		return null;
    	}
	}
    
    public static Long convertStringToLong(String value) {
    	try{
    		if (!Strings.isNullOrEmpty(value)) {
		    	String valueStr = value;
		    	valueStr = valueStr.replaceAll(",", "");
		    	Long lng = new Long(valueStr);
				return lng;
    		} else {
    			return null;
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    		return null;
    	}
	}
    
    public static Long convertStringToLongRoundUp(String value) {
    	try{
    		if (!Strings.isNullOrEmpty(value)) {
		    	String valueStr = value;
		    	valueStr = valueStr.replaceAll(",", "");
		    	Double big = new Double(valueStr);
		    	Long lng = (long) Math.ceil(big);
				return lng;
    		} else {
    			return null;
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    		return null;
    	}
	}
    
    public static boolean validateBigDecimal(String value, String max) {
    	boolean valid=true;
    	try {
			BigDecimal data = FormatUtil.convertStringToBigDecimal(value);
			BigDecimal maxData = FormatUtil.convertStringToBigDecimal(max);
			if (data != null && maxData !=null) {
				if (data.compareTo(maxData)>0) {
					valid=false;
				}else{
					int decimal = maxData.scale();
					int lenMax = max.length();
					if(isNumber(value, lenMax, decimal) == false){
						valid = false;
					}
				}
			}  else {
//				In case of cannot convert value or max to big decimal object
				valid = false;
			}
		} catch (Exception e) {
			valid = false;
		}
		return valid;
	}
    
    public static boolean validateBigDecimal(BigDecimal data, String min, String max) {
    	boolean valid=true;
    	try {
    		BigDecimal minData = FormatUtil.convertStringToBigDecimal(min);
			BigDecimal maxData = FormatUtil.convertStringToBigDecimal(max);
			if (data != null && maxData !=null) {
				if ((data.compareTo(minData)==-1)|| data.compareTo(maxData)==1) {//data < min or data > max
					valid=false;
				}
			}  else {
				//In case of cannot convert minimum or maximum to big decimal object
				valid = false;
			}
		} catch (Exception e) {
			valid = false;
		}
		return valid;
	}
    
    private static boolean isNumber(String numberAsString,int maxlen, int decimal){
    	try {
    		int max = maxlen;
    		if(decimal>0){
    			max = (maxlen - decimal) - 1;
    		}
    		String regExCheck1 = "^(-)?\\d{1,3}(,\\d{3}){1,3}(\\.\\d{0,"+decimal+"})?$";	//decimal	
    		String regExCheck2 = "^(-)?\\d{1,"+max+"}(\\.\\d{0,"+decimal+"})?$";	//decimal	
    		String regExCheck3 = "^(-)?\\d{1,3}(,\\d{3}){1,3}$"; //number with comma
    		String regExCheck4 = "^(-)?(\\d{1,"+max+"})$"; //number
			if (numberAsString != null && numberAsString.trim().length() > 0 && 
					(numberAsString.matches(regExCheck1) || numberAsString.matches(regExCheck2) 
					 || numberAsString.matches(regExCheck3) || numberAsString.matches(regExCheck4)) ) {
				return true;
			}else{
				return false;
			}    	   
      	} catch (Exception e) {
    	   return false;
    	}
    }

    public static Double convertStringToDouble(String numberAsString,int maxlen, int precision){
    	try {    		
    		if(isNumber(numberAsString, maxlen, precision)) {
				numberAsString = numberAsString.replaceAll(",", "");
				BigDecimal bigd = new BigDecimal(numberAsString);
				return bigd.doubleValue();
			}else{
				return null;
			}    	   
    	   
    	} catch (Exception e) {
    	   return null;
    	}
    }
    
    public static BigDecimal convertStringToBigDecimal(String numberAsString, int maxlen, int precision){
    	try {    		
    		if(isNumber(numberAsString, maxlen, precision)) {
				numberAsString = numberAsString.replaceAll(",", "");
				return new BigDecimal(numberAsString);
			}else{
				return null;
			}    	   
    	   
    	} catch (Exception e) {
    	   return null;
    	}
    }
    
    public static String convertStringToCorrectNumber(String numberAsString,int maxlen, int precision, boolean withcomma){
    	try {    		
    		if(isNumber(numberAsString, maxlen, precision)) {
				numberAsString = numberAsString.replaceAll(",", "");
				BigDecimal number = new BigDecimal(numberAsString);
				return convertBigDecimalToString(number, precision, withcomma);
			}else{
				return null;
			}   	   
    	   
    	} catch (Exception e) {
    	   return null;
    	}
    }

    public static Double convertStringToDouble(String numberAsString,int maxlen, int precision, boolean withcomma){
    	try {    		
    		if(isNumber(numberAsString, maxlen, precision)) {
				numberAsString = numberAsString.replaceAll(",", "");
				BigDecimal bigd = new BigDecimal(numberAsString);
				return bigd.doubleValue();
			}else{
				return null;
			}   	   
    	   
    	} catch (Exception e) {
    	   return null;
    	}
    }
    
    public static String trimValue(String value, boolean upperCase) {
    	if(upperCase){
    		return Strings.nullToEmpty(value==null?"":value.trim()).toUpperCase();
    	}else{
    		return Strings.nullToEmpty(value==null?"":value.trim());
    	}
	}
    
    public static String nullToZero(String value) {
		return Strings.nullToEmpty(value==null?"0":value.trim());
	}
    
    public static boolean isNullorZeroOrLessthanZero(BigDecimal value) {
		if(value==null){
			return true;
		}else if(value.compareTo(BigDecimal.ZERO)!=1){
			return true;
		}else{
			return false;
		}
	}
    
    public static boolean isNullorZero(BigDecimal value) {
		if(value==null){
			return true;
		}else if(value.compareTo(BigDecimal.ZERO)==0){
			return true;
		}else{
			return false;
		}
	}
    
	//Remark !!! this mehtod it return false if input 31/2/2015 
	public static boolean isValidDate(String dateStr, String format){
		if (dateStr == null) {
			return false;
		}
		dateStr = dateStr.trim();
		try{
			if(Strings.isNullOrEmpty(format)){
				format = AppConstants.DATE_SHOW_IN_SCREEN;
			}
	    	if(AppConstants.DATE_SHOW_IN_SCREEN.equals(format)){
	    		String[] tmp = dateStr.split(DASH);
	    		if(tmp!=null && tmp.length==2){
	    			dateStr = tmp[0]+DASH+START_PREFIX_YEAR+tmp[1];
	    			format = DATE_MMM_YYYY;
	    		}
	    	}
			SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.US);
			formatter.setLenient(false);
			formatter.parse(dateStr.trim());
		}catch(Exception ex){
			return false;
		}
		return true;
	}	
	
	public static boolean isValidDate(String dateStr){
		return isValidDate(dateStr,null);
	}
	
	public static boolean isValidByPattern(String strPattern, String strValue){
		Pattern p = Pattern.compile(strPattern);
		Matcher m = p.matcher(strValue);		
		if (!m.matches()) return false;
		return true;
	}
	
	//Remark 0 = equal, 1 = greater, -1 = less, -2 = error 
	public static int compareDateTime(Date from, Date to){
    	try {
    		return from.compareTo(to);
      	} catch (Exception e) {
    	   return -2;
    	}
    }
	
	//Remark 0 = equal, 1 = greater, -1 = less, -2 = error
	public static int compareDate(Date from, Date to){
    	try {
    		String dtStrTo = convertDateToString(to, AppConstants.DATE_SHOW_IN_SCREEN);
    		String dtStrFrom = convertDateToString(from, AppConstants.DATE_SHOW_IN_SCREEN);
    		Date dtCurNotTime = convertStringToDate(dtStrTo, AppConstants.DATE_SHOW_IN_SCREEN);
    		Date dtFromNotTime = convertStringToDate(dtStrFrom, AppConstants.DATE_SHOW_IN_SCREEN);
    		return dtFromNotTime.compareTo(dtCurNotTime);
      	} catch (Exception e) {
    	   return -2;
    	}
    }
    
	public static Date getCurrentDate(){
    	try {
    		String currenttStr = convertDateToString(new Date(), AppConstants.DATE_STRING_SCREEN_FORMAT);
    		Date dtCurNotTime = convertStringToDate(currenttStr, AppConstants.DATE_STRING_SCREEN_FORMAT);
    		return dtCurNotTime;
      	} catch (Exception e) {
    	   return new Date();
    	}
    }
	
	public static Date getCurrentMonth(){
    	try {
    		String currenttStr = convertDateToString(new Date(), AppConstants.DATE_SHOW_IN_SCREEN);
    		Date dtCurNotTime = convertStringToDate(currenttStr, AppConstants.DATE_SHOW_IN_SCREEN);
    		return dtCurNotTime;
      	} catch (Exception e) {
    	   return new Date();
    	}
    }
	
	public static Date getMaxDatyOfMonth(Date date){
		Calendar aCalendar = Calendar.getInstance();
		aCalendar.setTime(date);
		aCalendar.set(Calendar.DATE,     aCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return aCalendar.getTime();
	}
	
	public static Date getCurrentDate(int increaseDate){
    	try {
    		Calendar cal = Calendar.getInstance();
    		cal.setTime(FormatUtil.getCurrentDate());
    		cal.add(Calendar.DATE, increaseDate);
    		return cal.getTime();
      	} catch (Exception e) {
    	   return new Date();
    	}
    }
	
	public static String getCurrentYear(){
		return convertDateToString(new Date(), AppConstants.DATE_STRING_FORMAT_YYYY);
    }	
    
//	public static Date getCurrentDateTime(){
//		return new Date();
////    	try {
////    		String currenttStr = convertDateToString(new Date(), AppConstants.DATE_TIME_FORMAT_UPDATEDT_KEY);
////    		Date dtCurNotTime = convertStringToDate(currenttStr, AppConstants.DATE_TIME_FORMAT_UPDATEDT_KEY);
////    		return dtCurNotTime;
////      	} catch (Exception e) {
////    	   return new Date();
////    	}
//    }
	
	public static String removeDashFromPartNo(String partNo){
		return FormatUtil.trimValue(partNo, true).replaceAll("-", "").toUpperCase();
	}
	
	public static String addDashToPartNo(String partNo){
		String tmpPartNo = FormatUtil.trimValue(partNo, true);
		if(tmpPartNo.indexOf("-")> -1){
			return tmpPartNo;
		}
		String newPartNo = tmpPartNo;
		if(tmpPartNo.length() > 10){
			newPartNo =  tmpPartNo.substring(0,5)+"-"+tmpPartNo.substring(5,10)+"-"+tmpPartNo.substring(10,tmpPartNo.length());
		}else if(tmpPartNo.length() == 10){
			newPartNo =  tmpPartNo.substring(0,5)+"-"+tmpPartNo.substring(5,tmpPartNo.length());
		}else if(tmpPartNo.length() > 5){
			newPartNo =  tmpPartNo.substring(0,5)+"-"+tmpPartNo.substring(5,tmpPartNo.length());
		}
		return newPartNo;
	}
	
	public static String convertObjIdJSON(Map<String, String> objMap) {
		String objIdJSON = "";
		ObjectMapper mapper = new ObjectMapper();
		try {
			objIdJSON = mapper.writeValueAsString(objMap);
		} catch (Exception e) {
			objIdJSON = "";
		}
		return objIdJSON;
	}
    
	public static BigDecimal add(BigDecimal total, BigDecimal object){
		if(total == null){
			total = object;
		}else if(total!= null && object!= null){
			total = total.add(object);
		}
		return total;
	}
	
	public static BigDecimal minus(BigDecimal total, BigDecimal object){
		if(total == null){
			total = object;
		}else if(total!= null && object!= null){
			total = total.add(object.multiply(BigDecimal.valueOf(-1)));
		}
		return total;
	}
	
	public static long dateDiff(Date dtFrom, Date dtTo) {
		long diff = dtTo.getTime() - dtFrom.getTime();
		return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
	}
	
	public static boolean checkOverMonth(Date dtFrom, Date dtTo, int monthChk) {
		Calendar calNextMonth = Calendar.getInstance();
		calNextMonth.setTime(dtFrom);
		calNextMonth.add(Calendar.MONTH, monthChk);
		
		Calendar calToChk = Calendar.getInstance();
		calToChk.setTime(dtTo);

		if(calToChk.after(calNextMonth)){
			return true;
		}else{
			return false;
		}
	}
	
	   /**
     * This method will pad specified char to specified location
     * @param value - the String to be padded
     * @param maxLength - maximum length of string after padding
     * @param position - the place where char be added LEFT_PAD or RIGHT_PAD
     * @param ch - the character to be padded
     * @return - the final string after padding
     */
    public static String pad(String value, int maxLength, int position, char ch) 
    {
        int stringLength = value.length();
        int count =  maxLength - stringLength;
        StringBuffer strBuf = new StringBuffer();
        StringBuffer returnBuffer = new StringBuffer();
        // form the pad
        for (int i=0;i<count; i++) {
            strBuf.append(ch);     
        }

        if (position == AppConstants.LEFT_PAD) {
            returnBuffer.append(strBuf).append(value);
        }
        else if (position == AppConstants.RIGHT_PAD) {
            returnBuffer.append(value).append(strBuf);
        }
        else {
            returnBuffer.append(value);
        }

        return returnBuffer.toString();
    }
    
	
	public static java.sql.Date convert(java.util.Date dtDate){
		return dtDate == null ? null : new java.sql.Date(dtDate.getTime());
	}
	
	public static java.sql.Timestamp convertTimestamp(java.util.Date dtDate){
		return dtDate == null ? null : new java.sql.Timestamp(dtDate.getTime());
	}
	
	public static Date addMonth(Date date, int month){
		Calendar cal = Calendar.getInstance(); 
		cal.setTime(date);
		cal.add(Calendar.MONTH, month);
		return cal.getTime();
	}

    /**
     * for generate random String
     */
    private static final char[] symbols = new char[50];    
     static {     
            for (int idx = 0; idx < 10; ++idx)       
                    symbols[idx] = (char) ('0' + idx);     
            for (int idx = 10; idx < 50; ++idx)       
                    symbols[idx] = (char) ('a' + idx - 10);   
            }    
    private static final java.util.Random random = new java.util.Random();    

    public static String nextString()   { 
        char[] buf = new char[30];  
        for (int idx = 0; idx < buf.length; ++idx)        
                buf[idx] = symbols[random.nextInt(symbols.length)];     
        return new String(buf);   
    }

  	/**
  	 * Used be for Oracle DB By Check String --> sql.Date , Can Change Format
  	 * @param strDate
  	 * @param format
  	 * @return
  	 */
  	public static java.sql.Date convertDateToOracleDB(String strDate, String format) {
  		return FormatUtil.convert(FormatUtil.convertStringToDate(strDate, format));
  	}
  	
 	public static java.sql.Date convertDateToOracleDB(String strDate) {
  		return FormatUtil.convert(FormatUtil.convertStringToDate(strDate, AppConstants.DATE_SHOW_IN_SCREEN));
  	}
  	
  	/**
  	 * Get Current Date at be for Oracle DB
  	 * @return
  	 */
  	public static java.sql.Timestamp currentTimestampToOracleDB(){
  		return FormatUtil.convertTimestamp(new Date());
  	}
    
  	public static java.sql.Timestamp convertTimestampToOracleDB(String strLongDate) {
  		return new java.sql.Timestamp(Long.parseLong(strLongDate));
  	}
  	
	/**
	 * Compare Date if StartDate >= EndDate will return true , else return false
	 * @param strStartDate
	 * @param strEndDate
	 * @param format
	 * @return
	 */
	public static int compareDateTime(String strStartDate, String strEndDate , String format) {
		Date startDate = FormatUtil.convertStringToDate(strStartDate,format);
		Date endDate = FormatUtil.convertStringToDate(strEndDate,format);
		return compareDateTime(startDate,endDate);
	}
	
    public static void main(String[] arg){
    	Date dDate = convertStringToDate("Mar-99","MMM-yy");
    	String dtStr = convertDateToString(dDate);
    	System.out.println(dtStr);
    	dtStr = convertDateToString(dDate,"MMM-yyyy");
    	System.out.println(dtStr);
//    	dDate = convertDateTimeToOracleDB("Mar-99");
//    	System.out.println(dtStr);
    	dtStr = convertDateToString(dDate,"MMM-yyyy");
    	System.out.println(dtStr);
//    	boolean flag = FormatUtil.validateBigDecimal("12.9999", "9999999.99");
//    	System.out.println("12.9999="+flag);
//    	flag = FormatUtil.validateBigDecimal("199999999", "9999999.99");
//    	System.out.println("199999999="+flag);
//    	flag = FormatUtil.validateBigDecimal("1,999,999", "9999999.99");
//    	System.out.println("1,999,999="+flag);
//    	flag = FormatUtil.validateBigDecimal("21,999,999", "9999999.99");
//    	System.out.println("21,999,999="+flag);
//    	flag = FormatUtil.validateBigDecimal("1,999,999.00", "9999999.99");
//    	System.out.println("1,999,999.00="+flag);
//    	flag = FormatUtil.validateBigDecimal("1,99,999", "9999999.99");
//    	System.out.println("1,99,999="+flag);
//    	flag = FormatUtil.validateBigDecimal("12.99", "9999999.99");
//    	System.out.println("12.99="+flag);
//    	flag = FormatUtil.validateBigDecimal("2,22,212.99", "9999999.99");
//    	System.out.println("2,22,212.99 = "+flag);
//    	flag = FormatUtil.validateBigDecimal("222,212.9", "9999999.9");
//    	System.out.println("222,212.9 = "+flag);
//    	flag = FormatUtil.validateBigDecimal("222,212.9444", "9999999.9999");
//    	System.out.println("222,212.9444 = "+flag);
//    	flag = FormatUtil.validateBigDecimal("222,212.94434", "9999999.9999");
//    	System.out.println("222,212.94434 = "+flag);
//    	flag = FormatUtil.validateBigDecimal("222,212.94", "9999999.9999");
//    	System.out.println("222,212.94 = "+flag);
//    	flag = FormatUtil.validateBigDecimal("222,212", "9999");
//    	System.out.println("222,212= "+flag);
//    	flag = FormatUtil.validateBigDecimal("44", "9999");
//    	System.out.println("44= "+flag);
//    	flag = FormatUtil.validateBigDecimal("9999", "9999");
//    	System.out.println("9999= "+flag);
//    	flag = FormatUtil.validateBigDecimal("44.1", "9999");
//    	System.out.println("44.1= "+flag);
//    	Date dtFrom = convertStringToDate("11/01/2016","dd/MM/yyyy");
//    	Date dtTo = getCurrentDate();
    	
//    	Calendar aCalendar = Calendar.getInstance();
//    	aCalendar.setTime(dtFrom);
//    	aCalendar.set(Calendar.DATE,     aCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
//    	String maxdate = convertDateToString(aCalendar.getTime(), "dd/MM/yyyy");
//    	System.out.println("current =  "+dtFrom.toString()+" max date = "+maxdate);
//    	
//    	aCalendar = Calendar.getInstance();
//    	aCalendar.setTime(dtTo);
//    	aCalendar.set(Calendar.DATE,     aCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
//    	maxdate = convertDateToString(aCalendar.getTime(), "dd/MM/yyyy");
//    	System.out.println("current =  "+dtTo.toString()+" max date = "+maxdate);
//    	
//    	dtFrom = convertStringToDate("20/02/2012","dd/MM/yyyy");
//    	aCalendar = Calendar.getInstance();
//    	aCalendar.setTime(dtFrom);
//    	aCalendar.set(Calendar.DATE,     aCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
//    	maxdate = convertDateToString(aCalendar.getTime(), "dd/MM/yyyy");
//    	System.out.println("current =  "+dtFrom.toString()+" max date = "+maxdate);
//    	
//    	dtFrom = convertStringToDate("20/02/2013","dd/MM/yyyy");
//    	aCalendar = Calendar.getInstance();
//    	aCalendar.setTime(dtFrom);
//    	aCalendar.set(Calendar.DATE,     aCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
//    	maxdate = convertDateToString(aCalendar.getTime(), "dd/MM/yyyy");
//    	System.out.println("current =  "+dtFrom.toString()+" max date = "+maxdate);
//
//    	boolean chk = FormatUtil.checkOverMonth(dtFrom, dtTo,7);
//    	System.out.println("b. "+chk);
//    	
//    	dtFrom = convertStringToDate("01/01/2016");
//    	dtTo = convertStringToDate("12/08/2016");
//
//    	chk = FormatUtil.checkOverMonth(dtFrom, dtTo,7);
//    	System.out.println("b. "+chk);
//    	
//    	dtFrom = convertStringToDate("11/01/2016");
//    	dtTo = convertStringToDate("13/08/2016");
//
//    	chk = FormatUtil.checkOverMonth(dtFrom, dtTo,7);
//    	System.out.println("b. "+chk);
//    	
//    	dtFrom = convertStringToDate("11/01/2016");
//    	dtTo = convertStringToDate("12/07/2016");
//
//    	chk = FormatUtil.checkOverMonth(dtFrom, dtTo,7);
//    	System.out.println("b. "+chk);
    }
}
