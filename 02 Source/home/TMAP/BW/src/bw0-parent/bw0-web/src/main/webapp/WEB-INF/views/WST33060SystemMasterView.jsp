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
	.result .col3, .result .col4, .result .col5 {
		width: 300px;
	}
	.result .col6, .result .col7 {
		width: auto !important;
	}
	.result .col8 {
		width: 100px;
		text-align: center;
	}
	.result .col9, .result .col11 {
		width: 150px;
	}
	.result .col10, .result .col12 {
		width: 100px;
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
	var ActiveStatus = ['Active', 'Inactive'];
	var actionFormUrl = '';
	var __categoryValue = ${categoryJSON};
	
	ST3Lib.validate.format_msg['code'] = {
			regEx: /^([0-9a-zA-Z\-_:*]+\*?)?$/,
			ErrorCode: 'MSTD0043AERR'
		};
	ST3Lib.validate.format_msg['bodycode'] = {
			regEx: /^([0-9a-zA-Z\-_]+)?$/,
			ErrorCode: 'MSTD0043AERR'
		};
	ST3Lib.validate.format_msg['bodycode1'] = {
			regEx: /^([0-9a-zA-Z\-_:*]+)?$/,
			ErrorCode: 'MSTD0043AERR'
		};
	$(function(){
		actionFormUrl = $('#search-form').attr('action');
		window.roleSystemEditor = ST3Lib.roweditor( '#result', {
			template: "#systemtemplate",
			success: function( type, tr ){
				if ( tr.index() %2 > 0 ) tr.addClass( 'even' );
				else tr.addClass( 'odd' );
				if ( type === 'add' ) {
					addrow(tr);
					var categoryCtrl = $('#result').find('input[name=category]').focus();
					$('input[type=radio]').prop( 'disabled', true );
					categoryCtrl.focus();//fixed for ie6
					ST3Lib.message.clear();
					ST3Lib.message.hide(true);
					
				}else if (type === 'edit') {
					tr.find('input:radio').click();
					$('input[type=radio]').prop( 'disabled', true );
					ST3Lib.message.clear();
					ST3Lib.message.hide(true);
					var value = tr.find('select').attr('_value');
					for (var i = 0; i < ActiveStatus.length; ++i) {
						if ( ActiveStatus[i] === value ) {
							tr.find('select').val(tr.find('select').find('option:eq(' + i + ')').val());		
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
				$('#WST33060Search').click();	
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
			form.find('input:hidden[name=category]').val(obj.id.category);
			form.find('input:hidden[name=subCategory]').val(obj.id.subCategory);
			form.find('input:hidden[name=code]').val(obj.id.code);
		});
		searchSelectChange(null, true);
		$('#search-form').find('select[name=category]').focus();
	});
	
	function searchSelectChange(self, init){
		if ( init ) {
			$('#search-form select').off('change').change(function(){
				searchSelectChange(this);
			}).off('focus').focus(function(){
				if ( $(this).val() === '' && $(this).children().length <= 1 ){
					searchSelectChange(this);	
				}
			})
			.filter('[name=category]').each(function(i, e){
				searchSelectChange(this, false, $(this).closest('form'));
				$(this).prop('selectedIndex', 0);
			});
		}
		var selects = [ $('select[name=category]'),  $('select[name=subCategory]') ];
		var startIndex = 0;
		var tmpCategoryValue = __categoryValue;
		if ( self ) {
			for( startIndex = 0; startIndex < selects.length; ++startIndex ) {
				var val = selects[startIndex].val();
				if(val) tmpCategoryValue = tmpCategoryValue[val];
				else break;
				if (selects[startIndex][0] === self) {
					startIndex++;
					break;
				}
			}
		}
		
		if (startIndex > 0 && selects[startIndex-1].val() === '')
			return;

		for( var i = startIndex; i < selects.length; ++i ) {
			var label = selects[i].attr('first-item') || 'Select';
			selects[i].empty().append('<option value="">&lt;' + label + '&gt;</option>');
		}
		
		
		if ( startIndex < selects.length ) {
			var values = null;
			if (tmpCategoryValue.splice)
				values = tmpCategoryValue;
			else values = Object.keys(tmpCategoryValue).sort();
			for(var i = 0; i < values.length; ++i) {
				selects[startIndex].append('<option value="'+values[i]+'">' + values[i] + '</option>');
			}
		}
		
		
	}
	
	function moveHScrollBar(value){
		$('#search-result .dataTable-wrapper').scrollLeft(0);
	}
	
	function mergeCategory(categories){
		var categoryCtrl = $('#search-form').find('select[name=category]');
		var options = categoryCtrl.find('option');
		var dummy = $(document);
		
		for(var i = 0; i < categories.length; ++i) {
			var found = false;
			for	(var j = 1; j < options.length; ++j) {
				if (categories[i] === options[j].value) {
					found = true;
					break;
				}
			}
			if (found === false){
				categoryCtrl.append('<option value="'+ categories[i] +'">' + categories[i] + '</option>');
			}
		}
		
		options = categoryCtrl.find('option');
		for	(var j = 1; j < options.length; ++j) {
			var found = false;
			for(var i = 0; i < categories.length; ++i) {	
				if (categories[i] === options[j].value) {
					found = true;
					break;
				}
			}
			if (found === false){
				$(options[j]).prop('disabled', true).hide().remove();
				[].splice.call(options, j ,1);
				--j;
			}
		}
		
		var firstOption = categoryCtrl.find('option:eq(0)');
		options = categoryCtrl.find('option');
		var values = [];
		for(var i = 1; i < options.length; ++i) {
			values.push(options[i].value);
		}
		values.sort();
		for(var i = values.length - 1; i >= 0 ; --i){
			var opt = categoryCtrl.find('option[value="' + values[i] + '"]');
			opt.insertAfter(firstOption);
		}
	}
	
	var lastSearchCriteria = {}, isSearchButtonClick = false, lastAction = ''
		, lastSelectedId = null, tempLastSelectedId = null;
	function restoreSearchCriteria(){
		var searchForm = $('#search-form');
		searchForm.find('select[name=category]').val(lastSearchCriteria.category);
		searchForm.find('select[name=subCategory]').val(lastSearchCriteria.subCategory);
		searchForm.find('input[name=code]').val(lastSearchCriteria.code);	
	}
	function saveSearchCriteria(){
		var searchForm = $('#search-form');
		lastSearchCriteria.category = searchForm.find('select[name=category]').val();
		lastSearchCriteria.subCategory = searchForm.find('select[name=subCategory]').val();
		lastSearchCriteria.code = searchForm.find('input[name=code]').val();
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
	
	function prepare(){
		
	}
	
	window.clearFilter = 
	function clearFilter(selector){
		$((selector || '') + '.dataTable:not(.fixed-header)').each(function(){
			$(this).closest('.dataTables_wrapper').find('.dataTables_filter input').val('');
			$(this).dataTable().fnSettings().oPreviousSearch.sSearch = '';
			$(this).dataTable().fnDraw();
		})
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
	window.addSystem =
	function addSystem(){
		//restoreSearchCriteria();
		//clearFilter();
		lastAction = 'add';
		$('#result .dataTables_empty').parent().remove();
		$('.dataTables_length').hide();
		$('#result-list-form').attr('action', actionFormUrl + '/add').attr('_method', 'put');
		roleSystemEditor.addRow();
		moveHScrollBar(0);
	};
	
	window.editSystem = 
	function editSystem() {
		moveToLastSelectId('#result', lastSelectedId);
		var radio = $('#result').find('input:radio:checked');
		if (radio.length === 0) {
			$('#search-form').find('select[name=category]').focus();
			ST3Lib.message.clear(true);
			ST3Lib.message.addError('<spring:message code="MSTD1017AERR"></spring:message>');
			ST3Lib.message.show(1);
			return;
		} 
		restoreSearchCriteria();
		lastAction = 'edit';
		var tr = radio.closest('tr');
		$('#result-list-form').attr('action', actionFormUrl + '/update').attr('_method', 'post');
		roleSystemEditor.editRow(tr.index());
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
	window.deleteSystem = 
	function deleteSystem(){
		ST3Lib.message.clear(true);
		moveToLastSelectId('#result', lastSelectedId);
		var radio = $('#result').find('input:radio:checked');
		if (radio.length === 0) {
			$('#search-form').find('select[name=category]').focus();
			ST3Lib.message.clear(true);
			ST3Lib.message.addError('<spring:message code="MSTD1016AERR"></spring:message>');
			ST3Lib.message.show(1);
			$('#search-form').find('select[name=category]').focus();
			return;
		} 
		restoreSearchCriteria();
		ST3Lib.dialog.confirm('<spring:message code="MSTD0001ACFM"></spring:message>', 'MSTD0001ACFM', function(ret){
			if (ret) {
				lastAction = 'delete';
				lastSelectedId = tempLastSelectedId = getNextSelectId('#result', lastSelectedId);
				
				var form = $('#result-list-form');
				var hidden = form.find('input:hidden[name=category],input:hidden[name=subCategory],input:hidden[name=code]')
					.prop('disabled', false);
				try{
					form.attr('action', actionFormUrl + '/delete').attr('_method', 'delete').submit();
					hidden.prop('disabled', true);
				}catch(e){
					hidden.prop('disabled', true);
					throw e;
				}
				$('#search-form').find('select[name=category]').focus();
				return;
			}else{
				$('#search-form').find('select[name=category]').focus();	
			}
		});
			
	};
	window.downloadSystem =
	function downloadSystem(){
		var loading = ST3Lib.dialog.loading('#WST33060Download');
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
		$('#search-form').find('select[name=category]').focus();
		setTimeout(function(){
			loading.close();
			$('#search-form').find('select[name=category]').focus();
		}, 1500);
	};
	window.saveAddEditSystem = 
	function saveAddEditSystem(){
		ST3Lib.dialog.confirm('<spring:message code="MSTD0004ACFM"></spring:message>', 'MSTD0004ACFM', function(ret){
			if (!ret) {
				var categoryInput = $('#result-list-form input[name=category]:visible');
				if (categoryInput.length)
					categoryInput.focus();
				else{
					$('#result-list-form input[name=value]').focus();
				}
				return;
			}
			ST3Lib.message.clear(true);
			if (lastAction === 'add') {
				template = $('#result-list-form').find('.template');
				lastSelectedId = template.find('input[name=category]').val().toUpperCase() + '-' +
					template.find('input[name=subCategory]').val().toUpperCase() + '-' +
					template.find('input[name=code]').val().toUpperCase();
				
				lastSearchCriteria.category = template.find('input[name=category]').val();
				lastSearchCriteria.subCategory = template.find('input[name=subCategory]').val();
			}
			var saveForm = $('#result-list-form');
			saveForm.submit();
		});
	};
	window.cancelAddEditSystem =
	function cancelAddEditSystem(){
		ST3Lib.dialog.confirm('<spring:message code="MSTD0003ACFM"></spring:message>', 'MSTD0003ACFM', function(ret){
			if ( !ret ) {
				var categoryInput = $('#result-list-form input[name=category]:visible');
				if (categoryInput.length)
					categoryInput.focus();
				else{
					$('#result-list-form input[name=value]').focus();
				}
				return;
			}
			roleSystemEditor.cancel();
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
			$('#search-form').find('select[name=category]').focus();
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
		$('select[name=result_length]').val('10').change();
		dataTable.fnClearTable(false);//no redraw
		datas = datas.listCategory;
		if ( !datas || datas.length === 0 ){

			ST3Lib.message.clear(true);
			ST3Lib.message.addError('<spring:message code="MSTD0059AERR"></spring:message>');
			ST3Lib.message.show(1);
			dataTable.fnDraw(true);
			$('#afterSearch-command').addClass('hide');
			$('select[name=result_length]').val('10');
			return;	
			
		}else{
			$('#afterSearch-command').removeClass('hide');
			$('#add-command').removeClass('hide');
			$('select[name=result_length]').val('10');
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
				//$('select[name=region]').focus();
				var serialize = tempDiv.text(JSON.stringify(data)).html();
				arrayData.push([ 
									 '<input class="radio" type="radio" name="categoryId" value="' + 
									 	data.id.category + '-' +
									 	data.id.subCategory + '-' +
									 	data.id.code +
									 	 '" /><div class="serialize" style="display:none">' + serialize + '</div>' 
								    , ''
								    , data.id.category || ''
								    , data.id.subCategory || ''
								    , data.id.code || ''
								    , data.value || ''
								    , data.remark || ''
								    , data.status === 'Y'?'Active':'Inactive'
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
					$('#search-form').find('select[name=category]').focus();
				break;
			}
		}
		},1 );
	};	
	window.clearForm =
	function clearForm(){
		$('#afterSearch-command').addClass('hide');
		$('select[name=result_length]').val('10');
		$('.dataTables_filter input').val('');
		$( '#result' ).dataTable().fnClearTable();
		ST3Lib.message.clear(true);
		$('#search-form').find('select[name=category]').focus();
	};
	
	window.clearResult =
	function clearResult(){
		$('#afterSearch-command').addClass('hide');
		$( '#result' ).dataTable().fnClearTable();
	};
	
	window.updateSystemFinish =
	function updateSystemFinish(){
		if ( ST3Lib.message.countError() === 0 ) {
			$( 'input[type=radio]' ).prop( 'disabled', false );
			var templateTR = $('#result').find('.template');
			
			//lastSelectedId = tempLastSelectedId;
			roleSystemEditor.cancel();
			$('#save-panel').addClass('hide');
			if ( $('#result > tbody > tr').length === 0 ){
				$('#add-command').removeClass('hide');
				$('#result').dataTable().fnDraw();
				
				$('#afterSearch-command').addClass('hide');
				$('select[name=result_length]').val('10');
			}else $('#afterSearch-command, #add-command').removeClass('hide');
			$('.dataTables_length').show();
			
			ST3Lib.content.disabled( '#search-field', false );
			ST3Lib.content.disabled( '#search-panel', false );
			$('#search-panel').removeClass('hide');
			
			ST3Lib.ajax(actionFormUrl + '/listCategory', 'GET', 'isajax=1', function(data, status){
				__categoryValue = data;
				var searchForm = $('#search-form');
				var curCatValue = searchForm.find('select[name=category]').val();
				var curSubCatValue = searchForm.find('select[name=subCategory]').val();
				mergeCategory(Object.keys(data));
				var cateCtrl = searchForm.find('select[name=category]').change();
				var subCatCtrl = searchForm.find('select[name=subCategory]').val(curSubCatValue);
				if (cateCtrl.val() === null)
					cateCtrl.val('');
				if (subCatCtrl.val() === null)
					subCatCtrl.val('');
				saveSearchCriteria();
				
				
				if (cateCtrl.val())
					searchForm.submit();
				else{
					$('#result').dataTable().fnClearTable();
					if (curCatValue){ //prev has selected value but it was deleted
						$('#afterSearch-command').addClass('hide');
						$('select[name=result_length]').val('10');
						
						ST3Lib.message.addError('<spring:message code="MSTD0059AERR"></spring:message>');
						ST3Lib.message.show(1);
					}
				}
			});
			moveHScrollBar(0);
		}else{
			
			ST3Lib.message.setAppendMode(false);
			$('#result input:text:first').focus();
			
			if ( $('#result > tbody > tr').length === 0 ){
				$('#afterSearch-command').addClass('hide');
				$('select[name=result_length]').val('10');
			}
		}
	}
})(ST3Lib.$);

</script>
<div id="search-criteria" style="zoom:1;position:relative;">
	<form:form method="post" id="search-form" action="${_mappingPath}" ajax="searchFinish" 
		ajax-loading-target="#result_wrapper" validate-error="clearResult">
	<div id="search-field" style="zoom:1;position:relative;">
		<table width="100%"><tbody>
			<tr>
				<td width="50%">
					<div><label class="label" for="category"><span class="MandatoryFieldFont">*</span>
					<spring:message code="ST3.WST33060.Label.Category"/>:</label>
					<select name="category" class="MandatoryField" style="width:400px;">
							<option value="">&lt;Select&gt;</option>
						</select></div>
				</td>
				<td>
					<div><label class="label" for="subCategory"><spring:message code="ST3.WST33060.Label.SubCategory"/>:</label>
					<select name="subCategory" style="width:400px;" first-item="All">
							<option value="">&lt;All&gt;</option>
						</select></div>
				</td>
			</tr>
			<tr>
				<td>
					<div><label class="label" for="code"><spring:message code="ST3.WST33060.Label.Code"/>:</label>
						<input class="textbox" type="text" name="code" style="text-transform:uppercase;width:400px;" maxlength="40" format-msg="code" >
					</div>
				</td>
			</tr>
		</tbody></table>
	</div>
	<div class="button-panel" style="zoom:1;position:relative;">
		<div id="search-panel" style="float:left">
			<spring:message code="ST3.WST33060.Label.BtnSearch" var="WST33060Search" />
			<sc2:button functionId="ST3306" screenId="WST33060" buttonId="WST33060Search" 
				type="submit" value="${WST33060Search}"  style="width:80px;"
				styleClass="button"
			/>
			<spring:message code="ST3.WST33060.Label.BtnClear" var="WST33060Clear" />
			<sc2:button functionId="ST3306" screenId="WST33060" buttonId="WST33060Clear"
				type="reset" value="${WST33060Clear}"  style="width:80px;"
				styleClass="button" secured="false"
				onClick="clearForm();"
			/>
		</div>
		<div id="alloperation-command" style="float:right;text-align: right;white-space: nowrap;">
			<div id="add-command" style="display:inline;zoom:1;position:relative;">
			<spring:message code="ST3.WST33060.Label.BtnAdd" var="WST33060Add" />
				<sc2:button functionId="ST3306" screenId="WST33060" buttonId="WST33060Add"
					type="button" value="${WST33060Add}" 
					styleClass="button"
					style="width:80px;"
					onClick="addSystem();"
					/>
			</div>
			<div id="afterSearch-command" class="hide" style="display:inline;zoom:1;position:relative;">
				<spring:message code="ST3.WST33060.Label.BtnEdit" var="WST33060Edit" />
				<sc2:button functionId="ST3306" screenId="WST33060" buttonId="WST33060Edit" 
					type="button" value="${WST33060Edit}" 
					styleClass="button"
					style="width:80px;"
					onClick="editSystem();"
					/>
				<spring:message code="ST3.WST33060.Label.BtnDelete" var="WST33060Delete" />
				<sc2:button functionId="ST3306" screenId="WST33060" buttonId="WST33060Delete" 
					type="button" value="${WST33060Delete}" 
					styleClass="button"
					style="width:80px;"
					onClick="deleteSystem();"
					/>
				<spring:message code="ST3.WST33060.Label.BtnDownload" var="WST33060Download" />
				<sc2:button functionId="ST3306" screenId="WST33060" buttonId="WST33060Download" 
					type="button" value="${WST33060Download}" 
					styleClass="button"
					style="width:80px;"
					onClick="downloadSystem();"
					/>
			</div>
			<div id="save-panel" class="hide" style="display:inline;zoom:1;position:relative;">
				<spring:message code="ST3.WST33060.Label.BtnSave" var="WST33060Save" />
				<sc2:button functionId="ST3306" screenId="WST33060" buttonId="WST33060Save" 
					type="button" value="${WST33060Save}" 
					styleClass="button"
					style="width:80px;"
					onClick="saveAddEditSystem();"
					/>
				<spring:message code="ST3.WST33060.Label.BtnCancel" var="WST33060Cancel" />
				<sc2:button functionId="ST3306" screenId="WST33060" buttonId="WST33060Cancel" 
					type="button" value="${WST33060Cancel}" 
					styleClass="button" secured="false"
					style="width:80px;"
					onClick="cancelAddEditSystem();"
					/>
			</div>
		</div>

		
	</div>
	<div class="clear"></div>
	</form:form>
</div>
<hr>
<div id="search-result" class="overflow-hidden autoheight" style="margin-top: 5px; height: 150px !important;" >
	<div id="syslist" class="autoheight" >
		<form id="result-list-form" action="systemManagementList" method="post"  ajax="updateSystemFinish" 
			ajax-loading-target="#result" class="autoheight overflow-visible">
		    <input type="hidden" name="updateDate"  />
			<input type="hidden" name="category" disabled="disabled" />
			<input type="hidden" name="subCategory" disabled="disabled" />
			<input type="hidden" name="code" disabled="disabled" />
			
		    <spring:message code="ST3.WST33060.Label.ColNo" var="ColNo"/>
		    <spring:message code="ST3.WST33060.Label.ColCategory" var="ColCategory"/>
		    <spring:message code="ST3.WST33060.Label.ColSubCategory" var="ColSubCategory"/>
		    <spring:message code="ST3.WST33060.Label.ColCode" var="ColCode"/>
		    <spring:message code="ST3.WST33060.Label.ColValue" var="ColValue"/>
		    <spring:message code="ST3.WST33060.Label.ColRemark" var="ColRemark"/>
		    <spring:message code="ST3.WST33060.Label.ColStatus" var="ColStatus"/>
		    <spring:message code="ST3.WST33060.Label.ColUpdateDate" var="ColUpdateDate"/>
		    <spring:message code="ST3.WST33060.Label.ColUpdateBy" var="ColUpdateBy"/>
		    <spring:message code="ST3.WST33060.Label.ColCreateDate" var="ColCreateDate"/>
		    <spring:message code="ST3.WST33060.Label.ColCreateBy" var="ColCreateBy"/>
		    <datatables:table id="result" data="${syslist}" cdn="false" row="row" cssClass="result fixedheader fixedcolumn" 
		    	paginate="true" cssStyle="width:2200px;height:330px;"
				info="true" displayLength="10" paginationType="full_numbers" fixedPosition="col">
				<datatables:column title="" cssClass="col1" sortable="false" searchable="false" />
		        <datatables:column title="${ColNo}" cssClass="col2 rownum fixed" sortable="false" searchable="false" />
				<datatables:column title="${ColCategory}" cssClass="col3" sortable="false"  sortInit="asc" /> <!-- sortInit="asc" -->
				<datatables:column title="${ColSubCategory}" cssClass="col4 systemcode" sortable="false" />
				<datatables:column title="${ColCode}" cssClass="col5" sortable="false" />
				<datatables:column title="${ColValue}" cssClass="col6" sortable="false" />
				<datatables:column title="${ColRemark}" cssClass="col7" sortable="false" searchable="false" />
				<datatables:column title="${ColStatus}" cssClass="col8" sortable="false" />
				<datatables:column title="${ColUpdateDate}" cssClass="col9" sortable="false" />
				<datatables:column title="${ColUpdateBy}" cssClass="col10" sortable="false"  />
				<datatables:column title="${ColCreateDate}" cssClass="col11" sortable="false"  />
				<datatables:column title="${ColCreateBy}" cssClass="col12" sortable="false"  />
			</datatables:table>
		</form>
		<table id="systemtemplate" class="template">
			<tr class="template add">
					<td class="col1"></td>
					<td class="col2 rownum"></td>
					<td class="col3"><input type="text" name="category" class="MandatoryField" 
						style="width:100%;text-transform:uppercase;" title="Category" format-msg="bodycode"  maxlength="40"/></td>
					<td class="col4 systemcode"><input type="text" name="subCategory" class="MandatoryField" 
						style="width:100%;text-transform:uppercase;" title="SubCategory" format-msg="bodycode" maxlength="40"/></td>
					<td class="col5"><input type="text" name="code" class="MandatoryField" 
						style="width:100%;text-transform:uppercase;" title="Code" format-msg="bodycode"  maxlength="40"/></td>
					<td class="col6"><input type="text" name="value" class="MandatoryField" style="width:100%" maxlength="200" title="Value"/></td>
					<td class="col7"><input type="text" name="remark" style="width:100%;" maxlength="200" title="Remark"/></td>
					<td class="col8">
						<select name="status" style="width:100%" title="Status">
							<option value="Y" selected>Active</option>
							<option value="N">Inactive</option>
						</select>
					</td>
					<td class="col9"></td>
					<td class="col10"></td>
					<td class="col11"></td>
					<td class="col12"></td>
			</tr>
			<tr class="template edit">
					<td class="col1">@data</td>
					<td class="col2 rownum">@data</td>
					<td class="col3">@data<input type="hidden" name="category" value="@data"/></td>
					<td class="col4 systemcode">@data<input type="hidden" name="subCategory" value="@data"/></td>
					<td class="col5">@data<input type="hidden" name="code" value="@data"/></td>
					<td class="col6"><input type="text" name="value" class="MandatoryField" style="width:100%" maxlength="200" title="Value" value="@data"/></td>
					<td class="col7"><input type="text" name="remark" style="width:100%;" maxlength="200" title="Remark" value="@data"/></td>
					<td class="col8">
						<select name="status" _value="@data" style="width:100%" title="Status">
							<option value="Y" selected>Active</option>
							<option value="N">Inactive</option>
						</select>
					</td>
					<td class="col9">@data</td>
					<td class="col10">@data</td>
					<td class="col11">@data</td>
					<td class="col12">@data</td>
			</tr>
		</table>
		
	</div>
	<iframe id="downloadIframe" name="downloadIframe" frameborder="0" style="width:100px;height:100px;border:0px solid black;position:static;left:0px;display:none;" ></iframe>
</div>