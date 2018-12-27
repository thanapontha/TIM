<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="datatables" uri="http://github.com/dandelion/datatables" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %> 
<%@ taglib prefix="views" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sc2" uri="/WEB-INF/tld/sc2.tld"%>

<views:script src="json2.js"/>
<views:script src="jquery-ui-1.9.1/jquery.dataTables.js"/>
<views:style src="jquery.dataTables.css"/>
<style>
	.result{
		table-layout:fixed !important;
	}
		.result td{
			word-wrap:break-word !important;
		}
	.dataTables_length {
		display:block;	
		position: relative;
		top: 2px;
	}
	#search-criteria {
		margin-bottom: 5px;
	}
		#search-criteria tbody td div {
			padding: 0px 10px 0px 90px;
			text-align: left;
			position: relative;
		}
			#search-criteria  tbody  td label.label {
				padding: 0px 5px 0px 5px;
				position:absolute;
				left: 0px;
				*left: -8px;/*fixed for IE7*/
				_left: -100px;/*fixed for IE6*/
				top: 2px;
				width: 90px;
				text-align: right;
				display: block;
			}
		#search-criteria tbody td.wide div{
			padding-left: 100px;
		}
			#search-criteria  tbody  td.wide label.label {
				*left: -8px;/*fixed for IE7*/
				_left: -110px;/*fixed for IE6*/
			}
			#search-criteria tbody td.wide label.label{
				width: 100px;
			}
		#search-criteria input.textbox, #search-criteria select{
			width: 100%;
			min-width: 120px;
		}
		#search-criteria select {
			padding: 1px;
			margin-top: 2px;
			margin-bottom: 2px;
		}
	.button-panel {
		margin: 10px 0px 10px 0px;
	}
	
	.result .col1 {
		width: 50px;
		text-align: center;
	}
	.result .col2 {
		width: 40px;
		text-align: center;
	}
	.result .col3{
		width: 100px;
	}
	.result .col4, .result .col5, .result .col7, .result .col10 {
		width: 80px;
	}
	.result .col9{
		width: 200px;
	}
	.result .col6 {
		width: 300px;
	}
	.result .col8 {
		width: 500px;
	}
	
	.result .col11, .result .col12 {
		width: 150px;
	}
	.result .col13 {
		width: 10px;
	}
	.dataTables_paginate  span {
		display: none;
	}
	
	#join-domain-desc{
		margin-left: 57px;
		font-size:0.9em;
	}
	
	.checkbox {
		padding: 0px;
		margin: 0px;
	}
</style>
<script language="javascript" type="text/javascript">
$(document).ready(function() {
	
    $( "#requestDate" ).datepicker({
      showOn: "button",
      buttonImage: _imagePath + "icons/cal.gif",
      buttonImageOnly: true,
      buttonText: "Select date",
      dateFormat: "dd/mm/yy"
    });
});

