package th.co.toyota.bw0.common.form;

import th.co.toyota.application.web.form.IST30000Form;

public class CommonBaseForm implements IST30000Form {

	private String updateKeySet = "";
	private int firstResult = 0;
	private int rowsPerPage = 10;
	
	public String getUpdateKeySet() {
		return updateKeySet;
	}

	public void setUpdateKeySet(String updateKeySet) {
		this.updateKeySet = updateKeySet;
	}

	public int getFirstResult() {
		return firstResult;
	}

	public void setFirstResult(int firstResult) {
		this.firstResult = firstResult;
	}

	public int getRowsPerPage() {
		return rowsPerPage;
	}

	public void setRowsPerPage(int rowsPerPage) {
		this.rowsPerPage = rowsPerPage;
	}

	@Override
	public String displayFriendlyField(String field) {
		return null;
	}

}
