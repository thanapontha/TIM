/******************************************************
 * Program History
 * 
 * Project Name	            :  GWRDS : Getsudo Worksheet Rundown System
 * Client Name				:  TDEM
 * Package Name             :  th.co.toyota.bw0.web.report.main
 * Program ID 	            :  CBW04221CalendarDownload.java
 * Program Description	    :  <put description>
 * Environment	 	        :  Java 7
 * Author					:  Thanawut T.
 * Version					:  1.0
 * Creation Date            :  August 28, 2017
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2017-Toyota Daihatsu Engineering & Manufacturing Co., Ltd. All Rights Reserved.    
 ********************************************************/
package th.co.toyota.bw0.batch.report.main;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;

import th.co.toyota.bw0.api.common.CBW00000CommonPOI;
import th.co.toyota.bw0.api.common.CBW00000Util;
import th.co.toyota.bw0.api.constants.AppConstants;
import th.co.toyota.bw0.api.constants.MessagesConstants;
import th.co.toyota.bw0.api.exception.common.CommonErrorException;
import th.co.toyota.bw0.api.repository.common.IBW00000Repository;
import th.co.toyota.bw0.api.repository.common.IBW03060Repository;
import th.co.toyota.bw0.batch.report.repository.IBW04221Repository;
import th.co.toyota.bw0.util.FormatUtil;
import th.co.toyota.st3.api.constants.CST30000Constants;
import th.co.toyota.st3.api.constants.CST30000Messages;
import th.co.toyota.st3.api.util.IST30000LoggerDb;

import com.google.common.base.Strings;

public class CBW04221CalendarDownload {

	final Logger logger = LoggerFactory.getLogger(CBW04221CalendarDownload.class);
	protected IST30000LoggerDb loggerBBW04221;
	protected MessageSource messageSource;
	protected IBW03060Repository systemRepository;
	protected IBW00000Repository commonRepository;
	protected IBW04221Repository repository;
	protected CBW00000CommonPOI poi;
	protected String downloadFolder;

	private SXSSFWorkbook swb = null;
	private final String reportId = "LBW04221";
	private String reportName = "";
	
	private String version = "";
	private String getsudoMonth = "";
	private String timing = "";
	private String appId;
	private String userLog = AppConstants.SYSTEM_NAME;
	
	protected String dateFormatPattern = "dd/MM/yyyy HH:mm:ss"; 
    protected DateFormat datef = new SimpleDateFormat(dateFormatPattern); 
    
    private static final String STYLE_HEADER_TITLE = "header_title";
    private static final String STYLE_HEADER_LABEL = "header_label";
    private static final String STYLE_HEADER_VALUE = "header_value";
    
    private static final String STYLE_HEADER_PLANT = "header_plant";
    private static final String STYLE_HEADER_V_PLANT = "header_vehicle_plant";
    private static final String STYLE_HEADER_U_PLANT = "header_unit_plant";
    
    private static final String STYLE_CALENDAR_SAT_SUN = "calendar_sat_sun"; //Gray
    private static final String STYLE_CALENDAR1 = "calendar1"; //Yellow
    private static final String STYLE_CALENDAR2 = "calendar2"; //Red
    
    private static final String STYLE_CALENDAR_DATE_SAT_SUN = "calendar_dt_sat_sun"; //Gray
    private static final String STYLE_CALENDAR_DATE1 = "calendar_dt1"; //Yellow
    private static final String STYLE_CALENDAR_DATE2 = "calendar_dt2"; //Red
    
    private static final int STYLE_1 = 1;
    private static final int STYLE_2 = 2;
    private static final int STYLE_SAT_SUN = 3;

    private static final String VEHICLE_LABEL = "Vehicle Plant";
    private static final String UNIT_LABEL = "Unit Plant";

    private Map<String, CellStyle> styles = null;
	
