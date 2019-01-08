<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="datatables" uri="http://github.com/dandelion/datatables" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %> 
<%@ taglib prefix="views" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sc2" uri="/WEB-INF/tld/sc2.tld"%>

<views:script src="json2.js"/>
<views:script src="jquery-ui-1.9.1/jquery.dataTables.js"/>
<views:script src="jquery.fileDownload.js" />
<views:style src="jquery.dataTables.css"/>

<style type="text/css">
.col3{text-align: center;}
.col4{text-align: center;}
.col6{text-align: center;}
.col8{text-align: center;}
.col9{text-align: center;}
</style>

<script language="javascript" type="text/javascript">
	$(function() {
		
		var gTableName 		= '';
		var gModelClassName = '';
		var gBigData 		= '';
		var gBookmarkId 	= '';
		var gReportName 	= '';
		var gReportTitle 	= '';
		var gStartRow 		= '';
		var gStartColumn 	= '';
		var gFileType 		= '';
		var gGenExcelFlag	= '';
		var gDbFieldName 	= [];
		var gField 			= [];
		var gDisplayName 	= [];
		var gOrderDisp 		= [];
		var gPk 			= [];
		var gDataType 		= [];
		var gCriteria 		= [];
		var gOperationLogic = [];
		var gChkDisplayOpt 	= [];
		var gSort 			= [];
		
		var actionFormUrl = '';		
		actionFormUrl = $('#search-form').attr('action');
		
		$("#WST30900SaveBookmark").click(function() {
			//alert("Save bookmark.");
			saveBookmark();
		});
		
		$('#result-list-form').on('keypress' ,'select,input:submit,input:button,input:reset,input:radio,input:checkbox,button',function(e){
			if (e.keyCode === 13 || e.keyCode === 10)
				return false;
		});
		
		$("#tableName").change(function(){
			$( "#tableName option:selected" ).each(function() {
				var selectedVal = $(this).val();
				var selectedTxt = $(this).text();
				if (selectedVal == "") {
					clearForm();
					return;	
				}
				
				var loadingTarget = "#result_wrapper";
				var loading = ST3Lib.dialog.loading(loadingTarget);
				ST3Lib.message.clear();
				$("#bookmarks").val("");
				
				ST3Lib.ajax(actionFormUrl + '/changeTableView', 'GET', {'isajax' : '1', 'modelClass' : selectedVal, 'tableName' : selectedTxt}, function(datas, status){
					displayAjaxResult(datas, status, loading);					
				});
		    });
		});
		
		$("#bookmarks").change(function(){
			ST3Lib.message.clear(true);
			$( "#bookmarks option:selected" ).each(function() {
				var selectedVal = $(this).val();
				var loadingTarget = "#result_wrapper";
				var loading = ST3Lib.dialog.loading(loadingTarget);
				ST3Lib.message.clear();
	
				if (selectedVal === ""){ 
					//Fixed IncidentForm_PH(IFT-ST3)_0.9.0_009 by Thanapon
					var selectedModelClass = $('#search-form').find('select[name=tableName]').val();
					var selectedTableName = $('#search-form').find('select[name=tableName] :selected').text();
		
					ST3Lib.ajax(actionFormUrl + '/changeBookmark', 'GET', {'isajax' : '1', 'bookmarkID' : selectedVal,'modelClass' : selectedModelClass, 'tableName' : selectedTableName}, function(datas, status){
						displayAjaxResult(datas, status, loading);
					});	
					return;
				}else{
					ST3Lib.ajax(actionFormUrl + '/changeBookmark', 'GET', {'isajax' : '1', 'bookmarkID' : selectedVal}, function(datas, status){
						displayAjaxResult(datas, status, loading);
					});	
				}
		    });
		});
		
		window.displayAjaxResult =
			function displayAjaxResult(datas, status, loading){
				if (datas === null) return;
				var dataTable = $( '#result' ).dataTable();
				dataTable.fnClearTable(false);//no redraw
				//Fixed IncidentForm_PH(IFT-ST3)_0.9.0_008 by Thanapon 18/05/2015
				var selectedTableName = $('#search-form').find('select[name=tableName] :selected').text();
				
				$('#common-command').removeClass('hide');
				$("#totalRecordFound").val(datas.totalRecord);
				
				var mandatorys = datas.mandatoryFieldList;	
				
				datas = datas.bookmarksList;
				if ( !datas || datas.length === 0 ){
					dataTable.fnDraw(true);
					$('#result').dataTable().fnClearTable();
					//execLayoutState('search-data-not-found');
					$('#module').focus();
					
					//Fixed IncidentForm_PH(IFT-ST3)_0.9.0_009 by Thanapon
					//var selectedTableName = $('#search-form').find('select[name=tableName] :selected').text();
					var selectedBookmark = $('#search-form').find('select[name=bookmarks] :selected').val();
					loadBookmarksByTableResult(selectedTableName, selectedBookmark);	
					//end Fixed IncidentForm_PH(IFT-ST3)_0.9.0_009 by Thanapon
					loading.close();
					return;	
				}else{
					//execLayoutState('search-data-found');
				}
				if ( loading ) {
					loading.autoClose = false;
					loading.resize();
				}
				
				var setting = dataTable.fnSettings();
				setting.aoColumns[0].sClass = 'col1';
				setting.aoColumns[1].sClass = 'col2';
				setting.aoColumns[2].sClass = 'col3';
				setting.aoColumns[3].sClass = 'col4';
				setting.aoColumns[4].sClass = 'col5';
				setting.aoColumns[5].sClass = 'col6';
				setting.aoColumns[6].sClass = 'col7';
				setting.aoColumns[7].sClass = 'col8';
				setting.aoColumns[8].sClass = 'col9';
				
				dataTable.fnClearTable(false);//no redraw
				var arrayData = [];
				var i;
				var lastIndex = datas.length - 1;
				var tableName = "";
				var reportName = "";
				var reportTitle = "";
				var startRow = "";
				var startColumn = "";
				var logicOpr = "";
						
				for(i=0;i<=lastIndex;i++){
					//Set variable form
					data = datas[i];
					settingID = datas[i].settingID;
					tableName = datas[i].tableName;
					reportName = datas[i].reportName;
					reportTitle = datas[i].reportTitle;
					startRow = datas[i].startRow;
					startColumn = datas[i].startColumn;
					
					var chkDispOpt = "";
					if (datas[i].displayOption == 1) {
						chkDispOpt = "checked";
					}
					if (datas[i].logicalOpr == "AND") {
						logicOpr = '<option value="AND" selected>AND</option><option value="OR">OR</option>';
					} else if (datas[i].logicalOpr == "OR") {
						logicOpr = '<option value="AND">AND</option><option value="OR" selected>OR</option>';
					} else {
						logicOpr = '<option value="AND">AND</option><option value="OR">OR</option>';
					}
					//Set data of table list
					var orderDisplay = i+1;
					if (convertNullToBank(datas[i].orderDisp) != "") {
						orderDisplay = datas[i].orderDisp;
					}
					var mandatoryFieldCheck = "";
					if ( mandatorys != null && mandatorys !== undefined && mandatorys.length > 0 ){
						for(k=0; k < mandatorys.length; k++){
							mandatory = mandatorys[k];
							if(mandatory.value === datas[i].fieldName){
								mandatoryFieldCheck = ' class="MandatoryField"';
								break;
							}
						}
					}
					arrayData.push([
								      convertNullToBank(datas[i].fieldName) + '<input type="hidden" name="dbFieldName" value="'+convertNullToBank(datas[i].fieldName)+'" /><input type="hidden" name="field" value="'+convertNullToBank(datas[i].objFieldName)+'" />'
								    , '<input type="text" name="displayName" value="' + convertNullToBank(datas[i].displayName) + '" maxlength="30" size="12" style="width: 100%;" />'
								    , '<input type="text" name="orderDisp" onkeyup="numbersOnly(this)" onblur="numbersOnly(this)" maxlength="3" value="' + orderDisplay + '" size="3" style="text-align: right;" />'
								    , convertNullToBank(datas[i].pkOption) + '<input type="hidden" name="pk" value="'+convertNullToBank(datas[i].pkOption)+'" />'
								    , datas[i].dataType + '<input type="hidden" name="dataType" value="'+datas[i].dataType+'" />'
								    , '<input type="text" title="Criteria of '+convertNullToBank(datas[i].fieldName)+'" name="criteria"'+mandatoryFieldCheck+' maxlength="256" value="' + convertNullToBank(datas[i].criteria) + '" style="width: 100%;" />'
								    , '<select style="width: 70%;" name="operationLogic"><option></option>' + logicOpr + '</select>'
								    , '<input type="checkbox" name="checkboxDisplayOption" value="1" ' + chkDispOpt + ' onClick="setDisplayOpt('+i+');" /><input type="hidden" name="ChkDisplayOption" value="'+datas[i].displayOption+'" />'
								    , '<input type="text" name="sort" onkeyup="numbersOnly(this)" onblur="numbersOnly(this)" maxlength="3" value="' + convertNullToBank(datas[i].sort) + '" size="3" style="text-align: right;" />'
				                    
				                    ]);
				}
				
				//assign value to form object		
				$("#tableName option").filter(function(index) { return $(this).text() === tableName; }).attr('selected', 'selected');
				//Fixed IncidentForm_PH(IFT-ST3)_0.9.0_008 by Thanapon 18/05/2015
				
				if(convertNullToBank(selectedTableName) === ""){
					$("#reportName").val('');
					$("#reportTitle").val('');
					$("#startRow").val('');
					$("#startColumn").val('');
				}else if(convertNullToBank(selectedTableName) != "" && convertNullToBank(settingID) != ""){
					$("#reportName").val(reportName);
					$("#reportTitle").val(reportTitle);
					$("#startRow").val(startRow);
					$("#startColumn").val(startColumn);
				}
				//end Fixed IncidentForm_PH(IFT-ST3)_0.9.0_008 by Thanapon 18/05/2015
				
				//Fixed IncidentForm_PH(IFT-ST3)_0.9.0_009 by Thanapon
				selectedTableName = $('#search-form').find('select[name=tableName] :selected').text();
				var selectedBookmark = $('#search-form').find('select[name=bookmarks] :selected').val();
				loadBookmarksByTableResult(selectedTableName, selectedBookmark);	
				//end Fixed IncidentForm_PH(IFT-ST3)_0.9.0_009 by Thanapon
				
				$("#WST30900BigData").attr('checked', true);
				dataTable.fnAddData(arrayData);
				$("#downloadControl").css("display", "block");
				loading.close();
		};
		
		$(document).on("click", "#input[name='checkboxDisplayOption']", function () {
			alert($(this).index());
		});
		
		function convertNullToBank(obj) {
			if (obj == null) {
				obj = "";
			}
			
			return obj;
		}
		function execLayoutState(name){
			//ST3Lib.content.execLayoutState(layout_state, name);	
			ST3Lib.content.execLayoutState(name);	
		}
	});

