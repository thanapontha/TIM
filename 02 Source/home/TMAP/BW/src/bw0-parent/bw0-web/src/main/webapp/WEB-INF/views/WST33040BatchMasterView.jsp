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
	.result .col3, .result .col4 {
		width: 100px;
	}
	.result .col5 {
		width: 300px;
	}
	.result .col6, .result .col7 {
		width: auto !important;
	}
	.result .col8 {
		width: 100px;
	}
	.result .col9 {
		width: 250px;
	}
	.result .col10 {
		width: 150px;
	}
	.result .col12, .result .col14 {
		width: 100px;
	}
	.result .col11, .result .col13 {
		width: 150px;
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
(function($){
	var actionFormUrl = '';
	var __priorityValue = ${priorityJSON};
	var __concurrencyValue = ${concurrencyJSON};
	
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
	ST3Lib.validate.format_msg['pathcode'] = {
			regEx: /^([0-9a-zA-Z\-_./]+)?$/,
			ErrorCode: 'MSTD0043AERR'
		};
	$(function(){
		actionFormUrl = $('#search-form').attr('action');
		window.roleBatchEditor = ST3Lib.roweditor( '#result', {
			template: "#batchtemplate",
			success: function( type, tr ){
				if ( tr.index() %2 > 0 ) tr.addClass( 'even' );
				else tr.addClass( 'odd' );
				if ( type === 'add' ) {
					addrow(tr);
					var projectCodeCtrl = $('#result').find('input[name=projectCode]').focus();
					$('input[type=radio]').prop( 'disabled', true );
					projectCodeCtrl.focus();//fixed for ie6
					ST3Lib.message.clear();
					ST3Lib.message.hide(true);
					
				}else if (type === 'edit') {
					tr.find('input:radio').click();
					$('input[type=radio]').prop( 'disabled', true );
					ST3Lib.message.clear();
					ST3Lib.message.hide(true);
					var value = tr.find('select[name=priorityLevel]').attr('_value');
					var priorityKeys = Object.keys(__priorityValue);
					for (var i = 0; i < priorityKeys.length; ++i) {
						if ( __priorityValue[priorityKeys[ i ]] == value ) {
							tr.find('select[name=priorityLevel]').val(priorityKeys[ i ]);		
							break;
						}
					}
					
					var value = tr.find('select[name=concurrency]').attr('_value');
					var concurrencyKeys = Object.keys(__concurrencyValue);
					for (var i = 0; i < concurrencyKeys.length; ++i) {
						if ( __concurrencyValue[concurrencyKeys[ i ]] == value ) {
							tr.find('select[name=concurrency]').val(concurrencyKeys[ i ]);		
							break;
						}
					} 
				}else if ( type === 'cancel' ) {
					$( 'input[type=radio]' ).prop( 'disabled', false );
					tr.find('input[type=radio]').click();
				}
			}
		});
		
		$('#search-form').submit(clickSearch)
		.on('keypress', 'select,input:not(input:submit,input:button,input:reset,button)', function(e){
			if (e.keyCode === 13 || e.keyCode === 10) {
				$('#WST33040Search').click();	
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
			form.find('input:hidden[name=updateDate]').val(obj.updateDate);
			form.find('input:hidden[name=projectCode]').val(obj.id.projectCode);
			form.find('input:hidden[name=batchId]').val(obj.id.batchId);
			form.find('input:hidden[name=batchName]').val(obj.batchName);
			form.find('input:hidden[name=priorityLevel]').val(obj.priority);
			form.find('input:hidden[name=concurrency]').val(obj.concurrency);
		});
		setSelectChange();
		$('#search-form').find('input[name=projectCode]').focus();
	});
	
	function setSelectChange(){
		var selects = [ $('select[name=priorityLevel]'),  $('select[name=concurrency]') ];
		var startIndex = 0;
		var tmpValue = [__priorityValue, __concurrencyValue];

		if (startIndex > 0 && selects[startIndex-1].val() === '')
			return;

		while ( startIndex < selects.length ) {
			var values = null;
			if (tmpValue[startIndex].splice)
				values = tmpValue[startIndex];
			else values = Object.keys(tmpValue[startIndex]).sort();
			for(var i = 0; i < values.length; ++i) {
				selects[startIndex].append('<option value="'+values[i]+'">' + tmpValue[startIndex][values[i]] + '</option>');
			}
			startIndex ++;
		}
	} 
	
	function moveHScrollBar(value){
		$('#search-result .dataTable-wrapper').scrollLeft(0);
	}
	
	var lastSearchCriteria = {}, isSearchButtonClick = false, lastAction = ''
		, lastSelectedId = null, tempLastSelectedId = null;
	function restoreSearchCriteria(){
		var searchForm = $('#search-form');
		searchForm.find('input[name=projectCode]').val(lastSearchCriteria.projectCode);
		searchForm.find('input[name=batchId]').val(lastSearchCriteria.batchId);
		searchForm.find('input[name=batchName]').val(lastSearchCriteria.batchName);
		searchForm.find('select[name=priorityLevel]').val(lastSearchCriteria.priority);
		searchForm.find('select[name=concurrency]').val(lastSearchCriteria.concurrency);
	}
	function saveSearchCriteria(){
		var searchForm = $('#search-form');
		lastSearchCriteria.projectCode = searchForm.find('input[name=projectCode]').val();
		lastSearchCriteria.batchId = searchForm.find('input[name=batchId]').val();
		lastSearchCriteria.batchName = searchForm.find('input[name=batchName]').val();
		lastSearchCriteria.priority = searchForm.find('select[name=priorityLevel]').val();
		lastSearchCriteria.concurrency = searchForm.find('select[name=concurrency]').val();
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
	};
	
	window.moveToLastSelectId = function(tableSelector, id){
		return ST3Lib.dataTable.moveToLastSelectId(tableSelector, id);
	}
	
	
	window.getNextSelectId = function(tableSelector, id, step, fnMatch){
		return ST3Lib.dataTable.getNextSelectId(tableSelector, id, step, fnMatch);
	}
	
	window.getSelectId = function(tableSelector, index){
		return ST3Lib.dataTable.getSelectId(tableSelector, index);
	}
	
	function addrow(tr) {
		//var currentuser = $('#right-area .userinfo').text();
		$('#afterSearch-command, #add-command').addClass('hide');
		$('#save-panel').removeClass('hide');
		tr.find('.rownum').html('');
		//tr.find('td:eq(9),td:eq(11)').html(currentuser);
		ST3Lib.content.disabled( '#search-field', true );
		ST3Lib.content.disabled( '#search-panel', true );
		$('#search-panel').addClass('hide');
	}
	window.addBatch =
	function addBatch(){
		//restoreSearchCriteria();
		//clearFilter();
		lastAction = 'add';
		$('#result .dataTables_empty').parent().remove();
		$('.dataTables_length').hide();
		$('#result-list-form').attr('action', actionFormUrl + '/add').attr('_method', 'put');
		roleBatchEditor.addRow();
		moveHScrollBar(0);
	};
	
	window.editBatch = 
	function editBatch() {
		//moveToLastSelectId('#result', lastSelectedId);
		var radio = $('#result').find('input:radio:checked');
		if (radio.length === 0) {
			$('#search-form').find('input[name=projectCode]').focus();
			ST3Lib.message.clear(true);
			ST3Lib.message.addError('<spring:message code="MSTD1017AERR"></spring:message>');
			ST3Lib.message.show(1);
			return;
		} 
		restoreSearchCriteria();
		lastAction = 'edit';
		var tr = radio.closest('tr');
		$('#result-list-form').attr('action', actionFormUrl + '/update').attr('_method', 'post');
		roleBatchEditor.editRow(tr.index());
		$('#result').find('input:radio:checked').removeAttr('disabled');
		$('.dataTables_length').hide();
		$('#afterSearch-command, #add-command').addClass('hide');
		$('#save-panel').removeClass('hide');
		tr.find('.rownum').html('');
		ST3Lib.content.disabled( '#search-field', true );
		ST3Lib.content.disabled( '#search-panel', true );
		$('#search-panel').addClass('hide');
		var valueCtrl = $('#result').find('input[name=value]').focus();
		valueCtrl.blur(); //fixed for ie6
		valueCtrl.focus();
		moveHScrollBar(0);
	};
	window.deleteBatch = 
	function deleteBatch(){
		ST3Lib.message.clear(true);
		//moveToLastSelectId('#result', lastSelectedId);
		var radio =$('#result').find('input:radio:checked');
		if (radio.length == 0) {
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
				var hidden = form.find('input:hidden[name=projectCode],input:hidden[name=batchId],input:hidden[name=batchName]')
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
	window.downloadBatch =
	function downloadBatch(){
		var loading = ST3Lib.dialog.loading('#WST33040Download');
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
	window.saveAddEditBatch = 
	function saveAddEditBatch(){
		ST3Lib.dialog.confirm('<spring:message code="MSTD0004ACFM"></spring:message>', 'MSTD0004ACFM', function(ret){
			if (!ret) {
				var projectCodeInput = $('#result-list-form input[name=projectCode]:visible');
				if (projectCodeInput.length)
					projectCodeInput.focus();
				else{
					$('#result-list-form input[name=value]').focus();
				}
				return;
			}
			ST3Lib.message.clear(true);
			if (lastAction === 'add') {
				template = $('#result-list-form').find('.template');
				lastSelectedId = template.find('input[name=projectCode]').val().toUpperCase() + '-' +
					template.find('input[name=batchId]').val().toUpperCase() + '-' +
					template.find('input[name=batchName]').val().toUpperCase() + '-' +
					template.find('select[name=priorityLevel]').val() + '-' +
					template.find('select[name=concurrency]').val();
				
				lastSearchCriteria.projectCode = template.find('input[name=projectCode]').val();
				lastSearchCriteria.batchId = template.find('input[name=batchId]').val();
				lastSearchCriteria.batchName = template.find('input[name=batchName]').val();
				lastSearchCriteria.batchName = template.find('select[name=priorityLevel]').val();
				lastSearchCriteria.batchName = template.find('select[name=concurrency]').val();
			}
			var saveForm = $('#result-list-form');
			saveForm.submit();
		});
	};
	window.cancelAddEditBatch =
	function cancelAddEditBatch(){
		ST3Lib.dialog.confirm('<spring:message code="MSTD0003ACFM"></spring:message>', 'MSTD0003ACFM', function(ret){
			if ( !ret ) {
				var projectCodeInput = $('#result-list-form input[name=projectCode]:visible');
				if (projectCodeInput.length)
					projectCodeInput.focus();
				else{
					$('#result-list-form input[name=value]').focus();
				}
				return;
			}
			roleBatchEditor.cancel();
			ST3Lib.message.clear(true);
			$('#save-panel').addClass('hide');
			$('.dataTables_length').show();
			if ( $('#result > tbody > tr').length === 0 ){
				$('#add-command').removeClass('hide');
				$('#result').dataTable().fnDraw();
			}else $('#afterSearch-command, #add-command').removeClass('hide');
			
			ST3Lib.content.disabled( '#search-field', false );
			ST3Lib.content.disabled( '#search-panel', false );
			$('#search-panel').removeClass('hide');
			if (lastAction === 'edit')
				moveToLastSelectId('#result', lastSelectedId);
			$('#search-form').find('input[name=projectCode]').focus();
			moveHScrollBar(0);
		});
	};
	window.searchFinish =
	function searchFinish(datas, loading){
		if (datas === null) return;
		ST3Lib.message.setAppendMode(false);
		
		var isSearchBtn = isSearchButtonClick;
		isSearchButtonClick = false;
		
		var dataTable = $( '#result' ).dataTable();
		dataTable.fnClearTable(false);//no redraw
		datas = datas.batchMasterList;
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
		setting.aoColumns[13].sClass = 'col14';
						
		var tempDiv = $('<div/>');
		dataTable.fnClearTable(false);//no redraw
		var i = 0;
		var arrayData = [];
		var processor = setInterval(function(){
		var processPerTick = 10;
		
		for(var j = 0; j < processPerTick; ++j) {
			var data = datas[i++];
			if ( data ) {
				var updateDate = new Date(+data.updateDate);
				var createDate = new Date(+data.createDate);
				var updateTime = zeroLead( updateDate.getHours(), 2) + ':' 
								+ zeroLead(updateDate.getMinutes(), 2) + ':' 
								+ zeroLead(updateDate.getSeconds(), 2);
				var createTime = zeroLead( createDate.getHours(), 2) + ':' 
								+ zeroLead(createDate.getMinutes(), 2) + ':' 
								+ zeroLead(createDate.getSeconds(), 2);
				var serialize = tempDiv.text(JSON.stringify(data)).html();
				arrayData.push([ 
									 '<input class="radio" type="radio" name="id" value="' + 
									 data.id.projectCode +
									 data.id.batchId +
									 	 '" /><div class="serialize" style="display:none">' + serialize + '</div>' 
								    , ''
								    , data.id.projectCode || ''
								    , data.id.batchId || ''
								    , data.batchName || ''
								    , __priorityValue[data.priorityLevel] || ''
								    , __concurrencyValue[data.concurrencyFlag] || ''
								    , data.runAs || ''
								    , data.shell || ''
								    , data.supportId || ''
								    , updateDate.format('dd/mm/yy ' + updateTime)
								    , data.updateBy || ''
								    , createDate.format('dd/mm/yy ' + createTime)
								    , data.createBy || ''
								]);
			}else{
				processPerTick = 0;
				clearInterval(processor);
				dataTable.fnAddData(arrayData);
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
				<td width="30%">
					<div><label class="label" for="projectCode" style="width: 92px"><span class="MandatoryFieldFont">*</span>
					<spring:message code="ST3.WST33040.Label.ProjectCode"/>:</label>
					<input class="MandatoryField" type="text" name="projectCode" style="text-transform:uppercase;width:275px;" maxlength="7" format-msg="code" ></div>
				</td>
				<td width="30%">
					<div><label class="label" for="batchId" style="width: 92px">
					<spring:message code="ST3.WST33040.Label.BatchID"/>:</label>
					<input class="textbox" type="text" name="batchId" style="text-transform:uppercase;width:275px;" maxlength="10" format-msg="code" ></div>
				</td>
				<td width="30%">
					<div><label class="label" for="batchName" style="width: 92px">
					<spring:message code="ST3.WST33040.Label.BatchName"/>:</label>
					<input class="textbox" type="text" name="batchName" style="width:275px;" maxlength="100" format-msg="bodycode" ></div>
				</td>
			</tr>
			<tr>
				<td>
					<div><label class="label" for="priorityLevel" style="width: 92px"><spring:message code="ST3.WST33040.Label.Priority"/>:</label>
						<select name="priorityLevel" style="width:275px;" first-item="All">
							<option value="">&lt;All&gt;</option>
						</select></div>
				</td>
				<td>
					<div><label class="label" for="concurrency" style="width: 92px"><spring:message code="ST3.WST33040.Label.Concurrency"/>:</label>
						<select name="concurrency" style="width:275px;" first-item="All">
							<option value="">&lt;All&gt;</option>
						</select>
					</div>
				</td>
				<td></td>
			</tr>
		</tbody></table>
	</div>
	<div class="button-panel" style="zoom:1;position:relative;">
		<div id="search-panel" style="float:left">
			<spring:message code="ST3.WST33040.Label.BtnSearch" var="WST33040Search" />
			<sc2:button functionId="ST3304" screenId="WST33040" buttonId="WST33040Search" 
				type="submit" value="${WST33040Search}"  style="width:80px;"
				styleClass="button"
			/>
			<spring:message code="ST3.WST33040.Label.BtnClear" var="WST33040Clear" />
			<sc2:button functionId="ST3304" screenId="WST33040" buttonId="WST33040Clear"
				type="reset" value="${WST33040Clear}"  style="width:80px;"
				styleClass="button" secured="false"
				onClick="clearForm();"
			/>
		</div>
		<div id="alloperation-command" style="float:right;text-align: right;white-space: nowrap;">
			<div id="add-command" style="display:inline;zoom:1;position:relative;">
			<spring:message code="ST3.WST33040.Label.BtnAdd" var="WST33040Add" />
				<sc2:button functionId="ST3304" screenId="WST33040" buttonId="WST33040Add"
					type="button" value="${WST33040Add}" 
					styleClass="button"
					style="width:80px;"
					onClick="addBatch();"
					/>
			</div>
			<div id="afterSearch-command" class="hide" style="display:inline;zoom:1;position:relative;">
				<spring:message code="ST3.WST33040.Label.BtnEdit" var="WST33040Edit" />
				<sc2:button functionId="ST3304" screenId="WST33040" buttonId="WST33040Edit" 
					type="button" value="${WST33040Edit}" 
					styleClass="button"
					style="width:80px;"
					onClick="editBatch();"
					/>
				<spring:message code="ST3.WST33040.Label.BtnDelete" var="WST33040Delete" />
				<sc2:button functionId="ST3304" screenId="WST33040" buttonId="WST33040Delete" 
					type="button" value="${WST33040Delete}" 
					styleClass="button"
					style="width:80px;"
					onClick="deleteBatch();"
					/>
				<spring:message code="ST3.WST33040.Label.BtnDownload" var="WST33040Download" />
				<sc2:button functionId="ST3304" screenId="WST33040" buttonId="WST33040Download" 
					type="button" value="${WST33040Download}" 
					styleClass="button"
					style="width:80px;"
					onClick="downloadBatch();"
					/>
			</div>
			<div id="save-panel" class="hide" style="display:inline;zoom:1;position:relative;">
				<spring:message code="ST3.WST33040.Label.BtnSave" var="WST33040Save" />
				<sc2:button functionId="ST3304" screenId="WST33040" buttonId="WST33040Save" 
					type="button" value="${WST33040Save}" 
					styleClass="button"
					style="width:80px;"
					onClick="saveAddEditBatch();"
					/>
				<spring:message code="ST3.WST33040.Label.BtnCancel" var="WST33040Cancel" />
				<sc2:button functionId="ST3304" screenId="WST33040" buttonId="WST33040Cancel" 
					type="button" value="${WST33040Cancel}" 
					styleClass="button" secured="false"
					style="width:80px;"
					onClick="cancelAddEditBatch();"
					/>
			</div>
		</div>

		
	</div>
	<div class="clear"></div>
	</form:form>
</div>
<hr>
<div id="search-result" class="overflow-hidden autoheight" style="margin-top: 5px;" >
	<div id="syslist" class="autoheight" >
		<form id="result-list-form" action="batchManagementList" method="post"  ajax="updateBatchFinish" 
			ajax-loading-target="#result" class="autoheight overflow-visible">
		    <input type="hidden" name="updateDate"  />
			<input type="hidden" name="projectCode" disabled="disabled" />
			<input type="hidden" name="batchId" disabled="disabled" />
			<input type="hidden" name="batchName" disabled="disabled" />
			<input type="hidden" name="priorityLevel" disabled="disabled" />
			<input type="hidden" name="concurrency" disabled="disabled" />
			
		    <spring:message code="ST3.WST33040.Label.ColNo" var="ColNo"/>
		    <spring:message code="ST3.WST33040.Label.ColProjectCode" var="ColProjectCode"/>
		    <spring:message code="ST3.WST33040.Label.ColBatchID" var="ColBatchID"/>
		    <spring:message code="ST3.WST33040.Label.ColBatchName" var="ColBatchName"/>
		    <spring:message code="ST3.WST33040.Label.ColPriority" var="ColPriority"/>
		    <spring:message code="ST3.WST33040.Label.ColConcurrency" var="ColConcurrency"/>
		    <spring:message code="ST3.WST33040.Label.ColOwner" var="ColOwner"/>
		    <spring:message code="ST3.WST33040.Label.ColShell" var="ColShell"/>
		    <spring:message code="ST3.WST33040.Label.ColSupportID" var="ColSupportID"/>
		    <spring:message code="ST3.WST33040.Label.ColUpdateDate" var="ColUpdateDate"/>
		    <spring:message code="ST3.WST33040.Label.ColUpdateBy" var="ColUpdateBy"/>
		    <spring:message code="ST3.WST33040.Label.ColCreateDate" var="ColCreateDate"/>
		    <spring:message code="ST3.WST33040.Label.ColCreateBy" var="ColCreateBy"/>
		    <datatables:table id="result" data="${syslist}" cdn="false" row="row" cssClass="result fixedheader fixedcolumn" 
		    	paginate="true" cssStyle="width:1890px;height:330px;"
				info="true" displayLength="10" paginationType="full_numbers" fixedPosition="col">
				<datatables:column title="" cssClass="col1" sortable="false" searchable="false" />
		        <datatables:column title="${ColNo}" cssClass="col2 rownum fixed" sortable="false" searchable="false" />
		        <datatables:column title="${ColProjectCode}" cssClass="col3" sortable="false"  sortInit="asc" />
				<datatables:column title="${ColBatchID}" cssClass="col4 systemcode" sortable="false" />
				<datatables:column title="${ColBatchName}" cssClass="col5" sortable="false" />
				<datatables:column title="${ColPriority}" cssClass="col6" sortable="false" />
				<datatables:column title="${ColConcurrency}" cssClass="col7" sortable="false" />
				<datatables:column title="${ColOwner}" cssClass="col8" sortable="false" />
				<datatables:column title="${ColShell}" cssClass="col9" sortable="false" />
				<datatables:column title="${ColSupportID}" cssClass="col10" sortable="false" />
				<datatables:column title="${ColUpdateDate}" cssClass="col11" sortable="false" searchable="false" />
				<datatables:column title="${ColUpdateBy}" cssClass="col12" sortable="false" searchable="false" />
				<datatables:column title="${ColCreateDate}" cssClass="col13" sortable="false" searchable="false" />
				<datatables:column title="${ColCreateBy}" cssClass="col14" sortable="false" searchable="false" />
			</datatables:table>
		</form>
		<table id="batchtemplate" class="template">
			<tr class="template add">
					<td class="col1"></td>
					<td class="col2 rownum"></td>
					<td class="col3"><input type="text" name="projectCode" class="MandatoryField" 
						style="width:100%;text-transform:uppercase;" title="Project Code" format-msg="code"  maxlength="3"/></td>
					<td class="col4 systemcode"><input type="text" name="batchId" class="MandatoryField" 
						style="width:100%;text-transform:uppercase;" title="Batch ID" format-msg="id" maxlength="10"/></td>
					<td class="col5"><input type="text" name="batchName" class="MandatoryField" 
						style="width:100%;" title="Batch Name" format-msg="bodycode" maxlength="100"/></td>
					<td class="col6">
						<select name="priorityLevel" class="MandatoryField" style="width:100%" title="Priority" first-item="<SELECT>">
							<option value="">&lt;SELECT&gt;</option>
						</select>
					</td>
					<td class="col7">
						<select name="concurrency" class="MandatoryField" style="width:100%" title="Concurrency" first-item="<SELECT>">
							<option value="">&lt;SELECT&gt;</option>
						</select>
					</td>
					<td class="col8"><input type="text" name="owner" class="MandatoryField" style="width:100%" maxlength="10" title="Owner" format-msg="code"/></td>
					<td class="col9"><input type="text" name="shell" class="MandatoryField" style="width:100%;" maxlength="255" title="Shell" format-msg="pathcode"/></td>
					<td class="col10"><input type="text" name="supportId" style="width:100%;" maxlength="10" title="Support ID"/></td>
					<td class="col11"></td>
					<td class="col12"></td>
					<td class="col13"></td>
					<td class="col14"></td>
			</tr>
			<tr class="template edit">
					<td class="col1">@data</td>
					<td class="col2 rownum">@data</td>
					<td class="col3">@data<input type="hidden" name="projectCode" value="@data"/></td>
					<td class="col4 systemcode">@data <input type="hidden" name="batchId" value="@data"> </td>
					<td class="col5"><input type="text" name="batchName" class="MandatoryField" style="width:100%;" title="Batch Name" format-msg="bodycode" maxlength="100" value="@data"/></td>
					<td class="col6">
						<select name="priorityLevel" _value="@data" class="MandatoryField" style="width:100%" title="Priority Level" first-item="<SELECT>">
							<option value="">&lt;SELECT&gt;</option>
						</select>
					</td>
					<td class="col7">
						<select name="concurrency" _value="@data" class="MandatoryField" style="width:100%" title="Concurrency" first-item="<SELECT>">
							<option value="">&lt;SELECT&gt;</option>
						</select>
					</td>
					<td class="col8"><input type="text" name="owner" class="MandatoryField" style="width:100%" maxlength="10" title="Owner" value="@data" format-msg="code"/></td>
					<td class="col9"><input type="text" name="shell" class="MandatoryField" style="width:100%;" maxlength="255" title="Shell" value="@data" format-msg="pathcode"/></td>
					<td class="col10"><input type="text" name="supportId" style="width:100%;" maxlength="10" title="Support ID" value="@data"/></td>
					<td class="col11">@data</td>
					<td class="col12">@data</td>
					<td class="col13">@data</td>
					<td class="col14">@data</td>
			</tr>
		</table>
		
	</div>
	<iframe id="downloadIframe" name="downloadIframe" frameborder="0" style="width:100px;height:100px;border:0px solid black;position:static;left:0px;display:none;" ></iframe>
</div>