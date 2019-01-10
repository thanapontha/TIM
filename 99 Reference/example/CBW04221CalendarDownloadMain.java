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

import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import th.co.toyota.bw0.api.common.CBW00000CommonPOI;
import th.co.toyota.bw0.api.common.CBW00000Util;
import th.co.toyota.bw0.api.exception.common.CommonErrorException;
import th.co.toyota.bw0.api.repository.common.IBW00000Repository;
import th.co.toyota.bw0.api.repository.common.IBW03060Repository;
import th.co.toyota.bw0.batch.report.repository.IBW04221Repository;
import th.co.toyota.config.AppConfig;
import th.co.toyota.st3.api.constants.CST30000Messages;
import th.co.toyota.st3.api.util.CST32010DocNoGenerator;
import th.co.toyota.st3.api.util.IST30000LoggerDb;

@Component
public class CBW04221CalendarDownloadMain {

	final Logger logger = LoggerFactory.getLogger(CBW04221CalendarDownloadMain.class);
	
	@Autowired
	private IST30000LoggerDb loggerBBW04221;
	
	@Autowired
	protected MessageSource messageSource;
	
	@Autowired
	private IBW03060Repository systemRepository;
	
	@Autowired
	private IBW00000Repository commonRepository;
	
	@Autowired
	private IBW04221Repository repository;
	
	@Autowired
	protected CBW00000CommonPOI poi;
	
	@Autowired
	private CST32010DocNoGenerator docNoGenerator;

	@Value("${default.download.folder}")
	protected String downloadFolder;
	
	public static void main(String[] args) {
		ApplicationContext appContext = new AnnotationConfigApplicationContext(AppConfig.class);
		CBW04221CalendarDownloadMain main = appContext.getBean(CBW04221CalendarDownloadMain.class);

		String[] params = {
				"TDEM", //param 1: Version (User Company Login TMAP-MS or TDEM)
				"Jan-18", //paam 2: Getsudo Month
				"D-14", // param 3: Timing
				"testUser", //param 4: User Login
				"99999" //param 5: Application ID
		};
		
		if (args.length != 0) {
			params = args;
		}
		try {
			main.generateReport(params);
		} catch (CommonErrorException e) {
			String message = main.messageSource.getMessage(CST30000Messages.ERROR_UNDEFINED_ERROR, new String[] { CBW00000Util.genMessageOfException(e) }, Locale.getDefault());
			main.logger.error(message);
		}
		
		((ConfigurableApplicationContext) appContext).close();

		System.exit(0);
	}
	
	public List<String> generateReport(String[] args) throws CommonErrorException {
		CBW04221CalendarDownload download = new CBW04221CalendarDownload();
		download.loggerBBW04221 = loggerBBW04221;
		download.messageSource = messageSource;
		download.systemRepository = systemRepository;
		download.commonRepository = commonRepository;
		download.repository = repository;
		download.poi = poi;
		download.downloadFolder = downloadFolder;
		List<String> excelFileList = download.generateReport(args);
		download = null;
		return excelFileList;
	}
}
