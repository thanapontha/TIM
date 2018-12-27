/******************************************************
 * Program History
 * 
 * Project Name	            :  st-server-webapp-template
 * Client Name				:  TMAP-EM
 * Package Name             :  th.co.toyota.application.service
 * Program ID 	            :  CST33060SystemMasterService.java
 * Program Description	    :  <put description>
 * Environment	 	        :  Java 7
 * Author					:  LJ
 * Version					:  1.0
 * Creation Date            :  Nov 4, 2013
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2013-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/
package th.co.toyota.application.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFBorderFormatting;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import th.co.toyota.application.repository.IST33060SystemMasterRepository;
import th.co.toyota.bw0.util.FormatUtil;
import th.co.toyota.st3.api.constants.CST30000Constants;
import th.co.toyota.st3.api.exception.ConcurrencyException;
import th.co.toyota.st3.api.exception.SystemAlreadyExistsException;
import th.co.toyota.st3.api.exception.SystemDoesNotExistsException;
import th.co.toyota.st3.api.exception.UnableToCreateExcelForDowloadException;
import th.co.toyota.st3.api.model.SystemInfo;
import th.co.toyota.st3.api.model.SystemInfoId;

/**
 * Service implementation for system master screen.
 * 
 * @author Manego
 * 
 */
@Service
public class CST33060SystemMasterService implements IST33060SystemMasterService {
	/** A file logger instance. */
	final Logger logger = LoggerFactory.getLogger(CST33060SystemMasterService.class);
	
	/** A system master repository. */
	@Autowired
	private IST33060SystemMasterRepository systemMasterRepository;
	
	/** Template file to generate report for system master. */
	@Value("${templateFileSystemMaster}")
	private String systemMasterExcelTemplate;
	
