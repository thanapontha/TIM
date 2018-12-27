(function($){
	var lastSearchCriteria = {};
	var lastAction = '';
	var UPDATE_KEYS_SEPERATER = '|';
	
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
	        	$('#WBW02160Browse').val('');
	        	appId = '';
	        	if(loadingUpload){
	        		enableButton('Initial');
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
		
		$('#getsudoMonthSearch').focus();
		
		$('#getsudoMonthSearch').MonthPicker({
		    Button: '<img src="'+_imagePath + 'icons/cal.gif" title="Select Month" class="getsudoMonthSearchImg" />',
		    MonthFormat: 'M-y',
	        onSelect: function(){
				$(this).focus();	
			},
			OnAfterChooseMonth: function(){
				$('#getsudoMonthSearch').focus();
				if(prevGetsudoSearch != $('#getsudoMonthSearch').val()){
					prevGetsudoSearch = $('#getsudoMonthSearch').val();
					getsudoMonthSearchChange();
				}
			}
		});
		
		var prevGetsudoSearch = $('#getsudoMonthSearch').val();
		
		window.getsudoMonthSearchChange = function getsudoMonthSearchChange(){
			$("#unitPlantSearch").val('');
			$("#unitModelSearch").val('').empty().append('<option value="">&lt;All&gt;</option>');
			$("#vehicleModelSearch").val('').empty().append('<option value="">&lt;All&gt;</option>');
		};
		
		validateDateMMMYY(document.getElementById('getsudoMonthSearch'));
		
		$("#getsudoMonthSearch").change(function() {
			prevGetsudoSearch = $('#getsudoMonthSearch').val();
			getsudoMonthSearchChange();
		});
		
		$('#result').on('click', 'input:checkbox', function(ev){
			var checkbox = $( this );
			if (checkbox.is(':disabled')) return;
			$('#result-list-form').find('input:hidden[name=updateKeySet]').val(checkbox.val());
		});
		
		$('#unitPlantSearch').change(function() {
			$.ajax({
				  method: "POST",
				  async : false,
				  url: mappingPath+'/unitPlantSearchChange',
				  data: { getsudoMonthSearch : $("#getsudoMonthSearch").val(), 
					  	  unitPlantSearch : $("#unitPlantSearch").val(),
					  	  unitModelSearch : $("#unitModelSearch").val()}
				})
				.done(function( data ) {
					//console.log(data);
					var $unitModel = $('#unitModelSearch');
					$unitModel.empty();
					$unitModel.append('<option value="">&lt;All&gt;</option>');
					if(data!=null && data.unitModelList!=null && data.unitModelList){						
						for(var i = 0; i < data.unitModelList.length; ++i) {
							var obj = data.unitModelList[i];
							$unitModel.append('<option value="{0}">{1}</option>'.format(obj.stValue, obj.stLabel));
						}
					}
					var $vehicleModel = $('#vehicleModelSearch');
					$vehicleModel.empty();
					$vehicleModel.append('<option value="">&lt;All&gt;</option>');
					if(data!=null && data.vehicleModelList!=null && data.vehicleModelList){						
						for(var i = 0; i < data.vehicleModelList.length; ++i) {
							var obj = data.vehicleModelList[i];
							$vehicleModel.append('<option value="{0}">{1}</option>'.format(obj.stValue, obj.stLabel));
						}
					}
					
					var $parentLine = $('#parentLineUpload');
					//ST3Lib.content.disabled( '#parentLineUpload', false );
					$parentLine.empty();
					$parentLine.append('<option value="">&lt;Select&gt;</option>');
					if(data!=null && data.parentLineList!=null && data.parentLineList){						
						for(var i = 0; i < data.parentLineList.length; ++i) {
							var obj = data.parentLineList[i];
							$parentLine.append('<option value="{0}">{1}</option>'.format(obj.stValue, obj.stLabel));
						}
					}
					
					
				})
				.error(function() {});
		});
		
		$('#unitModelSearch').change(function() {
			$.ajax({
				  method: "POST",
				  async : false,
				  url: mappingPath+'/unitModelSearchChange',
				  data: { getsudoMonthSearch : $("#getsudoMonthSearch").val(), 
					  	  unitPlantSearch : $("#unitPlantSearch").val(),
					  	  unitModelSearch : $("#unitModelSearch").val()}
				})
				.done(function( data ) {
					//console.log(data);
					var $vehicleModel = $('#vehicleModelSearch');
					$vehicleModel.empty();
					$vehicleModel.append('<option value="">&lt;All&gt;</option>');
					if(data!=null && data.vehicleModelList!=null && data.vehicleModelList){						
						for(var i = 0; i < data.vehicleModelList.length; ++i) {
							var obj = data.vehicleModelList[i];
							$vehicleModel.append('<option value="{0}">{1}</option>'.format(obj.stValue, obj.stLabel));
						}
					}
				})
				.error(function() {});
		});
		
		enableButton('Initial');
	});
	
	window.enableButton = 
		function enableButton(mode){
		if(mode == 'Initial'){
			$('#getsudoMonthSearch').focus();
			ST3Lib.content.disabled( '#parentLineUpload', true );
			ST3Lib.validate.disabledButtonSpecify( '#WBW02160Download', true );
			ST3Lib.validate.disabledButtonSpecify( '#WBW02160Browse', true );
			ST3Lib.validate.disabledButtonSpecify( '#WBW02160Upload', true );
			
			ST3Lib.validate.disabledButtonSpecify('#WBW02160CheckingReport', true);
			ST3Lib.validate.disabledButtonSpecify('#WBW02160Confirm', true);
		}else if(mode == 'Search'){
			$('#result input:checkbox:first').focus();
			
			ST3Lib.content.disabled( '#parentLineUpload', false );
			ST3Lib.validate.disabledButtonSpecify( '#WBW02160Download', false );
			ST3Lib.validate.disabledButtonSpecify( '#WBW02160Browse', false );
			ST3Lib.validate.disabledButtonSpecify( '#WBW02160Upload', false );
			
			ST3Lib.validate.disabledButtonSpecify('#WBW02160CheckingReport', false);
			ST3Lib.validate.disabledButtonSpecify('#WBW02160Confirm', true);
		}else if(mode == 'Confirm'){
			ST3Lib.content.disabled( '#parentLineUpload', false );
			ST3Lib.validate.disabledButtonSpecify( '#WBW02160Download', false );
			ST3Lib.validate.disabledButtonSpecify( '#WBW02160Browse', false );
			ST3Lib.validate.disabledButtonSpecify( '#WBW02160Upload', false );
			ST3Lib.validate.disabledButtonSpecify('#WBW02160CheckingReport', false);
			ST3Lib.validate.disabledButtonSpecify('#WBW02160Confirm', false);
			
		}else if(mode == 'Not Confirm'){
			ST3Lib.content.disabled( '#parentLineUpload', false );
			ST3Lib.validate.disabledButtonSpecify( '#WBW02160Download', false );
			ST3Lib.validate.disabledButtonSpecify( '#WBW02160Browse', false );
			ST3Lib.validate.disabledButtonSpecify( '#WBW02160Upload', false );
			ST3Lib.validate.disabledButtonSpecify('#WBW02160CheckingReport', false);
			ST3Lib.validate.disabledButtonSpecify('#WBW02160Confirm', true);
			
		}else if(mode == 'No Data Found'){
			$('#getsudoMonthSearch').focus();
			
			ST3Lib.content.disabled( '#parentLineUpload', false );
			ST3Lib.validate.disabledButtonSpecify( '#WBW02160Download', false );
			ST3Lib.validate.disabledButtonSpecify( '#WBW02160Browse', false );
			ST3Lib.validate.disabledButtonSpecify( '#WBW02160Upload', false );
			
			ST3Lib.validate.disabledButtonSpecify('#WBW02160CheckingReport', false);
			ST3Lib.validate.disabledButtonSpecify('#WBW02160Confirm', true);
		}
	}

	window.doSearch = 
		function doSearch(){
			lastAction = 'search';
			lastSelectedId = '';
			GWRDSLib.clearST3Message();
			
			$('#WBW02160Browse').val('');
			
			//Reset result panel
			var dataTable = $( '#result' ).dataTable();
			dataTable.fnClearTable(true);
			$('#getsudoMonthCriteria,#timingCriteria,#unitPlantCriteria,#unitModelCriteria,#vehicleModelCriteria').html('-');
			
			enableButton('Initial');
			var searchForm = $('#search-form');
			$('#search-form').submit();
		}
	
	window.searchFinish =
		function searchFinish(dataServer, loading){
			saveSearchCriteria();
			if (dataServer === null) return;
			checkConfirmMessageBeforeExit = false;
			ST3Lib.message.setAppendMode(false);
			
			var dataTable = $( '#result' ).dataTable();
			dataTable.fnClearTable(false);//no redraw
			datas = dataServer.objectsInfoList;
			if ( !datas || datas.length === 0 ){

				//ST3Lib.message.clear(true);
				//ST3Lib.message.addError('No Data<spring:message code="MSTD0059AERR"></spring:message>');
				//ST3Lib.message.show(1);
				if(dataServer.errorMessages == "MSTD0059AERR: No data found"){
					//Disable/Enable Components
					enableButton('No Data Found');
					
					//Fill value in Criteria Label
					dataForm = dataServer.objectForm;
					$('#getsudoMonthCriteria').html(dataForm.getsudoMonthSearch);
					$('#timingCriteria').html(dataForm.timingSearch);
					$('#unitPlantCriteria').html(dataForm.unitPlantSearch);
					$('#unitModelCriteria').html(isNullOrEmptyString(dataForm.unitModelSearch) ? 'All' : dataForm.unitModelSearch);
					$('#vehicleModelCriteria').html(isNullOrEmptyString(dataForm.vehicleModelSearch) ? 'All' : dataForm.vehicleModelSearch);
				}
				dataTable.fnDraw(true);
				$('#afterSearch-command').addClass('hide');
				return;	
				
			}else{
				
				$('#afterSearch-command').removeClass('hide');
				$('#add-command').removeClass('hide');
			}
			ST3Lib.sizing.autoHeight();
			if ( loading ) {
				loading.autoClose = false;
				loading.resize();
			}	
			
			generateDataList(dataServer);
			
			loading.close();
			
			enableButton('Search');
		};
		
		window.generateDataList =
			function generateDataList(dataServer){
			datas = dataServer.objectsInfoList;
			
			if(datas == null) return;
			
			var dataTable = $( '#result' ).dataTable();
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
			setting.aoColumns[10].sClass = 'col11';
			setting.aoColumns[11].sClass = 'col12';
			setting.aoColumns[12].sClass = 'col13';
							
			var tempDiv = $('<div/>');
			dataTable.fnClearTable(false);//no redraw
			var i = 0;
			var iNo = 1;
			var arrayData = [];
			var prevUnitParentLine = '';
			var countRowspan = 1;
			var rowSpanMap = {};
			for(var j=0; j<datas.length; j++){
				var data = datas[j];
				if ( data ) {
					if(prevUnitParentLine == data.unitParentLine){
						countRowspan++;
					}else{
						prevUnitParentLine = data.unitParentLine;
						countRowspan = 1;
					}
					rowSpanMap[data.unitParentLine] = countRowspan;
				}
				
			}
			
			for(var j=1; j<=datas.length; j++){
				
				var data = datas[i];
				if ( data ) {
					var rowData = [];
					var serialize = tempDiv.text(JSON.stringify(data)).html();
					var unitParentLine = '';
					
					//Prepare result data 
					if(i == 0){
						//Disable/Enable Components
						enableButton('Search');
						
						//Fill value in Criteria Label
						dataForm = dataServer.objectForm;
						$('#getsudoMonthCriteria').html(dataForm.getsudoMonthSearch);
						$('#timingCriteria').html(dataForm.timingSearch);
						$('#unitPlantCriteria').html(dataForm.unitPlantSearch);
						$('#unitModelCriteria').html(isNullOrEmptyString(dataForm.unitModelSearch) ? 'All' : dataForm.unitModelSearch);
						$('#vehicleModelCriteria').html(isNullOrEmptyString(dataForm.vehicleModelSearch) ? 'All' : dataForm.vehicleModelSearch);
						unitParentLine = data.unitParentLine;
					}
					if(prevUnitParentLine == data.unitParentLine && j != 1){
						unitParentLine = '';
						countRowspan++;
					}else{
						prevUnitParentLine = data.unitParentLine;
						unitParentLine = data.unitParentLine;
						countRowspan = 1;
					}
					
					var incomeVolumeStatus = '';
					if(data.incomeVolumeStatus != null 
							&& (data.incomeVolumeStatus == 'Received' || data.incomeVolumeStatus == 'All vehicle plants received')){
						incomeVolumeStatus = '<span class="font-green">'+data.incomeVolumeStatus+'</span>';
					}else{
						incomeVolumeStatus = data.incomeVolumeStatus;
					}
					if(unitParentLine == ''){
						rowData = [ i
										, '<div class="empty_cell"/>'
									    , ''
									    , unitParentLine || ''
									    , (data.vehiclePlant || '') + 
									    '<input type="hidden" name="capacityId'+data.unitParentLine+'" value="' + 
									 	data.unitParentLine + UPDATE_KEYS_SEPERATER +
									 	data.vehiclePlant + UPDATE_KEYS_SEPERATER +
									 	data.vehicleModel + UPDATE_KEYS_SEPERATER +
									 	data.unitModel + UPDATE_KEYS_SEPERATER +
									 	data.incomeVolumeStatus + UPDATE_KEYS_SEPERATER +
									 	data.capacityMasterStatus + UPDATE_KEYS_SEPERATER +
									 	data.parentLineCapacityResult + UPDATE_KEYS_SEPERATER +
									 	data.subLineCapacityResult + UPDATE_KEYS_SEPERATER +
									 	data.confirmStatus + UPDATE_KEYS_SEPERATER +
									 	data.remark + UPDATE_KEYS_SEPERATER +
									 	data.updateKeySet +
									 	UPDATE_KEYS_SEPERATER + 'CLOSER" />'
									    , data.vehicleModel || ''
									    , data.unitModel || ''
									    , incomeVolumeStatus
									    , ''
									    , ''
									    , ''
									    , ''
									    , data.remark || ''
									];
					}else{
						var capaMasterSts = '';
						var parentLineCapacityResult = '';
						var subLineCapacityResult = '';
						var remain = '';
						
						if(data.capacityMasterStatus != null && data.capacityMasterStatus == 'Set'){
							capaMasterSts = '<span class="font-green">'+data.capacityMasterStatus+'</span>';
						}else{
							capaMasterSts = data.capacityMasterStatus;
						}
						
						if(data.remainWait != null && data.remainWait != "0"){
							remain = '[Remain '+data.remainWait+' Unit(s)]';
						}
						
						if(data.parentLineCapacityResult != null && data.parentLineCapacityResult == 'OK'){
							parentLineCapacityResult = '<span class="bg-green">'+data.parentLineCapacityResult+'<br/>'+remain+'</span>';
						}else if(data.parentLineCapacityResult != null && data.parentLineCapacityResult == 'OVER'){
							parentLineCapacityResult = '<span class="bg-red">'+data.parentLineCapacityResult+'<br/>'+remain+'</span>';
						}else{
							parentLineCapacityResult = data.parentLineCapacityResult;
						}
						if(data.subLineCapacityResult!=null && data.subLineCapacityResult.indexOf('OVER') != -1){
							subLineCapacityResult = '<span class="bg-red">'+data.subLineCapacityResult+'</span>';
						}else if(data.subLineCapacityResult!=null && data.subLineCapacityResult.indexOf('OK') != -1){
							subLineCapacityResult = '<span class="bg-green">'+data.subLineCapacityResult+'</span>';
						}else{
							subLineCapacityResult = data.subLineCapacityResult;
						}
						
						var keyVals = data.unitParentLine + UPDATE_KEYS_SEPERATER +
								 	  data.vehiclePlant + UPDATE_KEYS_SEPERATER +
								 	  data.vehicleModel + UPDATE_KEYS_SEPERATER +
								 	  data.unitModel + UPDATE_KEYS_SEPERATER +
								 	  data.incomeVolumeStatus + UPDATE_KEYS_SEPERATER +
								 	  data.capacityMasterStatus + UPDATE_KEYS_SEPERATER +
								 	  data.parentLineCapacityResult + UPDATE_KEYS_SEPERATER +
								 	  data.subLineCapacityResult + UPDATE_KEYS_SEPERATER +
								 	  data.confirmStatus + UPDATE_KEYS_SEPERATER +
								 	  data.remark + UPDATE_KEYS_SEPERATER +
								 	  data.updateKeySet + UPDATE_KEYS_SEPERATER +
								 	  data.remainWait + UPDATE_KEYS_SEPERATER + 'CLOSER';
						rowData = [ i
										, '<input class="" type="checkbox" name="capacityId" value="'+keyVals+'" onclick="clickCheckbox(this);" /><div class="serialize" style="display:none">' + serialize + '</div>' 
									    , iNo++
									    , data.unitParentLine || ''
									    , (data.vehiclePlant || '') + 
									    '<input type="hidden" name="capacityId'+data.unitParentLine+'" value="'+keyVals+'" />'
									    , data.vehicleModel || ''
									    , data.unitModel || ''
									    , incomeVolumeStatus
									    , capaMasterSts
									    , parentLineCapacityResult
									    , subLineCapacityResult || ''
									    , data.confirmStatus || ''
									    , data.remark || ''
									];
					}
					arrayData.push(rowData);
					
				}
				
				i++;
			}
			
			dataTable.fnAddData(arrayData);
			mergeCellTable(rowSpanMap);
			
			//Copy class from child(Span) to parent(TD)
			$('.bg-green').parent().addClass('bg-green');
			$('.bg-red').parent().addClass('bg-red');
			
			GWRDSLib.selectOneCheckboxMode('#result');
		}
		
	window.clickCheckbox = function clickCheckbox(chk) {
		$(':checkbox').on(
				'change',
				function() {
					var th = $(this), name = th.prop('name');
					if (th.is(':checked')) {
						$(':checkbox[name="' + name + '"]').not($(this)).prop(
								'checked', false);
					}
				});
		if ($(chk).prop('checked')) {
			
			var param = chk.value;
			var paramsplit = param.split(UPDATE_KEYS_SEPERATER);
			if (paramsplit.length > 0) {
				var parentLineSts = paramsplit[6];
				var subLineSts = paramsplit[7];
				var confirmSts = paramsplit[8];
				var remainWait = paramsplit[16];
				var subLineStsArr = subLineSts.split(',');
				var subLineHasOVER = false;
				
				if(subLineSts == '-' || subLineSts == SUBLINE_CAPA_WA){
					subLineHasOVER = true;
				}else{
					for(var i=0;i<subLineStsArr.length; i++){
						var valArr = subLineStsArr[i].split(':');
						if(valArr.length > 1 && valArr[1].trim() == 'OVER'){
							subLineHasOVER = true;
						}
					}
				}
	
			/*	parent capa	remain	sub line capa	confirm sts		Enable/Disable
				OK			0		OK				NO				E
				OK			0		-				NO				E
			 */
				if(parentLineSts == 'OK' && (remainWait == null || remainWait == "0") && confirmSts != 'Confirmed'){
					if(subLineSts == '-' || !subLineHasOVER){
						enableButton('Confirm');
					}else{
						enableButton('Not Confirm');
					}
				}else{
					enableButton('Not Confirm');
				}
			}
		}else{
			enableButton('Not Confirm');
		}
	};
		
		function mergeCellTable(rowSpanMap){
			if(rowSpanMap){
				$('#result').find('tbody tr').each(function (i, el) {
					var cbxVal = $(this).find('td input[type=checkbox]').val();
					if(cbxVal != undefined && cbxVal != ""){
						var cbxValArr = cbxVal.split('|');
						var parentLine = cbxValArr[0];
						var rowSpan = 1;
						if(parentLine != null && rowSpanMap[parentLine] != null && rowSpanMap[parentLine] > 0){
							rowSpan = rowSpanMap[parentLine];
						}
						$(this).find('td').eq(0).attr("rowspan",rowSpan);
						$(this).find('td').eq(1).attr("rowspan",rowSpan);
						$(this).find('td').eq(2).attr("rowspan",rowSpan);
						$(this).find('td').eq(7).attr("rowspan",rowSpan);
				        $(this).find('td').eq(8).attr("rowspan",rowSpan);
				        $(this).find('td').eq(9).attr("rowspan",rowSpan);
				        $(this).find('td').eq(10).attr("rowspan",rowSpan);
				    }else{
				        $(this).find('td').eq(10).remove();
				        $(this).find('td').eq(9).remove();
				        $(this).find('td').eq(8).remove();
				        $(this).find('td').eq(7).remove();
				        $(this).find('td').eq(2).remove();
				        $(this).find('td').eq(1).remove();
				        $(this).find('td').eq(0).remove();
				    }
				});
			}
		};
		
		function searchNotFoundMode(){
			ST3Lib.content.disabled('#search-criteria', false);
			ST3Lib.content.disabled($('#search-panel'), false);
			$('#save-panel').addClass('hide');
			
			setDefaultSearchDisplay();
			$( '#result' ).dataTable().fnClearTable();
		}
		
		function searchFoundMode(){			
			ST3Lib.content.disabled('#search-result', true);
			ST3Lib.content.disabled('#search-criteria', false);

			$('#search-result').removeClass('hide');
			ST3Lib.content.disabled($('#search-panel'), false);
			$('#save-panel').addClass('hide');
			$("#search-result").find('input[type=checkbox]').prop( 'disabled', false );
			$("#search-result").find('input[type=checkbox]').prop( 'checked', false );
		}
		
	function setDefaultSearchDisplay() {
	}
	
	function restoreSearchCriteria(){
		var currentParentLine = $('#parentLineUpload').val();
		$("#getsudoMonthSearch").val(lastSearchCriteria.getsudoMonthSearch);
		$("#timingSearch").val(lastSearchCriteria.timingSearch);
		$("#unitPlantSearch").val(lastSearchCriteria.unitPlantSearch);
		
		//load combo box of unitModel before set value
		$("#unitPlantSearch").change();	
		$("#unitModelSearch").val(lastSearchCriteria.unitModelSearch);
		
		//load combo box of unitModel before set value
		$("#unitModelSearch").change();
		$("#vehicleModelSearch").val(lastSearchCriteria.vehicleModelSearch);
		$('#parentLineUpload').val(currentParentLine);
	}
	
	function saveSearchCriteria(){
		lastSearchCriteria.getsudoMonthSearch = $("#getsudoMonthSearch").val();
		lastSearchCriteria.timingSearch = $("#timingSearch").val();
		lastSearchCriteria.unitPlantSearch = $("#unitPlantSearch").val();
		lastSearchCriteria.unitModelSearch = $("#unitModelSearch").val();
		lastSearchCriteria.vehicleModelSearch = $("#vehicleModelSearch").val();
	}
	
	window.doConfirm = 
		function doConfirm(){
		GWRDSLib.clearST3Message();
		restoreSearchCriteria();
		$('#WBW02160Browse').val('');
		ST3Lib.dialog.confirm(MBW00004ACFM_CONFIRM, 'MBW00004ACFM', function(ret){
			if (!ret) {
				return;
			}else{
				var loadingTarget = "#screen-panel";
				var loading = ST3Lib.dialog.loading(loadingTarget);
				var dataConfirm = {};
				var chkVal = $('input[type=checkbox]:checked').val();
				var paramsplit = chkVal.split(UPDATE_KEYS_SEPERATER);
				var parentLineChk = '';
				if (paramsplit.length > 0) {
					parentLineChk = paramsplit[0];
				}
				$('input[type=hidden][name=capacityId'+parentLineChk+']').each(function(i){
					dataConfirm[i] = $(this).val();
				});
				
				//console.log(dataConfirm); 
				$.ajax({
					method : "POST",
					url : mappingPath + '/doConfirm',
					data : {
						dataConfirm : JSON.stringify(dataConfirm),
						getsudoMonthCriteria : $("#getsudoMonthSearch").val(), 
	            		timingCriteria : $("#timingSearch").val(),
	            		unitPlantCriteria : $("#unitPlantSearch").val(),
	            		unitModelCriteria: $("#unitModelSearch").val(),
	            		vehicleModelCriteria: $("#vehicleModelSearch").val()
					}
				})
				.done(function(datas) {
					if(datas.status === 'OK'){
						checkConfirmMessageBeforeExit = false;
						
						if(datas.infoMessages.length > 0) {
							 ST3Lib.message.addInfo(datas.infoMessages);
							 ST3Lib.message.show(1);
						}
						
						var dataForm = datas.objectForm;
						if (dataForm!=null) { 
							$.each(dataForm, function(item, value){
					            $("#"+item).val(value);
					        });  
						}
						generateDataList(datas);
						
						enableButton('Search');
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
							$('#result input:first').focus();
						}
					}
					
					if (loading) {
						loading.close();
						loading = null;
					}
				})
				.error(function() {
					enableButton('Search');
					if (loading) {
						loading.close();
						loading = null;
					}
				});
			}
		});
	};
	
	window.doClear =
		function doClear(){
			checkConfirmMessageBeforeExit = false;
			document.getElementById("search-form").reset();
			document.getElementById("upload-form").reset();
			$('#save-panel').addClass('hide');
			$('#result').dataTable().fnClearTable();
			$('#getsudoMonthCriteria,#timingCriteria,#unitPlantCriteria,#unitModelCriteria,#vehicleModelCriteria').html('-');
			
			$('#WBW02160Browse').val('');
			
			GWRDSLib.clearST3Message();
			lastSearchCriteria = {};
			
			enableButton('Initial');
		};
		
		window.doDownload =
			function doDownload(){
				$('#parentLineDownload').val($('#parentLineUpload').val());
				GWRDSLib.clearST3Message();
				restoreSearchCriteria();
				$('#WBW02160Browse').val('');
				var searchForm = $('#search-form');
				var uploadForm = $('#upload-form');
				$('#WBW02160Browse').removeClass('MandatoryField');
				if (formValidateError(searchForm) && formValidateError(uploadForm)) {
					$('#WBW02160Browse').addClass('MandatoryField');
				
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
							var text = $('#downloadIframe').contents().text();//$(window.downloadIframe.document.body).text();
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
				
				$('#WBW02160Browse').addClass('MandatoryField');
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
			        	}else {
			        		checkDownloadStatus(loadingDownload, appIdStr);
			        	}
					})
					.error(function() {
						loadingDownload.close();
						loadingDownload = null;
						appId = '';
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
	            data: { getsudoMonthCriteria : $("#getsudoMonthSearch").val(), 
	            		timingCriteria : $("#timingSearch").val(),
	            		unitPlantCriteria : $("#unitPlantSearch").val(),
	            		unitModelCriteria : $("#unitModelSearch").val(),
	            		vehicleModelCriteria : $("#vehicleModelSearch").val(),
	            		parentLineCriteria : $("#parentLineUpload").val()},
	            dataType: 'text',
	            success: function(data) {
	            	if(data  && typeof data != 'undefined'){	
	            		var objSerialize = JSON.parse(data);
	            		
		        		if(objSerialize.errorMessages != "" && typeof objSerialize.errorMessages != 'undefined') {
			        		ST3Lib.message.addError(objSerialize.errorMessages);
							ST3Lib.message.show(1);

			            	$('#WBW02160Upload').removeAttr('disabled');
			            	loadingUpload.close();
			            	loadingUpload = null;
			            	enableButton('Search');
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
	        		$('#WBW02160Browse').val('');
	        		$('#WBW02160Upload').removeAttr('disabled');
	        		loadingUpload.close();
	            	loadingUpload = null;
	            	enableButton('Search');
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
					  	  getsudoMonthSearch: $('#getsudoMonthSearch').val(),
					  	  timingSearch : $('#timingSearch').val(),
						  vehicleModelSearch : $('#vehicleModelSearch').val(),
						  unitPlantSearch : $('#unitPlantSearch').val(),
						  unitModelSearch : $('#unitModelSearch').val()}
				})
				.done(function( data1 ) {
					var objPayload = JSON.parse(data1);						            		
					if(objPayload.status == "OK" && typeof objPayload.status != 'undefined') {      			
						ST3Lib.message.clear();
						var warning = false;
						if(objPayload.errorMessages != "" && typeof objPayload.errorMessages != 'undefined') {
			        		ST3Lib.message.addError(objPayload.errorMessages);
							ST3Lib.message.show(1);
							
							//refresh search data
							var objPayload = JSON.parse(data1);
							generateDataList(objPayload);

			        	}else if(objPayload.infoMessages.length > 0) {
							ST3Lib.message.addInfo(objPayload.infoMessages);
							ST3Lib.message.show(1);	
							
							//refresh search data
							var objPayload = JSON.parse(data1);
							generateDataList(objPayload);
						}
						var tmp = $('#parentLineUpload').val();
						document.getElementById("upload-form").reset();	
						$('#parentLineUpload').val(tmp);
						$('#WBW02160Upload').removeAttr('disabled');
		            	
		            	checkConfirmMessageBeforeExit = false;
		            	$('#WBW02160Browse').val('');
		            	loadingUpload.close();
		            	loadingUpload = null;
		            	enableButton('Search');
		        	}else {
		        		$('#WBW02160Browse').val('');
		        		checkConfirmMessageBeforeExit = true;
		        		checkUploadStatus(loadingUpload, appId);
		        	}
				})
				.error(function() {
					$('#WBW02160Browse').val('');
					loadingUpload.close();
		          	loadingUpload = null;
		          	enableButton('Search');
		          	checkConfirmMessageBeforeExit = false;
				});
		}, uploadTimingCheckStatus);
	}
	
	window.doCheckingReport = 
		function doCheckingReport(){
		restoreSearchCriteria();
		$('#WBW02160Browse').val('');
		var jsonParams = {
				"reportCode" : "LBW03240", 
				"getsudoMonth" : $('#getsudoMonthSearch').val(), 
				"timing": $('#timingSearch').val(), 
				"unitPlant": $('#unitPlantSearch').val()
				};			
		
		GWRDSLib.PageOpen(basePath + '/common/gwrdsCommonDownload', "WBW03210", jsonParams);
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
			processAjaxDataList(datas);
			$('.dataTables_info').css("visibility","visible")
			$('.dataTables_paginate').css("visibility","visible");
			$('#unitPlantSearch').focus();
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
	
	window.isNullOrEmptyString =
		function isNullOrEmptyString(val) {
		if(val === '' || val === null || val === undefined || val == null){
			return true;
		}else{
			return false;
		}
	}
	
})(ST3Lib.$);