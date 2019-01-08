(function($){
	var checkConfirmMessageBeforeExit = false;
	var lastSearchCriteria = {};
	var lastAction = '';
	var appId = '';
	var appId2 = '';
	var loadingUpload = null;
	
	window.onbeforeunload = onbeforeunloadGWRDS;
	
	function onbeforeunloadGWRDS (event) {
		if(checkConfirmMessageBeforeExit){
			if(appId != ''){
	        	$.ajax({
				 	method: "POST",
		            url: mappingPath + '/updateStatusInterruptOfLogUpload',
		            dataType: 'json',
		            async : false,
		            data: {
		            	appIdStr : appId,
		            	appId2Str : appId2
		            },
		            success: function (data) {
		            }
			 	});
	        	$('#WBW04210Browse').val('');
	        	appId = '';
	        	appId2 = '';
	        	if(loadingUpload){
	        		disableButtonOfBodySection(true);
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
		$('#ui-accordion-menu-header-2').click();
		$('#ui-accordion-menu-header-2').attr('tabindex','-1');
		
		disableButtonOfBodySection(true);
		
		$('#getsudoMonthSearch').focus();
		getLabelDetail();
		var imagepath = _imagePath + "icons/cal.gif";
		
		$('#getsudoMonthSearch').MonthPicker({
	        MonthFormat: 'M-y', // Short month name, Full year.
	        Button: '<img class="getsudoMonth" title="Select Month" src='+imagepath+ '/>',
	        AltFormat: 'M-y', //result	  
	        ShowIcon: true,
	        title: 'Select date',
	        onSelect: function(){
				$(this).focus();	
			},
			OnAfterChooseMonth: function(){
				$('#getsudoMonthSearch').focus();
			}
	    });
		
		validateDateMMMYY(document.getElementById('getsudoMonthSearch'));
	});
	
	window.clickSearch = 
		function clickSearch(){
			lastAction = 'search';
			
			//Clear message bar
			GWRDSLib.clearST3Message();
			
			//Reset operation panel
			$('#WBW04210Browse').val('');
			
			//Reset result panel
			var dataTable = $( '#result' ).dataTable();
			dataTable.fnClearTable(true);
			$("#label_GetsudoMonthSearch, #label_TimingSearch").text('-');
			disableButtonOfBodySection(true);

			var searchForm = $('#search-form');
			$('#search-form').submit();
		};
		
	window.searchFinish =
		function searchFinish(data, loading) {
			
			saveSearchCriteria();
			
			if(data  && typeof data != 'undefined'){
				if(data.objectForm != null && data.objectForm.updateKeySet != null){
					$("#updateKeySet").val(data.objectForm.updateKeySet);
				}
				
				checkConfirmMessageBeforeExit = false;
				if(data.errorMessages != "" && typeof data.errorMessages != 'undefined') {
	    			if (data.focusId != "" && typeof data.focusId != 'undefined') {
	    				searchNotFoundMode();
	    				setFormFocus("getsudoMonthSearch");
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
	    				processAjaxDataList(data);
	    			}
	    			loading.close();
	    		}
			}
			disableButtonOfBodySection(false);
			loading.close();
		};
	
		
	window.processAjaxDataList =
		function processAjaxDataList(datas){
			var dataList = datas.objectsInfoList;
			if(dataList){
				$('#result .dataTables_empty').parent().remove();
				var dataTable = $('#result').dataTable();
				dataTable.fnClearTable(false);//no redraw
				
				var setting = dataTable.fnSettings();
				setting.aoColumns[0].sClass = 'col1 rownum';
				setting.aoColumns[1].sClass = 'col2';
				setting.aoColumns[2].sClass = 'col3';
				setting.aoColumns[3].sClass = 'col4';
				setting.aoColumns[4].sClass = 'col5';
				setting.aoColumns[5].sClass = 'col6';
								
				var tempDiv = $('<div/>');
				var arrayData = [];
				for(var j = 0; j < dataList.length; j++) {
					var data = dataList[j];
					if ( data ) {
						var serialize = tempDiv.text(JSON.stringify(data)).html();
						arrayData.push([ 
						                j+1
									    , data.getsudoMonth || ''
									    , data.timing || ''
									    , data.message || ''
									    , data.updateBy || ''
									    , data.updateDateDisp || ''
									]);
					}
				}
				dataTable.fnAddData(arrayData);
				getLabelDetail();
			}
		};			
		
	window.clearSearch =
		function clearSearch(){
			lastAction = "clear";
			$('#result').dataTable().fnClearTable();
			checkConfirmMessageBeforeExit = false;
			document.getElementById("search-form").reset();	
			
			GWRDSLib.clearST3Message();
			
			$("#getsudoMonth").val('-');
			$("#timing").val('-');
			$('#WBW04210Browse').val('');
			lastSearchCriteria = {}
			getLabelDetail();
			disableButtonOfBodySection(true);
			$("#getsudoMonthSearch").focus();
		};	
	
	function getLabelDetail(){
		$("#label_GetsudoMonthSearch").text(lastSearchCriteria.getsudoMonthSearch || '-');
		$("#label_TimingSearch").text(lastSearchCriteria.timingSearch || '-');
		
	}
		
	function searchNotFoundMode(){
		ST3Lib.content.disabled('#search-criteria', false);
		$( '#result' ).dataTable().fnClearTable(true);
		
		$("#label_GetsudoMonthSearch").text($("#getsudoMonthSearch").val());
		$("#label_TimingSearch").text($("#timingSearch").val());
	}
	
	function searchFoundMode(){
		ST3Lib.content.disabled('#search-criteria', false);
	}
	
	function saveSearchCriteria(){
		lastSearchCriteria.getsudoMonthSearch = $("#getsudoMonthSearch").val();
		lastSearchCriteria.timingSearch = $("#timingSearch").val();
	}
	
	function restoreSearchCriteria(){
		$("#getsudoMonthSearch").val(lastSearchCriteria.getsudoMonthSearch);
		$("#timingSearch").val(lastSearchCriteria.timingSearch);
	}
	
	window.doDownload =
		function doDownload(){
			GWRDSLib.clearST3Message();
			var searchForm = $('#search-form');
			$('#WBW04210Browse').val('');
			restoreSearchCriteria();
			if (formValidateError(searchForm)) {
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
				            	disableButtonOfBodySection(false);
				        	}else {
				        		if(objSerialize.infoMessages.length > 0) {
									//ST3Lib.message.addInfo(objSerialize.infoMessages);
									//ST3Lib.message.show(1);
									
									document.getElementById("upload-form").reset();
									
					            	//loading.close();
					            	disableButtonOfBodySection(false);
								}
				        	}
						}
						appId = '';
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
			}
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
							disableButtonOfBodySection(false);
			        	}else {
			        		checkDownloadStatus(loadingDownload, appIdStr);
			        	}
					})
					.error(function() {
						loadingDownload.close();
						loadingDownload = null;
						appId = '';
						disableButtonOfBodySection(false);
					});
			}, downloadTimingCheckStatus);
		}
				
		window.doUpload =
			function doUpload(){
				GWRDSLib.clearST3Message();
				var searchForm = $('#search-form');
				var uploadForm = $('#upload-form');
				restoreSearchCriteria();
				if (formValidateError(searchForm) && formValidateError(uploadForm)) {
					checkConfirmMessageBeforeExit = true;
					var loadingTarget = "#screen-panel";
					loadingUpload = ST3Lib.dialog.loading(loadingTarget);
					ST3Lib.content.disabled( '#updateKeySet', false );
					uploadForm.ajaxForm({ 
			            url: mappingPath+'/upload', 
			            type: 'POST',
			            data: { getsudoMonthSearch : $("#label_GetsudoMonthSearch").text(), 
			            		timingSearch : $("#label_TimingSearch").text(),
			            		updateKeySet : $("#updateKeySet").val()},
			            dataType: 'text',
			            success: function(data) {
			            	if(data  && typeof data != 'undefined'){	
			            		var objSerialize = JSON.parse(data);
			            		
				        		if(objSerialize.errorMessages != "" && typeof objSerialize.errorMessages != 'undefined') {
					        		ST3Lib.message.addError(objSerialize.errorMessages);
									ST3Lib.message.show(1);

					            	$('#WBW04210Upload').removeAttr('disabled');
					            	loadingUpload.close();
					            	loadingUpload = null;
					            	disableButtonOfBodySection(false);
					            	checkConfirmMessageBeforeExit = false;
					        	}else {
					        		if(objSerialize.infoMessages.length > 0) {
					        			appId = objSerialize.appId;
					        			appId2 = objSerialize.appId2;
					        			checkUploadStatus(loadingUpload, appId, appId2);             
									}
					        	}
			        		}
			            },
			        	error: function() {
			        		$('#WBW04210Upload').val('');
			        		$('#WBW04210Upload').removeAttr('disabled');
			        		loadingUpload.close();
			        		loadingUpload = null;
			        		disableButtonOfBodySection(false);
			        		checkConfirmMessageBeforeExit = false;
			        	}
			        }).submit();	
				}
			};	
				
				
		function checkUploadStatus(loadingUpload, appId, appId2){
			setTimeout(function(){
				$.ajax({
					  method: "POST",
					  url: mappingPath+'/checkStatusOfLogUpload',
					  async: false,
					  data: { appId : appId, 
						      appId2 : appId2,
						      getsudoMonthSearch : $('#getsudoMonthSearch').val(),
						      timingSearch : $('#timingSearch').val()
						    }
					})
					.done(function( data1 ) {
						var objPayload = JSON.parse(data1);						            		
						if(objPayload.status == "OK" && typeof objPayload.status != 'undefined') {      			
							ST3Lib.message.clear();
							if (objPayload.errorMessages != "" && typeof objPayload.errorMessages != 'undefined') {
								ST3Lib.message.addError(objPayload.errorMessages);
								ST3Lib.message.show(1);
							} else if (objPayload.infoMessages.length > 0) {
								ST3Lib.message.addInfo(objPayload.infoMessages);
								ST3Lib.message.show(1);
							}
							
							var dataForm = objPayload.objectForm;
							$.each(dataForm, function(item, value){
		    		            $("#"+item).val(value);
		    		        });
							//refresh search data
							processAjaxDataList(objPayload);
							searchFoundMode();
							
							document.getElementById("upload-form").reset();													
							$('#WBW04210Upload').removeAttr('disabled');
			            	
			            	checkConfirmMessageBeforeExit = false;
			            	$('#WBW04210Browse').val('');
			            	loadingUpload.close();
			            	loadingUpload = null;
			            	disableButtonOfBodySection(false);
			        	}else {
			        		$('#WBW04210Browse').val('');
			        		checkConfirmMessageBeforeExit = true;
			        		checkUploadStatus(loadingUpload, appId,appId2);
			        	}
					})
					.error(function() {
						$('#WBW04210Browse').val('');
						loadingUpload.close();
			          	loadingUpload = null;
			          	disableButtonOfBodySection(false);
			          	checkConfirmMessageBeforeExit = false;
					});
			}, uploadTimingCheckStatus);
		}
		
		function disableButtonOfBodySection(disableFlag){
			ST3Lib.validate.disabledButtonSpecify( '#WBW04210Download', disableFlag );
			ST3Lib.validate.disabledButtonSpecify( '#WBW04210Upload', disableFlag );
			ST3Lib.validate.disabledButtonSpecify( '#WBW04210Browse', disableFlag );
		}
		
})(ST3Lib.$);