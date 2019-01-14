package th.co.toyota.bw0.api.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.EntityExistsException;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.exception.SQLGrammarException;
import org.slf4j.Logger;
import org.springframework.dao.InvalidDataAccessResourceUsageException;

import com.google.common.base.Strings;

import th.co.toyota.bw0.api.constants.AppConstants;
import th.co.toyota.bw0.api.constants.MessagesConstants;
import th.co.toyota.bw0.api.exception.common.CommonErrorException;
import th.co.toyota.st3.api.constants.CST30000Messages;

public final class CommonUtility {
	
	private CommonUtility() {
	    throw new IllegalStateException("CommonUtility class");
	  }

	public static void deleteFile(File sourcefile){
	    sourcefile.deleteOnExit();
	}
	
	public static void createDirectoryIfNotExists(String directoryName) {
		File theDir = new File(directoryName);
		if (!theDir.exists()) theDir.mkdir();
	}

	public static int toInt(Object obj) {
		return (obj == null) ? 0 : Integer.parseInt(obj.toString());
	}

	public static char toChar(Object obj) {
		return (obj == null) ? ' ' : obj.toString().charAt(0);
	}

	public static double stringToDouble(String number) {
		return (Strings.isNullOrEmpty(number)) ? 0.0 : Double
				.parseDouble(number.replaceAll(",", ""));
	}
	
	public static Double stringToDoubleOrNull(String number) {
		Double value =  stringToDouble(number);
		if (value == 0.0){
			return null;
		}
		
		return value;
	}

	public static long daysDiff(Date from, Date to) {
		long diff = to.getTime() - from.getTime();
		return (diff / (24 * 60 * 60 * 1000));
	}

	public static long daysDiffInMinute(Date date1, Date date2) {
		long diffMs = date1.getTime() - date2.getTime();
		long diffSec = diffMs / 1000;
		long min = diffSec / 60;
		return min;
	}

	public static int monthsDiff(Date from, Date to) {
		if (from == null || to == null) {
			return 0;
		}

		Calendar startCalendar = new GregorianCalendar();
		startCalendar.setTime(from);
		Calendar endCalendar = new GregorianCalendar();
		endCalendar.setTime(to);

		int startMonth = startCalendar.get(Calendar.MONTH);
		int endMonth = endCalendar.get(Calendar.MONTH);
		int startYear = startCalendar.get(Calendar.YEAR);
		int endYear = endCalendar.get(Calendar.YEAR);

		int diffYear = endYear - startYear;
		int diffMonth = 0;

		if (startMonth > endMonth) {
			diffYear = diffYear - 1;
			diffMonth = 12 + (endMonth - startMonth);
		} else {
			diffMonth = endMonth - startMonth;
		}
		diffMonth = (diffYear * 12) + diffMonth;
		return diffMonth;
	}

	public static String convertBatchParam(String param) {
		if(param == null) return "";
		String value = param.replaceAll(AppConstants.BATCH_CHARACTOR_REPLACE_BLANK_BACK, AppConstants.BLANK_SPACE);
		value = value.replaceAll(AppConstants.BATCH_CHARACTOR_REPLACE_LEFT_PARENTHESIS_BACK, AppConstants.LEFT_PARENTHESIS);
		value = value.replaceAll(AppConstants.BATCH_CHARACTOR_REPLACE_RIGHT_PARENTHESIS_BACK, AppConstants.RIGHT_PARENTHESIS);
		return value.trim();
	}

	public static String addBlankSaparator(String inputString){
		if (inputString == null){
			return null;
		}
		String value = inputString.replaceAll(AppConstants.BLANK_SPACE, AppConstants.BATCH_CHARACTOR_REPLACE_BLANK);
		value = value.replaceAll(AppConstants.LEFT_PARENTHESIS, AppConstants.BATCH_CHARACTOR_REPLACE_LEFT_PARENTHESIS);
		value = value.replaceAll(AppConstants.RIGHT_PARENTHESIS, AppConstants.BATCH_CHARACTOR_REPLACE_RIGHT_PARENTHESIS);
		return value;
	}
	
	public static String removeZeroLeading(String stringWithZero) {
		if (stringWithZero == null) {
			return null;
		}
		return stringWithZero.replaceFirst("^0+(?!$)", "");
	}

