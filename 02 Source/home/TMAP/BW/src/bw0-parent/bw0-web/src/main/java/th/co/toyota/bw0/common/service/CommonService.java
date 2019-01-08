package th.co.toyota.bw0.common.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import th.co.toyota.bw0.common.repository.CommonRepository;
import th.co.toyota.sc2.client.CSC22110Constant;
import th.co.toyota.sc2.client.model.simple.CSC22110UserInfo;

import com.google.common.base.Strings;

@Service
public class CommonService{
	final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private CommonRepository repository;
	
	@Value("${projectCode}")
	private String projectCode;
	
	@Value("${pu_phase_test}")
	private String puPhaseTest;	

	public CSC22110UserInfo getUserInfo(HttpServletRequest request) {
		CSC22110UserInfo userInfo = (CSC22110UserInfo) request.getSession()
				.getAttribute(CSC22110Constant.SESSION_USER_INFO);
		if("true".equalsIgnoreCase(puPhaseTest)){
			String ipAddress = request.getHeader("X-FORWARDED-FOR");
			if (ipAddress == null) {
				ipAddress = request.getRemoteAddr();
			}
	
			try{
				Map<String, String> userMap = repository.getUserInfoForTestOnDev(ipAddress);
				
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
}
