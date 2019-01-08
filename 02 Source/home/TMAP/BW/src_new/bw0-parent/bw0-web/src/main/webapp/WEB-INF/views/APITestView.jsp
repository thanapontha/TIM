<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="datatables" uri="http://github.com/dandelion/datatables" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %> 
<%@ taglib prefix="views" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sc2" uri="/WEB-INF/tld/sc2.tld"%>

<views:script src="json2.js"/>
<views:script src="jquery-ui-1.9.1/jquery.dataTables.js"/>
<views:style src="jquery.dataTables.css"/>
<style>
		.outer{
			width:100%; text-align:center;
		}
		.inner{
			width: 70%;height:50%; margin: 0px auto; text-align:left;margin-top:10%;
		}
		li:focus {
			outline: 2px solid #0000EE;
	    	outline-offset: -1px;
		}
</style>
<script language="javascript" type="text/javascript">
(function($){
	window.flagNotClear = true;
	$(function(){
		$('.screenid-panel>.screenid').html(' ');
		
		//FTP file upload download
		$('#upload-form').find('[name=uploadFile]').on('keypress' , function(e){
				e.preventDefault();
		});
		$('#download-form').on('keypress','select,input:not(input:submit,input:button,input:reset,button)', function(e){
			if (e.keyCode === 13 || e.keyCode === 10) {
				ST3Lib.message.clear();
				ST3Lib.message.hide(true);
				$('#download-form').submit();
				e.preventDefault();
			}
		});
		$('#upload-form').on('keypress','select,input:not(input:submit,input:button,input:reset,button)', function(e){
			if (e.keyCode === 13 || e.keyCode === 10) {
				ST3Lib.message.clear();
				ST3Lib.message.hide(true);
				$('#upload-form').submit();
				e.preventDefault();
			}
		});
		$('#delete-form').on('keypress','select,input:not(input:submit,input:button,input:reset,button)', function(e){
			if (e.keyCode === 13 || e.keyCode === 10) {
				ST3Lib.message.clear();
				ST3Lib.message.hide(true);
				$('#delete-form').submit();
				e.preventDefault();
			}
		});
		
		//Error Util
		$('#errorutil-form').on('keypress','select,input:not(input:submit,input:button,input:reset,button)', function(e){
			if (e.keyCode === 13 || e.keyCode === 10) {
				ST3Lib.message.clear();
				ST3Lib.message.hide(true);
				$('#errorutil-form').submit();
				e.preventDefault();
			}
		});		

		//Data file upload
		$('#dataupload-form').find('[name=uploadFile]').on('keypress' , function(e){
			e.preventDefault();
		});		
		$('#dataupload-form').on('keypress','select,input:not(input:submit,input:button,input:reset,button)', function(e){
			if (e.keyCode === 13 || e.keyCode === 10) {
				ST3Lib.message.clear();
				ST3Lib.message.hide(true);
				$('#dataupload-form').submit();
				e.preventDefault();
			}
		});		
		
		//Jasper Report
		$('#jasper-form').on('keypress','select,input:not(input:submit,input:button,input:reset,button)', function(e){
			if (e.keyCode === 13 || e.keyCode === 10) {
				ST3Lib.message.clear();
				ST3Lib.message.hide(true);
				$('#jasper-form').submit();
				e.preventDefault();
			}
		});			
		
		window.downloadFinish =
		function downloadFinish(datas, loading) {
			
		};
		
		window.getFileName =
		function getFileName(str){
			if (str == null) return '';
			var li = str.lastIndexOf('\\');
			if (li === -1)
				li = str.lastIndexOf('/');
			return str.substr(li + 1);
		}
	});
})(ST3Lib.$);

</script>

