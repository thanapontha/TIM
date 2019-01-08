(function($){		
	var checkConfirmMessageBeforeExit = false;
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
		            	appIdStr : appId
		            },
		            success: function (data) {
		            }
			 	});
	        	$('#WBW04240Browse').val('');
	        	appId = '';
	        	if(loadingUpload){
	        		disabledOperationButton(true);
	        		loadingUpload.close();
	            	loadingUpload = null;
	        	}
	        	return "";
			}else{
				return MSTD0003ACFM;
			}
		}
	};
	var totalLenght = 0;
	var lastSearchCriteria = {};
	var lastAction = '';
	var menuList;
	
	$(function(){
		$('#ui-accordion-menu-header-2').click();
		$('#ui-accordion-menu-header-2').attr('tabindex','-1');
		
		$('#unitPlantSearch').focus();
			
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
			}
	    });
		
		validateDateMMMYY(document.getElementById('getsudoMonthSearch'));
		
		$('#unitPlantSearch').change(function() {
			$.ajax({
				  method: "POST",
				  async : false,
				  url: mappingPath+'/unitPlantSearchChange',
				  data: { unitPlantSearch : $("#unitPlantSearch").val()}
				})
				.done(function( data ) {
					var unitParentLine = $('#unitParentLineSearch');
					var unitSubLine = $("#unitSubLineSearch");
					unitParentLine.empty();
					unitSubLine.empty();
									
					unitParentLine.append('<option value="">&lt;Select&gt;</option>');
					unitSubLine.append('<option value="">&lt;Select&gt;</option>');
					
					if(data){										
						for(var i = 0; i < data.length; ++i) {
							var obj = data[i];						
							unitParentLine.append('<option value="{0}">{1}</option>'.format(obj.stValue, obj.stLabel));
						}
					}
				})
				.error(function() {});
		});
		
		$('#unitParentLineSearch').change(function() {
			$.ajax({
				  method: "POST",
				  async : false,
				  url: mappingPath+'/unitParentLineSearchChange',
				  data: { unitPlantSearch : $("#unitPlantSearch").val(),
					  	  unitParentLineSearch : $("#unitParentLineSearch").val() }
				})
				.done(function( data ) {
					var unitSubLineSearch = $('#unitSubLineSearch');
					unitSubLineSearch.empty();
										
					if(data){				
						unitSubLineSearch.append('<option value="">&lt;Select&gt;</option>');
						for(var i = 0; i < data.length; ++i) {
							var obj = data[i];							
							unitSubLineSearch.append('<option value="{0}">{1}</option>'.format(obj.stValue, obj.stLabel));
						}
					}
				})
				.error(function() {});
		});	
		
		searchNotFoundMode();
	});
	
	window.diableOnEditMode =
		function diableOnEditMode() {
			ST3Lib.content.disabled( '#search-field', true );
			ST3Lib.content.disabled($('#search-panel'), true);
			disabledOperationButton(true);
			$('.getsudoMonth').addClass('hide');
		}

	window.clickSearch = 
		function clickSearch(){
			lastAction = 'search';
			GWRDSLib.clearST3Message();
			$('#WBW04240Browse').val('');
			clearTableRowAndColumn();
			$('#search-criteria-label').addClass('hide');
			disabledOperationButton(true);
			var searchForm = $('#search-form');
			$('#search-form').submit();
		}
	
	window.searchFinish =
		function searchFinish(data, loading) {
			saveSearchCriteria();
			if(data  && typeof data != 'undefined'){
				checkConfirmMessageBeforeExit = false;
				if(data.errorMessages != "" && typeof data.errorMessages != 'undefined') {
	    			if (data.focusId != "" && typeof data.focusId != 'undefined') {
	    				searchNotFoundMode();
	    				setFormFocus("unitPlantSearch");
	    				restoreSearchCriteria();
	    			}
	    		} else {
	    			dataForm = data.objectForm;
	    			
	    			if (dataForm != null) {
	    				ST3Lib.content.disabled( '#data-head-panel', false );
	    				$.each(dataForm, function(item, value){
	    		            $("#"+item).val(value);
	    		        });
	    				ST3Lib.content.disabled( '#data-head-panel', true );
	    				
	    				generateDataList(data);
	    				searchFoundMode();
	    			}
	    		}
			}
			loading.close();
		};
		
		function searchNotFoundMode(){
			ST3Lib.content.disabled('#search-criteria', false);
			ST3Lib.content.disabled($('#search-panel'), false);
			disabledOperationButton(true);
			$('#save-panel').addClass('hide');
			$('#result-list-form').addClass('hide');
			$('#search-criteria-label').addClass('hide');
			
			setDefaultSearchDisplay();
		}
		
		function searchFoundMode(){			
			ST3Lib.content.disabled('#search-result', true);
			ST3Lib.content.disabled('#search-criteria', false);
			disabledOperationButton(false);
			
			$('#save-panel').addClass('hide');
			$('#result-list-form').removeClass('hide');
			$('#search-criteria-label').removeClass('hide');
			
			$('.getsudoMonth').removeClass('hide');
			$('#unitPlantSearch').focus();
		}
		
		function disabledOperationButton(flag){	
			ST3Lib.validate.disabledButtonSpecify('#WBW04240Download', flag);	
			ST3Lib.validate.disabledButtonSpecify('#WBW04240Browse', flag);	
			ST3Lib.validate.disabledButtonSpecify('#WBW04240Upload', flag);	
			ST3Lib.validate.disabledButtonSpecify('#WBW04240Edit', flag);
		}
		
		window.generateDataList =
			function generateDataList(datas){
			var dataList = datas.objectsInfoList;
			 menuList = datas.objectsInfo2List;
			 $('input[name=getsudoMonths]').val(menuList);
			if(menuList){
				if(dataList){
					clearTableRowAndColumn();
					//time to generate header
					//generate empty header
					$('#result-header').append('<th class="colmenuheader sorting_disabled" role="columnheader" rowspan="1" colspan="1"></th>');
					for(var i=0; i < menuList.length; i++){
						$('#result-header').append( '<th class="colheader sorting_disabled" role="columnheader" rowspan="1" colspan="1">'
								+ menuList[i]
								+'</th>');
					}
					
					for(var i=0; i < dataList.length; i++){
						$('#result-body').append('<tr id=body-row-'+i+'>');
						var rows = dataList[i];
						for(var j=1; j < rows.length; j++){
							totalLength = rows.length;
							if(j == 1){
								$('#body-row-'+i).append( '<td class="colmenubody sorting_disabled" role="columnbody" rowspan="1" colspan="1">'
										+'<label  class="">'+addCommas(returnBlankIsNull(rows[j]))+'</label>'				
										+'</td>');
							}else{
								var rowarr = rows[j].split(":");
								$('#body-row-'+i).append( '<td class="col1 sorting_disabled" role="columnbody" rowspan="1" colspan="1">'
										+'<label for="row'+i+j+'" class="control-label">'+addCommas(returnBlankIsNull(rowarr[0]))+'</label>'
										+'<input type="text"  display-format="'+menucolumn[i][4]+'"  class="'+menucolumn[i][3]+' edit-input" title="'+menucolumn[i][0]+ '('+menuList[j-2]+')" id="row'+i+j+'" name="row'+i+j+'" value='+addCommas(returnBlankIsNull(rowarr[0]))+'>'
										+'</td>');
								
								if(i==2 || i==5 || i==6 || i==7){
									validateDecimal(document.getElementById('row'+i+j), menucolumn[i][1], menucolumn[i][2], '00');
								}else{
									validateNumber(document.getElementById('row'+i+j), menucolumn[i][1], menucolumn[i][2], false);
								}
								
								if(rowarr[1]=='Y'){
									$('#row'+i+j).attr('toenable', true);
								}
							}
						}
						$('#result-body').append('</tr>');
					}
				}
			}
		}	
	
	function returnBlankIsNull(param){
		if(typeof param == 'object'){
			return '';
		}
		return param;
	}	
		
	function clearTableRowAndColumn(){
		$('#result-header th').remove();
		$('#result-body td').remove();
	}
	
	function setDefaultSearchDisplay() {
		//clear parent line search
		$('#unitParentLineSearch').find('option').remove().end().append('<option value="">&lt;Select&gt;</option>').val('');
		//clear sub line search		
		$('#unitSubLineSearch').find('option').remove().end().append('<option value="">&lt;Select&gt;</option>').val('');
		
	}
	
	window.clearSearch =
		function clearSearch(){
			checkConfirmMessageBeforeExit = false;
			document.getElementById("search-form").reset();	
			$('#save-panel').addClass('hide');
			
			$('#WBW04240Browse').val('');
			clearTableRowAndColumn();
			searchNotFoundMode();			
			
			GWRDSLib.clearST3Message();
			$('#unitPlantSearch').focus();
			
		};
	
	function restoreSearchCriteria(){
		$('#unitPlantSearch').val(lastSearchCriteria.unitPlantSearch);
		
		$('#unitPlantSearch').change();
		$('#unitParentLineSearch').val(lastSearchCriteria.unitParentLineSearch);
		
		$('#unitParentLineSearch').change();
		$('#unitSubLineSearch').val(lastSearchCriteria.unitSubLineSearch);
		
		$('#getsudoMonthSearch').val(lastSearchCriteria.getsudoMonthSearch);
		$('#timingSearch').val(lastSearchCriteria.timingSearch);
	}
	
	function saveSearchCriteria(){
		var searchForm = $('#search-form');
		lastSearchCriteria.unitPlantSearch = searchForm.find('select[name=unitPlantSearch]').val();
		lastSearchCriteria.unitParentLineSearch = searchForm.find('select[name=unitParentLineSearch]').val();
		lastSearchCriteria.unitSubLineSearch = searchForm.find('select[name=unitSubLineSearch]').val();
		lastSearchCriteria.getsudoMonthSearch = searchForm.find('input[name=getsudoMonthSearch]').val();
		lastSearchCriteria.timingSearch = searchForm.find('select[name=timingSearch]').val();
		
		$('#resultUnitPlantCriteria').text(lastSearchCriteria.unitPlantSearch);
		$('#resultUnitParentLineCriteria').text(lastSearchCriteria.unitParentLineSearch);
		$('#resultGetsudoMonthCriteria').text(lastSearchCriteria.getsudoMonthSearch);
		$('#resultUnitSubLineCriteria').text(lastSearchCriteria.unitSubLineSearch);
		$('#resultTimingCriteria').text(lastSearchCriteria.timingSearch);
		
		$('#unitPlantSearchHdd').val(lastSearchCriteria.unitPlantSearch);
		$('#unitParentLineSearchHdd').val(lastSearchCriteria.unitParentLineSearch);
		$('#unitSubLineSearchHdd').val(lastSearchCriteria.unitSubLineSearch);
		$('#getsudoMonthSearchHdd').val(lastSearchCriteria.getsudoMonthSearch);
		$('#timingSearchHdd').val(lastSearchCriteria.timingSearch);
	}
	
	window.editObject =
		function editObject() {
			restoreSearchCriteria();
			GWRDSLib.clearST3Message();
			$('#WBW04240Browse').val('');
			$('#save-panel').removeClass('hide');
			$('.edit-input').show();
			$('.control-label').hide();
			diableOnEditMode();
			checkConfirmMessageBeforeExit = true;
			appId = '';
			ST3Lib.content.disabled( '#data-head-panel', false );
			ST3Lib.content.disabled( '.edit-input', true );
			var arr = [];
			var i =0;
			$('#result input[toenable=true]').each(function(){				
				arr[i] = this.id;
				i++;
			});
			for(var k=0; k<arr.length; k++){
				ST3Lib.content.disabled( $('#'+arr[k]), false );
			}
			$('#result input:text:enabled:first').focus();
			
			$('#result input').on( 'keydown', function( e ) {
			    if( e.which == 9 ) {
			    	var msgPanel = $(ST3Lib.id.ID_ERRORMSG);
			    	if ( msgPanel.hasClass('toggle') ) {
			    	}else{
			    		msgPanel.height(18);
			    		ST3Lib.message.hide();
			    	}
			    }
			});
		};
	
	window.saveAddEditObject = 
		function saveAddEditObject(){
			ST3Lib.dialog.confirm(MBW00001ACFM, 'MBW00001ACFM', function(ret){
				if (!ret) {
					return;
				}else{
					GWRDSLib.clearST3Message();
					
					restoreSearchCriteria();
					
					if (lastAction === 'add') {
						
					} else {
						var list = $('#result-body :input');
						var headerlist = $('#getsudoMonths').val();
						var headerarr = headerlist.split(",");
						var objectList = [];
						var temp = [];
						for(var i=0; i < headerarr.length; i++){
							temp[i] = headerarr[i];
						}
						
						for(var i=0; i < list.length; i++){
							temp[i+headerarr.length] = list[i].value.replace(",", "");
						}
						while(temp.length) objectList.push(temp.splice(0,headerarr.length));
																							
						$('input[name=allSelect]').val(objectList);
						$('input[name=rowLength]').val(headerarr.length);
						
						$('#result-list-form').attr('action', mappingPath + '/submitEdit').attr('_method', 'post');												
					}
					
					$("#messageBar").css("overflow", "auto");
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
					ST3Lib.message.clear(true);
					$('#save-panel').addClass('hide');
					
					ST3Lib.content.disabled( '#search-field', false );
					ST3Lib.content.disabled($('#search-panel'), false);
					disabledOperationButton(false);
					$('.getsudoMonth').removeClass('hide');
					
					restoreSearchCriteria();
	    			var searchForm = $('#search-form');
	    			$('#search-form').submit();
					GWRDSLib.clearST3Message();
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
				generateDataList(datas);
				searchFoundMode();
			}else{
				if(datas.errorMessage) {
					ST3Lib.message.show(1);
				}else if(datas.errorMessages != null && datas.errorMessages.length > 0) {
					ST3Lib.message.show(1);
				}
				if (datas.focusId != "" && typeof datas.focusId != 'undefined') {
					setFormFocus(datas.focusId);
				}else{
					$('#result input:text:enabled:first').focus();
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

	window.doDownload =
		function doDownload(){
		GWRDSLib.clearST3Message();
		restoreSearchCriteria();
		$('#WBW04240Browse').val('');
		var searchForm = $('#search-form');
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
			            	disabledOperationButton(false);
			        	}else {
			        		if(objSerialize.infoMessages.length > 0) {
								//ST3Lib.message.addInfo(objSerialize.infoMessages);
								//ST3Lib.message.show(1);
								
								document.getElementById("upload-form").reset();
								
				            	//loading.close();
				            	disabledOperationButton(false);
							}
			        	}
					}
					appId = '';
					//loading.close();
				}catch(e){}
			});
			
			searchForm.attr('action', mappingPath + '/download?appIdStr='+appId).attr('target', 'downloadIframe')
				.removeAttr('ajax')
				.submit();
			searchForm.attr('ajax', ajax).removeAttr('target').attr('action', action);
			$('#getsudoMonthSearch').focus();
			checkDownloadStatus(loading, appId);
		}
	}
	
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
						disabledOperationButton(false);
		        	}else {
		        		checkDownloadStatus(loadingDownload, appIdStr);
		        	}
				})
				.error(function() {
					loadingDownload.close();
					loadingDownload = null;
					appId = '';
					disabledOperationButton(false);
				});
		}, downloadTimingCheckStatus);
	}
	
	window.doUpload =
		function doUpload(){
			GWRDSLib.clearST3Message();
			restoreSearchCriteria();
			var searchForm = $('#search-form');
			var uploadForm = $('#upload-form');
			if (formValidateError(searchForm) && formValidateError(uploadForm)) {
				checkConfirmMessageBeforeExit = true;
				var loadingTarget = "#screen-panel";
				loadingUpload = ST3Lib.dialog.loading(loadingTarget);
				
				uploadForm.ajaxForm({ 
		            url: mappingPath+'/upload', 
		            type: 'POST',
		            data: { unitPlant : $("#unitPlantSearch").val(), 
		            		unitParentLine : $("#unitParentLineSearch").val(),
		            	    unitSubLine : $("#unitSubLineSearch").val(),
		            	    getsudoMonth : $("#getsudoMonthSearch").val(),
		            	    timing : $("#timingSearch").val(),
		            	    updateKeySet : $("#updateKeySet").val()
		            	    },
		            dataType: 'text',
		            success: function(data) {
		            	if(data  && typeof data != 'undefined'){	
		            		var objSerialize = JSON.parse(data);
		            		
			        		if(objSerialize.errorMessages != "" && typeof objSerialize.errorMessages != 'undefined') {
				        		ST3Lib.message.addError(objSerialize.errorMessages);
								ST3Lib.message.show(1);

				            	$('#WBW04240Upload').removeAttr('disabled');
				            	loadingUpload.close();
				            	loadingUpload = null;
				            	disabledOperationButton(false);
				            	checkConfirmMessageBeforeExit = false;
				        	}else {
				        		if(objSerialize.infoMessages.length > 0) {
				        			appId = objSerialize.appId;
				        			checkUploadStatus(loadingUpload, appId);             
								}
				        	}
		        		}
		            },		
		        	error: function() {
		        		$('#WBW04240Upload').removeAttr('disabled');
		        		loadingUpload.close();
		        		loadingUpload = null;
		        		disabledOperationButton(false);
		        		checkConfirmMessageBeforeExit = false;
		        	}
		        }).submit();	
			}
		};	
			
		function checkUploadStatus(loadingUpload, appId){
			setTimeout(function(){
				$.ajax({
					  method: "POST",
					  url: mappingPath+'/checkStatusOfLogUpload',
					  async: false,
					  data: { appId : appId,
						  	unitPlant : $("#unitPlantSearch").val(), 
		            		unitParentLine : $("#unitParentLineSearch").val(),
		            	    unitSubLine : $("#unitSubLineSearch").val(),
		            	    getsudoMonth : $("#getsudoMonthSearch").val(),
		            	    timing : $("#timingSearch").val()  
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
							$('#WBW04240Upload').removeAttr('disabled');
			            	
			            	checkConfirmMessageBeforeExit = false;
			            	appId = '';
			            	$('#WBW04240Browse').val('');
			            	
			            	dataForm = objPayload.objectForm;
			            	if (dataForm!=null) { 
								$.each(dataForm, function(item, value){
						            $("#"+item).val(value);
						        });  
							}
			            	
			            	generateDataList(objPayload);
			            	searchFoundMode();
			            	
			            	loadingUpload.close();
			            	loadingUpload = null;
			            	disabledOperationButton(false);
			        	}else {
			        		$('#WBW04240Browse').val('');
			        		checkConfirmMessageBeforeExit = true;
			        		checkUploadStatus(loadingUpload, appId);
			        	}
					})
					.error(function() {
						$('#WBW04240Browse').val('');
						loadingUpload.close();
			          	loadingUpload = null;
			          	
			          	disabledOperationButton(false);
			          	checkConfirmMessageBeforeExit = false;
					});
			}, uploadTimingCheckStatus);
		}
			

})(ST3Lib.$);