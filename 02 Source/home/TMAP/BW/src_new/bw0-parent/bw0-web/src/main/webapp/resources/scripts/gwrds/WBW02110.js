(function($) {
	var appId = '';
	var checkConfirmMessageBeforeExit = false;
	var loadingUpload = null;
	var selectedCheckbox;
	var totalLenght = 0;
	var lastSearchCriteria = {};
	var lastAction = '';
	var UPDATE_KEYS_SEPERATER = '|';
	
	window.onbeforeunload = onbeforeunloadGWRDS;

	function onbeforeunloadGWRDS(event) {
		if (checkConfirmMessageBeforeExit) {
			if (appId != '') {
				$.ajax({
					method : "POST",
					url : mappingPath + '/updateStatusInterruptOfLogUpload',
					dataType : 'json',
					async : false,
					data : {
						appIdStr : appId
					},
					success : function(data) {
					}
				});
				$('#WBW02110Browse').val('');
				appId = '';
				if (loadingUpload) {
					selectedCheckbox = null;
					enableButtonBaseOnStatus();
					loadingUpload.close();
					loadingUpload = null;
				}
				return "";
			} else {
				return MSTD0003ACFM;
			}
		}
	};

	$(function() {
		enableButtonBaseOnStatus();

		$('#getsudoMonthSearch').focus();

		$('#result').on('click', 'input:checkbox', function(ev){
			
			var iSelectedCount = 0;
			var unitModelParams = "";
			var unitPlantParams = "";
			var unitTypeParams = "";
			$('#result :checkbox:checked').each(function(){
				var paramsplit = $(this).val().split(UPDATE_KEYS_SEPERATER);
				if(iSelectedCount == 0){
					unitModelParams = paramsplit[1];
					unitPlantParams = paramsplit[2];
					unitTypeParams = paramsplit[3];
				}else{
					unitModelParams = unitModelParams + "^#^" + paramsplit[1];
					unitPlantParams = unitPlantParams + "^#^" + paramsplit[2];
					unitTypeParams = unitTypeParams + "^#^" + paramsplit[3];
				}
				
				iSelectedCount++;
			});
			console.log(iSelectedCount);
			if(iSelectedCount > 1){
				//Disable PAMs/Skip upload/Detail/Log/Fix
				enableButtonBaseOnStatus();
			}
			
			$('#result :checkbox').on('change', function() {
				var th = $(this), name = th.prop('name');
				if (th.is(':checked')) {
					//$(':checkbox[name="' + name + '"]').not($(this)).prop('checked', false);
					$('#WBW02110Browse').val('');
					$('input[name="validateTypesRad"]:checked').prop('checked', false);
				}
			});
			var checkbox = $( this );
			if (checkbox.is(':disabled')) return;
			
			selectedCheckbox = checkbox;
//			enableButtonBaseOnStatus(selectedCheckbox);
			enableButtonBaseOnStatus();
			$('#WBW02110Browse').val('');
			
			//NEW
			$('#unitModelParam').val(unitModelParams);
			$('#unitPlantParam').val(unitPlantParams);
			$('#unitTypeParam').val(unitTypeParams);
		});
		
		var imagepath = _imagePath + "icons/cal.gif";
		$('#getsudoMonthSearch').MonthPicker({
			MonthFormat : 'M-y', // Short month name, Full year.
			Button : '<img class="getsudoMonth" title="Select Month" src=' + imagepath + '/>',
			AltFormat : 'M-y', // result
			OnAfterChooseMonth : function() {
				$('#getsudoMonthSearch').focus();
				if(prevGetsudoSearch != $('#getsudoMonthSearch').val()){
					prevGetsudoSearch = $('#getsudoMonthSearch').val();
					getsudoMonthSearchChange();
				}
			}
		});
		
		var prevGetsudoSearch = $('#getsudoMonthSearch').val();
		
		window.getsudoMonthSearchChange = function getsudoMonthSearchChange(){
			$("#vehiclePlantSearch").val('');
			$("#vehicleModelSearch").val('').empty().append('<option value="">&lt;All&gt;</option>');
			$("#unitModelSearch").val('').empty().append('<option value="">&lt;All&gt;</option>');
			$("#unitPlantSearch").val('').empty().append('<option value="">&lt;All&gt;</option>');
		};
		
		validateDateMMMYY(document.getElementById('getsudoMonthSearch'));

		$("#getsudoMonthSearch").change(function() {
			prevGetsudoSearch = $('#getsudoMonthSearch').val();
			getsudoMonthSearchChange();
		});
		
		$('#vehiclePlantSearch').change(function() {
			$.ajax({method : "POST",
					url : mappingPath + '/vehiclePlantSearchChanged',
					data : {getsudoMonth : $("#getsudoMonthSearch").val(),
					vehiclePlant : $("#vehiclePlantSearch").val()}
			}).done(function(data) {
				var $vehicleModelSearch = $('#vehicleModelSearch');
				$vehicleModelSearch.empty();
				$vehicleModelSearch.append('<option value="">&lt;All&gt;</option>');
				if (data) {
					for (var i = 0; i < data.length; ++i) {
						var obj = data[i];
						$vehicleModelSearch.append('<option value="{0}">{1}</option>'.format(obj.stValue, obj.stLabel));
					}
				}
			}).error(function() { });
		});

		$('#vehicleModelSearch').change(function() {
			$.ajax({method : "POST",
					url : mappingPath + '/vehicleModelSearchChanged',
					data : {getsudoMonth : $("#getsudoMonthSearch").val(),
					vehiclePlant : $("#vehiclePlantSearch").val(),
					vehicleModel : $("#vehicleModelSearch").val()}
			}).done(function(data) {
				var $vehicleModelSearch = $('#unitModelSearch');
				$vehicleModelSearch.empty();
				$vehicleModelSearch
						.append('<option value="">&lt;All&gt;</option>');
				if (data) {
					for (var i = 0; i < data.length; ++i) {
						var obj = data[i];
						$vehicleModelSearch.append('<option value="{0}">{1}</option>'.format(obj.stValue, obj.stLabel));
					}
				}
			}).error(function() { });
		});

		$('#unitModelSearch').change(function() {
			$.ajax({method : "POST",
					url : mappingPath + '/unitModelSearchChanged',
					data : { getsudoMonth : $("#getsudoMonthSearch").val(),
							 vehiclePlant : $("#vehiclePlantSearch").val(),
							 vehicleModel : $("#vehicleModelSearch").val(),
							 unitModel : $("#unitModelSearch").val()}
			}).done(function(data) {
				var $vehicleModelSearch = $('#unitPlantSearch');
				$vehicleModelSearch.empty();
				$vehicleModelSearch.append('<option value="">&lt;All&gt;</option>');
				if (data) {
					for (var i = 0; i < data.length; ++i) {
						var obj = data[i];
						$vehicleModelSearch.append('<option value="{0}">{1}</option>'.format(obj.stValue, obj.stLabel));
					}
				}
			}).error(function() { });
		});

		clearResultSearchCriteria();
	});
	
	function enableButtonBaseOnStatus(){
		var iSelectedCount = 0;
		var selectedcheckbox;
		var isMultiVehiclePlant = false;
		var isMultiUnitPlant = false;
		var isSelectFix = false;
		var isCannotValidate = false;
		var vehicleModelParamChk = "";
		var unitPlantParamChk = "";
		
		$('#result :checkbox:checked').each(function(){
			var paramsplit = $(this).val().split(UPDATE_KEYS_SEPERATER);
			if(iSelectedCount == 0){
				selectedcheckbox = $(this);
				vehicleModelParamChk = paramsplit[0];
				unitPlantParamChk = paramsplit[2];
				
				statusParamChk = paramsplit[5];
				uploadTypeParam = paramsplit[6];
			}else{
				if(vehicleModelParamChk != paramsplit[0]){
					isMultiVehiclePlant = true;
				}
				if(unitPlantParamChk != paramsplit[2]){
					isMultiUnitPlant = true;
				}
			}
			
			if(paramsplit[9] == "FX"){ //Kompokung Fixed, PAMS Rundown Fixed, Skip Upload Fixed
				isSelectFix = true;
			}
			
			if("BLANK" == paramsplit[5] || "WE" == paramsplit[5]  || "RS" == paramsplit[5]) { //Worksheet data is not available
				isCannotValidate = true;
			}
			
			iSelectedCount++;
		});

		if(iSelectedCount > 1){
			//Disable all
			if(isMultiVehiclePlant || isMultiUnitPlant || isSelectFix || isCannotValidate) {
				$("input[type=radio][name=validateTypesRad]").prop('checked',false); // clear selected radio
				$("input[type=radio]").attr('disabled', true);
				$("input[type=radio][name=validateTypesRad]").attr('disabled', true);
				ST3Lib.validate.disabledButtonSpecify('#WBW02110Browse', true);
				ST3Lib.validate.disabledButtonSpecify('#WBW02110Validate', true);
				ST3Lib.validate.disabledButtonSpecify('#WBW02110Detail', true);
				ST3Lib.validate.disabledButtonSpecify('#WBW02110Log', true);
				ST3Lib.validate.disabledButtonSpecify('#WBW02110Fix', true);
			}else{
				//Disable PAMs/Skip upload/Detail/Log/Fix
				$("input[type=radio]").attr('disabled', true);
				
				ST3Lib.validate.disabledButtonSpecify('#WBW02110Detail', true);
				ST3Lib.validate.disabledButtonSpecify('#WBW02110Log', true);
				ST3Lib.validate.disabledButtonSpecify('#WBW02110Fix', true);
				
				$("input[type=radio][name=validateTypesRad][value=K]").attr('disabled', false);
				$("input[type=radio][name=validateTypesRad]").prop('checked',false);
				ST3Lib.validate.disabledButtonSpecify('#WBW02110Browse', false);
				ST3Lib.validate.disabledButtonSpecify('#WBW02110Validate', false);
			}
			
		}else{ // Count == 1
			var selectedRow;
			var checked;
			if(selectedcheckbox){
				selectedRow = selectedcheckbox.val();
				checked = selectedcheckbox.is(':checked');
			}
			
			if(selectedRow && checked){
				var paramsplit = selectedRow.split(UPDATE_KEYS_SEPERATER);
				$('#vehicleModelParam').val(paramsplit[0]);
				$('#unitModelParam').val(paramsplit[1]);
				$('#unitPlantParam').val(paramsplit[2]);
				$('#unitTypeParam').val(paramsplit[3]);
				$('#roundFlagParam').val(paramsplit[4]);
				$('#statusParam').val(paramsplit[5]);
				$('#uploadTypeParam').val(paramsplit[6]);
				$('#aplIdParam').val(paramsplit[7]);
				$('#errorDateParam').val(paramsplit[8]);
				$('#uploadStatus').val(paramsplit[9]);
				$('#updateDtStr').val(paramsplit[10]);
				$('#updateDtPAMs').val(paramsplit[11]);
				$('#updateDtKompo').val(paramsplit[12]);
				var roundFlag = paramsplit[4];
				var status = paramsplit[5];
				var uploadType = paramsplit[6];
				if("BLANK" == status || "WE" == status  || "RS" == status  || //Worksheet data is not available
						"K-FX" == status || //Kompokung Fixed
						"R-FX" == status || //PAMS Rundown Fixed
						"S-FX" == status ) //Skip Upload Fixed
				{
					$("input[type=radio]").attr('disabled', true);
					ST3Lib.validate.disabledButtonSpecify('#WBW02110Browse', true);
					ST3Lib.validate.disabledButtonSpecify('#WBW02110Validate', true);
					ST3Lib.validate.disabledButtonSpecify('#WBW02110Detail', true);
					ST3Lib.validate.disabledButtonSpecify('#WBW02110Log', true);
					ST3Lib.validate.disabledButtonSpecify('#WBW02110Fix', true);
				}else if ("WS" == status){ //Worksheet data is available
					$("input[type=radio]").attr('disabled', false);
					ST3Lib.validate.disabledButtonSpecify('#WBW02110Browse', false);
					ST3Lib.validate.disabledButtonSpecify('#WBW02110Validate',false);
					ST3Lib.validate.disabledButtonSpecify('#WBW02110Detail', true);
					ST3Lib.validate.disabledButtonSpecify('#WBW02110Log', true);
					ST3Lib.validate.disabledButtonSpecify('#WBW02110Fix', true);
				}else if ("K-SC" == status) { //Kompokung, Uploaded but not yet Fix
					$("input[type=radio]").attr('disabled', false);
					ST3Lib.validate.disabledButtonSpecify('#WBW02110Browse', false);
					ST3Lib.validate.disabledButtonSpecify('#WBW02110Validate', false);
					ST3Lib.validate.disabledButtonSpecify('#WBW02110Detail', false);
					ST3Lib.validate.disabledButtonSpecify('#WBW02110Log', false);
					if ("Y" == roundFlag) {
						ST3Lib.validate.disabledButtonSpecify('#WBW02110Fix', false);
					} else {
						ST3Lib.validate.disabledButtonSpecify('#WBW02110Fix', true);
					}
				}else if ("R-SC" == status){ //PAMS Rundown, Uploaded but not yet Fix
					$("input[type=radio]").attr('disabled', false);
					ST3Lib.validate.disabledButtonSpecify('#WBW02110Browse', false);
					ST3Lib.validate.disabledButtonSpecify('#WBW02110Validate', false);
					ST3Lib.validate.disabledButtonSpecify('#WBW02110Detail', true);
					ST3Lib.validate.disabledButtonSpecify('#WBW02110Log', false);
					ST3Lib.validate.disabledButtonSpecify('#WBW02110Fix', false);
				}else if ("S-SC" == status){ //Skip Upload, Uploaded but not yet Fix
					$("input[type=radio]").attr('disabled', false);
					ST3Lib.validate.disabledButtonSpecify('#WBW02110Browse', false);
					ST3Lib.validate.disabledButtonSpecify('#WBW02110Validate', false);
					ST3Lib.validate.disabledButtonSpecify('#WBW02110Detail', true);
					ST3Lib.validate.disabledButtonSpecify('#WBW02110Log', true);
					ST3Lib.validate.disabledButtonSpecify('#WBW02110Fix', false);
				}else if(("R" == uploadType || "K" == uploadType) && "ER" == status){ //PAMS Rundown or Kompokung, Uploaded Error
					$("input[type=radio]").attr('disabled', false);
					ST3Lib.validate.disabledButtonSpecify('#WBW02110Browse', false);
					ST3Lib.validate.disabledButtonSpecify('#WBW02110Validate',false);
					ST3Lib.validate.disabledButtonSpecify('#WBW02110Detail', false);
					ST3Lib.validate.disabledButtonSpecify('#WBW02110Log', false);
					ST3Lib.validate.disabledButtonSpecify('#WBW02110Fix', true);
				}else if ("S" == uploadType && "ER" == status){ //Skip Upload, Uploaded Error
					$("input[type=radio]").attr('disabled', false);
					ST3Lib.validate.disabledButtonSpecify('#WBW02110Browse', false);
					ST3Lib.validate.disabledButtonSpecify('#WBW02110Validate', false);
					ST3Lib.validate.disabledButtonSpecify('#WBW02110Detail', true);
					ST3Lib.validate.disabledButtonSpecify('#WBW02110Log', true);
					ST3Lib.validate.disabledButtonSpecify('#WBW02110Fix', true);
				}
			}else{
				$("input[type=radio]").attr('disabled', true);
				ST3Lib.validate.disabledButtonSpecify('#WBW02110Browse', true);
				ST3Lib.validate.disabledButtonSpecify('#WBW02110Validate', true);
				ST3Lib.validate.disabledButtonSpecify('#WBW02110Detail', true);
				ST3Lib.validate.disabledButtonSpecify('#WBW02110Log', true);
				ST3Lib.validate.disabledButtonSpecify('#WBW02110Fix', true);
			}
		}
	}

	window.clickSearch = function clickSearch() {
		lastAction = 'search';
		
		//Clear message bar
		GWRDSLib.clearST3Message();
		
		//Reset operation panel
		$('#WBW02110Browse').val('');
		$('input[name="validateTypesRad"]:checked').prop('checked', false);
		
		//Reset result panel
		var dataTable = $( '#result' ).dataTable();
		dataTable.fnClearTable(true);
		clearResultSearchCriteria();
		
		selectedCheckbox = null;
		enableButtonBaseOnStatus();
		
		var searchForm = $('#search-form');
		$('#search-form').submit();
	}

	window.searchFinish = function searchFinish(data, loading) {
		
		saveSearchCriteria();
		
		if (data && typeof data != 'undefined') {
			checkConfirmMessageBeforeExit = false;
			if (data.errorMessages != "" && typeof data.errorMessages != 'undefined') {
				
				var dataTable = $( '#result' ).dataTable();
				dataTable.fnClearTable(true);
				
				if (data.focusId != "" && typeof data.focusId != 'undefined') {
					enableButtonBaseOnStatus();
					$('#getsudoMonthSearch').focus();
				}
				
			} else {
				
				dataForm = data.objectForm;
				var dataTable = $('#result').dataTable();
				dataTable.fnClearTable(true);

				if (dataForm != null) {
					ST3Lib.content.disabled('#data-head-panel', false);
					$.each(dataForm, function(item, value) {
						$("#" + item).val(value);
					});
					ST3Lib.content.disabled('#data-head-panel', true);
					generateDataList(data);
					enableButtonBaseOnStatus();
					$('#result input:checkbox:first').focus();
				}

				loading.close();
			}
		}
		loading.close();
	};

	window.generateDataList = function generateDataList(dataServer) {
		datas = dataServer.objectsInfoList;
		
		if(datas == null) return;

		var dataTable = $( '#result' ).dataTable();
		dataTable.fnClearTable(false);//no redraw

		var setting = dataTable.fnSettings();
		setting.aoColumns[0].sClass = 'col1';
		setting.aoColumns[1].sClass = 'col2 rownum';
		setting.aoColumns[2].sClass = 'col3';
		setting.aoColumns[3].sClass = 'col4';
		setting.aoColumns[4].sClass = 'col5';
		setting.aoColumns[5].sClass = 'col6';
		setting.aoColumns[6].sClass = 'col7';

		var tempDiv = $('<div/>');
		
		var i = 1;
		var arrayData = [];

		for(var j = 0; j < datas.length; j++){
			var data = datas[j];
			if (data) {
				var serialize = tempDiv.text(JSON.stringify(data)).html();
				var chkBox = '<input class="checkbox" type="checkbox" name="objectIds" id="objectIds" value="'
						+ data.vehicleModel
						+ UPDATE_KEYS_SEPERATER
						+ data.unitModel
						+ UPDATE_KEYS_SEPERATER
						+ data.unitPlant
						+ UPDATE_KEYS_SEPERATER
						+ data.unitType
						+ UPDATE_KEYS_SEPERATER
						+ data.roundFlag
						+ UPDATE_KEYS_SEPERATER
						+ data.status
						+ UPDATE_KEYS_SEPERATER
						+ data.uploadType
						+ UPDATE_KEYS_SEPERATER
						+ data.aplId
						+ UPDATE_KEYS_SEPERATER
						+ data.errorDate
						+ UPDATE_KEYS_SEPERATER
						+ data.uploadStatus
						+ UPDATE_KEYS_SEPERATER
						+ data.updateDtStr
						+ UPDATE_KEYS_SEPERATER
						+ data.updateDtPAMs
						+ UPDATE_KEYS_SEPERATER
						+ data.updateDtKompo
						+ UPDATE_KEYS_SEPERATER
						+ '" /><div class="serialize" style="display:none">'
						+ serialize + '</div>';
				arrayData.push([ chkBox, 
				                 i++, 
				                 data.unitModel,
				                 data.vehicleModel, 
				                 data.unitPlant,
				                 data.statusDesc, 
				                 data.uploadTypeDesc ]);
			}
		}
		dataTable.fnAddData(arrayData);
	};

	window.saveValidateError = function saveValidateError() {
		return;
	};

	function clearResultSearchCriteria() {
		var resultList = $('#search-criteria-label');
		resultList.find('label[for=resultGetsudoMonthSearch]').text("-");
		resultList.find('label[for=resultTimingSearch]').text("-");
		resultList.find('label[for=resultVehiclePlantSearch]').text("-");
		resultList.find('label[for=resultVehicleModelSearch]').text("-");
		resultList.find('label[for=resultUnitModelSearch]').text("-");
		resultList.find('label[for=resultUnitPlantSearch]').text("-");
	}

	window.clearSearch = function clearSearch() {
		lastAction = 'clear';
		checkConfirmMessageBeforeExit = false;

		$("#search-form").trigger('reset');
		$("#upload-form").trigger('reset');
		//									
		$('#result').dataTable().fnClearTable();
		$('#getsudoMonthSearch').focus();
		
		$('#WBW02110Browse').val('');	
		GWRDSLib.clearST3Message();
		clearResultSearchCriteria();

		selectedCheckbox = null;
		enableButtonBaseOnStatus();
	};

	function restoreSearchCriteria() {
		var searchForm = $('#search-form');
		searchForm.find('input[name=getsudoMonthSearch]').val(lastSearchCriteria.getsudoMonthSearch);
		searchForm.find('select[name=timingSearch]').val(lastSearchCriteria.timingSearch);
		searchForm.find('select[name=vehiclePlantSearch]').val(lastSearchCriteria.vehiclePlantSearch);
		searchForm.find('select[name=vehicleModelSearch]').val(lastSearchCriteria.vehicleModelSearch);
		searchForm.find('select[name=unitModelSearch]').val(lastSearchCriteria.unitModelSearch);
		searchForm.find('select[name=unitPlantSearch]').val(lastSearchCriteria.unitPlantSearch);

	}

	function saveSearchCriteria() {
		var searchForm = $('#search-form');
		lastSearchCriteria.getsudoMonthSearch = searchForm.find('input[name=getsudoMonthSearch]').val();
		lastSearchCriteria.timingSearch = searchForm.find('select[name=timingSearch]').val();
		lastSearchCriteria.vehiclePlantSearch = searchForm.find('select[name=vehiclePlantSearch]').val();
		lastSearchCriteria.vehicleModelSearch = searchForm.find('select[name=vehicleModelSearch]').val();
		lastSearchCriteria.unitModelSearch = searchForm.find('select[name=unitModelSearch]').val();
		lastSearchCriteria.unitPlantSearch = searchForm.find('select[name=unitPlantSearch]').val();

		var resultList = $('#search-criteria-label');
		resultList.find('label[for=resultGetsudoMonthSearch]').text(lastSearchCriteria.getsudoMonthSearch);
		resultList.find('label[for=resultTimingSearch]').text(lastSearchCriteria.timingSearch);
		resultList.find('label[for=resultVehiclePlantSearch]').text(lastSearchCriteria.vehiclePlantSearch);
		resultList.find('label[for=resultVehicleModelSearch]').text(lastSearchCriteria.vehicleModelSearch == "" ? "All" : lastSearchCriteria.vehicleModelSearch);
		resultList.find('label[for=resultUnitModelSearch]').text(lastSearchCriteria.unitModelSearch == "" ? "All" : lastSearchCriteria.unitModelSearch);
		resultList.find('label[for=resultUnitPlantSearch]').text(lastSearchCriteria.unitPlantSearch == "" ? "All" : lastSearchCriteria.unitPlantSearch);

	}

	function formCustomValidateError(form) {
		var mandaField = form.find(".radioButtonField");
		if ($('.radioButtonField:checked').length == 0) {
			ST3Lib.message.addError(MSTD0031AERR.replace("{0}", "Upload Type"));
			ST3Lib.message.show(1);
			return false;
		} else {
			var uploadTypeChecked = $('.radioButtonField:checked').val(); //R,K,S
			var uploadTypeSelected = $('#uploadTypeParam').val(); //R,K,S
			var status = $('#statusParam').val(); //X-XX
			if (status != "") {
				if ("R-SC" == status) {
					if (uploadTypeChecked.trim() == "K") {
						ST3Lib.message.addError(MBW00014AERR);
						ST3Lib.message.show(1);
						return false;
					}
				}
				if ("K-SC" == status) {
					if (uploadTypeChecked.trim() == "R" || uploadTypeChecked.trim() == "S") {
						ST3Lib.message.addError(MBW00014AERR);
						ST3Lib.message.show(1);
						return false;
					}
				}
			}
		}

		return true;
	};

	window.doLog = function doLog() {
		GWRDSLib.clearST3Message();
		$('#WBW02110Browse').val('');
		var str = $(':checkbox[name=objectIds]:checked').val();
		if (str && str.length > 0) {
			var aplId = $('#aplIdParam').val();
			var errorDate = $('#errorDateParam').val();
			var uploadtype = $('#uploadTypeParam').val();
			var functionId = "";
			if ("K" == uploadtype.trim()) {
				functionId = "BBW02130";
			} else {
				functionId = "BBW02120";
			}
			if(functionId!= ""){
				var jsonParams = {
					"module" : "BW02",
					"function" : functionId,
					"appId" : aplId,
					"dateFrom" : errorDate == "" ? " " : errorDate,
					"dateTo" : errorDate == "" ? " " : errorDate
				};
				GWRDSLib.PageOpen(_rootPath + '/common/logMonitoring', "WST33010", jsonParams);
			}
		}
	}

	window.doDetail = function doDetail() {
		GWRDSLib.clearST3Message();
		restoreSearchCriteria();
		$('#WBW02110Browse').val('');
		var cbxVal = $('#result input:checked').val();
		var getsudoMonth = $('#getsudoMonthSearch').val();
		var timing = $('#timingSearch').val();
		var vehiclePlant = $('#vehiclePlantSearch').val();
		var vehicleModel = $('#vehicleModelParam').val();
		var unitPlant = $('#unitPlantParam').val();
		var unitType = $('#unitTypeParam').val();
		var unitModel = $('#unitModelParam').val();
		var uploadStatus = $('#uploadStatus').val();
		var uploadType = $('#uploadTypeParam').val();
		
		if('K' == uploadType && 'SC' == uploadStatus){
			GWRDSLib.dialog.open("VanningAdjustmentDialog", _rootPath
					+ "/RundownandUnit/vanningAdjustment?getsudoMonth="+ getsudoMonth 
					+ "&timing=" + timing 
					+ "&vehiclePlant="+ vehiclePlant 
					+ "&vehicleModel=" + vehicleModel
					+ "&unitModel=" + unitModel 
					+ "&unitPlant=" + unitPlant
					+ "&unitType=" + unitType,
					"WBW02111 : Vanning Adjustment Screen", 1000, 500);
		}else if('K' == uploadType && 'ER' == uploadStatus){
			doDownloadDetailReport(uploadType);
		}else if('R' == uploadType && 'ER' == uploadStatus){
			doDownloadDetailReport(uploadType);
		}
	}
	
	window.doDownloadDetailReport =
		function doDownloadDetailReport(uploadType){
			GWRDSLib.clearST3Message();
			restoreSearchCriteria();
			$('#WBW02110Browse').val('');
			var searchForm = $('#search-form');
			var uploadForm = $('#upload-form');

			$('#WBW02110Browse').removeClass('MandatoryField');
			if (formValidateError(searchForm) && formValidateError(uploadForm)) {
				$('#WBW02110Browse').addClass('MandatoryField');
				
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
				        	}else {
				        		if(objSerialize.infoMessages.length > 0) {
									//ST3Lib.message.addInfo(objSerialize.infoMessages);
									//ST3Lib.message.show(1);
									
									document.getElementById("upload-form").reset();
									
					            	//loading.close();
								}
				        	}
						}
//						enableButtonBaseOnStatus(selectedCheckbox);
						enableButtonBaseOnStatus();
						appId = '';
						//loading.close();
					}catch(e){}
				});
				
				var params = 'getsudoMonthDL='+lastSearchCriteria.getsudoMonthSearch
							+'&timingDL='+lastSearchCriteria.timingSearch
							+'&vehiclePlantDL='+lastSearchCriteria.vehiclePlantSearch
							+'&vehicleModelDL='+$('#vehicleModelParam').val()
							+'&unitPlantDL='+$('#unitPlantParam').val()
							+'&unitModelDL='+$('#unitModelParam').val()
							+'&uploadTypeDL='+$('#uploadTypeParam').val()
							+'&appIdStr='+appId;
				
				//check data on iframe to get error message
				searchForm.attr('action', mappingPath + '/download?'+params).attr('target', 'downloadIframe')
					.removeAttr('ajax')
					.submit();
				searchForm.attr('ajax', ajax).removeAttr('target').attr('action', action);
				$('#getsudoMonthSearch').focus();
				checkDownloadStatus(loading, appId, $('#uploadTypeParam').val());				
			}
		};	

		function checkDownloadStatus(loadingDownload, appIdStr, uploadTypeDL){
			setTimeout(function(){
				$.ajax({
					  method: "POST",
					  url: mappingPath+'/checkStatusOfLogDownload',
					  async: false,
					  data: { uploadTypeDL : uploadTypeDL,
						      appIdStr : appIdStr }
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
//							enableButtonBaseOnStatus(selectedCheckbox);
							enableButtonBaseOnStatus();
			        	}else {
			        		checkDownloadStatus(loadingDownload, appIdStr, uploadTypeDL);
			        	}
					})
					.error(function() {
						loadingDownload.close();
						loadingDownload = null;
						appId = '';
//						enableButtonBaseOnStatus(selectedCheckbox);
						enableButtonBaseOnStatus();
					});
			}, downloadTimingCheckStatus);
		}
		
	window.doFix = function doFix() {
		GWRDSLib.clearST3Message();
		$('#WBW02110Browse').val('');
		ST3Lib.dialog.confirm(MBW00004ACFM_FIX, 'MBW00004ACFM', function(ret){
			if (!ret) {
				return;
			}else{
				var loadingTarget = "#screen-panel";
				var loading = ST3Lib.dialog.loading(loadingTarget);
				$.ajax({
					method : "POST",
					url : mappingPath + '/doFix',
					data : {
						getsudoMonth : $('#getsudoMonthSearch').val(),
						timing : $('#timingSearch').val(),
						vehiclePlant : $('#vehiclePlantSearch').val(),
						vehicleModel : $('#vehicleModelParam').val(),
						unitPlant : $('#unitPlantParam').val(),
						unitType : $('#unitTypeParam').val(),
						unitModel : $('#unitModelParam').val(),
						updateDtStr : $('#updateDtStr').val(),
						vehicleModelSearch : $('#vehicleModelSearch').val(),
						unitPlantSearch : $('#unitPlantSearch').val(),
						unitModelSearch : $('#unitModelSearch').val()
					}
				})
				.done(
					function(data1) {
						var objPayload = JSON.parse(data1);
						if (typeof objPayload.status != 'undefined') {
							ST3Lib.message.clear();
							if (objPayload.errorMessages != "" && typeof objPayload.errorMessages != 'undefined') {
								ST3Lib.message.addError(objPayload.errorMessages);
								ST3Lib.message.show(1);
							} else if (objPayload.infoMessages.length > 0) {
								ST3Lib.message.addInfo(objPayload.infoMessages);
								ST3Lib.message.show(1);
							}
							
							data1 = JSON.parse(data1);
							
							dataForm = objPayload.objectForm;
							if (dataForm!=null) { 
								$.each(dataForm, function(item, value){
						            $("#"+item).val(value);
						        });  
							}
							generateDataList(data1);
							
							document.getElementById("upload-form").reset();
							$('#WBW02110Upload').removeAttr('disabled');

							checkConfirmMessageBeforeExit = false;
							$('#WBW02110Browse').val('');
							selectedCheckbox = null;
							enableButtonBaseOnStatus();
							loading.close();
						} else {
							$('#WBW02110Browse').val('');
							checkConfirmMessageBeforeExit = true;
//							enableButtonBaseOnStatus(selectedCheckbox);
							enableButtonBaseOnStatus();
							loading.close();
							loading = null;
						}
				}).error(function() {
					$('#WBW02110Browse').val('');
//					enableButtonBaseOnStatus(selectedCheckbox);
					enableButtonBaseOnStatus();
					loading.close();
					loading = null;
				});
			}
		});
	};

	window.doUpload = function doUpload() {
		GWRDSLib.clearST3Message();
		restoreSearchCriteria();
		var doSkipUpload = false;
		var searchForm = $('#search-form');
		var uploadForm = $('#upload-form');
		if ($('input[name="validateTypesRad"]:checked').val()) {
			var radstr = $('input[name="validateTypesRad"]:checked').val();
			if ("S" == radstr) {
				$('#WBW02110Browse').removeClass('MandatoryField');
				doSkipUpload = true;
			} else {
				$('#WBW02110Browse').addClass('MandatoryField');
				doSkipUpload = false;
			}
		}
		if (formValidateError(searchForm) && formValidateError(uploadForm)
				&& formCustomValidateError(uploadForm)) {
			checkConfirmMessageBeforeExit = true;
			var loadingTarget = "#screen-panel";
			loadingUpload = ST3Lib.dialog.loading(loadingTarget);
			if (doSkipUpload) {
				$.ajax({
						method : "POST",
						url : mappingPath + '/skipUpload',
						data : {
							getsudoMonth : $('#getsudoMonthSearch').val(),
							timing : $('#timingSearch').val(),
							vehiclePlant : $('#vehiclePlantSearch').val(),
							vehicleModel : $('#vehicleModelParam').val(),
							unitPlant : $('#unitPlantParam').val(),
							unitType : $('#unitTypeParam').val(),
							unitModel : $('#unitModelParam').val(),
							uploadType : $('.radioButtonField:checked').val(),
							updateDtStr : $('#updateDtStr').val(),
							vehicleModelSearch : $('#vehicleModelSearch').val(),
							unitPlantSearch : $('#unitPlantSearch').val(),
							unitModelSearch : $('#unitModelSearch').val()
						}
					})
				.done(function(data1) {
					var objPayload = JSON.parse(data1);
					if (typeof objPayload.status != 'undefined') {
						ST3Lib.message.clear();
						if (objPayload.errorMessages != "" && typeof objPayload.errorMessages != 'undefined') {
							ST3Lib.message.addError(objPayload.errorMessages);
							ST3Lib.message.show(1);
						} else if (objPayload.infoMessages.length > 0) {
							ST3Lib.message.addInfo(objPayload.infoMessages);
							ST3Lib.message.show(1);
						}
						document.getElementById("upload-form").reset();
						$('#WBW02110Upload').removeAttr('disabled');

						dataForm = objPayload.objectForm;
						if (dataForm!=null) { 
							$.each(dataForm, function(item, value){
					            $("#"+item).val(value);
					        });  
						}
						
						generateDataList(objPayload);
						
						checkConfirmMessageBeforeExit = false;
						$('#WBW02110Browse').val('');
						loadingUpload.close();
						selectedCheckbox = null;
		            	enableButtonBaseOnStatus();
					} else {
						$('#WBW02110Browse').val('');
						checkConfirmMessageBeforeExit = true;
						loadingUpload.close();
						loadingUpload = null;
						selectedCheckbox = null;
		            	enableButtonBaseOnStatus();
					}
				}).error(function() {
					$('#WBW02110Browse').val('');
					loadingUpload.close();
					loadingUpload = null;
//					enableButtonBaseOnStatus(selectedCheckbox);
					enableButtonBaseOnStatus();
					checkConfirmMessageBeforeExit = false;
				});
			} else {
				uploadForm.ajaxForm(
					{
						url : mappingPath + '/upload',
						type : 'POST',
						data : {
							uploadType : $('.radioButtonField:checked').val(),
							getsudoMonth : $('#getsudoMonthSearch').val(),
							timing : $('#timingSearch').val(),
							vehiclePlant : $('#vehiclePlantSearch').val(),
							vehicleModel : $('#vehicleModelParam').val(),
							unitPlant : $('#unitPlantParam').val(),
							unitType : $('#unitTypeParam').val(),
							unitModel : $('#unitModelParam').val(),
							vehicleModelSearch : $('#vehicleModelSearch').val(),
							unitPlantSearch : $('#unitPlantSearch').val(),
							unitModelSearch : $('#unitModelSearch').val(),
							uploadStatus : $('#uploadStatus').val(),
							updateDtPAMs : $('#updateDtPAMs').val(),
							updateDtKompo : $('#updateDtKompo').val(),
						},
						dataType : 'text',
						success : function(data) {
							if (data && typeof data != 'undefined') {
								var objSerialize = JSON.parse(data);

								if (objSerialize.errorMessages != "" && typeof objSerialize.errorMessages != 'undefined') {
									ST3Lib.message.addError(objSerialize.errorMessages);
									ST3Lib.message.show(1);

									$('#WBW02110Upload').removeAttr('disabled');
									loadingUpload.close();
									loadingUpload = null;
//									enableButtonBaseOnStatus(selectedCheckbox);
									enableButtonBaseOnStatus();
									checkConfirmMessageBeforeExit = false;
								} else {
									if (objSerialize.infoMessages.length > 0) {
										appId = objSerialize.appId;
										checkUploadStatus(loadingUpload, appId);
									}
								}
							}
						},
						error : function(data) {
							$('#WBW02110Browse').val('');
							$('#WBW02110Upload').removeAttr('disabled');
							loadingUpload.close();
							loadingUpload = null;
//							enableButtonBaseOnStatus(selectedCheckbox);
							enableButtonBaseOnStatus();
							checkConfirmMessageBeforeExit = false;
						}
					}).submit();
			}
		}
	};
	
	function checkUploadStatus(loadingUpload, appId) {
		setTimeout(function() {
			$.ajax({
					method : "POST",
					url : mappingPath + '/checkStatusOfLogUpload',
					async : false,
					data : {appId : appId, 
							uploadTypeRadio : $('.radioButtonField:checked').val(),
							getsudoMonthSearch : $('#getsudoMonthSearch').val(),
							timingSearch : $('#timingSearch').val(),
							vehiclePlantSearch : $('#vehiclePlantSearch').val(),
							vehicleModelSearch : $('#vehicleModelSearch').val(),
							unitPlantSearch : $('#unitPlantSearch').val(),
							unitModelSearch : $('#unitModelSearch').val()}
					}).done(function(data1) {
						var objPayload = JSON.parse(data1);
						if (objPayload.status == "OK" && typeof objPayload.status != 'undefined') {
							ST3Lib.message.clear();
							if (objPayload.errorMessages != "" && typeof objPayload.errorMessages != 'undefined') {
								ST3Lib.message.addError(objPayload.errorMessages);
								ST3Lib.message.show(1);
							} else if (objPayload.infoMessages.length > 0) {
								ST3Lib.message.addInfo(objPayload.infoMessages);
								ST3Lib.message.show(1);
							}
							document.getElementById("upload-form").reset();
							$('#WBW02110Upload').removeAttr('disabled');

							checkConfirmMessageBeforeExit = false;
							$('#WBW02110Browse').val('');
							loadingUpload.close();
							
							dataForm = objPayload.objectForm;
							if (dataForm!=null) { 
								$.each(dataForm, function(item, value){
						            $("#"+item).val(value);
						        });  
							}
							
							//refresh search data
							generateDataList(objPayload);

							selectedCheckbox = null;
			            	enableButtonBaseOnStatus();
						} else {
							$('#WBW02110Browse').val('');
							checkConfirmMessageBeforeExit = true;
							checkUploadStatus(loadingUpload, appId);
						}
					}).error(function() {
						$('#WBW02110Browse').val('');
						loadingUpload.close();
						loadingUpload = null;
//						enableButtonBaseOnStatus(selectedCheckbox);
						enableButtonBaseOnStatus();
						checkConfirmMessageBeforeExit = false;
					});
		}, uploadTimingCheckStatus);
	}

	window.moveHScrollBar = function moveHScrollBar(value) {
		$('#search-result .dataTable-wrapper').scrollLeft(0);
	};

	window.moveToLastSelectId = function moveToLastSelectId(tableSelector, id) {
		return ST3Lib.dataTable.moveToLastSelectId(tableSelector, id);
	};

	window.getSelectId = function getSelectId(tableSelector, index) {
		return ST3Lib.dataTable.getSelectId(tableSelector, index);
	};

	window.searchValidateError = function searchValidateError() {
		return;
	};

})(ST3Lib.$);