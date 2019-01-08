<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="datatables" uri="http://github.com/dandelion/datatables" %>
<%@ taglib prefix="sc2" uri="/WEB-INF/tld/sc2.tld"%>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags" %>

<views:script src="jquery-ui-1.9.1/jquery.dataTables.js"/>
<views:style src="jquery.dataTables.css"/>
<style type="text/css">
	#tbl-search-criteria {
		width: 100%;
	}
	.col-search-1 {
		float: left;
		width: 90px;
		text-align: right;
		margin: 2px 10px;
		display: inline;
	   }
	.col-search-2 { 
		float: left;
		width: 160px;
		text-align: left;
		margin-top: 2px;
		display: inline;
	}
	.col-search-3 { 
		float: left;
		width: 300px;
		text-align: left;
		margin-top: 2px;
		display: inline;
	}
	
	.Table {
	    display: table;
	     border-collapse: collapse;
	    width: 100%
	}
	.Heading {
	    display: table-row;
	    font-weight: bold;
	    text-align: center;
	}
/* 	.Row {
	    display: table-row;
	    border: 0;
	    width:100%
	} */
	.Cell {
	    display: table-cell;
	    border: 0;
	    padding-right: 5px;
	}
	.result .col1 { width: 15px; text-align: center; }
	.result .col2 { width: 35px; text-align: center; }
	.result .col3 { width: 130px; }
	.result .col4 { width: 300px; }
	.result .col5 { width: 100px; }
	.result .col6 { width: 130px; }
	.result .col7 { width: 80px; }
	.result .col8 { width: 80px; }

	.dataTables_length{
		display: none;
	}
	.dataTables_paginate  span {
		display: none;
	}
	.fake-link:focus {
		outline: 2px solid #E59700;
 	    outline-offset: 0px;
 	    display:inline-block;
	}
	
	.ui-widget-overlay {
	   background: #AAA url(images/ui-bg_flat_0_aaaaaa_40x100.png) 50% 50% repeat-x;
	   opacity: .70;
	   filter: Alpha(Opacity=70);
	}
	.noTitleStuff .ui-dialog-titlebar { display:none; }
	
	.head-table-detail {
		text-align:right;
		background-color: rgb(127,127,127);
		color: white;
		font-weight: normal;
		padding: 3px 0 3px 0;
	}
