/******************************************************
 * Program History
 * 
 * Project Name	            :    
 * Client Name				:  TDEM
 * Package Name             :  th.co.toyota.bw0.api.common.upload
 * Program ID 	            :  CommonDataFileUpload.java
 * Program Description	    :  <put description>
 * Environment	 	        :  Java 7
 * Author					:  Thanapon T.
 * Version					:  1.0
 * Creation Date            :  Jan 11, 2018
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2013-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/

package th.co.toyota.bw0.api.common.upload;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.internal.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import th.co.toyota.bw0.api.common.CommonUtility;
import th.co.toyota.bw0.api.constants.AppConstants;
import th.co.toyota.bw0.api.constants.MessagesConstants;
import th.co.toyota.bw0.api.exception.common.CommonErrorException;
import th.co.toyota.bw0.api.repository.common.CommonAPIRepository;
import th.co.toyota.bw0.api.repository.common.SystemMasterAPIRepository;
import th.co.toyota.bw0.util.FormatUtil;
import th.co.toyota.st3.api.constants.CST30000Messages;
import th.co.toyota.st3.api.exception.FileUploadDownloadException;
import th.co.toyota.st3.api.exception.PostODBFailedException;
import th.co.toyota.st3.api.model.BatchQueue;
import th.co.toyota.st3.api.model.SystemInfo;
import th.co.toyota.st3.api.model.SystemInfoId;
import th.co.toyota.st3.api.upload.CST32020DataFileUploadConfig;
import th.co.toyota.st3.api.util.CST30000BatchManager;
import th.co.toyota.st3.api.util.IST30000LoggerDb;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;

@Component
public class CommonDataFileUpload {
	    final Logger logger = LoggerFactory.getLogger(CommonDataFileUpload.class);
	    @Autowired
	    protected MessageSource messageSource;
	    @Autowired
	    private SystemMasterAPIRepository systemRepository;
		@Autowired
		public CommonAPIRepository commonRepository;
	    @Autowired
	    private CST30000BatchManager batchManager;
	    @Autowired
	    public CST32020DataFileUploadConfig uploadConfig;
		@NotNull
		@PersistenceContext(unitName = "entityManagerFactory")
		private EntityManager em;
		@Transient
		public CommonDataFileUploadXMLLoader xmlLoader;
		@Transient
		public CommonDataFileUploadExcelConversionDTO xlsConvVo;
		@Transient
		public IST30000LoggerDb loggerDb;
		
		
		@Value(value="${ctl.file.path}")
		public String ctlFilePath;
	    public final String companyCode = "TMAP-EM";
		private String CHECK_WITH_PARAM = "CHECK_WITH_PARAM";
	    private String subFolder;
	    public int genAsciiCnt = 0;
	    protected HashMap<String,Object> tableMetaData;
	    public String appId;
	    public String appId2;
		public String createBy;
		public String filename;
		public String fileId;

		public boolean alreadyLoggedInvaidTemplate = false;
		public int maxColData = -1;
		
	    public List<String> gmHeaderChk = new ArrayList<String>();

		public String uploadFile(MultipartFile file, String userID, String screenID, String batchID, ArrayList<String> additionalParam) throws Exception {
			try {
	            this.checkUploadFile(file, batchID);
                String newExcelFileName = this.processExcelFileUpload(file, true);
                ArrayList<String> listParameter = new ArrayList<String>();
                if(additionalParam != null && !additionalParam.isEmpty()){
                	for(int i=0; i<additionalParam.size(); i++){
                		String value = additionalParam.get(i);
                		if(AppConstants.REPLACE_NEW_FILE_NAME_OF_UPLOAD.equals(value)){
                			listParameter.add(CommonUtility.addBlankSaparator(newExcelFileName));
                		}else{
                			listParameter.add(CommonUtility.addBlankSaparator(additionalParam.get(i)));
                		}                		
                	}
                }
                
                this.postODBatch(screenID, batchID, userID, listParameter);
                return newExcelFileName;
	        }
	        catch (Exception e) {
	            e.printStackTrace();
	            this.logger.error(e.getMessage());
	            throw e;
	        }
	    }
	    
	    public Object[] convertExcelToStaging(String fileName, String functionId, String userID,String stagingTbName, boolean checkFileSize, HashMap<String, String[]> paramHeaderChk, String deleteStageSQL) throws CommonErrorException {
	        int dataCnt = 0;
	        boolean validAll = true;
	    	String tmpFileName = "";
	        FileInputStream file = null;
	        Workbook workbook = null;
	        FormulaEvaluator objFormulaEvaluator = null;
	        try {
	        	//Remove Staging data
	        	if(deleteStageSQL!=null && !"".equals(deleteStageSQL)){
	        		this.deleteDataStaging(null, deleteStageSQL);
	        	}else{
	        		this.deleteDataStaging(null, "TRUNCATE TABLE " + stagingTbName);
	        	}
	        	
	        	//Load xml mapping configuration
	        	xmlLoader = new CommonDataFileUploadXMLLoader();
	    		xlsConvVo = xmlLoader.loadXMLConfig(ctlFilePath, functionId+".xml");
	    		
	    		//Get table metadata
	    		tableMetaData = commonRepository.getTableMeataData(stagingTbName);
	        	
	            String tempFolder = this.uploadConfig.getTempUploadFolder(this.companyCode);
	            
	            
	            this.validateUploadFile(fileName, tempFolder, checkFileSize, functionId);
	            
	            String targetFolder = this.uploadConfig.getTempUploadFolder(this.companyCode);
	            file = new FileInputStream(new File(targetFolder + fileName));
	            int iPos = fileName.lastIndexOf('.');
	            String fileExtension = fileName.substring(iPos + 1, fileName.length()).toUpperCase();
	            if (AppConstants.FILE_FORMAT_EXCEL_XLS.equals(fileExtension)) {
	                workbook = new HSSFWorkbook((InputStream)file);
	                objFormulaEvaluator = new HSSFFormulaEvaluator((HSSFWorkbook)workbook);
	            } else {
	                workbook = new XSSFWorkbook((InputStream)file);
	                objFormulaEvaluator = new XSSFFormulaEvaluator((XSSFWorkbook)workbook);
	            }
	            List<Sheet> workingSheet = this.getWokringSheet(workbook);
	            boolean validHeader = this.checkHeaderTemplate(workingSheet, objFormulaEvaluator, paramHeaderChk);
	            if(validHeader==false){
	            	if(alreadyLoggedInvaidTemplate==false){
	            		alreadyLoggedInvaidTemplate = true;
	    				String errMsg = messageSource.getMessage(MessagesConstants.B_ERROR_INVALID_FILE_TEMPLATE, new String[]{}, Locale.getDefault());
	    				logger.error(errMsg);
	    				loggerDb.error(appId, MessagesConstants.B_ERROR_INVALID_FILE_TEMPLATE, errMsg, createBy);
	    				if(!Strings.isNullOrEmpty(appId2)) loggerDb.error(appId2, MessagesConstants.B_ERROR_INVALID_FILE_TEMPLATE, errMsg, createBy);
	            	}
    				validAll = validHeader;
	            }else{
	            	validHeader = this.getDataHeaderOfEachFunction(workingSheet, objFormulaEvaluator);
	            	validAll = validHeader;
	            }
				Object[] result = this.generateData(workbook, objFormulaEvaluator);
				if(result!=null && result.length == 3){
					boolean validDetail = (boolean)result[0];
					boolean validHeaderOfDetail = (boolean)result[1];
					List<Object[]> dataLs = (List<Object[]>)result[2];
					if(validHeader && validHeaderOfDetail == false){
						if(alreadyLoggedInvaidTemplate==false){
		            		alreadyLoggedInvaidTemplate = true;
							String errMsg = messageSource.getMessage(MessagesConstants.B_ERROR_INVALID_FILE_TEMPLATE, new String[]{}, Locale.getDefault());
							logger.error(errMsg);
							loggerDb.error(appId, MessagesConstants.B_ERROR_INVALID_FILE_TEMPLATE, errMsg, createBy);
							if(!Strings.isNullOrEmpty(appId2)) loggerDb.error(appId2, MessagesConstants.B_ERROR_INVALID_FILE_TEMPLATE, errMsg, createBy);
						}
					}
					if(validDetail && validHeader){						
						dataCnt = insertDataToStaging(dataLs);
					}else{
						//dataCnt = dataLs!=null?dataLs.size():0;
						dataCnt = 0;
						validAll = false;
					}
				}

	        }catch (CommonErrorException e) {
	            throw e;
	        }catch (Exception e) {
	            throw new CommonErrorException(CST30000Messages.ERROR_UNDEFINED_ERROR, new String[]{e.getMessage()}, AppConstants.ERROR);
	        }
	        finally {
	            String tempFolder = this.uploadConfig.getTempUploadFolder(this.companyCode);
	            if (!Strings.isNullOrEmpty((String)tmpFileName)) {
	                new File(tempFolder + tmpFileName).delete();
	            }
	        }
	        return new Object[]{validAll, dataCnt};
	    }

	    public HashMap<String, String[]> getHeaderParamToCheckWithExcel(String[] params){
	    	return null;
	    }
	    
	    public void deleteDataStaging(Connection conn, String deleteSQL) throws CommonErrorException{
			boolean completed = false;
			boolean closeConnection = true;
			try{
				if(conn==null){
					SessionImpl session = (SessionImpl)(em.getDelegate());
					conn = session.getJdbcConnectionAccess().obtainConnection();
				}else{
					closeConnection = false;
				}
				conn.setAutoCommit(false);
				
				//delete data from staging by user id
				PreparedStatement ps = conn.prepareStatement(deleteSQL);
				ps.executeUpdate();
				ps.close();
				
				completed = true;
			}catch (Exception e) {
				completed = false;
				throw CommonUtility.handleExceptionToCommonErrorException(e, logger, true);
			}finally{
				try {
					if(conn!=null && !conn.isClosed()){
						if(completed){
							conn.commit();
						}else{
							conn.rollback();
						}
						
						if(closeConnection){
							conn.close();
							conn = null;
						}
					}
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
	    
	    public int insertDataToStaging(List<Object[]> dataLs)throws Exception {
	    	return 0;
	    }
	    
	    public List<Sheet> getWokringSheet(Workbook workbook) throws Exception {
			//Generate Data
			List<Sheet> sheetList = new ArrayList<Sheet>();
			Integer[] intWrkShtIdx = xlsConvVo.getArrayWorkSheetIdx();
			String[] strWrkShtName = xlsConvVo.getArrayWorkSheetName();
			if (intWrkShtIdx != null && intWrkShtIdx.length > 0) {
				//Higher priority than Work Sheet Name
				for (int i = 0; i < intWrkShtIdx.length; i++) {
					Sheet curSheet = workbook.getSheetAt(intWrkShtIdx[i].intValue());
					sheetList.add(curSheet);
				}
			}
			else if (strWrkShtName != null && strWrkShtName.length > 0) {
				for (int i = 0; i < strWrkShtName.length; i++) {
					Sheet curSheet = workbook.getSheet(strWrkShtName[i]);
					sheetList.add(curSheet);
				}
			}
			
			return sheetList;
		}
		
		private void handleUploadedFileWhenError(List<String> uploadedFiles, List<MultipartFile> files) {
	        String inputFolder = this.uploadConfig.getInputFolder(this.companyCode, this.subFolder);
	        for (int i = 0; i < uploadedFiles.size(); ++i) {
	            new File(inputFolder + uploadedFiles.get(i)).delete();
	        }
	        String sFolder = this.uploadConfig.getArchiveSuccess(this.companyCode);
	        String eFolder = this.uploadConfig.getArchiveError(this.companyCode);
	        File sFile = null;
	        File eFile = null;
	        for (int i2 = 0; i2 < files.size(); ++i2) {
	            sFile = new File(sFolder + files.get(i2).getOriginalFilename());
	            if (!sFile.exists()) continue;
	            eFile = new File(eFolder + files.get(i2).getOriginalFilename());
	            sFile.renameTo(eFile);
	            sFile.delete();
	        }
	        sFile = null;
	        eFile = null;
	    }

	    protected String processExcelFileUpload(MultipartFile file, boolean online) throws Exception {
	        String tempFolder = this.uploadConfig.getTempUploadFolder(this.companyCode);
	        String newExcelFileName = file.getOriginalFilename();
	        try {
	        	newExcelFileName = this.uploadToTempFolder(file, file.getOriginalFilename(), tempFolder, true);
	        }
	        catch (Exception e) {
	            this.transferTargetFileToServer(newExcelFileName, newExcelFileName, tempFolder, this.uploadConfig.getArchiveError(this.companyCode), false, online);
	            throw e;
	        }
	        return newExcelFileName;
	    }
	    
//	    protected String processExcelFileUploadODB(String fileName, String extension, List<ColSpecs> colList, String newName) throws Exception {
//	        String inputPath = this.uploadConfig.getInputFolder(this.companyCode, this.subFolder);
//	        String tempFolder = this.uploadConfig.getTempUploadFolder(this.companyCode);
//	        String asciiFilename = "";
//	        try {
//	            asciiFilename = this.writeDataFromExcel(extension, fileName, colList, tempFolder, false);
//	            newName = Strings.isNullOrEmpty((String)newName) ? asciiFilename : newName;
//	            this.transferTargetFileToServer(asciiFilename, newName, tempFolder, inputPath, true, false);
//	            this.transferTargetFileToServer(fileName, fileName, tempFolder, this.uploadConfig.getArchiveSuccess(this.companyCode), true, false);
//	        }
//	        catch (Exception e) {
//	            if (!Strings.isNullOrEmpty((String)asciiFilename)) {
//	                new File(tempFolder + asciiFilename).delete();
//	            }
//	            this.transferTargetFileToServer(fileName, fileName, tempFolder, this.uploadConfig.getArchiveError(this.companyCode), true, false);
//	            throw e;
//	        }
//	        return newName;
//	    }

	    protected void postODBatch(String screenId, String batchId, String userId, ArrayList<String> listParameter) throws PostODBFailedException {
	        try {
	            this.logger.info("Posting process is started");
	            BatchQueue batch = new BatchQueue();
	            batch.setRequestId(screenId);
	            batch.setBatchId(batchId);
	            batch.setRequestBy(userId);
	            batch.setRequestDate(new Date());
	            batch.setSupportId("");
	            batch.setProjectCode(this.uploadConfig.getProjectID());
	            
	            batch.setParameters(Joiner.on((String)" ").join(listParameter));
	            this.batchManager.createBatchQueue(batch);
	        }
	        catch (PostODBFailedException e) {
	            throw new PostODBFailedException(this.messageSource.getMessage(CST30000Messages.ERROR_ODB_POST_FAILED, 
	            																(Object[])new String[0], Locale.getDefault()));
	        }
	    }

	    /*
	     * WARNING - Removed try catching itself - possible behaviour change.
	     */
	    protected String createNameListFile(List<String> filenameList, String fileID) {
	        String filename;
	        String folder = this.uploadConfig.getInputFolder(this.companyCode, this.subFolder);
	        filename = fileID + "_" + System.currentTimeMillis();
	        Writer writer = null;
	        try {
	            File file = new File(folder + filename);
	            writer = new BufferedWriter(new FileWriter(file));
	            for (int i = 0; i < filenameList.size(); ++i) {
	                writer.write(filenameList.get(i));
	                writer.write("\r\n");
	            }
	        }
	        catch (FileNotFoundException e) {
	            e.printStackTrace();
	        }
	        catch (IOException e) {
	            e.printStackTrace();
	        }
	        finally {
	            try {
	                if (writer != null) {
	                    writer.close();
	                }
	            }
	            catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	        return filename;
	    }

	    protected void checkUploadFile(MultipartFile file, String functionId) throws FileUploadDownloadException {
	    	
	        this.getFileExtention(file);
	        
	        String originalFilename = file.getOriginalFilename();
	        if(FormatUtil.isValidByPattern("^[0-9a-zA-Z-_ ]*(.xls|.xlsx)$", originalFilename.toLowerCase()) == false){
	        	throw new FileUploadDownloadException(this.messageSource.getMessage(CST30000Messages.ERROR_MESSAGE_INVALID_FIELD, 
	        			(Object[])new String[]{"file name."}, Locale.getDefault()));
			}
	        
	        int lengthFileName = Strings.nullToEmpty(originalFilename).length();
	        int maxLenFileNameChk = AppConstants.MAX_LENGTH_UPLOAD_FILENAME;
	        BigDecimal maxLenFileName = getMaxLengthUploadFileName(functionId);
	        if(maxLenFileName != null){
	        	maxLenFileNameChk = maxLenFileName.intValue();
	        }	        
	        if(lengthFileName > maxLenFileNameChk){
	        	throw new FileUploadDownloadException(this.messageSource.getMessage(MessagesConstants.A_ERROR_LENGTH_FILE_NAME_OVER, 
	        																		(Object[])new String[]{Integer.toString(maxLenFileNameChk)}, 
	        																		Locale.getDefault()));
			}
	        
	        int iFileSize = 0;
	        BigDecimal maxFileSize = getMaxFileSizeForUpload(functionId);
	        int maxFileSizeChk = this.uploadConfig.getLimitDataSize();
	        if(maxFileSize != null){
	        	maxFileSizeChk = maxFileSize.intValue() * 1024;
	        }else{
	        	maxFileSizeChk = this.uploadConfig.getLimitDataSize();
	        }
	        
	        iFileSize = (int)file.getSize() / 1024;
	        if (iFileSize > maxFileSizeChk) {	        	
	        	throw new FileUploadDownloadException(this.messageSource.getMessage(MessagesConstants.A_ERROR_FILE_SIZE_OVER, 
	        			(Object[])new String[]{String.valueOf(maxFileSizeChk/ 1024)+" MB"}, Locale.getDefault()));
	        }

	        if (file.isEmpty() || file.getSize() == 0) {
	            throw new FileUploadDownloadException(this.messageSource.getMessage(CST30000Messages.ERROR_FILE_HAS_NO_DATA, 
	            		(Object[])new String[]{file.getOriginalFilename()}, Locale.getDefault()));
	        }
	    }

//	    private String[] getSystemMasterConfig(String fileID, boolean online) throws FileUploadDownloadException, CommonErrorException {
//	        SystemInfoId sysID = new SystemInfoId();
//	        sysID.setCategory(AppConstants.SYS_CATEGORY_COMMON);
//	        sysID.setSubCategory(AppConstants.SYS_SUB_CATEGORY_INTRFC_FILE_ID);
//	        sysID.setCode(fileID);
//	        SystemInfo sysInfo = systemRepository.findSystemMasterInfo(sysID);
//	        if (sysInfo == null || !"Y".equals(String.valueOf(sysInfo.getStatus()))) {
//	        	if(online){
//	        		throw new FileUploadDownloadException(this.messageSource.getMessage(CST30000Messages.ERROR_FILE_ID_NOT_REGISTERED, 
//	        				(Object[])new String[]{fileID, "SUB_CATEGORY = "+AppConstants.SYS_SUB_CATEGORY_INTRFC_FILE_ID}, Locale.getDefault()));
//	        	}else{
//            		throw new CommonErrorException(CST30000Messages.ERROR_FILE_ID_NOT_REGISTERED, 
//            				new String[]{fileID, "SUB_CATEGORY = "+AppConstants.SYS_SUB_CATEGORY_INTRFC_FILE_ID}, 
//            				AppConstants.ERROR);
//            	}	            
//	        }
//	        String[] config = sysInfo.getValue().split(":", -1);
//	        if (config == null || config.length < 6) {
//	            if(online){
//	            	throw new FileUploadDownloadException(this.messageSource.getMessage(CST30000Messages.ERROR_FILE_ID_NOT_REGISTERED, 
//	            			(Object[])new String[]{fileID, "SUB_CATEGORY = "+AppConstants.SYS_SUB_CATEGORY_INTRFC_FILE_ID}, Locale.getDefault()));
//	        	}else{
//            		throw new CommonErrorException(CST30000Messages.ERROR_FILE_ID_NOT_REGISTERED, 
//            				new String[]{fileID, "SUB_CATEGORY = "+AppConstants.SYS_SUB_CATEGORY_INTRFC_FILE_ID}, 
//            				AppConstants.ERROR);
//            	}
//	        }
//	        return config;
//	    }
	    
	    public void validateUploadFile(String fileName, String tempFolder, boolean checkFileSize, String fileID) throws CommonErrorException {
			File uploadFile = null;
			try {
				String filepath = tempFolder + fileName;
				// check file type is not .xls or .xlsx :
				// (File {0} is not expected file type {1}, {2}, {3})
				if (!filepath.toUpperCase().endsWith(
						AppConstants.FILE_FORMAT_EXCEL_XLS)
						&& !filepath.toUpperCase().endsWith(
								AppConstants.FILE_FORMAT_EXCEL_XLSX)) {
					throw new CommonErrorException(CST30000Messages.ERROR_UNEXPECTED_FILE_TYPE, 
							new String[] { fileName,
							AppConstants.FILE_FORMAT_EXCEL_XLS,
							AppConstants.FILE_FORMAT_EXCEL_XLSX, "" }, 
	        				AppConstants.ERROR);
				}

				uploadFile = new File(filepath);

				// check file exist
				if (!uploadFile.isFile() || !uploadFile.exists()) {
					throw new CommonErrorException(CST30000Messages.ERROR_FILE_DOES_NOT_EXIST, 
							new String[] { filepath }, 
	        				AppConstants.ERROR);
				}

				// file size = zero
				if (uploadFile.length() <= 0) {
					throw new CommonErrorException(CST30000Messages.ERROR_FILE_HAS_NO_DATA, 
							new String[] { fileName }, 
	        				AppConstants.ERROR);
				}
	            if(checkFileSize){
	            	InputStream is = new FileInputStream( uploadFile );
		    		int size = is.available();
			    	BigDecimal fileSize = new BigDecimal(size/(1024.0));
			    	int iFileSize = fileSize.intValue();
			    	BigDecimal maxFizeSize = getMaxFileSizeForUpload(fileID);
			    	int maxFileSizeChk = this.uploadConfig.getLimitDataSize();
			        if(maxFizeSize != null){
			        	maxFileSizeChk = maxFizeSize.intValue() * 1024;
			        }else{
			        	maxFileSizeChk = this.uploadConfig.getLimitDataSize();
			        }
			    	
			        if (iFileSize > maxFileSizeChk) {
			        	BigDecimal fileSizeMB = new BigDecimal(iFileSize/(1024.0)); 
			        	fileSizeMB = fileSizeMB.setScale(2, BigDecimal.ROUND_DOWN);
			    		throw new CommonErrorException(CST30000Messages.ERROR_UNDEFINED_ERROR, 
			    									  new String[]{"Found excel file size "+(fileSizeMB.toString())+" MB ("+fileName+") more than "+(maxFileSizeChk/1024)+" MB."}, 
			    									  AppConstants.ERROR);
			    	}
			    	is.close();
		    	}
			}catch(CommonErrorException e){
				throw e;
			}catch (Exception e) {
				throw new CommonErrorException(CST30000Messages.ERROR_UNDEFINED_ERROR, 
						  new String[]{e.getMessage()}, 
						  AppConstants.ERROR);
			}finally{
				uploadFile = null;
			}
		}
	    
	    public BigDecimal getMaxLengthUploadFileName(String functionId) {
			SystemInfoId sysId = new SystemInfoId();
			sysId.setCategory(AppConstants.SYS_CATEGORY_COMMON_UPLOAD);
			sysId.setSubCategory(AppConstants.SYS_SUB_CATEGORY_MAX_LENGTH_UPLOAD_FILENAME);
			sysId.setCode("01");

			SystemInfo sysInfo = systemRepository.findSystemMasterInfo(sysId);
			if(sysInfo != null){
				try{
					return new BigDecimal(sysInfo.getValue().toString());
				} catch (Exception ex){
					return null;
				}
			}else{
				return null;
			}
		}
	    
	    public BigDecimal getMaxFileSizeForUpload(String functionId) {
			SystemInfoId sysId = new SystemInfoId();
			sysId.setCategory(AppConstants.SYS_CATEGORY_COMMON_UPLOAD);
			sysId.setSubCategory(AppConstants.SYS_SUB_CATEGORY_MAX_SIZE_EXCEL);
			sysId.setCode(functionId);

			SystemInfo sysInfo = systemRepository.findSystemMasterInfo(sysId);
			if(sysInfo != null){
				try{
					return new BigDecimal(sysInfo.getValue().toString());
				} catch (Exception ex){
					return null;
				}
			}else{
				return null;
			}
		}
	    
//	    private boolean existClass(String fullyQualifiedJavaClass) {
//	        try {
//	            Class.forName(fullyQualifiedJavaClass);
//	        }
//	        catch (ClassNotFoundException e) {
//	            return false;
//	        }
//	        return true;
//	    }

//	    protected String generateFileName(String pattern, String fileId, String fromSystem, String toSystem) {
//	        if (Strings.isNullOrEmpty((String)pattern)) {
//	            return "";
//	        }
//	        String copyStr = pattern;
//	        copyStr = this.replaceString(copyStr, "[FILE_ID]", fileId);
//	        copyStr = this.replaceString(copyStr, "[FROM_SYSTEM]", fromSystem);
//	        copyStr = this.replaceString(copyStr, "[TO_SYSTEM]", toSystem);
//	        copyStr = this.replaceString(copyStr, "[COMPANY]", this.companyCode);
//	        String dateStr = this.dateToString(new Date(), "yyyyMMddhhmmss");
//	        copyStr = this.replaceString(copyStr, "[YYYY]", dateStr.substring(0, 4));
//	        copyStr = this.replaceString(copyStr, "[MM]", dateStr.substring(4, 6));
//	        copyStr = this.replaceString(copyStr, "[DD]", dateStr.substring(6, 8));
//	        copyStr = this.replaceString(copyStr, "[HH]", dateStr.substring(8, 10));
//	        copyStr = this.replaceString(copyStr, "[MI]", dateStr.substring(10, 12));
//	        copyStr = this.replaceString(copyStr, "[SS]", dateStr.substring(12, 14));
//	        return copyStr;
//	    }

//	    private String replaceString(String orgString, String regex, String newValue) {
//	        String newString = "";
//	        int index = orgString.indexOf(regex);
//	        newString = index >= 0 ? orgString.substring(0, index) + newValue + orgString.substring(index + regex.length()) : orgString;
//	        return newString;
//	    }

	    private String dateToString(Date dDate, String stFormat) {
	        String stDate = "";
	        try {
	            SimpleDateFormat sdf = new SimpleDateFormat(stFormat, Locale.US);
	            stDate = sdf.format(dDate);
	        }
	        catch (Exception e) {
	            e.printStackTrace();
	        }
	        return stDate;
	    }

	    protected String getExtension(String fileName) {
	        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
	        return extension;
	    }

	    protected String getFileExtention(MultipartFile file) throws FileUploadDownloadException {
	        int iPos = 0;
	        String strFileName = file.getOriginalFilename();
	        iPos = strFileName.lastIndexOf(46);
	        if (iPos != -1) {
	            String fileExtension = this.getExtension(strFileName).toUpperCase();
	            if (AppConstants.FILE_FORMAT_EXCEL_XLS.equals(fileExtension) || AppConstants.FILE_FORMAT_EXCEL_XLSX.equals(fileExtension)) {
	                return fileExtension;
	            }
	            String message = this.messageSource.getMessage(MessagesConstants.A_ERROR_FILE_TYPE_IS_INCORRECT, 
	            		(Object[])new String[]{file.getOriginalFilename(), "*.xls, *.xlsx", "", ""}, Locale.getDefault());
	            throw new FileUploadDownloadException(message.replaceAll(", , ", ""));
	        }
	        String message = this.messageSource.getMessage(MessagesConstants.A_ERROR_FILE_TYPE_IS_INCORRECT, 
	        		(Object[])new String[]{file.getOriginalFilename(), "*.xls, *.xlsx", "", ""}, Locale.getDefault());
	        throw new FileUploadDownloadException(message.replaceAll(", , ", ""));
	    }

	    protected void transferTargetFileToServer(String originalFilename, String destFilename, String strTempUpLocation, 
	    										  String strUpLocation, boolean shouldThrowException, boolean online) 
	    												  throws IOException, NoSuchMessageException, FileUploadDownloadException, CommonErrorException {
	        File destFile;
	        File sourceFile = new File(strTempUpLocation + originalFilename);
	        if (!sourceFile.renameTo(destFile = new File(strUpLocation + destFilename))) {
	            if (shouldThrowException) {
	            	if(online){
	            		throw new FileUploadDownloadException(this.messageSource.getMessage(CST30000Messages.ERROR_TRANSFER_FAILED, 
	            				(Object[])new String[]{originalFilename, strTempUpLocation, strUpLocation}, Locale.getDefault()));
	            	}else{
	            		throw new CommonErrorException(CST30000Messages.ERROR_TRANSFER_FAILED, 
	            				new String[]{originalFilename, strTempUpLocation, strUpLocation}, AppConstants.ERROR);
	            	}
	            }
	            this.logger.debug("Failed to move file " + originalFilename + " to " + strUpLocation);
	        }
	        String log = MessageFormat.format("File {0} was successfully moved to {1}.", originalFilename, strUpLocation);
	        this.logger.debug(log);
	        sourceFile = null;
	        destFile = null;
	    }

	    protected String uploadToTempFolder(MultipartFile file, String strFileName, String strUploadLocation, boolean appendTimestamp) throws Exception {
	    	String newFileName = strFileName;
    		if(appendTimestamp){
		    	int iPos = strFileName.lastIndexOf('.');
	    		String fileExtension = AppConstants.XLSX_REPORT_EXTENTION;
				if(iPos != -1) {					
					fileExtension = strFileName.substring(iPos + 1,strFileName.length());
				}
				for(int i=1; i<=5; i++){
					String suffxTime = "_" +this.dateToString(new Date(), "yyyyMMddhhmmss");
					
					newFileName = strFileName.replaceAll("(?i)."+fileExtension,"") + suffxTime + "."+fileExtension;
					String stFilePath = strUploadLocation + File.separator + newFileName;
					
					File f = new File(stFilePath);
					boolean exist = f.exists();
					if(exist){
						Thread.sleep(1000);
						if(f != null){
							f = null;
						}
					}else{
				        String filePath = strUploadLocation + newFileName;
				        File destFile = new File(filePath);
				        file.transferTo(destFile);
				        if(f != null){
							f = null;
						}
				        break;
					}
				}
			}else{
				String filePath = strUploadLocation + strFileName;
		        File destFile = new File(filePath);
		        file.transferTo(destFile);
			}
    		return newFileName;
	    }

//		public boolean checkHeaderTemplate(List<Sheet> workingSheet, FormulaEvaluator objFormulaEvaluator, HashMap<String, String[]> paramHeaderChk) throws Exception {
//			boolean valid = true;
//			//int headerCheckStartRow = xlsConvVo.getCheckHeadersStartRow().intValue() - 1;
//			int headerCheckEndRow = xlsConvVo.getCheckHeadersEndRow().intValue() - 1;
//			List checkHeaderLs = xlsConvVo.getCheckHeaders();
//			if(checkHeaderLs != null && checkHeaderLs.size() > 0){
//				for(int i=0; i < workingSheet.size(); i++){
//					Sheet curSheet = workingSheet.get(i);
//					int lastRow = curSheet.getLastRowNum();
//					if (lastRow == 0 || lastRow < headerCheckEndRow) {
//						throw new CommonErrorException(MessagesConstants.B_ERROR_INVALID_FILE_TEMPLATE, new String[]{}, AppConstants.ERROR);
//					}
//					for(int ri=0; ri<lastRow; ri++){
//						if(ri > headerCheckEndRow){
//							break;
//						}
//						
//						for(int j=0; j<checkHeaderLs.size(); j++){
//							
//							int headerCheckRow = xlsConvVo.getCheckHeaderStartRow(j).intValue() - 1;
//							int headerStartCol = xlsConvVo.getCheckHeaderStartCol(j).intValue() - 1;
//							int headerEndCol = xlsConvVo.getCheckHeaderEndCol(j).intValue() - 1;
//							if(ri==headerCheckRow){
//								HashMap obj = (HashMap)checkHeaderLs.get(j);
//								if(obj != null){
//									HashMap headerNameCheck = (HashMap)obj.get(CBW00000CommonExcelConversionDTO.TAG_CHECK_HEADER);
//									Row headerRow = curSheet.getRow(ri);
//									if (headerRow == null) {
//										throw new CommonErrorException(MessagesConstants.B_ERROR_INVALID_FILE_TEMPLATE, new String[]{}, AppConstants.ERROR);
//									}
//									
//									Iterator<Object> iter = headerNameCheck.entrySet().iterator();
//									while(iter.hasNext()){
//										Map.Entry<Object,Object> entry = (Map.Entry<Object,Object>) iter.next();
//										String rowKey = (String)entry.getKey();
//										int rowKeyIdx = Integer.parseInt(rowKey)-1;
//										HashMap mapInfo =  (HashMap)entry.getValue();
//										String value = (String)mapInfo.get(CBW00000CommonExcelConversionDTO.ATTR_VALUE);
//										String cellValue = "";
//										for (int k = headerStartCol; k <= headerEndCol; k++) {
//											Cell colNameCell = headerRow.getCell((short)k);
//											if (null == colNameCell) {
//												throw new CommonErrorException(MessagesConstants.B_ERROR_INVALID_FILE_TEMPLATE, new String[]{}, AppConstants.ERROR);
//											}
//											
//											if( colNameCell != null && 
//													(colNameCell.getCellType() == Cell.CELL_TYPE_STRING)){						
//													 cellValue = colNameCell.getStringCellValue();
//													 if(cellValue != null){
//														 cellValue = cellValue.trim();
//													 }
//												}
//												else if(colNameCell != null && 
//														(colNameCell.getCellType() == Cell.CELL_TYPE_NUMERIC)){
//														 cellValue = Double.toString(colNameCell.getNumericCellValue());
//												} 
//												else if(colNameCell == null || 
//														colNameCell.getCellType() == Cell.CELL_TYPE_BLANK){
//														cellValue = "";
//												}							
//											
//											if(k == rowKeyIdx){
//												if(!Strings.nullToEmpty(value).trim().equals(Strings.nullToEmpty(cellValue).trim())){
//													throw new CommonErrorException(MessagesConstants.B_ERROR_INVALID_FILE_TEMPLATE, new String[]{}, AppConstants.ERROR);
//												}
//												break;
//											}
//										}
//									}
//								}
//							}
//						}
//					}
//				}
//			}
//			return valid;
//		}
		
		public boolean checkHeaderTemplate(List<Sheet> workingSheet, FormulaEvaluator objFormulaEvaluator, HashMap<String, String[]> paramHeaderChk) throws Exception {
			boolean valid = true;
			//int headerCheckStartRow = xlsConvVo.getCheckHeadersStartRow().intValue() - 1;
			int headerCheckEndRow = xlsConvVo.getCheckHeadersEndRow().intValue() - 1;
			List checkHeaderLs = xlsConvVo.getCheckHeaders();
			if(checkHeaderLs != null && !checkHeaderLs.isEmpty()){
				for(int i=0; i < workingSheet.size(); i++){
					String[] paramHChk = null;
					if(paramHeaderChk!=null){
						paramHChk = paramHeaderChk.get(Integer.toString(i));
					}
					
					Sheet curSheet = workingSheet.get(i);
					int lastRow = curSheet.getLastRowNum();
					if (lastRow == 0 || lastRow < headerCheckEndRow) {
						valid = false;
						throw new CommonErrorException(MessagesConstants.B_ERROR_INVALID_FILE_TEMPLATE, new String[]{}, AppConstants.ERROR);
					}
					for(int ri=0; ri<=lastRow; ri++){
						if(ri > headerCheckEndRow){
							break;
						}
						
						for(int j=0; j<checkHeaderLs.size(); j++){
							
							int headerCheckRow = xlsConvVo.getCheckHeaderStartRow(j).intValue() - 1;
							int headerStartCol = xlsConvVo.getCheckHeaderStartCol(j).intValue() - 1;
							int headerEndCol = xlsConvVo.getCheckHeaderEndCol(j).intValue() - 1;
							if(ri==headerCheckRow){
								HashMap obj = (HashMap)checkHeaderLs.get(j);
								if(obj != null){
									HashMap headerNameCheck = (HashMap)obj.get(CommonDataFileUploadExcelConversionDTO.TAG_CHECK_HEADER);
									Row headerRow = curSheet.getRow(ri);
									if (headerRow == null) {
										valid = false;
										throw new CommonErrorException(MessagesConstants.B_ERROR_INVALID_FILE_TEMPLATE, new String[]{}, AppConstants.ERROR);
									}
									
									Iterator<Object> iter = headerNameCheck.entrySet().iterator();
									while(iter.hasNext()){
										Map.Entry<Object,Object> entry = (Map.Entry<Object,Object>) iter.next();
										String rowKey = (String)entry.getKey();
										int rowKeyIdx = Integer.parseInt(rowKey)-1;
										HashMap mapInfo =  (HashMap)entry.getValue();
										String value = (String)mapInfo.get(CommonDataFileUploadExcelConversionDTO.ATTR_VALUE);
										String headLabel = (String)mapInfo.get(CommonDataFileUploadExcelConversionDTO.ATTR_LABEL);
										for (int k = headerStartCol; k <= headerEndCol; k++) {
											Cell colNameCell = headerRow.getCell((short)k);
											String cellValue = readCellHeaderValue(colNameCell, objFormulaEvaluator);
											
											if(k == rowKeyIdx){
												if(Strings.nullToEmpty(value).trim().equalsIgnoreCase(CHECK_WITH_PARAM)){
													String mandatoryChk = (String)mapInfo.get(CommonDataFileUploadExcelConversionDTO.ATTR_MANDATORY_FIELD);
													if(Strings.nullToEmpty(mandatoryChk).trim().equalsIgnoreCase("true")){
														if(Strings.isNullOrEmpty(cellValue)){
															String errMsg = messageSource.getMessage(MessagesConstants.B_ERROR_INVALID_EMPTY_FIELD, 
																	new String[]{headLabel}, Locale.getDefault());
															logger.error(errMsg);
															loggerDb.error(appId, MessagesConstants.B_ERROR_INVALID_EMPTY_FIELD, errMsg, createBy);
															if(!Strings.isNullOrEmpty(appId2)) loggerDb.error(appId2, MessagesConstants.B_ERROR_INVALID_EMPTY_FIELD, errMsg, createBy);
															valid = false;
														}else{
															String lengthChk = (String)mapInfo.get(CommonDataFileUploadExcelConversionDTO.ATTR_LENGTH);
															if(Strings.isNullOrEmpty(lengthChk)==false){
																try{
																	int valueLen = Strings.nullToEmpty(cellValue).trim().length();
																	int intLengthChk = Integer.parseInt(lengthChk);
																	if(valueLen>intLengthChk){
																		String errMsg = messageSource.getMessage(MessagesConstants.B_ERROR_INVALID_LENGTH, 
																				new String[]{headLabel, lengthChk}, Locale.getDefault());
																		logger.error(errMsg);
																		loggerDb.error(appId, MessagesConstants.B_ERROR_INVALID_LENGTH, errMsg, createBy);
																		if(!Strings.isNullOrEmpty(appId2)) loggerDb.error(appId2, MessagesConstants.B_ERROR_INVALID_LENGTH, errMsg, createBy);
																		valid = false;
																	}
																}catch (Exception e){																
																}
															}
															if(paramHChk!=null && paramHChk.length > 0){
																String hparamchk = (String)paramHChk[headerCheckRow];
																if(!Strings.nullToEmpty(cellValue).trim().equals(hparamchk)){
																	String errMsg = messageSource.getMessage(MessagesConstants.B_ERROR_VALUE_COMPARE_NOT_MATCH, 
																					new String[]{headLabel+"("+cellValue+")", headLabel+"("+hparamchk+") in Parameter data"}, Locale.getDefault());
																	logger.error(errMsg);
																	loggerDb.error(appId, MessagesConstants.B_ERROR_VALUE_COMPARE_NOT_MATCH, errMsg, createBy);
																	if(!Strings.isNullOrEmpty(appId2)) loggerDb.error(appId2, MessagesConstants.B_ERROR_VALUE_COMPARE_NOT_MATCH, errMsg, createBy);
																	valid = false;
																}
															}
														}
													}else{
														if(paramHChk!=null && paramHChk.length > 0){
															String hparamchk = (String)paramHChk[headerCheckRow];
															if(!Strings.nullToEmpty(cellValue).trim().equals(hparamchk)){
																String errMsg = messageSource.getMessage(MessagesConstants.B_ERROR_VALUE_COMPARE_NOT_MATCH, 
																			    new String[]{headLabel+"("+cellValue+")", headLabel+"("+hparamchk+") in Parameter data"}, Locale.getDefault());
																logger.error(errMsg);
																loggerDb.error(appId, MessagesConstants.B_ERROR_VALUE_COMPARE_NOT_MATCH, errMsg, createBy);
																if(!Strings.isNullOrEmpty(appId2)) loggerDb.error(appId2, MessagesConstants.B_ERROR_VALUE_COMPARE_NOT_MATCH, errMsg, createBy);
																valid = false;
															}
														}
													}
													
												}else{
													if(!Strings.nullToEmpty(value).trim().equals(Strings.nullToEmpty(cellValue).trim())){
														String errMsg = messageSource.getMessage(MessagesConstants.B_ERROR_VALUE_COMPARE_NOT_MATCH, 
																	    new String[]{cellValue, value+" in XML file"}, Locale.getDefault());
														logger.error(errMsg);
														loggerDb.error(appId, MessagesConstants.B_ERROR_VALUE_COMPARE_NOT_MATCH, errMsg, createBy);
														if(!Strings.isNullOrEmpty(appId2)) loggerDb.error(appId2, MessagesConstants.B_ERROR_VALUE_COMPARE_NOT_MATCH, errMsg, createBy);
														valid = false;
													}
												}
												break;
											}
										}
									}
								}
							}
						}
					}
				}
			}
			return valid;
		}
	    
		public Object[] generateData(Workbook workbook, FormulaEvaluator objFormulaEvaluator) throws Exception {
			//Generate Data
			Object[] result = null;
			Integer[] intWrkShtIdx = xlsConvVo.getArrayWorkSheetIdx();
			String[] strWrkShtName = xlsConvVo.getArrayWorkSheetName();
			if (intWrkShtIdx != null && intWrkShtIdx.length > 0) {
				//Higher priority than Work Sheet Name
				for (int i = 0; i < intWrkShtIdx.length; i++) {
					Sheet curSheet = workbook.getSheetAt(intWrkShtIdx[i].intValue());
					result = generateData(curSheet, objFormulaEvaluator);
				}
			}else if (strWrkShtName != null && strWrkShtName.length > 0) {
				for (int i = 0; i < strWrkShtName.length; i++) {
					Sheet curSheet = workbook.getSheet(strWrkShtName[i]);
					result = generateData(curSheet, objFormulaEvaluator);
				}
			}
			
			return result;
		}

		/*
		 * Function   : getDataHeaderOfEachFunction()
		 * Description: Override in your Service class for manual validate Header section
		 * return true  = No error found
		 * 		  false = Error found (Invalid template)
		 */
		public boolean getDataHeaderOfEachFunction(List<Sheet> workingSheet, FormulaEvaluator objFormulaEvaluator) throws Exception{
			return true;
		}
		
		public Object[] generateData(Sheet curSheet, FormulaEvaluator objFormulaEvaluator) throws Exception {
			List<Object[]> dataList = new ArrayList<Object[]>();
			boolean validAll = true;
			boolean validHeaderOfDetail = true;
			if (xlsConvVo.getExcelDetailConfig() != null) {
				int detailStartRow = xlsConvVo.getDetailStartRow().intValue() - 1;
				int detailStartCol = xlsConvVo.getDetailStartCol().intValue() - 1;
				
				int rowNum = curSheet.getLastRowNum();// End Row
	            if (null != xlsConvVo.getDetailEndRow()) {
	                rowNum = xlsConvVo.getDetailEndRow().intValue() - 1;
	            }
				if (rowNum == 0 || rowNum < detailStartRow) {
					return new Object[]{validAll, dataList};
				}
				
//				Row colNameRow = curSheet.getRow(xlsConvVo.getDetailStartCol().intValue());
//				if (null == colNameRow) {
////					throw CSTD0054ErrorUtil.generateError("MTRI3027AERR", "");
//				}
				int endCol = -1;
				if (xlsConvVo.getDetailEndCol()!=null) {
					endCol = xlsConvVo.getDetailEndCol().intValue();
				}
				maxColData = endCol;
				for (int i = 0; i <= rowNum; i++) {
					Row dataRow = curSheet.getRow(i);
					if (dataRow != null) {	
						
						if(endCol == -1){
							if (null == xlsConvVo.getDetailEndCol()) {
								endCol = dataRow.getLastCellNum();
								maxColData = endCol;
							}
						}
						
						int endColChk = dataRow.getLastCellNum();
						if(endColChk > maxColData){
							if(alreadyLoggedInvaidTemplate==false){
			            		alreadyLoggedInvaidTemplate = true;
								String errMsg = messageSource.getMessage(MessagesConstants.B_ERROR_INVALID_FILE_TEMPLATE, new String[]{}, Locale.getDefault());
								logger.error(errMsg);
								loggerDb.error(appId, MessagesConstants.B_ERROR_INVALID_FILE_TEMPLATE, errMsg, createBy);
							}
							validAll = false;
							continue;
						}
						if(i<detailStartRow){//if current row < start row of detail from XML configuration system will continue read next row
							continue;
						}
						
						Object[] obj = getDataSection(detailStartCol, endCol, dataRow, i, null, objFormulaEvaluator, dataList);
						boolean validDetail = (boolean)obj[0];
						boolean validHd = (boolean)obj[1];
						if(validDetail == false){
							validAll = validDetail;
						}
						if(validHd == false){
							validHeaderOfDetail = validHd;
						}
					}
				}
			}
			return new Object[]{validAll, validHeaderOfDetail, dataList};
		}
		
		public Object[] getDataSection(int startCol, 
				  int endCol,
				  Row dataRow,
				  int rowIdx,
				  Object[] headerChk,
				  FormulaEvaluator objFormulaEvaluator,
				  List<Object[]> dataList) throws Exception {
			return new Object[]{false, false, dataList};
		}
			    
		protected Object[] readCell(Cell dataCell, String columnName, int columnIdx, String col1ofRow, int rowIdx) {
			boolean valid = true;
		    String cellValue = "";
		    String cellValueOriginal = "";
		    if(dataCell!=null){
		    	if(Strings.isNullOrEmpty(col1ofRow)){
		    		col1ofRow = Strings.isNullOrEmpty(columnName) ? "Column No. " + (columnIdx+1) : columnName;
		    	}
			    int cellType = dataCell.getCellType();
			    
			    if (cellType == Cell.CELL_TYPE_BLANK) {
		            cellValue = "";
		            cellValueOriginal = cellValue;
		        }
			    else if (cellType == Cell.CELL_TYPE_STRING) {
			    	boolean flg = true;
		            // Read the Numeric first
		            try {
		            	String tmpValue = dataCell.getStringCellValue();
		            	double decimalPoint = (Double.parseDouble(tmpValue) % 1);
		                if (decimalPoint == 0) {
		                    cellValue = tmpValue;
		                }
		                else {
		                	cellValue = String.valueOf(new BigDecimal("" + tmpValue));
		                }
		            } catch (Exception e) {
		                flg = false;
		            }
		            // If can not read Formula Numeric Cell
		            // Try to read Formula Text Cell
		            if (!flg) {
		                cellValue = dataCell.getStringCellValue();
		            }
		        	try{
		        		cellValueOriginal = cellValue;
		        		String convertToNumberic = "N";
		        		String checkAlphaNumeric = "N";
		        		
		        		HashMap detailMap = this.xlsConvVo.getDetailMappingField();
		                if(detailMap != null){
		                	HashMap mapInfoDetail = (HashMap)detailMap.get(columnName);
		                	if(mapInfoDetail!=null){
				        		convertToNumberic = (String)mapInfoDetail.get(CommonDataFileUploadExcelConversionDTO.ATTR_CONVERT_TO_NUMBERIC);
				        		checkAlphaNumeric = (String)mapInfoDetail.get(CommonDataFileUploadExcelConversionDTO.ATTR_ALPHANUMERIC_CHECK);
		                	}
			                if(convertToNumberic!=null && convertToNumberic.equalsIgnoreCase("Y") && !Strings.isNullOrEmpty(cellValue)){
			        			Object[] result = convertNumbericValue(cellValue, columnName, columnIdx, col1ofRow, rowIdx);
			        			valid = (boolean)result[0];
			        			cellValue = (String)result[1];        			 
			        		}else if(checkAlphaNumeric!=null && checkAlphaNumeric.equalsIgnoreCase("Y")){
			        			Object[] result = checkAlphaNumeric(mapInfoDetail, cellValueOriginal, rowIdx,columnIdx, col1ofRow);
			                	valid = (boolean)result[0];
			        			cellValue = (String)result[1];  
			        		}else{
			        			cellValue = dataCell.getStringCellValue();
			            		cellValueOriginal = cellValue;
			        		}
			                
		                }else{
		        			cellValue = dataCell.getStringCellValue();
		            		cellValueOriginal = cellValue;
		        		}
		                
		        	}catch(Exception e){
		        		cellValue = dataCell.getStringCellValue();
		        		cellValueOriginal = cellValue;
		        	}
			    }
		        else if (cellType == Cell.CELL_TYPE_NUMERIC) {
		        	if(DateUtil.isCellDateFormatted(dataCell)){
				    	cellValue = FormatUtil.convertDateToString(dataCell.getDateCellValue(), AppConstants.DATE_STRING_SCREEN_FORMAT);
				    	cellValueOriginal = cellValue;
				    }else{
			            double tmpValue = dataCell.getNumericCellValue();
			            double decimalPoint = (tmpValue % 1);
			            
			            if (decimalPoint == 0) {
			                cellValue = String.valueOf((long)tmpValue);
			            }
			            else {
			                cellValue = String.valueOf(new BigDecimal("" + tmpValue));
			            }
			            cellValueOriginal = cellValue;
			            Object[] result = this.convertNumbericValue(cellValue, columnName, columnIdx, col1ofRow, rowIdx);
			            valid = (boolean)result[0];
						cellValue = (String)result[1];   
				    }
		        }
		        else if (cellType == Cell.CELL_TYPE_FORMULA){
		            boolean flg = true;
		            // Read the Numeric first
		            try {
		            	double tmpValue = dataCell.getNumericCellValue();
		            	double decimalPoint = (tmpValue % 1);
		                if (decimalPoint == 0) {
		                    cellValue = String.valueOf((long)tmpValue);
		                }
		                else {
		                    //cellValue = String.valueOf(tmpValue);
		                	cellValue = String.valueOf(new BigDecimal("" + tmpValue));
		                }
		            } catch (Exception e) {
		                flg = false;
		            }
		            // If can not read Fomular Numeric Cell
		            // Try to read Fomular Text Cell
		            if (!flg) {
		                cellValue = dataCell.getStringCellValue();
		            }
		            cellValueOriginal = cellValue;
		            Object[] result = this.convertNumbericValue(cellValue, columnName, columnIdx, col1ofRow, rowIdx);
		            valid = (boolean)result[0];
					cellValue = (String)result[1];   
		        }
		        else {
		            cellValue = dataCell.getStringCellValue();
		            cellValueOriginal = cellValue;
		        }
		    }
	        return new Object[]{valid, cellValue.trim(),cellValueOriginal.trim()};
	    }
		
		public String readCellHeaderValue(Cell cell, FormulaEvaluator objFormulaEvaluator){
			DataFormatter objDefaultFormat = new DataFormatter();
			objFormulaEvaluator.evaluate(cell);
	        String cellValue = objDefaultFormat.formatCellValue(cell, (FormulaEvaluator)objFormulaEvaluator);
			return cellValue;
		}
	    
		protected String checkAndReplaceValueBeforeChkLength(HashMap mapInfo, String cellValue){
			String replaceValueBeforeChkLength = (String)mapInfo.get(CommonDataFileUploadExcelConversionDTO.ATTR_REPLACE_CALUE_BEFORE_CHK_LENGTH);
			if (replaceValueBeforeChkLength != null && replaceValueBeforeChkLength.equalsIgnoreCase("Y")) {
				String replaceKey = (String)mapInfo.get(CommonDataFileUploadExcelConversionDTO.ATTR_REPLACE_KEY);
				if (replaceKey != null && replaceKey.length() > 0) {
					String replaceToValue = (String)mapInfo.get(CommonDataFileUploadExcelConversionDTO.ATTR_REPLACE_TO_VALUE);
					if (replaceToValue != null && replaceToValue.length() > -1) {
						if(cellValue != null){
							cellValue = cellValue.replaceAll(replaceKey, replaceToValue);
						}
					}
				}	
			}
			return cellValue;
		}
		
		private Object[] convertNumbericValue(String cellValue, String columnName, int columnIdx, String col1ofRow, int rowIdx){
			HashMap detailMap = this.xlsConvVo.getDetailMappingField();
			boolean valid = true;
	        if(detailMap != null){
	        	HashMap mapInfo = (HashMap)detailMap.get(columnName);
	        	if(mapInfo!=null){
	        		Object[] objs = checkMatchRegularAndReplaceValueBeforeGenByFormat(mapInfo, cellValue, columnIdx, col1ofRow, rowIdx);
	        		valid = (boolean)objs[0];
	        		cellValue = Strings.nullToEmpty((String)objs[1]);
	        		if(valid){
		            	String decimal = (String)mapInfo.get(CommonDataFileUploadExcelConversionDTO.ATTR_DECIMAL);
		            	if(decimal!=null && !decimal.equalsIgnoreCase("NULL")){
		            		int intDecimal = Integer.parseInt(decimal);
		            		
		            		String roundHaftUp = "N";
		            		roundHaftUp = (String)mapInfo.get(CommonDataFileUploadExcelConversionDTO.ATTR_ROUND_HAFT_UP);
		     
		                    if(roundHaftUp!=null && roundHaftUp.equalsIgnoreCase("Y")){
		                    	BigDecimal tmpcellValue = new BigDecimal(cellValue);
		                		tmpcellValue = tmpcellValue.setScale(intDecimal, BigDecimal.ROUND_HALF_UP);
		                		
		                		cellValue = tmpcellValue.toString();
		            		}else{
		            			int index = cellValue.lastIndexOf(".");
		            			String paddingzero = "Y";
		                		if(index > 0){
		                			String decimalString = cellValue.substring(index+1, cellValue.length());
		                			if(decimalString !=null && decimalString.length() >= intDecimal){
		                				paddingzero = "N";
		                			}
		                		}
		            			
		            			BigDecimal tmpcellValue = new BigDecimal(cellValue);
		                		if(paddingzero.equals("Y")){
		                			tmpcellValue = tmpcellValue.setScale(intDecimal);
		                		}
		                		cellValue = tmpcellValue.toString();
		            		}
		            	}
	        		}
	        	}
	        }
	        return new Object[]{valid, cellValue};
		}
		
		private Object[] checkAlphaNumeric(HashMap mapInfo, String cellValue, int rowIdx, int columnIdx, String colName){
			String alphaNumericCheck = (String)mapInfo.get(CommonDataFileUploadExcelConversionDTO.ATTR_ALPHANUMERIC_CHECK);
			boolean valid = true;
			boolean passChkReg = true;
			if (alphaNumericCheck != null && AppConstants.YES_STR.equals(alphaNumericCheck)) {	
//				Pattern p = Pattern.compile("[^a-zA-Z0-9]");
//				boolean hasSpecialChar = p.matcher(s).find();
				if (cellValue != null && cellValue.trim().length() > 0 && StringUtils.isAlphanumeric(cellValue)) {
					passChkReg = true;
				}else{
					passChkReg = false;
				}
			}
			if(passChkReg){
				valid = true;
			}else{
				String fieldLabel = "";
				if(colName!=null){
					fieldLabel = colName;
					if(rowIdx > 0){
						fieldLabel = fieldLabel + " {Row No. "+(rowIdx+1)+"}";
					}
				}
				String errMsg = messageSource.getMessage(MessagesConstants.B_ERROR_INVALID_FORMAT, 
						new String[]{fieldLabel, "Character or Number only"}, Locale.getDefault());
				logger.error(errMsg);
				loggerDb.error(appId, MessagesConstants.B_ERROR_INVALID_FORMAT, errMsg, createBy);
				if(!Strings.isNullOrEmpty(appId2)) loggerDb.error(appId2, MessagesConstants.B_ERROR_INVALID_FORMAT, errMsg, createBy);
				valid = false;
				
			}
			return new Object[]{valid, cellValue};
		}
		
		private Object[] checkMatchRegularAndReplaceValueBeforeGenByFormat(HashMap mapInfo, String cellValue, int columnIdx, String col1ofRow, int rowIdx){
			String regExCheck = (String)mapInfo.get(CommonDataFileUploadExcelConversionDTO.ATTR_REGEX_CHECK);
			boolean valid = true;
			boolean passChkReg = true;
			if (regExCheck != null && regExCheck.length() > 0) {				
				if (cellValue != null && cellValue.trim().length() > 0 && cellValue.matches(regExCheck)) {
					passChkReg = true;
				}else{
					passChkReg = false;
				}
			}
			if(passChkReg){
				String replaceKey = (String)mapInfo.get(CommonDataFileUploadExcelConversionDTO.ATTR_REPLACE_KEY);
				if (replaceKey != null && replaceKey.length() > 0) {
					String replaceToValue = (String)mapInfo.get(CommonDataFileUploadExcelConversionDTO.ATTR_REPLACE_TO_VALUE);
					if (replaceToValue != null && replaceToValue.length() > -1) {
						if(cellValue != null){
							cellValue = cellValue.replaceAll(replaceKey, replaceToValue);
						}
					}
				}	
			}else{
				String fieldLabel = "";
				if(col1ofRow!=null){
					fieldLabel = col1ofRow;
				}
				if(rowIdx > 0){
					fieldLabel = fieldLabel + " {Row No. "+(rowIdx+1)+"}";
				}
				if(gmHeaderChk!=null && !gmHeaderChk.isEmpty() && this.gmHeaderChk.toArray()[columnIdx] !=null){
					if(Strings.isNullOrEmpty((String)this.gmHeaderChk.toArray()[columnIdx]) == false){
						fieldLabel+=" of "+this.gmHeaderChk.toArray()[columnIdx]+"("+cellValue+")";
					}				
				}
				String correctFormat = (String)mapInfo.get(CommonDataFileUploadExcelConversionDTO.ATTR_CORRECT_NUMBER_FORMAT);
				String errMsg = messageSource.getMessage(MessagesConstants.B_ERROR_INVALID_FORMAT, 
						new String[]{fieldLabel, Strings.nullToEmpty(correctFormat)}, Locale.getDefault());
				logger.error(errMsg);
				loggerDb.error(appId, MessagesConstants.B_ERROR_INVALID_FORMAT, errMsg, createBy);
				if(!Strings.isNullOrEmpty(appId2)) loggerDb.error(appId2, MessagesConstants.B_ERROR_INVALID_FORMAT, errMsg, createBy);
				valid = false;
				
			}
			return new Object[]{valid, cellValue};
		}

		public Date convertToDate(HashMap mapInfo, 
								   String cellValue, 
								   String columnName) throws Exception {		
					String dbColumnName = (String)mapInfo.get(CommonDataFileUploadExcelConversionDTO.ATTR_VALUE);
					if(dbColumnName==null){
						dbColumnName = columnName;
					}
					HashMap colInfo = (HashMap)tableMetaData.get(dbColumnName);
					String colType = (String)colInfo.get("TYPE");
					if (colType.equals("DATE")) {
						int length = AppConstants.DATE_STRING_SCREEN_FORMAT.length();
						int dataLength = cellValue.length();
					
					
					}
					return null;
		}

		public Object[] checkLength(HashMap mapInfo, 
									   String cellValue, 
									   String columnName, 
									   String cellValueOriginal,
									   int columnIdx,
									   String col1ofRow,
									   int rowIdx) throws Exception {		
			String dbColumnName = (String)mapInfo.get(CommonDataFileUploadExcelConversionDTO.ATTR_VALUE);
			if(dbColumnName==null){
				dbColumnName = columnName;
			}
			HashMap colInfo = (HashMap)tableMetaData.get(dbColumnName);
			String colType = (String)colInfo.get("TYPE");
			if (colType.equals("DATE")) {				
			    int length = AppConstants.DATE_STRING_SCREEN_FORMAT.length();
				String convertToDateBy = (String)mapInfo.get(CommonDataFileUploadExcelConversionDTO.ATTR_CONVERT_STRING_TO_DATE);
				if(convertToDateBy!=null){
					length = convertToDateBy.length();
				}
			    int dataLength = cellValue.length();
			    boolean valid = validateLength(cellValue, dataLength, length, columnIdx, col1ofRow, rowIdx);
				return new Object[]{valid, cellValue};
				
			}
			else if (colType.equals("NUMBER")) {
			    // Revise the Length calculation
			    int dbLength = ((Integer)colInfo.get("PRECISION")).intValue();
			    if (((Integer)colInfo.get("SCALE")).intValue() > 0) {
			        dbLength += 1;
			    }
				String overrideLength = (String)mapInfo.get(CommonDataFileUploadExcelConversionDTO.ATTR_LENGTH);
				if (overrideLength != null && overrideLength.length() > 0) {
					dbLength = Integer.parseInt(overrideLength);
				}
				
				int dataLength = cellValue.length();
				boolean valid = validateLength(cellValue, dataLength, dbLength, columnIdx, col1ofRow, rowIdx);
				return new Object[]{valid, cellValue.trim()};
			}
			else {
				int dbLength = ((Integer)colInfo.get("LENGTH")).intValue();
				String overrideLength = (String)mapInfo.get(CommonDataFileUploadExcelConversionDTO.ATTR_LENGTH);
				if (overrideLength != null && overrideLength.length() > 0) {
					dbLength = Integer.parseInt(overrideLength);
				}

				String cellValueLog = cellValue;
				cellValue = checkAndReplaceValueBeforeChkLength(mapInfo, cellValueLog);
				
				int dataLength = cellValue.trim().length();
				boolean valid = validateLength(cellValue, dataLength, dbLength, columnIdx, col1ofRow, rowIdx);
				return new Object[]{valid, cellValue.trim()};
			}
		}
		
		private boolean validateLength(String cellValue, int dataLength, int dbLength, int columnIdx, String col1ofRow, int rowIdx){
			boolean valid = true;
			if (dataLength > dbLength) {
				String fieldLabel = "";
				if(col1ofRow!=null){
					fieldLabel = col1ofRow;
				}
				if(rowIdx > 0){
					fieldLabel = fieldLabel + " {Row No. "+(rowIdx+1)+"}";
				}
				if(gmHeaderChk!=null && !gmHeaderChk.isEmpty() && this.gmHeaderChk.toArray()[columnIdx] !=null){
					if(Strings.isNullOrEmpty((String)this.gmHeaderChk.toArray()[columnIdx]) == false){
						fieldLabel+=" of "+this.gmHeaderChk.toArray()[columnIdx]+"("+cellValue+")";
					}
				}
				String errMsg = messageSource.getMessage(MessagesConstants.B_ERROR_INVALID_LENGTH, 
						new String[]{fieldLabel, Integer.toString(dbLength)}, Locale.getDefault());
				logger.error(errMsg);
				loggerDb.error(appId, MessagesConstants.B_ERROR_INVALID_LENGTH, errMsg, createBy);
				if(!Strings.isNullOrEmpty(appId2)) loggerDb.error(appId2, MessagesConstants.B_ERROR_INVALID_LENGTH, errMsg, createBy);
				valid = false;
			}
			return valid;
		}
		
		
		
	}
