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
		
		ST3Lib.validate.format_msg['code'] = {
				regEx: /^([0-9a-zA-Z\-_:*]+\*?)?$/,
				ErrorCode: 'MSTD0043AERR'
			};
		
		ST3Lib.validate.format_msg['bodycode'] = {
				regEx: /^([0-9a-zA-Z\-_]+)?$/,
				ErrorCode: 'MSTD0043AERR'
			};
	
		lastSearchCriteria = {};
		enableDisableButton(false);
		$('#rowsPerPage').val(rowsPerPage);
		
		$('#categorySearch').focus();
		
		$('#categorySearch').change(function() {
			$.ajax({
				  method: "POST",
				  async : false,
				  url: mappingPath+'/categoryListChange',
				  data: { categorySearch : $(this).val()}
				})
				.done(function( data ) {
					var $subCategorySearch = $('#subCategorySearch');
					$subCategorySearch.empty();
					if(data){			
						$subCategorySearch.append('<option value="">&lt;All&gt;</option>');
						for(var i = 0; i < data.length; ++i) {
							var obj = data[i];
							$subCategorySearch.append('<option value="{0}">{1}</option>'.format(obj.stValue, obj.stLabel));
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
				if ( tr.index() %2 > 0 ) tr.addClass( 'even' );
				else tr.addClass( 'odd' );
				if ( type === 'add' ) {
					addrow(tr);
					enableFieldMode(type);
					ST3Lib.message.clear(true);
					ST3Lib.message.hide(true);
					
					$("#search-result").find('input[type=checkbox][name=objectIds]').prop( 'disabled', true );
					$("#category").focus();
				}else if (type === 'edit') {
					tr.find('input:checkbox').click();
					enableFieldMode(type);
					
					var jsonObjectData = JSON.parse($(".serialize").get(tr.index()).innerHTML);
					
					$("#search-result").find('input[type=checkbox][name=objectIds]').prop( 'disabled', true );
					
					ST3Lib.message.clear();
					ST3Lib.message.hide(true);
					$("#value").focus();
				}else if ( type === 'cancel' ) {
					$( 'input[type=checkbox]' ).prop( 'disabled', false );
					tr.find('input[type=checkbox]').click();
				}
			}
		});
		
		
		function enableFieldMode(type) {
			var resultForm = $('#result-list-form');
			if (type === 'edit') {
				ST3Lib.content.disabled('#category', false );
				ST3Lib.content.disabled('#subCategory', false );
				ST3Lib.content.disabled('#code', false );
				ST3Lib.content.disabled('#value', false );
				ST3Lib.content.disabled('#remark', false );
				ST3Lib.content.disabled('#status', false );
			}else if (type === 'add') {
				ST3Lib.content.disabled('#category', false );
				ST3Lib.content.disabled('#subCategory', false );
				ST3Lib.content.disabled('#code', false );
				ST3Lib.content.disabled('#value', false );
				ST3Lib.content.disabled('#remark', false );
				ST3Lib.content.disabled('#status', false );
			}
			
			ST3Lib.content.disabled( '#updateKeySet', false );
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
	    				setFormFocus("categorySearch");
	    				enableDisableButton(false);
	    				
	    				dataForm = data.objectForm;
	    				if (dataForm != null) {
		    				
		    				if(dataForm.categoryList){
								reloadCategoryCobobox(dataForm.categoryList);
							}
							
							if(dataForm.subCategoryList){	
								reloadSubCategoryCobobox(dataForm.subCategoryList);
							}		    				
		    			}
	    				$.each(dataForm, function(item, value){
	    		            $("#"+item).val(value);
	    		        });
	    			}
	    		} else {
	    			dataForm = data.objectForm;
	    			var dataTable = $( '#result' ).dataTable();
	    			dataTable.fnClearTable(true);
	    			
	    			if (dataForm != null) {
	    				
	    				if(dataForm.categoryList){
							reloadCategoryCobobox(dataForm.categoryList);
						}
						
						if(dataForm.subCategoryList){	
							reloadSubCategoryCobobox(dataForm.subCategoryList);
						}
	    				
	    				$.each(dataForm, function(item, value){
	    		            $("#"+item).val(value);
	    		        });
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
					setting.aoColumns[10].sClass = 'col11';
					setting.aoColumns[11].sClass = 'col12';
									
					var tempDiv = $('<div/>');
					var i = 0;
					var arrayData = [];
					var processor = setInterval(function(){
					var processPerTick = rowsPerPage;
					for(var j = 0; j < processPerTick; ++j) {
						var data = dataList[i++];
						if ( data ) {
							var updateDate = new Date(+data.updateDate);
							var createDate = new Date(+data.createDate);
							var updateTime = zeroLead( updateDate.getHours(), 2) + ':' 
											+ zeroLead(updateDate.getMinutes(), 2) + ':' 
											+ zeroLead(updateDate.getSeconds(), 2);
							var createTime = zeroLead( createDate.getHours(), 2) + ':' 
											+ zeroLead(createDate.getMinutes(), 2) + ':' 
											+ zeroLead(createDate.getSeconds(), 2);
							
							var serialize = tempDiv.text(JSON.stringify(data)).html();
							var chkBox = '<input class="checkbox" type="checkbox" name="objectIds" id="checkbox'+(j+1)+'" value="' + 
										 	data.id.category + UPDATE_KEYS_SEPERATER +
										 	data.id.subCategory + UPDATE_KEYS_SEPERATER +
										 	data.id.code + UPDATE_KEYS_SEPERATER +
										 	data.updateDate + UPDATE_KEYS_SEPERATER +
										 	'CLOSER'+
										 	 '" /><div class="serialize" style="display:none">' + serialize + '</div>';
							arrayData.push([ 
							                chkBox
							                , ''
							                , data.id.category || ''
										    , data.id.subCategory || ''
										    , data.id.code || ''
										    , data.value || ''
										    , data.remark || ''
										    , data.status === 'Y'?'Active':'Inactive'
										    , updateDate.format('dd/mm/yy ' + updateTime)
										    , data.updateBy || ''
										    , createDate.format('dd/mm/yy ' + createTime)
										    , data.createBy || ''
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
				$('#search-result .dataTable-wrapper').scrollTop(0);
				return;
			}
			if (count>0) {
				
				ST3Lib.dialog.confirm(MSTD0001ACFM, 'MSTD0001ACFM', function(ret){
					if(ret) {
						checkConfirmMessageBeforeExit = true;
						lastAction = 'delete';
						$.post( mappingPath+"/delete", { dataList:checked, 
														 categorySearch:$('#categorySearch').val(),
														 subCategorySearch:$('#subCategorySearch').val(),
														 codeSearch:$('#codeSearch').val()}, function( data ) {
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
									$('#search-result .dataTable-wrapper').scrollTop(0);
								}else if(data.errorMessages != null && data.errorMessages.length > 0) {
									ST3Lib.message.addError(data.errorMessages);
									ST3Lib.message.show(1);
									$('#search-result .dataTable-wrapper').scrollTop(0);
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
				$('#search-result .dataTable-wrapper').scrollTop(0);
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
			
			resetSubCategoryList();
			GWRDSLib.clearST3Message();
			
			enableDisableButton(false);
			
			lastSearchCriteria = {}
			
			$('#categorySearch').focus();
		};
	
	function resetSubCategoryList(){
		var $subCategorySearch = $('#subCategorySearch');
		$subCategorySearch.empty();
		$subCategorySearch.append('<option value="">&lt;All&gt;</option>');
	}	
	
	
	function resetRestoreSearchCriteria(){
		$("#categorySearch").val("");
		$("#categorySearch").change();
		saveSearchCriteria();
	}
	
	function restoreSearchCriteria(){
		if((lastSearchCriteria.categorySearch != undefined && lastSearchCriteria.subCategorySearch != "")){
			$("#categorySearch").val(lastSearchCriteria.categorySearch);
			$("#categorySearch").change();
			$("#subCategorySearch").val(lastSearchCriteria.subCategorySearch);
		}
	}
	
	function saveSearchCriteria(){
		lastSearchCriteria.categorySearch = $("#categorySearch").val();
		lastSearchCriteria.subCategorySearch = $("#subCategorySearch").val();
	}
	
	
	window.addObject =
		function addObject(){
			GWRDSLib.clearST3Message();
			restoreSearchCriteria();
			
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
			
			$("#search-result").find('input[type=checkbox][name=objectIds]').prop( 'checked', false );
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
				$('#search-result .dataTable-wrapper').scrollTop(0);
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
						
						$('#result-list-form').attr('action', mappingPath + '/submitAdd').attr('_method', 'post');
						
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
					
					if((lastSearchCriteria.categorySearch != undefined && lastSearchCriteria.categorySearch != "")){
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
		
		window.reloadCategoryCobobox =
			function reloadCategoryCobobox(categoryList){
				if(categoryList){		
					var $categorySearch = $('#categorySearch');
					$categorySearch.empty();
					
					$categorySearch.append('<option value="">&lt;Select&gt;</option>');
					for(var i = 0; i < dataForm.categoryList.length; ++i) {
						var obj = dataForm.categoryList[i];
						$categorySearch.append('<option value="{0}">{1}</option>'.format(obj.stValue, obj.stLabel));
					}
				}
		}
		
		window.reloadSubCategoryCobobox =
			function reloadSubCategoryCobobox(subCategoryList){
				if(subCategoryList){
					var $subCategorySearch = $('#subCategorySearch');
					$subCategorySearch.empty();
					
					$subCategorySearch.append('<option value="">&lt;All&gt;</option>');
					for(var i = 0; i < dataForm.subCategoryList.length; ++i) {
						var obj = dataForm.subCategoryList[i];
						$subCategorySearch.append('<option value="{0}">{1}</option>'.format(obj.stValue, obj.stLabel));
					}
				}
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
						if(dataForm.categoryList){
							reloadCategoryCobobox(dataForm.categoryList);
						}
						
						if(dataForm.subCategoryList){	
							reloadSubCategoryCobobox(dataForm.subCategoryList);
						}
						
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

	window.downloadSystem =
		function downloadSystem(){
			var loading = ST3Lib.dialog.loading('#WST33060Download');
			ST3Lib.message.clear(true);
			restoreSearchCriteria();
			var searchForm = $('#search-form');
			var ajax = searchForm.attr('ajax');
			var action = searchForm.attr('action');
			
			$('#downloadIframe').off('load').on('load', function(){
				try{
					var text = $('#downloadIframe').contents().text();
					if (text) {
						var obj = JSON.parse(text);
						ST3Lib.message.clear();
						ST3Lib.message.setPayload(obj);
						ST3Lib.message.setAppendMode(false);
						$('#search-form').submit();
					}
					loading.close();
				}catch(e){}
			});
			lastAction = 'download';
			searchForm.attr('action', mappingPath + '/download').attr('target', 'downloadIframe')
				.removeAttr('ajax')
				.submit();
			searchForm.attr('ajax', ajax).attr('action', action).removeAttr('target');
			$('#search-form').find('select[name=categorySearch]').focus();
			setTimeout(function(){
				loading.close();
				$('#search-form').find('select[name=categorySearch]').focus();
			}, 1500);
		};
		
	function enableDisableButton(haveData){
		if(lastAction=='add' || lastAction=='edit'){
			ST3Lib.validate.disabledButtonSpecify('#WST33060Search', true);
			ST3Lib.validate.disabledButtonSpecify('#WST33060Clear', true);
			
			ST3Lib.validate.disabledButtonSpecify('#WST33060Add', true);
			ST3Lib.validate.disabledButtonSpecify('#WST33060Edit', true);
			ST3Lib.validate.disabledButtonSpecify('#WST33060Delete', true);
			ST3Lib.validate.disabledButtonSpecify('#WST33060Download', true);
		}else{
			ST3Lib.validate.disabledButtonSpecify('#WST33060Search', false);
			ST3Lib.validate.disabledButtonSpecify('#WST33060Clear', false);
			
			ST3Lib.validate.disabledButtonSpecify('#WST33060Add', false);
			if(haveData){
				ST3Lib.validate.disabledButtonSpecify('#WST33060Edit', false);
				ST3Lib.validate.disabledButtonSpecify('#WST33060Delete', false);
				ST3Lib.validate.disabledButtonSpecify('#WST33060Download', false);
			}else{
				ST3Lib.validate.disabledButtonSpecify('#WST33060Edit', true);
				ST3Lib.validate.disabledButtonSpecify('#WST33060Delete', true);
				ST3Lib.validate.disabledButtonSpecify('#WST33060Download', true);
			}
		}
	}
		
})(ST3Lib.$);