</style>
<div id="screen-panel" class="container-fluid">
	<form:form method="post"
			   id="search-form" 
			   action="${_mappingPath}/search" 
			   ajax="search" 
			   ajax-loading-target="#result_wrapper"
			   autocomplete="off" 
			   validate-error="searchValidateError" >
		
		<div class="row pb-2">
			<div class="col-12">
				<label for="requestDate" class="mx-1 my-0"><span class="MandatoryFieldFont">*</span><spring:message code="ST3.WST33020.Label.RequestDate" />:</label>
				<div class="d-flex flex-row">
					<input type="text" 
						   id="requestDate" 
						   name="requestDate" 
						   class="MandatoryField form-control form-control-sm"  
						   maxlength="10"  
						   default="${defaultRequestDate}" 
						   value="${defaultRequestDate}"
						   style="width:133px;" />
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-xl-2 col-lg-2 col-md-3 col-sm-12 col-12">
				<label for="reportStatus" class="mx-1 my-0">
					<spring:message code="ST3.WST33020.Label.ReportStatus"/>:
				</label>
				<select name="reportStatus" id="reportStatus" class="form-control form-control-sm">
					<OPTION value=0>&lt;<spring:message code="ST3.WST33020.Select.ReportStatus"/>&gt;</OPTION>
					<c:forEach items="${payload.reportStatusData}" var="reportStatusData">
						<OPTION value="${reportStatusData.key}">${reportStatusData.value}</OPTION> 
					</c:forEach>
				</select>
			</div>
			<div class="col-xl-4 col-lg-4 col-md-5 col-sm-12 col-12">
				<label for="reportName" class="mx-1 my-0">
					<spring:message code="ST3.WST33020.Label.ReportName" />:
				</label>
				<input type="text" name="reportName" id="reportName" class="form-control form-control-sm" />
			</div>
		</div>
		
		<div class="row">
			<div id="search-command" class="col-xl-6 col-lg-6 col-md-6 col-sm-12 col-12">
				<spring:message code="ST3.WST33020.Label.btnSearch" var="WST33020Search"/>
				<sc2:button functionId="ST3302" 
							screenId="WST33020" 
							buttonId="WST33020Search" 
							type="submit" value="${WST33020Search}"  
							style="width:80px;"
							styleClass="button"
							onClick="beforeSearch();" />
							
				<spring:message code="ST3.WST33020.Label.btnClear" var="WST33020Clear"/>
				<sc2:button functionId="ST3302" 
							screenId="WST33020" 
							buttonId="WST33020Clear" 
							type="reset" 
							value="${WST33020Clear}" 
							style="width:80px;" 
							secured="false"
							onClick="clearSearch()" />
			</div>
			
			<div id="action-command" class="col-xl-6 col-lg-6 col-md-6 col-sm-12 col-12">
				<div class="float-xl-right float-lg-right float-md-right float-left">
					<spring:message code="ST3.WST33020.Label.btnDetails" var="WST33020Details"/>
					<sc2:button functionId="ST3302" 
								screenId="WST33020" 
								buttonId="WST33020Details" 
								type="button" 
								value="${WST33020Details}" 
								style="width:80px;" 
								secured="false"
								onClick="doDetails();" />
	
					<spring:message code="ST3.WST33020.Label.btnDelete" var="WST33020Delete"/>
					<sc2:button functionId="ST3302" 
								screenId="WST33020" 
								buttonId="WST33020Delete" 
								type="button" 
								value="${WST33020Delete}" 
								style="width:80px;" 
								secured="false"
								onClick="doDelete();" />
								
					<spring:message code="ST3.WST33020.Label.btnCancel" var="WST33020Cancel"/>
					<sc2:button functionId="ST3302" 
								screenId="WST33020" 
								buttonId="WST33020Cancel" 
								type="button" 
								value="${WST33020Cancel}" 
								style="width:80px;" 
								secured="false"
								onClick="doCancel();" />
				</div>
			</div>
		</div>
	</form:form>
	
	<div class="row"><div class="col-12"><hr/></div></div>
	<div class="pb-5 col-xl-12 col-lg-12 col-md-12 col-sm-12 col-12 px-0">
	<div id="search-result" class="overflow-hidden autoheight" style="min-width:1000px; height: 300px !important;" >
	<div id="excelDownloadlist" class="autoheight">
			
			<spring:message code="ST3.WST33020.Label.ColNo" var="ColNo"/>
			<spring:message code="ST3.WST33020.Label.ColDocNo" var="ColDocNo"/>
			<spring:message code="ST3.WST33020.Label.ColReportName" var="ColReportName"/>
			<spring:message code="ST3.WST33020.Label.ColNoOfZipFile" var="ColNoOfZipFile"/>
			<spring:message code="ST3.WST33020.Label.ColRequestDate" var="ColRequestDate"/>
			<spring:message code="ST3.WST33020.Label.ColReportStatus" var="ColReportStatus"/>
			<spring:message code="ST3.WST33020.Label.ColGeneratedBy" var="ColGeneratedBy"/>
			
			<datatables:table id="result" 
							  data="${excelDownloadlist}" 
							  cdn="false" 
							  row="row" 
							  cssClass="result fixedheader" 
							  paginate="true" 
							  cssStyle="height:310px;" 
							  sort="false"
							  info="true" 
							  displayLength="10" 
							  paginationType="full_numbers" filter="false">
				<datatables:column title="" cssClass="col1" sortable="false" searchable="false" />
				<datatables:column title="${ColNo}" cssClass="col2" sortable="false" searchable="false" />
		        <datatables:column title="${ColDocNo}" cssClass="col3" sortable="false" />
		        <datatables:column title="${ColReportName}" cssClass="col4" sortable="false"/>
		        <datatables:column title="${ColNoOfZipFile}" cssClass="col5" sortable="false"/>
		        <datatables:column title="${ColRequestDate}" cssClass="col6" sortable="false"/>
		        <datatables:column title="${ColReportStatus}" cssClass="col7" sortable="false"/>
		        <datatables:column title="${ColGeneratedBy}" cssClass="col8" sortable="false"/>
		    </datatables:table>

	</div>
	