	public List<String> generateReport(String[] args) throws CommonErrorException {
		String message = AppConstants.BLANK;
		List<String> excelFileList = new ArrayList<>();
		String batchName = "Calendar Download";
		int status = CST30000Constants.ERROR;
		Connection conn = null;
		try {
			if(args != null && args.length == 5) {
				this.version = CBW00000Util.toString(args[0]);
				this.getsudoMonth = CBW00000Util.toString(args[1]);
				this.timing = CBW00000Util.toString(args[2]);
				this.userLog = CBW00000Util.toString(args[3]);
				this.appId = CBW00000Util.toString(args[4]);						
			}
			
			//Add by Thanawut T. 2017/12/12 BCT-Vendor for detect download processing
			commonRepository.updateStatusOfLogUpload(appId, userLog, AppConstants.STATUS_PROCESSING, AppConstants.STATUS_PROCESSING_DESC+" Download Calendar.");
			
			String msg = messageSource.getMessage(CST30000Messages.INFO_PROCESS_START,	new String[] { batchName }, Locale.getDefault());
			logger.info(msg);
			loggerBBW04221.start(appId, CST30000Messages.INFO_PROCESS_START, msg, userLog);
			
			conn = commonRepository.getConnection();
			
			if(this.loadConfigForGenerateReport(conn)){
	         
				XSSFWorkbook xssfWorkbook = new XSSFWorkbook();
			    xssfWorkbook.createSheet("Data");
  		        swb = new SXSSFWorkbook(xssfWorkbook);
			    styles = createStyles(); 
			    Sheet sh  = xssfWorkbook.getSheetAt(0);
			   
	            // General setup
	            sh.setDisplayGridlines(false);
	            sh.setPrintGridlines(false);
	            sh.setFitToPage(true);
	            sh.setHorizontallyCenter(true);
	            
	            PrintSetup printSetup = sh.getPrintSetup();
	            printSetup.setLandscape(true);
	            
		        poi.protectedSheet(sh);
	            int iRow = 0; 

	            // Create data rows
                List<String> vehiclePlantList = commonRepository.getVehiclePlantMaster(conn, this.getsudoMonth);
                List<String> unitPlantList = commonRepository.getUnitPlantMaster(conn, this.getsudoMonth);
                String vehiclePlantStr = String.join("','", vehiclePlantList); 
                String unitPlantStr = String.join("','", unitPlantList);
                vehiclePlantStr= (vehiclePlantStr!=null && !"".equals(vehiclePlantStr)) ? "'"+vehiclePlantStr+"'": "";
                unitPlantStr= (unitPlantStr!=null && !"".equals(unitPlantStr)) ? "'"+unitPlantStr+"'": "";
                String plantCondition = "'"+AppConstants.COMPANY_CD_TMAP_MS+"','"+AppConstants.COMPANY_CD_TDEM+"'";
                if(!"".equals(vehiclePlantStr)) {
                	plantCondition = plantCondition + "," + vehiclePlantStr;
                }
                if(!"".equals(unitPlantStr)) {
                	plantCondition = plantCondition + "," + unitPlantStr;
                }
                
	            // Create header
	            Row header = sh.createRow(iRow++); // Row 0
	            header.setHeightInPoints(15.75f);
	            Cell cellTitle = header.createCell(0);
	            cellTitle.setCellStyle(styles.get(STYLE_HEADER_TITLE));
	            cellTitle.setCellValue("Calendar Upload Template");
                
	            List<Object[]> headerLs = new ArrayList<>();
	            headerLs.add(new Object[]{"Getsudo Month :", this.getsudoMonth});
	            headerLs.add(new Object[]{"Timing :", this.timing});
	            
	            String[] sections = new String[]{STYLE_HEADER_LABEL, STYLE_HEADER_VALUE};
	            iRow++; // Row 1
	            iRow = generateHeader(sh, headerLs, iRow, sections.length, sections); // Row 2-3
                
                int iColHeader = 1; //Start at cell B
                
                iRow = 5;
                header = sh.createRow(iRow);
                header.setHeightInPoints(11.25f);
                if(!vehiclePlantList.isEmpty()){
                	if(vehiclePlantList.size() > 1){
                		poi.setMergeCellWithStyle(sh, iRow, iRow, 3, vehiclePlantList.size() + 2, VEHICLE_LABEL , 
    	                		styles.get(STYLE_HEADER_V_PLANT));
                    }else{
                    	Cell cell = header.createCell(3);
                        cell.setCellStyle(styles.get(STYLE_HEADER_V_PLANT));                
                        cell.setCellValue(VEHICLE_LABEL);
                    }
	                
                }
                if(!unitPlantList.isEmpty()){
                	if(unitPlantList.size() > 1){
                		poi.setMergeCellWithStyle(sh, iRow, iRow, vehiclePlantList.size() + 3, vehiclePlantList.size() + 2 + unitPlantList.size(), UNIT_LABEL , 
    	                		styles.get(STYLE_HEADER_U_PLANT));
                    }else{
                    	Cell cell = header.createCell(vehiclePlantList.size() + 3);
                        cell.setCellStyle(styles.get(STYLE_HEADER_U_PLANT));                
                        cell.setCellValue(UNIT_LABEL);
                    }
	                
                }
                
                iRow = 6;
                Row row6 = sh.createRow(iRow);
                row6.setHeightInPoints(11.25f);
                Cell cellHead = row6.createCell(iColHeader);
                cellHead.setCellStyle(styles.get(STYLE_HEADER_PLANT));
                cellHead.setCellValue(AppConstants.COMPANY_CD_TMAP_MS);
                sh.setColumnWidth(iColHeader, 3500);
                iColHeader++;
				
                cellHead = row6.createCell(iColHeader);
                cellHead.setCellStyle(styles.get(STYLE_HEADER_PLANT));
                cellHead.setCellValue(AppConstants.COMPANY_CD_TDEM);
                iColHeader++;
                
				
                for(int i=0;i<vehiclePlantList.size();i++){
                	String value = vehiclePlantList.get(i); 
                	Cell cell = row6.createCell(iColHeader+i);
					cell.setCellStyle(styles.get(STYLE_HEADER_PLANT));
					cell.setCellValue(value);
					
                }
                iColHeader+=vehiclePlantList.size();
                
                for(int i=0;i<unitPlantList.size();i++){
                	String value = unitPlantList.get(i); 
                	Cell cell = row6.createCell(iColHeader+i);
					cell.setCellStyle(styles.get(STYLE_HEADER_PLANT));
					cell.setCellValue(value);
					
                }
                iColHeader+=unitPlantList.size();
                iRow++;
                //end Create header
                
                List<Object[]> reportDatas = repository.searchObject(conn, this.getsudoMonth, this.timing, plantCondition);
					
                int calendarStyleFlag = 1;
				if(reportDatas != null && !reportDatas.isEmpty()) {
					
		            for (int dataIdx = 0; dataIdx < reportDatas.size(); dataIdx++) {
	                    Object[] values = reportDatas.get(dataIdx);
	                    
	                    String calendarDtStr = (String)values[0];

	                    calendarStyleFlag = this.getCalendarStyleFlag(calendarDtStr);
	                  
            			Row rowData = sh.createRow(iRow++);
             			rowData.setHeightInPoints(11.25f);
             			
             			 //Set calendar date to Excel
             			Object calDtObj = values[0];	
            			Cell calDtObjCell = rowData.createCell(0);
            			if(calendarStyleFlag==STYLE_SAT_SUN){
            				calDtObjCell.setCellStyle(styles.get(STYLE_CALENDAR_DATE_SAT_SUN));
            			}else if(calendarStyleFlag==STYLE_1){
            				calDtObjCell.setCellStyle(styles.get(STYLE_CALENDAR_DATE1));
            			}else if(calendarStyleFlag==STYLE_2){
            				calDtObjCell.setCellStyle(styles.get(STYLE_CALENDAR_DATE2));
            			}
            			calDtObjCell.setCellValue((String)calDtObj);
            			
            			//Set calendar flag to Excel
             			for (int dcolnum = 2; dcolnum < values.length; dcolnum++) {
             				Object value = values[dcolnum];	
                			Cell cell = rowData.createCell(dcolnum-1);
                			if(calendarStyleFlag==STYLE_SAT_SUN){
                				cell.setCellStyle(styles.get(STYLE_CALENDAR_SAT_SUN));
                			}else if(calendarStyleFlag==STYLE_1){
                				cell.setCellStyle(styles.get(STYLE_CALENDAR1));
                			}else if(calendarStyleFlag==STYLE_2){
                				cell.setCellStyle(styles.get(STYLE_CALENDAR2));
                			}
                			cell.setCellValue((String)value);
             			}
		            }
				}
				
				//Set column width
				int iSetwidthCol = 0;
				sh.setColumnWidth(iSetwidthCol++, 3500);
				sh.setColumnWidth(iSetwidthCol++, 3500);
				sh.setColumnWidth(iSetwidthCol++, 3500);
				for(int i=0;i<vehiclePlantList.size();i++){
                	sh.setColumnWidth(iSetwidthCol++, 3500);
                }
                for(int i=0;i<unitPlantList.size();i++){
                	sh.setColumnWidth(iSetwidthCol++, 3500);
                }
                
				//write excel file
				this.writeFileReport(excelFileList);
				status = CST30000Constants.SUCCESS;
			}
		} catch (CommonErrorException e) {
			message = messageSource.getMessage(e.getMessageCode(),e.getMessageArg(), Locale.getDefault());
			logger.error(message);		
			loggerBBW04221.error(appId, e.getMessageCode(), message, this.userLog);
			status = CST30000Constants.ERROR;
		} catch (Exception e) {
			message = messageSource.getMessage(CST30000Messages.ERROR_UNDEFINED_ERROR, new String[] { CBW00000Util.genMessageOfException(e) }, Locale.getDefault());
			logger.error(message);
			loggerBBW04221.error(appId, CST30000Messages.ERROR_UNDEFINED_ERROR, message, this.userLog);
			status = CST30000Constants.ERROR;
		}finally{
			if (status == CST30000Constants.ERROR) {
				message = messageSource.getMessage(CST30000Messages.INFO_PROCESS_END_ERROR, new String[] {batchName, "", "", "" }, Locale.getDefault());
				logger.info(message);
				loggerBBW04221.endError(appId,CST30000Messages.INFO_PROCESS_END_ERROR, message, this.userLog);
				status = CST30000Constants.ERROR;
				
				//Add by Thanawut T. 2017/12/12 BCT-Vendor for detect download processing
				commonRepository.updateStatusOfLogUpload(appId, userLog, AppConstants.STATUS_ERROR, AppConstants.STATUS_ERROR_DESC+" Download Calendar.");
			} else {
				String msgFileName = "";
				if(excelFileList != null && !excelFileList.isEmpty()){
					msgFileName = "File Name: " + excelFileList.get(0);
				}
				message = messageSource.getMessage(CST30000Messages.INFO_PROCESS_END_SUCCESS, new String[] { batchName, msgFileName, "", "" }, Locale.getDefault());
				logger.info(message);
				loggerBBW04221.end(appId, CST30000Messages.INFO_PROCESS_END_SUCCESS, message, this.userLog);
				status = CST30000Constants.SUCCESS;
				
				//Add by Thanawut T. 2017/12/12 BCT-Vendor for detect download processing
				commonRepository.updateStatusOfLogUpload(appId, userLog, AppConstants.STATUS_SUCCESS, AppConstants.STATUS_SUCCESS_DESC+" Download Calendar.");
			}
			try {
				if(conn!=null && !conn.isClosed()){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return excelFileList;
	}
	
	private int generateHeader(Sheet sh, List<Object[]> headerLs, int startRow, int columnSize, String[] sections){
		int rowIdx = startRow;
		if(headerLs!=null && sections!=null){
			for(Object[] values : headerLs){
				Row row = sh.createRow(rowIdx++);
				row.setHeightInPoints(11.25f);
				for(int columnIdx=0;columnIdx<values.length;columnIdx++){
					Object value = values[columnIdx];
					String styleName = sections[columnIdx];
					Cell cell = row.createCell(columnIdx);					
					this.setCellValue(cell, value, styleName);
				}
			}
		}
		return rowIdx;
	}
	
	private void setCellValue(Cell cell, Object value, String styleName){
		cell.setCellStyle(styles.get(styleName));
		if (value instanceof Short || value instanceof Long || value instanceof Integer || value instanceof BigInteger ) {
            cell.setCellValue(((Number) value).intValue());
        } else if (value instanceof Float || value instanceof Double || value instanceof BigDecimal ) {
        	cell.setCellValue(((Number) value).doubleValue());
        } else if (value instanceof Date) {
            cell.setCellValue((Date) value);
        } else {
            cell.setCellValue((String)value);
        }
	}
	
	 @SuppressWarnings("deprecation")
	private Map<String, CellStyle> createStyles(){ 
	        Map<String, CellStyle> stylesMap = new HashMap<>(); 
	        XSSFCellStyle style; 
	        
	        Font titleFont = swb.createFont(); 
	        titleFont.setFontHeightInPoints((short)12); 
	        titleFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
	        titleFont.setUnderline(Font.U_SINGLE);
	        style = (XSSFCellStyle)swb.createCellStyle(); 
	        style.setAlignment(CellStyle.ALIGN_LEFT); 
	        style.setFont(titleFont); 
	        style.setWrapText(false);
	        style.setBottomBorderColor(IndexedColors.GREY_80_PERCENT.getIndex()); 
	        stylesMap.put(STYLE_HEADER_TITLE, style); 
	        
	        Font headerLabelFont = swb.createFont(); 
	        headerLabelFont.setFontHeightInPoints((short)8);
	        headerLabelFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
	        style = (XSSFCellStyle)swb.createCellStyle(); 
	        style.setAlignment(CellStyle.ALIGN_RIGHT); 
	        style.setFont(headerLabelFont); 
	        style.setWrapText(false); 
	        stylesMap.put(STYLE_HEADER_LABEL, style);
	        
	        Font headerValueFont = swb.createFont(); 
	        headerValueFont.setFontHeightInPoints((short)8);
	        style = (XSSFCellStyle)swb.createCellStyle(); 
	        style.setAlignment(CellStyle.ALIGN_LEFT); 
	        style.setFont(headerValueFont); 
	        style.setWrapText(false);
	        stylesMap.put(STYLE_HEADER_VALUE, style);
	        
	        Font headerPlantFont = swb.createFont(); 
	        headerPlantFont.setFontHeightInPoints((short)8); 
	        headerPlantFont.setColor(IndexedColors.BLACK.getIndex());
	        style = (XSSFCellStyle)swb.createCellStyle();
	        style.setAlignment(CellStyle.ALIGN_CENTER); 
	        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
	        style.setFont(headerPlantFont);
	        this.setBorder(style);
	        style.setWrapText(false);
	        stylesMap.put(STYLE_HEADER_PLANT, style); 
	        
	        Font headerVehiclePlantFont = swb.createFont(); 
	        headerVehiclePlantFont.setFontHeightInPoints((short)8); 
	        headerVehiclePlantFont.setColor(IndexedColors.WHITE.getIndex());
	        style =(XSSFCellStyle) swb.createCellStyle();
	        style.setAlignment(CellStyle.ALIGN_CENTER); 
	        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER); 
	        style.setFillForegroundColor(IndexedColors.BLACK.getIndex());
	        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
	        style.setFont(headerVehiclePlantFont);
	        this.setBorder(style);
	        style.setWrapText(false);
	        stylesMap.put(STYLE_HEADER_V_PLANT, style); 
	        
	        Font headerUnitPlantFont = swb.createFont(); 
	        headerUnitPlantFont.setFontHeightInPoints((short)8); 
	        headerUnitPlantFont.setColor(IndexedColors.WHITE.getIndex());
	        style = (XSSFCellStyle)swb.createCellStyle();
	        style.setAlignment(CellStyle.ALIGN_CENTER); 
	        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER); 
	        style.setFillForegroundColor(new XSSFColor(new java.awt.Color(31,73,125)));
	        style.setFillPattern(CellStyle.SOLID_FOREGROUND); 
	        style.setFont(headerUnitPlantFont);
	        this.setBorder(style);
	        style.setWrapText(false);
	        stylesMap.put(STYLE_HEADER_U_PLANT, style); 
	        
	        Font cal1Font = swb.createFont(); 
	        cal1Font.setFontHeightInPoints((short)8); 
	        cal1Font.setColor(IndexedColors.BLACK.getIndex());
	        style = (XSSFCellStyle)swb.createCellStyle();
	        style.setAlignment(CellStyle.ALIGN_CENTER); 
	        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER); 
	        style.setFillForegroundColor(new XSSFColor(new java.awt.Color(255,255,153)));
	        style.setFillPattern(CellStyle.SOLID_FOREGROUND); 
	        style.setFont(cal1Font); 
	        style.setWrapText(false);
	        style.setLocked(false);
	        this.setBorder(style);
	        stylesMap.put(STYLE_CALENDAR1, style); 
	        
	        Font cal2Font = swb.createFont(); 
	        cal2Font.setFontHeightInPoints((short)8); 
	        cal2Font.setColor(IndexedColors.BLACK.getIndex());
	        style = (XSSFCellStyle)swb.createCellStyle();
	        style.setAlignment(CellStyle.ALIGN_CENTER); 
	        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
	        style.setFillForegroundColor(new XSSFColor(new java.awt.Color(255,204,255)));
	        style.setFillPattern(CellStyle.SOLID_FOREGROUND); 
	        style.setFont(cal2Font); 
	        style.setWrapText(false);
	        style.setLocked(false);
	        this.setBorder(style);
	        stylesMap.put(STYLE_CALENDAR2, style); 
	        
	        Font calSatSunFont = swb.createFont(); 
	        calSatSunFont.setFontHeightInPoints((short)8); 
	        calSatSunFont.setColor(IndexedColors.BLACK.getIndex());
	        style = (XSSFCellStyle)swb.createCellStyle();
	        style.setAlignment(CellStyle.ALIGN_CENTER); 
	        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER); 
	        style.setFillForegroundColor(new XSSFColor(new java.awt.Color(166,166,166)));
	        style.setFillPattern(CellStyle.SOLID_FOREGROUND); 
	        style.setFont(calSatSunFont); 
	        style.setWrapText(false);
	        style.setLocked(false);
	        this.setBorder(style);
	        stylesMap.put(STYLE_CALENDAR_SAT_SUN, style); 
	        
	        
	        Font calDateSatSunFont = swb.createFont(); 
	        calDateSatSunFont.setFontHeightInPoints((short)8); 
	        calDateSatSunFont.setColor(IndexedColors.BLACK.getIndex());
	        style = (XSSFCellStyle)swb.createCellStyle();
	        style.setAlignment(CellStyle.ALIGN_CENTER); 
	        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER); 
	        style.setFillForegroundColor(new XSSFColor(new java.awt.Color(166,166,166)));
	        style.setFillPattern(CellStyle.SOLID_FOREGROUND); 
	        style.setFont(calDateSatSunFont); 
	        style.setWrapText(false);
	        this.setBorder(style);
	        stylesMap.put(STYLE_CALENDAR_DATE_SAT_SUN, style); 
	        
	        Font calDate1Font = swb.createFont(); 
	        calDate1Font.setFontHeightInPoints((short)8); 
	        calDate1Font.setColor(IndexedColors.BLACK.getIndex());
	        style = (XSSFCellStyle)swb.createCellStyle();
	        style.setAlignment(CellStyle.ALIGN_CENTER); 
	        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER); 
	        style.setFillForegroundColor(new XSSFColor(new java.awt.Color(255,255,153)));
	        style.setFillPattern(CellStyle.SOLID_FOREGROUND); 
	        style.setFont(calDate1Font); 
	        style.setWrapText(false);
	        this.setBorder(style);
	        stylesMap.put(STYLE_CALENDAR_DATE1, style); 
	        
	        Font calDate2Font = swb.createFont(); 
	        calDate2Font.setFontHeightInPoints((short)8); 
	        calDate2Font.setColor(IndexedColors.BLACK.getIndex());
	        style = (XSSFCellStyle)swb.createCellStyle();
	        style.setAlignment(CellStyle.ALIGN_CENTER); 
	        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER); 
	        style.setFillForegroundColor(new XSSFColor(new java.awt.Color(255,204,255)));
	        style.setFillPattern(CellStyle.SOLID_FOREGROUND); 
	        style.setFont(calDate2Font); 
	        style.setWrapText(false);
	        this.setBorder(style);
	        stylesMap.put(STYLE_CALENDAR_DATE2, style); 
	        return stylesMap; 
	    } 
	
	@SuppressWarnings("deprecation")
	private void setBorder(CellStyle style){
        style.setBorderBottom(CellStyle.BORDER_THIN); 
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex()); 
        style.setBorderTop(CellStyle.BORDER_THIN); 
        style.setTopBorderColor(IndexedColors.BLACK.getIndex()); 
        style.setBorderLeft(CellStyle.BORDER_THIN); 
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex()); 
        style.setBorderRight(CellStyle.BORDER_THIN); 
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
	}
	private String writeFileReport(List<String> excelFileList) throws Exception {
		String newReportName = reportName.replace("{Getsudo Month}", this.getsudoMonth);
		newReportName = newReportName.replace("{Timing}", this.timing);
		if(newReportName.lastIndexOf("_") <= 0){
			newReportName = newReportName + AppConstants.UNDERSCORE;
		}
		String reportFileName = poi.writeFile(swb, newReportName, downloadFolder, true, excelFileList);
		
		reportFileName = FilenameUtils.getName(reportFileName);
		excelFileList.add(reportFileName);
		return reportFileName;		
	}
	
	public boolean loadConfigForGenerateReport(Connection conn) throws CommonErrorException {		
		reportName = systemRepository.findSystemMasterValue(conn, AppConstants.SYS_CATEGORY_REPORT_NAME, AppConstants.SYS_SUB_CATEGORY_REPORT_NAME, reportId);
		if(Strings.isNullOrEmpty(reportName)){
			String[] params = new String[5];
			params[0] = "Report Name";
			params[1] = "TB_M_SYSTEM";
			params[2] = "CATEGORY="+AppConstants.SYS_CATEGORY_REPORT_NAME;
			params[3] = "SUB_CATEGORY="+AppConstants.SYS_SUB_CATEGORY_REPORT_NAME;
			params[4] = "CD="+reportId+", STATUS=Y";
			String message = messageSource.getMessage(MessagesConstants.B_ERROR_DATA_NOT_FOUND_FROM, params , Locale.getDefault());
			logger.error(message);
			throw new CommonErrorException(MessagesConstants.B_ERROR_DATA_NOT_FOUND_FROM
					, params
					, AppConstants.ERROR);
		}
		return true;
	}
	
	private int getCalendarStyleFlag(String dateValue){
		int calendarStyleFlag = STYLE_1;
		String calendarDtStr = dateValue;
        Date calendarDt = FormatUtil.convertStringToDate(calendarDtStr,AppConstants.DATE_SHOW_IN_REPORT);
        Calendar c = Calendar.getInstance();
        c.setTime(calendarDt);
        int month = c.get(Calendar.MONTH);
        DateFormat df = new SimpleDateFormat("EEE");
        
        if("Sat".equals(df.format(c.getTime())) || "Sun".equals(df.format(c.getTime()))){
        	calendarStyleFlag = STYLE_SAT_SUN;
        }else if((month % 2) == 0){
			calendarStyleFlag = STYLE_1;
		}else{
			calendarStyleFlag = STYLE_2;
		}
        
        return calendarStyleFlag;
	}
}
