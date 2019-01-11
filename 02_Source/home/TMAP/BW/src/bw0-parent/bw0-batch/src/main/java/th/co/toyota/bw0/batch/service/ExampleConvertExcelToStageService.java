/******************************************************
 * Program History
 * 
 * Project Name	            :  TIM : Toyota Insurance Management
 * Client Name				:  TDEM
 * Package Name             :  th.co.toyota.bw0.batch.service
 * Program ID 	            :  ExampleConvertExcelToStageService.java
 * Program Description	    :  Example Upload
 * Environment	 	    	:  Java 7
 * Author		    		:  Thanawut T.
 * Version		    		:  1.0
 * Creation Date            :  10 January 2019
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2013-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/
package th.co.toyota.bw0.batch.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import th.co.toyota.bw0.api.common.upload.CBW00000CommonExcelConversionDTO;
import th.co.toyota.bw0.api.common.upload.CBW00000DataFileUpload;
import th.co.toyota.bw0.api.constants.MessagesConstants;
import th.co.toyota.bw0.api.exception.common.CommonErrorException;
import th.co.toyota.bw0.api.service.common.CBW00000CommonService;
import th.co.toyota.bw0.batch.repository.ExampleConvertExcelToStageRepository;
import th.co.toyota.bw0.util.FormatUtil;
import th.co.toyota.st3.api.constants.CST30000Messages;
import th.co.toyota.st3.api.util.IST30000LoggerDb;

import com.google.common.base.Strings;

@Service
public class ExampleConvertExcelToStageService extends CBW00000DataFileUpload{
	final Logger logger = LoggerFactory.getLogger(ExampleConvertExcelToStageService.class);
	
	@Autowired
	private IST30000LoggerDb loggerBBW02130;
	
	@Autowired
	CBW00000CommonService commonService;
	
	@Autowired
	private ExampleConvertExcelToStageRepository repository;

	protected static final String DEFAULT_PAD = " ";
	
	private int runningNo = 1;

	private String fileIdInUploadFile;
	private String fileNameInUploadFile;
	
	public boolean validateParameters(String[] params, int lengthParamCheck, String appId, String createBy, String fileName, String fileId) {
		this.appId = appId;
		this.createBy = createBy;
		this.filename = fileName;
		this.fileId = fileId;
		if (params.length != lengthParamCheck) {
			String errMsg = messageSource.getMessage(CST30000Messages.ERROR_MESSAGE_MISSING_PARAMETER, null, Locale.getDefault());
			errMsg = errMsg + "(" + params.length + "/"+lengthParamCheck+")";
			logger.error(errMsg);
			loggerBBW02130.error(appId, CST30000Messages.ERROR_MESSAGE_MISSING_PARAMETER, errMsg, createBy);
			return false;
		}

		return true;
	}
	
	//if some report want to check File type. Need override this method and return true
	public boolean checkFileSize(){
		return false;
	}

	public Object[] convertExcelToStaging(String tableName) throws CommonErrorException{
		try{
			String deleteStageSQL = " DELETE FROM " + tableName + " WHERE CREATE_BY = '" + this.createBy + "'";
	        return this.convertExcelToStaging(this.filename, fileId, this.createBy, tableName, this.checkFileSize(), this.getHeaderParamToCheckWithExcel(null), deleteStageSQL);
		}catch (CommonErrorException e){		
			String errMsg = messageSource.getMessage(e.getMessageCode(),e.getMessageArg(), Locale.getDefault());
			logger.error(errMsg);
			loggerBBW02130.error(appId, e.getMessageCode(), errMsg, createBy);
			throw e;
		}
	}
	
	@Override 
	public int insertDataToStaging(List<Object[]> dataLs)throws Exception {
    	return repository.insertDataToStaging(dataLs, this.createBy);
    }
	
	public boolean getDataHeaderOfEachFunction(List<Sheet> workingSheet, FormulaEvaluator objFormulaEvaluator) throws Exception {
		if(workingSheet!=null && !workingSheet.isEmpty()){
			Sheet curSheet = workingSheet.get(0);
			int hdStartRow = 1;
			int rowNum = curSheet.getLastRowNum();// End Row
			if (rowNum == 0 || rowNum > hdStartRow) {
				Row dataRow = curSheet.getRow(hdStartRow);
				HashMap mapInfo = new HashMap();
				for (int j = 0; j < 2; j++) {
					Cell cell = dataRow.getCell((short)j);
					Object[] cellValues;
					String colName = "";
					if(j==0){
						colName = "FILE_ID";
					}else if(j==1){
						colName = "FILE_NAME";
					}
					String cellValue = "";
					cellValues = readCell(mapInfo, cell, colName, j, "", -1);
					boolean valid = (boolean)cellValues[0];
					if(valid){
						cellValue = (String)cellValues[1];
					    String cellValueOriginal = (String)cellValues[2];
		        
					    cellValues = checkLength(mapInfo, cellValue, colName, cellValueOriginal, j, "", -1);
			            valid = (boolean)cellValues[0];
						if(valid){
							cellValue = (String)cellValues[1];
						}
					}
					if(j==0){
						this.fileIdInUploadFile = cellValue;
					}else if(j==1){
						this.fileNameInUploadFile = cellValue;
					}
				}
			}
		}
		return true; //Need edit??
	}
	
	public Object[] getDataSection(int startCol, 
								  int endCol,
								  Row dataRow,
								  int rowIdx,
								  Object[] headerChk,
								  FormulaEvaluator objFormulaEvaluator,
								  List<Object[]> dataList) throws Exception {
		boolean validAll = true;
		boolean validHeaderOfDetail = true;	
		HashMap dMapField = xlsConvVo.getDetailMappingField();
		
		String col1ofRow = "";
		List<Object> lsData = new ArrayList<>();
		lsData.add("201901");
		lsData.add("D-11");
		lsData.add("TMT");
		lsData.add("Hilux");
		lsData.add("STM");
		lsData.add("DA1");
		lsData.add(this.fileIdInUploadFile);
		lsData.add(this.fileNameInUploadFile);
		
		for (int j = startCol; j < endCol; j++) {
			Cell cell = dataRow.getCell((short)j);
			Object[] cellValues;
			String colName = this.xlsConvVo.getColumnNames()[j];	
			HashMap mapInfo = (HashMap)dMapField.get(colName);
			
			cellValues = readCell(mapInfo, cell, colName, j, col1ofRow, rowIdx);
			boolean valid = (boolean)cellValues[0];
			if(valid){
				String cellValue = (String)cellValues[1];
			    String cellValueOriginal = (String)cellValues[2];
        
			    cellValues = checkLength(mapInfo, cellValue, colName, cellValueOriginal, j, colName, rowIdx);
	            valid = (boolean)cellValues[0];
				if(valid){
					cellValue = (String)cellValues[1];
					
					String convertDtFormat = (String)mapInfo.get(CBW00000CommonExcelConversionDTO.ATTR_CONVERT_STRING_TO_DATE);
					if(!Strings.isNullOrEmpty(convertDtFormat)){
						Date dt = null;
						if(!Strings.isNullOrEmpty(cellValue)){
							if(!FormatUtil.isValidDate(cellValue, convertDtFormat)){
								String errMsg = messageSource.getMessage(MessagesConstants.B_ERROR_INVALID_FORMAT, 
										new String[]{colName+" {Row No. "+(rowIdx+1)+"}", Strings.nullToEmpty(convertDtFormat)}, Locale.getDefault());
								logger.error(errMsg);
								loggerDb.error(appId, MessagesConstants.B_ERROR_INVALID_FORMAT, errMsg, createBy);
								validAll = false;
							}else{
								dt = FormatUtil.convertStringToDate(cellValue, convertDtFormat);								
							}
						}
						lsData.add(dt);
					}else{
						lsData.add(cellValue);
					}
				}else{
					validAll = valid;
				}
			}else{
				validAll = valid;
			}
		}
		lsData.add(this.filename);
		lsData.add(this.createBy);
		lsData.add(this.runningNo++);
		lsData.add(this.appId);
		if(validAll){
			dataList.add(lsData.toArray());
		}
		return new Object[]{validAll, validHeaderOfDetail, dataList};
	}
	
	
}
