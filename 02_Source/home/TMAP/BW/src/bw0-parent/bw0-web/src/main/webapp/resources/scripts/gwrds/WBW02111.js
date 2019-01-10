(function($) {

	var totalLenght = 0;
	var lastSearchCriteria = {};
	var lastAction = '';
	var UPDATE_KEYS_SEPERATER = '|';
	var adjVolumeList = [];
	var adjVolumeList = [];
	var roundVolumeList = [];
	var getsudoMonthParamList = [];
	var tableResultTemplate = '';

	$(function() {
		tableResultTemplate = $('#production-result').html();
		var searchForm = $('#search-form');
		searchForm.submit();
		
		var $content = $('#VanningAdjustmentDialog');
		$content.height(500);
		
		ST3Lib.validate.disabledButtonSpecify('#WBW02111Save', true);	
		
	});
	

		window.generateDataList = function generateDataList(datas) {
		var dataList = datas.objectsInfoList;
		menuList = datas.objectsInfo2List;
		$('input[name=getsudoMonths]').val(menuList);
		$('#updateKeySet').val(datas.objectForm.updateKeySet);
		if (menuList) {
			if (dataList) {

				for (var i = 0; i < menuList.length; i++) {
					$('#production-header').append( '<th class="colheader sorting_disabled" role="columnheader" rowspan="1" colspan="1">'
							+ menuList[i]
							+'</th>');
					$('#vanning-header').append( '<th class="colheader sorting_disabled" role="columnheader" rowspan="1" colspan="1">'
							+ menuList[i]
							+'</th>');
					$('#adjVanning-header').append( '<th class="colheader sorting_disabled" role="columnheader" rowspan="1" colspan="1">'
							+ menuList[i]
							+'</th>');
					$('#roundResult-header').append( '<th class="colheader sorting_disabled" role="columnheader" rowspan="1" colspan="1">'
							+ menuList[i]
							+'</th>');
				}
				var lotsize = 0;
				var colSpanAll = 1;
				
				for (var iRow = 0; iRow < dataList.length; iRow++) {
					
					var rows = dataList[iRow];
					colSpanAll = rows.length - 1;
					
					var htmlProd = "<tr>";
					var htmlVan = "<tr>";
					var htmlAdj = "<tr>";
					var htmlRes = "<tr>";
					for (var j = 3; j < rows.length; j++) {
						if("production" == rows[0]){	
							if(j == 3){
								htmlProd += '<td style="text-align: center;width: 200px;" colspan="2">'+'<label  class="">'+rows[j]+'</label>'+'</td>';
							}else{
								if(j != 4){ //do not display unit model
									htmlProd += '<td style="text-align: right"><label class="commaNumber">'+returnBlankIsNull(rows[j])+'</label>'+'</td>'; 
								}
							}
						}else if("vanning" == rows[0]){
							if(j < 5){
								htmlVan += '<td style="text-align: center;width: 100px;"><label class="">'+returnBlankIsNull(rows[j])+'</label></td>';
							}else{
								htmlVan += '<td id = "'+rows[0]+''+menuList[j-5]+'" style="text-align: right" ><label class="commaNumber">'+returnBlankIsNull(rows[j])+'</label>'
										+'<input class="colbody hide" gm="'+menuList[j-5]+'" type="text"  title="'+rows[0]+''+menuList[j-5]+'" id="txt'+rows[0]+''+menuList[j-5]+'" name="'+rows[0]+''+menuList[j-5]+'" value="'+returnBlankIsNull(rows[j])+'"></td>';
							}
							
						}else if("adjVanning" == rows[0]){
							if(j == 3){
								lotsize = rows[1];
							}
							if(j < 5){
								htmlAdj += '<td style="text-align: center;width: 100px;" ><label class="">'+rows[j]+'</label></td>';
							}else{
								htmlAdj += '<td style="text-align: center;"><label for="'+rows[0]+''+menuList[j-5]+'" class="control-label hide">Adj Vanning of '+menuList[j-5]+'</label>'
										+  '<input class="colbody MandatoryField number commaNumber" gm="'+menuList[j-5]+'" type="text" title="Adj Vanning of '+menuList[j-5]+'"' 
										+  'id="'+rows[0]+''+menuList[j-5]+'" name="'+rows[0]+''+menuList[j-5]+'" value="'+returnNullToZero(rows[j])+'" maxlength="6" onblur="doAdjustVanning(this,\''+menuList[j-5]+'\');"></td>';
							
							}
						}else{
							if(j < 5){
								htmlRes += '<td style="text-align: center;width: 100px;"><label  class="">'+returnBlankIsNull(rows[j])+'</label></td>';
							}else{
								htmlRes += '<td id = "'+rows[0]+''+menuList[j-5]+'" style="text-align: right"><label class="commaNumber">'+returnBlankIsNull(rows[j])+'</label>'
										+'<input class="colbody hide" gm="'+menuList[j-5]+'" type="text"  title="'+rows[0]+''+menuList[j-5]+'" id="txt'+rows[0]+''+menuList[j-5]+'" name="'+rows[0]+''+menuList[j-5]+'" value="'+returnBlankIsNull(rows[j])+'"></td>';
							}
						}
					}

					$('#production-body').append("<tr>"+htmlProd+"</tr>");
					$('#vanning-body').append("<tr>"+htmlVan+"</tr>");
					$('#adjVanning-body').append("<tr>"+htmlAdj+"</tr>");
					$('#roundResult-body').append("<tr>"+htmlRes+"</tr>");
					$('.commaNumber').formatNumberWithcomma();
					
					$('#adjVanning-body :input').not('#lotSize').each(function(){
						validateNumber(this, 6, 7, true);
					});
					$('#lotSize').attr('maxlength',3);
										
					//add lot size
					if("adjVanning" == rows[0]){
						var col = rows.length - 4;
						var col2 = col+1;
						$('#adjVanning-body').append('<tr id="lotSize">');
						$('#adjVanning-body').append(
								'<td style="text-align: right;border-left: none; border-bottom: none;" class=" sorting_disabled" role="columnbody" rowspan="1" colspan="'+col+'"><label>Lot Size</label></td>');
						$('#adjVanning-body').append(
								'<td style="text-align: center" class=" sorting_disabled" role="columnbody" rowspan="1" colspan="'+col2+'">'
										+'<label for="lotSize" class="control-label hide">Lot Size</label>' 
										+'<input  class="colbody MandatoryField" type="text"  title="Lot Size" id="lotSize" name="lotSize" value="'+(lotsize||'')+'" maxlength="3" '
										+' onkeypress="return event.charCode >= 48 && event.charCode <= 57" >'
										+'</label>'
										+ '</td>');	
					}
				}
				var emptyRow = "<tr style='height:15px;'><td colspan='"+colSpanAll+"' style='border: none; border-right: none;'></td></tr>";
				$('#production-body').append(emptyRow);
				$('#vanning-body').append(emptyRow);
				$('#adjVanning-body').append(emptyRow);
			}
		}
	};				
	
	window.doAdjustVanning = function doAdjustVanning(obj, monthName){
		var vanning = $('#vanning'+monthName+' :input').val();
		var adjvanning = $(obj).val();
		
		if(!$.isNumeric(adjvanning)){
			adjvanning = 0;
			$(obj).val(0);
		}
		
		var calVal = parseInt(vanning) + parseInt(adjvanning);
		if($.isNumeric(calVal)){
			$('#roundResult'+monthName).find('label').text(calVal);
			$('#txtroundResult'+monthName).val(calVal);
		}else{
			$('#roundResult'+monthName).find('label').text(vanning);
			$('#txtroundResult'+monthName).val(vanning);
		}
		$('.commaNumber').formatNumberWithcomma();
	}

	window.doCancel = function doCancel() {
		ST3Lib.dialog.confirm(MSTD0003ACFM, 'MSTD0003ACFM', function(ret){
			if (!ret) {
				return;
			}else{
				window.parent.GWRDSLib.dialog.close();
			}
		});

	};
	
	window.doSave = function doSave() {
		ST3Lib.dialog.confirm(MBW00004ACFM_ROUND_RESULT, 'MBW00004ACFM', function(ret){
			if (!ret) {
				return;
			}else{
				GWRDSLib.clearST3Message();
				
				//Enable input before submit
				ST3Lib.content.disabled( '#lotSize', false );
				ST3Lib.content.disabled( '#adjVanning-body input', false );
				
				var loadingTarget = "#screen-panel";
				var loading = ST3Lib.dialog.loading(loadingTarget);
				var saveForm = $('#result-list-form');
				
				$.ajax({
				 	method: "POST",
				 	url: mappingPath+'/doSave', 
		            data: {
		            	getsudoMonth : $('#getsudoMonth').val(),
		        	    timing : $('#timing').val(),	        	    
		        	    vehiclePlant : $('#vehiclePlant').val(),	
		        	    vehicleModel : $('#vehicleModel').val(),
		        	    unitModel : $('#unitModel').val(),
		        	    unitPlant : $('#unitPlant').val(),
		        	    unitType : $('#unitType').val(),
		        	    adjVolumeList : $('#adjVolumeList').val(),
        	    		roundVolumeList : $('#roundVolumeList').val(),
        	    		getsudoMonthParamList : $('#getsudoMonthParamList').val(),
        	    		lotSizeVolume : $('#lotSizeVolume').val(),
        	    		updateKeySet : $('#updateKeySet').val()
	            	    },
	            	dataType: 'text',
	            	success: function(data) {
		            	if(data  && typeof data != 'undefined'){	
		            		var objSerialize = JSON.parse(data);
		            		
		            		//Refresh data
		        			totalLenght = 0;
		        			lastSearchCriteria = {};
		        			lastAction = '';
		        			adjVolumeList = [];
		        			roundVolumeList = [];
		        			getsudoMonthParamList = [];
		        			$('#adjVolumeList').val('');
	        	    		$('#roundVolumeList').val('');
	        	    		$('#getsudoMonthParamList').val('');
		        			$('#production-result').html(tableResultTemplate);
		        			var searchForm = $('#search-form');
		        			searchForm.submit();
		        			
			        		if(objSerialize.errorMessages != "" && typeof objSerialize.errorMessages != 'undefined') {
				        		ST3Lib.message.addError(objSerialize.errorMessages);
								ST3Lib.message.show(1);
				            
				            	loading.close();
				        	}else if(objSerialize.infoMessages.length > 0) {
				        			ST3Lib.message.addInfo(objSerialize.infoMessages);
				        			ST3Lib.message.show(1); 
				        			
				        			loading.close();
				        			
				        			parent.clickSearch();
				        	}else{
				        		loading.close();
				        	}
		        		}
		            	
		            	ST3Lib.validate.disabledButtonSpecify('#WBW02111Save', true);
	    				ST3Lib.validate.disabledButtonSpecify('#WBW02111Round', false);
		            },		
		        	error: function() {
		        		ST3Lib.validate.disabledButtonSpecify('#WBW02111Save', true);
	    				ST3Lib.validate.disabledButtonSpecify('#WBW02111Round', false);
		            	loading.close();
		        	}
			 	});
			}
		});
	};
	
	window.doRound = function doRound() {
		GWRDSLib.clearST3Message();
		var saveForm = $('#result-list-form');
		var lotsize = $('input[name="lotSize"]').val();
		$('#lotSizeVolume').val(lotsize);
		roundVolumeList = [];
		adjVolumeList = [];
		getsudoMonthParamList = [];
		var doSave = false;
		if(formCustomValidateError(saveForm, lotsize)){
			var adjvanninglist = $('#adjVanning-body :input');
			var vanninglist = $('#vanning-body :input');
			var errorCount = 0;
			for (var i = 0; i < adjvanninglist.length-1; i++) {
				var adjvolume = adjvanninglist[i].value.replace(/,/g, '');
				adjVolumeList.push(adjvolume);
				var vanningvolume = vanninglist[i].value.replace(/,/g, '');
				var gm = $('#' + adjvanninglist[i].id).attr('gm');

				getsudoMonthParamList.push(gm);
				
				if (adjvolume < -99999 || adjvolume > 99999) {
					
					ST3Lib.message.addError(MSTD0016AERR.replace("{0}",
							"Adj Vanning of " + gm).replace("{1}", "-99,999")
							.replace("{2}", "99,999"));
					ST3Lib.message.show(1);
					doSave = false;
					errorCount++;
				}
				
				var result = parseInt(vanningvolume||0) + parseInt(adjvolume||0);
				if(result > 99999){
					ST3Lib.message.addError(MSTD0025AERR.replace("{0}","Round Result Volume of "+gm).replace("{1}", "99,999"));
					ST3Lib.message.show(1);
					doSave = false;
					errorCount++;
				}else if(result < 0){
					ST3Lib.message.addError(MSTD0018AERR.replace("{0}","Vanning Volume("+ vanningvolume+") - Adj Vanning Volume("+Math.abs(parseInt(adjvolume))+") of "+gm).replace("{1}", "0")
							);
					ST3Lib.message.show(1);
					doSave = false;
					errorCount++;
				}else{
					var volume =roundFormula(result, lotsize);
					roundVolumeList.push(volume);
					$('#roundResult'+gm).find('label').text(volume);
					$('#txtroundResult'+gm).val(volume);
					doSave = true;
				}
			}
		
			if(errorCount > 0) doSave = false;
			
			if(doSave){

				ST3Lib.content.disabled( '#lotSize', true );
				ST3Lib.content.disabled( '#adjVanning-body input', true );
				
				$('#adjVolumeList').val(adjVolumeList);
				$('#roundVolumeList').val(roundVolumeList);
				$('#getsudoMonthParamList').val(getsudoMonthParamList);
				ST3Lib.validate.disabledButtonSpecify('#WBW02111Save', false);
				ST3Lib.validate.disabledButtonSpecify('#WBW02111Round', true);
			}else{
				ST3Lib.validate.disabledButtonSpecify('#WBW02111Save', true);
				ST3Lib.validate.disabledButtonSpecify('#WBW02111Round', false);
			}
			
			$('.commaNumber').formatNumberWithcomma();
		}
		
		function roundFormula(volume, lotsize){
			var roundup = Math.ceil(volume/lotsize);
			
			return roundup*lotsize;
		}
		

		function formCustomValidateError(form, volume) {
			if (formValidateError(form)) {
				if (volume >= 1 && volume <= 999) {
					// do nothing
				} else {
					ST3Lib.message.addError(MSTD0016AERR.replace("{0}",
							"Lot Size").replace("{1}", "1").replace("{2}",
							"999"));
					ST3Lib.message.show(1);
					return false;

				}
			}else{
				return false;
			}

			return true;
		}
		
	};
	
	
	
	window.searchFinish = function searchFinish(data, loading) {
		if (data && typeof data != 'undefined') {
			checkConfirmMessageBeforeExit = false;
			if (data.errorMessages != ""
					&& typeof data.errorMessages != 'undefined') {
				if (data.focusId != "" && typeof data.focusId != 'undefined') {
					searchNotFoundMode();
				}
			} else {
				dataForm = data.objectForm;
				generateDataList(data);
				loading.close();
			}
		}
		loading.close();
	};

	window.saveValidateError = function saveValidateError() {
		return;
	};

	function returnBlankIsNull(param) {
		if (typeof param == 'object') {
			return '';
		}
		return param;
	}
	
	function returnNullToZero(param) {
		if (typeof param == 'object') {
			return 0;
		}
		return param;
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

	window.sleep = function sleep(milliseconds) {
		var start = new Date().getTime();
		for (var i = 0; i < 1e7; i++) {
			if ((new Date().getTime() - start) > milliseconds) {
				break;
			}
		}
	};
})(ST3Lib.$);