window.setDisplayOpt = 
	function setDisplayOpt(i) {
	var isChecked = document.getElementsByName("checkboxDisplayOption").item(i).checked;
	if (isChecked) {
		document.getElementsByName("ChkDisplayOption").item(i).value = "1";
	} else {
		document.getElementsByName("ChkDisplayOption").item(i).value = "0";
	}
};

//Fixed IncidentForm_PH(IFT-ST3)_0.9.0_009 by Thanapon
window.loadBookmarksByTableResult =
	function loadBookmarksByTableResult(selectedTableName, selectedBookmark){
		var actionFormUrl = '';		
		actionFormUrl = $('#search-form').attr('action');
		ST3Lib.ajax(actionFormUrl + '/listBookmarkByTable', 'GET', {'isajax' : '1', 'selectedTableName' : selectedTableName}, function(datas){
			var $bookmarks = $('#bookmarks');
			$bookmarks.empty();
			if (datas != null){
				if(selectedBookmark === ""){
					$bookmarks.append('<option value="" selected="selected">&lt;All&gt;</option>');
				}else{
					$bookmarks.append('<option value="">&lt;All&gt;</option>');
				}
				if ( !datas || datas.length !== 0 ){
					for(i=0; i <= datas.length - 1; i++){
						//Set variable form
						data = datas[i];
						settingID = datas[i].settingID;
						if(selectedBookmark === settingID){
							$bookmarks.append('<option value="{0}" selected="selected">{1}</option>'.format(settingID, settingID));
						}else{
							$bookmarks.append('<option value="{0}">{1}</option>'.format(settingID, settingID));
						}
						
					}
				}
			}else{
				$bookmarks.append('<option value="" selected="selected">&lt;All&gt;</option>');
			}
		});
	};
