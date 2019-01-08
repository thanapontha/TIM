<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="datatables" uri="http://github.com/dandelion/datatables" %>
<%@ taglib prefix="sc2" uri="/WEB-INF/tld/sc2.tld"%>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags" %>

<views:script src="jquery-ui-1.9.1/jquery.dataTables.js"/>
<views:style src="jquery.dataTables.css"/>
<style type="text/css">
	td.col1 { width: 15px; text-align: center; }
	td.col2 { width: 35px; text-align: center; }
	td.col3 { width: 130px; }
	td.col4 {  }
	td.col5 { width: 100px; }
	td.col6 { width: 130px; }
	td.col7 { width: 80px; }
	td.col8 { width: 80px; }

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

<div id="search-result" class="overflow-hidden autoheight" style="min-width:1000px;" >
	<div id="excelDownloadlist" class="autoheight overflow-hidden" style="height:500px;">
			
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
							  cssStyle="height:170px;"
							  sort="false"
							  info="false" 
							  paginationType="full_numbers" 
							  filter="false"
							  displayLength="5">
				<datatables:column title="" cssClass="col1" sortable="false" searchable="false" />
				<datatables:column title="${ColNo}" cssClass="col2" />
		        <datatables:column title="${ColDocNo}" cssClass="col3" sortable="false" />
		        <datatables:column title="${ColReportName}" cssClass="col4" sortable="false"/>
		        <datatables:column title="${ColNoOfZipFile}" cssClass="col5" sortable="false"/>
		        <datatables:column title="${ColRequestDate}" cssClass="col6" sortable="false"/>
		        <datatables:column title="${ColReportStatus}" cssClass="col7" sortable="false"/>
		        <datatables:column title="${ColGeneratedBy}" cssClass="col8" sortable="false"/>
		    </datatables:table>

	</div>
	
	<div id="button-panel" style="padding:0px 10px 10px 0px;width:100%;min-width: 300px;">
		<div id="button-right" style="text-align: right;">
			<div id="action-command" style="">

				<spring:message code="ST3.WST33022.Label.btnMiniRefresh" var="WST33022Refresh"/>
				<sc2:button functionId="ST3302" 
							screenId="WST33022" 
							buttonId="WST33022MiniRefresh" 
							type="button" 
							value="${WST33022Refresh}" 
							style="width:80px;" 
							secured="false"
							onClick="location.reload();" />
						
				<spring:message code="ST3.WST33022.Label.btnMiniDetails" var="WST33022Details"/>	
				<sc2:button functionId="ST3302" 
							screenId="WST33022" 
							buttonId="WST33022MiniDetails" 
							type="button" 
							value="${WST33022Details}" 
							style="width:80px;" 
							secured="false"
							onClick="doDetails();" />
							
				<spring:message code="ST3.WST33022.Label.btnMiniClose" var="WST33022Close"/>
				<sc2:button functionId="ST3302" 
							screenId="WST33022" 
							buttonId="WST33022MiniClose" 
							type="button" 
							value="${WST33022Close}" 
							style="width:80px;" 
							secured="false"
							onClick="window.close();" />
			</div>
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
		
		<table style="table-layout: fixed; width: 100%;font-size: small;">
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

<script>
window.scrollTo(0, 0);

