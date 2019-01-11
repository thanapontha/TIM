/******************************************************
 * Program History
 * 
 * Project Name	            :  GWRDS : 
 * Client Name				:  TMAP-EM
 * Package Name             :  th.co.toyota.bw0.api.service.common
 * Program ID 	            :  CBW00000CommonService.java
 * Program Description	    :  <put description>
 * Environment	 	        :  Java 7
 * Author					:  Thanapon T.
 * Version					:  1.0
 * Creation Date            :  July 14, 2016
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2013-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/
package th.co.toyota.bw0.api.service.common;

import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;

import th.co.toyota.bw0.api.constants.AppConstants;
import th.co.toyota.bw0.api.constants.MessagesConstants;
import th.co.toyota.bw0.api.exception.common.CommonErrorException;
import th.co.toyota.bw0.api.repository.common.CommonAPIRepository;
import th.co.toyota.st3.api.constants.CST30000Constants;
import th.co.toyota.st3.api.download.CST30090ExcelGenerator;
import th.co.toyota.st3.api.model.BatchQueue;
import th.co.toyota.st3.api.model.ModuleDetailInfo;
import th.co.toyota.st3.api.util.CST30000BatchManager;

@Service
public class CommonService{

	@Autowired
	private CommonAPIRepository repository;

	@Autowired
	protected MessageSource messageSource;

	@Autowired
	@Qualifier("CST30090ExcelGenerator")
	protected CST30090ExcelGenerator generator;

	@Autowired
	protected CST30000BatchManager batchManager;
	
	@Value("${projectCode}")
	protected String PROJECT_CODE;
	
	@Value("${pu_phase_test}")
	protected String PU_PHASE_TEST;	
	
	//Index parameter for delete Rundwon Kompo Status
	final int IDX_VERSION = 0;
	final int IDX_GETSUDO_MONTH = 1;
	final int IDX_TIMING = 2;
	final int IDX_VEHICLE_PLANT = 3;
	final int IDX_VEHICLE_MODEL = 4;
	final int IDX_UNIT_PLANT = 5;
	final int IDX_UNIT_TYPE = 6;
	final int IDX_UNIT_MODEL = 7;


	public boolean postBatchRequest(List<String> parameters,
											String screenId, 
											String batchId,
											String userId, 
											String projectCode) throws Exception {

		// Start set batch queue
		BatchQueue batchQueue = new BatchQueue();
		batchQueue.setRequestId(screenId);
		batchQueue.setBatchId(batchId);
		batchQueue.setRequestBy(userId);
		batchQueue.setRequestDate(new Date());
		batchQueue.setSupportId(AppConstants.BLANK);
		batchQueue.setProjectCode(projectCode);

		batchQueue.setParameters(Joiner.on(AppConstants.BLANK_SPACE).join(
				parameters));
		// End set batch queue

		String appId = batchManager.createBatchQueue(batchQueue);
		if(Strings.isNullOrEmpty(appId)){
			return false;
		}
		return true;

	}

	
	public String getFunctionName(String functionId) throws CommonErrorException {
		String value = "";
		ModuleDetailInfo moduleD = repository.findModuleDetailInfo(functionId);
		if(moduleD != null){
			value = moduleD.getFunctionName();
		}else{
			throw new CommonErrorException(MessagesConstants.A_ERROR_NOT_REGISTER, 
    				new String[]{functionId, "TB_M_MODULE_D, Please register function id="+functionId}, 
    				AppConstants.ERROR);
		}
			
		return value;
	}
	
	public String genAppId() throws CommonErrorException{
		return repository.genAppId();
	}

	public Object[] getPrevGetsudoMonthAndTiming(Connection conn, String getsudoMonthCurrent, String timingCurrent){
		try {
			return repository.getPrevGetsudoMonthAndTiming(conn, getsudoMonthCurrent, timingCurrent);
		} catch (CommonErrorException e) {
			return null;
		}
	}
	
	public String convertRundownKompoUploadStatus(int processStatus){
		String uploadStatus = "";
		if (processStatus == CST30000Constants.SUCCESS) {
			uploadStatus = "SC";
		}else if (processStatus == CST30000Constants.ERROR) {
			uploadStatus = "ER";
		}else if (processStatus == CST30000Constants.WARNING) {
			uploadStatus = "SC";
		}
		return uploadStatus;
	}
	
	public void addRundownKompoSts(Connection conn, Object[] data) throws Exception {
		String[] paramDel = new String[]{data[IDX_VERSION].toString(), 
										 data[IDX_GETSUDO_MONTH].toString(), 
										 data[IDX_TIMING].toString(), 
										 data[IDX_VEHICLE_PLANT].toString(), 
										 data[IDX_VEHICLE_MODEL].toString(),
										 data[IDX_UNIT_PLANT].toString(),
										 data[IDX_UNIT_MODEL].toString()};
		
		repository.insertRundownKompoSts(conn, paramDel, data);
	}
	
	public void addRundownKompoStsKOMPO(Connection conn, List<Object[]> datas) throws Exception {
		
		if(datas!=null && !datas.isEmpty()){
			Object[] data = datas.get(0);
			String[] paramDel = new String[]{data[IDX_VERSION].toString(), 
											 data[IDX_GETSUDO_MONTH].toString(), 
											 data[IDX_TIMING].toString(), 
											 data[IDX_VEHICLE_PLANT].toString(), 
											 data[IDX_VEHICLE_MODEL].toString()};
			
			repository.insertRundownKompoSts(conn, paramDel, datas);
		}
	}
	
	public Date getNextGetsudoMonthFromCurrent(){
		Calendar calendar = Calendar.getInstance();         
		calendar.add(Calendar.MONTH, 1);
		calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
		Date nextMonthFirstDay = calendar.getTime();
		return nextMonthFirstDay;
	}
}