	public static boolean isListNullOrEmpty(List<?> list) {
		if (list != null && !list.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}

	public static String getSQLErrorMessage(RuntimeException e) {
		Throwable throwable = e;
		String sqlExMsg = "";
		if(e instanceof InvalidDataAccessResourceUsageException) {
			while (throwable != null && !(throwable instanceof SQLSyntaxErrorException)) {
				throwable = throwable.getCause();
			}
			if (throwable instanceof SQLSyntaxErrorException) {
				SQLSyntaxErrorException sqlex = (SQLSyntaxErrorException) throwable;
				sqlExMsg = sqlex.getMessage();
			}
		}else if(e instanceof EntityExistsException) {
			while (throwable != null && !(throwable instanceof EntityExistsException)) {
				throwable = throwable.getCause();
			}
			if (throwable instanceof EntityExistsException) {
				EntityExistsException sqlex = (EntityExistsException) throwable;
				sqlExMsg = sqlex.getMessage();
			}
		}else{
			
			while (throwable != null && !(throwable instanceof SQLException)) {
				throwable = throwable.getCause();
			}
			if (throwable instanceof SQLException) {
				SQLException sqlex = (SQLException) throwable;
				sqlExMsg = sqlex.getMessage();
			}
		}
		return sqlExMsg;
	}
	
	public static String genMessageOfException(Exception e){
		if(e instanceof NullPointerException){
			return NullPointerException.class.getName();
		}else{
			return e.getCause()==null?e.getMessage():e.getMessage()+e.getCause().getMessage();
		}
	}
	
	public static CommonErrorException handleExceptionToCommonErrorException(Exception e, Logger loggerObj, boolean batchProcess){
		if(e instanceof CommonErrorException){
			return (CommonErrorException)e;
		}else{
			String errMsg = e.getMessage();
			Object obj = e.getCause();
			if(obj instanceof SQLGrammarException){
				SQLGrammarException ge = (SQLGrammarException)obj;
				obj = ge.getCause();
				if(obj instanceof SQLSyntaxErrorException){
					SQLSyntaxErrorException se = (SQLSyntaxErrorException)obj;
					errMsg = se.getMessage();
				}else{
					errMsg = ge.getMessage();
				}
			}
			if(loggerObj!=null && loggerObj instanceof Logger){
				loggerObj.error(ExceptionUtils.getStackTrace(e));
			}
			if(batchProcess){
				return new CommonErrorException(MessagesConstants.B_ERROR_UNDEFINED_ERROR, new String[]{errMsg}, AppConstants.ERROR);
			}else{
				return new CommonErrorException(CST30000Messages.ERROR_UNDEFINED_ERROR, new String[]{errMsg}, AppConstants.ERROR);
			}
		}
	}

	public static String leadTimeFormat(int times) {
		boolean isNegative = false;
		int day = 0;
		int hour = 0;
		int mins = 0;
		String leadTimeFormat = "";
		
		if (times < 0){
			times*=-1;
			isNegative = true;
		}
		
		day = times / 24 / 60;
		hour = times / 60 % 24;
		mins = times % 60;
		
		if (isNegative){
			leadTimeFormat = "-";
		}
		
		leadTimeFormat += new StringBuffer("").append(String.format("%02d", day))
											  .append(":").append(String.format("%02d", hour)).append(":")
											  .append(String.format("%02d", mins)).toString();
		return leadTimeFormat;
	}
	
	public static boolean moveFile(String sourcePathWithFileName, String destinationPathWithFileName) {
		InputStream inStream = null;
		OutputStream outStream = null;
	 
	    	try{
	 
	    	    File sourcefile = new File(sourcePathWithFileName);
	    	    File destinationfile = new File(destinationPathWithFileName);
	 
	    	    inStream = new FileInputStream(sourcefile);
	    	    outStream = new FileOutputStream(destinationfile);
	 
	    	    byte[] buffer = new byte[1024];
	 
	    	    int length;
	    	    //copy the file content in bytes 
	    	    while ((length = inStream.read(buffer)) > 0){
	 
	    	    	outStream.write(buffer, 0, length);
	 
	    	    }
	 
	    	    inStream.close();
	    	    outStream.close();
	 
	    	    //delete the original file
	    	    sourcefile.delete();
	 
// 				System.out.println("File is copied successful!");
	    	    return true;
	    	} catch(IOException e) {
	    	    e.printStackTrace();
	    	    return false;
	    	}
	}
	
	public static Date removeTime(Date date) {
		if(date != null) {
	        Calendar cal = Calendar.getInstance();
	        cal.setTime(date);
	        cal.set(Calendar.HOUR_OF_DAY, 0);
	        cal.set(Calendar.MINUTE, 0);
	        cal.set(Calendar.SECOND, 0);
	        cal.set(Calendar.MILLISECOND, 0);
	        return cal.getTime();
		}else{
			return null;
		}
    }
}