(function($){
	var actionFormUrl = '';
	
	ST3Lib.validate.format_msg['id'] = {
			regEx: /^B([0-9A-Z]+)?$/,
			ErrorCode: 'MSTD0043AERR'
		};
	
	ST3Lib.validate.format_msg['code'] = {
			regEx: /^([0-9a-zA-Z\-_]+)?$/,
			ErrorCode: 'MSTD0043AERR'
		};
	ST3Lib.validate.format_msg['bodycode'] = {
			regEx: /^([0-9a-zA-Z\ ]+\*?)?$/,
			ErrorCode: 'MSTD0043AERR'
		};
	ST3Lib.validate.format_msg['date'] = {
			regEx: /^\d{2}\/\d{2}\/\d{4}$/,
			ErrorCode: 'MSTD0043AERR'
		};
	ST3Lib.validate.format_msg['userId'] = {
			regEx: /^[0-9a-zA-Z\.]*\*?$/,
			ErrorCode: 'MSTD0043AERR'
		};
	$(function(){
		actionFormUrl = $('#search-form').attr('action');
		window.roleBatchEditor = ST3Lib.roweditor( '#result', {
			template: "#batchtemplate",
			success: function( type, tr ){
				if ( tr.index() %2 > 0 ) tr.addClass( 'even' );
				else tr.addClass( 'odd' );
			}
		});
		
		$('#search-form').submit(clickSearch)
		.on('keypress', 'select,input:not(input:submit,input:button,input:reset,button)', function(e){
			if (e.keyCode === 13 || e.keyCode === 10) {
				$('#WST33050Search').click();	
				e.preventDefault();
			}
		});
		$('#result').on('click', 'input:radio', function(ev){
			var radio = $( this );
			if (radio.is(':disabled')) return;
			radio.prop( 'checked', true );//.focus();
			lastSelectedId = radio.val();
			
			var obj = JSON.parse(radio.parent().find('.serialize').text());
			var form = $('#result-list-form');
			form.find('input:hidden[name=batchId]').val(obj.batchId);
			form.find('input:hidden[name=batchNo]').val(obj.batchNo);
			form.find('input:hidden[name=appId]').val(obj.appId);
			var reqDate = obj.requestDate == null ? obj.runDate : obj.requestDate;
			form.find('input:hidden[name=requestDate]').val(reqDate);
		});
		$('#search-form').find('input[name=projectCode]').focus();
	});
	
	function moveHScrollBar(value){
		$('#search-result .dataTable-wrapper').scrollLeft(0);
	}
	
	var lastSearchCriteria = {}, isSearchButtonClick = false, lastAction = ''
		, lastSelectedId = null, tempLastSelectedId = null;
	function restoreSearchCriteria(){
		var searchForm = $('#search-form');
		searchForm.find('input[name=projectCode]').val(lastSearchCriteria.projectCode);
		searchForm.find('input[name=batchId]').val(lastSearchCriteria.batchId);
		searchForm.find('input[name=batchNo]').val(lastSearchCriteria.batchNo);
		searchForm.find('select[name=reqestDate]').val(lastSearchCriteria.reqestDate);
		searchForm.find('select[name=requestBy]').val(lastSearchCriteria.requestBy);
	}
	function saveSearchCriteria(){
		var searchForm = $('#search-form');
		lastSearchCriteria.projectCode = searchForm.find('input[name=projectCode]').val();
		lastSearchCriteria.batchId = searchForm.find('input[name=batchId]').val();
		lastSearchCriteria.batchName = searchForm.find('input[name=batchName]').val();
		lastSearchCriteria.reqestDate = searchForm.find('select[name=reqestDate]').val();
		lastSearchCriteria.requestBy = searchForm.find('select[name=requestBy]').val();
	}
	function clickSearch(ev){
		isSearchButtonClick = !!ev.originalEvent; //trigger by user  or not (convert to boolean)
		if (isSearchButtonClick) {
			lastSelectedId = '';
			clearFilter();
			$( '#result' ).dataTable().fnClearTable(false);
			lastAction = 'search';
			saveSearchCriteria();
		}
	}
	
	window.clearFilter = 
	function clearFilter(selector){
		$((selector || '') + '.dataTable:not(.fixed-header)').each(function(){
			$(this).closest('.dataTables_wrapper').find('.dataTables_filter input').val('');
			$(this).dataTable().fnSettings().oPreviousSearch.sSearch = '';
			$(this).dataTable().fnDraw();
		});
	}
	
	window.moveToLastSelectId = function(tableSelector, id){
		return ST3Lib.dataTable.moveToLastSelectId(tableSelector, id);
	}
	
	
	window.getNextSelectId = function(tableSelector, id, step, fnMatch){
		return ST3Lib.dataTable.getNextSelectId(tableSelector, id, step, fnMatch);
	}
	
	window.getSelectId = function(tableSelector, index){
		return ST3Lib.dataTable.getSelectId(tableSelector, index);
	}
	
	
	window.doDeleteStatus = 
	function doDeleteStatus(){
		ST3Lib.message.clear(true);
		//moveToLastSelectId('#result', lastSelectedId);
		var radio = $('#result').find('input:radio:checked');
		if (radio.length === 0) {
			$('#search-form').find('input[name=projectCode]').focus();
			ST3Lib.message.clear(true);
			ST3Lib.message.addError('<spring:message code="MSTD1016AERR"></spring:message>');
			ST3Lib.message.show(1);
			$('#search-form').find('input[name=projectCode]').focus();
			return;
		} 
		restoreSearchCriteria();
		ST3Lib.dialog.confirm('<spring:message code="MSTD0001ACFM"></spring:message>', 'MSTD0001ACFM', function(ret){
			if (ret) {
				lastAction = 'delete';
				lastSelectedId = tempLastSelectedId = getNextSelectId('#result', lastSelectedId);
				
				var form = $('#result-list-form');
				var hidden = form.find('input:hidden[name=batchNo],input:hidden[name=batchId],input:hidden[name=updateDate]')
					.prop('disabled', false);
				try{
					form.attr('action', actionFormUrl + '/delete').attr('_method', 'delete').submit();
					hidden.prop('disabled', true);
				}catch(e){
					hidden.prop('disabled', true);
					throw e;
				}
				$('#search-form').find('input[name=projectCode]').focus();
				return;
			}else{
				$('#search-form').find('input[name=projectCode]').focus();	
			}
		});
			
	};
	
	window.doCancelStatus = 
	function doCancelStatus(){
			ST3Lib.message.clear(true);
			//moveToLastSelectId('#result', lastSelectedId);
			var radio = $('#result').find('input:radio:checked');
			if (radio.length === 0) {
				$('#search-form').find('input[name=projectCode]').focus();
				ST3Lib.message.clear(true);
				ST3Lib.message.addError('<spring:message code="MSTD1018AERR"></spring:message>');
				ST3Lib.message.show(1);
				$('#search-form').find('input[name=projectCode]').focus();
				return;
			} 
			restoreSearchCriteria();
			ST3Lib.dialog.confirm('<spring:message code="MSTD0003ACFM"></spring:message>', 'MSTD0003ACFM', function(ret){
				if (ret) {
					lastAction = 'cancel';
					lastSelectedId = tempLastSelectedId = getNextSelectId('#result', lastSelectedId);
					
					var form = $('#result-list-form');
					var hidden = form.find('input:hidden[name=batchNo],input:hidden[name=batchId],input:hidden[name=updateDate]')
						.prop('disabled', false);
					try{
						form.attr('action', actionFormUrl + '/cancel').attr('_method', 'delete').submit();
						hidden.prop('disabled', true);
					}catch(e){
						hidden.prop('disabled', true);
						throw e;
					}
					$('#search-form').find('input[name=projectCode]').focus();
					return;
				}else{
					$('#search-form').find('input[name=projectCode]').focus();	
				}
			});
	};
	
	window.doDetailsStatus = 
	function doDetailsStatus(){
		ST3Lib.message.clear(true);
		//moveToLastSelectId('#result', lastSelectedId);
		var radio = $('#result').find('input:radio:checked');
		if (radio.length === 0) {
			$('#search-form').find('input[name=projectCode]').focus();
			ST3Lib.message.clear(true);
			ST3Lib.message.addError('<spring:message code="MSTD1019AERR"></spring:message>');
			ST3Lib.message.show(1);
			$('#search-form').find('input[name=projectCode]').focus();
			return;
		} 
		restoreSearchCriteria();

		var form = $('#result-list-form');
		var hidden = form.find('input:hidden[name=batchId],input:hidden[name=appId],input:hidden[name=requestDate]')
			.prop('disabled', false);
		$.post(actionFormUrl + '/details', hidden, 
		  function (data) {
			if(data.appLogURL != null && (data.appLogURL != '' && data.appLogURL != 'null')){
		      	window.open(data.appLogURL, "WST33010");
		      	hidden.prop('disabled', true);
			}else{
				ST3Lib.message.addError(data.errorMessages);
				ST3Lib.message.show(1);
			}
		});
	};

	window.doDownloadStatus =
	function doDownloadStatus(){
		var loading = ST3Lib.dialog.loading('#WST33050Download');
		ST3Lib.message.clear(true);
		restoreSearchCriteria();
		var searchForm = $('#search-form');
		var ajax = searchForm.attr('ajax');
		var action = searchForm.attr('action');
		
		$('#downloadIframe').off('load').on('load', function(){
			try{
				var text = $('#downloadIframe').contents().text();
				if (text) {
					var obj = JSON.parse(text);
					ST3Lib.message.clear();
					ST3Lib.message.setPayload(obj);
					ST3Lib.message.setAppendMode(false);
					$('#search-form').submit();
				}
				loading.close();
			}catch(e){}
		});
		lastAction = 'download';
		searchForm.attr('action', actionFormUrl + '/download').attr('target', 'downloadIframe')
			.removeAttr('ajax')
			.submit();
		searchForm.attr('ajax', ajax).attr('action', action).removeAttr('target');
		$('#search-form').find('input[name=projectCode]').focus();
		setTimeout(function(){
			loading.close();
			$('#search-form').find('input[name=projectCode]').focus();
		}, 1500);
	};
	
	window.searchFinish =
	function searchFinish(datas, loading){
		if (datas === null) return;
		ST3Lib.message.setAppendMode(false);
		
		var isSearchBtn = isSearchButtonClick;
		isSearchButtonClick = false;
		
		var dataTable = $( '#result' ).dataTable();
		dataTable.fnClearTable(false);//no redraw
		datas = datas.batchStatusList;
		if ( !datas || datas.length === 0 ){

			ST3Lib.message.clear(true);
			ST3Lib.message.addError('<spring:message code="MSTD0059AERR"></spring:message>');
			ST3Lib.message.show(1);
			dataTable.fnDraw(true);
			$('#afterSearch-command').addClass('hide');
			return;	
			
		}else{
			$('#afterSearch-command').removeClass('hide');
			$('#add-command').removeClass('hide');
		}
		ST3Lib.sizing.autoHeight();
		if ( loading ) {
			loading.autoClose = false;
			loading.resize();
		}	
		var setting = dataTable.fnSettings();
		setting.aoColumns[0].sClass = 'col1';
		setting.aoColumns[1].sClass = 'col2 rownum';
		setting.aoColumns[2].sClass = 'col3';
		setting.aoColumns[3].sClass = 'col4';
		setting.aoColumns[4].sClass = 'col5';
		setting.aoColumns[5].sClass = 'col6';
		setting.aoColumns[6].sClass = 'col7';
		setting.aoColumns[7].sClass = 'col8';
		setting.aoColumns[8].sClass = 'col9';
		setting.aoColumns[9].sClass = 'col10';
		setting.aoColumns[10].sClass = 'col11';
		setting.aoColumns[11].sClass = 'col12';
		setting.aoColumns[12].sClass = 'col13';
						
		var tempDiv = $('<div/>');
		dataTable.fnClearTable(false);//no redraw
		var i = 0;
		var arrayData = [];
		var processor = setInterval(function(){
		var processPerTick = 10;
		
		for(var j = 0; j < processPerTick; ++j) {
			var data = datas[i++];
			if ( data ) {
				var requestDate = null;
				if(data.requestDate){
					requestDate = new Date(+data.requestDate);
					var requestTime = zeroLead( requestDate.getHours(), 2) + ':' 
						+ zeroLead(requestDate.getMinutes(), 2) + ':' 
						+ zeroLead(requestDate.getSeconds(), 2);
					requestDate = requestDate.format('dd/mm/yy ' + requestTime);
				}
				
				var runDate = null;
				if(data.runDate){
					runDate = new Date(+data.runDate);
					var runTime = zeroLead( runDate.getHours(), 2) + ':' 
									+ zeroLead(runDate.getMinutes(), 2) + ':' 
									+ zeroLead(runDate.getSeconds(), 2);
					runDate = runDate.format('dd/mm/yy ' + runTime)	;
				}

				var serialize = tempDiv.text(JSON.stringify(data)).html();
				arrayData.push([ 
									 '<input class="radio" type="radio" name="batchNo" value="' + 
									  data.batchNo +
									  '" /><div class="serialize" style="display:none">' + serialize + '</div>' 
								    , ''
								    , data.appId || ''
								    , data.projectCode || ''
								    , data.batchId || ''
								    , data.batchName || ''
								    , data.supportId || ''
								    , data.description || ''
								    , data.status || ''
								    , data.requestBy || ''
								    , requestDate  || ''
								    , runDate || ''
								    , i
								]);
			}else{
				processPerTick = 0;
				clearInterval(processor);
				dataTable.fnAddData(arrayData);
				dataTable.fnSort([ [12,'asc']]);
				dataTable.fnSetColumnVis(12,false);
				if (moveToLastSelectId('#result', lastSelectedId) === false){
					moveToLastSelectId('#result', getSelectId('#result', 0));
				}
				loading.close();
				if (isSearchBtn)
					$('#search-form').find('input[name=projectCode]').focus();
				break;
			}
		}
		},1 );
	};	
	window.clearForm =
	function clearForm(){
		$('#afterSearch-command').addClass('hide');
		$('select[name=result_length]').val('10').change();
		$('.dataTables_filter input').val('');
		$( '#result' ).dataTable().fnClearTable();
		ST3Lib.message.clear(true);
		$('#search-form').find('input[name=projectCode]').focus();
	};
	
	window.clearResult =
	function clearResult(){
		$('#afterSearch-command').addClass('hide');
		$( '#result' ).dataTable().fnClearTable();
	};
	
	window.updateBatchFinish =
	function updateBatchFinish(){
			if ( ST3Lib.message.countError() === 0 ) {
				$( 'input[type=radio]' ).prop( 'disabled', false );
				var templateTR = $('#result').find('.template');
				
				//lastSelectedId = tempLastSelectedId;
				roleBatchEditor.cancel();
				$('#save-panel').addClass('hide');
				if ( $('#result > tbody > tr').length === 0 ){
					$('#add-command').removeClass('hide');
					$('#result').dataTable().fnDraw();
					
					$('#afterSearch-command').addClass('hide');
				}else $('#afterSearch-command, #add-command').removeClass('hide');
				$('.dataTables_length').show();
				
				ST3Lib.content.disabled( '#search-field', false );
				ST3Lib.content.disabled( '#search-panel', false );
				$('#search-panel').removeClass('hide');
				
				var searchForm = $('#search-form');
				var curprojectCodeValue = searchForm.find('input[name=projectCode]').val();
				saveSearchCriteria();
				if (curprojectCodeValue)
					searchForm.submit();
				else{
					$('#result').dataTable().fnClearTable();
					if (curprojectCodeValue){
						$('#afterSearch-command').addClass('hide');
						
						ST3Lib.message.addError('<spring:message code="MSTD0059AERR"></spring:message>');
						ST3Lib.message.show(1);
					}
				}
				
				moveHScrollBar(0);
			}else{
				
				ST3Lib.message.setAppendMode(false);
				$('#result input:text:first').focus();
				
				if ( $('#result > tbody > tr').length === 0 ){
					$('#afterSearch-command').addClass('hide');
				}
			}
		};
	
})(ST3Lib.$);