</div>
</div>
  <div id="modal-detail" style="display:none;padding: 0em;">
	<div class="application-panel">
		<span class="application" style="padding-left:5px;" >TOYOTA Application </span>
	</div>
	<div class="below-panel">
		<div class="screenid-panel" style="background-color:rgb(127,127,127);">
			<span class="screenid st3-title-dialog" style="color: white;margin-left: 2px;">WST33021: Excel Download Detail</span>
			<span class="time" style="color: black;"></span>
		</div>
	</div>
	<div id="modal-body" style="padding: 0 5px 0 5px;">
		<input type="hidden" id="hddDetailFileName" name="hddDetailFileName" value="" />
		<input type="hidden" id="hddDetailFileNo" name="hddDetailFileNo" value="" />
		<input type="hidden" id="hddDetailDocId" name="hddDetailDocId" value="" />
		
		<table style="table-layout: fixed; width: 100%">
			<colgroup>
				<col style="width: 120px">
				<col>
			</colgroup>
			<tr>
				<td class="head-table-detail"><spring:message code="ST3.WST33021.Label.dtDocNo" /> :</td>
				<td><span id="dtDocNo"></span></td>
			</tr>
			<tr>
				<td class="head-table-detail"><spring:message code="ST3.WST33021.Label.dtReportName" /> :</td>
				<td style="background-color: #EAEAEA;"><span id="dtReportName"></span></td>
			</tr>
			<tr>
				<td class="head-table-detail"><spring:message code="ST3.WST33021.Label.dtFileName" /> :</td>
				<td><span id="dtFileName"></span></td>
			</tr>
			<tr>
				<td class="head-table-detail"><spring:message code="ST3.WST33021.Label.dtNoOfZipFile" /> :</td>
				<td style="background-color: #EAEAEA;"><span id="dtNoOfZipFile"></span></td>
			</tr>
			<tr>
				<td class="head-table-detail"><spring:message code="ST3.WST33021.Label.dtFileSize" /> :</td>
				<td><span id="dtFileSize"></span></td>
			</tr>
			<tr>
				<td class="head-table-detail"><spring:message code="ST3.WST33021.Label.dtRequestDate" /> :</td>
				<td style="background-color: #EAEAEA;"><span id="dtRequestDate"></span></td>
			</tr>
			<tr>
				<td class="head-table-detail"><spring:message code="ST3.WST33021.Label.dtStartDate" /> :</td>
				<td><span id="dtStartDate"></span></td>
			</tr>
			<tr>
				<td class="head-table-detail"><spring:message code="ST3.WST33021.Label.dtEndDate" /> :</td>
				<td style="background-color: #EAEAEA;"><span id="dtEndDate"></span></td>
			</tr>
			<tr>
				<td class="head-table-detail"><spring:message code="ST3.WST33021.Label.dtReportStatus" /> :</td>
				<td><span id="dtReportStatus"></span></td>
			</tr>
			<tr>
				<td class="head-table-detail"><spring:message code="ST3.WST33021.Label.dtEmail" /> :</td>
				<td style="background-color: #EAEAEA;"><span id="dtEmail"></span></td>
			</tr>
			<tr>
				<td class="head-table-detail"><spring:message code="ST3.WST33021.Label.dtGeneratedBy" /> :</td>
				<td><span id="dtGeneratedBy"></span></td>
			</tr>
			<tr>
				<td colspan="2" align="right" style="padding-top: 5px;">
					<spring:message code="ST3.WST33021.Label.btnDetailDownload" var="WST33021Download"/>
					<sc2:button functionId="ST3302" 
										screenId="WST33020" 
										buttonId="WST33021DetailDownload" 
										type="button" 
										value="${WST33021Download}" 
										style="width:80px;" 
										secured="false"
										onClick="doDetailDownload();" />
					
					<spring:message code="ST3.WST33021.Label.btnDetailClose" var="WST33021Cancel"/>	
					<sc2:button functionId="ST3302" 
										screenId="WST33020" 
										buttonId="WST33021DetailClose" 
										type="button" 
										value="${WST33021Cancel}" 
										style="width:80px;" 
										secured="false"
										onClick="doCancelDetail();" />
				</td>
			</tr>
		</table>
	  </div>
	</div>
