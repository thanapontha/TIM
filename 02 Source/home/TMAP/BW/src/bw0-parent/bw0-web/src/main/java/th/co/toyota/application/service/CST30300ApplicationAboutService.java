/******************************************************
 * Program History
 * 
 * Project Name	            :  st3-web-template
 * Client Name				:  TMAP-EM
 * Package Name             :  th.co.toyota.application.service
 * Program ID 	            :  CST30300ApplicationAboutService.java
 * Program Description	    :  
 * Environment	 	        :  Java 7
 * Author					:  Manego
 * Version					:  1.0
 * Creation Date            :  June 3, 2015
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2015-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/
package th.co.toyota.application.service;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import th.co.toyota.application.repository.IST33060SystemMasterRepository;
import th.co.toyota.st3.api.constants.CST30000Constants;
import th.co.toyota.st3.api.exception.NoDataFoundException;
import th.co.toyota.st3.api.exception.SystemDoesNotExistsException;
import th.co.toyota.st3.api.exception.SystemIsNotActiveException;
import th.co.toyota.st3.api.model.SystemInfo;
import th.co.toyota.st3.api.model.SystemInfoId;
import th.co.toyota.st3.api.util.CST30300VersionUtil;

/**
 * A service class for the about screen implementation.
 * 
 * @author Manego
 * 
 */
