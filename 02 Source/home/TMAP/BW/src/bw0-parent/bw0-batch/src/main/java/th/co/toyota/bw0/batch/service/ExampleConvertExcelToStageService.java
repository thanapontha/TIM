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
 * Copyright(C) 2019-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/
package th.co.toyota.bw0.batch.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import th.co.toyota.bw0.api.common.upload.CommonDataFileUpload;
import th.co.toyota.bw0.api.common.upload.CommonExcelConversionDTO;
import th.co.toyota.bw0.api.constants.MessagesConstants;
import th.co.toyota.bw0.api.exception.common.CommonErrorException;
import th.co.toyota.bw0.batch.repository.ExampleConvertExcelToStageRepository;
import th.co.toyota.bw0.util.FormatUtil;
import th.co.toyota.st3.api.constants.CST30000Messages;
import th.co.toyota.st3.api.util.IST30000LoggerDb;

import com.google.common.base.Strings;

@Service
public class ExampleConvertExcelToStageService extends CommonDataFileUpload{
	final Logger logger = LoggerFactory.getLogger(ExampleConvertExcelToStageService.class);
	
	@Autowired
	private IST30000LoggerDb loggerDB;
	
	@Autowired
	private ExampleConvertExcelToStageRepository repository;

	protected String DEFAULT_SEPARATOR = "|";
	protected static final String DEFAULT_PAD = " ";
	
	public String userCompanyLogin;
	public String uploadType;
	public String getsudoMonth;
	public String timing;
	public String vehiclePlant;
	public String vehicleModel;
	public String unitPlant;
	public String unitType;
	public String unitModel;
	public String fileNameInUploadFile;
	public String fileIdInUploadFile;
	public Timestamp sysdate;
	private int runningNo = 1;
	
	String unitPlantAllSelected = null;
	String unitModelAllSelected = null;
	String unitTypeAllSelected = null;

	public boolean validateParameters(String[] params, int lengthParamCheck, String sAppId, String sCreateBy, String fileName, String fileId,Timestamp sysdate) {
		this.setAppId(sAppId);
		this.setCreateBy(sCreateBy);
		this.setFileName(fileName);
		this.setFileId(fileId);
		this.sysdate = sysdate;
		if (params.length != lengthParamCheck) {
			String errMsg = messageSource.getMessage(CST30000Messages.ERROR_MESSAGE_MISSING_PARAMETER, null, Locale.getDefault());
			errMsg = errMsg + "(" + params.length + "/"+lengthParamCheck+")";
			logger.error(errMsg);
			loggerDB.error(this.getAppId(), CST30000Messages.ERROR_MESSAGE_MISSING_PARAMETER, errMsg, this.getCreateBy());
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
			String deleteStageSQL = " DELETE FROM "+tableName+" WHERE CREATE_BY = '"+this.getCreateBy()+"'";
	        return this.convertExcelToStaging(tableName, this.checkFileSize(), this.getHeaderParamToCheckWithExcel(null), deleteStageSQL);
		}catch (CommonErrorException e){		
			String errMsg = messageSource.getMessage(e.getMessageCode(),e.getMessageArg(), Locale.getDefault());
			logger.error(errMsg);
			loggerDB.error(this.getAppId(), e.getMessageCode(), errMsg, this.getCreateBy());
			throw e;
		}
	}	
	
	@Override
	public int insertDataToStaging(List<Object[]> dataLs)throws Exception {
    	return repository.insertDataToStaging(null, dataLs, this.getCreateBy());
    }
	
	public boolean getDataHeaderOfEachFunction(List<Sheet> workingSheet, FormulaEvaluator objFormulaEvaluator) throws Exception {
		if(workingSheet!=null && !workingSheet.isEmpty()){
			Sheet curSheet = workingSheet.get(0);
			int hdStartRow = 1;
			int rowNum = curSheet.getLastRowNum();// End Row
			if (rowNum == 0 || rowNum > hdStartRow) {
				Row dataRow = curSheet.getRow(hdStartRow);
				Map<String, Object> mapInfo = new HashMap<String, Object>();
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
	
	@Override
	public Object[] getDataSection(int startCol, 
								  int endCol,
								  Row dataRow,
								  int rowIdx,
								  Object[] headerChk,
								  FormulaEvaluator objFormulaEvaluator,
								  List<Object[]> dataList) throws CommonErrorException {
		boolean validAll = true;
		boolean validHeaderOfDetail = true;	
		Map<String, Object> dMapField = xlsConvVo.getDetailMappingField();
		
		String col1ofRow = "";
		List<Object> lsData = new ArrayList<Object>();
		lsData.add(this.getsudoMonth);
		lsData.add(this.timing);
		lsData.add(this.vehiclePlant);
		lsData.add(this.vehicleModel);
		lsData.add(this.unitPlant);
		lsData.add(this.unitModel);
		lsData.add(this.fileIdInUploadFile);
		lsData.add(this.fileNameInUploadFile);
		
		for (int j = startCol; j < endCol; j++) {
			Cell cell = dataRow.getCell((short)j);
			Object[] cellValues;
			String colName = this.xlsConvVo.getColumnNames()[j];	
			Map<String, Object> mapInfo = (HashMap)dMapField.get(colName);
			
			cellValues = readCell(mapInfo, cell, colName, j, col1ofRow, rowIdx);
			boolean valid = (boolean)cellValues[0];
			if(valid){
				String cellValue = (String)cellValues[1];
			    String cellValueOriginal = (String)cellValues[2];
        
			    cellValues = checkLength(mapInfo, cellValue, colName, cellValueOriginal, j, colName, rowIdx);
	            valid = (boolean)cellValues[0];
				if(valid){
					cellValue = (String)cellValues[1];
					
					String convertDtFormat = (String)mapInfo.get(CommonExcelConversionDTO.ATTR_CONVERT_STRING_TO_DATE);
					if(!Strings.isNullOrEmpty(convertDtFormat)){
						Date dt = null;
						if(!Strings.isNullOrEmpty(cellValue)){
							if(FormatUtil.isValidDate(cellValue, convertDtFormat) == false){
								String errMsg = messageSource.getMessage(MessagesConstants.B_ERROR_INVALID_FORMAT, 
										new String[]{colName+" {Row No. "+(rowIdx+1)+"}", Strings.nullToEmpty(convertDtFormat)}, Locale.getDefault());
								logger.error(errMsg);
								loggerDb.error(this.getAppId(), MessagesConstants.B_ERROR_INVALID_FORMAT, errMsg, this.getCreateBy());
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
		lsData.add(this.getFileName());
		lsData.add(this.getCreateBy());
		lsData.add(this.runningNo++);
		lsData.add(this.getAppId());
		if(validAll){
			dataList.add(lsData.toArray());
		}
		return new Object[]{validAll, validHeaderOfDetail, dataList};
	}
	
	
}