//end Fixed IncidentForm_PH(IFT-ST3)_0.9.0_009 by Thanapon

window.clearForm =
	function clearForm(){
		$('#afterSearch-command').addClass('hide');
		$('select[name=result_length]').val('10');
		$('.dataTables_filter input').val('');
		$( '#result' ).dataTable().fnClearTable();
		ST3Lib.message.clear(true);
		$("#downloadControl").css("display", "none");
		//Fixed IncidentForm_PH(IFT-ST3)_0.9.0_009 by Thanapon
		loadBookmarksByTableResult('','');
		var form = $('#search-form');
		form.find('input[name=reportName]').attr('value', '');
		form.find('input[name=startRow]').attr('value', '');
		form.find('input[name=reportTitle]').attr('value', '');
		form.find('input[name=startColumn]').attr('value', '');
		form.find('select[name=tableName]').attr('value', '');
		form.find('select[name=tableName]').focus();
		//END Fixed IncidentForm_PH(IFT-ST3)_0.9.0_009 by Thanapon
	};

window.saveBookmark =
	function saveBookmark(){
	var actionFormUrl = '';		
	actionFormUrl = $('#search-form').attr('action');
	
	//Fixed IncidentForm_PH(IFT-ST3)_0.9.0_009 by Thanapon
	ST3Lib.message.clear(true);
	//end IncidentForm_PH(IFT-ST3)_0.9.0_009 by Thanapon

	$("#result-list-form input[name='tableName']").val($("#tableName option:selected").text());
	$("#result-list-form input[name='modelClassName']").val($("#tableName option:selected").val());
	$("#result-list-form input[name='bookmarkId']").val($("#bookmarks option:selected").val());
	$("#result-list-form input[name='reportName']").val($("#reportName").val());
	$("#result-list-form input[name='reportTitle']").val($("#reportTitle").val());
	$("#result-list-form input[name='startRow']").val($("#startRow").val());
	$("#result-list-form input[name='startColumn']").val($("#startColumn").val());
	
	var saveForm = $("#result-list-form");
	saveForm.attr({"action" : actionFormUrl+"/saveBookmark",
		 "ajax" : "saveBookmarkFinish"});
// 	saveForm.submit();
	var colSize = $("input[name='dbFieldName']").length;
	if (colSize > 60) {
		setGlobalVar();
	 	ST3Lib.ajax(actionFormUrl + '/saveBookmark', 
	 			'POST',
	 				{
	 					'isajax' 			: '1' 
	 					,'modelClassName' 	: gModelClassName 
	 					,'tableName' 		: gTableName
	 					,'WST30900BigData' 	: gBigData
	 					,'bookmarks' 		: gBookmarkId
	 					,'reportName'	 	: gReportName
	 					,'reportTitle' 		: gReportTitle
	 					,'startRow'		 	: gStartRow
	 					,'startColumn'	 	: gStartColumn
	 					,'generateExcelFlag': gGenExcelFlag
	 					,'fileType' 		: gFileType
	 					,'dbFieldName' 		: JSON.parse(JSON.stringify(gDbFieldName.join()))
	 					,'field' 			: JSON.parse(JSON.stringify(gField.join()))
	 					,'displayName' 		: JSON.parse(JSON.stringify(gDisplayName.join()))
	 					,'orderDisp' 		: JSON.parse(JSON.stringify(gOrderDisp.join()))
	 					,'pk' 				: JSON.parse(JSON.stringify(gPk.join()))
	 					,'dataType' 		: JSON.parse(JSON.stringify(gDataType.join()))
	 					,'criteria' 		: JSON.parse(JSON.stringify(gCriteria.join()))
	 					,'operationLogic' 	: JSON.parse(JSON.stringify(gOperationLogic.join()))
	 					,'chkDisplayOption' : JSON.parse(JSON.stringify(gChkDisplayOpt.join()))
	 					,'sort' 			: JSON.parse(JSON.stringify(gSort.join()))
	 				}
	 			, saveBookmarkFinish()
	 	);
	     
	} else {
		saveForm.submit();
	}
	
};

