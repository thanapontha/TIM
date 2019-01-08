(function($){
	var appId = '';
	var lastSearchCriteria = {};
	var totalLenght = 0;
	var lastAction = '';
	var UPDATE_KEYS_SEPERATER = '|';
	
	SECTION_VEHICLE = "Vehicle"+"H";
	UNIT_TYPE_ENGINE = "Engine"+"H";
	UNIT_TYPE_TRANSMISSION = "Transmission"+"H";
	UNIT_TYPE_OTHER = "Other"+"H";
	SECTION_TOTAL = "Total";
	SECTION_BLANK_LINE = "BLANK_LINE";
	
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
	        	$('#WBW01110Browse').val('');
	        	appId = '';
	        	if(loadingUpload){
	        		$('#dataNotFound').val('');
	        		$('#kaikiengStatus').val('');
	        		$('#enableFixButton').val('');
	        		enableButtonBaseOnVersionAndStatus();
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
		
		enableButtonBaseOnVersionAndStatus();
		
		$('#getsudoMonthSearch').focus();
		
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
					getsudoMonthSearchChange();
				}
			}
	    });	
		
		var prevGetsudoSearch = $('#getsudoMonthSearch').val();
		
		window.getsudoMonthSearchChange = function getsudoMonthSearchChange(){
			$("#vehiclePlantSearch").val('');
			$("#vehicleModelSearch").val('');
			var $vehicleModelSearch = $('#vehicleModelSearch');
			$vehicleModelSearch.empty();
			$vehicleModelSearch.append('<option value="">&lt;Select&gt;</option>');
		}
		validateDateMMMYY(document.getElementById('getsudoMonthSearch'));
		
		$("#getsudoMonthSearch").change(function() {
			prevGetsudoSearch = $('#getsudoMonthSearch').val();
			getsudoMonthSearchChange();
		});
		
		window.objectEditor = ST3Lib.roweditor( '#result', {
			template: "#datatemplate",
			success: function( type, tr ){
				if ( tr.index() %2 > 0 ) tr.addClass( 'even' );
				else tr.addClass( 'odd' );
				if ( type === 'add' ) {
					//do nothing
				}else if (type === 'edit') {																	
					ST3Lib.message.clear();
					ST3Lib.message.hide(true);
				}else if ( type === 'cancel' ) {
					// to do
				}
			}
		});
		
		$('#vehiclePlantSearch').change(function() {
			$.ajax({
				  method: "POST",
				  url: mappingPath+'/vehiclePlantSearchChange',
				  async : false,
				  data: { getsudoMonth : $("#getsudoMonthSearch").val(), 
					      vehiclePlant : $("#vehiclePlantSearch").val()}
				})
				.done(function( data ) {
					var $vehicleModelSearch = $('#vehicleModelSearch');
					$vehicleModelSearch.empty();
					$vehicleModelSearch.append('<option value="">&lt;Select&gt;</option>');
					if(data){						
						for(var i = 0; i < data.length; ++i) {
							var obj = data[i];
							$vehicleModelSearch.append('<option value="{0}">{1}</option>'.format(obj.stValue, obj.stLabel));
						}
					}
				})
				.error(function() {});
		});
		
	});
	
	window.diableOnEditMode =
		function diableOnEditMode() {
			ST3Lib.content.disabled( '#search-field', true );
			ST3Lib.validate.disabledButton($('#search-panel'), true);
			ST3Lib.validate.disabledButton($('#operation-panel'), true);
			ST3Lib.validate.disabledButtonSpecify( '#WBW01110Browse', true );
			$("#search-form").find(".getsudoMonth").css("display", "none");
		}

	window.clickSearch = 
		function clickSearch(){
			lastAction = 'search';
			GWRDSLib.clearST3Message();			
			$('#WBW01110Browse').val('');

			$('#dataNotFound').val('');
    		$('#kaikiengStatus').val('');
    		$('#enableFixButton').val('');
    		enableButtonBaseOnVersionAndStatus();
    		$('#result-list-form').addClass('hide');
    		$('#search-criteria-label').addClass('hide');
    		clearTableRowAndColumn();
    		
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
	    				setFormFocus("getsudoMonthSearch");
	    			}
	    		} else {
	    			dataForm = data.objectForm;
	    			
	    			if (dataForm != null) {
	    				//ST3Lib.content.disabled( '#data-head-panel', false );
	    				$.each(dataForm, function(item, value){
	    		            $("#"+item).val(value);
	    		            //console.log($("#"+item).val(value));
	    		        });
	    				ST3Lib.content.disabled( '#data-head-panel', true );
	    				
	    				generateDataList(data);
	    			}
	    		}
			}
			loading.close();
			$("#search-form").find(".getsudoMonth").css("display", "");
		};
		
		function clearTableRowAndColumn(){
			$('#result-header th').remove();
			$('#result-body td').remove();
		}
		
		window.generateDataList =
			function generateDataList(datas){
			var dataList = datas.objectsInfoList;
			var menuList = datas.objectsInfo2List;
			if(menuList){
				checkMonth = datas.objectForm.checkMonth;
				headerGm = menuList[0];
				 $('input[name=getsudoMonths]').val(headerGm);
				if(dataList){
					clearTableRowAndColumn();
					//time to generate header
					//generate empty header
					$('#result-header').append('<th class="colmenuheader sorting_disabled" role="columnheader" rowspan="1" colspan="1"></th>');
					for(var col=1; col < headerGm.length; col++){
						if(col==1){							
						}else{
							$('#result-header').append( '<th class="colheader sorting_disabled" role="columnheader" rowspan="1" colspan="1">'
									+ headerGm[col] +'</th>');
						}
					}
					
					for(var row=0; row < dataList.length; row++){
						$('#result-body').append('<tr id=body-row-'+row+'>');
						var dataRow = dataList[row];
						var valueCol0 = "";
						var rowid0 = "";
						for(var col=0; col < dataRow.length; col++){
							var valueOfColume = dataRow[col];
							if(col==0){
								valueCol0 = dataRow[0];
								rowid0 = ""+row+col;
							}else if (col == 1){
								var classStr = "colmenubody";
								var hiddenInputCol0 = "";
								var hiddenInputCol1 = "";
								if(valueCol0 == SECTION_VEHICLE || valueCol0 == UNIT_TYPE_ENGINE 
										|| valueCol0 == UNIT_TYPE_TRANSMISSION || valueCol0 == UNIT_TYPE_OTHER ){
									
									classStr = "colmenubodyH";
									if(valueCol0 == SECTION_VEHICLE){
										hiddenInputCol0 = '<input type="hidden" name="row'+rowid0+'" id="row'+rowid0+'" class="NoMandatoryField" value="'+valueCol0+'">';
										hiddenInputCol1 = '<input type="hidden" name="row'+row+col+'" id="row'+row+col+'" class="NoMandatoryField" value="'+valueOfColume+'">';
									}
								}else if (valueCol0 == SECTION_TOTAL ){
									classStr = "colTotalbody";
								}else if(valueCol0==SECTION_BLANK_LINE){
									classStr = "colBlankbody";
									valueOfColume = "";
								}else{
									hiddenInputCol0 = '<input type="hidden" name="row'+rowid0+'" id="row'+rowid0+'" class="NoMandatoryField" value="'+valueCol0+'">';
									hiddenInputCol1 = '<input type="hidden" name="row'+row+col+'" id="row'+row+col+'" class="NoMandatoryField" value="'+valueOfColume+'">';
								}
															
								$('#body-row-'+row).append( '<td class="'+classStr+' sorting_disabled" role="columnbody" rowspan="1" colspan="1">'
										+hiddenInputCol0+' '+hiddenInputCol1
										+'<label for="row'+row+col+'">'+valueOfColume+'</label> </td>');
							}else{
								if(valueCol0==SECTION_BLANK_LINE){
									$('#body-row-'+row).append( '<td class="colBlankbody sorting_disabled" role="columnbody" rowspan="1" colspan="1">'
											+'<label for="row'+row+col+'"> </label> </td>');
								}else{
									var title = headerGm[col]+"("+dataRow[1]+")";
									var rowarr = valueOfColume.split(":");
									var volume = "";
									var enableFlag = rowarr[1];
									if(enableFlag == "Y"){
										volume = rowarr[0];
									}
									var classlabel = "control-label";
									var onblurCalTotal = "";
									var classForCalTotal = "";
																		
									if(valueCol0 == UNIT_TYPE_ENGINE || valueCol0 == UNIT_TYPE_TRANSMISSION || valueCol0 == UNIT_TYPE_OTHER){
										
										classlabel = "control-labelH";
										$('#body-row-'+row).append( '<td class="colheader" role="columnbody" rowspan="1" colspan="1">'
												+'<label for="row'+row+col+'" class="'+classlabel+'">'+rowarr[0]+'</label> </td>');
										
										
									}else if(valueCol0 == SECTION_TOTAL){
										var tmpData = dataList[row-1];
										var section = tmpData[0];
										
										classForCalTotal = "Total"+section+headerGm[col];
										classlabel = "control-labelH";
										$('#body-row-'+row).append( '<td class="colTotalbody2" role="columnbody" rowspan="1" colspan="1">'
												+'<label for="row'+row+col+'" class="'+classlabel+'" id="'+classForCalTotal+'">'+valueOfColume+'</label> </td>');
									}else{
										
										var classInputStr = menucolumn[3];
										if(col-2 < checkMonth){
											if(enableFlag == "Y" && valueCol0 !== 'Other'){
												classInputStr = classInputStr+' MandatoryField';
											}
										}
										classForCalTotal = valueCol0+headerGm[col];
										onblurCalTotal = 'onblur="calculateTotal(this,\''+classForCalTotal+'\')"';
										
										var displayformat = menucolumn[4];
										
										$('#body-row-'+row).append( '<td class="col1 sorting_disabled '+classForCalTotal+'" role="columnbody" rowspan="1" colspan="1">'
												+'<label for="row'+row+col+'" class="'+classlabel+'">'+volume+'</label>'
												
												+'<input type="text"  display-format="'+displayformat+'"  class="'+classInputStr
												+' edit-input" title="'+title+'" id="row'+row+col+'" name="row'+row+col+'" value="'+volume+'" '+onblurCalTotal+'>'
												+'</td>');
										
										validateNumber(document.getElementById('row'+row+col), menucolumn[1], menucolumn[2], false);
										//validateDecimal(document.getElementById('row'+row+col), menucolumn[1], menucolumn[2], '');
										
										if(enableFlag=="Y"){
											$('#row'+row+col).attr('toenable', true);
										}
									}
								}
							}
						}
						$('#result-body').append('</tr>');
						//console.log(dataList[i]);
					}
				}
				searchFoundMode();
				$('#kaikiengStatus').val(datas.objectForm.kaikiengStatus);
				$('#enableFixButton').val(datas.objectForm.enableFixButton);				
				$('#dataNotFound').val(datas.objectForm.dataNotFound);
				version = datas.objectForm.version;
				enableButtonBaseOnVersionAndStatus();
			}
		}
		
		window.calculateTotal =
			function calculateTotal(obj, classForCalTotal) {		
				var arr = [];
				var total = '';
				$('.'+classForCalTotal+' input[toenable=true]').each(function(){
					if($(this).val() != ''){
						var tmp = new Number($(this).val().replace(/,/g, ''));
						if(tmp){
							total = new Number(total)+tmp;
						}
					}
				});
				$('#Total'+classForCalTotal).text(addCommas(total));
			};
		
	function searchNotFoundMode(){
		ST3Lib.content.disabled('#search-criteria', false);
		ST3Lib.validate.disabledButton($('#search-panel'), false);
		$('#save-panel').addClass('hide');		
		$('#result-list-form').addClass('hide');
		clearTableRowAndColumn();
		$('#search-criteria-label').addClass('hide');
	}
	
	function searchFoundMode(){			
		ST3Lib.content.disabled('#search-result', true);
		ST3Lib.content.disabled('#search-criteria', false);
		
		ST3Lib.validate.disabledButton($('#search-panel'), false);
		$('#save-panel').addClass('hide');
		$('#result-list-form').removeClass('hide');
		$('#search-criteria-label').removeClass('hide');
	}	
	
	function enableButtonBaseOnVersionAndStatus(){
		var dataNotFound = $('#dataNotFound').val();
		var kaikiengStatus = $('#kaikiengStatus').val();
		var enableFixButton = $('#enableFixButton').val();
		if(dataNotFound == 'Y'){
			if(version == TDEM){
				jQuery("#WBW01110Submit").hide();
				jQuery("#WBW01110RejectKaikieng").show();
				jQuery("#WBW01110Fix").show();
				
				ST3Lib.validate.disabledButtonSpecify( '#WBW01110Upload', true );
				ST3Lib.validate.disabledButtonSpecify( '#WBW01110Browse', true );
				ST3Lib.validate.disabledButtonSpecify('#WBW01110Edit', true);
				ST3Lib.validate.disabledButtonSpecify( '#WBW01110Fix', true );
				ST3Lib.validate.disabledButtonSpecify( '#WBW01110RejectKaikieng', true );
				if(kaikiengStatus == 'MP'){
					ST3Lib.validate.disabledButtonSpecify( '#WBW01110Download', false );
				}else{
					ST3Lib.validate.disabledButtonSpecify( '#WBW01110Download', true );
				}
			}else{
				jQuery("#WBW01110Submit").show();
				jQuery("#WBW01110RejectKaikieng").hide();
				jQuery("#WBW01110Fix").hide();
				
				ST3Lib.validate.disabledButtonSpecify('#WBW01110Edit', false);
				ST3Lib.validate.disabledButtonSpecify( '#WBW01110Upload', false );
				ST3Lib.validate.disabledButtonSpecify( '#WBW01110Browse', false );
				ST3Lib.validate.disabledButtonSpecify( '#WBW01110Download', false );

				ST3Lib.validate.disabledButtonSpecify( '#WBW01110Submit', true );
			}
		}else{
			if(version == TDEM){
				jQuery("#WBW01110Submit").hide();
				jQuery("#WBW01110RejectKaikieng").show();
				jQuery("#WBW01110Fix").show();
				
				if(kaikiengStatus == '' || kaikiengStatus == 'FX'){
					ST3Lib.validate.disabledButtonSpecify( '#WBW01110Upload', true );
					ST3Lib.validate.disabledButtonSpecify( '#WBW01110Browse', true );
					ST3Lib.validate.disabledButtonSpecify('#WBW01110Edit', true);
					ST3Lib.validate.disabledButtonSpecify( '#WBW01110Fix', true );
					ST3Lib.validate.disabledButtonSpecify( '#WBW01110RejectKaikieng', true );	
					if(kaikiengStatus == 'FX'){					
						ST3Lib.validate.disabledButtonSpecify( '#WBW01110Download', false );
					}else{
						ST3Lib.validate.disabledButtonSpecify( '#WBW01110Download', true );
					}
				}else{
					ST3Lib.validate.disabledButtonSpecify('#WBW01110Edit', false);
					if("YES"===enableFixButton){
						ST3Lib.validate.disabledButtonSpecify( '#WBW01110Fix', false );
					}else{
						ST3Lib.validate.disabledButtonSpecify( '#WBW01110Fix', true );
					}					
					ST3Lib.validate.disabledButtonSpecify( '#WBW01110RejectKaikieng', false );
					ST3Lib.validate.disabledButtonSpecify( '#WBW01110Browse', false );
					ST3Lib.validate.disabledButtonSpecify( '#WBW01110Upload', false );
					ST3Lib.validate.disabledButtonSpecify( '#WBW01110Download', false );
				}
			}else{
				jQuery("#WBW01110Submit").show();
				jQuery("#WBW01110RejectKaikieng").hide();
				jQuery("#WBW01110Fix").hide();
				
				if(kaikiengStatus == '' || kaikiengStatus == 'SM'){
					ST3Lib.validate.disabledButtonSpecify('#WBW01110Edit', true);
					ST3Lib.validate.disabledButtonSpecify( '#WBW01110Submit', true );
					ST3Lib.validate.disabledButtonSpecify( '#WBW01110Upload', true );
					ST3Lib.validate.disabledButtonSpecify( '#WBW01110Browse', true );
					if(kaikiengStatus == 'SM'){
						ST3Lib.validate.disabledButtonSpecify( '#WBW01110Download', false );
					}else{
						ST3Lib.validate.disabledButtonSpecify( '#WBW01110Download', true );
					}
				}else{
					ST3Lib.validate.disabledButtonSpecify( '#WBW01110Upload', false );
					ST3Lib.validate.disabledButtonSpecify( '#WBW01110Browse', false );
					ST3Lib.validate.disabledButtonSpecify('#WBW01110Edit', false);
					ST3Lib.validate.disabledButtonSpecify( '#WBW01110Submit', false );
					ST3Lib.validate.disabledButtonSpecify( '#WBW01110Download', false );
				}
			}
		}
	}
	
	window.clearSearch =
		function clearSearch(){
			checkConfirmMessageBeforeExit = false;
			document.getElementById("search-form").reset();	
			$('#save-panel').addClass('hide');
			
			searchNotFoundMode();
			ST3Lib.validate.disabledButton( '#operation-panel', true );
			ST3Lib.validate.disabledButtonSpecify( '#WBW01110Browse', true );
			$("#search-form").find(".getsudoMonth").css("display", "");
			$('#WBW01110Browse').val('');
			$('#getsudoMonthSearch').focus();
			GWRDSLib.clearST3Message();
			lastSearchCriteria = {};
		};
	
		function restoreSearchCriteria(){
			$("#getsudoMonthSearch").val(lastSearchCriteria.getsudoMonthSearch);
			$("#timingSearch").val(lastSearchCriteria.timingSearch);
			$("#vehiclePlantSearch").val(lastSearchCriteria.vehiclePlantSearch);
			//load combo box of vehicleModel before set value
			$("#vehiclePlantSearch").change();	

			$("#vehicleModelSearch").val(lastSearchCriteria.vehicleModelSearch);
		}
		
		function saveSearchCriteria(){
			lastSearchCriteria.getsudoMonthSearch = $("#getsudoMonthSearch").val();
			lastSearchCriteria.timingSearch = $("#timingSearch").val();
			lastSearchCriteria.vehiclePlantSearch = $("#vehiclePlantSearch").val();
			lastSearchCriteria.vehicleModelSearch = $("#vehicleModelSearch").val();
			
			$('#getsudoMonthCriteria').text(lastSearchCriteria.getsudoMonthSearch);
			$('#timingCriteria').text(lastSearchCriteria.timingSearch);
			$('#vehiclePlantCriteria').text(lastSearchCriteria.vehiclePlantSearch);
			$('#vehicleModelCriteria').text(lastSearchCriteria.vehicleModelSearch);
		}
	
	window.editObject =
		function editObject() {
			$('#save-panel').removeClass('hide');
			$('.edit-input').show();
			$('.control-label').hide();
			GWRDSLib.clearST3Message();
			diableOnEditMode();
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
			checkConfirmMessageBeforeExit = true;
			restoreSearchCriteria();
			$('#WBW01110Browse').val('');
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
			ST3Lib.dialog.confirm(MBW00002ACFM, 'MBW00002ACFM', function(ret){
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
						
						var params = 'version='+version
									+'&getsudoMonthSearch='+lastSearchCriteria.getsudoMonthSearch
									+'&timingSearch='+lastSearchCriteria.timingSearch
									+'&vehiclePlantSearch='+lastSearchCriteria.vehiclePlantSearch
									+'&vehicleModelSearch='+lastSearchCriteria.vehicleModelSearch;
						
						$('#result-list-form').attr('action', mappingPath + '/submitEdit?'+params).attr('_method', 'post');												
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
					$("#search-form").find(".getsudoMonth").css("display", "");
					
					restoreSearchCriteria();
	    			var searchForm = $('#search-form');
	    			$('#search-form').submit();
					GWRDSLib.clearST3Message();
				}
				
			});
		};
		
	window.submitKaikieng = 
		function submitKaikieng(){
			$('#WBW01110Browse').val('');
			ST3Lib.dialog.confirm(MBW00004ACFM_SubmitKaikieng, 'MBW00004ACFM', function(ret){
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
						  url: mappingPath+'/submitKaikiengData',
						  data: { version : version, 
							  	  getsudoMonthSearch : lastSearchCriteria.getsudoMonthSearch, 
							  	  timingSearch : lastSearchCriteria.timingSearch, 
							  	  vehiclePlantSearch : lastSearchCriteria.vehiclePlantSearch, 
							  	  vehicleModelSearch : lastSearchCriteria.vehicleModelSearch,
							  	  updateKeySet : $("#updateKeySet").val() }
						})
						.done(function( datas ) {
							if(datas.status === 'OK'){
								checkConfirmMessageBeforeExit = false;
								
								if(datas.infoMessages.length > 0) {
									 ST3Lib.message.addInfo(datas.infoMessages);
									 if(datas.errorMessage) {
										ST3Lib.message.addError(datas.errorMessage);
									 }else if(datas.errorMessages != null && datas.errorMessages.length > 0) {
										ST3Lib.message.addError(datas.errorMessages);
									 }
									 ST3Lib.message.show(1);
								}
								
								dataForm = datas.objectForm;
								if (dataForm!=null) { 
									$.each(dataForm, function(item, value){
							            $("#"+item).val(value);
							        });  
								}
								generateDataList(datas);
								$('#kaikiengStatus').val(dataForm.kaikiengStatus);
								$('#enableFixButton').val(dataForm.enableFixButton);								
								$('#dataNotFound').val(dataForm.dataNotFound);
								version = dataForm.version;
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
									$('#result input:text:enabled:first').focus();
								}
							}
							enableButtonBaseOnVersionAndStatus();
							if (loading) {
								loading.close();
							}
						})
						.error(function() {
							enableButtonBaseOnVersionAndStatus();
							if (loading) {
								loading.close();
							}
						});
				}
			});
		};
		
	window.fixKaikieng = 
		function fixKaikieng(){
			$('#WBW01110Browse').val('');
			ST3Lib.dialog.confirm(MBW00004ACFM_FixKaikieng, 'MBW00004ACFM', function(ret){
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
						  url: mappingPath+'/fixKaikiengData',
						  data: { version : version, 
							  	  getsudoMonthSearch : lastSearchCriteria.getsudoMonthSearch, 
							  	  timingSearch : lastSearchCriteria.timingSearch, 
							  	  vehiclePlantSearch : lastSearchCriteria.vehiclePlantSearch, 
							  	  vehicleModelSearch : lastSearchCriteria.vehicleModelSearch,
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
								$('#kaikiengStatus').val(dataForm.kaikiengStatus);
								$('#enableFixButton').val(dataForm.enableFixButton);								
								$('#dataNotFound').val(dataForm.dataNotFound);
								version = dataForm.version;
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
							}
							enableButtonBaseOnVersionAndStatus();
							if (loading) {
								loading.close();
							}
						})
						.error(function() {
							enableButtonBaseOnVersionAndStatus();
							if (loading) {
								loading.close();
							}
						});
					
				}
			});
		};

	window.rejectKaikieng = 
		function rejectKaikieng(){
			$('#WBW01110Browse').val('');
			ST3Lib.dialog.confirm(MBW00004ACFM_RejectKaikieng, 'MBW00004ACFM', function(ret){
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
						  url: mappingPath+'/rejectKaikiengData',
						  data: { version : version, 
							  	  getsudoMonthSearch : lastSearchCriteria.getsudoMonthSearch, 
							  	  timingSearch : lastSearchCriteria.timingSearch, 
							  	  vehiclePlantSearch : lastSearchCriteria.vehiclePlantSearch, 
							  	  vehicleModelSearch : lastSearchCriteria.vehicleModelSearch,
							  	  updateKeySet : $("#updateKeySet").val() }
						})
						.done(function( datas ) {
							if(datas.status === 'OK'){
								checkConfirmMessageBeforeExit = false;
								
								if(datas.infoMessages.length > 0) {
									 ST3Lib.message.addInfo(datas.infoMessages);
									 if(datas.errorMessage) {
										ST3Lib.message.addError(datas.errorMessage);
									 }else if(datas.errorMessages != null && datas.errorMessages.length > 0) {
										ST3Lib.message.addError(datas.errorMessages);
									 }
									 ST3Lib.message.show(1);
								}
								
								dataForm = datas.objectForm;
								if (dataForm!=null) { 
									$.each(dataForm, function(item, value){
							            $("#"+item).val(value);
							        });  
								}
								generateDataList(datas);
								$('#kaikiengStatus').val(dataForm.kaikiengStatus);
								$('#enableFixButton').val(dataForm.enableFixButton);								
								$('#dataNotFound').val(dataForm.dataNotFound);
								version = dataForm.version;
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
							}

							enableButtonBaseOnVersionAndStatus();
							if (loading) {
								loading.close();
							}
						})
						.error(function() {
							enableButtonBaseOnVersionAndStatus();
							if (loading) {
								loading.close();
							}
						});
					
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
			ST3Lib.content.disabled( '#data-head-panel', true );
			generateDataList(datas);
			$("#search-form").find(".getsudoMonth").css("display", "");
		}else if(datas.status === 'WARN'){
			$("#search-form").find(".getsudoMonth").css("display", "");
			ST3Lib.dialog.confirm(MBW00006ACFM, 'MBW00006ACFM', function(ret){
				if (!ret) {
					return;
				}else{
					restoreSearchCriteria();
					
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
					
					var params = 'version='+version
								+'&confirmAdjustVolume=Y'
								+'&getsudoMonthSearch='+lastSearchCriteria.getsudoMonthSearch
								+'&timingSearch='+lastSearchCriteria.timingSearch
								+'&vehiclePlantSearch='+lastSearchCriteria.vehiclePlantSearch
								+'&vehicleModelSearch='+lastSearchCriteria.vehicleModelSearch;
					
					$('#result-list-form').attr('action', mappingPath + '/submitEdit?'+params).attr('_method', 'post');
					
					var saveForm = $('#result-list-form');	
					saveForm.submit();
				}
			});
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
				
				ST3Lib.content.disabled( '#updateKeySet', false );
				
				uploadForm.ajaxForm({ 
		            url: mappingPath+'/upload', 
		            type: 'POST',
		            data: { getsudoMonthSearch : $("#getsudoMonthSearch").val(), 
		            		timingSearch : $("#timingSearch").val(),
		            	    vehiclePlantSearch : $("#vehiclePlantSearch").val(),
		            	    vehicleModelSearch : $("#vehicleModelSearch").val(),
		            	    updateKeySet : $("#updateKeySet").val()},
		            dataType: 'text',
		            success: function(data) {
		            	if(data  && typeof data != 'undefined'){	
		            		var objSerialize = JSON.parse(data);
		            		
			        		if(objSerialize.errorMessages != "" && typeof objSerialize.errorMessages != 'undefined') {
				        		ST3Lib.message.addError(objSerialize.errorMessages);
								ST3Lib.message.show(1);

				            	$('#WBW01110Upload').removeAttr('disabled');
				            	loadingUpload.close();
				            	loadingUpload = null;
				            	enableButtonBaseOnVersionAndStatus();
				            	checkConfirmMessageBeforeExit = false;
				        	}else {
				        		if(objSerialize.infoMessages.length > 0) {
				        			appId = objSerialize.appId;
				        			checkUploadStatus(loadingUpload,appId, $("#updateKeySet").val());             
								}
				        	}
		        		}
		            },
		        	error: function() {
		        		$('#WBW01110Browse').val('');
		        		$('#WBW01110Upload').removeAttr('disabled');
		        		loadingUpload.close();
		            	loadingUpload = null;
		            	enableButtonBaseOnVersionAndStatus();
		            	checkConfirmMessageBeforeExit = false;
		        	}
		        }).submit();	
			}
		};	
		
	function checkUploadStatus(loadingUpload, appId, updateKeySet){
		setTimeout(function(){
			$.ajax({
				  method: "POST",
				  url: mappingPath+'/checkStatusOfLogUpload',
				  async: false,
				  data: { appId : appId,
					  	getsudoMonthSearch : $('#getsudoMonthSearch').val(),
						timingSearch : $('#timingSearch').val(),
						vehiclePlantSearch : $('#vehiclePlantSearch').val(),
						vehicleModelSearch : $('#vehicleModelSearch').val()}
				})
				.done(function( data1 ) {
					var objPayload = JSON.parse(data1);						            		
					if(objPayload.status == "OK" && typeof objPayload.status != 'undefined') {      			
						ST3Lib.message.clear();
						var warning = false;
						if(objPayload.errorMessages != "" && typeof objPayload.errorMessages != 'undefined') {
			        		ST3Lib.message.addError(objPayload.errorMessages);
							ST3Lib.message.show(1);
			        	}else if(objPayload.infoMessages.length > 0) {
							ST3Lib.message.addInfo(objPayload.infoMessages);
							ST3Lib.message.show(1);	
							
							//refresh search data
							dataForm = objPayload.objectForm;
							if (dataForm!=null) { 
								$.each(dataForm, function(item, value){
						            $("#"+item).val(value);
						        });  
							}
							generateDataList(objPayload);
							$('#kaikiengStatus').val(dataForm.kaikiengStatus);
							$('#enableFixButton').val(dataForm.enableFixButton);								
							$('#dataNotFound').val(dataForm.dataNotFound);
							version = dataForm.version;
							
						}else if(objPayload.warningMessages.length > 0) {
							ST3Lib.message.addWarning(objPayload.warningMessages);
							ST3Lib.message.show(1);	
							warning = true;
						}
						if(warning){
							document.getElementById("upload-form").reset();													
							$('#WBW01110Upload').removeAttr('disabled');
			            	
			            	checkConfirmMessageBeforeExit = true;
			            	$('#WBW01110Browse').val('');
			            	loadingUpload.close();
			            	loadingUpload = null;
//			            	enableButtonBaseOnVersionAndStatus();
			            	
							ST3Lib.dialog.confirm(MBW00006ACFM, 'MBW00006ACFM', function(ret){
								if (!ret) {
									appId = '';
									$.ajax({
										  method: "POST",
										  url: mappingPath+'/deleteParkingData',
										  //async : false,
										  data: { getsudoMonthSearch : $("#getsudoMonthSearch").val(), 
							            		  timingSearch : $("#timingSearch").val(),
							            	      vehiclePlantSearch : $("#vehiclePlantSearch").val(),
							            	      vehicleModelSearch : $("#vehicleModelSearch").val()	}
										})
										.done(function( data2 ) {
											checkConfirmMessageBeforeExit = false;
											enableButtonBaseOnVersionAndStatus();
										})
										.error(function() {
											checkConfirmMessageBeforeExit = false;
										});
								}else{
									ST3Lib.content.disabled( '#updateKeySet', false );
									appId = '';
									
									$.ajax({
										  method: "POST",
										  url: mappingPath+'/confirmMoveParkingDataToBase',
										  //async : false,
										  data: { getsudoMonthSearch : $("#getsudoMonthSearch").val(), 
							            		  timingSearch : $("#timingSearch").val(),
							            	      vehiclePlantSearch : $("#vehiclePlantSearch").val(),
							            	      vehicleModelSearch : $("#vehicleModelSearch").val(),
							            	      updateKeySet : updateKeySet}
										})
										.done(function( data3 ) {
											var objPayload3 = JSON.parse(data3);	
											if(objPayload3.errorMessages != "" && typeof objPayload3.errorMessages != 'undefined') {
								        		ST3Lib.message.addError(objPayload3.errorMessages);
												ST3Lib.message.show(1);
								        	}else if(objPayload3.infoMessages.length > 0) {
								        		//ST3Lib.message.clear();
												ST3Lib.message.addInfo(objPayload3.infoMessages);
												ST3Lib.message.show(1);					
												
												//refresh search data
												dataForm = objPayload3.objectForm;
												if (dataForm!=null) { 
													$.each(dataForm, function(item, value){
											            $("#"+item).val(value);
											        });  
												}
												generateDataList(objPayload3);
												$('#kaikiengStatus').val(dataForm.kaikiengStatus);
												$('#enableFixButton').val(dataForm.enableFixButton);								
												$('#dataNotFound').val(dataForm.dataNotFound);
												version = dataForm.version;
											}
											enableButtonBaseOnVersionAndStatus();
											checkConfirmMessageBeforeExit = false;
										})
										.error(function() {
											checkConfirmMessageBeforeExit = false;
										});
								}
							});
							
						}else{
							document.getElementById("upload-form").reset();													
							$('#WBW01110Upload').removeAttr('disabled');
			            	
			            	checkConfirmMessageBeforeExit = false;
			            	$('#WBW01110Browse').val('');
			            	loadingUpload.close();
			            	loadingUpload = null;
			            	enableButtonBaseOnVersionAndStatus();
						}
		        	}else {
		        		$('#WBW01110Browse').val('');
		        		checkConfirmMessageBeforeExit = true;
		        		checkUploadStatus(loadingUpload, appId, updateKeySet);
		        	}
				})
				.error(function() {
					$('#WBW01110Browse').val('');
					loadingUpload.close();
		          	loadingUpload = null;
		          	enableButtonBaseOnVersionAndStatus();
		          	checkConfirmMessageBeforeExit = false;
				});
		}, uploadTimingCheckStatus);
	}
		
	window.doDownload =
		function doDownload(){
			GWRDSLib.clearST3Message();
			restoreSearchCriteria();
			var searchForm = $('#search-form');
			$('#WBW01110Browse').val('');
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
				            	enableButtonBaseOnVersionAndStatus();
				        	}else {
				        		if(objSerialize.infoMessages.length > 0) {
									//ST3Lib.message.addInfo(objSerialize.infoMessages);
									//ST3Lib.message.show(1);
									
									document.getElementById("upload-form").reset();
									
					            	//loading.close();
					            	enableButtonBaseOnVersionAndStatus();
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
							enableButtonBaseOnVersionAndStatus();
			        	}else {
			        		checkDownloadStatus(loadingDownload, appIdStr);
			        	}
					})
					.error(function() {
						loadingDownload.close();
						loadingDownload = null;
						appId = '';
						enableButtonBaseOnVersionAndStatus();
					});
			}, downloadTimingCheckStatus);
		}
		
})(ST3Lib.$);