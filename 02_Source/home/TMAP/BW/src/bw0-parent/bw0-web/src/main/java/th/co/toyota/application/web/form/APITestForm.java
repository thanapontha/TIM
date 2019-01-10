package th.co.toyota.application.web.form;

import org.springframework.web.multipart.MultipartFile;
public class APITestForm implements IST30000Form {

	private MultipartFile uploadFile;
	private Object uploadedFile;
	private String fileIdDelete;
	private String fileIdDownload;
	
	private String errorUtilSql;
	
	private String functionId;
	
	private String reportName;
	private String reportType;
	private String actionType;
	
	public Object getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(Object uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

	public MultipartFile getUploadFile() {
		return uploadFile;
	}

	public void setUploadFile(MultipartFile uploadFile) {
		this.uploadFile = uploadFile;
	}

	public String getFileIdDownload() {
		return fileIdDownload;
	}

	public void setFileIdDownload(String fileIdDownload) {
		this.fileIdDownload = fileIdDownload;
	}
	
	public String getFileIdDelete() {
		return fileIdDelete;
	}

	public void setFileIdDelete(String fileIdDelete) {
		this.fileIdDelete = fileIdDelete;
	}

	public String getErrorUtilSql() {
		return errorUtilSql;
	}

	public void setErrorUtilSql(String errorUtilSql) {
		this.errorUtilSql = errorUtilSql;
	}	
	
	@Override
	public String displayFriendlyField(String field) {
		String friendly = null;

		return friendly;
	}

	public String getFunctionId() {
		return functionId;
	}

	public void setFunctionId(String functionId) {
		this.functionId = functionId;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}
	
}