$(document).ready(function() {
	var loadingTarget = "body";
	var loading = ST3Lib.dialog.loading(loadingTarget);
	
	disable_scroll();
	
    $( "#requestDate" ).datepicker({
      showOn: "button",
      buttonImage: _imagePath + "icons/cal.gif",
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
    
    $.post("${_mappingPath}/search", 
			{}, 
			function( datas ) {
		    	search(datas, loading);
		    	enable_scroll();
	});
});

var layout_state = {
		'init': { 
			  hide: ['']
			, show: ['#excelDownloadlist']
			, focus: '#WST33022MiniRefresh'
		},
		'search-data-not-found': { 
			  hide: ['']
			, show: ['#excelDownloadlist' ]
			, disabled: ['#WST33022MiniDetails']
			, focus: '#WST33022MiniRefresh'
		},
		'search-data-found': { 
			  hide: []
			, show: ['#excelDownloadlist']
			, enabled: ['#WST33022MiniDetails']
			, focus: '#WST33022MiniRefresh'
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

function search(datas, loading){
	
	if(ST3Lib.message.countError() > 0) {
		ST3Lib.message.setAppendMode(false);
		loading.close();
	}else{
		doSearch(datas, loading);
	}
	
}

function doSearch(datas, loading) {
	showLoading();
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
		

		ST3Lib.dialog.prompt('<spring:message code="MSTD0059AERR"></spring:message>', 'Error', function(ret){		
    	});
		loading.close();
		return;	
	}else{
		execLayoutState('search-data-found');
	}
	if ( loading ) {
		loading.autoClose = false;
		//loading.resize();
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
					$('#hddDetailDocId').val(data.id.docId);					
					$('#dtReportName').html(data.excelDownloadStatus.reportName);
					$('#dtFileName').html(data.fileName);
					$('#hddDetailFileName').val(data.fileName);
					$('#hddDetailFileNo').val(data.id.fileNo);
					$('#dtNoOfZipFile').html(data.id.fileNo + ' of ' + data.excelDownloadStatus.fileCount);
					$('#dtFileSize').html(data.fileSizeReadable);
					$('#dtRequestDate').html(timestampToDatetime(data.excelDownloadStatus.requestDt));
					$('#dtStartDate').html(timestampToDatetime(data.execStartDt));
					$('#dtEndDate').html(timestampToDatetime(data.execEndDt));
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
		ST3Lib.dialog.prompt('<spring:message code="MSTD1019AERR"></spring:message>', 'Error', function(ret){		
    	});
		$('input:radio[name=rdSearchData]:first').focus();
	}
}

function doCancelDetail() {
	$('#modal-detail').dialog('close');
	$('#dtDocNo').html("");
	$('#hddDetailDocId').val("");	
	$('#dtReportName').html("");
	$('#dtFileName').html("");
	$('#hddDetailFileName').val("");
	$('#hddDetailFileNo').val("");
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

function generateDataForTable(datas){
	var arrayData = [];
	var lastIndex = datas.length - 1;
	var docId = "";
	for(var i=0;i<=lastIndex;i++){
		var statusLabel = "";	
		var counter = i;	
		if(docId !== datas[i].docId){
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
								    , (counter+j+1)
								    , dataExcelList[j].id.docId
								    , datas[i].reportName
								    , dataExcelList[j].id.fileNo + ' of ' + datas[i].fileCount
				                    , timestampToDatetime(datas[i].requestDt)
				                    , statusLabel
				                    , datas[i].requestBy
				                    
				                    ]);
				}
			} else {
				statusLabel = datas[i].translateStatus;
				arrayData.push([
							    ' '
							    , (counter+1)
							    , datas[i].docId
							    , datas[i].reportName
							    , '0 of ' + datas[i].fileCount
			                    , timestampToDatetime(datas[i].requestDt)
			                    , statusLabel
			                    , datas[i].requestBy
			                    
			                    ]);
			}
		}
		docId = datas[i].docId;
		
		
		
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


function isBlank(str) {
    return (!str || /^\s*$/.test(str));
}

function doDetailDownload(){
	var docNo = $('#hddDetailDocId').val();
	var fileNo = $('#hddDetailFileNo').val();

	if(!isBlank(docNo) && !isBlank(fileNo)){
		postDownload(docNo, fileNo);
	}
}

function postDownload(pdocId, pfileNo) {
	clearSTDMessage();
	$.post( "${_mappingPath}/download/", { docId:pdocId, fileNo:pfileNo }, function( data ) {
		if (data.status != 'OK') {
			for(var i = 0; i < data.errorMessages.length; ++i) {
				ST3Lib.dialog.prompt(data.errorMessages[i], 'Error', function(ret){		
		    	});				
			}
		}else{			
			window.location = '${_mappingPath}/download/' + pdocId + '/' + pfileNo;
		}
	});		
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

//left: 37, up: 38, right: 39, down: 40,
//spacebar: 32, pageup: 33, pagedown: 34, end: 35, home: 36
var keys = [37, 38, 39, 40];

function preventDefault(e) {
e = e || window.event;
if (e.preventDefault)
   e.preventDefault();
e.returnValue = false;  
}

function keydown(e) {
 for (var i = keys.length; i--;) {
     if (e.keyCode === keys[i]) {
         preventDefault(e);
         return;
     }
 }
}

function wheel(e) {
preventDefault(e);
}

function disable_scroll() {
if (window.addEventListener) {
   window.addEventListener('DOMMouseScroll', wheel, false);
}
window.onmousewheel = document.onmousewheel = wheel;
document.onkeydown = keydown;
}

function enable_scroll() {
	if (window.removeEventListener) {
	    window.removeEventListener('DOMMouseScroll', wheel, false);
	}
	window.onmousewheel = document.onmousewheel = document.onkeydown = null;  
}


</script>