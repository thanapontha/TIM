(function($){
	var appId = '';
	var lastSearchCriteria = {};
	var lastAction = '';
	var UPDATE_KEYS_SEPERATER = '|';
	
	var checkConfirmMessageBeforeExit = false;
	var loadingUpload = null;

	window.onbeforeunload = onbeforeunloadGWRDS;
	
	var selectedCheckbox;
	
	function onbeforeunloadGWRDS (event) {
		if(checkConfirmMessageBeforeExit){
			if(appId != ''){
	        	$.ajax({
				 	method: "POST",
		            url: mappingPath + '/updateStatusInterruptOfLogUpload',
		            dataType: 'json',
		            async : false,
		            data: {
		            	appIdStr : appId
		            },
		            success: function (data) {
		            }
			 	});
	        	$('#WBW01150Browse').val('');
	        	appId = '';
	        	if(loadingUpload){
	        		selectedCheckbox = null;
	        		enableButtonBaseOnStatus();
	        		loadingUpload.close();
	            	loadingUpload = null;
	        	}
	        	return "";
			}else{
				return MSTD0003ACFM;
			}
		}
	};
	
	$(function(){
		enableButtonBaseOnStatus();
		
		$('#getsudoMonthSearch').focus();
		
		$('#result').on('click', 'input:checkbox', function(ev){
			
			var checkbox = $( this );
			if (checkbox.is(':disabled')) return;
			$('#search-form').find('input:hidden[name=updateKeySet]').val(checkbox.val());
			
			selectedCheckbox = checkbox;
			enableButtonBaseOnStatus(selectedCheckbox);
			$('#WBW01150Browse').val('');
		});
		
		var imagepath = _imagePath + "icons/cal.gif";		
		$('#getsudoMonthSearch').MonthPicker({
	        MonthFormat: 'M-y', // Short month name, Full year.
	        Button: '<img class="getsudoMonth" title="Select Month" src='+imagepath+ '/>',
	        AltFormat: 'M-y', //result	        
	        onSelect: function(){
				$(this).focus();
			},
			OnAfterChooseMonth: function(){
				$('#getsudoMonthSearch').focus();
				if(prevGetsudoSearch != $('#getsudoMonthSearch').val()){
					prevGetsudoSearch = $('#getsudoMonthSearch').val();
					$("#vehiclePlantSearch").val('');
				}
			}
	    });	
		
		var prevGetsudoSearch = $('#getsudoMonthSearch').val();
		
		validateDateMMMYY(document.getElementById('getsudoMonthSearch'));
		
		$("#getsudoMonthSearch").change(function() {
			prevGetsudoSearch = $('#getsudoMonthSearch').val();
			$("#vehiclePlantSearch").val('');
		});
	});

	window.clickSearch = 
		function clickSearch(){
			lastAction = 'search';
			GWRDSLib.clearST3Message();
			
			$('#WBW01150Browse').val('');
			selectedCheckbox = null;
    		enableButtonBaseOnStatus();
    		clearResultSearchCriteria();
    		$('#result').dataTable().fnClearTable();
			var searchForm = $('#search-form');
			$('#search-form').submit();
		}
	
	function clearResultSearchCriteria() {
		$('#getsudoMonthCriteria').text("-");
		$('#timingCriteria').text("-");
		$('#vehiclePlantCriteria').text("-");
	}
	
	window.searchFinish =
		function searchFinish(data, loading) {
			saveSearchCriteria();
			
			if(data  && typeof data != 'undefined'){
				checkConfirmMessageBeforeExit = false;
				if(data.errorMessages != "" && typeof data.errorMessages != 'undefined') {
	    			if (data.focusId != "" && typeof data.focusId != 'undefined') {
	    				searchNotFoundMode();
	    				setFormFocus("getsudoMonthSearch");
	    			}
	    		} else {
	    			dataForm = data.objectForm;	    			
	    			if (dataForm != null) {
	    				$.each(dataForm, function(item, value){
	    		            $("#"+item).val(value);
	    		            //console.log($("#"+item).val(value));
	    		        });
	    				
	    				generateDataList(data);
	    				enableButtonBaseOnStatus();
	    			}
	    		}
			}
			loading.close();
		};
		
	window.generateDataList =
		function generateDataList(datas){
		var dataList = datas.objectsInfoList;
			if(dataList){
				var objectForm  = datas.objectForm;
				$('#result .dataTables_empty').parent().remove();
				var dataTable = $('#result').dataTable();
				dataTable.fnClearTable(true);
				
				var setting = dataTable.fnSettings();
				setting.aoColumns[0].sClass = 'col1';
				setting.aoColumns[1].sClass = 'col2';
				setting.aoColumns[2].sClass = 'col3';
				setting.aoColumns[3].sClass = 'col4';
				setting.aoColumns[4].sClass = 'col5';
				setting.aoColumns[5].sClass = 'col6';
				setting.aoColumns[6].sClass = 'col7';
								
				var tempDiv = $('<div/>');
				var i = 0;
				var arrayData = [];
				var processor = setInterval(function(){
				var processPerTick = 5;
				for(var j = 0; j < processPerTick; ++j) {
					var data = dataList[i++];
					if ( data ) {
						var serialize = tempDiv.text(JSON.stringify(data)).html();
					
						arrayData.push([ 
										 '<input class="checkbox" type="checkbox" name="objectIds" value="' + 
										 	data.updateDateKey + UPDATE_KEYS_SEPERATER +
										 	data.status + UPDATE_KEYS_SEPERATER +
										 	data.workSheetStatus + UPDATE_KEYS_SEPERATER +
										 	data.confirmedStatus + UPDATE_KEYS_SEPERATER +
										 	objectForm.getsudoMonthSearch + UPDATE_KEYS_SEPERATER +
										 	objectForm.timingSearch + UPDATE_KEYS_SEPERATER +
										 	objectForm.vehiclePlantSearch + UPDATE_KEYS_SEPERATER +
										 	data.vehicleModel + UPDATE_KEYS_SEPERATER +
										 	data.updateDate + UPDATE_KEYS_SEPERATER +
										 	data.appId + UPDATE_KEYS_SEPERATER +
										 	data.l1UpdateDate+
										 	 '" /><div class="serialize" style="display:none">' + serialize + '</div>' 
									    , data.no
									    , data.vehicleModel
									    , data.statusDesc
									    , data.updateBy
									    , data.updateDate
									    , data.completeStatus
									]);
					}else{
						processPerTick = 0;
						clearInterval(processor);
						dataTable.fnAddData(arrayData);
						GWRDSLib.selectOneCheckboxMode('#result');
						$('#result input:checkbox:first').focus(); 
						break;
					}
				}
				},1 );
			}
		};		
		
	function searchNotFoundMode(){
		ST3Lib.content.disabled('#search-criteria', false);
		ST3Lib.validate.disabledButton($('#search-panel'), false);
		$('#result').dataTable().fnClearTable();
	}
	
	function searchFoundMode(){			
		ST3Lib.content.disabled('#search-result', true);
		ST3Lib.content.disabled('#search-criteria', false);
		
		ST3Lib.validate.disabledButton($('#search-panel'), false);
	}	
	
	function enableButtonBaseOnStatus(selectedcheckbox){
		var selectedRow;
		var checked;
		if(selectedcheckbox){
			selectedRow = selectedcheckbox.val();
			checked = selectedcheckbox.is(':checked');
		}
		
		if(selectedRow && checked){
			var keys = selectedRow.split(UPDATE_KEYS_SEPERATER);
			var status = keys[1];
			var confirmedStatus = keys[3];
			ST3Lib.validate.disabledButtonSpecify( '#WBW01150Download', false );
			
			if(status === '-' || status ==='MP' || status === 'SM' || status === 'PC' || status ==='CP'){
				ST3Lib.validate.disabledButtonSpecify( '#WBW01150Upload', true );
				ST3Lib.validate.disabledButtonSpecify( '#WBW01150Browse', true );
				ST3Lib.validate.disabledButtonSpecify( '#WBW01150Reset', true );
			}else{
				ST3Lib.validate.disabledButtonSpecify( '#WBW01150Upload', false );
				ST3Lib.validate.disabledButtonSpecify( '#WBW01150Browse', false );
				ST3Lib.validate.disabledButtonSpecify( '#WBW01150Reset', false );
			}
			
			if(status ==='WE' || status === 'WS' || status === 'WS-LT0'){
				ST3Lib.validate.disabledButtonSpecify('#WBW01150Log', false);
			}else{
				ST3Lib.validate.disabledButtonSpecify('#WBW01150Log', true);
			}
			if(status ==='K-FX' || status === 'R-FX' || status === 'S-FX' || status === 'WS-LT0'){
				if(confirmedStatus === 'YES'){
					ST3Lib.validate.disabledButtonSpecify('#WBW01150Complete', false);
				}else{
					ST3Lib.validate.disabledButtonSpecify('#WBW01150Complete', true);
				}				
			}else{
				ST3Lib.validate.disabledButtonSpecify('#WBW01150Complete', true);
			}
			
			if(status ==='CP' &&  $("#getsudoMonthSearch").val() === currentMonthYear){
				ST3Lib.validate.disabledButtonSpecify('#WBW01150CompletedReset', false);
			}else{
				ST3Lib.validate.disabledButtonSpecify('#WBW01150CompletedReset', true);
			}
		}else{
			ST3Lib.validate.disabledButtonSpecify( '#WBW01150Download', true );
			ST3Lib.validate.disabledButtonSpecify( '#WBW01150Upload', true );
			ST3Lib.validate.disabledButtonSpecify('#WBW01150Browse', true);
			ST3Lib.validate.disabledButtonSpecify('#WBW01150Log', true);
			ST3Lib.validate.disabledButtonSpecify('#WBW01150Complete', true);
			ST3Lib.validate.disabledButtonSpecify( '#WBW01150Reset', true );
			ST3Lib.validate.disabledButtonSpecify( '#WBW01150CompletedReset', true );	
		}
	}

	
	window.clearSearch =
		function clearSearch(){
			checkConfirmMessageBeforeExit = false;
			document.getElementById("search-form").reset();	
			selectedCheckbox = null;
			
			searchNotFoundMode();
			enableButtonBaseOnStatus();
			clearResultSearchCriteria();
			$('#WBW01150Browse').val('');
			$("#search-form").find(".getsudoMonth").css("display", "");
			$('#getsudoMonthSearch').focus();
			GWRDSLib.clearST3Message();
			lastSearchCriteria = {};
		};
	
	function restoreSearchCriteria(){
		$("#getsudoMonthSearch").val(lastSearchCriteria.getsudoMonthSearch);
		$("#timingSearch").val(lastSearchCriteria.timingSearch);
		$("#vehiclePlantSearch").val(lastSearchCriteria.vehiclePlantSearch);
	}
	
	function saveSearchCriteria(){
		lastSearchCriteria.getsudoMonthSearch = $("#getsudoMonthSearch").val();
		lastSearchCriteria.timingSearch = $("#timingSearch").val();
		lastSearchCriteria.vehiclePlantSearch = $("#vehiclePlantSearch").val();
		
		$('#getsudoMonthCriteria').text(lastSearchCriteria.getsudoMonthSearch);
		$('#timingCriteria').text(lastSearchCriteria.timingSearch);
		$('#vehiclePlantCriteria').text(lastSearchCriteria.vehiclePlantSearch);
	}
			
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

	
	window.doUpload =
		function doUpload(){
			GWRDSLib.clearST3Message();
			restoreSearchCriteria();
			var searchForm = $('#search-form');
			var uploadForm = $('#upload-form');
			if (formValidateError(searchForm) && formValidateError(uploadForm)) {
				
				var selectedUpdateKeySet = $("#updateKeySet").val();
				if(selectedUpdateKeySet){
					var allowUpload = "YES";
					var keys = selectedUpdateKeySet.split(UPDATE_KEYS_SEPERATER);
					var workSheetStatus = keys[2];
					if(workSheetStatus === 'WS'){
						var updateDtPrev = keys[8];
						MBW00005ACFM_Upload = MBW00005ACFM_Upload.replace("{Date_Time}", updateDtPrev);
						ST3Lib.dialog.confirm(MBW00005ACFM_Upload, 'MBW00005ACFM', function(ret){
							if (!ret) {
								return;
							}else{
								uploadWorksheet();
							}
						});
					}else{
						uploadWorksheet();
					}					
				}
			}
		};	
		
	function uploadWorksheet(){
		checkConfirmMessageBeforeExit = true;
		var loadingTarget = "#screen-panel";
		loadingUpload = ST3Lib.dialog.loading(loadingTarget);
		
		$('#upload-form').ajaxForm({ 
            url: mappingPath+'/upload', 
            type: 'POST',
            data: { getsudoMonthSearch : $("#getsudoMonthSearch").val(), 
            		timingSearch : $("#timingSearch").val(),
            	    vehiclePlantSearch : $("#vehiclePlantSearch").val(),
            	    updateKeySet : $("#updateKeySet").val()},
            dataType: 'text',
            success: function(data) {
            	if(data  && typeof data != 'undefined'){	
            		var objSerialize = JSON.parse(data);
            		
	        		if(objSerialize.errorMessages != "" && typeof objSerialize.errorMessages != 'undefined') {
		        		ST3Lib.message.addError(objSerialize.errorMessages);
						ST3Lib.message.show(1);

		            	$('#WBW01150Upload').removeAttr('disabled');
		            	loadingUpload.close();
		            	loadingUpload = null;
		            	enableButtonBaseOnStatus(selectedCheckbox);
		            	checkConfirmMessageBeforeExit = false;
		        	}else {
		        		if(objSerialize.infoMessages.length > 0) {
		        			appId = objSerialize.appId;
		        			checkUploadStatus(loadingUpload,appId);
						}
		        	}
        		}
            },
        	error: function() {
        		$('#WBW01150Browse').val('');
        		$('#WBW01150Upload').removeAttr('disabled');
        		loadingUpload.close();
            	loadingUpload = null;
            	enableButtonBaseOnStatus(selectedCheckbox);
            	checkConfirmMessageBeforeExit = false;
        	}
        }).submit();
	}
	
	function checkUploadStatus(loadingUpload, appId){
		setTimeout(function(){
			$.ajax({
				  method: "POST",
				  url: mappingPath+'/checkStatusOfLogUpload',
				  async: false,
				  data: { appId : appId,
					  	getsudoMonthSearch : $("#getsudoMonthSearch").val(), 
	            		timingSearch : $("#timingSearch").val(),
	            	    vehiclePlantSearch : $("#vehiclePlantSearch").val(),
	            	    updateKeySet : $("#updateKeySet").val() 
				  }
				})
				.done(function( data1 ) {
					var objPayload = JSON.parse(data1);						            		
					if(objPayload.status == "OK" && typeof objPayload.status != 'undefined') {      			
						ST3Lib.message.clear();
						if(objPayload.errorMessages != "" && typeof objPayload.errorMessages != 'undefined') {
			        		ST3Lib.message.addError(objPayload.errorMessages);
							ST3Lib.message.show(1);
			        	}else if(objPayload.infoMessages.length > 0) {
							ST3Lib.message.addInfo(objPayload.infoMessages);
							ST3Lib.message.show(1);						            	
						}
						document.getElementById("upload-form").reset();													
						$('#WBW01150Upload').removeAttr('disabled');
		            	
		            	checkConfirmMessageBeforeExit = false;
		            	$('#WBW01150Browse').val('');
		            	loadingUpload.close();
		            	loadingUpload = null;
		            	
		            	generateDataList(objPayload);
		            	selectedCheckbox = null;
		            	enableButtonBaseOnStatus();
		        	}else {
		        		$('#WBW01150Browse').val('');
		        		checkConfirmMessageBeforeExit = true;
		        		checkUploadStatus(loadingUpload, appId);
		        	}
				})
				.error(function() {
					$('#WBW01150Browse').val('');
					loadingUpload.close();
		          	loadingUpload = null;
		          	enableButtonBaseOnStatus(selectedCheckbox);
		          	checkConfirmMessageBeforeExit = false;
				});
		}, uploadTimingCheckStatus);
	}
		
	window.doDownload =
		function doDownload(){
			GWRDSLib.clearST3Message();
			restoreSearchCriteria();
			var searchForm = $('#search-form');
			$('#WBW01150Browse').val('');
			//if (formValidateError(searchForm)) {
				var loadingTarget = "#screen-panel";
				var loading = ST3Lib.dialog.loading(loadingTarget);
			
				//generate AppId for download operation
				$.ajax({
					  method: "POST",
					  url: mappingPath+'/genAppIdOfLogDownload',
					  async : false,
					  data: {}
					})
					.done(function( data ) {
						appId = data.appId;
					})
					.error(function() {});
				
				
				var ajax = searchForm.attr('ajax');
				var action = searchForm.attr('action');
				lastAction = 'download';
				$('#downloadIframe').off('load').on('load', function(){
					try{
						var text = $('#downloadIframe').contents().text();
						if (text) {
							var objSerialize = JSON.parse(text);
							ST3Lib.message.clear();
							if(objSerialize.errorMessages != "" && typeof objSerialize.errorMessages != 'undefined') {
				        		ST3Lib.message.addError(objSerialize.errorMessages);
								ST3Lib.message.show(1);
								
				            	loading.close();
				        	}else {
				        		if(objSerialize.infoMessages.length > 0) {
									//ST3Lib.message.addInfo(objSerialize.infoMessages);
									//ST3Lib.message.show(1);
									
									document.getElementById("upload-form").reset();
									
					            	//loading.close();
								}
				        	}
						}
						appId = '';
						enableButtonBaseOnStatus(selectedCheckbox);
						//loading.close();
					}catch(e){}
				});
				
				//check data on iframe to get error message
				searchForm.attr('action', mappingPath + '/download?appIdStr='+appId).attr('target', 'downloadIframe')
					.removeAttr('ajax')
					.submit();

				searchForm.attr('ajax', ajax).removeAttr('target').attr('action', action);
				$('#getsudoMonthSearch').focus();
				checkDownloadStatus(loading, appId);
			//}
		};
		
		function checkDownloadStatus(loadingDownload, appIdStr){
			setTimeout(function(){
				$.ajax({
					  method: "POST",
					  url: mappingPath+'/checkStatusOfLogDownload',
					  async: false,
					  data: { appIdStr : appIdStr }
					})
					.done(function( data1 ) {
						var objPayload = JSON.parse(data1);						            		
						if(objPayload.status == "OK" && typeof objPayload.status != 'undefined') {      			
							if(objPayload.errorMessages != "" && typeof objPayload.errorMessages != 'undefined') {
								ST3Lib.message.clear();
				        		ST3Lib.message.addError(objPayload.errorMessages);
								ST3Lib.message.show(1);
				        	}else if(objPayload.infoMessages.length > 0) {
								//ST3Lib.message.addInfo(objPayload.infoMessages);
								//ST3Lib.message.show(1);						            	
							}
							document.getElementById("upload-form").reset();	
							loadingDownload.close();
							loadingDownload = null;
							appId = '';
							enableButtonBaseOnStatus(selectedCheckbox);
			        	}else {
			        		checkDownloadStatus(loadingDownload, appIdStr);
			        	}
					})
					.error(function() {
						loadingDownload.close();
						loadingDownload = null;
						appId = '';
						enableButtonBaseOnStatus(selectedCheckbox);
					});
			}, downloadTimingCheckStatus);
		}
		
	window.doComplete = 
		function doComplete(){
			$('#WBW01150Browse').val('');
			ST3Lib.dialog.confirm(MBW00004ACFM_Complete, 'MBW00004ACFM', function(ret){
				if (!ret) {
					return;
				}else{
					GWRDSLib.clearST3Message();
					
					restoreSearchCriteria();
					
					var loadingTarget = "#screen-panel";
					var loading = ST3Lib.dialog.loading(loadingTarget);
					
					ST3Lib.content.disabled( '#updateKeySet', false );
					
					$.ajax({
						  method: "POST",
						  url: mappingPath+'/completeData',
						  data: { getsudoMonthSearch : $("#getsudoMonthSearch").val(), 
			            		timingSearch : $("#timingSearch").val(),
			            	    vehiclePlantSearch : $("#vehiclePlantSearch").val(),
			            	    updateKeySet : $("#updateKeySet").val() }
						})
						.done(function( datas ) {
							if(datas.status === 'OK'){
								checkConfirmMessageBeforeExit = false;
								
								if(datas.infoMessages.length > 0) {
									 ST3Lib.message.addInfo(datas.infoMessages);
									 ST3Lib.message.show(1);
								}
								
								dataForm = datas.objectForm;
								if (dataForm!=null) { 
									$.each(dataForm, function(item, value){
							            $("#"+item).val(value);
							        });  
								}
								generateDataList(datas);

								selectedCheckbox = null;
								enableButtonBaseOnStatus();
							}else{
								if(datas.errorMessage) {
									ST3Lib.message.addError(datas.errorMessage);
									ST3Lib.message.show(1);
								}else if(datas.errorMessages != null && datas.errorMessages.length > 0) {
									ST3Lib.message.addError(datas.errorMessages);
									ST3Lib.message.show(1);
								}
								if (datas.focusId != "" && typeof datas.focusId != 'undefined') {
									setFormFocus(datas.focusId);
								}else{
									$('#result input:text:first').focus();
								}
								enableButtonBaseOnStatus(selectedCheckbox);
							}
							if (loading) {
								loading.close();
							}
						})
						.error(function() {							
							enableButtonBaseOnStatus(selectedCheckbox);
							if (loading) {
								loading.close();
							}
						});
					
				}
			});
		};

	window.doReset = 
		function doReset(){
			$('#WBW01150Browse').val('');
			ST3Lib.dialog.confirm(MBW00004ACFM_Reset, 'MBW00004ACFM', function(ret){
				if (!ret) {
					return;
				}else{
					GWRDSLib.clearST3Message();
					
					restoreSearchCriteria();
					
					var loadingTarget = "#screen-panel";
					var loading = ST3Lib.dialog.loading(loadingTarget);
					
					ST3Lib.content.disabled( '#updateKeySet', false );
					
					$.ajax({
						  method: "POST",
						  url: mappingPath+'/resetData',
						  data: { getsudoMonthSearch : $("#getsudoMonthSearch").val(), 
			            		timingSearch : $("#timingSearch").val(),
			            	    vehiclePlantSearch : $("#vehiclePlantSearch").val(),
			            	    updateKeySet : $("#updateKeySet").val() }
						})
						.done(function( datas ) {
							if(datas.status === 'OK'){
								checkConfirmMessageBeforeExit = false;
								
								if(datas.infoMessages.length > 0) {
									 ST3Lib.message.addInfo(datas.infoMessages);
									 ST3Lib.message.show(1);
								}
								
								dataForm = datas.objectForm;
								if (dataForm!=null) { 
									$.each(dataForm, function(item, value){
							            $("#"+item).val(value);
							        });  
								}
								generateDataList(datas);

								selectedCheckbox = null;
								enableButtonBaseOnStatus();
							}else{
								if(datas.errorMessage) {
									ST3Lib.message.addError(datas.errorMessage);
									ST3Lib.message.show(1);
								}else if(datas.errorMessages != null && datas.errorMessages.length > 0) {
									ST3Lib.message.addError(datas.errorMessages);
									ST3Lib.message.show(1);
								}
								if (datas.focusId != "" && typeof datas.focusId != 'undefined') {
									setFormFocus(datas.focusId);
								}else{
									$('#result input:text:first').focus();
								}
								enableButtonBaseOnStatus(selectedCheckbox);
							}
							if (loading) {
								loading.close();
							}
						})
						.error(function() {							
							enableButtonBaseOnStatus(selectedCheckbox);
							if (loading) {
								loading.close();
							}
						});
					
				}
			});
		};
		
	window.doCompletedReset = 
		function doCompletedReset(){
			$('#WBW01150Browse').val('');
			ST3Lib.dialog.confirm(MBW00006ACFM_CompleteReset, 'MBW00006ACFM', function(ret){
				if (!ret) {
					return;
				}else{
					GWRDSLib.clearST3Message();
					
					restoreSearchCriteria();
					
					var loadingTarget = "#screen-panel";
					var loading = ST3Lib.dialog.loading(loadingTarget);
					
					ST3Lib.content.disabled( '#updateKeySet', false );
					
					$.ajax({
						  method: "POST",
						  url: mappingPath+'/completedResetData',
						  data: { getsudoMonthSearch : $("#getsudoMonthSearch").val(), 
			            		timingSearch : $("#timingSearch").val(),
			            	    vehiclePlantSearch : $("#vehiclePlantSearch").val(),
			            	    updateKeySet : $("#updateKeySet").val() }
						})
						.done(function( datas ) {
							if(datas.status === 'OK'){
								checkConfirmMessageBeforeExit = false;
								
								if(datas.infoMessages.length > 0) {
									 ST3Lib.message.addInfo(datas.infoMessages);
									 ST3Lib.message.show(1);
								}
								
								dataForm = datas.objectForm;
								if (dataForm!=null) { 
									$.each(dataForm, function(item, value){
							            $("#"+item).val(value);
							        });  
								}
								generateDataList(datas);

								selectedCheckbox = null;
								enableButtonBaseOnStatus();
							}else{
								if(datas.errorMessage) {
									ST3Lib.message.addError(datas.errorMessage);
									ST3Lib.message.show(1);
								}else if(datas.errorMessages != null && datas.errorMessages.length > 0) {
									ST3Lib.message.addError(datas.errorMessages);
									ST3Lib.message.show(1);
								}
								if (datas.focusId != "" && typeof datas.focusId != 'undefined') {
									setFormFocus(datas.focusId);
								}else{
									$('#result input:text:first').focus();
								}
								enableButtonBaseOnStatus(selectedCheckbox);
							}
							if (loading) {
								loading.close();
							}
						})
						.error(function() {							
							enableButtonBaseOnStatus(selectedCheckbox);
							if (loading) {
								loading.close();
							}
						});
					
				}
			});
		};
		
	window.doOpenLog = 
		function doOpenLog(){
		restoreSearchCriteria();
		$('#WBW01150Browse').val('');
		
		var selectedUpdateKeySet = $("#updateKeySet").val();
		if(selectedUpdateKeySet){
			var keys = selectedUpdateKeySet.split(UPDATE_KEYS_SEPERATER);
			var appIdForOpenLog = keys[9];
			var l1UpdateDate = keys[10];
			var jsonParams = {
					"module" : "BW01", 
					"function" : "BBW01170", 
					"appId": appIdForOpenLog,
					"dateFrom" : l1UpdateDate,
					"dateTo" : l1UpdateDate
					};			
			
			GWRDSLib.PageOpen(_rootPath + '/common/logMonitoring', "WST33010", jsonParams);
		}
	}
		
})(ST3Lib.$);