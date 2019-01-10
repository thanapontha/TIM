(function($){
	
	var checkConfirmMessageBeforeExit = false;
	var lastAction = '';
	var lastSearchCriteria = {};
	var UPDATE_KEYS_SEPERATER = '|';
	
	/**
	 * Used for the case at user close screen
	 */ 
	window.onbeforeunload = function(event) {
		if(checkConfirmMessageBeforeExit){
		  return MSTD0003ACFM;
		}
	};
	
	/**
	 * Likewise Onload()
	 */
	$(function(){
		
		var colGroup = $("#fake-header-table colgroup").html();
		$("#result").prepend(colGroup);

		$("#result").find("thead tr th").each(function(k,v){ 
			if ($(v).text() == "") {
				$(v).remove();
			}
		});
		$("#result thead").prepend($("#fake-header-table thead").html());//.append(dataTable);
		
		
		lastSearchCriteria = {};
		enableDisableButton(false);
		$('#rowsPerPage').val(rowsPerPage);
		
	});
	
	
	/**
	 * Function : Click Search at Button 'Search' by in Button will have OnClick = 'clickSearch'
	 */
	window.clickSearch = 
		function clickSearch(){
			lastAction = 'search';
			$("#messageResult").val("");
			
			var searchForm = $('#search-form');
			searchForm.find('input[name=firstResult]').val(0);
			searchForm.find('input[name=rowsPerPage]').val(rowsPerPage);
			
			//Reset result panel
			var prevFlag = initDataTable;
			initDataTable = false;
			var dataTable = $('#result').dataTable();
			var setting = dataTable.fnSettings();
			setting._iDisplayStart = 0;
			dataTable.fnClearTable(true);
			initDataTable = prevFlag;
			
			GWRDSLib.clearST3Message();
			$("#search-form").submit();
			enableDisableButton(false);
	}
	
	/**
	 * Function : Return from method for Controller(Action) by set at Form:[ajax='searchFinish']
	 */
	window.searchFinish = 
		function searchFinish(data,loading){
		
			saveSearchCriteria();
		
			if(data  && typeof data != 'undefined'){
				checkConfirmMessageBeforeExit = false;
				
				if(data.errorMessages != "" && typeof data.errorMessages != 'undefined') {
	    			if (data.focusId != "" && typeof data.focusId != 'undefined') {
	    				searchNotFoundMode();
	    				enableDisableButton(false);
	    			}
	    		} else {
	    			dataForm = data.objectForm;
	    			var dataTable = $( '#result' ).dataTable();
	    			dataTable.fnClearTable(true);
	    			
	    			if (dataForm != null) {
	    				ST3Lib.content.disabled( '#data-head-panel', false );
	    				$.each(dataForm, function(item, value){
	    		            $("#"+item).val(value);
	    		        });
	    				ST3Lib.content.disabled( '#data-head-panel', true );
	    			}
	    			
	    			loading.close();
	    		}
			}
			loading.close();
		};
	
	/**
	 * Function : Create Row in Table in Case Search Data
	 */
	window.processAjaxDataList =
		function processAjaxDataList(datas, loading, callback){
			var dataList = datas.objectsInfoList;
			if(dataList){
				$('#result .dataTables_empty').parent().hide();
				var dataTable = $('#result').dataTable();
				
				var setting = dataTable.fnSettings();
				setting.aoColumns[0].sClass = 'col1';
				setting.aoColumns[1].sClass = 'col2 rownum';
				setting.aoColumns[2].sClass = 'col3';
				setting.aoColumns[3].sClass = 'col4';
				setting.aoColumns[4].sClass = 'col5';
				setting.aoColumns[5].sClass = 'col6';
				setting.aoColumns[6].sClass = 'col7';
								
				var tempDiv = $('<div/>');
				var i = 0;
				var arrayData = [];
				var processor = setInterval(function(){
				var processPerTick = rowsPerPage;
				for(var j = 0; j < processPerTick; ++j) {
					var data = dataList[i++];
					if ( data ) {
						var serialize = tempDiv.text(JSON.stringify(data)).html();
						var chkBox = '<input class="checkbox" type="checkbox" name="objectIds" value="' + 
									 	data.updateDt + UPDATE_KEYS_SEPERATER +
									 	data.vehiclePlant + UPDATE_KEYS_SEPERATER +
									 	data.vehicleModel + UPDATE_KEYS_SEPERATER +
									 	data.tcFromDisp +
									 	 '" /><div class="serialize" style="display:none">' + serialize + '</div>';
						arrayData.push([ 
						                chkBox
						                , ''
									    , data.vehicleModel || ''
									    , data.tcFromDisp || ''
									    , data.tcToDisp || ''
									    , data.updateBy || ''
									    , data.updateDateDisp || ''
									]);
					}else{
						processPerTick = 0;
						clearInterval(processor);
						callback(arrayData);
						break;
					}
				}
			},1 );
				
			enableDisableButton(true);
		} else {
			enableDisableButton(false);
			callback([]);
		}};	
	
		window.clearSearch =
			function clearSearch(){
				lastAction = "clear";
				$('#result').dataTable().fnClearTable();
				$("#messageResult").val("");
				checkConfirmMessageBeforeExit = false;
				document.getElementById("search-form").reset();	
				GWRDSLib.clearST3Message();
				enableDisableButton(false);
				lastSearchCriteria = {}
			};
		
			
		window.searchValidateError =
			function searchValidateError(){
				return;
			};
				
		window.saveValidateError =
			function saveValidateError(){
				return;
			};	
		
		/**
		 * Funciton : Create Pagination 
		 */
		var initDataTable = false;
		window.serverData = 
		function serverData(sSource, aoData, fnCallback, oSettings) {

			if (initDataTable === false || lastAction === 'clear' ) {
//				GWRDSLib.clearST3Message();
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
		
		/**
		 * Function : Add Row TR than SAVE Data
		 */
		window.addObject =
			function addObject(){
		};
		
		window.saveAddEditObject = 
			function saveAddEditObject(){
			};
		
		window.updateObjectFinish =
			function updateObjectFinish(datas, loading){
		};
		
		/**
		 * Function : Edit Row 
		 */
		window.editObject =
			function editObject() {
		};
		
		
		/**
		 * Function : Delete Rows in Table
		 */
		window.deleteObject = 
			function deleteObject() {
		};
		
		window.cancelAddEditObject =
			function cancelAddEditObject(){
				ST3Lib.dialog.confirm(MSTD0003ACFM, 'MSTD0003ACFM', function(ret){
					if ( !ret ) {
						return;
					}else{
						checkConfirmMessageBeforeExit = false;
						objectEditor.cancel();
						ST3Lib.message.clear(true);
						
						if(lastSearchCriteria.vehiclePlantSearch != undefined && lastSearchCriteria.vehiclePlantSearch != ""){
							restoreSearchCriteria();
							lastAction = 'search';
							ST3Lib.content.disabled( '#search-field', false );
			    			$('#search-form').submit();
						}else{
							$('#result .dataTables_empty').parent().show();
							ST3Lib.content.disabled('#search-criteria', false);
							lastAction = "";
							enableDisableButton(false);
						}
						
						GWRDSLib.clearST3Message();
					}
				});
			};
			
		window.moveHScrollBar =
			function moveHScrollBar(value){
				$('#search-result .dataTable-wrapper').scrollLeft(0);
			};
		
		function restoreSearchCriteria(){
		}
			
		function saveSearchCriteria(){
		}
		
		function searchNotFoundMode(){
			ST3Lib.content.disabled('#search-criteria', false);
			$( '#result' ).dataTable().fnClearTable(true);
		}
		
		function enableFieldMode(type) {
		}
		
		function addrow(tr) {
		}
		
		function searchFoundMode(){		
			ST3Lib.content.disabled('#search-result', true);
			ST3Lib.content.disabled('#search-criteria', false);

		}
		
	function enableDisableButton(haveData){
	}
	
})(ST3Lib.$);