	/** Time format supported by system master. */
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	public CST33060SystemMasterService() {
	}
	/*
	 * (non-Javadoc)
	 * @see th.co.toyota.application.service.IST33060SystemMasterService#retrieveSystemMasterInfoId()
	 */
	@Override
	public List<SystemInfoId> retrieveSystemMasterInfoId() {
		return systemMasterRepository.querySystemMasterInfoId();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.service.IST33060SystemMasterService#
	 * listSystemMasterInfo(java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public List<SystemInfo> listSystemMasterInfo(String category, String subCategory, String code) {

		SystemInfoId id = new SystemInfoId();
		id.setCategory(category.trim());
		id.setSubCategory(subCategory.trim());
		id.setCode(code.trim());

		return systemMasterRepository.querySystemMasterInfo(id);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.service.IST33060SystemMasterService#
	 * addSystemMasterInfo(java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.Character,
	 * java.lang.String)
	 */
	@Override
	public void addSystemMasterInfo(String category, String subCategory, String code,
			String value, String remark, Character status, String userId) 
					throws SystemAlreadyExistsException {
		
		SystemInfoId id = new SystemInfoId();
		id.setCategory(category.trim().toUpperCase());
		id.setSubCategory(subCategory.trim().toUpperCase());
		id.setCode(code.trim().toUpperCase());
		
		SystemInfo sys = new SystemInfo();
		sys.setId(id);
		sys.setValue(value);
		sys.setRemark(remark);
		sys.setStatus(status);
		sys.setUpdateBy(userId);
		sys.setCreateBy(userId);
		sys.setUpdateDate(FormatUtil.currentTimestampToOracleDB());
		sys.setCreateDate(sys.getUpdateDate());
		
		systemMasterRepository.addSystemMasterInfo(sys);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.service.IST33060SystemMasterService#
	 * updateSystemMasterInfo(java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.Character, java.lang.String, java.util.Date)
	 */
	@Override
	public void updateSystemMasterInfo(String category, String subCategory, String code,
			String value, String remark, Character status, String userId, Date updateDt) 
					throws ConcurrencyException, SystemDoesNotExistsException {

		SystemInfoId id = new SystemInfoId();
		id.setCategory(category.trim().toUpperCase());
		id.setSubCategory(subCategory.trim().toUpperCase());
		id.setCode(code.trim().toUpperCase());

		SystemInfo sys = new SystemInfo();
		sys.setId(id);
		sys.setValue(value);
		sys.setRemark(remark);
		sys.setStatus(status);
		sys.setUpdateBy(userId);
		sys.setUpdateDate(updateDt);
		
		systemMasterRepository.updateSystemMasterInfo(sys);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.service.IST33060SystemMasterService#
	 * deleteSystemMasterInfo(java.lang.String, java.lang.String,
	 * java.lang.String, java.util.Date)
	 */
	@Override
	public void deleteSystemMasterInfo(String category, String subCategory, String code, Date updateDt) 
			throws ConcurrencyException, SystemDoesNotExistsException {

		SystemInfoId id = new SystemInfoId();
		id.setCategory(category.trim().toUpperCase());
		id.setSubCategory(subCategory.trim().toUpperCase());
		id.setCode(code.trim().toUpperCase());

		SystemInfo sys = new SystemInfo();
		sys.setId(id);
		sys.setUpdateDate(updateDt);
		
		systemMasterRepository.deleteSystemMasterInfo(sys);

	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.service.IST33060SystemMasterService#
	 * listSystemMasterInfoToExcel(java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public HSSFWorkbook listSystemMasterInfoToExcel(String category, String subCategory, String code) 
			throws UnableToCreateExcelForDowloadException {
		
		SystemInfoId id = new SystemInfoId();
		id.setCategory(category);
		id.setSubCategory(subCategory);
		id.setCode(code);

		SystemInfo sys = new SystemInfo();
		sys.setId(id);
		
		InputStream is = null;
		HSSFWorkbook wb = null;
		
		try {
			
			List<SystemInfo> systemMasterInfo = systemMasterRepository.querySystemMasterInfo(id);
			is = Thread.currentThread().getContextClassLoader().getResourceAsStream(systemMasterExcelTemplate);
			wb = new HSSFWorkbook(is);

			HSSFSheet ws = wb.getSheetAt(0);
			int startRow = 3;
			
			// iterate system master data
			for (SystemInfo sysInfo : systemMasterInfo) {
				int startCell = 0;
				HSSFRow row = null;
				row = createRow(ws, startRow++);
				writeSystemMasterInfo(row, sysInfo, startCell);
			}

		} catch (FileNotFoundException fe) {
			logger.error("Unable to read the template excel file: {}", systemMasterExcelTemplate);
			throw new UnableToCreateExcelForDowloadException();
		} catch (IOException e) {
			logger.error("Unable to create excel file from template using POI.", e);
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

	/**
	 * Create new excel row.
	 * 
	 * @param ws
	 *            excel object.
	 * @param startRow
	 *            the row index.
	 * @return A newly created row.
	 */
	private HSSFRow createRow(HSSFSheet ws, int startRow) {

		HSSFRow row = ws.createRow(startRow);

		// pre-create the cell
		CellStyle csBorder = ws.getWorkbook().createCellStyle();
		csBorder.setBorderLeft(HSSFBorderFormatting.BORDER_THIN);
		csBorder.setBorderRight(HSSFBorderFormatting.BORDER_THIN);
		csBorder.setBorderTop(HSSFBorderFormatting.BORDER_THIN);
		csBorder.setBorderBottom(HSSFBorderFormatting.BORDER_THIN);

		for (int i = 0; i < 10; i++) {
			row.createCell(i).setCellStyle(csBorder);
		}

		return row;
	}

	/**
	 * Write the system master information in the excel sheet.
	 * 
	 * @param row
	 *            The excel row
	 * @param sysInfo
	 *            The {@link SystemInfo} details.
	 * @param startCell
	 *            A shell index.
	 * @return a next shell index.
	 */
	private int writeSystemMasterInfo(HSSFRow row, SystemInfo sysInfo, int startCell) {
		row.getCell(startCell++).setCellValue(sysInfo.getId().getCategory());
		row.getCell(startCell++).setCellValue(sysInfo.getId().getSubCategory());
		row.getCell(startCell++).setCellValue(sysInfo.getId().getCode());
		row.getCell(startCell++).setCellValue(sysInfo.getValue());
		row.getCell(startCell++).setCellValue(sysInfo.getRemark());
		
		if (sysInfo.getStatus().equals(CST30000Constants.ACTIVE_CODE)) {
			row.getCell(startCell++).setCellValue(CST30000Constants.STRING_ACTIVE);
		} else if (sysInfo.getStatus().equals(CST30000Constants.INACTIVE_CODE)) {
			row.getCell(startCell++).setCellValue(CST30000Constants.STRING_INACTIVE);
		}

		if (sysInfo.getUpdateDate() != null) {
			row.getCell(startCell++).setCellValue(
					sdf.format(sysInfo.getUpdateDate()));
		} else {
			row.getCell(startCell++).setCellValue("");
		}
		row.getCell(startCell++).setCellValue(sysInfo.getUpdateBy());
		
		if (sysInfo.getCreateDate() != null) {
			row.getCell(startCell++).setCellValue(
					sdf.format(sysInfo.getCreateDate()));
		} else {
			row.getCell(startCell++).setCellValue("");
		}
		row.getCell(startCell++).setCellValue(sysInfo.getCreateBy());

		return startCell;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.service.IST33060SystemMasterService#
	 * querySystemMasterCodeValue(java.lang.String, java.lang.String)
	 */
	//TODO Used by log monitoring to retrieve data for combobox. This should be in common service.
	@Override
	public List<SystemInfo> querySystemMasterCodeValue(String category, String subCategory){
		return systemMasterRepository.querySystemMasterCodeValue(category, subCategory);
	}
	
}
