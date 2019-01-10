package th.co.toyota.application.web;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.support.RequestContextUtils;

import th.co.toyota.application.model.Payload;
import th.co.toyota.application.model.ServiceStatus;
import th.co.toyota.application.model.XmlPayload;
import th.co.toyota.application.web.form.APITestForm;
import th.co.toyota.sc2.client.model.simple.CSC22110UserInfo;
import th.co.toyota.st3.api.download.CST30070UploadDownload;
import th.co.toyota.st3.api.upload.CST32020DataFileUpload;
import th.co.toyota.st3.api.util.IST30050ErrorUtil;

@Controller
@RequestMapping(value = "/common/test")
public class APITestController extends CommonBaseController {
	private static final String viewName = "WST30070";

	@Autowired
	protected CST30070UploadDownload uploadDownload;
	
    @Autowired
    protected IST30050ErrorUtil errorUtil;
    
	@Autowired
	protected CST32020DataFileUpload dataUpload;
    
//    @Autowired
//    private CST30170JasperReportConnector jasperReport;
    
	@Value("${jr.destination.folder}")
	private String destinationFolder;
	
	@PersistenceContext(unitName = "entityManagerFactory")
	private EntityManager em;
	
	APITestController(){
		logger = LoggerFactory.getLogger(APITestController.class);
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView initialGet(HttpServletRequest request) {
		logger.info("Initial form is initated.");
		Payload payload = new XmlPayload();
		payload = populatePayloadForDisplay(viewName, payload,
				RequestContextUtils.getLocale(request));
		
		ModelAndView mv = new ModelAndView(viewName);
		mv.addObject("upload", new APITestForm());
		mv.addObject("payload", payload);
		return mv;
	}

	// Start: FTP Upload/Download
	@RequestMapping(value = "/download", method = RequestMethod.POST)
	public ModelAndView download(APITestForm form, HttpServletRequest request,
			HttpServletResponse response, RequestContext context, RedirectAttributes redirectAttributes) {
	
		logger.info("Downloading File.");
		ModelAndView mv = new ModelAndView(viewName);		
		
		Payload payload = new XmlPayload();
		payload = populatePayloadForDisplay(viewName, payload,
				RequestContextUtils.getLocale(request));

		try {	
			uploadDownload.downloadFile(form.getFileIdDownload(), response, 512, true);
			
			payload.setStatus(ServiceStatus.OK);
			mv.addObject("upload", new APITestForm());
			mv.addObject("payload", payload);	
			redirectAttributes.addFlashAttribute("upload",  new APITestForm());
			redirectAttributes.addFlashAttribute("payload", payload);
			return mv;	
		} catch (Exception e) {
			logger.debug("Stacktrace:", e);
		
			payload.setStatus(ServiceStatus.NG);
			payload.addErrorMessage( e.getMessage() );	
			
			mv.addObject("upload", new APITestForm());
			mv.addObject("payload", payload);
			redirectAttributes.addFlashAttribute("upload",  new APITestForm());
			redirectAttributes.addFlashAttribute("payload", payload);
			return mv;
		}
	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public ModelAndView delete(APITestForm form, HttpServletRequest request,
			HttpServletResponse response, RequestContext context, RedirectAttributes redirectAttributes) {
	
		logger.info("Deleting File.");
		ModelAndView mv = new ModelAndView(viewName);		
		
		Payload payload = new XmlPayload();
		payload = populatePayloadForDisplay(viewName, payload,
				RequestContextUtils.getLocale(request));

		try {	
			CSC22110UserInfo userInfo = getUserInSession(request);
			uploadDownload.deleteFile(form.getFileIdDelete(), userInfo.getUserId(), true);
			
			payload.setStatus(ServiceStatus.OK);
			payload.addInfoMessage("File deleted successfully.");
			mv.addObject("upload", new APITestForm());
			mv.addObject("payload", payload);	
			redirectAttributes.addFlashAttribute("upload",  new APITestForm());
			redirectAttributes.addFlashAttribute("payload", payload);
			return mv;	
		} catch (Exception e) {
			logger.debug("Stacktrace:", e);
		
			payload.setStatus(ServiceStatus.NG);
			payload.addErrorMessage(e.getMessage());	
			
			mv.addObject("upload", new APITestForm());
			mv.addObject("payload", payload);
			redirectAttributes.addFlashAttribute("upload",  new APITestForm());
			redirectAttributes.addFlashAttribute("payload", payload);
			return mv;
		}
	}	
	
		
	@RequestMapping(value = "/upload" , method = RequestMethod.POST)
	public ModelAndView upload(APITestForm form, HttpServletRequest request,
			HttpServletResponse response,RequestContext context,RedirectAttributes redirectAttributes) {
		
		logger.info("Uploading File .");
		ModelAndView mv = new ModelAndView(viewName);
		
		Payload payload = new XmlPayload();
		payload = populatePayloadForDisplay(viewName, payload,
				RequestContextUtils.getLocale(request));
		MultipartFile file = form.getUploadFile();
		form.setUploadedFile(form.getUploadFile());
		
		CSC22110UserInfo userInfo = getUserInSession(request);
		
			try {
				String fileID = uploadDownload.uploadFile(file, userInfo.getUserId(), 
						userInfo.getLocation().toUpperCase(), 1024, true);
				payload.setStatus(ServiceStatus.OK);
				payload.addInfoMessage("File Uploaded. File ID: " + fileID);
				mv.addObject("upload", new APITestForm());
				mv.addObject("payload", payload);	
				redirectAttributes.addFlashAttribute("upload",  new APITestForm());
				redirectAttributes.addFlashAttribute("payload", payload);
				return mv;
			} catch (Exception e) {
				logger.debug("Stacktrace:", e);
				
				payload.setStatus(ServiceStatus.NG);
				payload.addErrorMessage(e.getMessage());	
				
				mv.addObject("upload", new APITestForm());
				mv.addObject("payload", payload);
				redirectAttributes.addFlashAttribute("upload",  new APITestForm());
				redirectAttributes.addFlashAttribute("payload", payload);
				return mv;
			}
	}
	// End: FTP Upload/Download
	
	// Start: Error Handler
	@RequestMapping(value = "/errorUtil" , method = RequestMethod.POST)
	public ModelAndView errorUtil(APITestForm form, HttpServletRequest request,
			HttpServletResponse response ,RequestContext context, RedirectAttributes redirectAttributes) {
		
		logger.info("Test Error Util");
		ModelAndView mv = new ModelAndView(viewName);
		
		Payload payload = new XmlPayload();
		payload = populatePayloadForDisplay(viewName, payload,
				RequestContextUtils.getLocale(request));
		
			try {
				Query query = em.createNativeQuery(form.getErrorUtilSql());
				Object data = query.getSingleResult();
				
				payload.setStatus(ServiceStatus.OK);
				payload.addInfoMessage("NO ERROR");
				mv.addObject("upload", new APITestForm());
				mv.addObject("payload", payload);	
				redirectAttributes.addFlashAttribute("upload",  new APITestForm());
				redirectAttributes.addFlashAttribute("payload", payload);
				return mv;
			} catch (DataAccessException aa) {
				logger.debug("DataAccessException: ", aa);
				
				payload.setStatus(ServiceStatus.NG);
				payload.addErrorMessage(errorUtil.translateSpringDataAccessException(aa));	
				
				mv.addObject("upload", new APITestForm());
				mv.addObject("payload", payload);
				redirectAttributes.addFlashAttribute("upload",  new APITestForm());
				redirectAttributes.addFlashAttribute("payload", payload);
				return mv;				
			} catch (PersistenceException cc) {
				logger.debug("PersistenceException:", cc);
				
				try {
					if (cc.getCause().getClass().equals(ConstraintViolationException.class)) {
						cc = new EntityExistsException();
					}
				} catch (Exception e){
					// do nothing
				}
				
				payload.setStatus(ServiceStatus.NG);
				payload.addErrorMessage(errorUtil.translatePersistenceException(cc));	
				
				mv.addObject("upload", new APITestForm());
				mv.addObject("payload", payload);
				redirectAttributes.addFlashAttribute("upload",  new APITestForm());
				redirectAttributes.addFlashAttribute("payload", payload);
				return mv;	
			}
	}	
	// End: Error Handler
	
	// Start: Data File Upload
	@RequestMapping(value = "/dataUpload" , method = RequestMethod.POST)
	public ModelAndView dataUpload(APITestForm form, HttpServletRequest request,
			HttpServletResponse response,RequestContext context,RedirectAttributes redirectAttributes) {
		
		logger.info("Uploading Data File.");
		ModelAndView mv = new ModelAndView(viewName);
		
		Payload payload = new XmlPayload();
		payload = populatePayloadForDisplay(viewName, payload,
				RequestContextUtils.getLocale(request));
		MultipartFile file = form.getUploadFile();
		form.setUploadedFile(form.getUploadFile());
		
		CSC22110UserInfo userInfo = getUserInSession(request);
		
			try {
				dataUpload.uploadFile(file, form.getFunctionId(), userInfo.getUserId(), viewName);
				
				payload.setStatus(ServiceStatus.OK);
				payload.addInfoMessage("Posting process is started");
				mv.addObject("upload", new APITestForm());
				mv.addObject("payload", payload);	
				redirectAttributes.addFlashAttribute("upload",  new APITestForm());
				redirectAttributes.addFlashAttribute("payload", payload);
				return mv;
			} catch (Exception e) {
				logger.debug("Stacktrace:", e);
				
				payload.setStatus(ServiceStatus.NG);
				payload.addErrorMessage(e.getMessage());	
				
				mv.addObject("upload", new APITestForm());
				mv.addObject("payload", payload);
				redirectAttributes.addFlashAttribute("upload",  new APITestForm());
				redirectAttributes.addFlashAttribute("payload", payload);
				return mv;
			}
	}
	// End: Data File Upload
	
	// Start: Jasper Reports
//	@RequestMapping(value = "/jasper", method = RequestMethod.POST)
//	public ModelAndView jasperApi(APITestForm form, HttpServletRequest request,
//			HttpServletResponse response, RequestContext context, RedirectAttributes redirectAttributes) {
//	
//		logger.info("Jasper Report API");
//		ModelAndView mv = new ModelAndView(viewName);		
//		
//		Payload payload = new XmlPayload();
//		payload = populatePayloadForDisplay(viewName, payload,
//				RequestContextUtils.getLocale(request));
//
//		try {	
//			String currentDate = new SimpleDateFormat(
//					CST30000Constants.DATE_TIME_STRING_FILENAME_FORMAT).format(new Date());
//			
//			String fileName = "TEST_" + form.getReportType() + "_" + currentDate;
//			Map<String, Object> params = new HashedMap();
//
//			String sqlHistSeqQuery = "SELECT TB_M_HIST_SEQ.* from TB_M_HIST_SEQ";
//	        Query q = em.createNativeQuery(sqlHistSeqQuery, SequenceHistoryMaster.class);			
//			
//	        String message = "Successfully Processed.";
//	        
//	        String action = form.getActionType();
//	        switch (action) {
//	        	case "Generate":
//	        		message =  jasperReport.generateReport(form.getReportName(), 
//							params, q.getResultList(), form.getReportType(), 
//							destinationFolder + File.separator + fileName + "." + form.getReportType());
//	        		break;
//	        	case "Download":
//					jasperReport.downloadReport(form.getReportName(), 
//							params, q.getResultList(), form.getReportType(), fileName, response, request);	        		
//	        		break;
//	        	case "Preview":
//	        		jasperReport.previewReport(form.getReportName(), 
//							params, q.getResultList(), request, response);
//	        		break;
//	        	default:
//	        		break;
//	       
//	        }
//			
//			payload.setStatus(ServiceStatus.OK);
//			payload.addInfoMessage(message);
//			mv.addObject("upload", new APITestForm());
//			mv.addObject("payload", payload);	
//			redirectAttributes.addFlashAttribute("upload",  new APITestForm());
//			redirectAttributes.addFlashAttribute("payload", payload);
//			return mv;	
//		} catch (Exception e) {
//			logger.debug("Stacktrace:", e);
//		
//			payload.setStatus(ServiceStatus.NG);
//			payload.addErrorMessage(e.getMessage());	
//			
//			mv.addObject("upload", new APITestForm());
//			mv.addObject("payload", payload);
//			redirectAttributes.addFlashAttribute("upload",  new APITestForm());
//			redirectAttributes.addFlashAttribute("payload", payload);
//			return mv;
//		}
//	}	
	// End: Jasper Reports
}
