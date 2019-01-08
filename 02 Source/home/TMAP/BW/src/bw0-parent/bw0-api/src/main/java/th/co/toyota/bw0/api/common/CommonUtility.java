package th.co.toyota.bw0.api.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.exception.SQLGrammarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import th.co.toyota.bw0.api.constants.AppConstants;
import th.co.toyota.bw0.api.constants.MessagesConstants;
import th.co.toyota.bw0.api.exception.common.CommonErrorException;
import th.co.toyota.st3.api.constants.CST30000Messages;

import com.google.common.base.Strings;

public final class CommonUtility {
	private static Logger logger = LoggerFactory.getLogger(CommonUtility.class);
	
	private CommonUtility() {
	    throw new IllegalStateException("CommonUtility class");
	  }
	
	public static void closeConnection(Connection conn, ResultSet rs, PreparedStatement ps, boolean closeConnection){
		try {
			if(conn!=null && !conn.isClosed()){
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (ps != null) {
					ps.close();
					ps = null;
				}
				
				if(closeConnection){
					conn.close();
					conn = null;
				}
			}
		} catch (SQLException e) {
			logger.error(ExceptionUtils.getStackTrace(e));
		}
	}
	
	public static void closeConnection(Connection conn, ResultSet rs, PreparedStatement ps, boolean closeConnection, boolean completed){
		try {
			if(conn!=null && !conn.isClosed()){
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (ps != null) {
					ps.close();
					ps = null;
				}
				if (completed) {
					conn.commit();
				} else {
					conn.rollback();
				}
				
				if(closeConnection){
					conn.close();
					conn = null;
				}
			}
		} catch (SQLException e) {
			logger.error(ExceptionUtils.getStackTrace(e));
		}
	}

	public static double toDouble(Object obj) {
		return (obj == null) ? 0.0 : Double.parseDouble(obj.toString());
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
		return (diffMs / 1000) / 60;
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
		return list == null || list.isEmpty();
	}

//	/**
//	 * This method converts a JSON String into and Object of passed class name.
//	 * 
//	 * @param className
//	 *            {@link String}
//	 * @param jsonString
//	 *            {@link String}
//	 * @return object {@link Object}
//	 * @throws JsonStringToObjectConversionException
//	 */
//	public static Object converJsonStringToObject(String className,
//			String jsonString) throws JsonStringToObjectConversionException {
//
//		if (className == null) {
//			throw new JsonStringToObjectConversionException(
//					"Class name can not be null");
//		}
//
//		if (jsonString == null) {
//			throw new JsonStringToObjectConversionException(
//					"Json string to conver in object can not be null");
//		}
//
//		Object object = null;
//		try {
//			Class<?> objectClass = Class.forName(className);
//			Gson gson = new Gson();
//			object = gson.fromJson(jsonString, objectClass);
//		} catch (ClassNotFoundException e) {
//			throw new JsonStringToObjectConversionException(className
//					+ " not found in class path");
//		} catch (Exception e) {
//			throw new JsonStringToObjectConversionException(
//					"Exception occured while converting jsonString into an Object of type :-"
//							+ className);
//		}
//		return object;
//	}
//
//	public static String getSQLErrorMessage(RuntimeException e) {
//		Throwable throwable = e;
//		if(e instanceof InvalidDataAccessResourceUsageException) {
//			while (throwable != null && !(throwable instanceof SQLSyntaxErrorException)) {
//				throwable = throwable.getCause();
//			}
//			if (throwable instanceof SQLSyntaxErrorException)
//				return ((SQLSyntaxErrorException) throwable).getMessage();
//		}else if(e instanceof EntityExistsException) {
//			while (throwable != null && !(throwable instanceof EntityExistsException)) {
//				throwable = throwable.getCause();
//			}
//			if (throwable instanceof EntityExistsException)
//				return ((EntityExistsException) throwable).getMessage();
//		}else{			
//			while (throwable != null && !(throwable instanceof SQLException)) {
//				throwable = throwable.getCause();
//			}
//			if (throwable instanceof SQLException) 
//				return ((SQLException) throwable).getMessage();
//		}
//		return "";
//	}
	
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
				obj = ((SQLGrammarException)obj).getCause();
				if(obj instanceof SQLSyntaxErrorException){
					errMsg = ((SQLSyntaxErrorException)obj).getMessage();
				}else{
					errMsg = ((SQLGrammarException)obj).getMessage();
				}
			}
			if(loggerObj instanceof Logger){
				loggerObj.error(ExceptionUtils.getStackTrace(e));
			}
			if(batchProcess){
				return new CommonErrorException(MessagesConstants.B_ERROR_UNDEFINED_ERROR, new String[]{errMsg}, AppConstants.ERROR);
			}else{
				return new CommonErrorException(CST30000Messages.ERROR_UNDEFINED_ERROR, new String[]{errMsg}, AppConstants.ERROR);
			}
		}
	}
	
	public static boolean moveFile(String sourcePathWithFileName, String destinationPathWithFileName) {	 
    	try(InputStream inStream = new FileInputStream(new File(sourcePathWithFileName));
    			OutputStream outStream = new FileOutputStream(new File(destinationPathWithFileName));){
 
    	    File sourcefile = new File(sourcePathWithFileName);
    	    byte[] buffer = new byte[1024];
 
    	    int length;
    	    //copy the file content in bytes 
    	    while ((length = inStream.read(buffer)) > 0){
    	    	outStream.write(buffer, 0, length);
    	    }
 
    	    deleteFile(sourcefile);
 
	      return true;
    	} catch(IOException e) {
    	    return false;
    	}
	}
	
	public static void deleteFile(File sourcefile){
	    sourcefile.deleteOnExit();
	}
	
	public static void createDirectoryIfNotExists(String directoryName) {
		File theDir = new File(directoryName);
		if (!theDir.exists()) theDir.mkdir();
	}
	
}
