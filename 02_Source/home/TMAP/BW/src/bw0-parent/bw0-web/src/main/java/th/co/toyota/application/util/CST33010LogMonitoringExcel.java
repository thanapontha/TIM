/******************************************************
 * Program History
 * 
 * Project Name	            :  st-server-webapp-template
 * Client Name				:  TMAP-EM
 * Package Name             :  th.co.toyota.application.util
 * Program ID 	            :  CST33010ExcelGenerator.java
 * Program Description	    :  Excel Generator for Log Moitoring (Override class)
 * Environment	 	        :  Java 7
 * Author					:  jofferson
 * Version					:  1.0
 * Creation Date            :  Sep 3, 2015
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2015-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/
package th.co.toyota.application.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.TemporalType;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.hibernate.QueryException;
import org.hibernate.exception.SQLGrammarException;
import org.hibernate.hql.internal.ast.QuerySyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import th.co.toyota.st3.api.constants.CST30000Constants;
import th.co.toyota.st3.api.constants.CST30000Messages;
import th.co.toyota.st3.api.download.CST30090ExcelGenerator;
import th.co.toyota.st3.api.exception.EntityMappingException;
import th.co.toyota.st3.api.exception.NoDataFoundException;
import th.co.toyota.st3.api.exception.UnsupportedQuerySyntaxException;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;

/**
 * The purpose of this library is to request the Excel Generation
 * by posting the on-demand batch queue.
 * 
 * @author jofferson
 * 
 */
@Component
public class CST33010LogMonitoringExcel extends CST30090ExcelGenerator {
	final Logger logger = LoggerFactory.getLogger(CST33010LogMonitoringExcel.class);

