(function($){
	var checkConfirmMessageBeforeExit = false;
	
	window.onbeforeunload = function(event) {
		if(checkConfirmMessageBeforeExit){
		  return MSTD0003ACFM;
		}
	};
	
	var lastSearchCriteria = {};
	var lastAction = '';
	var UPDATE_KEYS_SEPERATER = '|';
	
	
	window.cancelSubScreen = 
		function cancelSubScreen(){
			window.parent.GWRDSLib.dialog.close();
	};
	
	// Onload
	$(function(){
		
		clickSearch();
		$('#result').on('click', 'input:checkbox', function(ev){
			$('input[type=checkbox][name=objectIds]').not($(this)).prop( 'checked', false );
			var checkbox = $(this);
//			if (checkbox.is(':disabled')) return;
			$('#result-list-form').find('input:hidden[name=updateKeySet]').val(checkbox.val());
		});
	});
	
	
	window.clickSearch = 
		function clickSearch(){
		lastAction = 'search';
		GWRDSLib.clearST3Message();
		$("#messageResult").val("");
		
		var searchForm = $('#search-form');
		searchForm.find('input[name=firstResult]').val(0);
		searchForm.find('input[name=rowsPerPage]').val(rowsPerPage);
		
		$("#search-form").submit();
	}
	
	
	var oTable;
	window.searchFinish = 
		function searchFinish(data,loading){
			if(data  && typeof data != 'undefined'){
				checkConfirmMessageBeforeExit = false;
				
				if(data.errorMessages != "" && typeof data.errorMessages != 'undefined') {
	    			if (data.focusId != "" && typeof data.focusId != 'undefined') {
	    				searchNotFoundMode();
	    			}
	    		} else {
	    			dataForm = data.objectForm;
	    			var dataTable = $( '#result' ).dataTable();
	    			dataTable.fnClearTable(true);
	    			
	    			if (dataForm != null) {
//	    				ST3Lib.content.disabled( '#data-head-panel', false );
	    				$.each(dataForm, function(item, value){
	    		            $("#"+item).val(value);
	    		        });
//	    				ST3Lib.content.disabled( '#data-head-panel', true );
	    				
	    				processAjaxDataList(data);
	    			}
	    			loading.close();
	    		}
			}
			loading.close();
			$('#result_filter input:text:enabled:first').focus();
		};
	
		window.processAjaxDataList =
			function processAjaxDataList(datas){
			var dataList = datas.objectsInfoList;
//			console.info(dataList);
				if(dataList){
//					ST3Lib.content.disabled($('#operation-panel'), false);
					$('#result .dataTables_empty').parent().remove();
					var dataTable = $('#result').dataTable();
					dataTable.fnClearTable(true);
					var setting = dataTable.fnSettings();
					setting.aoColumns[0].sClass = 'col1';
					setting.aoColumns[1].sClass = 'col2 rownum';
					setting.aoColumns[2].sClass = 'col3';
					setting.aoColumns[3].sClass = 'col4';
					setting.aoColumns[4].sClass = 'col5';
									
					var tempDiv = $('<div/>');
					var i = 0;
					var arrayData = [];
					var processor = setInterval(function(){
					var processPerTick = 15;
					for(var j = 0; j < processPerTick; ++j) {
						var data = dataList[i++];
						if ( data ) {
							var serialize = tempDiv.text(JSON.stringify(data)).html();
							var chkBox = '<input class="checkbox" type="checkbox" name="objectIds" value="' + 
										 	data.unitModel + UPDATE_KEYS_SEPERATER +
										 	data.unitType + UPDATE_KEYS_SEPERATER +
										 	data.unitPlant + UPDATE_KEYS_SEPERATER +
										 	'" /><div class="serialize" style="display:none">' + serialize + '</div>';
							arrayData.push([ 
							                chkBox
							                , '' 
										    , data.unitModel
										    , data.unitType  
										    , data.unitPlant
										]);
						}else{
							processPerTick = 0;
							clearInterval(processor);
							dataTable.fnAddData(arrayData);
							break;
						}
					}
					},1 );
				}
				
				$("input[type=text]").attr("maxlength","30");
				
			};
			
			
			window.selectData = 
				function selectData(){
					
					var checkbox = $('#result').find('input:checkbox:checked');
					var count = 0;
					var checked = [];
					$("input[name='objectIds']:checked").each(function ()
					{
						checked.push($(this).val());
					    count++;
					});
					if (count == 1) {
						var jsonSerialize = $("input[name='objectIds']:checked").parent().find('.serialize').text();
						var objSerialize = JSON.parse(jsonSerialize);
						window.parent.GWRDSLib.dialog._callback(objSerialize);
						window.parent.__callbackData();
					}else{
						$("input:checkbox[name='objectIds']:checked:first").focus();
						GWRDSLib.clearST3Message();
						ST3Lib.message.addError(MSTD1017AERR);
						ST3Lib.message.show(1);
						return;
					}
			};
			
			/**
			 * Funciton : Create Pagination 
			 */
			var initDataTable = false;
			window.serverData = 
			function serverData(sSource, aoData, fnCallback, oSettings) {

				if (initDataTable === false || lastAction === 'clear' ) {
					GWRDSLib.clearST3Message();
					initDataTable = true;
					fnCallback({iTotalRecords: 0, iTotalDisplayRecords: 0, aaData: []});
					return;
				}
				
				if(lastAction === 'cancel') {
					GWRDSLib.clearST3Message();
					fnCallback({iTotalRecords: 0, iTotalDisplayRecords: 0, aaData: []});
					return;
				}
				
				var searchForm = $('#search-form');
				searchForm.find('input[name=firstResult]').val(oSettings._iDisplayStart || 0);
				searchForm.find('input[name=rowsPerPage]').val(oSettings._iDisplayLength || rowsPerPage);
				searchForm.find('input[name=messagResult]').val("");
				
				
				searchForm.submitForm(function(data, loading){
					var firstResult = data.firstResult
					, rowsPerPage = data.rowsPerPage
					, totalRows =  data.totalRows;
					processAjaxDataList(data, loading, function(arrayData){
						fnCallback({
							iTotalRecords: totalRows,
							iTotalDisplayRecords: totalRows,
							aaData: arrayData
						});
					});
				});
			};
	
			function searchNotFoundMode(){
//				ST3Lib.content.disabled('#search-criteria', false);
//				ST3Lib.content.disabled($('#search-panel'), false);
//				ST3Lib.content.disabled($('#operation-panel'), false);
//				$('#save-panel').addClass('hide');
//				$( '#result' ).dataTable().fnClearTable(true);
			}
			
			
			
	
})(ST3Lib.$);