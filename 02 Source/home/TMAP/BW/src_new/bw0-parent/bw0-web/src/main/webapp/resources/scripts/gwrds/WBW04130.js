(function($){
	var checkConfirmMessageBeforeExit = false;
	var lastSearchCriteria = {};
	var lastAction = '';
	var UPDATE_KEYS_SEPERATER = '|';
	
	window.onbeforeunload = function(event) {
		if(checkConfirmMessageBeforeExit){
		  return MSTD0003ACFM;
		}
	};
	
	$(function(){
		$('#ui-accordion-menu-header-2').click();
		$('#ui-accordion-menu-header-2').attr('tabindex','-1');
		
		lastSearchCriteria = {};
		enableDisableButton(false);
		$('#rowsPerPage').val(rowsPerPage);
		
		$('#vehiclePlantSearch').focus();
		
		$('#vehiclePlantSearch').change(function() {
			$.ajax({
				  method: "POST",
				  async : false,
				  url: mappingPath+'/vehiclePlantListChange',
				  data: { vehiclePlantSearch : $(this).val()}
				})
				.done(function( data ) {
					var $vehicleModelSearch = $('#vehicleModelSearch');
					$vehicleModelSearch.empty();
					if(data){			
						$vehicleModelSearch.append('<option value="">&lt;Select&gt;</option>');
						for(var i = 0; i < data.length; ++i) {
							var obj = data[i];
							$vehicleModelSearch.append('<option value="{0}">{1}</option>'.format(obj.stValue, obj.stLabel));
						}
					}
				})
				.error(function() {});
		});
		
		
		/**
		 * Event at use : Add or Edit Row in Table 
		 */
		window.objectEditor = ST3Lib.roweditor( '#result', {
			template: "#datatemplate",
			success: function( type, tr ){
				var imgCal = _imagePath + "icons/cal.gif";
				if ( tr.index() %2 > 0 ) tr.addClass( 'even' );
				else tr.addClass( 'odd' );
				if ( type === 'add' ) {
					enableFieldMode(type);
					addrow(tr);
					ST3Lib.message.clear(true);
					ST3Lib.message.hide(true);
					
					$('#tcFrom').MonthPicker({
				        Button: '<img class="ui-datepicker-trigger" src="'+imgCal+'" />',
				        ShowIcon: true,
				        title: 'Select date',
				        MonthFormat: 'M-y',
				        onSelect: function(){
							$(this).focus();
						},
						OnAfterChooseMonth: function(){
							$('#tcFrom').focus();
						}
				    });
					
					$('#tcTo').MonthPicker({
				        Button: '<img class="ui-datepicker-trigger" src="'+imgCal+'" />',
				        ShowIcon: true,
				        title: 'Select date',
				        MonthFormat: 'M-y',
				        onSelect: function(){
							$(this).focus();
						},
						OnAfterChooseMonth: function(){
							$('#tcTo').focus();
						}
				    });
					
					validateDateMMMYY(document.getElementById('tcFrom'));
					validateDateMMMYY(document.getElementById('tcTo'));
					
					$("#search-result").find('input[type=checkbox][name=objectIds]').prop( 'disabled', true );
					$("#search-criteria").find('input[type=checkbox][name=objectIds]').prop( 'checked', false );
					
					$("#btnPopupSubsreen").removeAttr("disabled");
//					$("#txtUnitModel").attr('disabled','disabled');
//					$("#txtUnitModel").focus();
				}else if (type === 'edit') {
					tr.find('input:checkbox').click();
					enableFieldMode(type);
					
					$('#tcTo').MonthPicker({
				        Button: '<img class="ui-datepicker-trigger" src="'+imgCal+'" />',
				        ShowIcon: true,
				        title: 'Select date',
				        MonthFormat: 'M-y',
				        onSelect: function(){
							$(this).focus();
						},
						OnAfterChooseMonth: function(){
							$('#tcTo').focus();
						}
				    });
					
					validateDateMMMYY(document.getElementById('tcTo'));
					
					var jsonObjectData = JSON.parse($(".serialize").get(tr.index()).innerHTML);
					
					$('#tcTo').focus();
					$("#search-result").find('input[type=checkbox][name=objectIds]').prop( 'disabled', true );
					
					ST3Lib.message.clear();
					ST3Lib.message.hide(true);
				}else if ( type === 'cancel' ) {
					$( 'input[type=checkbox]' ).prop( 'disabled', false );
					tr.find('input[type=checkbox]').click();
				}
			}
		});
		
		
		function enableFieldMode(type) {
			var resultForm = $('#result-list-form');
			if (type === 'edit') {
				ST3Lib.content.disabled($(resultForm.find('input[name=tcTo]')), false);
			}else if (type === 'add') {				
				ST3Lib.content.disabled($(resultForm.find('input[name=tcFrom]')), false);
				ST3Lib.content.disabled($(resultForm.find('input[name=tcTo]')), false);
			}
			
			resultForm.find('input:hidden[name=vehiclePlant]').val($('#vehiclePlantSearch').val());
			resultForm.find('input:hidden[name=vehicleModel]').val($('#vehicleModelSearch').val());
			
			ST3Lib.content.disabled( '#updateKeySet', false );
			ST3Lib.content.disabled( '#vehiclePlant', false );
			
			ST3Lib.content.disabled( '#unitType', false );
			ST3Lib.content.disabled( '#unitModel', false );
			ST3Lib.content.disabled( '#priority', false );
			ST3Lib.content.disabled( '#unitPlant', false );
			
			ST3Lib.content.disabled( '#vehicleModel', false );
			ST3Lib.content.disabled( '#vehiclePlant', false );
		}
		
		function addrow(tr) {
			$('#save-panel').removeClass('hide');
			GWRDSLib.clearST3Message();
		}
		
		$('#result').on('click', 'input:checkbox', function(ev){
			var checkbox = $( this );
			if (checkbox.is(':disabled')) return;
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
	
	window.searchFinish = 
		function searchFinish(data,loading){
		
			saveSearchCriteria();
			
			if(data  && typeof data != 'undefined'){
				checkConfirmMessageBeforeExit = false;
				if(data.errorMessages != "" && typeof data.errorMessages != 'undefined') {
	    			if (data.focusId != "" && typeof data.focusId != 'undefined') {
	    				searchNotFoundMode();
	    				setFormFocus("vehiclePlantSearch");
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
		
		function searchNotFoundMode(){
			ST3Lib.content.disabled('#search-criteria', false);
			$('#save-panel').addClass('hide');
			$( '#result' ).dataTable().fnClearTable(true);
		}
		
		function searchFoundMode(){			
			ST3Lib.content.disabled('#search-result', true);
			ST3Lib.content.disabled('#search-criteria', false);

			$('#search-result').removeClass('hide');
			$('#save-panel').addClass('hide');
			$("#search-result").find('input[type=checkbox]').prop( 'disabled', false );
			$("#search-result").find('input[type=checkbox]').prop( 'checked', false );
		}
		
		
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
					setting.aoColumns[7].sClass = 'col8';
					setting.aoColumns[8].sClass = 'col9';
					setting.aoColumns[9].sClass = 'col10';
									
					var tempDiv = $('<div/>');
					var i = 0;
					var arrayData = [];
					var processor = setInterval(function(){
					var processPerTick = rowsPerPage;
					for(var j = 0; j < processPerTick; ++j) {
						var data = dataList[i++];
						if ( data ) {
							var serialize = tempDiv.text(JSON.stringify(data)).html();
							var chkBox = '<input class="checkbox" type="checkbox" name="objectIds" id="checkbox'+(j+1)+'" value="' + 
										 	data.updateDt + UPDATE_KEYS_SEPERATER +
										 	data.vehiclePlant + UPDATE_KEYS_SEPERATER +
										 	data.vehicleModel + UPDATE_KEYS_SEPERATER +
										 	data.unitPlant + UPDATE_KEYS_SEPERATER +
										 	data.unitModel + UPDATE_KEYS_SEPERATER +
										 	data.unitType + UPDATE_KEYS_SEPERATER +
										 	data.priority + UPDATE_KEYS_SEPERATER +
										 	data.tcFromDisp + UPDATE_KEYS_SEPERATER +
										 	data.tcToDisp + UPDATE_KEYS_SEPERATER +
										 	'CLOSER'+
										 	 '" /><div class="serialize" style="display:none">' + serialize + '</div>';
							arrayData.push([ 
							                chkBox
							                , ''
										    , data.unitModel || ''
										    , data.unitType || ''
										    , data.priority || ''
										    , data.unitPlant || ''
										    , data.tcFromDisp || ''
										    , data.tcToDisp || ''
										    , data.updateBy || ''
										    , data.updateDateDisp || ''
										]);
						}else{
							processPerTick = 0;
							clearInterval(processor);
							callback(arrayData);
							$('#result input:checkbox:first').focus();
							break;
						}
						}
					},1 );
					enableDisableButton(true);
				}else{
					enableDisableButton(false);
					callback([]);
				}
			};
			
			/**
			 * Funciton : Create Pagination 
			 */
			var initDataTable = false;
			window.serverData = 
				function serverData(sSource, aoData, fnCallback, oSettings) {
					if (initDataTable === false || lastAction === 'clear') {
//						GWRDSLib.clearST3Message();
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
			if (count == 0) {
				$("input:checkbox[name='objectIds']:first").focus();
				GWRDSLib.clearST3Message();
				ST3Lib.message.addError(MSTD1016AERR);
				ST3Lib.message.show(1);
				return;
			}
			if (count>0) {
				
				ST3Lib.dialog.confirm(MSTD0001ACFM, 'MSTD0001ACFM', function(ret){
					if(ret) {
						checkConfirmMessageBeforeExit = true;
						lastAction = 'delete';
						$.post( mappingPath+"/delete", { dataList:checked }, function( data ) {
							checkConfirmMessageBeforeExit = false;
							if(data.status === 'OK'){
								if(data.warningMessages.length > 0) {
									ST3Lib.message.addWarning(data.warningMessages);
									ST3Lib.message.show(1);	
								}
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
		    	GWRDSLib.clearST3Message();
		    	ST3Lib.message.addError(MSTD1016AERR);
		    	ST3Lib.message.show(1);
				$("input:checkbox[name='objectIds']:first").focus();
				return;
		    }
		};
	
	
	window.clearSearch =
		function clearSearch(){
			lastAction = "clear";
			$('#result').dataTable().fnClearTable();
			$("#messageResult").val("");
			checkConfirmMessageBeforeExit = false;
			document.getElementById("search-form").reset();	
			$('#save-panel').addClass('hide');
			
			resetSubList();
			GWRDSLib.clearST3Message();
			
			enableDisableButton(false);
			
			lastSearchCriteria = {}
			
			$('#vehiclePlantSearch').focus();
		};
	
	function resetSubList(){
		var $vehicleModelSearch = $('#vehicleModelSearch');
		$vehicleModelSearch.empty();
		$vehicleModelSearch.append('<option value="">&lt;Select&gt;</option>');
	}	
	
	
	function resetRestoreSearchCriteria(){
		$("#vehiclePlantSearch").val("");
		$("#vehiclePlantSearch").change();
		$("#includeExpiredData").prop( 'checked', false );
		$("#includeExpiredData").val(false);
		saveSearchCriteria();
	}
	
	function restoreSearchCriteria(){
		if((lastSearchCriteria.vehiclePlantSearch != undefined && lastSearchCriteria.vehicelPlantSearch != "")){
			$("#vehiclePlantSearch").val(lastSearchCriteria.vehiclePlantSearch);
			$("#vehiclePlantSearch").change();
			$("#vehicleModelSearch").val(lastSearchCriteria.vehicleModelSearch);
			
			$("#includeExpiredData").prop( 'checked', lastSearchCriteria.includeExpiredData );
		}
	}
	
	function saveSearchCriteria(){
		lastSearchCriteria.vehiclePlantSearch = $("#vehiclePlantSearch").val();
		lastSearchCriteria.vehicleModelSearch = $("#vehicleModelSearch").val();
		lastSearchCriteria.includeExpiredData = $("#includeExpiredData").prop("checked");
	}
	
	
	window.addObject =
		function addObject(){
			GWRDSLib.clearST3Message();
			restoreSearchCriteria();
			if(formValidateError($("#search-criteria"))){
				lastAction = 'add';
				
				
				ST3Lib.content.disabled( '#search-field', true );
				enableDisableButton(false);
				
				
				checkConfirmMessageBeforeExit = true;
				$('#result .dataTables_empty').parent().hide();
				$('.dataTables_length').hide();
				$('#result-list-form').attr('action', mappingPath + '/add').attr('_method', 'post');
				var firstRow = false;
				objectEditor.addRow(firstRow);
				moveHScrollBar(0);
				
//				$("#txtUnitModel").removeAttr("disabled");
				$("#data-head-panel").find('input[type=checkbox][name=objectIds]').prop( 'checked', false );
				$("#btnPopupSubsreen").focus();
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
			
			if (count == 1) {
				GWRDSLib.clearST3Message();
				lastAction = 'edit';
				
				checkConfirmMessageBeforeExit = true;
				
				ST3Lib.content.disabled( '#search-field', true );
				enableDisableButton(false);
				
				var tr = checkbox.closest('tr');
				$('#result-list-form').attr('action', mappingPath+'/edit').attr('_method', 'post');
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
						
						var dataTable = $('#result').find("tr:last");
					 	
						var unitModel = dataTable.find("td:eq(2)").find("input").val();
						var unitType = dataTable.find("td:eq(3)").text();
						var priority = dataTable.find("td:eq(4)").text();
						var unitPlant = dataTable.find("td:eq(5)").text();
						
						$("#data-head-panel").find('input[name=unitModel]').val(unitModel);
						$("#data-head-panel").find('input[name=unitType]').val(unitType);
						$("#data-head-panel").find('input[name=priority]').val(priority);
						$("#data-head-panel").find('input[name=unitPlant]').val(unitPlant);
						
						$('#result-list-form').attr('action', mappingPath + '/submitAdd').attr('_method', 'post');
						
//						if($("#txtUnitModel").val() == ''){
//							ST3Lib.message.addError(MSTD1016AERR);
//							ST3Lib.message.show(1);
//							$("#btnPopupSubsreen").focus();
//						}
					} else {
						$("#tcFrom").val($("#hidtcFrom").val());
						$('#result-list-form').attr('action', mappingPath + '/submitEdit').attr('_method', 'post');
					}
					
					var saveForm = $('#result-list-form');
					saveForm.submit();
					
				}
			});
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
					
					if((lastSearchCriteria.vehiclePlantSearch != undefined && lastSearchCriteria.vehiclePlantSearch != "")){
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
		
		
		var obj_prority = "";
		function getPrority(unitPlant,unitModel,unitType,vehiclePlant,vehicleModel){
			$.ajax({
				  method: "POST",
				  async: false,
				  url: mappingPath + "/getPrority",
				  data: { unitPlant: unitPlant, 
					  unitModel: unitModel,
					  unitType: unitType,
					  vehiclePlant: vehiclePlant,
					  vehicleModel: vehicleModel
					 	}
				})
				.done(function( data ) {
					if(data != null && data != '') {
						obj_prority =  data.objectsInfoList[0];
					}else{
						obj_prority = "";
					}
				});
		}
		
		window.popupSubsreen =
			function popupSubsreen(){
				var vehiclePlant = $("#vehiclePlantSearch").val();
				var vehicleModel = $("#vehicleModelSearch").val();
				GWRDSLib.dialog.open("subScreenUnitModelAndUnitPlantDialog", _rootPath + "/master/subScreenUnitModelAndUnitPlant?vehiclePlant="+vehiclePlant+"&vehicleModel="+vehicleModel, "WBW04131 : Sub screen Unit Model - Unit Plant" , 805, 480);
		};
		
		window.__callbackData =
			function __callbackData() {
				var emp = GWRDSLib.dialog.$output;
				
				var vehiclePlant = $("#vehiclePlantSearch").val();
				var vehicleModel = $("#vehicleModelSearch").val();
				var unitPlant = emp.unitPlant;
				var unitModel = emp.unitModel;
				var unitType = emp.unitType;
				
				getPrority(unitPlant,unitModel,unitType,vehiclePlant,vehicleModel);
				addRowCallBackData(unitPlant,unitModel,unitType,vehiclePlant,vehicleModel,obj_prority);
		};
		
		function addRowCallBackData(unitPlant,unitModel,unitType,vehiclePlant,vehicleModel,prority){
			var dataTable = $('#result').find("tr:last");
		 	
//			dataTable.find("td:eq(2)").append(unitModel);
			$("#txtUnitModel").val(unitModel);
			dataTable.find("td:eq(3)").html(unitType);
			dataTable.find("td:eq(4)").html(obj_prority);
			dataTable.find("td:eq(5)").html(unitPlant);
		}
	
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
			
	window.moveHScrollBar =
		function moveHScrollBar(value){
			$('#search-result .dataTable-wrapper').scrollLeft(0);
		};
		
	window.moveToLastSelectId =
		function moveToLastSelectId(tableSelector, id){
			return ST3Lib.dataTable.moveToLastSelectId(tableSelector, id);
		};
		
	window.getSelectId =
		function getSelectId(tableSelector, index){
			return ST3Lib.dataTable.getSelectId(tableSelector, index);
		};	
	
	window.searchValidateError =
	function searchValidateError(){
		return;
	};
	
	window.saveValidateError =
		function saveValidateError(){
			return;
		};
	
	
	function enableDisableButton(haveData){
//		alert("HaveData : "+haveData +"  lastAction : "+lastAction);
		if(lastAction=='add' || lastAction=='edit'){
			ST3Lib.validate.disabledButtonSpecify('#WBW04130Search', true);
			ST3Lib.validate.disabledButtonSpecify('#WBW04130Clear', true);
			
			ST3Lib.validate.disabledButtonSpecify('#WBW04130Add', true);
			ST3Lib.validate.disabledButtonSpecify('#WBW04130Edit', true);
			ST3Lib.validate.disabledButtonSpecify('#WBW04130Delete', true);
		}else{
			ST3Lib.validate.disabledButtonSpecify('#WBW04130Search', false);
			ST3Lib.validate.disabledButtonSpecify('#WBW04130Clear', false);
			
			ST3Lib.validate.disabledButtonSpecify('#WBW04130Add', false);
			if(haveData){
				ST3Lib.validate.disabledButtonSpecify('#WBW04130Edit', false);
				ST3Lib.validate.disabledButtonSpecify('#WBW04130Delete', false);
			}else{
				ST3Lib.validate.disabledButtonSpecify('#WBW04130Edit', true);
				ST3Lib.validate.disabledButtonSpecify('#WBW04130Delete', true);
			}
		}
	}
		
})(ST3Lib.$);