window.saveBookmarkFinish =
	function saveBookmarkFinish() {
	$("#WST30900SaveBookmark").attr("checked", false);
	if ( ST3Lib.message.countError() === 0 ) {
		var actionFormUrl = '';		
		actionFormUrl = $('#search-form').attr('action');
		//Fixed IncidentForm_PH(IFT-ST3)_0.9.0_009 by Thanapon
		var selectedTableName = $('#search-form').find('select[name=tableName] :selected').text();
		ST3Lib.ajax(actionFormUrl + '/listBookmarkByTable', 'GET', {'isajax' : '1', 'selectedTableName' : selectedTableName}, function(data, status){
		//END Fixed IncidentForm_PH(IFT-ST3)_0.9.0_009 by Thanapon
			var bookmark = $("#bookmarks");
			var options = bookmark.find('option');
			
			for(var i = 0; i < data.length; ++i) {
				var newBookmark = data[i].settingID;
				var found = false;
				for	(var j = 1; j < options.length; ++j) {
					if (newBookmark === options[j].value) {
						found = true;
						break;
					}
				}
				if (found === false){
					bookmark.append('<option value="'+ newBookmark +'" selected="selected">' + newBookmark + '</option>');
					//Fixed IncidentForm_PH(IFT-ST3)_0.9.0_011 by Thanapon 19/05/2015
					var loadingTarget = "#result_wrapper";
					var loading = ST3Lib.dialog.loading(loadingTarget);
					ST3Lib.ajax(actionFormUrl + '/changeBookmark', 'GET', {'isajax' : '1', 'bookmarkID' : newBookmark}, function(datas, status){
								displayAjaxResult(datas, status, loading);
						});	
					break;
					//Fixed IncidentForm_PH(IFT-ST3)_0.9.0_011 by Thanapon 19/05/2015
				}
			}
			
		});
	} else {
		ST3Lib.message.setAppendMode(false);
	}
};