<div id="search-result" class="outer" style="padding: 1px;" ><!--overflow autoheight -->
	<div align="left"><label><b>FTP UPLOAD/DOWNLOAD</b></label></div>
	<div id="upload" class="overflow">
		<form:form method="post" commandName="upload" id="upload-form" action="${pageContext.request.contextPath}/common/test/upload" enctype="multipart/form-data">
		<table width="100%"><tbody>
			<tr>
				<td align="left" width="150px">
					<label>File Name to Upload:</label>
				</td>
				<td align="left" width="300px">
					<input type="hidden" name="name" />
					<form:input path="uploadFile" title="Upload" type="file" onchange="$('input[name=name]').val(getFileName(this.value))" />
				</td>
				<td align="left">
					<sc2:button functionId="ST3007" screenId="WST30070" buttonId="WST30070TestUpload" 
						type="submit" value="Upload" style="width:80px;" styleClass="button" />
				</td>
			</tr>
		</tbody></table>
		</form:form>
	</div>
	<div id="download" class="overflow">
		<form:form method="post" id="download-form" action="${pageContext.request.contextPath}/common/test/download">
		<table width="100%"><tbody>
			<tr>
				<td align="left" width="150px">
					<label>File ID to download:</label>
				</td>
				<td align="left" width="300px">					
					<input type="text" name="fileIdDownload" />
				</td>
				<td align="left">
					<sc2:button functionId="ST3007" screenId="WST30070" buttonId="WST30070TestDownload" 
						type="submit" value="Download" style="width:80px;" styleClass="button" />
				</td>
			</tr>
		</tbody></table>
		</form:form>
	</div>
	<div id="delete" class="overflow">
		<form:form method="post" id="delete-form" action="${pageContext.request.contextPath}/common/test/delete">
		<table width="100%"><tbody>
			<tr>
				<td align="left" width="150px">
					<label>File ID to delete:</label>
				</td>
				<td align="left" width="300px">
					<input type="text" name="fileIdDelete" />
				</td>
				<td align="left">
					<sc2:button functionId="ST3007" screenId="WST30070" buttonId="WST30070TestDelete" 
						type="submit" value="Delete"  style="width:80px;" styleClass="button" />
				</td>
			</tr>
		</tbody></table>
		</form:form>
	</div>
	<br>
	<br>
	<div align="left"><label><b>ERROR HANDLER</b></label></div>
	<div id="errorutil" class="overflow">
		<form:form method="post" id="errorutil-form" action="${pageContext.request.contextPath}/common/test/errorUtil">
		<table width="100%"><tbody>
			<tr>
				<td align="left" width="150px">
					<label>SQL with error:</label>
				</td>
				<td align="left" width="300px">
					<input type="text" name="errorUtilSql" style="width:90%"/>
				</td>
				<td align="left">
					<sc2:button functionId="ST3007" screenId="WST30070" buttonId="WST30070TestError" 
						type="submit" value="Error Util"  style="width:80px;" styleClass="button" />
				</td>
			</tr>
		</tbody></table>
		</form:form>
	</div>
	<br>
	<br>
	<div align="left"><label><b>DATA FILE UPLOAD</b></label></div>
	<div id="dataupload" class="overflow">
		<form:form method="post" commandName="upload" id="dataupload-form" action="${pageContext.request.contextPath}/common/test/dataUpload" enctype="multipart/form-data">
		<table width="100%"><tbody>
			<tr>
				<td align="left" width="150px">
					<label>File Id:</label>
				</td>
				<td align="left" width="300px">
					<input type="text" name="functionId"/>
				</td>
				<td align="left">
				</td>
			</tr>
			<tr>
				<td align="left" width="150px">
					<label>Data File to Upload:</label>
				</td>
				<td align="left" width="300px">
					<input type="hidden" name="name" />
					<form:input path="uploadFile" title="Upload" type="file" onchange="$('input[name=name]').val(getFileName(this.value))" />
				</td>
				<td align="left">
					<sc2:button functionId="ST3007" screenId="WST30070" buttonId="WST30070TestDataUpload" 
						type="submit" value="Upload"  style="width:80px;" styleClass="button" />
				</td>
			</tr>
		</tbody></table>
		</form:form>
	</div>	
	<br>
	<br>
	<div align="left"><label><b>JASPER REPORTS</b></label></div>
	<div id="jasper" class="overflow">
		<form:form method="post" id="jasper-form" action="${pageContext.request.contextPath}/common/test/jasper">
		<table width="100%"><tbody>
			<tr>
				<td align="left" width="150px">
					<label>Report Name:</label>
				</td>
				<td align="left" width="300px">
					<input type="text" name="reportName" value="seqHistMasterReport.jasper" />
				</td>
				<td align="left"></td>
			</tr>
			<tr>
				<td align="left" width="150px">
					<label>Report Type:</label>
				</td>
				<td align="left" width="300px">
					<select name="reportType" style="width:165px">
						<option value="XLS">Xls</option>
						<option value="XLSX">Xlsx</option>
						<option value="PDF">PDF</option>					
					</select>
				</td>
				<td align="left"></td>
			</tr>			
			<tr>
				<td align="left" width="150px">
					<label>Action:</label>
				</td>
				<td align="left" width="300px">
					<select name="actionType" style="width:165px">
						<option value="Generate">Generate</option>
						<option value="Download">Download</option>
						<option value="Preview">Preview</option>
					</select>
				</td>
				<td align="left">
					<sc2:button functionId="ST3007" screenId="WST30070" buttonId="WST30070TestJasper" 
						type="submit" value="Execute"  style="width:80px;" styleClass="button" />
				</td>
			</tr>			
		</tbody></table>
		</form:form>
	</div>	

</div>