//to come on first page when change the page length.
var last_val;
$('.dataTables_length select').live('change', function(){
	if(this.value != last_val){
		$('#result_first').trigger('click');
		$('input[type=radio]').first().trigger('click');
		last_val = this.value;
	}
 });
</script>
<div id="search-criteria" style="zoom:1;position:relative;">
	<form:form method="post" id="search-form" action="${_mappingPath}" ajax="searchFinish" 
		ajax-loading-target="#result_wrapper" validate-error="clearResult">
	<div id="search-field" style="zoom:1;position:relative;">
		<table width="100%"><tbody>
			<tr>
				<td width="25%">
					<div><label class="label" for="projectCode" style="width: 92px"><span class="MandatoryFieldFont">*</span>
					<spring:message code="ST3.WST33050.Label.ProjectCode"/>:</label>
					<input class="MandatoryField" type="text" name="projectCode" style="text-transform:uppercase;width:175px;" maxlength="3" format-msg="code" ></div>
				</td>
				<td width="45%">
					<div><label class="label" for="requestDate" style="width: 140px"><span class="MandatoryFieldFont">*</span>
					<spring:message code="ST3.WST33050.Label.RequestDate"/>:</label></div>
					<span style="padding-left: 140px;"><input class="MandatoryField" type="text" id="requestDate" name="requestDate" style="width:133px;" maxlength="10" 
					   		default="${defaultRequestDate}" value="${defaultRequestDate}" format-msg="date"></span>
				</td>
				<td width="30%">
					<div><label class="label" for="batchId" style="width: 92px">
					<spring:message code="ST3.WST33050.Label.BatchID"/>:</label>
					<input class="textbox" type="text" name="batchId" style="text-transform:uppercase;width:175px;" maxlength="10" format-msg="code" ></div>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<div><label class="label" for="batchName" style="width: 92px">
					<spring:message code="ST3.WST33050.Label.BatchName"/>:</label>
					<input class="textbox" type="text" name="batchName" style="width:650px;" maxlength="70" format-msg="bodycode" ></div>
				</td>
				<td>
					<div><label class="label" for="requestBy" style="width: 92px"><spring:message code="ST3.WST33050.Label.RequestBy"/>:</label>
						<input class="textbox" type="text" name="requestBy" style="text-transform:uppercase;width:175px;" maxlength="20" format-msg="userId" >
					</div>
				</td>
				<td></td>
			</tr>
		</tbody></table>
	</div>
	<div class="button-panel" style="zoom:1;position:relative;">
		<div id="search-panel" style="float:left">
			<spring:message code="ST3.WST33050.Label.BtnSearch" var="WST33050Search" />
			<sc2:button functionId="ST3305" screenId="WST33050" buttonId="WST33050Search" 
				type="submit" value="${WST33050Search}"  style="width:80px;"
				styleClass="button"
			/>
			<spring:message code="ST3.WST33050.Label.BtnClear" var="WST33050Clear" />
			<sc2:button functionId="ST3305" screenId="WST33050" buttonId="WST33050Clear"
				type="reset" value="${WST33050Clear}"  style="width:80px;"
				styleClass="button" secured="false"
				onClick="clearForm();"
			/>
		</div>
		<div id="alloperation-command" style="float:right;text-align: right;white-space: nowrap;">
			<div id="afterSearch-command" class="hide" style="display:inline;zoom:1;position:relative;">
				<spring:message code="ST3.WST33050.Label.BtnDetails" var="WST33050Details"/>
				<sc2:button functionId="ST3305" screenId="WST33050" buttonId="WST33050Details" 
					type="button" value="${WST33050Details}" 
					styleClass="button"
					style="width:80px;"
					onClick="doDetailsStatus();" 
					/>
				
				<spring:message code="ST3.WST33050.Label.BtnDelete" var="WST33050Delete" />
				<sc2:button functionId="ST3305" screenId="WST33050" buttonId="WST33050Delete" 
					type="button" value="${WST33050Delete}" 
					styleClass="button"
					style="width:80px;"
					onClick="doDeleteStatus();"
					/>
				
				<spring:message code="ST3.WST33050.Label.BtnCancel" var="WST33050Cancel"/>
				<sc2:button functionId="ST3305" screenId="WST33050" buttonId="WST33050Cancel" 
					type="button" value="${WST33050Cancel}" 
					styleClass="button"
					style="width:80px;"
					onClick="doCancelStatus();" 
					/>
							
				<spring:message code="ST3.WST33050.Label.BtnDownload" var="WST33050Download" />
				<sc2:button functionId="ST3305" screenId="WST33050" buttonId="WST33050Download" 
					type="button" value="${WST33050Download}" 
					styleClass="button"
					style="width:80px;"
					onClick="doDownloadStatus();"
					/>
			</div>
		</div>
	</div>
	<div class="clear"></div>
	</form:form>