window.xlsDownload =
	function xlsDownload(generateType) {
	var actionFormUrl = '';		
	actionFormUrl = $('#search-form').attr('action');
	
	$("#result-list-form").attr({"action" : actionFormUrl+"/downloadReport",
								 "method" : "POST",
								 "ajax" : "downloadFinish"});
	
	$("#result-list-form input[name='tableName']").val($("#tableName option:selected").text());
	$("#result-list-form input[name='modelClassName']").val($("#tableName option:selected").val());
	$("#result-list-form input[name='bookmarkId']").val($("#bookmarks option:selected").val());
	$("#result-list-form input[name='reportName']").val($("#reportName").val());
	$("#result-list-form input[name='reportTitle']").val($("#reportTitle").val());
	$("#result-list-form input[name='startRow']").val($("#startRow").val());
	$("#result-list-form input[name='startColumn']").val($("#startColumn").val());
	$("#result-list-form input[name='fileType']").val(generateType);
	
	ST3Lib.message.clear(true);
	var saveForm = $("#result-list-form");
	// else-if condition to support IE8 warning message
	var colSize = $("input[name='dbFieldName']").length;
	if (colSize > 60) {		
		setGlobalVar();
	 	ST3Lib.ajax(actionFormUrl + '/downloadReport', 
	 			'POST',
	 				{
	 					'isajax' 			: '1' 
	 					,'modelClassName' 	: gModelClassName 
	 					,'tableName' 		: gTableName
	 					,'WST30900BigData' 	: gBigData
	 					,'bookmarks' 		: gBookmarkId
	 					,'reportName'	 	: gReportName
	 					,'reportTitle' 		: gReportTitle
	 					,'startRow'		 	: gStartRow
	 					,'startColumn'	 	: gStartColumn
	 					,'generateExcelFlag': gGenExcelFlag
	 					,'fileType' 		: gFileType
	 					,'dbFieldName' 		: JSON.parse(JSON.stringify(gDbFieldName.join()))
	 					,'field' 			: JSON.parse(JSON.stringify(gField.join()))
	 					,'displayName' 		: JSON.parse(JSON.stringify(gDisplayName.join()))
	 					,'orderDisp' 		: JSON.parse(JSON.stringify(gOrderDisp.join()))
	 					,'pk' 				: JSON.parse(JSON.stringify(gPk.join()))
	 					,'dataType' 		: JSON.parse(JSON.stringify(gDataType.join()))
	 					,'criteria' 		: JSON.parse(JSON.stringify(gCriteria.join()))
	 					,'operationLogic' 	: JSON.parse(JSON.stringify(gOperationLogic.join()))
	 					,'chkDisplayOption' : JSON.parse(JSON.stringify(gChkDisplayOpt.join()))
	 					,'sort' 			: JSON.parse(JSON.stringify(gSort.join()))
	 				}
	 			, function(datas, status){
	 				downloadFinish(datas);
	 			}
	 	);
	     
	} else {
		saveForm.submit();
	}

};

