/******************************************************
 * Program History
 * 
 * Project Name	            :  GWRDS : 
 * Client Name				:  TDEM
 * Package Name             :  th.co.toyota.bw0.batch.preprocess.main
 * Program ID 	            :  CBW02120Preprocess.java
 * Program Description	    :  PAMs Rundown Upload
 * Environment	 	    	:  Java 7
 * Author		    		:  Thanawut T.
 * Version		    		:  1.0
 * Creation Date            :  30 August 2017
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2013-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/
package th.co.toyota.bw0.batch.preprocess.main;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import th.co.toyota.config.AppConfig;
import th.co.toyota.bw0.api.common.CBW00000Util;
import th.co.toyota.bw0.api.constants.AppConstants;
import th.co.toyota.bw0.api.exception.common.CommonErrorException;
import th.co.toyota.bw0.api.repository.common.IBW00000Repository;
import th.co.toyota.bw0.batch.preprocess.service.CBW02120PreprocessService;
import th.co.toyota.st3.api.constants.CST30000Constants;
import th.co.toyota.st3.api.constants.CST30000Messages;
import th.co.toyota.st3.api.util.IST30000LoggerDb;
import th.co.toyota.st3.batch.receiving.CST31250FileReceivingCmdOptions;
import th.co.toyota.st3.batch.receiving.IST31250PreProcessClass;

public class CBW02120Preprocess implements IST31250PreProcessClass {

	final Logger logger = LoggerFactory.getLogger(CBW02120Preprocess.class);

	@Autowired
	private IST30000LoggerDb loggerBBW02120;

	@Autowired
	protected MessageSource messageSource;

	@Autowired
	private CBW02120PreprocessService service;

	@Autowired
	private IBW00000Repository repository;
	
	private String batchName = "Upload PAMs Rundown preprocess";
	
	public Timestamp sysdate;
	
	//for test on your computer
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		ApplicationContext appContext = new AnnotationConfigApplicationContext(AppConfig.class);
		CBW02120Preprocess main = appContext.getBean(CBW02120Preprocess.class);
		CST31250FileReceivingCmdOptions arg0 = new CST31250FileReceivingCmdOptions();
		arg0.setModuleId("BW02");
		arg0.setFunctionId("BBW02120");
		arg0.setApplicationId("1800001459");
		arg0.setUser("gwrds03");
		
		List<String> additionalPrameters = new ArrayList<>();
		additionalPrameters.add("TDEM");
		additionalPrameters.add("R");
		additionalPrameters.add("Jan-19");
		additionalPrameters.add("D-14");
		additionalPrameters.add("IMC");
		additionalPrameters.add("Hiace");//5
		additionalPrameters.add("STM");
		additionalPrameters.add("Engine");
		additionalPrameters.add("1ZR");
		additionalPrameters.add("Test_Incident_0003_3_Rundown.xls");
		additionalPrameters.add("gwrds03");
		additionalPrameters.add("1800001459");

		arg0.setAdditionalPrameters(additionalPrameters);
		main.preProcessInterfaceFileData(arg0);
	}

	@Override
	public int preProcessInterfaceFileData(CST31250FileReceivingCmdOptions arg0) {
		String appId = arg0.getApplicationId();
		String createBy = arg0.getUser();
		Connection conn = null;
		StringBuffer paramStr1 = new StringBuffer();
		StringBuffer paramStr2 = new StringBuffer();
		StringBuffer paramStr3 = new StringBuffer();
		try {
			conn = repository.getConnection();
			
			// step 1 : write start log
			String msg = messageSource.getMessage(CST30000Messages.INFO_PROCESS_START, new String[] { batchName }, Locale.getDefault());
			logger.info(msg);
			loggerBBW02120.start(appId, CST30000Messages.INFO_PROCESS_START, msg, createBy);
			
			service.sysdate = this.sysdate;
			int result = service.validate(conn, arg0);
			if ((result == CST30000Constants.SUCCESS || result == CST30000Constants.WARNING) && service.totalRead>0) {
				Object[] resultChk = service.manageTransaction(conn, result);
				result =(int)resultChk[0];
			}
					
			paramStr1.append("TOTAL SUMMARY INPUT: TB_S_PAMS_RUNDOWN : Total read records=").append(service.totalRead);
			
			paramStr2.append(" OUTPUT:");
			paramStr2.append(" TB_R_RUNDOWN_KOMPO_STS : Total inserted records = 1");
			paramStr2.append(" TB_R_PAMS_RUNDOWN : Total inserted records = ").append(service.insertedCnt);
			paramStr2.append(", Total warning records = ").append(service.warningCnt);
			paramStr2.append(", Total error records = ").append(service.errorCnt);
			
			paramStr3.append("Upload file: "+ service.fileName);
			
			if (result == CST30000Constants.SUCCESS) {
				msg = messageSource.getMessage(
						CST30000Messages.INFO_PROCESS_END_SUCCESS,
						new String[] { batchName, paramStr1.toString(), paramStr2.toString(), paramStr3.toString() },
						Locale.getDefault());
				logger.info(msg);
				loggerBBW02120.end(appId, CST30000Messages.INFO_PROCESS_END_SUCCESS, msg, createBy);
				
				repository.updateStatusOfLogUpload(appId, createBy, AppConstants.STATUS_SUCCESS, AppConstants.STATUS_SUCCESS_DESC+" Upload PAMs Rundown.");
			}else if (result == CST30000Constants.ERROR) {				
				msg = messageSource.getMessage(
						CST30000Messages.INFO_PROCESS_END_ERROR,
						new String[] { batchName, paramStr1.toString(), paramStr2.toString(), paramStr3.toString() },
						Locale.getDefault());
				logger.error(msg);
				loggerBBW02120.endError(appId, CST30000Messages.INFO_PROCESS_END_ERROR, msg, createBy);
				
				repository.updateStatusOfLogUpload(appId, createBy, AppConstants.STATUS_ERROR, AppConstants.STATUS_ERROR_DESC+" Upload PAMs Rundown.");
			}else if (result == CST30000Constants.WARNING) {
				msg = messageSource.getMessage(
						CST30000Messages.INFO_PROCESS_END_WARNING,
						new String[] { batchName, paramStr1.toString(), paramStr2.toString(), paramStr3.toString() },
						Locale.getDefault());
				logger.warn(msg);
				loggerBBW02120.endWarning(appId, CST30000Messages.INFO_PROCESS_END_WARNING, msg, createBy);		
				
				repository.updateStatusOfLogUpload(appId, createBy, AppConstants.STATUS_WARNING, AppConstants.STATUS_WARNING_DESC+" Upload PAMs Rundown.");
			}
			return result;
		} catch (Exception e) {		
			String errMsg = messageSource
					.getMessage(CST30000Messages.ERROR_UNDEFINED_ERROR,
							new String[] { CBW00000Util.genMessageOfException(e) },
							Locale.getDefault());
			logger.error(errMsg);
			loggerBBW02120.error(appId, CST30000Messages.ERROR_UNDEFINED_ERROR,
					errMsg, createBy);
			
			try {
				errMsg = messageSource.getMessage(
						CST30000Messages.INFO_PROCESS_END_ERROR,
						new String[] { batchName, paramStr1.toString(), paramStr2.toString(), paramStr3.toString() },
						Locale.getDefault());
				logger.error(errMsg);
				loggerBBW02120.endError(appId, CST30000Messages.INFO_PROCESS_END_ERROR, errMsg, createBy);
				repository.updateStatusOfLogUpload(appId, createBy, AppConstants.STATUS_ERROR, AppConstants.STATUS_ERROR_DESC+" Upload PAMs Rundown.");
				e.printStackTrace();
			} catch (CommonErrorException e1) {
				errMsg = messageSource
						.getMessage(CST30000Messages.ERROR_UNDEFINED_ERROR,
								new String[] { CBW00000Util.genMessageOfException(e1) },
								Locale.getDefault());
				logger.error(errMsg);
				loggerBBW02120.error(appId, CST30000Messages.ERROR_UNDEFINED_ERROR,
						errMsg, createBy);
			}
		
			return CST30000Constants.ERROR;
		}finally{
			try {
				if(conn!=null && !conn.isClosed()){
					conn.close();
					conn = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void insertLogDetail(Object[] keys) throws Exception {
		Connection conn = null;
		boolean completed = false;
		try{
			conn = repository.getConnection();
			conn.setAutoCommit(false);
			service.deleteAllLogDetail(conn, keys);
			service.insertLogDetail(conn);
			service.insertLogDetailOther(conn, keys);
			completed = true;
		}catch (CommonErrorException e){
			completed = false;
			throw e;
		}catch (Exception e) {
			completed = false;
			throw CBW00000Util.handleExceptionToCommonErrorException(e, logger, true);
		}finally{
			try {
				if(conn!=null && !conn.isClosed()){
					if(completed){
						conn.commit();
					}else{
						conn.rollback();
					}
					
					conn.close();
					conn = null;	
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public void deleteLogDetail(Object[] keys) throws CommonErrorException {
		Connection conn = null;
		boolean completed = false;
		try{
			conn = repository.getConnection();
			conn.setAutoCommit(false);
			service.deleteAllLogDetail(conn, keys);
			completed = true;
		}catch (CommonErrorException e){
			completed = false;
			throw e;
		}catch (Exception e) {
			completed = false;
			throw CBW00000Util.handleExceptionToCommonErrorException(e, logger, true);
		}finally{
			try {
				if(conn!=null && !conn.isClosed()){
					if(completed){
						conn.commit();
					}else{
						conn.rollback();
					}
					
					conn.close();
					conn = null;	
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}
}
