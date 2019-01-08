/******************************************************
 * Program History
 * 
 * Project Name	            :  st-server-webapp-template
 * Client Name				:  TMAP-EM
 * Package Name             :  th.co.toyota.application.service
 * Program ID 	            :  CST33010LogMonitoringService.java
 * Program Description	    :  <put description>
 * Environment	 	        :  Java 7
 * Author					:  danilo
 * Version					:  1.0
 * Creation Date            :  Sep 2, 2013
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2015-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/
package th.co.toyota.application.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFBorderFormatting;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.datetime.joda.DateTimeFormatterFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;

import th.co.toyota.application.repository.IST33010LogMonitoringRepository;
import th.co.toyota.application.util.CST33010LogMonitoringExcel;
import th.co.toyota.st3.api.constants.CST30000Constants;
import th.co.toyota.st3.api.exception.EntityMappingException;
import th.co.toyota.st3.api.exception.LogsDoesNotExistsException;
import th.co.toyota.st3.api.exception.NoDataFoundException;
import th.co.toyota.st3.api.exception.UnableToCreateExcelForDowloadException;
import th.co.toyota.st3.api.exception.UnsupportedQuerySyntaxException;
import th.co.toyota.st3.api.model.LogInfo;
import th.co.toyota.st3.api.model.ModuleHeaderInfo;

/**
 * Service implementation for log monitoring screen.
 * 
 * @author danilo
 * 
 */
@Service
@Component
public class CST33010LogMonitoringService implements IST33010LogMonitoringService {
	final Logger logger = LoggerFactory.getLogger(CST33010LogMonitoringService.class);

	@Autowired
	protected IST33010LogMonitoringRepository repository;

	@Value("${templateLogMonitoring}")
	private String templateLogMonitoring;
	
	@Value("${report.onlineLimitation:0}")
	protected int excelLimitRecord;
	
	@Value("${default.download.folder}")
	protected String sharedFolder;

	@Autowired
	@Qualifier("CST33010LogMonitoringExcel")
	protected CST33010LogMonitoringExcel generator;

	private DateTimeFormatter dtFmt;
	
