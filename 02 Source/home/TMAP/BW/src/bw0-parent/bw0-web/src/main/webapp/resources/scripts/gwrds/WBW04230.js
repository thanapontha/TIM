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
	
	var totalLenght = 0;
	var lastSearchCriteria = {};
	var lastAction = '';
	var menuList;
	
	$(function(){
		$('#ui-accordion-menu-header-2').click();
		$('#ui-accordion-menu-header-2').attr('tabindex','-1');
		
		enableDisableButton(false);
		function clearListAll(plantModel,unitModel,unitPlant){
			if(plantModel){
				clearListPlantModel();
			}
			if(unitModel){
				clearListUnitModel();
			}
			if(unitPlant){
				clearListUnitPlant();
			}
		}
		
		function clearListPlant(){
			var $vehicleModelSearch = $('#vehiclePlantSearch');
			$vehicleModelSearch.empty();
			$vehicleModelSearch.append('<option value="">&lt;Select&gt;</option>');
			return $vehicleModelSearch;
		}
		
		function clearListPlantModel(){
			var $vehicleModelSearch = $('#vehicleModelSearch');
			$vehicleModelSearch.empty();
			$vehicleModelSearch.append('<option value="">&lt;Select&gt;</option>');
			return $vehicleModelSearch;
		}
		function clearListUnitModel(){
			var $vehicleModelSearch = $('#unitModelSearch');
			$vehicleModelSearch.empty();
			$vehicleModelSearch.append('<option value="">&lt;Select&gt;</option>');
			return $vehicleModelSearch;
		}
		
		function clearListUnitPlant(){
			var $vehicleModelSearch = $('#unitPlantSearch');
			$vehicleModelSearch.empty();
			$vehicleModelSearch.append('<option value="">&lt;Select&gt;</option>');
			return $vehicleModelSearch;
		}
		
		
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
			clearListAll(true,true,true);
			$.ajax({
				  method: "POST",
				  async : false,
				  url: mappingPath+'/getsudoMonthSearchChange',
				  data: { getsudoMonth : $("#getsudoMonthSearch").val()}
				})
				.done(function( data ) {
					var $vehiclePlantSearch = clearListPlant();
					if(data){						
						for(var i = 0; i < data.length; ++i) {
							var obj = data[i];
							$vehiclePlantSearch.append('<option value="{0}">{1}</option>'.format(obj.stValue, obj.stLabel));
						}
					}
				})
				.error(function() {});
		};
		
		validateDateMMMYY(document.getElementById('getsudoMonthSearch'));
		
		$("#getsudoMonthSearch").change(function() {
			prevGetsudoSearch = $('#getsudoMonthSearch').val();
			getsudoMonthSearchChange();
		});
		
		$('#vehiclePlantSearch').change(function() {
			clearListAll(true,true,true);
			$.ajax({
				  method: "POST",
				  async : false,
				  url: mappingPath+'/vehiclePlantSearchChange',
				  data: { getsudoMonth : $("#getsudoMonthSearch").val(), 
					      vehiclePlant : $("#vehiclePlantSearch").val()}
				})
				.done(function( data ) {
					var $vehicleModelSearch = clearListPlantModel();
					if(data){						
						for(var i = 0; i < data.length; ++i) {
							var obj = data[i];
							$vehicleModelSearch.append('<option value="{0}">{1}</option>'.format(obj.stValue, obj.stLabel));
						}
					}
				})
				.error(function() {});
		});
		
		
		$('#vehicleModelSearch').change(function() {
			clearListAll(false,true,true);
			$.ajax({
				  method: "POST",
				  async : false,
				  url: mappingPath+'/vehicleModelSearchChange',
				  data: { getsudoMonth : $("#getsudoMonthSearch").val(), 
					      vehiclePlant : $("#vehiclePlantSearch").val(),
					      vehicleModel : $("#vehicleModelSearch").val()}
				})
				.done(function( data ) {
					var $vehicleModelSearch = clearListUnitModel();
					if(data){						
						for(var i = 0; i < data.length; ++i) {
							var obj = data[i];
							$vehicleModelSearch.append('<option value="{0}">{1}</option>'.format(obj.stValue, obj.stLabel));
						}
					}
				})
				.error(function() {});
		});
		
		
		$('#unitModelSearch').change(function() {
			clearListAll(false,false,true);
			$.ajax({
				  method: "POST",
				  async : false,
				  url: mappingPath+'/unitModelSearchChange',
				  data: { getsudoMonth : $("#getsudoMonthSearch").val(), 
					      vehiclePlant : $("#vehiclePlantSearch").val(),
					      vehicleModel : $("#vehicleModelSearch").val(),
					      unitModel : $("#unitModelSearch").val()}
				})
				.done(function( data ) {
					var $vehicleModelSearch = clearListUnitPlant();
					if(data){						
						for(var i = 0; i < data.length; ++i) {
							var obj = data[i];
							$vehicleModelSearch.append('<option value="{0}">{1}</option>'.format(obj.stValue, obj.stLabel));
						}
					}
				})
				.error(function() {});
		});
		
		
		window.objectEditor = ST3Lib.roweditor( '#result', {
			template: "#datatemplate",
			success: function( type, tr ){
				if ( tr.index() %2 > 0 ) tr.addClass( 'even' );
				else tr.addClass( 'odd' );
				if (type === 'edit') {
					enableFieldMode(type);																				
					ST3Lib.message.clear();
					ST3Lib.message.hide(true);
				}
			}
		});
		
		
	function enableFieldMode(type) {
		var resultForm = $('#result-list-form');
			
			ST3Lib.content.disabled($(resultForm.find('input[name=strJsonHeader]')), false);
			ST3Lib.content.disabled($(resultForm.find('input[name=strJsonBodyMax]')), false);
			ST3Lib.content.disabled($(resultForm.find('input[name=strJsonBodyMin]')), false);
			
			ST3Lib.content.disabled($(resultForm.find('input[name=vehiclePlantSearch]')), false);
			ST3Lib.content.disabled($(resultForm.find('input[name=vehicleModelSearch]')), false);
			ST3Lib.content.disabled($(resultForm.find('input[name=timingSearch]')), false);
			ST3Lib.content.disabled($(resultForm.find('input[name=unitModelSearch]')), false);
			ST3Lib.content.disabled($(resultForm.find('input[name=unitPlantSearch]')), false);
			ST3Lib.content.disabled($(resultForm.find('input[name=getsudoMonths]')), false);
			ST3Lib.content.disabled($(resultForm.find('input[name=getsudoMonthSearch]')), false);
		}
	});
	
	
	window.clickSearch = 
		function clickSearch(){
			lastAction = 'search';
			GWRDSLib.clearST3Message();
			
			clearTableRowAndColumn();
			$('#search-criteria-label').addClass('hide');
			enableDisableButton(false);
			var searchForm = $('#search-form');
			$('#search-form').submit();
		};
		
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
	    			$('#save-panel').addClass('hide');
					clearTableRowAndColumn();
					enableDisableButton(false);
	    		} else {
	    			dataForm = data.objectForm;
	    			if (dataForm != null) {
	    				ST3Lib.content.disabled( '#data-head-panel', false );
	    				$.each(dataForm, function(item, value){
	    		            $("#"+item).val(value);
	    		        });
	    				
	    				ST3Lib.content.disabled( '#search-field', false );
						ST3Lib.content.disabled($('#search-panel'), false);
						enableDisableButton(true);
	    				generateDataList(data);
	    				$("input[name^='row']").attr('style','display:none;');
	    				searchFoundMode();
	    				$('#getsudoMonthSearch').focus();
	    			}
	    		}
			}
			loading.close();
		};
			
		window.generateDataList =
			function generateDataList(datas){
			var dataList = datas.objectsInfoList;
			 menuList = datas.objectsInfo2List;
			if(menuList){
				if(dataList){
					clearTableRowAndColumn();
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
								
								var valRows = rows[j].split(":");
								var rowarr = valRows;
								
								if(rowarr[1] == 'N'){
									$('#body-row-'+i).append( '<td class="col1 sorting_disabled" role="columnbody" rowspan="1" colspan="1">'
											+'<label for="row'+i+j+'" class="control-label">'+addCommas(returnBlankIsNull(rowarr[0]))+'</label>'
											+'<input type="text" value="'+addCommas(returnBlankIsNull(rowarr[0]))+'"   id="row'+i+j+'" disabled="disabled" class="edit-input edit-disabled"  name="row'+i+j+'" />'
											+'</td>');
								}else{
									$('#body-row-'+i).append( '<td class="col1 sorting_disabled" role="columnbody" rowspan="1" colspan="1">'
											+'<label for="row'+i+j+'" class="control-label">'+addCommas(returnBlankIsNull(rowarr[0]))+'</label>'
											+'<input type="text"  display-format="'+menucolumn[4]+'"  class="'+menucolumn[3]+' edit-input" title="'+rows[1]+' '+menucolumn[0]+ '('+menuList[j-2]+')" id="row'+i+j+'" name="row'+i+j+'" value="'+addCommas(returnBlankIsNull(rowarr[0]))+'"/>'
											+'</td>');
								}
								
								validateDecimal(document.getElementById('row'+i+j), menucolumn[1], menucolumn[2], '00');
							}
						}
						$('#result-body').append('</tr>');
					}
				}
			}
		}
			
		function clearTableRowAndColumn(){
			$('#result-header th').remove();
			$('#result-body td').remove();
		}
			
		function returnBlankIsNull(param){
			if(typeof param == 'object'){
				return '';
			}
			return param;
		}	
			
		window.clearSearch =
			function clearSearch(){
				lastSearchCriteria = {};
				checkConfirmMessageBeforeExit = false;
				document.getElementById("search-form").reset();	
				$('#save-panel').addClass('hide');
				
				clearTableRowAndColumn();
				searchNotFoundMode();
				GWRDSLib.clearST3Message();
				enableDisableButton(false);
				$('#getsudoMonthSearch').focus();
			};
		
		function restoreSearchCriteria(){
			
			$('#getsudoMonthSearch').val(lastSearchCriteria.getsudoMonthSearch);
			
			$('#vehiclePlantSearch').val(lastSearchCriteria.vehiclePlantSearch);
			$("#vehiclePlantSearch").change();
			$('#vehicleModelSearch').val(lastSearchCriteria.vehicleModelSearch);
			
			$("#vehicleModelSearch").change();
			$('#unitModelSearch').val(lastSearchCriteria.unitModelSearch);
			
			$("#unitModelSearch").change();
			$('#unitPlantSearch').val(lastSearchCriteria.unitPlantSearch);
			$("#unitPlantSearch").change();
			$('#timingSearch').val(lastSearchCriteria.timingSearch);
		}
			
		function saveSearchCriteria(){
			var searchForm = $('#search-form');
			lastSearchCriteria.getsudoMonthSearch = searchForm.find('input[name=getsudoMonthSearch]').val();
			lastSearchCriteria.vehiclePlantSearch = searchForm.find('select[name=vehiclePlantSearch]').val();
			lastSearchCriteria.vehicleModelSearch = searchForm.find('select[name=vehicleModelSearch]').val();
			lastSearchCriteria.unitModelSearch = searchForm.find('select[name=unitModelSearch]').val();
			lastSearchCriteria.unitPlantSearch = searchForm.find('select[name=unitPlantSearch]').val();
			lastSearchCriteria.timingSearch = searchForm.find('select[name=timingSearch]').val();
			
			
			var editform = $('#search-criteria-label');
			
			editform.find('span[id=resultGetsudoMonthSearch]').text(lastSearchCriteria.getsudoMonthSearch);
			editform.find('span[id=resultVehiclePlantSearch]').text(lastSearchCriteria.vehiclePlantSearch);
			editform.find('span[id=resultVehicleModelSearch]').text(lastSearchCriteria.vehicleModelSearch);
			editform.find('span[id=resultUnitModelSearch]').text(lastSearchCriteria.unitModelSearch);
			editform.find('span[id=resultUnitPlantSearch]').text(lastSearchCriteria.unitPlantSearch);
			editform.find('span[id=resultTimingSearch]').text(lastSearchCriteria.timingSearch);
			
		}
		
		function setDefaultSearchDisplay() {
			//clear parent line search
			$('#unitParentLineSearch').find('option').remove().end().append('<option value="">&lt;Select&gt;</option>').val('');
			//clear sub line search		
			$('#unitSubLineSearch').find('option').remove().end().append('<option value="">&lt;Select&gt;</option>').val('');
			
		}
			
		window.diableSearchField =
			function diableSearchField() {
				ST3Lib.content.disabled( '#search-field', true );
				enableDisableButton(false);
				ST3Lib.content.disabled($('#search-panel'), false);
				ST3Lib.content.disabled($('#save-panel'), false);						
			}
		
		window.diableOnEditMode =
			function diableSearchField() {
				ST3Lib.content.disabled( '#search-field', true );
				ST3Lib.content.disabled($('#search-panel'), true);
				enableDisableButton(true);
				$('.getsudoMonth').addClass('hide');
					
			}
			
		function searchNotFoundMode(){
			ST3Lib.content.disabled('#search-criteria', false);
			ST3Lib.content.disabled($('#search-panel'), false);
			enableDisableButton(true);
			$('#save-panel').addClass('hide');
			$('#result-list-form').addClass('hide');
			$('#search-criteria-label').addClass('hide');
			setDefaultSearchDisplay();
		}
		
		function searchFoundMode(){			
			ST3Lib.content.disabled('#search-result', true);
			ST3Lib.content.disabled('#search-criteria', false);
						
			ST3Lib.content.disabled($('#search-panel'), false);		
			$('#save-panel').addClass('hide');
			$('#result-list-form').removeClass('hide');
			$('.getsudoMonth').removeClass('hide');
			$('#operation-panel').show();
			
			$('#search-criteria-label').removeClass('hide');
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
		
		window.editObject =
			function editObject() {
				restoreSearchCriteria();
				GWRDSLib.clearST3Message();
				$('#save-panel').removeClass('hide');
				$('.edit-input').show();
				
				$('.control-label').hide();
				diableOnEditMode();
				ST3Lib.content.disabled( '#data-head-panel', false );
				ST3Lib.content.disabled( '.edit-input', true );
				enableDisableButton(false);
				$('.edit-input').not('.edit-disabled').removeAttr('disabled');
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
						getListTable();
						ST3Lib.content.disabled( '#search-field', true );
						var params = 'getsudoMonthSearch='+lastSearchCriteria.getsudoMonthSearch
						+'&vehiclePlantSearch='+lastSearchCriteria.vehiclePlantSearch
						+'&vehicleModelSearch='+lastSearchCriteria.vehicleModelSearch
						+'&unitModelSearch='+lastSearchCriteria.unitModelSearch
						+'&unitPlantSearch='+lastSearchCriteria.unitPlantSearch
						+'&timingSearch='+lastSearchCriteria.timingSearch;
						
						$('#result-list-form').attr('action', mappingPath + '/submitEdit?'+params).attr('_method', 'post');
						$("#messageBar").css("overflow", "auto");
						var saveForm = $('#result-list-form');
						saveForm.submit();
					}
				});
		};
		
		function getListTable(){
			var strJsonHeader = {};
			var strJsonBodyMax = {};
			var strJsonBodyMin = {};
			$("#result").find("th").each(function(i){
				strJsonHeader[i] = $(this).text();
			});
			
			$("#result").find("#result-body").find("#body-row-0").find("td").find('input').each(function(i){
				strJsonBodyMax[i] =  $(this).val();
			});
			
			$("#result").find("#result-body").find("#body-row-1").find("td").find('input').each(function(i){
				strJsonBodyMin[i] =  $(this).val();
			});

			$("#strJsonHeader").val(JSON.stringify(strJsonHeader));
			$("#strJsonBodyMax").val(JSON.stringify(strJsonBodyMax));
			$("#strJsonBodyMin").val(JSON.stringify(strJsonBodyMin));
		}
		
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
						enableDisableButton(false);
						$('.getsudoMonth').removeClass('hide');
						
						restoreSearchCriteria();
		    			var searchForm = $('#search-form');
		    			$('#search-form').submit();
						GWRDSLib.clearST3Message();
					}
					
				});
			};				
				
		/**
		 * Function ajax in dataTable for ADD
		 */
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
					enableDisableButton(true);
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
						$('#result input:text:enabled:first').focus();
					}
				}
				if (loading) {
					loading.close();
				}
		};		
				
		function enableDisableButton(haveData){					
			if(lastAction=='edit'){
				ST3Lib.validate.disabledButtonSpecify('#WBW04230Search', true);
				ST3Lib.validate.disabledButtonSpecify('#WBW04230Clear', true);
				
				ST3Lib.validate.disabledButtonSpecify('#WBW04230Edit', true);
			}else{
				ST3Lib.validate.disabledButtonSpecify('#WBW04230Search', false);
				ST3Lib.validate.disabledButtonSpecify('#WBW04230Clear', false);
				
				if(haveData){
					ST3Lib.validate.disabledButtonSpecify('#WBW04230Edit', false);
				}else{
					ST3Lib.validate.disabledButtonSpecify('#WBW04230Edit', true);
				}
			}
		}		
				
})(ST3Lib.$);