</div>

<script>
$(document).ready(function() {
	
    $( "#requestDate" ).datepicker({
      showOn: "button",
      buttonImage: calendarImgPath,
      buttonImageOnly: true,
      buttonText: "Select date",
      dateFormat: "dd/mm/yy"
    });
    
    $('#modal-detail').dialog({
		autoOpen : false,
		height : "auto",
		width : 400,
		modal : true,
		show : {
			effect : 'fade',
			duration : 500
		},
		hide : {
			effect : 'fade',
			duration : 250
		},
		dialogClass : 'noTitleStuff',
		resizable: false,
	});
    
    execLayoutState('init');
});

var layout_state = {
		'init': { 
			  hide: ['#action-command']
			, show: ['#excelDownloadlist']
			, focus: '#requestDate'
		},
		'search-data-not-found': { 
			  hide: ['#action-command']
			, show: ['#excelDownloadlist' ]
			, focus: '#requestDate'
		},
		'search-data-found': { 
			  hide: []
			, show: ['#action-command', '#excelDownloadlist']
			, disabled: []
			, focus: '#requestDate'
		}
};
var file_status = {
		"on_queue" : '1',
		"process" : '2',
		"finish" : '3',
		"cancel" : '4',
		"delete" : '5',
		"error_occur" : '7',
		"no_data_found" : '8'
};

function execLayoutState(name){
	ST3Lib.message.setAppendMode(false);
	ST3Lib.message.clear(true);
	ST3Lib.content.execLayoutState(layout_state, name);	
}

function clearSearch(){
	//lastAction = 'clear';
	execLayoutState('init');
	var form = $('#search-form');

	$('#result').dataTable().fnClearTable();
	form.find('#reportStatus').attr('default', '0');
	form.find('#requestDate').focus();
	
}
function beforeSearch(){
	clearSTDMessage();
	ST3Lib.content.execLayoutState(layout_state, 'init');
	$('#result').dataTable().fnClearTable();
	if(validation()) {
		$('#search-form').submit();
	}else{
		ST3Lib.message.show(1);
		$('#requestDate').focus();		
	}
}
function isBlank(str) {
    return (!str || /^\s*$/.test(str));
}
function validation() {
	var error_count = 0;
	var ret = true;
	var requestDate = $('#requestDate').val();

	if (!isBlank(requestDate)) {
		var pattern =/^([0-9]{2})\/([0-9]{2})\/([0-9]{4})$/;
	
		if(!pattern.test(requestDate)) {
			
			ST3Lib.message.addError('<spring:message code="MSTD0043AERR" arguments="Request Date" ></spring:message>');
			
			error_count++;
		}
		
		if(error_count > 0) {
			ret = false;
		}
	} else {
		ret = false;
	}
	
	
	return ret;
}
function search(datas, loading){
	
	if(ST3Lib.message.countError() > 0) {
		ST3Lib.message.setAppendMode(false);
		loading.close();
	}else{
		doSearch(datas, loading);
	}
	
}

