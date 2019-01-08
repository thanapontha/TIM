<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="datatables" uri="http://github.com/dandelion/datatables" %>

<div id="log-detail-panel" class="autoheight hide overflow-hidden row pb-3">
<div class="col-xl-12 col-lg-12 col-md-12 col-sm-12 col-12">
<style type="text/css">
	#screen-name{
		font-size: 12px;
	}
	
	#logDetailTable .col1 {
		width: 130px;
		white-space: nowrap;
	}
	#logDetailTable .col2 {
		width: 85px;
	}
	#logDetailTable .col3 {
		width: 300px;
	}
	#logDetailTable .col4 {
		width: 300px;
	}
	#logDetailTable .col5 {
		width: 80px;
	}
	#logDetailTable .col6 {
		width: 60px;
	}
	#logDetailTable .col7 {
		width: 60px;
	}
	#logDetailTable .col8 {
		width: 850px;
	}
	
	#logDetailTable_wrapper .dataTables_filter {
		visibility: hidden;
	}
	
	#download-option {
		border: 1px solid #AAA;
		padding: 3px;
	}
		#download-option label {
			margin-right: 15px;
		}
		#download-option strong {
			font-size: 12px;
			margin-bottom: 10px;
			display: block;
		}
</style>
<script language="javascript" type="text/javascript">
(function($){
	//window.ready
	$(function(){
		
	});
	
	window.searchDetailFinish = 
	function searchDetailFinish(datas, loading){
		if (datas === null) return;
		ST3Lib.message.setAppendMode(false);
		var dataTable = $( '#logDetailTable' ).dataTable();
		dataTable.fnClearTable(false);//no redraw
		$('#common-command').removeClass('hide');
		//datas = datas.logs;
		if (loading) loading.close();
		$('#module').focus();
		datas = datas.groupedLogsDisplay[0];
		
		if ( loading ) {
			loading.autoClose = false;
			loading.resize();
		}
		
		if ( !datas || datas.length === 0 ){
			dataTable.fnDraw(true);
			$('#logDetailTable').dataTable().fnClearTable();
			$('#module').focus();
			loading.close();
			return;	
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
		
		dataTable.fnClearTable(false);//no redraw
		var i = 0;
		var arrayData = [];
		/* start: sorting already handled in db
 		datas.sort(function(obj1, obj2){
			if (obj1.seqNo === obj2.seqNo)
				return 0;
			return obj1.seqNo < obj2.seqNo?-1: 1;
		}); 
		end */
		//var processStatus = $('#processStatus').val();

		dataTable.fnAddData(datas);
		
		
	};
	
	var initDataTable = false;
	window.serverDetailData = 
	function serverDetailData(sSource, aoData, fnCallback, oSettings) {
		if (initDataTable === false || lastAction === 'clear') {
			initDataTable = true;
			fnCallback({iTotalRecords: 0, iTotalDisplayRecords: 0, aaData: []});
			return;
		}
		
		var searchForm = $('#search-form');
		searchForm.find('input[name=firstResult]').val(oSettings._iDisplayStart || 0);
		searchForm.find('input[name=rowsPerPage]').val(oSettings._iDisplayLength || 10);
		
		searchForm.find('input[name=logDetail]').val(1);
		
		searchForm.submitForm(function(data, loading){
			var firstResult = data.firstResult
			, rowsPerPage = data.rowsPerPage
			, totalRows =  data.totalRows;
			processAjaxDetailData(data, loading, function(arrayData){
				fnCallback({
					iTotalRecords: totalRows,
					iTotalDisplayRecords: totalRows,
					aaData: arrayData
				});
			});
		});
	};
	
	window.updateColumn = 
	function updateColumn() {
		var table = $('#logDetailTable');
		var dataTable = $('#logDetailTable').dataTable();
		var oSettings = dataTable.fnSettings();
		
		var checkbox = $('#download-option input:checkbox');
		var width = table.outerWidth();
		var state = [];
		for(var i = 0;  i < checkbox.length; ++i) {
			var col = checkbox[i].getAttribute('col');
			var visible = !checkbox[i].checked;
			state[i] = checkbox[i].checked;
			if( visible === oSettings.aoColumns[col].bVisible )
				continue;
			
			dataTable.fnSetColumnVis( col, visible );
			if (visible)
				width += $(oSettings.aoColumns[col].nTh).width();
			else width -= $(oSettings.aoColumns[col].nTh).width();
			
		}
		
		
		if (state[1] && state[2]) {
			table.removeAttr('table-width');
			table.parent().css('overflow', 'hidden');
			table[0].style.width = '100%';
			table.parent().css('overflow', 'auto');
			
		}else {
			table.parent().css('overflow', 'auto');
			table[0].style.width = width + 'px';
			while(table.find('th:last').width() < 830) {
				width += 20;
				table[0].style.width = width + 'px';
			}
			table.attr('table-width', width); //prevent auto resize script on IE6
		}
	}
	
	window.processAjaxDetailData =
	function processAjaxDetailData(datas, loading, callback){
		if (datas === null) return;
		ST3Lib.message.setAppendMode(false);
		
		var dataTable = $( '#logDetailTable' ).dataTable();
		dataTable.fnClearTable(false);//no redraw
		$('#common-command').removeClass('hide');
		if (loading) loading.close();
		$('#module').focus();
// 		datas = datas.groupedLogsDisplay[0];
		datas = datas.groupedLogsDisplay;
		
		if ( loading ) {
			loading.autoClose = false;
			loading.resize();
		}
		
		if ( !datas || datas.length === 0 ){
// 			dataTable.fnDraw(true);
// 			$('#logDetailTable').dataTable().fnClearTable();
			callback({iTotalRecords: 0, iTotalDisplayRecords: 0, aaData: []});
			$('#module').focus();
			loading.close();
			return;	
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
		
		dataTable.fnClearTable(false);
		var i = 0;
		var arrayData = [];
// 		dataTable.fnAddData(datas);
// 		callback(datas);
		for(var j = 0; j < datas.length; ++j) {
			var innArr = datas[j];
			for(var i = 0; i < innArr.length; ++i) {
				arrayData.push(innArr[i]);
			}
		}
		callback(arrayData);
		loading.close();
		
	}
	
	
})(ST3Lib.$);
</script>

	<div id="download-option">
		<strong>Non-Display Option</strong>
		<label for="noAppId">
			<input id="noAppId" type="checkbox" class="checkbox" name="noAppId" value="1" col="1" 
				checked="checked" onclick="updateColumn()"> 
			<spring:message code="ST3.WST33010.Label.AppID" /></label>
		<label for="noModule">
			<input id="noModule" type="checkbox" class="checkbox" name="noModule" value="1" col="2" 
				checked="checked" onclick="updateColumn()">
			<spring:message code="ST3.WST33010.Label.Module" /></label>
		<label for="noFunction">
			<input id="noFunction" type="checkbox" class="checkbox" name="noFunction" value="1" col="3" 
				checked="checked" onclick="updateColumn()">
			<spring:message code="ST3.WST33010.Label.Function" /></label>
		<label for="noStatus">
			<input id="noStatus" type="checkbox" class="checkbox" name="noStatus" value="1" col="4" 
				onclick="updateColumn()"> 
			<spring:message code="ST3.WST33010.Label.Status" /></label>
		<label for="noLevel">
			<input id="noLevel" type="checkbox" class="checkbox" name="noLevel" value="1" col="6" 
				onclick="updateColumn()"> 
			<spring:message code="ST3.WST33010.Label.ColLevel"/></label>
		<label for="noUserId">
			<input id="noUserId" type="checkbox" class="checkbox" name="noUserId" value="1" col="5" 
				onclick="updateColumn()"> 
			<spring:message code="ST3.WST33010.Label.UserID" /></label>
	</div>
	<spring:message code="ST3.WST33010.Label.ColAppID" var="ColAppID"/>
	<spring:message code="ST3.WST33010.Label.ColModule" var="ColModule"/>
	<spring:message code="ST3.WST33010.Label.ColFunction" var="ColFunction"/>
	<spring:message code="ST3.WST33010.Label.ColUserID" var="ColUserID"/>
	<spring:message code="ST3.WST33010.Label.ColStatus" var="ColStatus"/>
	<spring:message code="ST3.WST33010.Label.ColDateTime" var="ColDateTime"/>
	<spring:message code="ST3.WST33010.Label.ColLevel" var="ColLevel"/>
	<spring:message code="ST3.WST33010.Label.ColMessage" var="ColMessage"/>
<%-- 	<datatables:table id="logDetailTable" data="${rolelist}" row="row"  --%>
<%--  		cssClass="fixedheader" paginate="true" cssStyle="height:320px;" sort="false" --%>
<%--  		info="true" displayLength="10" paginationType="full_numbers"> --%>
	<datatables:table id="logDetailTable" data="${rolelist}" cdn="false" row="row" cssClass="fixedheader" 
 				paginate="true" cssStyle="height:320px;" sort="false" filter="false"
 				serverSide="true" serverData="serverDetailData"
 				info="true" displayLength="10" paginationType="full_numbers">
		<datatables:column title="${ColDateTime}" cssClass="col1" sortable="false"/>
        <datatables:column title="${ColAppID}" cssClass="col2" sortable="false" visible="false"/>
        <datatables:column title="${ColModule}" cssClass="col3" sortable="false" visible="false"/>
        <datatables:column title="${ColFunction}" cssClass="col4" sortable="false" visible="false"/>
        <datatables:column title="${ColStatus}" cssClass="col5" sortable="false"/>
        <datatables:column title="${ColUserID}" cssClass="col6" sortable="false"/>
        <datatables:column title="${ColLevel}" cssClass="col7" sortable="false"/>
        <datatables:column title="${ColMessage}" cssClass="col8" sortable="false"/>
    </datatables:table>
</div>
</div>
	