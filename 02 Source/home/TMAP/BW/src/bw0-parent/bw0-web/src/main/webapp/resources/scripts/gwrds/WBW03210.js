(function($) {
	var checkConfirmMessageBeforeExit = false;
	var truefalse =	{
           "N": true, 
           "Y": false
	};
	
	var isMandatory = {
			"N": '', 
            "Y": 'MandatoryField'
	};

	window.onbeforeunload = function(event) {
		if (checkConfirmMessageBeforeExit) {
			return MSTD0003ACFM;
		}
	};
	
	var totalLenght = 0;
	var lastSearchCriteria = {};
	var lastAction = '';
	var menuList;
	var appId = '';

	$(function() {
		$('#ui-accordion-menu-header-3').click();
		$('#ui-accordion-menu-header-3').attr('tabindex','-1');
		
		ST3Lib.validate.disabledButtonSpecify('#WBW03210Download', true);
//		ST3Lib.validate.disabledButtonSpecify('#WBW03210Clear', true);
		ST3Lib.content.disabled('#download-criteria-panel', true);
		
		$('#reportCodeSearch').focus();

		var imagepath = _imagePath + "icons/cal.gif";
		$('#getsudoMonthValue').MonthPicker({
				MonthFormat : 'M-y', // Short month name, Full// year.
				Button : '<img class="getsudoMonth" title="Select Month" src='+ imagepath + '/>',
				AltFormat : 'M-y', // result
				onSelect : function() {
					$(this).focus();
				},
				OnAfterChooseMonth: function() { 
					$('#getsudoMonthValue').focus();
					if(prevGetsudoSearch != $('#getsudoMonthValue').val()){
						prevGetsudoSearch = $('#getsudoMonthValue').val();
						getsudoMonthSearchChange();
					}
				} 
			});
		
		var prevGetsudoSearch = $('#getsudoMonthValue').val();
		
		window.getsudoMonthSearchChange = function getsudoMonthSearchChange(){
			if($('#unitPlantValue')[0].disabled == false){
		    	$.ajax({
					  method: "POST",
					  url: mappingPath+'/getUnitPlant',
					  data: { getsudoMonth :$("#getsudoMonthValue").val()}
					})
					.done(function( data ) {
						var modelValue = $('#unitPlantValue');
						resetSelectOption('#unitPlantValue', $('#unitPlantValue')[0].disabled);
						resetSelectOption('#unitModelValue', $('#unitModelValue')[0].disabled);
						resetSelectOption('#unitParentLineValue', $('#unitParentLineValue')[0].disabled);
						resetSelectOption('#unitSubLineValue', $('#unitSubLineValue')[0].disabled);
											
						if(data){				
							for(var i = 0; i < data.length; ++i) {
								var obj = data[i];							
								modelValue.append('<option value="{0}">{1}</option>'.format(obj.stValue, obj.stLabel));
							}
						}
					})
					.error(function() {});
		    }else if($('#vehiclePlantValue')[0].disabled == false){
		    	$.ajax({
					  method: "POST",
					  url: mappingPath+'/getVehiclePlant',
					  data: { 
						  getsudoMonth : $("#getsudoMonthValue").val()
						  }
					})
					.done(function( data ) {
						
						var modelValue = $('#vehiclePlantValue');
						
						resetSelectOption('#vehiclePlantValue',  $('#vehiclePlantValue')[0].disabled);
						resetSelectOption('#vehicleModelValue',  $('#vehicleModelValue')[0].disabled);
											
						if(data){				
							for(var i = 0; i < data.length; ++i) {
								var obj = data[i];							
								modelValue.append('<option value="{0}">{1}</option>'.format(obj.stValue, obj.stLabel));
							}
						}
					})
					.error(function() {});
		    }
		};
		
		$('#getsudoMonthValue').MonthPicker('option', 'Disabled', true);
		validateDateMMMYY(document.getElementById('getsudoMonthValue'));
		
		$("#getsudoMonthValue").change(function() {
			prevGetsudoSearch = $('#getsudoMonthValue').val();
			getsudoMonthSearchChange();
		});
		
		$('#vSGetsudoMonthValue').MonthPicker({
					MonthFormat : 'M-y', // Short month name, Full// year.
					Button : '<img class="getsudoMonth" title="Select Month" src='+ imagepath + '/>',
					AltFormat : 'M-y', // result
					onSelect : function() {
						$(this).focus();
					} ,
					OnAfterChooseMonth: function() { 
						$('#vSGetsudoMonthValue').focus();
					}
				});
		
		$('#vSGetsudoMonthValue').MonthPicker('option', 'Disabled', true);	
		validateDateMMMYY(document.getElementById('vSGetsudoMonthValue'));
	
		$('#reportCodeSearch').change(function() {
			if($("#reportCodeSearch").val()===''){
				$("#getsudoMonthValue").val('');
				$("#getsudoMonthValue").removeClass('MandatoryField');
				$('#getsudoMonthValue').MonthPicker('option', 'Disabled', true);
				ST3Lib.content.disabled("#getsudoMonthValue", true);
				resetSelectOption('#timingValue', true);
				resetSelectOption('#vehiclePlantValue', true);
				resetSelectOption('#vehicleModelValue', true);
				resetSelectOption('#unitPlantValue', true);
				resetSelectOption('#unitModelValue', true);
				resetSelectOption('#unitParentLineValue', true);
				resetSelectOption('#unitSubLineValue', true);
				resetSelectOption('#vSTimingValue', true);
				$("#vSGetsudoMonthValue").val('');
				$("#vSGetsudoMonthValue").removeClass('MandatoryField');
				ST3Lib.content.disabled("#vSGetsudoMonthValue", true);
				$('#vSGetsudoMonthValue').MonthPicker('option', 'Disabled', true);
			}else{
				$.ajax({
					  method: "POST",
					  url: mappingPath+'/reportSelected',
					  data:{ gm : $("#getsudoMonthValue").val() , reportCode : $("#reportCodeSearch").val()}
					})
					.success(function( datas ) {
						GWRDSLib.clearST3Message();
						setEnableAndMandatoryFields(datas);
						populateParentBox(datas);
						$('#reportNameSearch').val($('#reportCodeSearch option:selected').text());
						
						//clone options from timing to vs timing
						if($('#vSTimingValue')[0].disabled == false){
							$('#vSTimingValue').empty();
							$('#timingValue').find('option').clone().appendTo('#vSTimingValue');
						}
						if(isInitialWithPassParam){
							isInitialWithPassParam = false;
							var timing = $('#timing').val();
							var unitPlant = $('#unitPlant').val();
							if(timing != undefined || timing != ''){
								$("#timingValue option[value='"+timing+"']").prop('selected', true);
							}
							
							if(unitPlant != undefined || unitPlant != ''){
								$("#unitPlantValue option[value='"+unitPlant+"']").prop('selected', true);
							}
						
							if(datas.objectForm != null || datas.objectForm != undefined){
								var gm = datas.objectForm.getsudoMonthValue;
								if(gm != undefined || gm != ''){
									$('#getsudoMonthValue').val(gm);
								}
							}
						}else{
							$('#getsudoMonthValue').val($('#defaultGetsudoMonth').val());
							$("#unitPlantValue").val('');
							$("#timingValue").val('');
						}

						$('#vSGetsudoMonthValue').val('');
						
						ST3Lib.validate.disabledButtonSpecify('#WBW03210Download', false);
						ST3Lib.validate.disabledButtonSpecify('#WBW03210Clear', false);
						$('#download-criteria-panel input:text:first').focus();
					})
					.fail( function(xhr, textStatus, errorThrown) {
				         //console.log(textStatus + ":" + errorThrown);
				         
				         GWRDSLib.clearST3Message();
				         
				         if(xhr && xhr.responseJSON){			        	
				        	 var objPayload = xhr.responseJSON;
				        	 if (objPayload.errorMessages != "" && typeof objPayload.errorMessages != 'undefined') {
				        		clearDownload();
								ST3Lib.message.addError(objPayload.errorMessages);
								ST3Lib.message.show(1);
							}
				         }else{
				        	 clearDownload();
				        	 ST3Lib.message.addError(errorThrown);
							 ST3Lib.message.show(1);
							 
				         }
				     });
			}
		});
		
		$('#vehiclePlantValue').change(function() {
			if($('#vehicleModelValue')[0].disabled){
				return 
			}else{
				$.ajax({
					  method: "POST",
					  url: mappingPath+'/vehiclePlantChanged',
					  data: { vehiclePlant : $("#vehiclePlantValue").val(),
						  getsudoMonth : $("#getsudoMonthValue").val()	}
					})
					.done(function( data ) {
						var vehicleModelValue = $('#vehicleModelValue');
						vehicleModelValue.empty();
											
						if(data){				
							vehicleModelValue.append('<option value="">&lt;Select&gt;</option>');
							for(var i = 0; i < data.length; ++i) {
								var obj = data[i];							
								vehicleModelValue.append('<option value="{0}">{1}</option>'.format(obj.stValue, obj.stLabel));
							}
						}
					})
					.error(function() {});
			}
		});
		
		$('#unitPlantValue').change(function() {			
			if($('#unitModelValue')[0].disabled == false){
				$.ajax({
					  method: "POST",
					  url: mappingPath+'/getUnitModel',
					  data: { 
						  unitPlant : $("#unitPlantValue").val(),
						  getsudoMonth : $("#getsudoMonthValue").val()						  
						  }
					})
					.done(function( data ) {
						var modelValue = $('#unitModelValue');						
						resetSelectOption('#unitModelValue', $('#unitModelValue')[0].disabled);
						resetSelectOption('#unitParentLineValue', $('#unitParentLineValue')[0].disabled);
						resetSelectOption('#unitParentLineValue', $('#unitParentLineValue')[0].disabled);
						resetSelectOption('#unitSubLineValue', $('#unitSubLineValue')[0].disabled);
											
						if(data){				
							for(var i = 0; i < data.length; ++i) {
								var obj = data[i];							
								modelValue.append('<option value="{0}">{1}</option>'.format(obj.stValue, obj.stLabel));
							}
						}
					})
					.error(function() {});
			}
			else if($('#unitParentLineValue')[0].disabled == false){
				$.ajax({
					  method: "POST",
					  url: mappingPath+'/getUnitParentLine',
					  data: { 
						  unitPlant : $("#unitPlantValue").val(),
						  getsudoMonth : $("#getsudoMonthValue").val()						  
						  }
					})
					.done(function( data ) {
						var modelValue = $('#unitParentLineValue');			
						resetSelectOption('#unitParentLineValue', $('#unitParentLineValue')[0].disabled);
						resetSelectOption('#unitParentLineValue', $('#unitParentLineValue')[0].disabled);
						resetSelectOption('#unitSubLineValue', $('#unitSubLineValue')[0].disabled);
											
						if(data){				
							for(var i = 0; i < data.length; ++i) {
								var obj = data[i];							
								modelValue.append('<option value="{0}">{1}</option>'.format(obj.stValue, obj.stLabel));
							}
						}
					})
					.error(function() {});
			}
				

		});
		
		$('#unitModelValue').change(function() {
			if($('#unitParentLineValue')[0].disabled){
				return 
			}else{
				$.ajax({
					  method: "POST",
					  url: mappingPath+'/unitModelChanged',
					  data: { unitModel : $("#unitModelValue").val(),
						  getsudoMonth : $("#getsudoMonthValue").val()}
					})
					.done(function( data ) {
						var modelValue = $('#unitParentLineValue');
						modelValue.empty();
											
						if(data){				
							modelValue.append('<option value="">&lt;Select&gt;</option>');
							for(var i = 0; i < data.length; ++i) {
								var obj = data[i];							
								modelValue.append('<option value="{0}">{1}</option>'.format(obj.stValue, obj.stLabel));
							}
						}
					})
					.error(function() {});
			}
		});
		
		$('#unitParentLineValue').change(function() {
			if($('#unitSubLineValue')[0].disabled){
				return 
			}else{
				$.ajax({
					  method: "POST",
					  url: mappingPath+'/unitParentLineChanged',
					  data: { 
						  unitParentLine : $("#unitParentLineValue").val(),
						  getsudoMonth : $("#getsudoMonthValue").val()
						  
						  }
					})
					.done(function( data ) {
						var modelValue = $('#unitSubLineValue');
						modelValue.empty();
											
						if(data){				
							modelValue.append('<option value="">&lt;Select&gt;</option>');
							for(var i = 0; i < data.length; ++i) {
								var obj = data[i];							
								modelValue.append('<option value="{0}">{1}</option>'.format(obj.stValue, obj.stLabel));
							}
						}
					})
					.error(function() {});
			}
		});
	
		var isInitialWithPassParam;
		var reportCode = $('#reportCode').val();
		if(reportCode != ""){
			isInitialWithPassParam = true;
			var getsudoMonth = $('#getsudoMonth').val();
			$("#reportCodeSearch").val(reportCode);
			$("#reportCodeSearch option[value='"+reportCode+"']").prop('selected', true);
			
			$('#reportCodeSearch').trigger("change");
		}else{
			isInitialWithPassParam = false;
		}
		
	});
	
	window.clearDownload =
		function clearDownload(){
			GWRDSLib.clearST3Message();
			document.getElementById("download-form").reset();
			//reset all of select enabled
			var listOfSelectEnabled = $('#download-criteria-panel select:enabled');
			if(listOfSelectEnabled.size()>0){
				for(var i=0; i < listOfSelectEnabled.size(); i++){
					var id = listOfSelectEnabled[i].id;
					resetSelectOption('#'+id, true);
					ST3Lib.content.disabled('#'+id, true);						
					//console.log(id);
				}
			}	
					
			$('#getsudoMonthValue').MonthPicker('option', 'Disabled', true);
			$('#vSGetsudoMonthValue').MonthPicker('option', 'Disabled', true);
			$('#getsudoMonthValue').attr('value', '');
			ST3Lib.validate.disabledButtonSpecify('#WBW03210Download', true);
//			ST3Lib.validate.disabledButtonSpecify('#WBW03210Clear', true);
			$('#reportCodeSearch').focus();
		}
	
	window.clickDownload = 
		function clickDownload(){
			GWRDSLib.clearST3Message();
			
			//set first VS Getsudo Month
			$('#firstVSGetsudoMonth').val(getSubstractOfGetsudoMonths($('#getsudoMonthValue').val(),3));
						
			var downloadForm = $('#download-form');
			if (formValidateError(downloadForm)) {
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
				
				var ajax = downloadForm.attr('ajax');
				var action = downloadForm.attr('action');
				
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
									
					            	//loading.close();
								}
				        	}
						}
						appId = '';
						//loading.close();
					}catch(e){}
				});
				
				var params = 'appIdStr='+appId
				+'&reportCodeDL='+$('#reportCodeSearch').val()
				+'&reportNameDL='+$('#reportNameSearch').val();
				
				//check data on iframe to get error message
				downloadForm.attr('action', mappingPath + '/download?'+params).attr('target', 'downloadIframe')
					.removeAttr('ajax')
					.submit();
				downloadForm.attr('ajax', ajax).removeAttr('target').attr('action', action);
				checkDownloadStatus(loading, appId, $('#reportCodeSearch').val(), $('#reportNameSearch').val());	
			}
		}
	
	function checkDownloadStatus(loadingDownload, appIdStr, reportCodeDL, reportNameDL){
		setTimeout(function(){
			$.ajax({
				  method: "POST",
				  url: mappingPath+'/checkStatusOfLogDownload',
				  async: false,
				  data: { appIdStr : appIdStr,
					  	  reportCodeDL : reportCodeDL,
					  	  reportNameDL : reportNameDL}
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
						loadingDownload.close();
						loadingDownload = null;
						appId = '';
		        	}else {
		        		checkDownloadStatus(loadingDownload, appIdStr, reportCodeDL, reportNameDL);
		        	}
				})
				.error(function() {
					loadingDownload.close();
					loadingDownload = null;
					appId = '';
				});
		}, downloadTimingCheckStatus);
	}
	
	window.downloadFinish =
		function downloadFinish(data, loading) {
		
		loading.close();
	}
	
	window.downloadValidateError =
		function downloadValidateError(){
			return;
		};
		
	window.populateParentBox = function populateParentBox(datas){
		if(datas.criterias != null){
			var timingList = datas.criterias["TIMING_LIST"];
			if(timingList != null || timingList != undefined ){
				var timingValue = $('#timingValue');
				timingValue.empty();
				timingValue.append('<option value="">&lt;Select&gt;</option>');
				for(var i = 0; i < timingList.length; ++i) {
					var obj = timingList[i];							
					timingValue.append('<option value="{0}">{1}</option>'.format(obj.stValue, obj.stLabel));
				}
			}
			
			var vehiclePlantList = datas.criterias["VEHICLE_PLANT_LIST"];
			if(vehiclePlantList != null || vehiclePlantList != undefined){
				var vehiclePlantValue = $('#vehiclePlantValue');
				vehiclePlantValue.empty();
				vehiclePlantValue.append('<option value="">&lt;Select&gt;</option>');
				for(var i = 0; i < vehiclePlantList.length; ++i) {
					var obj = vehiclePlantList[i];							
					vehiclePlantValue.append('<option value="{0}">{1}</option>'.format(obj.stValue, obj.stLabel));
				}
	
			}
			
			var unitPlantList = datas.criterias["UNIT_PLANT_LIST"];
			if(unitPlantList != null || unitPlantList != undefined){
				var unitPlantValue = $('#unitPlantValue');
				unitPlantValue.empty();
				unitPlantValue.append('<option value="">&lt;Select&gt;</option>');
				for(var i = 0; i < unitPlantList.length; ++i) {
					var obj = unitPlantList[i];							
					unitPlantValue.append('<option value="{0}">{1}</option>'.format(obj.stValue, obj.stLabel));
				}
			}
		}
				
	}
	
	window.setEnableAndMandatoryFields = function setEnableAndMandatoryFields(datas){		
		if(datas.fields == null || datas.fields == undefined){
			return;
		}
		var enableField = datas.fields["ENABLE_FIELD"];
		if(enableField == null || enableField == undefined){
			return;
		}
		var mandatoryField = datas.fields["MANDATORY"];
		if(mandatoryField == null || mandatoryField == undefined){
			return;
		}
		if(enableField.length > 0){
			var enableFieldArr = enableField.split("|");
			
			for(var i=0; i < enableFieldArr.length; i++){
				var obj = enableFieldArr[i];
				var objarr = obj.split(":");
				if(objarr[0] == "GetsudoMonth" || objarr[0] == "VSGetsudoMonth"){
					$('#'+lowercaseFirstLetter(objarr[0])+'Value').MonthPicker('option', 'Disabled', truefalse[objarr[1]]);
				}else{
					ST3Lib.content.disabled('#'+lowercaseFirstLetter(objarr[0])+'Value', truefalse[objarr[1]]);
					resetSelectOption('#'+lowercaseFirstLetter(objarr[0])+'Value', truefalse[objarr[1]]);
				}
					
			}
		}
		if(mandatoryField.length > 0){
			var mandatoryarr = mandatoryField.split("|");
			for(var i=0; i < mandatoryarr.length; i++){
				var obj = mandatoryarr[i];
				var objarr = obj.split(":");
				$('#'+lowercaseFirstLetter(objarr[0])+'Value').removeClass('MandatoryField');
			}
			for(var i=0; i < mandatoryarr.length; i++){
				var obj = mandatoryarr[i];
				var objarr = obj.split(":");
				$('#'+lowercaseFirstLetter(objarr[0])+'Value').removeClass('NoMandatoryField'); 
				$('#'+lowercaseFirstLetter(objarr[0])+'Value').addClass(isMandatory[objarr[1]]);
			}
		}
		
	}
	
	function resetSelectOption(id, disabled){
		$(id).empty();
		if(disabled){			
			$(id).append('<option value="">&#45;None&#45;</option>');
			$(id).removeClass('MandatoryField');
			ST3Lib.content.disabled(id, true);
		}else{
			$(id).append('<option value="">&lt;Select&gt;</option>');
		}		
	}
	
	function lowercaseFirstLetter(str){
		return str.charAt(0).toLowerCase() + str.slice(1);
	}



	function returnBlankIsNull(param) {
		if (typeof param == 'object') {
			return '';
		}
		return param;
	}
	
	


})(ST3Lib.$);