window.setGlobalVar =
	function setGlobalVar(){
	
		gTableName 		= $("#result-list-form input[name='tableName']").val();
		gModelClassName	= $("#result-list-form input[name='modelClassName']").val();
		if (document.getElementById("WST30900BigData").checked){
			gBigData 	= '1';
		}else{
			gBigData 	= null;
		}
		gBookmarkId 	= $("#result-list-form input[name='bookmarkId']").val();
		gReportName 	= $("#result-list-form input[name='reportName']").val();
		gReportTitle 	= $("#result-list-form input[name='reportTitle']").val();
		gStartRow 		= $("#result-list-form input[name='startRow']").val();
		gStartColumn 	= $("#result-list-form input[name='startColumn']").val();
		gFileType 		= $("#result-list-form input[name='fileType']").val();
		gGenExcelFlag 	= $("#result-list-form input[name='generateExcelFlag']").val();
		
		gDbFieldName = $("#result-list-form input[name='dbFieldName']").map(function(){
			return $(this).val();
		}).get();
		gField = $("#result-list-form input[name='field']").map(function(){
			return $(this).val();
		}).get();
		gDisplayName = $("#result-list-form input[name='displayName']").map(function(){
			return $(this).val();
		}).get();
		gOrderDisp = $("#result-list-form input[name='orderDisp']").map(function(){
			return $(this).val();
		}).get();
		gPk = $("#result-list-form input[name='pk']").map(function(){
			return $(this).val();
		}).get();
		gDataType = $("#result-list-form input[name='dataType']").map(function(){
			return $(this).val();
		}).get();
		gCriteria = $("#result-list-form input[name='criteria']").map(function(){
			return $(this).val();
		}).get();
		gOperationLogic = $("#result-list-form select[name='operationLogic']").map(function(){
			return $(this).val();
		}).get();
		gChkDisplayOpt = $("#result-list-form input[name='checkboxDisplayOption']").map(function(){
			return $(this).val();
		}).get();
		gSort = $("#result-list-form input[name='sort']").map(function(){
			return $(this).val();
		}).get();
	
};


window.downloadFinish =
	function downloadFinish(data) {
	var status = data.status;
	if (status == "OK") {
		var actionFormUrl = $('#search-form').attr('action');
		
		ST3Lib.dialog.confirm('<spring:message code="MSTD0114ACFM" arguments="'+data.totalRecord+'"></spring:message>', 'MSTD0114ACFM', function(res){
			if (res) {
				var loadingTarget = "#result_wrapper";
				var loading = ST3Lib.dialog.loading(loadingTarget);
				var saveForm = $("#result-list-form");
				saveForm.attr({"action" : actionFormUrl+"/confirmDownload"});
				if ($("#WST30900BigData").is(':checked')) {
					// for ODB download.
					saveForm.attr("ajax", "resultDownload");
					saveForm.submit();
						setTimeout(function(){
							loading.close();
					}, 30);
				} else {
					// for online download
					saveForm.attr('target', 'downloadIframe').removeAttr("ajax");
					var postData = saveForm.serialize();
				    var formURL = actionFormUrl + "/confirmFileDownload";
					    
				   	$.fileDownload(formURL, {
				   		httpMethod: "POST",
				   	    data: postData,
			        	successCallback: function(url) {
			        	    setTimeout(function(){
								loading.close();
							}, 10);
			        	},
			        	failCallback: function (responseHtml, url, error) {
			        		var html = $.parseHTML(responseHtml);
			        		ST3Lib.message.addError($(html).text());
							ST3Lib.message.show(1);
			        		setTimeout(function(){
								loading.close();
							}, 10);
			        	}
				   	});
				}
			}
		});
	} else {
		$("#tableName").focus();
	}
};
window.resultDownload =
	function resultDownload(data) {
	tableName.focus();
};