	public CST33010LogMonitoringService() {
		dtFmt = new DateTimeFormatterFactory(
				CST30000Constants.DATE_TIME_STRING_SCREEN_FORMAT)
				.createDateTimeFormatter();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.service.ISTD3010Service#queryModules()
	 */
	@Override
	public List<ModuleHeaderInfo> queryModules() {
		return repository.queryModules();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * th.co.toyota.application.service.ISTD3010Service#searchLogSummary(th.co.
	 * th.co.toyota.application.model.common.LogInfo, java.util.Date, java.util.Date)
	 */
	@Override
	public List<List<LogInfo>> searchLog(LogInfo logInfo, Date dateFrom,
			Date dateTo) throws LogsDoesNotExistsException {
		logger.info("START: Searching log information");
		
		List<LogInfo> list = repository.queryLog(logInfo, dateFrom,
				dateTo);

		if (list.isEmpty()) {
			logger.info("END: Log information returned no results.");
			throw new LogsDoesNotExistsException();
		}
		
		logger.info("Log information returned: {}", list.size());
		
		Map<String, List<LogInfo>> map = new HashMap<String, List<LogInfo>>();
		for (LogInfo log : list) {
			List<LogInfo> listLog = map.get(log.getAppId());
			if (listLog == null) {
				listLog = new ArrayList<LogInfo>();
				map.put(log.getAppId(), listLog);
			}
			listLog.add(log);
		}
		
		ArrayList<List<LogInfo>> groupedLogs = new ArrayList<List<LogInfo>>();
		
		if (logInfo.isLogDetail()) {
			groupedLogs.addAll(map.values());
		} else {
			for (List<LogInfo> tmp : map.values()) {
				List<LogInfo> gl = new ArrayList<LogInfo>();
				gl.add(tmp.get(0));
				if (tmp.size() > 1) {
					gl.add(tmp.get(tmp.size()-1));
				}
				groupedLogs.add(gl);
			}
		}
	
		map.clear();
		map = null;
		groupedLogs.trimToSize();
		
		logger.info("END: Grouped Log information to return {}", groupedLogs.size());

		return groupedLogs;
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * th.co.toyota.application.service.ISTD3010Service#searchLogSummary(th.co.
	 * th.co.toyota.application.model.common.LogInfo, java.util.Date, java.util.Date)
	 */
	@Override
	public List<List<String[]>> searchLogDisplay(LogInfo logInfo, Date dateFrom,
			Date dateTo) throws LogsDoesNotExistsException {
		logger.info("START: Searching log information for Display");
		ArrayList<List<String[]>> groupedLogsDisplay= null;
		
		List<LogInfo> list = repository.queryLog(logInfo, dateFrom,
				dateTo);

		if (list.isEmpty()) {
			logger.info("END: Log information returned no results.");
			throw new LogsDoesNotExistsException();
		}
		
		logger.info("Log information returned: {}", list.size());
		
		Map<String, List<LogInfo>> map = new HashMap<String, List<LogInfo>>();
		for (LogInfo log : list) {
			List<LogInfo> listLog = map.get(log.getAppId());
			if (listLog == null) {
				listLog = new ArrayList<LogInfo>();
				map.put(log.getAppId(), listLog);
			}
			listLog.add(log);
		}
		
		if (logInfo.isLogDetail()) {
			DateTimeFormatter fmt = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss");
			
			groupedLogsDisplay=new ArrayList<List<String[]>>();
			for (List<LogInfo> group:map.values()){
				List<String[]> logDisplay = new ArrayList<String[]>(); 
				for (LogInfo log: group){
					
					logDisplay.add(new String[]{
							fmt.print(log.getCreateDate().getTime())
							, log.getAppId()
						    , (log.getModuleId()) + '-' + (log.getModuleName())
						    , (log.getFunctionId()) + '-' +  (log.getFunctionName())
						    , log.getDisplayStatus()
						    , log.getCreateBy()
						    , log.getDisplayMessageType()
						    , log.getMessage()
					});
				}
				groupedLogsDisplay.add(logDisplay);
			}
			
		} 
	
		map.clear();
		map = null;
		groupedLogsDisplay.trimToSize();
		
		logger.info("END: Grouped Log information to return {}", groupedLogsDisplay.size());

		return groupedLogsDisplay;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * th.co.toyota.application.service.ISTD3010Service#listRolesToExcel(th.co.
	 * th.co.toyota.application.model.common.LogInfo, java.util.Date, java.util.Date)
	 */
	@Override
	public HSSFWorkbook listRolesToExcelXLS(LogInfo logInfo, Date dateFrom,
			Date dateTo) throws LogsDoesNotExistsException,
			UnableToCreateExcelForDowloadException {
		List<LogInfo> logs = repository.queryLog(logInfo, dateFrom,
				dateTo);

		InputStream is = null;
		HSSFWorkbook wb = null;

		try {
			is = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(templateLogMonitoring);

			wb = new HSSFWorkbook(is);

			HSSFSheet ws = wb.getSheetAt(0);
			CellStyle csBorder = ws.getWorkbook().createCellStyle();
			int startRow = 3;
			for (LogInfo log : logs) {
				int startCell = 0;
				HSSFRow row = null;

				row = createRow(ws, startRow++, csBorder);
				writeLogInfo(row, log, startCell);
			} // for (LogInfo log : logs) {
		} catch (FileNotFoundException fe) {
			logger.error("Unable to read the template excel file: {}",
					templateLogMonitoring);
			throw new UnableToCreateExcelForDowloadException();
		} catch (IOException e) {
			logger.error(
					"Unable to create excel file from template using POI.", e);
			throw new UnableToCreateExcelForDowloadException();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					logger.error("Unable to close input stream.", e);
				}
			}
		}