	@SuppressWarnings({ "unchecked" })
	public String createExcel(String jpql, Object[] params, String entity)
			throws NoDataFoundException, ClassNotFoundException,
			UnsupportedQuerySyntaxException, IOException,
			EntityMappingException {
		
		logger.info("Creating Excel file for Log Monitoring screen.");
		String logString;
		String tmpJpql;
		String tableName = null;

		try {
			// get entity model class
			enModel = Class.forName(entity);
			
			// extract displayed fields from jpql
			displayedFields = getDisplayedFields(jpql);	
		
			// get field properties from entity
			getFieldProperties();
			
			// PRE-PROCESS jpql if called from ODB function,
			// extract all Date search criteria {@Ddd/MM/yyyy} eg. @D25/02/2003
			tmpJpql = jpql + " ";
			Pattern pattern = Pattern.compile(DATE_PARAM_REGEX);
			Matcher matcher = pattern.matcher(tmpJpql);
			List<Date> allMatches = new ArrayList<Date>();
			
			SimpleDateFormat sdf = new SimpleDateFormat(CST30000Constants.DATE_STRING_SCREEN_FORMAT);
			int matchCount = 1;
			
			while (matcher.find()) {
				allMatches.add(sdf.parse(CharMatcher.anyOf(DATE_PARAM).removeFrom(matcher.group())));
				tmpJpql = tmpJpql.replaceFirst(matcher.group().toString().trim().substring(0, 12), "?" + String.valueOf(matchCount));
				matchCount++;
			}
			
			if (matchCount > 1) {
				params = allMatches.toArray();
				jpql = tmpJpql.trim();
			}
			// END PRE-PROCESS jpql
			
			Query query = em.createQuery(jpql);
			// set parameters
			if (params != null && params.length != 0) {
				for (int i = 0; i < params.length; i++) {
					if (params[i].getClass().equals(Date.class)) {
						query.setParameter(i+1, (Date) params[i], TemporalType.DATE);
					} else {
						query.setParameter(i+1, params[i]);
					}
				}
			}
			// pagination
			query.setFirstResult(firstResult);
			query.setMaxResults(maxRowsPerPage);

			// get data
			List<Object[]> data = null;
			data = query.getResultList();
			
			if (data.isEmpty()) {
				throw new NoDataFoundException();
			}
			
			// assign values from calling function
			String currentDate = new SimpleDateFormat(
					CST30000Constants.DATE_TIME_STRING_FILENAME_FORMAT).format(new Date());
			
			// get table name
			Annotation[] annotations = enModel.getDeclaredAnnotations();
			for (Annotation table : annotations) {
				if (table instanceof Table) {
					tableName = ((Table) table).name();
					break;
				}
			}
			
			reportName = Strings.isNullOrEmpty(reportName) || StringUtils.equalsIgnoreCase(tableName, reportName) ? tableName + "_" + currentDate : reportName;
			reportTitle = Strings.isNullOrEmpty(reportTitle) ? tableName : reportTitle;
			fileType = Strings.isNullOrEmpty(fileType) ? CST30000Constants.FILE_TYPE_XLS : fileType;
			filePath = Strings.isNullOrEmpty(overridePath) ? sharedFolder : overridePath;
			// end assign values from calling function

			listData(data);
			
		} catch (NoDataFoundException nde) {
			logString = messageSource.getMessage(
					CST30000Messages.ERROR_MESSAGE_DATA_NOT_FOUND, null,
					Locale.getDefault());
			
			logger.error(logString);
			throw nde;
		} catch (ClassNotFoundException ce) {
			logString = messageSource.getMessage(
					CST30000Messages.ERROR_FILE_DOES_NOT_EXIST, new String[] { entity },
					Locale.getDefault());
			
			logger.error(logString);
			throw ce;
		} catch (FileNotFoundException fe) {
			logString = messageSource.getMessage(
					CST30000Messages.ERROR_WRITING_FILE, 
					new String[] { reportName, fe.getMessage() },
					Locale.getDefault());
			
			logger.error(logString);
			throw fe;
		} catch (EntityMappingException eme) {
			logString = messageSource.getMessage(
					CST30000Messages.ERROR_MAPPING_TABLE_MODEL, 
					new String[] { tableName },
					Locale.getDefault());
			
			logger.error(logString);
			throw new EntityMappingException(logString);
		} catch (IOException ioe) {
			logString = messageSource.getMessage(
					CST30000Messages.ERROR_WRITING_FILE, 
					new String[] { reportName, ioe.getMessage() },
					Locale.getDefault());
			
			logger.error(logString);
			throw ioe;
		} catch (PersistenceException pe) {
			Throwable t = pe.getCause();
			while ((t != null) && !(t instanceof SQLGrammarException)) {
				t = t.getCause();
			}
			
			if (t instanceof SQLGrammarException) {
				logString = messageSource.getMessage(
						CST30000Messages.ERROR_UNSUPPORTED_QUERY_SYNTAX, new String[] { t.getMessage() },
						Locale.getDefault());
				
				logger.error(logString);
				throw new UnsupportedQuerySyntaxException(logString);
			} else {
				logString = messageSource.getMessage(
						CST30000Messages.ERROR_UNDEFINED_ERROR, new String[] { pe.getMessage() },
						Locale.getDefault());
						
				logger.error(logString);
				throw pe;
			}
		} catch (ParseException pe) {
			logString = messageSource.getMessage(
					CST30000Messages.ERROR_INVALID_DATE_FORMAT, new String[] { 
							"Date parameter", CST30000Constants.DATE_STRING_SCREEN_FORMAT },
					Locale.getDefault());
			
			logger.error(logString);
		} catch (Exception e) {
			Throwable t = e.getCause();
			while ((t != null) && !((t instanceof QuerySyntaxException)||(t instanceof QueryException))) {
				t = t.getCause();
			}
			
			if (t instanceof QuerySyntaxException || t instanceof QueryException) {
				logString = messageSource.getMessage(
						CST30000Messages.ERROR_UNSUPPORTED_QUERY_SYNTAX, new String[] { t.getMessage() },
						Locale.getDefault());
				
				logger.error(logString);
				throw new UnsupportedQuerySyntaxException(logString);
			} else {
				logString = messageSource.getMessage(
						CST30000Messages.ERROR_UNDEFINED_ERROR, new String[] { e.getMessage() },
						Locale.getDefault());
						
				logger.error(logString);
				throw e;
			}			
		}
		
		return reportName;
	}
	
