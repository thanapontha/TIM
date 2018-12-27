(function($){	
	var lastAction = 'add';
	
	$(function(){
		
		$('#premiumInput, #activation, #birthDate, #compRegisterDate, #coverageUntil').datepicker({
			showOn: "button",
			buttonImage: calendarImgPath,
			buttonImageOnly: true,
			buttonText: "Select date",
			dateFormat: 'dd/mm/yy',
			onSelect: function(){
				$(this).focus();	
			}
		});
		
		validateDate(document.getElementById('premiumInput'));
		validateDate(document.getElementById('activation'));
		validateDate(document.getElementById('birthDate'));
		validateDate(document.getElementById('compRegisterDate'));
		validateDate(document.getElementById('coverageUntil'));
		
//		$('#coverageUntil').MonthPicker({
//	        MonthFormat: 'M-y', // Short month name, Full year.
//	        Button: '<img class="ui-datepicker-trigger" title="Select Month" src='+calendarImgPath+ '/>',
//	        AltFormat: 'M-y', //result
//	        onSelect: function(){
//				$(this).focus();
//			},
//			OnAfterChooseMonth: function(){
//				$('#coverageUntil').focus();
//			}
//	    });
//		
//		validateDateMMMYY(document.getElementById('coverageUntil'));
		
		$('#typeOfCustomer').change(function() {			
			if($(this).val()=='Individual'){
				$('#customerIndividual').show();
				$('#customerJuristic').hide();
			}else{
				$('#customerIndividual').hide();
				$('#customerJuristic').show();
			}
		});
		
		$('#typeOfCustomer').val('Individual').change();
		
		addProvinceList(__provinceList);
		
		$('#province').change(function(){
			changeProvince(__provinceList, this.value);
			$('#amphur').val('');
			$('#district').val('');
			$('#zipcode').val('');
		});
		
		$('#amphur').change(function(){
			changeAmphur(__provinceList, $('#province').val(), this.value);
			$('#district').val('');
			$('#zipcode').val('');
		});
		
		$('#district').change(function(){
			changeDistrict(__provinceList, $('#province').val(), $('#amphur').val(), this.value);
		});
		
		
		
		$('#compProvince').change(function(){
			changeCompProvince(__provinceList, this.value);
			$('#compAmphur').val('');
			$('#compDistrict').val('');
			$('#compZipcode').val('');
		});
		
		$('#compAmphur').change(function(){
			changeCompAmphur(__provinceList, $('#compProvince').val(), this.value);
			$('#compDistrict').val('');
			$('#compZipcode').val('');
		});
		
		$('#compDistrict').change(function(){
			changeCompDistrict(__provinceList, $('#compProvince').val(), $('#compAmphur').val(), this.value);
		});
		
		addCompProvinceList(__provinceList);
	});
	
	
	function addProvinceList(provinces){
		var $province = $('#province');
		$province.empty();
		if (!provinces) return;
		//var requiredModule = $province.hasClass('MandatoryField'); 
		//if (requiredModule) {
			$province.append('<option value="">&lt;Select&gt;</option>');
		//} else {
		//	$province.append('<option value="">&lt;All&gt;</option>');
		//}
		for(var i = 0; i < provinces.length; ++i) {
			var province = provinces[i];
			$province.append('<option value="{0}">{1}</option>'.format(province.provinceCode, province.provinceNameDisp));
		}
	}
	
	function changeProvince(provinces, provinceCode){
		var $amphur = $('#amphur');
		$amphur.empty();
		$amphur.append('<option value="">&lt;Select&gt;</option>');
		var ampLs = null;
		for(var i = 0; i < provinces.length; ++i) {
			if (provinces[i].provinceCode === provinceCode) {
				ampLs = provinces[i].listAmphur;
				break;
			}
		}
		
		if (ampLs == null) return;
		ampLs.sort(function(a1, a2){
			if (a1.amphurNameDisp === a2.amphurNameDisp)
				return 0;
			return a1.amphurNameDisp < a2.amphurNameDisp? -1:1;
		});
		for(var i = 0; i < ampLs.length; ++i) {
			var amp = ampLs[i];
			$amphur.append('<option value="{0}">{1}</option>'.format(amp.amphurCode, amp.amphurNameDisp));
		}
		
		clearDistrict();
	}
	
	function clearDistrict(){
		var $district = $('#district');
		$district.empty();
		$district.append('<option value="">&lt;Select&gt;</option>');
	}
	
	function changeAmphur(provinces, provinceCode, amphurCode){
		var $district = $('#district');
		$district.empty();
		$district.append('<option value="">&lt;Select&gt;</option>');
		
		var ampLs = null;
		for(var i = 0; i < provinces.length; ++i) {
			if (provinces[i].provinceCode === provinceCode) {
				ampLs = provinces[i].listAmphur;
				break;
			}
		}
		
		if (ampLs == null) return;
		
		var distLs = null;
		var zipcodeLs = null;
		for(var i = 0; i < ampLs.length; ++i) {
			if (ampLs[i].amphurCode === amphurCode) {
				distLs = ampLs[i].listDistrict;
				zipcodeLs = ampLs[i].listDistrict;
				break;
			}
		}
		
		if (distLs == null) return;
		distLs.sort(function(d1, d2){
			if (d1.districtNameDisp === d2.districtNameDisp)
				return 0;
			return d1.districtNameDisp < d2.districtNameDisp? -1:1;
		});
		for(var i = 0; i < distLs.length; ++i) {
			var dist = distLs[i];
			$district.append('<option value="{0}">{1}</option>'.format(dist.districtCode, dist.districtNameDisp));
		}
	}
	
	function changeDistrict(provinces, provinceCode, amphurCode, districtCode){
		var $zipcode = $('#zipcode');
		$zipcode.val('');
		
		var ampLs = null;
		for(var i = 0; i < provinces.length; ++i) {
			if (provinces[i].provinceCode === provinceCode) {
				ampLs = provinces[i].listAmphur;
				break;
			}
		}
		
		if (ampLs == null) return;
		
		var distLs = null;
		for(var i = 0; i < ampLs.length; ++i) {
			if (ampLs[i].amphurCode === amphurCode) {
				distLs = ampLs[i].listDistrict;
				break;
			}
		}
		
		if (distLs == null) return;
		for(var i = 0; i < distLs.length; ++i) {
			if (distLs[i].districtCode === districtCode) {
				$zipcode.val(distLs[i].zipcode);
				break;
			}
		}
	}
	
	function addCompProvinceList(provinces){
		var $province = $('#compProvince');
		$province.empty();
		if (!provinces) return;
		//var requiredModule = $province.hasClass('MandatoryField'); 
		//if (requiredModule) {
			$province.append('<option value="">&lt;Select&gt;</option>');
		//} else {
		//	$province.append('<option value="">&lt;All&gt;</option>');
		//}
		for(var i = 0; i < provinces.length; ++i) {
			var province = provinces[i];
			$province.append('<option value="{0}">{1}</option>'.format(province.provinceCode, province.provinceNameDisp));
		}
	}
	
	function changeCompProvince(provinces, provinceCode){
		var $amphur = $('#compAmphur');
		$amphur.empty();
		$amphur.append('<option value="">&lt;Select&gt;</option>');
		var ampLs = null;
		for(var i = 0; i < provinces.length; ++i) {
			if (provinces[i].provinceCode === provinceCode) {
				ampLs = provinces[i].listAmphur;
				break;
			}
		}
		
		if (ampLs == null) return;
		ampLs.sort(function(a1, a2){
			if (a1.amphurNameDisp === a2.amphurNameDisp)
				return 0;
			return a1.amphurNameDisp < a2.amphurNameDisp? -1:1;
		});
		for(var i = 0; i < ampLs.length; ++i) {
			var amp = ampLs[i];
			$amphur.append('<option value="{0}">{1}</option>'.format(amp.amphurCode, amp.amphurNameDisp));
		}
		
		clearCompDistrict();
	}
	
	function clearCompDistrict(){
		var $district = $('#compDistrict');
		$district.empty();
		$district.append('<option value="">&lt;Select&gt;</option>');
	}
	
	function changeCompAmphur(provinces, provinceCode, amphurCode){
		var $district = $('#compDistrict');
		$district.empty();
		$district.append('<option value="">&lt;Select&gt;</option>');
		
		var ampLs = null;
		for(var i = 0; i < provinces.length; ++i) {
			if (provinces[i].provinceCode === provinceCode) {
				ampLs = provinces[i].listAmphur;
				break;
			}
		}
		
		if (ampLs == null) return;
		
		var distLs = null;
		var zipcodeLs = null;
		for(var i = 0; i < ampLs.length; ++i) {
			if (ampLs[i].amphurCode === amphurCode) {
				distLs = ampLs[i].listDistrict;
				zipcodeLs = ampLs[i].listDistrict;
				break;
			}
		}
		
		if (distLs == null) return;
		distLs.sort(function(d1, d2){
			if (d1.districtNameDisp === d2.districtNameDisp)
				return 0;
			return d1.districtNameDisp < d2.districtNameDisp? -1:1;
		});
		for(var i = 0; i < distLs.length; ++i) {
			var dist = distLs[i];
			$district.append('<option value="{0}">{1}</option>'.format(dist.districtCode, dist.districtNameDisp));
		}
	}
	
	function changeCompDistrict(provinces, provinceCode, amphurCode, districtCode){
		var $zipcode = $('#compZipcode');
		$zipcode.val('');
		
		var ampLs = null;
		for(var i = 0; i < provinces.length; ++i) {
			if (provinces[i].provinceCode === provinceCode) {
				ampLs = provinces[i].listAmphur;
				break;
			}
		}
		
		if (ampLs == null) return;
		
		var distLs = null;
		for(var i = 0; i < ampLs.length; ++i) {
			if (ampLs[i].amphurCode === amphurCode) {
				distLs = ampLs[i].listDistrict;
				break;
			}
		}
		
		if (distLs == null) return;
		for(var i = 0; i < distLs.length; ++i) {
			if (distLs[i].districtCode === districtCode) {
				$zipcode.val(distLs[i].zipcode);
				break;
			}
		}
	}
	
	
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
					var saveForm = $('#result');
					if (lastAction === 'add') {
						saveForm.attr('action', mappingPath + '/submitAdd').attr('_method', 'post');
						
					} else {
						saveForm.attr('action', mappingPath + '/submitAdd').attr('_method', 'post');
					}

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
				
				if(datas.infoMessages.length > 0) {
					 ST3Lib.message.show(1);
				}
				
				dataForm = datas.objectForm;
				if (dataForm!=null) { 
					$.each(dataForm, function(item, value){
			            $("#"+item).val(value);
			        });  
				}
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
	
})(ST3Lib.$);