</div>
<hr>
<div id="search-result" class="overflow-hidden autoheight" style="margin-top: 5px;" >
	<div id="batchlist" class="autoheight" >
		<form id="result-list-form" action="batchStatusList" method="post"  ajax="updateBatchFinish" 
			ajax-loading-target="#result" class="autoheight overflow-visible">
			<input type="hidden" name="batchNo" disabled="disabled" />
			<input type="hidden" name="batchId" disabled="disabled" />
			<input type="hidden" name="appId" disabled="disabled" />
			<input type="hidden" name="requestDate" disabled="disabled" />
			
		    <spring:message code="ST3.WST33050.Label.ColNo" var="ColNo"/>
		    <spring:message code="ST3.WST33050.Label.ColApplicationID" var="ColApplicationID"/>
		    <spring:message code="ST3.WST33050.Label.ColProjectCode" var="ColProjectCode"/>
		    <spring:message code="ST3.WST33050.Label.ColBatchID" var="ColBatchID"/>
		    <spring:message code="ST3.WST33050.Label.ColBatchName" var="ColBatchName"/>
		    <spring:message code="ST3.WST33050.Label.ColSupportID" var="ColSupportID"/>
		    <spring:message code="ST3.WST33050.Label.ColDescription" var="ColDescription"/>
		    <spring:message code="ST3.WST33050.Label.ColStatus" var="ColStatus"/>
		    <spring:message code="ST3.WST33050.Label.ColRequestBy" var="ColRequestBy"/>
		    <spring:message code="ST3.WST33050.Label.ColRequesDate" var="ColRequesDate"/>
		    <spring:message code="ST3.WST33050.Label.ColRunDate" var="ColRunDate"/>
		    <datatables:table id="result" data="${batchlist}" cdn="false" row="row" cssClass="result fixedheader fixedcolumn" 
		    	paginate="true" cssStyle="width:1590px;height:330px;"
				info="true" displayLength="10" paginationType="full_numbers" fixedPosition="col">
				<datatables:column title="" cssClass="col1" sortable="false" searchable="false" />
		        <datatables:column title="${ColNo}" cssClass="col2 rownum fixed" sortable="false" searchable="false" />
		        <datatables:column title="${ColApplicationID}" cssClass="col3" sortable="false"  />
		        <datatables:column title="${ColProjectCode}" cssClass="col4" sortable="false" />
				<datatables:column title="${ColBatchID}" cssClass="col5" sortable="false" />
				<datatables:column title="${ColBatchName}" cssClass="col6" sortable="false" />
				<datatables:column title="${ColSupportID}" cssClass="col7" sortable="false" />
				<datatables:column title="${ColDescription}" cssClass="col8" sortable="false" searchable="false" />
				<datatables:column title="${ColStatus}" cssClass="col9" sortable="false" />
				<datatables:column title="${ColRequestBy}" cssClass="col10" sortable="false" />
				<datatables:column title="${ColRequesDate}" cssClass="col11" sortable="false" />
				<datatables:column title="${ColRunDate}" cssClass="col12" sortable="false" />
				<datatables:column title="" cssClass="col13" sortable="false" searchable="false"/>
			</datatables:table>
		</form>
	</div>
	<iframe id="downloadIframe" name="downloadIframe" frameborder="0" style="width:100px;height:100px;border:0px solid black;position:static;left:0px;display:none;" ></iframe>
</div>