	private void listData(List<Object[]> data) throws IOException {
		// row and column index starts at 0
		int currRow = startRow - 1;
		int currCol = startColumn - 1;
		String forDisplay="";
		
		reportName += CST30000Constants.FILE_TYPE_XLSX_EXTENSION;
		String fileFullPath;
		if (filePath.trim().endsWith(File.separator)) {
			fileFullPath = filePath + reportName;
		} else {
			fileFullPath = filePath + File.separator + reportName;
		}
		
		File newFile = new File(fileFullPath);
		FileOutputStream fos = new FileOutputStream(newFile);
		SXSSFWorkbook wb = new SXSSFWorkbook(1000);
		
		try {
			wb.createSheet();
			Sheet ws = wb.getSheetAt(0);
			wb.setSheetName(0, reportTitle);
			Row row = null;
			
			// start: set report title
			XSSFCellStyle titleStyle = setXlsxTitleStyle(wb);
			row = ws.createRow(currRow);
			Cell cell = row.createCell(currCol);
			cell.setCellStyle(titleStyle);
			cell.setCellValue(reportTitle);
			// end: set report title
			
			// start: set report criteria
			if (criteria != null && criteria.length != 0) {
				XSSFCellStyle criteriaStyle = setXlsxCriteriaStyle(wb);
				
				for (int i = 0; i < criteria.length; i++) {
					row = ws.createRow(++currRow);
					cell = row.createCell(currCol);
					cell.setCellStyle(criteriaStyle);
					cell.setCellValue(criteria[i]);
				}
			}
			// end: set report criteria

			// start: set column headers
			XSSFCellStyle headersStyle = setXlsxHeadersStyle(wb);
			row = ws.createRow(++currRow);
			for (int k=0; k<columnNames.length; k++) {
				cell = row.createCell(currCol + k);
				cell.setCellStyle(headersStyle);
				cell.setCellValue(columnNames[k]);
				
				// set column width
				if (k!=4){
					ws.setColumnWidth(currCol + k, (columnNames[k].length() + 5) * 256);
				}else{
					ws.setColumnWidth(currCol + k, 100 * 256);
				}
			}
			// end: set column headers
			
			// start: set data
			XSSFCellStyle dataStyle = setXlsxDataStyle(wb);
			
			DataFormat dtFormat = wb.createDataFormat();
			XSSFCellStyle dtStyle = setXlsxDataStyle(wb);
			dtStyle.setDataFormat(dtFormat.getFormat(dateTimeFormat));
			
			for (final Object object : data) {
				// for single column records, convert it in single object array.
				final Object[] objectArr = object instanceof Object[] ? (Object[]) object : new Object[] { object};
				
				// if object array is of type Character array(CLOB), convert to Object[][] data.
				Object[] obj = objectArr instanceof Character[] ? new Object[] { objectArr}: objectArr;
				
				// if object array is of type Byte array(BLOB), convert to Object[][] data. cell will take care of writing such data.
				obj = objectArr instanceof Byte[] ? new Object[] { objectArr }: obj;
				
				row = ws.createRow(++currRow);
				for (int l = 0; l< columnNames.length; l++) {
					cell = row.createCell(currCol + l);

					if (obj[l] != null && obj[l].toString().length() > 0) {
						if (Strings.isNullOrEmpty(columnTypes[l])) {
							forDisplay = convertDisplay((obj[l]).toString(), l);
							
							cell.setCellValue(forDisplay);
							cell.setCellStyle(dataStyle);
						} else if (columnTypes[l].equalsIgnoreCase(DATATYPE_DATE)) {
							obj[l] = new SimpleDateFormat(dateTimeFormat).format((obj[l]));
							
							cell.setCellValue((obj[l]).toString());
							cell.setCellStyle(dtStyle);
						} else if (columnTypes[l].equalsIgnoreCase(DATATYPE_NUM)) {
							String numPattern = getNumberPattern(l);
							
							cell.setCellValue(new DecimalFormat(numPattern).format(Double.valueOf((obj[l]).toString())));
							cell.setCellStyle(dataStyle);
						} else if (columnTypes[l].equalsIgnoreCase(DATATYPE_BLOB)) {
							cell.setCellValue(CONST_DATATYPE_BLOB);
							cell.setCellStyle(dataStyle);
						} else if (columnTypes[l].equalsIgnoreCase(DATATYPE_CLOB)) {
							cell.setCellValue(CONST_DATATYPE_CLOB);
							cell.setCellStyle(dataStyle);
						}
					} else {
						cell.setCellStyle(dataStyle);
					}
				}
			}
			wb.write(fos);
		} catch (Throwable ex) {
			if (newFile.isFile()) {
				newFile.delete();
			}
			throw ex;
		} finally {
			fos.close();			
		}
	}
	
	private String convertDisplay(String code, int colNumber){
		String forDisplay="";
		
		if (colNumber==1){
	        if (!Strings.isNullOrEmpty(code)) {
	            if (code.equalsIgnoreCase(CST30000Constants.LOG_STATUS_START)) {
	            	forDisplay = CST30000Constants.STRING_UPPER_START;
	            } else if (code.equalsIgnoreCase(CST30000Constants.LOG_STATUS_PROCESSING)) {
	            	forDisplay = CST30000Constants.STRING_UPPER_PROCESSING;
	            } else if (code.equalsIgnoreCase(CST30000Constants.LOG_STATUS_END)) {
	            	forDisplay = CST30000Constants.STRING_UPPER_END;
	            }
	        }
		} else if (colNumber==2){
	        if (!Strings.isNullOrEmpty(code)) {
	            if (code.equalsIgnoreCase(CST30000Constants.LOG_TYPE_INF)) {
	            	forDisplay = CST30000Constants.STRING_INFO;
	            } else if (code.equalsIgnoreCase(CST30000Constants.LOG_TYPE_WRN)) {
	            	forDisplay = CST30000Constants.STRING_WARNING;
	            } else if (code.equalsIgnoreCase(CST30000Constants.LOG_TYPE_FTL)) {
	            	forDisplay = CST30000Constants.STRING_FATAL;
	            } else if (code.equalsIgnoreCase(CST30000Constants.LOG_TYPE_ERR)) {
	            	forDisplay = CST30000Constants.STRING_ERROR;
	            }
	        }
		}else{
			forDisplay=code;
		}
		
		
		return forDisplay;
	}
	
}