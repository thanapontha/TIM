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
		
		lastSearchCriteria = {};
		enableDisableButton(false);
		$('#rowsPerPage').val(rowsPerPage);
		
		/*$('#vehiclePlantSearch').focus();*/
		
/*		$('#result').on('click', 'input:checkbox', function(ev){
			
			var checkbox = $( this );
			if (checkbox.is(':disabled')) return;
			$('#result-list-form').find('input:hidden[name=updateKeySet]').val(checkbox.val());
		});*/
		
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
	    				/*setFormFocus("vehiclePlantSearch");*/
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
				setting.aoColumns[0].sClass = 'col1 rownum';
				setting.aoColumns[1].sClass = 'col2 ';
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
				var i = 0;
				var arrayData = [];
				var processor = setInterval(function(){
				var processPerTick = rowsPerPage;
				for(var j = 0; j < processPerTick; ++j) {
					var data = dataList[i++];
					if ( data ) {
						var serialize = tempDiv.text(JSON.stringify(data)).html();
						/*var chkBox = '<input class="checkbox" type="checkbox" name="objectIds" value="' + 
								 	data.updateDt + UPDATE_KEYS_SEPERATER +
								 	data.vinNo + 
								 	 '" /><div class="serialize" style="display:none">' + serialize + '</div>';*/
						var link = '<a href="'+_rootPath+'/NewCarInsurance/ActivateRedPlant">' + data.vinNo + '</a>';
						arrayData.push([ 
						                ''
						                , data.ddmsStatus || ''
									    , data.insStatus || ''
									    , data.model || ''
									    , link || ''
									    , data.typeOfPurchase || ''
									    , data.fnCompany || ''
									    , data.insCode || ''
									    , data.insCompany || ''
									    , data.insType || ''
									    , data.insPremium || ''
									    , data.insActivationDate || ''
									]);
					}else{
						processPerTick = 0;
						clearInterval(processor);
						callback(arrayData);
						/*$('#result input:checkbox:first').focus();*/
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
				/*$('#save-panel').addClass('hide');
				$('#vehiclePlantSearch').focus();*/
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
		
/*
		window.addObject =
			function addObject(){
				GWRDSLib.clearST3Message();
				if(formValidateError($("#search-criteria"))){
					lastAction = 'add';
					restoreSearchCriteria();
					ST3Lib.content.disabled( '#search-field', true );
					enableDisableButton(false);
					checkConfirmMessageBeforeExit = true;
					$('#result .dataTables_empty').parent().hide();
					$('.dataTables_length').hide();
					$('#result-list-form').attr('action', mappingPath + '/add').attr('_method', 'post');
					var firstRow = false; // true Create Row first ,But false last Row
					objectEditor.addRow(firstRow);
					moveHScrollBar(0);
					$("#data-head-panel").find('input[type=checkbox][name=objectIds]').prop( 'checked', false );
			}
		};
		
		window.saveAddEditObject = 
			function saveAddEditObject(){
				var confirmMsg = MBW00001ACFM;
				var confirmCode = 'MBW00001ACFM';
				if (lastAction !== 'add') {
					confirmMsg = MBW00002ACFM;
					confirmCode = 'MBW00002ACFM';
				}
				ST3Lib.dialog.confirm(confirmMsg, confirmCode, function(ret){
					if (!ret) {
						return;
					}else{
						GWRDSLib.clearST3Message();
						if (lastAction === 'add') {
							$('#result-list-form').attr('action', mappingPath + '/submitAdd').attr('_method', 'post');
						} else {
							$("#tcFrom").val($("#hidtcFrom").val());
							$('#result-list-form').attr('action', mappingPath + '/submitEdit').attr('_method', 'post');
						}
						var saveForm = $('#result-list-form');
						saveForm.submit();
					}
				});
			};
		
		window.updateObjectFinish =
			function updateObjectFinish(datas, loading){
				if (datas === null) {
					loading.close();
					return;	
				}
				
				if(datas.status === 'OK'){
					checkConfirmMessageBeforeExit = false;
					if(datas.infoMessages.length > 0) {
						 ST3Lib.message.show(1);
					}
					dataForm = datas.objectForm;
					if (dataForm!=null) { 
						$.each(dataForm, function(item, value){
				            $("#"+item).val(value);
				        });  
					}
					
					searchFoundMode();
					$('.dataTables_info').css("visibility","visible")
					$('.dataTables_paginate').css("visibility","visible");
					
					lastAction = 'search';
					ST3Lib.content.disabled($('#search-field'), false);
					$('#search-form').submit();
				}else{
					if(datas.errorMessage) {
						ST3Lib.message.show(1);
					}else if(datas.errorMessages != null && datas.errorMessages.length > 0) {
						ST3Lib.message.show(1);
					}
					if (datas.focusId != "" && typeof datas.focusId != 'undefined') {
						setFormFocus(datas.focusId);
					}else{
						$('#result input:text:first').focus();
					}
				}
				if (loading) {
					loading.close();
				}
		};
		
		window.editObject =
			function editObject() {
				restoreSearchCriteria();
				var checkbox = $('#result').find('input:checkbox:checked');
				var count = 0;
				var checked = [];
				$("input[name='objectIds']:checked").each(function ()
				{
					checked.push($(this).val());
				    count++;
				});
				
				if (count==1) {
					GWRDSLib.clearST3Message();
					lastAction = 'edit';
					
					checkConfirmMessageBeforeExit = true;
					ST3Lib.content.disabled( '#search-field', true );
					enableDisableButton(false);
					
					var tr = checkbox.closest('tr');
					$('#result-list-form').attr('action', mappingPath+'/edit').attr('_method', 'post');
					//$("#data-head-panel").find('input[type=checkbox][name=objectIds]').prop( 'checked', true );
					objectEditor.editRow(tr.index());
					$('.dataTables_length').hide();
					$('#save-panel').removeClass('hide');
					
				}else{
					if(count == 0){
						$("input:checkbox[name='objectIds']").first().focus();
					}else{
						$("input:checkbox[name='objectIds']:checked").first().focus();
					}
					
					GWRDSLib.clearST3Message();
					ST3Lib.message.addError(MSTD1017AERR);
					ST3Lib.message.show(1);
					return;
				}
		};
		
		window.deleteObject = 
			function deleteObject() {
				GWRDSLib.clearST3Message();
				restoreSearchCriteria();
				var count = 0;
				var checked = [];
				$("input[name=objectIds]:checked").each(function ()
				{
					checked.push($(this).val());
				    count++;
				});
				
				if(count > 0 ){
					ST3Lib.dialog.confirm(MSTD0001ACFM, 'MSTD0001ACFM', function(ret){
						if(ret) {
							checkConfirmMessageBeforeExit = true;
							lastAction = 'delete';
							$.post( mappingPath+"/delete", { dataList:checked }, function( data ) {
								checkConfirmMessageBeforeExit = false;
								if(data.status === 'OK'){
									if(data.infoMessages.length > 0) {
										ST3Lib.message.addInfo(data.infoMessages);
										 ST3Lib.message.show(1);
										 $("#messageResult").val(data.infoMessages);
									}
									
									dataForm = data.objectForm;
									if (dataForm!=null) { 
										$.each(dataForm, function(item, value){
								            $("#"+item).val(value);
								        });  
									}
									
									var dataList = data.objectsInfoList;
									if(dataList){
										var dataListSize = dataList[0];
										if (!dataListSize) {
											searchFoundMode();
										}else{
											searchNotFoundMode();
										}
									} else {
										searchNotFoundMode();
									}
								}else{
									if(data.errorMessage) {
										ST3Lib.message.addError(data.errorMessage);
										ST3Lib.message.show(1);
									}else if(data.errorMessages != null && data.errorMessages.length > 0) {
										ST3Lib.message.addError(data.errorMessages);
										ST3Lib.message.show(1);
									}
								}
								$("input:checkbox[name='objectIds']:checked").first().focus();
							});
						}
					});
				}else{
					$("input:checkbox[name='objectIds']:first").focus();
					GWRDSLib.clearST3Message();
					ST3Lib.message.addError(MSTD1016AERR);
					ST3Lib.message.show(1);
					return;
				}
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
						$('#save-panel').addClass('hide');
						
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
			*/
		window.moveHScrollBar =
			function moveHScrollBar(value){
				$('#search-result .dataTable-wrapper').scrollLeft(0);
			};
		
		function restoreSearchCriteria(){
			
			/*if(lastSearchCriteria.vehiclePlantSearch != undefined && lastSearchCriteria.vehiclePlantSearch != ""){
				$("#vehiclePlantSearch").val(lastSearchCriteria.vehiclePlantSearch);			
				$("#includeExpiredData").prop( 'checked', lastSearchCriteria.includeExpiredData );
			}*/
		}
			
		function saveSearchCriteria(){
			/*lastSearchCriteria.vehiclePlantSearch = $("#vehiclePlantSearch").val();
			lastSearchCriteria.includeExpiredData = $("#includeExpiredData").prop("checked");*/
		}
		
		function searchNotFoundMode(){
			ST3Lib.content.disabled('#search-criteria', false);
			/*$('#save-panel').addClass('hide');*/
			$( '#result' ).dataTable().fnClearTable(true);
		}
		
		function enableFieldMode(type) {
			/*var resultForm = $('#result-list-form');
			if (type === 'edit') {
				resultForm.find('input:hidden[name=vehiclePlant]').val($('#vehiclePlantSearch').val());
				ST3Lib.content.disabled($(resultForm.find('input[name=tcTo]')), false);
			}else if (type === 'add') {
				resultForm.find('input:hidden[name=vehiclePlant]').val($('#vehiclePlantSearch').val());
				
				ST3Lib.content.disabled($(resultForm.find('select[name=vehicleModel]')), false);
				ST3Lib.content.disabled($(resultForm.find('input[name=tcFrom]')), false);
				ST3Lib.content.disabled($(resultForm.find('input[name=tcTo]')), false);
			}
			ST3Lib.content.disabled( '#updateKeySet', false );
			ST3Lib.content.disabled( '#vehiclePlant', false );*/
		}
		
		function addrow(tr) {
			/*$('#save-panel').removeClass('hide');*/
			GWRDSLib.clearST3Message();
		}
		
		function searchFoundMode(){		
			ST3Lib.content.disabled('#search-result', true);
			ST3Lib.content.disabled('#search-criteria', false);

			/*$('#save-panel').addClass('hide');
			$("#search-result").find('input[type=checkbox]').prop( 'disabled', false );
			$("#search-result").find('input[type=checkbox]').prop( 'checked', false );*/
		}
		
	function enableDisableButton(haveData){
		/*if(lastAction=='add' || lastAction=='edit'){
			ST3Lib.validate.disabledButtonSpecify('#WBW04110Search', true);
			ST3Lib.validate.disabledButtonSpecify('#WBW04110Clear', true);
			
			ST3Lib.validate.disabledButtonSpecify('#WBW04110Add', true);
			ST3Lib.validate.disabledButtonSpecify('#WBW04110Edit', true);
			ST3Lib.validate.disabledButtonSpecify('#WBW04110Delete', true);
		}else{
			ST3Lib.validate.disabledButtonSpecify('#WBW04110Search', false);
			ST3Lib.validate.disabledButtonSpecify('#WBW04110Clear', false);
			
			ST3Lib.validate.disabledButtonSpecify('#WBW04110Add', false);
			if(haveData){
				ST3Lib.validate.disabledButtonSpecify('#WBW04110Edit', false);
				ST3Lib.validate.disabledButtonSpecify('#WBW04110Delete', false);
			}else{
				ST3Lib.validate.disabledButtonSpecify('#WBW04110Edit', true);
				ST3Lib.validate.disabledButtonSpecify('#WBW04110Delete', true);
			}
		}*/
	}
	
})(ST3Lib.$);