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
<style type="text/css">

	#search-result {
		/*display: none;*/
	}
	
	.result .col1 {
		width: 40px;
		text-align: center;
	}
	.result .col2 {
		width: 40px;
	}
	.result .col3, .result .col4  {
		width: 150px;
	}
	.result .col5, .result .col6 {
		width: 130px;
	}
	.result .col7{
		width: 60px;
	}
	.result .col8  {
		width: 80px;
		text-align: center;
	}
	.result .col9 {
		width: auto !important;
	}
	
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
</style>
<script language="javascript" type="text/javascript">
(function($){
	var isSearchButtonClick = false;
	var lastSearchCriteria = {};
	var lastSearchCriteriaDetail = {};
	var actionFormUrl = '';
	var __moduleList = ${modules};
	var pageType = 'list';
	var selectedObj = null;
	window.lastAction = '';
	var isSearchStart = false;
	
	ST3Lib.validate.format_msg['date'] = {
			regEx: /^\d{2}\/\d{2}\/\d{4}$/,
			ErrorCode: 'MSTD0043AERR'
		};
	ST3Lib.validate.format_msg['userId'] = {
			regEx: /^[0-9a-zA-Z\.]*\*?$/,
			ErrorCode: 'MSTD0043AERR'
		};
	
	//window.ready
	$(function(){
		$('#ui-accordion-menu-header-3').click();
		$('#ui-accordion-menu-header-3').attr('tabindex','-1');
		
		actionFormUrl = $('#search-form').attr('action');
		$('#result-list-form').on('keypress' ,'select,input:submit,input:button,input:reset,input:radio,input:checkbox,button',function(e){
			if (e.keyCode === 13 || e.keyCode === 10)
				return false;
		});
		$('#search-form').submit(clickSearch)
			.on('keypress', 'select,input:not(input:submit,input:button,input:reset,button)', function(e){
				if (e.keyCode === 13 || e.keyCode === 10) {
					ST3Lib.message.clear(true);
					$('#WST33010Search').click();	
					e.preventDefault();
				}
			});
		execLayoutState('init');
		addModuleList(__moduleList);
		
		$('#dateFrom, #dateTo').datepicker({
			showOn: "button",
			buttonImage: calendarImgPath,
			buttonImageOnly: true,
			buttonText: "Select date",
			dateFormat: 'dd/mm/yy',
			onSelect: function(){
				$(this).focus();	
			}
		});
		
		validateDate(document.getElementById('dateFrom'));
		validateDate(document.getElementById('dateTo'));
		
/* 		$('.ui-datepicker-trigger').wrap('<a class="fake-link" href="javascript:void();" />');
		$('.fake-link').off().on('click', function(){
			$(this).find('img').click();	
		}); */
		$('#module').change(function(){
			changeModule(__moduleList, this.value);
			var functionParam = '${form.function}';
			if(functionParam){
				$('#function').val(functionParam);
			}
		});
		
		$('#result').on('click', 'a.detail', function(){
			var obj = JSON.parse($(this).siblings('div').html());
			// START - pagination
// 			goToDetail(obj);
			viewDetail(obj);
			// END - pagination
			return false;
		});
		
		//begin support case call from hyperlink of other screen
		var flagCallFromHyperlink = ${callFromHyperlink};//get('searchOnLoad');
		if(flagCallFromHyperlink){
			var moduleParam = '${form.module}';
			if(moduleParam){
				$('#module').val(moduleParam);
				$('#module').trigger('change');
			}
			var appIdParam = '${form.appId}';
			if(appIdParam){
				$('#appId').val(appIdParam);
			}
		}
		//end support case call from hyperlink of other screen
	});
	
	var layout_state = {
			'init': { 
				  hide: ['#common-command', '#log-detail-panel']
				, show: ['#loglist']
				, focus: '#module'
			},
			'search-data-not-found': { 
				  hide: ['#common-command', '#log-detail-panel']
				, show: ['#loglist' ]
				, focus: '#module'
			},
			'search-data-found': { 
				  hide: ['#common-command', '#log-detail-panel']
				, show: ['#loglist']
				, focus: '#module'
			},
			'detail': {
				  hide: ['#loglist', '#logStatus']
				, show: ['#log-detail-panel', '#common-command', '#processStatus', '#_logStatus-panel']
				, screenid: 'WST33011: <spring:message code="ST3.WST33011.Description"/>'
				, disabled: ['#logStatus']
				, enabled: ['#messageType', '#processStatus']
				, focus: '#module'
			},
			'back-to-list': {
				  hide: ['#common-command', '#log-detail-panel', '#processStatus', '#_logStatus-panel']
				, show: ['#loglist', '#logStatus']
				, screenid: 'WST33010: <spring:message code="ST3.WST33010.Description"/>'
				, disabled: ['#messageType', '#processStatus']
				, enabled: ['#logStatus']
				, focus: '#module'
			}
	};
	
	function execLayoutState(name){
		ST3Lib.content.execLayoutState(layout_state, name);	
	}
	
	window.searchValidateError =
	function searchValidateError(){
		return;
		 $('#result').dataTable().fnClearTable();
		 $('#logDetailTable').dataTable().fnClearTable();
	};

	
	function addModuleList(modules){
		var $module = $('#module');
		$module.empty();
		if (!modules) return;
		var requiredModule = $module.hasClass('MandatoryField'); 
		if (requiredModule) {
			$module.append('<option value="">&lt;Select&gt;</option>');
		} else {
			$module.append('<option value="">&lt;All&gt;</option>');
		}
		for(var i = 0; i < modules.length; ++i) {
			var module = modules[i];
			$module.append('<option value="{0}">{1}-{2}</option>'.format(module.moduleId, module.moduleId.toUpperCase(), module.moduleName));
		}
	}
	
	function changeModule(modules, moduleId){
		var $function = $('#function');
		$function.empty();
		$function.append('<option value="">&lt;All&gt;</option>');
		var fns = null;
		for(var i = 0; i < modules.length; ++i) {
			if (modules[i].moduleId === moduleId) {
				fns = modules[i].details;
				break;
			}
		}
		
		if (fns == null) return;
		fns.sort(function(f1, f2){
			if (f1.id.functionId === f2.id.functionId)
				return 0;
			return f1.id.functionId < f2.id.functionId? -1:1;
		});
		for(var i = 0; i < fns.length; ++i) {
			var fn = fns[i];
			$function.append('<option value="{0}">{0}-{1}</option>'.format(fn.id.functionId, fn.functionName));
		}
	}
	
	window.clearFilter = 
	function clearFilter(selector){
		return;
		$((selector || '') + '.dataTable:not(.fixed-header)').each(function(){
			$(this).closest('.dataTables_wrapper').find('.dataTables_filter input').val('');
			$(this).dataTable().fnSettings().oPreviousSearch.sSearch = '';
			$(this).dataTable().fnDraw();
		})
	};
	
	window.isSearchButtonClick = false;
	function clickSearch(ev){
		var _invokeBySubmitForm = this._invokeBySubmitForm;
// 		prepareDataTable();
		isSearchButtonClick = !!ev.originalEvent; //is trigger by user  or not (convert to boolean)
		if ((lastAction === 'download') && (!isSearchButtonClick))
			return;
		var dataTable;
		if (isSearchButtonClick) {
			isSearchStart = true;
			var obj = null;
			lastAction = 'search';
// 			lastAction = 'submit';
			ST3Lib.message.clear(true);
			if (pageType === 'list') {
				dataTable = $('#result').dataTable();
				clearFilter('#result');
				
				obj = lastSearchCriteria;
				$('#common-command').addClass('hide');
				$( '#result' ).dataTable().fnClearTable(false);
			}else{
				dataTable = $('#logDetailTable').dataTable();
				clearFilter('#logDetailTable');
				obj = lastSearchCriteriaDetail;
				$( '#logDetailTable' ).dataTable().fnClearTable(false);
				
			}
			
// 			var dataTable = $('#result').dataTable();
			var oSettings = dataTable.fnSettings();
			
			var searchForm = $('#search-form');
			
			searchForm.find('input[name=firstResult]').val(0);
			searchForm.find('input[name=rowsPerPage]').val(oSettings._iDisplayLength || 10);
			
			obj.module = searchForm.find('select[name=module]').val();
			obj['function'] = searchForm.find('select[name=function]').val();
			obj.logStatus = searchForm.find('select[name=logStatus]:enabled').val();
			obj.userId = searchForm.find('input[name=userId]').val();
			obj.appId = searchForm.find('input[name=appId]').val();
			obj.dateFrom = searchForm.find('input[name=dateFrom]').val();
			obj.dateTo = searchForm.find('input[name=dateTo]').val();
			obj.logDetail = searchForm.find('input[name=logDetail]').val();
			obj.messageType = searchForm.find('select[name=messageType]').val();
			
			dataTable.fnDraw(true); //trigger function 'serverData'
			return false;
			
		} else {
			if (pageType === 'list') {
				dataTable = $('#result').dataTable();
			} else {
				dataTable = $('#logDetailTable').dataTable();
			}
			var searchForm = $('#search-form');
// 			var dataTable = $('#result').dataTable();
			var oSettings = dataTable.fnSettings();
			searchForm.find('input[name=rowsPerPage]').val(oSettings._iDisplayLength || 10);
			
			if (_invokeBySubmitForm == false) {
				searchForm.find('input[name=firstResult]').val(oSettings._iDisplayStart);
				dataTable._fnDraw(); //redraw without set _iDisplayStart to 0
				return false;
			}
		}
	}
	
	window.restoreSearchCriteria =
	function restoreSearchCriteria(){
		var obj = null;
		if (pageType === 'list') {
			obj = lastSearchCriteria;
		}else{
			obj = lastSearchCriteriaDetail;
		}
		
		var searchForm = $('#search-form');
		try{
			searchForm.find('select[name=module]').val(obj.module).change();
		}catch(e){}
		try{
			searchForm.find('select[name=function]').val(obj['function']).change();
		}catch(e){}
		searchForm.find('select[name=logStatus]:enabled').val(obj.logStatus);
		searchForm.find('input[name=userId]').val(obj.userId);
		searchForm.find('input[name=appId]').val(obj.appId);
		searchForm.find('input[name=dateFrom]').val(obj.dateFrom);
		searchForm.find('input[name=dateTo]').val(obj.dateTo);
		searchForm.find('input[name=logDetail]').val(obj.logDetail).attr('default', obj.logDetail || '0');
		searchForm.find('select[name=messageType]').val(obj.messageType);
		
		if (pageType === 'detail' && selectedObj) {
			var $module = searchForm.find('select[name=module]');
			if ($module.val()  === null){
				$module.append('<option value="{0}">{1}</option>'.format(selectedObj.moduleId
						, (selectedObj.moduleId || '') + '-' + (selectedObj.moduleName || '')) );
				__moduleList.push({
					details: [],
					moduleId: selectedObj.moduleId,
					moduleName: selectedObj.moduleName
				});
			}
			
			var $function = searchForm.find('select[name=function]');
			if ($function.val()  === null){
				$function.append('<option value="{0}">{1}</option>'.format(selectedObj.functionId
						, (selectedObj.functionId || '') + '-' + (selectedObj.functionName || '')) );
				for(var i = 0; i < __moduleList.length; ++i) {
					var m = __moduleList[i];
					if (m.moduleId === selectedObj.moduleId) {
						m.details.push({
							id: { functionId:  selectedObj.functionId },
							functionName: selectedObj.functionName 
						});
						break;	
					}
				}
			}
			try{ //catch error for IE6
				searchForm.find('select[name=module]').val(obj.module).change();
			}catch(e){}
			try{ //catch error for IE6
				searchForm.find('select[name=function]').val(obj['function']).change();
			}catch(e){}
			
		}
	};
	
	function copyCriteria(from, to){
		for(var k in from)
			to[k] = from[k];
	}
	
	window.search = 
	function search(datas, loading){
		if (pageType === 'list') {
			searchLog(datas, loading);
		}else{
// 			searchDetailFinish(datas, loading);
			processAjaxDetailData(datas, loading);
		}
	};
	
	window.clearSearch =
	function clearSearch(){
		lastAction = 'clear';
		var form = $('#search-form');
		if (pageType === 'list') {
			clearFilter('#result');
			$('#result').dataTable().fnClearTable();
			$('#common-command').addClass('hide');
			form.find('select[name=module]').focus();
			form.find('input[name=logDetail]').attr('default', '0');
		}else{
			clearFilter('#logDetailTable');
			$('#logDetailTable').dataTable().fnClearTable();
			$('#common-command').removeClass('hide');
			form.find('select[name=module]').focus();
			form.find('input[name=logDetail]').attr('default', '1');
			
			$('#download-option').find('input:checkbox').prop('checked', false);
			$('#noAppId').click();
			$('#noModule').click();
			$('#noFunction').click();
		}
	};
	
	window.searchLog = 
	function searchLog(datas, loading) {
		if (datas === null) return;
		ST3Lib.message.setAppendMode(false);
		isSearchButtonClick = false;
		var dataTable = $( '#result' ).dataTable();
		dataTable.fnClearTable(false);//no redraw
		$('#common-command').removeClass('hide');
		//datas = datas.logs;
		datas = datas.groupedLogs;
		if ( !datas || datas.length === 0 ){
			dataTable.fnDraw(true);
			$('#result').dataTable().fnClearTable();
			execLayoutState('search-data-not-found');
			$('#module').focus();
			return;	
		}else{
			execLayoutState('search-data-found');
		}
		if ( loading ) {
			loading.autoClose = false;
			loading.resize();
		}
		
		//var data = $.parseJSON( data );
		var setting = dataTable.fnSettings();
		setting.aoColumns[0].sClass = 'col1 rownum';
		setting.aoColumns[1].sClass = 'col2 appid';
		setting.aoColumns[2].sClass = 'col3';
		setting.aoColumns[3].sClass = 'col4';
		setting.aoColumns[4].sClass = 'col5';
		setting.aoColumns[5].sClass = 'col6';
		setting.aoColumns[6].sClass = 'col7';
		setting.aoColumns[7].sClass = 'col8';
		setting.aoColumns[8].sClass = 'col9';
		
		var tempDiv = $('<div/>');
		dataTable.fnClearTable(false);//no redraw
		var i = 0;
		var arrayData = [];
		var group = {};
		var groupList = datas;
		/* var processor = setInterval(function(){
			var processPerTick = 10;
			for(var j  = 0; j < processPerTick; ++j) {
				var data = datas[i++];
				if ( data ) {
					var appId = data.appId;
					if (group[appId] == null) {
						group[appId] = [];
						groupList.push(group[appId]);
					}
					group[appId].push(data);
				}else{
					processPerTick = 0;
					clearInterval(processor);
					addToList();
					break;
				}
			}
		},1  ); */
		
		addToList();
		
		function addToList() {
			var i = 0;
			var processor = setInterval(function(){
				var processPerTick = 10;
				for(var j  = 0; j < processPerTick; ++j) {
					var datas = groupList[i++];
					if ( datas && datas.length) {
						datas.sort(function(obj1, obj2){
							if (obj1.seqNo === obj2.seqNo)
								return 0;
							return obj1.seqNo < obj2.seqNo?-1: 1;
						});
						var data = datas[0];
						var lastIndex = datas.length - 1;
						
						var startDate = new Date(data.createDate);
						var startTime = zeroLead( startDate.getHours(), 2) + ':' 
										+ zeroLead(startDate.getMinutes(), 2) + ':' 
										+ zeroLead(startDate.getSeconds(), 2);
						var endDate = new Date(datas[lastIndex].createDate);
						var endTime = zeroLead( endDate.getHours(), 2) + ':' 
										+ zeroLead(endDate.getMinutes(), 2) + ':' 
										+ zeroLead(endDate.getSeconds(), 2);
						data.endDate = datas[lastIndex].createDate;
						data.translateStatus = datas[lastIndex].translateStatus;
						data.messageCode = datas[lastIndex].messageCode;
						data.message = datas[lastIndex].message;
						var serialize = tempDiv.text(JSON.stringify(data)).html();
						
						arrayData.push([
						    ''
						    , data.appId || ''
						    , (data.moduleId || '') + '-' + (data.moduleName || '')
						    , (data.functionId || '') + '-' +  (data.functionName || '')
						    , startDate.format('dd/mm/yy ' + startTime)
		                    , endDate.format('dd/mm/yy ' + endTime)
		                    , data.createBy || ''
		                    , '<a href="#" class="detail">{0}</a><div style="display:none;">{1}</div>'.format(data.translateStatus || '', serialize)
		                    , data.message || ''
		                    ]);
					}else{
						processPerTick = 0;
						clearInterval(processor);
						dataTable.fnAddData(arrayData);
						loading.close();
						$('#module').focus();
						break;
					}
				}
			},1  );
		}//addToList
	};
	
	window.download =
	function download(){
		ST3Lib.message.clear();
		var loading = ST3Lib.dialog.loading('#WST33010Download');
		
		var searchForm = $('#search-form');
		var downloadOption = $('#download-option');
		var ajax = searchForm.attr('ajax');
		var action = searchForm.attr('action');
        lastAction = 'download';
		searchForm.find('input[name=noAppId]')
			.val(downloadOption.find('input[name=noAppId]').prop('checked')?'1':'');
		searchForm.find('input[name=noModule]')
			.val(downloadOption.find('input[name=noModule]').prop('checked')?'1':'');
		searchForm.find('input[name=noFunction]')
			.val(downloadOption.find('input[name=noFunction]').prop('checked')?'1':'');
		searchForm.find('input[name=noStatus]')
			.val(downloadOption.find('input[name=noStatus]').prop('checked')?'1':'');
		searchForm.find('input[name=noLevel]')
			.val(downloadOption.find('input[name=noLevel]').prop('checked')?'1':'');
		searchForm.find('input[name=noUserId]')
			.val(downloadOption.find('input[name=noUserId]').prop('checked')?'1':'');
		
		$('#downloadIframe').off('load').on('load', function(){
			try{
				var text = $('#downloadIframe').contents().text();//$(window.downloadIframe.document.body).text();
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
		
		//check data on iframe to get error message
		searchForm.attr('action', actionFormUrl + '/../download').attr('target', 'downloadIframe')
			.removeAttr('ajax')
			.submit();
		searchForm.attr('ajax', ajax).removeAttr('target').attr('action', action);
		$('#module').focus();
		setTimeout(function(){
			loading.close();
		}, 1500);
	};
	
	window.goToDetail = 
	function goToDetail(rowData) {
		restoreSearchCriteria();
		if (rowData == null) return;
		var startDate = new Date(rowData.createDate);
		var endDate = new Date(rowData.endDate);
		var objs = [
		           	 {name: 'module', value: rowData.moduleId}
		           , {name: 'function', value: rowData.functionId}
		           , {name: 'userId', value: rowData.createBy}
		           , {name: 'appId', value: rowData.appId}
		           , {name: 'logStatus', value: ''}
		           , {name: 'messageType', value: ''}
		           , {name: 'dateFrom', value: startDate.format('dd/mm/yy')}
		           , {name: 'dateTo', value: endDate.format('dd/mm/yy')}
		           , {name: 'logDetail', value: 1}
		           , {name:'isajax', value: '1'}
		           , {name:'rnd', value: Math.random()}
		];
		var param = $.param(objs);
		var loading = ST3Lib.dialog.loading('#result_wrapper');
		selectedObj = rowData;
		pageType = 'detail';
		ST3Lib.ajax(actionFormUrl, 'POST', param, function(data, status){
			if (data) {
				searchDetailFinish(data, loading);
			}
			
			$('#download-option').find('input:checkbox').prop('checked', false);
			$('#noAppId').click();
			$('#noModule').click();
			$('#noFunction').click();
			
			execLayoutState('detail');
			loading.changeTarget('#logDetailTable_wrapper');
			$('#processStatus').val('');
			
			copyCriteria(lastSearchCriteria, lastSearchCriteriaDetail);
			lastSearchCriteriaDetail.module = rowData.moduleId;
			lastSearchCriteriaDetail['function'] = rowData.functionId;
			lastSearchCriteriaDetail.logStatus = "";
			lastSearchCriteriaDetail.userId = rowData.createBy;
			lastSearchCriteriaDetail.appId = rowData.appId;
			lastSearchCriteriaDetail.dateFrom = startDate.format('dd/mm/yy');
			lastSearchCriteriaDetail.dateTo = endDate.format('dd/mm/yy');
			lastSearchCriteriaDetail.logDetail = "1";
			lastSearchCriteriaDetail.messageType = '';
			restoreSearchCriteria();
			
			$('#search-form').attr('ajax-loading-target', "#logDetailTable_wrapper");
		});
	};
	
	window.viewDetail = 
	function viewDetail(rowData) {
		var obj = null;
		lastAction = 'submit';

		clearFilter('#logDetailTable');
		obj = lastSearchCriteriaDetail;
		$( '#logDetailTable' ).dataTable().fnClearTable(false);
		
		var dataTable = $('#logDetailTable').dataTable();
		var oSettings = dataTable.fnSettings();
		
		var searchForm = $('#search-form');
		
		searchForm.find('input[name=firstResult]').val(0);
		searchForm.find('input[name=rowsPerPage]').val(oSettings._iDisplayLength || 10);
		
		obj.module = searchForm.find('select[name=module]').val();
		obj['function'] = searchForm.find('select[name=function]').val();
		obj.logStatus = searchForm.find('select[name=logStatus]:enabled').val();
		obj.userId = searchForm.find('input[name=userId]').val();
		obj.appId = searchForm.find('input[name=appId]').val();
		obj.dateFrom = searchForm.find('input[name=dateFrom]').val();
		obj.dateTo = searchForm.find('input[name=dateTo]').val();
		obj.logDetail = searchForm.find('input[name=logDetail]').val();
		obj.messageType = searchForm.find('select[name=messageType]').val();
		
// 		var loading = ST3Lib.dialog.loading('#result_wrapper');
		pageType = 'detail';
		
		// ---
		
		execLayoutState('detail');
// 		loading.changeTarget('#logDetailTable_wrapper');
		$('#processStatus').val('');
		
		var startDate = new Date(rowData.createDate);
		var endDate = new Date(rowData.endDate);
		copyCriteria(lastSearchCriteria, lastSearchCriteriaDetail);
		lastSearchCriteriaDetail.module = rowData.moduleId;
		lastSearchCriteriaDetail['function'] = rowData.functionId;
		lastSearchCriteriaDetail.logStatus = "";
		lastSearchCriteriaDetail.userId = rowData.createBy;
		lastSearchCriteriaDetail.appId = rowData.appId;
		lastSearchCriteriaDetail.dateFrom = startDate.format('dd/mm/yy');
		lastSearchCriteriaDetail.dateTo = endDate.format('dd/mm/yy');
		lastSearchCriteriaDetail.logDetail = "1";
		lastSearchCriteriaDetail.messageType = '';
		restoreSearchCriteria();
		
		$('#search-form').attr('ajax-loading-target', "#logDetailTable_wrapper");
		
		dataTable.fnDraw(true); //trigger function 'serverData'
		return false;
	}
	
	window.backToList =
	function backToList(notClearMsg, refreshSearch){
		ST3Lib.message.clear();
		execLayoutState('back-to-list');
		lastAction = 'back';
		pageType = 'list';
		restoreSearchCriteria();
		$('#search-form').attr('ajax-loading-target', "#result_wrapper");
	};
	
	window.refresh = 
	function refresh(){
		//restoreSearchCriteria();
		$('#WST33010Search').submit();	
	};
	
	window.onload = function(){
		/*
		var flagSearchOnLoad = get('searchOnLoad');
		if(flagSearchOnLoad == 'true'){
			var searchForm = $('#search-form');
			searchForm.find('input[name=appId]').val(get('appId'));
			searchForm.find('input[name=dateFrom]').val(get('dateFrom'));
			searchForm.find('input[name=dateTo]').val(get('dateTo'));
			searchForm.find('input[name=userId]').val(get('userId'));
			searchForm.submit();	
		}
		*/
		//noted : remove these comment if you want to use post method. check if searchOnLoad with other method		
		
		//var isSearchOnLoad = $('#isSearchOnLoad').val();
		//if(isSearchOnLoad){
		var flagCallFromHyperlink = ${callFromHyperlink};//get('searchOnLoad');
		if(flagCallFromHyperlink){
			$('#WST33010Search').click();
			//var searchForm = $('#search-form');
			//searchForm.submit();
		}
	};
	
	function get(name){
		   if(name=(new RegExp('[?&]'+encodeURIComponent(name)+'=([^&]*)')).exec(location.search))
		      return decodeURIComponent(name[1]);
	}
	
	var initDataTable = false;
	window.serverData = 
	function serverData(sSource, aoData, fnCallback, oSettings) {
		if (initDataTable === false || lastAction === 'clear') {
			initDataTable = true;
			fnCallback({iTotalRecords: 0, iTotalDisplayRecords: 0, aaData: []});
			return;
		}
		
		var searchForm = $('#search-form');
		searchForm.find('input[name=firstResult]').val(oSettings._iDisplayStart || 0);
		searchForm.find('input[name=rowsPerPage]').val(oSettings._iDisplayLength || 10);
		searchForm.submitForm(function(data, loading){
			var firstResult = data.firstResult
				, rowsPerPage = data.rowsPerPage
				, totalRows =  data.totalRows;
			processAjaxData(data, loading, function(arrayData){
				fnCallback({
					iTotalRecords: totalRows,
					iTotalDisplayRecords: totalRows,
					aaData: arrayData
				});
			});
		});
	};
	
	window.prepareDataTable =
	function prepareDataTable(){
		var dataTable = $('#result').dataTable();
		ST3Lib.sizing.autoHeight();
		
		var oSettings = dataTable.fnSettings();
		oSettings.aoColumns[0].sClass = 'col1 rownum';
		oSettings.aoColumns[1].sClass = 'col2 appid';
		oSettings.aoColumns[2].sClass = 'col3';
		oSettings.aoColumns[3].sClass = 'col4';
		oSettings.aoColumns[4].sClass = 'col5';
		oSettings.aoColumns[5].sClass = 'col6';
		oSettings.aoColumns[6].sClass = 'col7';
		oSettings.aoColumns[7].sClass = 'col8';
		oSettings.aoColumns[8].sClass = 'col9';
		
	};
	
	function processAjaxData(datas, loading, callback){
		if (datas === null) return;
		ST3Lib.message.setAppendMode(false);
		var isSearchBtn = isSearchButtonClick;
		isSearchButtonClick = false;
		var dataTable = $( '#result' ).dataTable();

		if ( loading ) {
			loading.autoClose = false;
			loading.resize();
		}
		
// 		var userList = datas.userList;
		datas = datas.groupedLogs;
		if ( !datas || datas.length === 0 ){
			lastSelectedId = {};
			fnDraw();
			callback({iTotalRecords: 0, iTotalDisplayRecords: 0, aaData: []});
			execLayoutState('search-data-not-found');
			loading.close();
			
			return;	
		}else{
			execLayoutState('search-data-found');
		}
		
		prepareDataTable();
		
		var logList = datas;
		var tempDiv = $('<div/>');
		var arrayData = [];
		
		var i = 0;
		var processor = setInterval(function(){
			var processPerTick = 10;
			for(var j  = 0; j < processPerTick; ++j) {
				var datas = logList[i++];
				if ( datas && datas.length) {
					datas.sort(function(obj1, obj2){
						if (obj1.seqNo === obj2.seqNo)
							return 0;
						return obj1.seqNo < obj2.seqNo?-1: 1;
					});
					var data = datas[0];
					var lastIndex = datas.length - 1;
					
					var startDate = new Date(data.createDate);
					var startTime = zeroLead( startDate.getHours(), 2) + ':' 
									+ zeroLead(startDate.getMinutes(), 2) + ':' 
									+ zeroLead(startDate.getSeconds(), 2);
					var endDate = new Date(datas[lastIndex].createDate);
					var endTime = zeroLead( endDate.getHours(), 2) + ':' 
									+ zeroLead(endDate.getMinutes(), 2) + ':' 
									+ zeroLead(endDate.getSeconds(), 2);
					data.endDate = datas[lastIndex].createDate;
					data.translateStatus = datas[lastIndex].translateStatus;
					data.messageCode = datas[lastIndex].messageCode;
					data.message = datas[lastIndex].message;
					var serialize = tempDiv.text(JSON.stringify(data)).html();
					
					arrayData.push([
					    ''
					    , data.appId || ''
					    , (data.moduleId || '') + '-' + (data.moduleName || '')
					    , (data.functionId || '') + '-' +  (data.functionName || '')
					    , startDate.format('dd/mm/yy ' + startTime)
	                    , endDate.format('dd/mm/yy ' + endTime)
	                    , data.createBy || ''
	                    , '<a href="#" class="detail">{0}</a><div style="display:none;">{1}</div>'.format(data.translateStatus || '', serialize)
	                    , data.message || ''
	                    ]);
				}else{
					processPerTick = 0;
					clearInterval(processor);
// 					dataTable.fnAddData(arrayData);
					callback(arrayData);
					loading.close();
					$('#module').focus();
					break;
				}
			}
		},1  );
		
	}
	
	function fnDraw(){
		return;
		var dataTable = $( '#result' ).dataTable();
		dataTable.fnSettings()._invokeOnDraw = true;
		dataTable.fnDraw(true);
	}
	

	
})(ST3Lib.$);

</script>

<div id="screen-panel" class="container-fluid">
<div id="search-criteria">
	<form:form method="post" id="search-form" action="${_mappingPath}/list" ajax="search" ajax-loading-target="#result_wrapper" 
		autocomplete="off" validate-error="searchValidateError">
		<input type="hidden" name="noAppId" />
		<input type="hidden" name="noModule" />
		<input type="hidden" name="noFunction" />
		<input type="hidden" name="isSearchOnLoad" id="isSearchOnLoad" value="${isSearchOnLoad}" />
		<input type="hidden" name="noStatus" />
		<input type="hidden" name="noLevel" />
		<input type="hidden" name="noUserId" />
		<input type="hidden" name="logDetail" default="0" value="0" />
		<input type="hidden" name="firstResult" value="0" default="0" />
		<input type="hidden" name="rowsPerPage" value="10" default="10" />
		
		
		<div class="row pb-2"> 
        	<div class="col-xl-6 col-lg-6 col-md-6 col-sm-12 col-12">
        		<label for="module" class="mx-1 my-0">
					<spring:message code="ST3.WST33010.Label.Module" />:
				</label>
				<select id="module" name="module" class="form-control form-control-sm">
					<option value="">&lt;All&gt;</option>
				</select>
        	</div>
        	<div class="col-xl-2 col-lg-2 col-md-2 col-sm-4 col-12">
				<label for="dateFrom" class="mx-1 my-0">
					<span class="MandatoryFieldFont">*</span>
					<spring:message code="ST3.WST33010.Label.FromDate" />:
				</label>
				<div class="d-flex flex-row">
					<input id="dateFrom" type="text" name="dateFrom" maxlength="10" class="form-control form-control-sm MandatoryField" format-msg="date"
						default="${currentDate}" value="${currentDate}"/>
				</div>
			</div>
			<div class="col-xl-2 col-lg-2 col-md-2 col-sm-4 col-12">
				<label for="dateTo" class="mx-1 my-0">
					<span class="MandatoryFieldFont">*</span>
					<spring:message code="ST3.WST33010.Label.ToDate" />:
				</label>
				<div class="d-flex flex-row">
					<input id="dateTo" type="text" name="dateTo" maxlength="10" class="form-control form-control-sm MandatoryField" format-msg="date"
						default="${currentDate}" value="${currentDate}"/>
				</div>
			</div>
			<div class="col-xl-2 col-lg-2 col-md-2 col-sm-4 col-12">
				<label for="logStatus" class="mx-1 my-0">
					<spring:message code="ST3.WST33010.Label.Status" />:
				</label>
				<select id="logStatus" name="logStatus" class="form-control form-control-sm">
					<option value="">&lt;All&gt;</option>
					<c:forEach items="${logStatus}" var="item">
						<option value="${item.id.code}">${item.value}</option>
					</c:forEach>
				</select>
				<select id="processStatus" name="logStatus" class="form-control form-control-sm hide" disabled="disabled">
					<option value="">&lt;All&gt;</option>
					<c:forEach items="${processStatus}" var="item">
						<option value="${item.id.code}">${item.value}</option>
					</c:forEach>
				</select>
			</div>
			<div class="col-xl-6 col-lg-6 col-md-6 col-sm-12 col-12">
				<label for="function" class="mx-1 my-0">
					<spring:message code="ST3.WST33010.Label.Function" />:
				</label>
				<select id="function" name="function" class="form-control form-control-sm">
					<option value="">&lt;All&gt;</option>
				</select>
			</div>
			<div class="col-xl-2 col-lg-2 col-md-2 col-sm-4 col-12">
				<label for="userId" class="mx-1 my-0">
					<spring:message code="ST3.WST33010.Label.UserID" />:
				</label>
				<input type="text" id="userId" name="userId" maxlength="20" format-msg="userId" class="form-control form-control-sm"/>
			</div>
			<div class="col-xl-2 col-lg-2 col-md-2 col-sm-4 col-12">
				<label for="appId" class="mx-1 my-0">
					<spring:message code="ST3.WST33010.Label.AppID" />:
				</label>
				<input type="text" id="appId" name="appId" maxlength="10" value="${appId}" class="form-control form-control-sm"/>
			</div>
			<div class="col-xl-2 col-lg-2 col-md-2 col-sm-4 col-12">
				<div id="_logStatus-panel" class="hide">
					<label for="messageType">
						<spring:message code="ST3.WST33010.Label.ColLevel" />:
					</label>
					<select id="messageType" name="messageType" class="form-control form-control-sm" disabled="disabled">
						<option value="">&lt;All&gt;</option>
						<c:forEach items="${messageLevel}" var="item">
							<option value="${item.id.code}">${item.value}</option>
						</c:forEach>
					</select>
				</div>
			</div>
		</div>
		<div class="row">
			<div id="search-command" class="col-xl-6 col-lg-6 col-md-6 col-sm-6 col-6" style="text-align: left;">
				<spring:message code="ST3.WST33010.Button.Search" var="WST33010Search"/>
				<sc2:button functionId="ST3301" screenId="WST33010" buttonId="WST33010Search" 
					type="submit" value="${WST33010Search}" styleClass="button"
					/>
					
				<spring:message code="ST3.WST33010.Button.BtnClear" var="WST33010Clear"/>
				<sc2:button functionId="ST3301" screenId="WST33010" buttonId="WST33010Clear" 
					type="reset" value="${WST33010Clear}"  styleClass="button" 
					secured="false" onClick="clearSearch()" />
			</div>
			
			
			<div class="col-xl-6 col-lg-6 col-md-6 col-sm-6 col-6 hide" id="common-command" style="text-align: right;">
				<spring:message code="ST3.WST33010.Button.Download" var="WST33010Download"/>
				<sc2:button functionId="ST3301" screenId="WST33010" buttonId="WST33010Download" 
					type="button" value="${WST33010Download}" styleClass="button" 
					onClick="download()" 
					/>
				<spring:message code="ST3.WST33010.Button.Back" var="WST33010Back"/>
				<sc2:button functionId="ST3301" screenId="WST33010" buttonId="WST33010Back" 
					type="button" value="${WST33010Back}" styleClass="button" 
					onClick="backToList();" secured="false" 
					/>
				
			</div>
		</div>
	</form:form>
</div>

<hr/>
<div id="data-head-panel" class="row pb-3">	
	<div class="col-xl-12 col-lg-12 col-md-12 col-sm-12 col-12">
		<div id="search-result" class="overflow-hidden autoheight">
			<div id="loglist" class="autoheight overflow-hidden">
					
					<spring:message code="ST3.WST33010.Label.ColNo" var="ColNo"/>
					<spring:message code="ST3.WST33010.Label.ColAppID" var="ColAppID"/>
					<spring:message code="ST3.WST33010.Label.ColModule" var="ColModule"/>
					<spring:message code="ST3.WST33010.Label.ColFunction" var="ColFunction"/>
					<spring:message code="ST3.WST33010.Label.ColStartTime" var="ColStartTime"/>
					<spring:message code="ST3.WST33010.Label.ColEndTime" var="ColEndTime"/>
					<spring:message code="ST3.WST33010.Label.ColUserID" var="ColUserID"/>
					<spring:message code="ST3.WST33010.Label.ColStatus" var="ColStatus"/>
					<spring:message code="ST3.WST33010.Label.ColMessage" var="ColMessage"/>
					<datatables:table id="result" data="${loglist}" cdn="false" row="row" cssClass="result fixedheader fixedcolumn" 
						paginate="true" cssStyle="height:310px;" sort="true" filter="false"
						serverSide="true" serverData="serverData"
						info="true" displayLength="10" paginationType="full_numbers">
						<datatables:column title="${ColNo}" cssClass="col1 rownum fixed" sortable="false" searchable="false" />
				        <datatables:column title="${ColAppID}" cssClass="col2 appid" sortable="false" />
				        <datatables:column title="${ColModule}" cssClass="col3" sortable="false"/>
				        <datatables:column title="${ColFunction}" cssClass="col4" sortable="false"/>
				        <datatables:column title="${ColStartTime}" cssClass="col5" sortable="false"/>
				        <datatables:column title="${ColEndTime}" cssClass="col6" sortable="false"/>
				        <datatables:column title="${ColUserID}" cssClass="col7" sortable="false"/>
				        <datatables:column title="${ColStatus}" cssClass="col8" sortable="false"/>
				        <datatables:column title="${ColMessage}" cssClass="col9" sortable="false"/>
				    </datatables:table>
				<iframe id="downloadIframe" name="downloadIframe" frameborder="0" style="width:100px;height:100px;border:0px solid black;position:static;left:0px;display:none;" ></iframe>
			</div>
		
			<jsp:include page="WST33011LogMonitoringDetailsView.jsp"></jsp:include>
						
						
			
		</div>
	</div>
</div>
</div>