function doSearch(datas, loading) {
	clearSTDMessage();
	if (datas === null) return;
	isSearchButtonClick = false;
	var dataTable = $( '#result' ).dataTable();
	dataTable.fnClearTable(false);//no redraw
	$('#common-command').removeClass('hide');

	datas = datas.excelDownloadStatusList;
	
	if ( !datas || datas.length === 0 ){
		dataTable.fnDraw(true);
		$('#result').dataTable().fnClearTable();
		execLayoutState('search-data-not-found');
		$('#module').focus();
		
		//show message
		ST3Lib.message.setAppendMode(false);
		ST3Lib.message.clear(true);
		ST3Lib.message.setAppendMode(true);
		ST3Lib.message.addError('<spring:message code="MSTD0059AERR"></spring:message>');
		ST3Lib.message.show(1);
		
		return;	
	}else{
		execLayoutState('search-data-found');
	}
	if ( loading ) {
		loading.autoClose = false;
		loading.resize();
	}
	
	var arrayData = generateDataForTable(datas);
	var setting = dataTable.fnSettings();
	
	setting.aoColumns[0].sClass = 'col1';
	setting.aoColumns[1].sClass = 'col2';
	setting.aoColumns[2].sClass = 'col3';
	setting.aoColumns[3].sClass = 'col4';
	setting.aoColumns[4].sClass = 'col5';
	setting.aoColumns[5].sClass = 'col6';
	setting.aoColumns[6].sClass = 'col7';
	setting.aoColumns[7].sClass = 'col8';

	dataTable.fnPageChange(0, false);
	dataTable.fnClearTable(false);//no redraw
	dataTable.fnAddData(arrayData);

	
	loading.close();
}
function doDetails(){
	clearSTDMessage();
	if ($('input[name=rdSearchData]').is(':checked')) {
		
		$('#WST33021DetailDownload').hide();
		$('#WST33021DetailDownload').attr('disabled','disabled');
		
		$.post( "${_mappingPath}/details", 
				{ docId:$('input[name=rdSearchData]:checked').val(),fileNo:$('input[name=rdSearchData]:checked').attr('fileNo') }, 
				function( dataObj ) {
					var data = dataObj.excelDownloadFile;
					
					$('#dtDocNo').html(data.id.docId);
					$('#dtReportName').html(data.excelDownloadStatus.reportName);
					$('#dtFileName').html(data.fileName);
					$('#hddDetailFileName').val(data.fileName);
					$('#hddDetailFileNo').val(data.id.fileNo);
					$('#hddDetailDocId').val(data.id.docId);
					
					if (data.id.fileNo > 0) {
						$('#dtNoOfZipFile').html(data.id.fileNo + ' of ' + data.excelDownloadStatus.fileCount);
						$('#dtFileSize').html(data.fileSizeReadable);
						$('#dtRequestDate').html(timestampToDatetime(data.excelDownloadStatus.requestDt));
						$('#dtStartDate').html(timestampToDatetime(data.execStartDt));
						$('#dtEndDate').html(timestampToDatetime(data.execEndDt));
					}
					
					$('#dtReportStatus').html(data.translateStatus);
					$('#dtEmail').html((data.excelDownloadStatus.picEmail == null) ? "-" : data.excelDownloadStatus.picEmail);
					$('#dtGeneratedBy').html(data.excelDownloadStatus.requestBy);
					if(data.status == file_status.finish) {
						$('#WST33021DetailDownload').removeAttr('disabled');
						$('#WST33021DetailDownload').show();
						$('#WST33021DetailDownload').focus();
					}
					
		});
		$('#modal-detail').dialog('open');
		
	}else{
		ST3Lib.message.addError('<spring:message code="MSTD1019AERR"></spring:message>');
		ST3Lib.message.show(1);
		$('input:radio[name=rdSearchData]:first').focus();
	}
}

