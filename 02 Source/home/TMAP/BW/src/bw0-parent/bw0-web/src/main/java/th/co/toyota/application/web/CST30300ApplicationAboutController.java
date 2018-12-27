/******************************************************
 * Program History
 * 
 * Project Name	            :  st-server-webapp-template
 * Client Name				:  TMAP-EM
 * Package Name             :  th.co.toyota.application.web
 * Program ID 	            :  CST30300ApplicationAboutController.java
 * Program Description	    :  Controller for about screen
 * Environment	 	        :  Java 7
 * Author					:  Manego
 * Version					:  1.0
 * Creation Date            :  Apr 7, 2014
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2015-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/
package th.co.toyota.application.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;

import th.co.toyota.application.model.Payload;
import th.co.toyota.application.model.ServiceStatus;
import th.co.toyota.application.model.XmlPayload;
import th.co.toyota.application.service.IST30300ApplicationAboutService;
import th.co.toyota.st3.api.constants.CST30000Messages;
import th.co.toyota.st3.api.exception.SystemDoesNotExistsException;
import th.co.toyota.st3.api.exception.SystemIsNotActiveException;

/**
 * This screen is used to show information about helpdesk (telephone number and
 * email), current application version, standard framework version and
 * additional info list of enhancement points for current running application
 * compared with previous version.
 * 
 * @author Manego
 * 
 */
@Controller
@RequestMapping(value = "/common/about")
public class CST30300ApplicationAboutController extends CommonBaseController {

	final Logger logger = LoggerFactory
			.getLogger(CST30300ApplicationAboutController.class);

	final String viewName = "WST30300";
	final String reportName = "Application About Screen";

	@Autowired
	private IST30300ApplicationAboutService service;

	@Autowired
	protected Validator validator;

	/**
	 * Initial of about screen will be displayed after user select from menu
	 * 
	 * <ol>
	 * <li>screen will retrieve following information
	 * <ul>
	 * <li>Wording/Simple explanation about current running application
	 * <li>HelpDesk information, including their phone number and email address
	 * <li>Version of current running application
	 * <li>Standard framework version used by current running application
	 * <li>Application Enhancement history: This will show all of enhancement
	 * history has been done by from each version of the application
	 * </ul>
	 * <li>All of those information will be retrieved from TB_M_SYSTEM,
	 * excluding information about application and standard framework version
	 * 
	 * <li>Application version will be retrieved from /META-INF/MANIFEST.MF
	 * inside the ear file.
	 * 
	 * <li>Standard framework version information will be retrieved from
	 * <ul>
	 * <li>Standard Library version from META-INF/MANIFEST.MF inside
	 * st3-api-<version>.jar
	 * <li>Batch Library version from META-INF/MANIFEST.MF inside
	 * st3-batch-<version>.jar
	 * <li>Security Center version from META-INF/MANIFEST.MF inside
	 * sc2-client-<version>.jar
	 * </ul>
	 * <li>Application enhancement history is a combobox input where user can
	 * select and see list of enhancement has been done for each version
	 * 
	 * <li>Mapping between version and it's enhancement will be retrieved from
	 * TB_M_SYSTEM. Version come from CD field, where list of enhancement from
	 * VALUE field.
	 * 
	 * <li>All data for enhancement history will retrieved at initial mode, by
	 * default it will show the latest version list of enhancement.
	 * </ol>
	 * 
	 * @param request A HttpRequest.
	 * @return {@link ModelAndView}
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView initials(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView(viewName);
		Payload payload = new XmlPayload();
		try {
			logger.debug("init the application about screen.");
			payload = populatePayloadForDisplay(viewName, payload,
					RequestContextUtils.getLocale(request));
			payload.setStatus(ServiceStatus.OK);

			final String currentVersion = service.getCurrentAppVersion();
			final Map<String, Object> aboutDataMap = new HashMap<String, Object>();
			aboutDataMap.put("appWording", service.getApplicationWording());
			aboutDataMap.put("helpDeskInfo", service.getHelpdeskInfo());
			aboutDataMap.put("currentAppVer", currentVersion);
			aboutDataMap.put("currentStdLibVer", service.getCurrentStandardLibVersion());
			aboutDataMap.put("currentStdBatchVer", service.getCurrentStandardBatchVersion());
			aboutDataMap.put("currentSecCenterVer",	service.getCurrentSecurityCenterVersion());
			aboutDataMap.put("versions", service.getVersionList());
			aboutDataMap.put("currentEnhancement", service.getEnhancement(currentVersion));

			mv.addObject("about", aboutDataMap);
		} catch (final SystemDoesNotExistsException sdnee) {
			logger.error("system not found for : " + sdnee.getMessage());
			payload.setStatus(ServiceStatus.NG);
			payload.addErrorMessage(messageSource.getMessage(
					CST30000Messages.ERROR_SYSTEM_NOT_EXISTS,
					new String[] { sdnee.getMessage(), "TB_M_SYSTEM." },
					RequestContextUtils.getLocale(request)));
		} catch (final SystemIsNotActiveException sinae) {
			logger.error("system not active for : " + sinae.getMessage());
			payload.setStatus(ServiceStatus.NG);
			payload.addErrorMessage(messageSource.getMessage(
					CST30000Messages.ERROR_INACTIVE_SYSTEM,
					new String[] { sinae.getMessage(), "TB_M_SYSTEM." },
					RequestContextUtils.getLocale(request)));
		} catch (final Exception e) {
			logger.error("Error message from common Library: " + e.getMessage());
			final String message = e.getMessage() == null ? e.toString() : e.getMessage();
			payload.setStatus(ServiceStatus.NG);
			payload.addErrorMessage(messageSource.getMessage(
					CST30000Messages.ERROR_UNDEFINED_ERROR,
					new String[] { message },
					RequestContextUtils.getLocale(request)));
		}

		mv.addObject("payload", payload);
		return mv;
	}

	/**
	 * Information about enhancement will be stored in disabled textbox. On
	 * change to application version, system will find the respective
	 * enhancement and display in text box.
	 * 
	 * @param versionID Application version.
	 * @param request A HttpRequest.
	 * @return A {@link Payload} instance.
	 */
	@RequestMapping(value = "/listEnhancement", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody
	Object listEnhancement(@RequestParam("code") String versionID,
			HttpServletRequest request) {
		Payload payload = new XmlPayload();
		try {
			logger.debug("list the enhancement for given version number.");
			final String value = service.getEnhancement(versionID);

			payload = populatePayloadForDisplay(viewName, payload,
					RequestContextUtils.getLocale(request));

			payload.setEnhancement(value);
			payload.setStatus(ServiceStatus.OK);
		} catch (final Exception e) {
			logger.error("Error message from common Library: " + e.getMessage());
			final String message = e.getMessage() == null ? e.toString() : e.getMessage();
			payload.setStatus(ServiceStatus.NG);
			payload.addErrorMessage(messageSource.getMessage(
					CST30000Messages.ERROR_UNDEFINED_ERROR,	new String[] { message },
					RequestContextUtils.getLocale(request)));
		}
		
		return payload;
	}
}