window.numbersOnly =
	function numbersOnly(obj) {
	obj.value=obj.value.replace(/[^\d^.]/g,'');
};
</script>

<div id="search-criteria">
	<form:form method="post" id="search-form" action="${_mappingPath}" ajax="search" ajax-loading-target="#result_wrapper" autocomplete="off" validate-error="searchValidateError">
	<table cellSpacing="0" cellPadding="0" width="100%" border="0" class="Body_Label"> 
		<colgroup>
			<COL width="8%" />
			<COL width="32%"/>
			<COL width="8%" />
			<COL width="32%"/>
			<COL width="12%"/>
			<COL width="10%"/>				                                    
		</colgroup>
		<tbody>
		<tr>                                		
			<td align="left" class="Body_label"><span class="MandatoryFieldFont">*</span><spring:message code="ST3.WST30900.Label.TableView" />&nbsp;:</td>
			<td align="left" class="Body_label" >
				<select id="tableName" name="tableName" class="MandatoryField" style="width: 350px;">
					<option value="">&lt;Select&gt;</option>
					<c:forEach items="${listTableName}" var="item">
						<option value="${item.modelClassName}">${item.tableName}</option>
					</c:forEach>
				</select></td>
			<td align="left" class="Body_label"><spring:message code="ST3.WST30900.Label.Bookmarks" />&nbsp;:</td>
			<td align="left" class="Body_label" >
				<select id="bookmarks" name="bookmarks" style="width: 350px;">
					<option value="">&lt;All&gt;</option>
					<c:forEach items="${listSettingInfo}" var="item">
						<option value="${item.settingID}">${item.settingID}</option>
					</c:forEach>
				</select>
			</td>
			<td align="left" class="Body_label"><label><input type="checkbox" id="WST30900SaveBookmark" name="WST30900SaveBookmark" value="1" />
			<spring:message code="ST3.WST30900.Label.ChkSaveBookmark" /></label></td>
			<td align="right">
				<spring:message code="ST3.WST30900.Button.BtnClear" var="WST30900Clear"/>
				<sc2:button functionId="ST3090" screenId="WST30900" buttonId="WST30900Clear" 
					type="reset" value="${WST30900Clear}"  style="width:80px;" 
					styleClass="button" secured="false" onClick="clearForm()" />
			</td>
		</tr>
		<tr>
			<td align="left" class="Body_label" style="vertical-align: top;"><spring:message code="ST3.WST30900.Label.Option" /> :</td>
			<td align="left" class="Body_label" colspan="2">
				<div style="width: 100px; float: left"><spring:message code="ST3.WST30900.Label.ReportName" />&nbsp;:</div>
				<input type="text" name="reportName" id="reportName" size="45" /></td>
			<td align="left" class="Body_label" colspan="3">
				<div style="width: 100px; float: left"><spring:message code="ST3.WST30900.Label.StartRow" />&nbsp;:</div>
				<input type="text" name="startRow" id="startRow" /></td>
		</tr>
		<tr>
			<td align="left" class="Body_label">&nbsp;</td>
			<td align="left" class="Body_label" colspan="2">
				<div style="width: 100px; float: left"><spring:message code="ST3.WST30900.Label.ReportTitle" />&nbsp;:</div>
				<input type="text" name="reportTitle" id="reportTitle" size="45" /></td>
			<td align="left" class="Body_label" colspan="3">
				<div style="width: 100px; float: left"><spring:message code="ST3.WST30900.Label.StartColumn" />&nbsp;:</div>
				<input type="text" name="startColumn" id="startColumn" /></td>
		</tr>
		</tbody>
	</table>
	</form:form>
