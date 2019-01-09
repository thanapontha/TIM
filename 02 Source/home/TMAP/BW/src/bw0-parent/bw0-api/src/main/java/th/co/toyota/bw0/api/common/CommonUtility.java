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
import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.exception.SQLGrammarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import th.co.toyota.bw0.api.constants.AppConstants;
import th.co.toyota.bw0.api.constants.MessagesConstants;
import th.co.toyota.bw0.api.exception.common.CommonErrorException;
import th.co.toyota.st3.api.constants.CST30000Messages;

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
	
}
