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
 * Creation Date            :  January 11, 2018
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2013-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/
package th.co.toyota.bw0.api.service.common;

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
}