</div>
<div style="margin: 10px 0 10px 0;"><hr/></div>
<div id="search-result" class="overflow-hidden autoheight" >
	<div id="colsList" class="autoheight" >
		<form id="result-list-form" action="${_mappingPath}/saveBookmark" method="post"  class="autoheight overflow-visible">
			
			<input type="hidden" name="tableName" />
			<input type="hidden" name="bookmarkId" />
			<input type="hidden" name="reportName" />
			<input type="hidden" name="reportTitle" />
			<input type="hidden" name="startRow" />
			<input type="hidden" name="startColumn" />
			<input type="hidden" name="totalRecordFound" />
			<input type="hidden" name="modelClassName" />
			<input type="hidden" name="generateExcelFlag" />
			<input type="hidden" name="fileType" />
			
			<spring:message code="ST3.WST30900.Label.Field" var="Field"/>
			<spring:message code="ST3.WST30900.Label.DisplayName" var="DisplayName"/>
			<spring:message code="ST3.WST30900.Label.DisplayOrder" var="DisplayOrder"/>
			<spring:message code="ST3.WST30900.Label.PK" var="PK"/>
			<spring:message code="ST3.WST30900.Label.DataType" var="DataType"/>
			<spring:message code="ST3.WST30900.Label.Criteria" var="Criteria"/>
			<spring:message code="ST3.WST30900.Label.LogicalOperator" var="LogicOperator"/>
			<spring:message code="ST3.WST30900.Label.DisplayOption" var="DisplayOption"/>
			<spring:message code="ST3.WST30900.Label.Sort" var="Sort"/>
			
			<datatables:table id="result" data="${colsList}" cdn="false" row="row" cssClass="result fixedheader"
				paginate="false" cssStyle="height:325px;" sort="false" filter="false"
				info="false" displayLength="10" paginationType="full_numbers">
				<datatables:column title="${Field}" cssClass="col1" sortable="false" />
				<datatables:column title="${DisplayName}" cssClass="col2" sortable="false"/>
				<datatables:column title="${DisplayOrder}" cssClass="col3" sortable="false" cssCellClass="text-align: center;" />
				<datatables:column title="${PK}" cssClass="col4" sortable="false" cssCellClass="text-align: center;" />
				<datatables:column title="${DataType}" cssClass="col5" sortable="false" />
				<datatables:column title="${Criteria}" cssClass="col6" sortable="false" />
				<datatables:column title="${LogicOperator}" cssClass="col7" sortable="false" />
				<datatables:column title="${DisplayOption}" cssClass="col8" sortable="false" />
				<datatables:column title="${Sort}" cssClass="col9" sortable="false" cssCellClass="text-align: center;" />
			</datatables:table>
			<br/>
			<div id="downloadControl" style="display:none;">
				<div>
				<label style="font-weight:700;"><input type="checkbox" id="WST30900BigData" name="WST30900BigData" value="1" />
				<spring:message code="ST3.WST30900.Label.ChkBigData" /></label>
				</div>
				<div>
				<spring:message code="ST3.WST30900.Button.BtnXlsDownload" var="WST30900XlsDownload"/>
					<sc2:button functionId="ST3090" screenId="WST30900" buttonId="WST30900XlsDownload" 
						type="button" value="${WST30900XlsDownload}"  style="width:150px;" 
						secured="false" onClick="xlsDownload('E')" />
				<spring:message code="ST3.WST30900.Button.BtnXlsxDownload" var="WST30900XlsxDownload"/>
					<sc2:button functionId="ST3090" screenId="WST30900" buttonId="WST30900XlsxDownload" 
						type="submit" value="${WST30900XlsxDownload}"  style="width:150px;" 
						secured="false" onClick="xlsDownload('X')" />
				<spring:message code="ST3.WST30900.Button.BtnCsvDownload" var="WST30900CsvDownload"/>
					<sc2:button functionId="ST3090" screenId="WST30900" buttonId="WST30900CsvDownload" 
						type="submit" value="${WST30900CsvDownload}"  style="width:120px;" 
						secured="false" onClick="xlsDownload('C')" />
				</div>
			</div>
		</form>
		<iframe id="downloadIframe" name="downloadIframe" frameborder="0" style="width:100px;height:100px;border:0px solid black;position:static;left:0px;display:none;" ></iframe>
	</div>
		
		
</div>