function doCancelDetail() {
	$('#modal-detail').dialog('close');
	$('#dtDocNo').html("");
	$('#dtReportName').html("");
	$('#dtFileName').html("");
	$('#hddDetailFileName').val("");
	$('#hddDetailFileNo').val("");
	$('#hddDetailDocId').val("");
	$('#dtNoOfZipFile').html("");
	$('#dtFileSize').html("");
	$('#dtRequestDate').html("");
	$('#dtStartDate').html("");
	$('#dtEndDate').html("");
	$('#dtReportStatus').html("");
	$('#dtEmail').html("");
	$('#dtGeneratedBy').html("");
	$('#requestDate').focus();
}
function doDelete() {
	clearSTDMessage();
	
	if ($('input[name=rdSearchData]').is(':checked')) {
		ST3Lib.dialog.confirm('<spring:message code="MSTD0001ACFM"></spring:message>', 'MSTD0001ACFM', function(ret){
			if(ret) {
				$.post( "${_mappingPath}/delete", { docId:$('input[name=rdSearchData]:checked').val(),fileNo:$('input[name=rdSearchData]:checked').attr('fileNo') }, function( data ) {
					if(data.errorMessages.length > 0) {
						ST3Lib.message.addError(data.errorMessages);
						ST3Lib.message.show(1);
					}else{
						if(data.infoMessages.length > 0) {
							ST3Lib.message.addInfo(data.infoMessages);
							ST3Lib.message.show(1);
						}
						RefreshTable();
					}
					$('#requestDate').focus();
				});
			}
    	});
        
    }else{
		ST3Lib.message.addError('<spring:message code="MSTD1016AERR"></spring:message>');
		ST3Lib.message.show(1);
		$('input:radio[name=rdSearchData]:first').focus();
    }
}

function doCancel() {
	clearSTDMessage();
	
	if ($('input[name=rdSearchData]').is(':checked')) {
		ST3Lib.dialog.confirm('<spring:message code="MSTD0003ACFM"></spring:message>', 'MSTD0003ACFM', function(ret){

			if(ret) {
				$.post( "${_mappingPath}/cancel", 
						{ docId:$('input[name=rdSearchData]:checked').val()}, 
						function( data, loading) {
							if(data.errorMessages.length > 0) {
								ST3Lib.message.addError(data.errorMessages);
								ST3Lib.message.show(1);
							}else{
								if(data.infoMessages.length > 0) {
									ST3Lib.message.addInfo(data.infoMessages);
									ST3Lib.message.show(1);
								}
								RefreshTable();
							}
							$('#requestDate').focus();
						});
			}
    	});
        
    }else{
		ST3Lib.message.addError('<spring:message code="MSTD1018AERR"></spring:message>');
		ST3Lib.message.show(1);
		$('input:radio[name=rdSearchData]:first').focus();
    }
}

function RefreshTable()
{
	$.post( "${_mappingPath}/search", 
			{requestDate:$('#requestDate').val(),
			 reportStatus:$('#reportStatus').val(),
			 reportName:$('#reportName').val()
			}
	,function( dataObj ){
		
		if (dataObj === null) return;
		
		var datas = dataObj.excelDownloadStatusList;
		var dataTable = $( '#result' ).dataTable();
		var arrayData = generateDataForTable(datas);
		
		dataTable.fnClearTable(false);//no redraw
		dataTable.fnAddData(arrayData);
		
	});
}

function postDownload(pdocId, pfileNo) {
	clearSTDMessage();
	$.post( "${_mappingPath}/download/", { docId:pdocId, fileNo:pfileNo }, function( data ) {

			if (data.status != 'OK') {
				if(data.errorMessages.length > 0) {
					ST3Lib.message.addError(data.errorMessages);
					ST3Lib.message.show(1);
				}
			} else {
				window.location = '${_mappingPath}/download/' + pdocId + '/' + pfileNo;
			}
	
		$('#requestDate').focus();
	});		
}