@Service
public class CST30300ApplicationAboutService implements
		IST30300ApplicationAboutService {
	final Logger logger = LoggerFactory
			.getLogger(CST30300ApplicationAboutService.class);

	@Autowired
	private IST33060SystemMasterRepository systemMasterRepository;

	@Value("${projectCode}")
	private String applicationCategory;

	public static final String SUB_CATEGORY_ABOUT = "ABOUT";
	public static final String SUB_CATEGORY_VERSION = "APP_VERSION";
	public static final String CODE_APP_WORDING = "APP_WORDING";
	public static final String CODE_HELPDESK_INFO = "HELPDESK_INFO";

	public static final String ST3_VERSION_ATTR = "Implementation-Version";

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.service.IST30300ApplicationAboutService#
	 * getApplicationWording()
	 */
	@Override
	public String getApplicationWording() throws SystemDoesNotExistsException, SystemIsNotActiveException {
		logger.debug("reading the application wording");
		
		final SystemInfoId id = new SystemInfoId();
		id.setCategory(applicationCategory);
		id.setSubCategory(SUB_CATEGORY_ABOUT);
		id.setCode(CODE_APP_WORDING);
		
		try {
			final SystemInfo systemMasterInfo = systemMasterRepository
					.findSystemMasterInfo(id);
			if (systemMasterInfo == null) {
				logger.error("No data found for application wording.");
				throw new NoDataFoundException(
						"No data found for application wording.");
			}

			// throws exception is configuration is not active.
			if(!CST30000Constants.YES.equalsIgnoreCase(String.valueOf(systemMasterInfo.getStatus()))){
				logger.error("application wording configuration is not active.");
				throw new SystemIsNotActiveException("CATEGORY="
						+ id.getCategory() + ", SUB_CATEGORY="
						+ id.getSubCategory() + ", CD = " + id.getCode());
			}
			
			return systemMasterInfo.getValue();
		} catch (final NoDataFoundException ndfe) {
			logger.warn("no data found for given details");
			throw new SystemDoesNotExistsException("CATEGORY="
					+ id.getCategory() + ", SUB_CATEGORY="
					+ id.getSubCategory() + ", CD = " + id.getCode());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.service.IST30300ApplicationAboutService#
	 * getHelpdeskInfo()
	 */
	@Override
	public String getHelpdeskInfo() throws SystemDoesNotExistsException, SystemIsNotActiveException {
		logger.debug("reading the helpdesk information.");
		
		final SystemInfoId id = new SystemInfoId();
		id.setCategory(applicationCategory);
		id.setSubCategory(SUB_CATEGORY_ABOUT);
		id.setCode(CODE_HELPDESK_INFO);
		
		try {
			final SystemInfo systemMasterInfo = systemMasterRepository
					.findSystemMasterInfo(id);
			if (systemMasterInfo == null || systemMasterInfo.getValue() == null) {
				logger.error("No data found for helpdesk information.");
				throw new NoDataFoundException("No data found for helpdesk information.");
			}

			// throws exception is configuration is not active.
			if(!CST30000Constants.YES.equalsIgnoreCase(String.valueOf(systemMasterInfo.getStatus()))){
				logger.error("helpdesk information configuration is not active.");
				throw new SystemIsNotActiveException("CATEGORY="
						+ id.getCategory() + ", SUB_CATEGORY="
						+ id.getSubCategory() + ", CD = " + id.getCode());
			}
			
			return systemMasterInfo.getValue();
		} catch (final NoDataFoundException ndfe) {
			logger.warn("no data found for given details");
			throw new SystemDoesNotExistsException("CATEGORY="
					+ id.getCategory() + ", SUB_CATEGORY="
					+ id.getSubCategory() + ", CD = " + id.getCode());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.service.IST30300ApplicationAboutService#
	 * getCurrentAppVersion()
	 */
	@Override
	public String getCurrentAppVersion() throws SystemDoesNotExistsException, SystemIsNotActiveException {
		logger.debug("read the current application version");
		try {
			final List<SystemInfo> systemInfoList = systemMasterRepository
					.querySystemMasterOrderedCodeValue(applicationCategory,
							SUB_CATEGORY_VERSION);
			if (systemInfoList == null || systemInfoList.isEmpty()) {
				logger.error("No data found for current application version.");
				throw new NoDataFoundException(
						"No data found for current application version.");
			}
			// pickup the latest version
			final SystemInfo systemMasterInfo = systemInfoList.get(systemInfoList.size() - 1);
			
			// throws exception is configuration is not active.
			if(!CST30000Constants.YES.equalsIgnoreCase(String.valueOf(systemMasterInfo.getStatus()))){
				logger.error("current application version configuration is not active.");
				throw new SystemIsNotActiveException("CATEGORY="+ applicationCategory + ", SUB_CATEGORY=" + SUB_CATEGORY_VERSION);
			}
			
			return systemMasterInfo.getId().getCode();
		} catch (final NoDataFoundException ndfe) {
			logger.warn("no data found for CATEGORY="+ applicationCategory + ", SUB_CATEGORY=" + SUB_CATEGORY_VERSION);
			throw new SystemDoesNotExistsException("CATEGORY="+applicationCategory + ", SUB_CATEGORY="
					+ SUB_CATEGORY_VERSION);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.service.IST30300ApplicationAboutService#
	 * getVersionList()
	 */
	@Override
	public List<String> getVersionList() throws SystemDoesNotExistsException {
		logger.debug("read available versions.");
		try {
			final List<String> versions = new ArrayList<String>();
			final List<SystemInfo> systemInfoList = systemMasterRepository
					.querySystemMasterOrderedCodeValue(applicationCategory,
							SUB_CATEGORY_VERSION);
			for (final SystemInfo systemInfo : systemInfoList) {
				versions.add(systemInfo.getId().getCode());
			}

			return versions;
		} catch (final NoDataFoundException ndfe) {
			logger.warn("no data found for CATEGORY="+ applicationCategory + ", SUB_CATEGORY=" + SUB_CATEGORY_VERSION);
			throw new SystemDoesNotExistsException("CATEGORY="+applicationCategory + ", SUB_CATEGORY="
					+ SUB_CATEGORY_VERSION);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.service.IST30300ApplicationAboutService#
	 * getEnhancement(java.lang.String)
	 */
	@Override
	public String getEnhancement(final String versionCode)
			throws SystemDoesNotExistsException {
		logger.debug("read the enhancement for given version no.");
		
		final SystemInfoId id = new SystemInfoId();
		id.setCategory(applicationCategory);
		id.setSubCategory(SUB_CATEGORY_VERSION);
		id.setCode(versionCode);
		
		try {
			final List<SystemInfo> systemMasterInfo = systemMasterRepository
					.querySystemMasterInfo(id);
			if (systemMasterInfo == null || systemMasterInfo.isEmpty()) {
				logger.error("No data found for appliucation wording.");
				throw new NoDataFoundException(
						"No data found for appliucation wording.");
			}

			return systemMasterInfo.get(0).getValue();
		} catch (final NoDataFoundException ndfe) {
			logger.warn("no data found for given app version " + versionCode);
			throw new SystemDoesNotExistsException("CATEGORY="
					+ id.getCategory() + ", SUB_CATEGORY="
					+ id.getSubCategory() + ", CD = " + id.getCode());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.service.IST30300ApplicationAboutService#
	 * getCurrentStandardLibVersion()
	 */
	@Override
	public String getCurrentStandardLibVersion() {
		logger.debug("find standard library version from meta-data");
		final Manifest manifest = CST30300VersionUtil
				.getManifest(CST30000Constants.class);

		final Attributes attr = manifest.getMainAttributes();
		final String value = attr.getValue(ST3_VERSION_ATTR);

		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.service.IST30300ApplicationAboutService#
	 * getCurrentStandardBatchVersion()
	 */
	@Override
	public String getCurrentStandardBatchVersion() {
		logger.debug("read batch version from meta-data.");
		final Manifest manifest = CST30300VersionUtil
				.getManifest("th.co.toyota.st3.batch.main.CST30090FileSending");

		final Attributes attr = manifest.getMainAttributes();
		final String value = attr.getValue(ST3_VERSION_ATTR);

		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see th.co.toyota.application.service.IST30300ApplicationAboutService#
	 * getCurrentSecurityCenterVersion()
	 */
	@Override
	public String getCurrentSecurityCenterVersion() {
		logger.debug("read the security center version from meta-data.");
		final Manifest manifest = CST30300VersionUtil
				.getManifest("th.co.toyota.sc2.client.CSC22110Constant");

		final Attributes attr = manifest.getMainAttributes();
		final String value = attr.getValue(ST3_VERSION_ATTR);

		return value;
	}
}