		return wb;
	}
	
	@Override
	public String listRolesToExcelXLSX(LogInfo logInfo, Date dateFrom,
			Date dateTo, String reportName) throws LogsDoesNotExistsException,
			UnableToCreateExcelForDowloadException, EntityMappingException,
			ClassNotFoundException, NoDataFoundException,
			UnsupportedQuerySyntaxException, IOException {
		
		DateTime dt = DateTime.now();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		
		HashMap<String,List<Object>> hmQueryAndParams = getQueryAndParams(logInfo, dateFrom,
				dateTo);
		
		generator.setMaxRowsPerPage(excelLimitRecord);
		generator.setReportName(reportName + "_" + format.format(dt.toDate()));
		generator.setDateTimeFormat(CST30000Constants.DATE_TIME_STRING_SCREEN_FORMAT);
		generator.setReportTitle(reportName);
		generator.setDisplayNames(getDisplayName());
		//excelGenerator.setCriteria(null);
		//excelGenerator.setOverridePath(overridePath);
		generator.setFileType(CST30000Constants.FILE_TYPE_XLSX);
		generator.setSharedFolder(sharedFolder);
		generator.setStartRow(1);
		generator.setStartColumn(1);
		generator.setFirstResult(0);
		
		String excelName = generator.createExcel(
				hmQueryAndParams.keySet().toArray()[0].toString(), 
				((ArrayList<Object>) hmQueryAndParams.values().toArray()[0]).toArray(),
				"th.co.toyota.st3.api.model.LogInfo");
		
		return excelName;
	}

	/**
	 * Create a new row to display the next log details.
	 * 
	 * @param ws A WorkSheet.
	 * @param startRow A row index
	 * @return A excel row {@link HSSFRow}
	 */
	private HSSFRow createRow(HSSFSheet ws, int startRow, CellStyle csBorder) {
		HSSFRow row = ws.createRow(startRow);

		// pre-create the cell
		//CellStyle csBorder = ws.getWorkbook().createCellStyle();
		csBorder.setBorderLeft(HSSFBorderFormatting.BORDER_THIN);
		csBorder.setBorderRight(HSSFBorderFormatting.BORDER_THIN);
		csBorder.setBorderTop(HSSFBorderFormatting.BORDER_THIN);
		csBorder.setBorderBottom(HSSFBorderFormatting.BORDER_THIN);

		for (int i = 0; i < 5; i++) {
			row.createCell(i).setCellStyle(csBorder);
		}

		return row;
	}

	/**
	 * Write the log details in newly created row
	 * 
	 * @param row
	 * @param log
	 * @param startCell
	 *            cell index
	 * @return a next cell index.
	 */
	private int writeLogInfo(HSSFRow row, LogInfo log, int startCell) {
		row.getCell(startCell++).setCellValue(
				new DateTime(log.getCreateDate()).toString(dtFmt));
		row.getCell(startCell++).setCellValue(log.getDisplayStatus());
		row.getCell(startCell++).setCellValue(log.getDisplayMessageType());
		row.getCell(startCell++).setCellValue(log.getCreateBy());
		row.getCell(startCell++).setCellValue(log.getMessage());

		return startCell;
	}
	
	private HashMap<String,List<Object>> getQueryAndParams(LogInfo logInfo, Date dateFrom,
			Date dateTo) {
		
		HashMap<String,List<Object>> hmQueryAndParams=new HashMap<String,List<Object>>();
		
		StringBuilder query = new StringBuilder();
		StringBuilder queryParams = new StringBuilder();
		List<Object> objParams = new ArrayList<Object>();
		
		if (!Strings.isNullOrEmpty(logInfo.getModuleId())){
			queryParams.append(" UPPER(logInfo.moduleId)=?");
			objParams.add(logInfo.getModuleId().toUpperCase());
		}
		
		if (!Strings.isNullOrEmpty(logInfo.getFunctionId())){
			queryParams.append(queryParams.toString().equalsIgnoreCase("")?"":" and");
			queryParams.append(" UPPER(logInfo.functionId)=?");
			objParams.add(logInfo.getFunctionId().toUpperCase());
		}
		
		if (!Strings.isNullOrEmpty(logInfo.getCreateBy())){
			queryParams.append(queryParams.toString().equalsIgnoreCase("")?"":" and");
			queryParams.append(" UPPER(logInfo.createBy)=?");
			objParams.add(logInfo.getCreateBy().toUpperCase());
		}
		
		if (!Strings.isNullOrEmpty(logInfo.getAppId())){
			queryParams.append(queryParams.toString().equalsIgnoreCase("")?"":" and");
			queryParams.append(" UPPER(logInfo.appId)=?");
			objParams.add(logInfo.getAppId().toUpperCase());
		}
		
		if (!Strings.isNullOrEmpty(logInfo.getStatus())){
			queryParams.append(queryParams.toString().equalsIgnoreCase("")?"":" and");
			queryParams.append(" UPPER(logInfo.status)=?");
			objParams.add(logInfo.getStatus().toLowerCase());
		}
		
		if (!Strings.isNullOrEmpty(logInfo.getMessageType())){
			queryParams.append(queryParams.toString().equalsIgnoreCase("")?"":" and");
			queryParams.append(" UPPER(logInfo.messageType)=?");
			objParams.add(logInfo.getMessageType().toUpperCase());
		}
		
		 if (dateFrom!=null){
			queryParams.append(queryParams.toString().equalsIgnoreCase("")?"":" and");
			queryParams.append(" logInfo.createDate>=?");
			objParams.add(dateFrom);
		 }
		
		 if (dateTo!=null){
			queryParams.append(queryParams.toString().equalsIgnoreCase("")?"":" and");
			queryParams.append(" logInfo.createDate<?");
			
			Calendar c = Calendar.getInstance();
			c.setTime(dateTo);
			c.add(Calendar.DATE, 1); 
			objParams.add(c.getTime());
		 }
		
		query.append("select logInfo.createDate, logInfo.status, logInfo.messageType,logInfo.createBy,logInfo.message ");
		query.append(" from LogInfo as logInfo ");
		
		query.append(queryParams.toString().equalsIgnoreCase("")?"":" where "+queryParams.toString());
		query.append(" order by logInfo.createDate desc, logInfo.seqNo desc");
		
		hmQueryAndParams.put(query.toString(), objParams);
		return hmQueryAndParams;
	}
	
	private HashMap<String,String> getDisplayName(){
		
		HashMap<String,String> hm = new HashMap<String,String>();
		
		hm.put("D_HODTCRE", "Date Time");
		hm.put("V_STATUS", "Status");
		hm.put("V_MESSAGE_TYPE", "Level");
		hm.put("V_USERCRE", "User ID");
		hm.put("V_MESSAGE", "Message");
		
		return hm;
	}

	@Override
	public int searchHeaderCount(LogInfo logInfo, Date dateFrom, 
			Date dateTo) throws LogsDoesNotExistsException {
		logger.info("START: Searching log information");
		
		List<Integer> appList = repository.queryLogGroup(logInfo, dateFrom,
				dateTo, 0, 0);

		if (appList.isEmpty()) {
			logger.info("END: Log information returned no results.");
			throw new LogsDoesNotExistsException();
		}
		
		logger.info("Log information returned: {}", appList.size());

		// START - bug fix - startDate and endDate display is equal
//		return appList.size();
		return appList.size()/2;
		// END - bug fix - startDate and endDate display is equal
	}

	@Override
	public List<List<LogInfo>> searchHeaderLog(LogInfo logInfo, Date dateFrom, 
			Date dateTo, int firstResult, int rowsPerPage) 
					throws LogsDoesNotExistsException {
		logger.info("START: Searching log information");
		
		// query 20(min & max n_seq_no) v_apl_id list based from pagination
		List<Integer> appList = repository.queryLogGroup(logInfo, dateFrom,
				dateTo, firstResult, rowsPerPage);

		if (appList.isEmpty()) {
			logger.info("END: Log information returned no results.");
			throw new LogsDoesNotExistsException();
		}
		
		logger.info("Log information returned: {}", appList.size());
		
		// query header based from v_apl_id list from above
		List<LogInfo> list = repository.queryHeaderLog(logInfo, dateFrom,
				dateTo, appList);
		// START - bug fix on sorted list from DB becomes unsorting if using Map
//		Map<String, List<LogInfo>> map = new HashMap<String, List<LogInfo>>();
		LinkedHashMap<String, List<LogInfo>> map = new LinkedHashMap<String, List<LogInfo>>();
		// END - bug fix on sorted list from DB becomes unsorting if using Map
		for (LogInfo log : list) {
			List<LogInfo> listLog = map.get(log.getAppId());
			if (listLog == null) {
				listLog = new ArrayList<LogInfo>();
				map.put(log.getAppId(), listLog);
			}
			listLog.add(log);
		}
		
		ArrayList<List<LogInfo>> groupedLogs = new ArrayList<List<LogInfo>>();
		
		if (logInfo.isLogDetail()) {
			groupedLogs.addAll(map.values());
		} else {
			for (List<LogInfo> tmp : map.values()) {
				List<LogInfo> gl = new ArrayList<LogInfo>();
				gl.add(tmp.get(0));
				if (tmp.size() > 1) {
					gl.add(tmp.get(tmp.size()-1));
				}
				groupedLogs.add(gl);
			}
		}
	
		map.clear();
		map = null;
		groupedLogs.trimToSize();
		
		logger.info("END: Grouped Log information to return {}", groupedLogs.size());

		return groupedLogs;
	}
	
	@Override
	public int getLogDetailCount(LogInfo logInfo, Date dateFrom, Date dateTo) 
			throws LogsDoesNotExistsException {
		logger.info("START: Searching log information for Display");

		int result = repository.queryDetailCount(logInfo, dateFrom,
				dateTo);
		
		if (result == 0) {
			logger.info("END: Log information returned no results.");
			throw new LogsDoesNotExistsException();
		}
		
		logger.info("Log information returned: {}", result);
		
		return result;
	}

	@Override
	public List<List<String[]>> searchLogDisplay(LogInfo logInfo, Date dateFrom, 
			Date dateTo, int firstResult, int rowsPerPage) 
					throws LogsDoesNotExistsException {
		logger.info("START: Searching log information for Display");
		ArrayList<List<String[]>> groupedLogsDisplay = 
				new ArrayList<List<String[]>>();
		
		List<LogInfo> list = repository.queryDetailLog(logInfo, dateFrom,
				dateTo, firstResult, rowsPerPage);

		if (list.isEmpty()) {
			logger.info("END: Log information returned no results.");
			throw new LogsDoesNotExistsException();
		}
		
		logger.info("Log information returned: {}", list.size());
		
		Map<String, List<LogInfo>> map = new HashMap<String, List<LogInfo>>();
		DateTimeFormatter fmt = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss");
		for (LogInfo log : list) {
			List<LogInfo> listLog = map.get(log.getAppId());
			if (listLog == null) {
				listLog = new ArrayList<LogInfo>();
				map.put(log.getAppId(), listLog);
			}
			listLog.add(log);
			
			if (logInfo.isLogDetail()) {
				List<String[]> logDisplay = new ArrayList<String[]>();
				logDisplay.add(new String[]{
						fmt.print(log.getCreateDate().getTime())
						, log.getAppId()
					    , (log.getModuleId()) + '-' + (log.getModuleName())
					    , (log.getFunctionId()) + '-' +  (log.getFunctionName())
					    , log.getDisplayStatus()
					    , log.getCreateBy()
					    , log.getDisplayMessageType()
					    , log.getMessage()
				});
				groupedLogsDisplay.add(logDisplay);
			}
			
		}
		
		/*
		if (logInfo.isLogDetail()) {
			DateTimeFormatter fmt = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss");
			
			groupedLogsDisplay=new ArrayList<List<String[]>>();
			for (List<LogInfo> group:map.values()){
				List<String[]> logDisplay = new ArrayList<String[]>(); 
				for (LogInfo log: group){
					
					logDisplay.add(new String[]{
							fmt.print(log.getCreateDate().getTime())
							, log.getAppId()
						    , (log.getModuleId()) + '-' + (log.getModuleName())
						    , (log.getFunctionId()) + '-' +  (log.getFunctionName())
						    , log.getDisplayStatus()
						    , log.getCreateBy()
						    , log.getDisplayMessageType()
						    , log.getMessage()
					});
				}
				groupedLogsDisplay.add(logDisplay);
			}
			
		} 
		*/
	
		map.clear();
		map = null;
		groupedLogsDisplay.trimToSize();
		
		logger.info("END: Grouped Log information to return {}", groupedLogsDisplay.size());

		return groupedLogsDisplay;
	}
	
}