function generateDataForTable(datas){
	var arrayData = [];
	var lastIndex = datas.length - 1;
	var counter = 0;
	for(var i=0;i<=lastIndex;i++){
		var statusLabel = "";	
			
		//iterate excel download files
		var dataExcelList = datas[i].excelDownloadFiles;
		if ( !dataExcelList || dataExcelList.length > 0 ){
			var xFileLastIndex =  dataExcelList.length - 1;
			
			for(var j=0;j<=xFileLastIndex;j++){
				statusLabel = "";
				if(datas[i].status == file_status.process) {
					if(dataExcelList[j].status == file_status.finish) {
						statusLabel = "<a nohref onclick=\"postDownload('" + dataExcelList[j].id.docId + "','" + dataExcelList[j].id.fileNo + "');return false;\">" + dataExcelList[j].translateStatus + "</a>";		
					}else{
						statusLabel = dataExcelList[j].translateStatus;
					}
				}else if(datas[i].status == file_status.finish) {
					statusLabel = "<a nohref onclick=\"postDownload('" + dataExcelList[j].id.docId + "','" + dataExcelList[j].id.fileNo + "');return false;\">" + datas[i].translateStatus  + "</a>";
				}else{
					statusLabel = datas[i].translateStatus;
				}
				
				arrayData.push([
							    '<input type="radio" name="rdSearchData" value="'+ dataExcelList[j].id.docId+'" fileNo=' + dataExcelList[j].id.fileNo + ' />'
							    , (++counter)
							    , dataExcelList[j].id.docId
							    , datas[i].reportName
							    , dataExcelList[j].id.fileNo + ' of ' + datas[i].fileCount
			                    , timestampToDatetime(datas[i].requestDt)
			                    , statusLabel
			                    , datas[i].requestBy
			                    
			                    ]);
				
				if ((xFileLastIndex > 0) && (j < xFileLastIndex)){
					i++;
				}
			}
		} else {
			statusLabel = datas[i].translateStatus;
			arrayData.push([
						    '<input type="radio" name="rdSearchData" value="'+ datas[i].docId+'" fileNo=0 />'
						    , (++counter)
						    , datas[i].docId
						    , datas[i].reportName
						    , '0 of ' + datas[i].fileCount
		                    , timestampToDatetime(datas[i].requestDt)
		                    , statusLabel
		                    , datas[i].requestBy
		                    
		                    ]);
		}
		
		
		
		
		/* var statusLabel = "";
		if(datas[i].excelDownloadStatus.status == file_status.process) {
			if(datas[i].status == file_status.finish) {
				statusLabel = "<a href='${_mappingPath}/download/"+ datas[i].fileName +"' target='_blank'>" + datas[i].translateStatus + "</a>";
			}else{
				statusLabel = datas[i].translateStatus;
			}
		}else if(datas[i].excelDownloadStatus.status == file_status.finish) {
			
			statusLabel = "<a href='${_mappingPath}/download/"+ datas[i].fileName +"' target='_blank'>" + datas[i].excelDownloadStatus.translateStatus + "</a>";
		}else{
			statusLabel = datas[i].excelDownloadStatus.translateStatus;
		}
		arrayData.push([
					    '<input type="radio" name="rdSearchData" value="'+datas[i].id.docId+'" fileNo=' + datas[i].id.fileNo + ' />'
					    , (i+1)
					    , datas[i].id.docId
					    , datas[i].excelDownloadStatus.reportName
					    , datas[i].id.fileNo + ' of ' + datas[i].excelDownloadStatus.fileCount
	                    , timestampToDatetime(datas[i].excelDownloadStatus.requestDt)
	                    , statusLabel
	                    , datas[i].excelDownloadStatus.requestBy
	                    
	                    ]); */
	                    
	}
	return arrayData;
}

function doDetailDownload(){
	var docNo = $('#hddDetailDocId').val();
	var fileNo = $('#hddDetailFileNo').val();

	if(!isBlank(docNo) && !isBlank(fileNo)){
		postDownload(docNo, fileNo);
		doCancelDetail();
	}
}
function timestampToDatetime(inputDate) {
	var date = new Date(inputDate);
	var time = zeroLead(date.getHours(), 2) + ':' 
					+ zeroLead(date.getMinutes(), 2) + ':' 
					+ zeroLead(date.getSeconds(), 2);
	return date.format('dd/mm/yy ' + time);
}

function clearSTDMessage(){
	ST3Lib.message.setAppendMode(false);
	ST3Lib.message.clear(true);
	ST3Lib.message.setAppendMode(true);
}

</script>