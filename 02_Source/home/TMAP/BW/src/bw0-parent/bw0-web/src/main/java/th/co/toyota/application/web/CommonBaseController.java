package th.co.toyota.application.web;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.constraints.Pattern;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;

import com.google.common.base.Strings;

import th.co.toyota.application.model.Payload;
import th.co.toyota.application.repository.IST33060SystemMasterRepository;
import th.co.toyota.application.web.form.IST30000Form;
import th.co.toyota.bw0.common.repository.CommonWebRepository;
import th.co.toyota.sc2.client.CSC22110Constant;
import th.co.toyota.sc2.client.model.simple.CSC22110AccessControlList;
import th.co.toyota.sc2.client.model.simple.CSC22110UserInfo;
import th.co.toyota.st3.api.constants.CST30000Messages;

/**
 * Base controller For ST3 web controllers to control there actions. This
 * controller helps to get the following details:
 * <ul>
 * <li>A current user session details.
 * <li>The current user access controls.
 * <li>Populate payload details.
 * <li>Process the validation messages for different screens.
 * <li>Format the message as per the message resource details.
 * 
 * @author danilo
 * 
 */
@Controller
public class CommonBaseController {

	/** A file logger instance. */
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	/** A spring validator. */
	@Autowired
	protected Validator validator;

	/** A application message resource. */
	@Autowired
	protected MessageSource messageSource;
	
	@Autowired
	private IST33060SystemMasterRepository systemRepository;
	
	@Autowired
	public CommonWebRepository commonRepository;
	
	@Value("${projectCode}")
	private String projectCode;
	
	@Value("${pu_phase_test}")
	private String puPhaseTest;	

	/**
	 * Processes the all validation error messages.
	 * 
	 * @param contraintVaiolationArray {@link ConstraintViolation} array.
	 * @param locale A local 
	 * @param form A Web From
	 * @return List of messages.
	 */
	protected List<String> processErrorMessageFromValidator(
			Object[] contraintVaiolationArray, Locale locale, IST30000Form form) {
		List<String> errorMessages = new ArrayList<>();
		for (Object obj : contraintVaiolationArray) {
			ConstraintViolation<?> cv = (ConstraintViolation<?>) obj;
			errorMessages.add(reinterpolateMessage(cv, locale, form));
		}

		return errorMessages;
	}

	/**
	 * Format all validation messages as per the application resource
	 * configuration and display on screen.
	 * 
	 * @param cv {@link ConstraintViolation} array.
	 * @param local A local 
	 * @param form A Web From
	 * @return A message.
	 */
	protected String reinterpolateMessage(ConstraintViolation<?> cv,
			Locale local, IST30000Form form) {
		String message = null;

		String errorCode = cv.getMessageTemplate();
		errorCode = errorCode.substring(1, errorCode.length() - 1);

		// handle empty messages
		if (CST30000Messages.ERROR_MESSAGE_EMPTY_FIELD.equals(errorCode)) {
			message = messageSource.getMessage(errorCode, new String[] { form
					.displayFriendlyField(cv.getPropertyPath().toString()) },
					local);
		} else if (CST30000Messages.ERROR_MESSAGE_INVALID_FIELD
				.equals(errorCode)) {
			message = messageSource.getMessage(errorCode, new String[] { form
					.displayFriendlyField(cv.getPropertyPath().toString()) },
					local);
		} else if (CST30000Messages.ERROR_MESSAGE_INVALID_FORMAT
				.equals(errorCode)) {
			Class<? extends IST30000Form> c = form.getClass();
			try {
				Field m = c.getDeclaredField(cv.getPropertyPath().toString());
				m.setAccessible(true);// Access to Private Field
				Pattern anno = m.getAnnotation(Pattern.class);
				String regExpAnnotation = String.valueOf(anno.regexp());
				message = messageSource
						.getMessage(
								errorCode,
								new String[] {
										form.displayFriendlyField(cv
												.getPropertyPath().toString()),
										regExpAnnotation }, local);
			} catch (Exception e) {
				logger.debug("Error Exception {}", e);
				message = e.toString();
			}
		} else {
			message = messageSource.getMessage(
					CST30000Messages.ERROR_UNDEFINED_ERROR, new String[] { form
							.displayFriendlyField(cv.getPropertyPath()
									.toString()) }, local);
		}

		return message;

	}

	/**
	 * This method use to populate {@link Payload} details.
	 * 
	 * @param viewName a screen name.
	 * @param payload A {@link Payload} instance.
	 * @param locale A local
	 * @return Same {@link Payload} instance
	 */
	protected Payload populatePayloadForDisplay(String viewName,
			Payload payload, Locale locale) {
		if (payload != null) {
			try {
				payload.setScreenId(viewName);
				payload.setScreenDescription(messageSource.getMessage("ST3."
						+ viewName + ".Description", null, locale));
			} catch (Throwable t) {
				logger.error("Error retrieving Screen ID and Description.", t);
			}
		}

		return payload;
	}

	/**
	 * Returns the current logged in user session.
	 * 
	 * @param request
	 *            A request.
	 * @return A user session {@link CSC22110UserInfo}
	 */

	public CSC22110UserInfo getUserInSession(HttpServletRequest request) {
		CSC22110UserInfo userInfo = (CSC22110UserInfo) request.getSession()
				.getAttribute(CSC22110Constant.SESSION_USER_INFO);
		if("true".equalsIgnoreCase(puPhaseTest)){
			String ipAddress = request.getHeader("X-FORWARDED-FOR");
			if (ipAddress == null) {
				ipAddress = request.getRemoteAddr();
			}
	
			try{
				Map<String, String> userMap = commonRepository.getUserInfoForTestOnDev(ipAddress);
				
				String userId = userMap.get("USERID");
				if (!Strings.isNullOrEmpty(userId)) {
					userInfo.setUserId(userId);
				}
				String firstName = userMap.get("FIRSTNAME");
				if (!Strings.isNullOrEmpty(firstName)) {
					userInfo.setFirstName(firstName);
				}
				String location = userMap.get("LOCATION");
				if (!Strings.isNullOrEmpty(location)) {
					userInfo.setLocation(location);
				}
				String employeeNo = userMap.get("EMPLOYEENO");
				if (!Strings.isNullOrEmpty(employeeNo)) {
					userInfo.setEmployeeNo(employeeNo);
				}
				String section = userMap.get("SECTION");
				if (!Strings.isNullOrEmpty(section)) {
					userInfo.setSection(section);
				}
				String division = userMap.get("DIVISION");
				if (!Strings.isNullOrEmpty(division)) {
					userInfo.setDivision(division);
				}
				String department = userMap.get("DEPARTMENT");
				if (!Strings.isNullOrEmpty(department)) {
					userInfo.setDepartment(department);
				}
			}catch(Exception e){
				logger.debug(ExceptionUtils.getStackTrace(e));
			}
		}
		return userInfo;
	}

	/**
	 * Returns the current logged in user access control details from session.
	 * 
	 * @param request
	 *            A request object.
	 * @return A access control details {@link CSC22110AccessControlList}
	 */
	protected CSC22110AccessControlList getAccessControlList(
			HttpServletRequest request) {
		return (CSC22110AccessControlList) request.getSession().getAttribute(
				CSC22110Constant.SESSION_SYSTEM_ACL);
	}
	
	protected String getUserCompany(CSC22110UserInfo userInfo) {
		return userInfo.getLocation().toUpperCase();
	}
}
