var isFirefox = typeof InstallTrigger !== 'undefined';

Number.prototype.toFixedUp = Number.prototype.toFixed;

Number.prototype.toFixed = function(fixed) {
	if(isNaN(this) == true ){
		return "";
	}
	if(fixed && fixed>0){
		var tmpfixed = fixed + 1;
		var value = this.toFixedUp(tmpfixed);
		var tmpObj = value.split(".");
		var obj1 = tmpObj[0];
		var obj2 = tmpObj[1];
		var lastChr = obj2.charAt(tmpfixed-1);
		lastChr = parseFloat(lastChr);
		
		var tmpObj2 = "0.";
		var tmpObj2Additional = "0.";
		for (var i = 0; i < fixed; i++) {
			tmpObj2 = tmpObj2 + obj2.charAt(i);
			if(i==fixed-1){
				if(lastChr>=5){
					tmpObj2Additional = tmpObj2Additional+"1";
				}else{
					tmpObj2Additional = tmpObj2Additional+"0";
				}					
			}else{					
				tmpObj2Additional = tmpObj2Additional+"0";
			}	
		}
		
		var amtNew = parseFloat(obj1);
		if(tmpObj2!=="0."){
			var tmp2 = parseFloat(tmpObj2);
			var tmpAddi = parseFloat(tmpObj2Additional);
			if (obj1.indexOf("-") >= 0) {
				tmp2 = tmp2 * -1;
				tmpAddi = tmpAddi * -1;
			} 
			amtNew = amtNew + tmp2 + tmpAddi;
		}
		return amtNew.toFixedUp(fixed);
	}else{
		return this.toFixedUp(fixed);
	}
};

function addNumberWithComma(obj, decpoint) {
	var value = obj.value.replace(/[^-?\d^.]/g, '');
	var decValue;
	if (arguments.length != 1 && value != "") {
		value = value.toString();
		
		if (value.length > 1) {
			var tmpvalue = value;
			var idx = -1;
			var dashCnt = 0;
			var lastIndxDash = -1;
			for (var i = 0; i < tmpvalue.length; i++) {
				if (tmpvalue.charAt(i) == '-') {
					dashCnt++;
					lastIndxDash = i;
				}
			}
			if(dashCnt>1){
				value = '';
			}else if(dashCnt==1){
				if(lastIndxDash!=0){
					value = '';
				}
			}
		}
		
		
		a = value.split(".");
		if (a.length > 1) {
			decValue = a[1]; // decimal value
			if (decValue && decpoint && decpoint != '') {
				if (decValue.length <= decpoint.length) {
					value = new Number(value);
					value = value.toFixed(decpoint.length);
					value = value.toString();
					var b = value.split(".");
					value = b[0]; // int value
					decValue = b[1];
				} else {
					// var tmpvalue = new Number("0."+decValue);
					// var tmpvalue2 = roundUp(tmpvalue, decpoint.length)+"";
					// var b = tmpvalue2.split(".");
					// decValue = b[1]; // decimal value
				}
			}
		} else {
			value = a[0]; // int value
		}
		if (!decValue && decpoint && decpoint != '') {
			decValue = decpoint;
		}
	}
	var index = -1;
	if (value.length > 1 && value.charAt(0) == "0") {
		for (var i = 0; i < value.length; i++) {
			index++;
			if (value.charAt(i) == '.') {
				i = value.length;
				index--;
			} else if (value.charAt(i) != 0 && value.charAt(i) != ',') {
				i = value.length;
			}
		}
		value = value.substr(index, obj.value.length);
	}

	var objRegExp = new RegExp('(-?[0-9]+)([0-9]{3})');
	if (value !== '0') {
		if (value != null && value.indexOf(".") > -1) {
			value = value.substring(0, value.indexOf("."));
			objRegExp = new RegExp('(-?[0-9]+)([0-9]{3})[\.[0-9]]');
		}
	}

	while (objRegExp.test(value)) {
		value = value.replace(objRegExp, '$1,$2');
	}

	if (arguments.length != 1) {
		if (value !== "") {
			if (decValue) {
				value += "." + decValue;
			}
		}
	}
	obj.value = value;
}

/**
 * function roundUp(num, places) { return +(Math.round(num + "e+"+places) +
 * "e-"+places); }
 */

window.removeNumberWithCommas = function removeNumberWithCommas(obj) {
	$(obj).val($(obj).val().replace(/,/g, ''));
};

window.removeNumberWithPercent = function removeNumberWithPercent(obj) {
	$(obj).val($(obj).val().replace(/%/g, ''));
};

function pasteValidate(restrictionType, e, decpoint) {
	if (!e)
		e = window.event;

	if (window.clipboardData && window.clipboardData.getData) { // IE
		pasteData = window.clipboardData.getData('Text');
	} else if (e.clipboardData && e.clipboardData.getData) {
		pasteData = e.clipboardData.getData('text/plain');
	}

	if (decimal == restrictionType) {
		var arrData = pasteData.split('.');

		if (arrData.length > 2) {
			if (isFirefox == false) {
				(event.preventDefault) ? event.preventDefault()
						: event.returnValue = false;
			} else {
				e.preventDefault();
			}
		} else if (arrData.length > 1) {
			if (decpoint && decpoint.length) {
				var decValue = arrData[1]; // decimal value
				if (decValue && decValue != '') {
					if (decValue.length > decpoint.length) {
						if (isFirefox == false) {
							(event.preventDefault) ? event.preventDefault()
									: event.returnValue = false;
						} else {
							e.preventDefault();
						}
					}
				}
			}
		}
	}

	var modData = pasteData.replace(new RegExp("[\\s-]+", "g"), "");
	var stError = modData.replace(new RegExp("^" + restrictionType + "+", "g"),
			"");
	if (stError == "") {
		return;
	} else {
		if (isFirefox == false) {
			(event.preventDefault) ? event.preventDefault()
					: event.returnValue = false;
		} else {
			e.preventDefault();
		}
	}
}

$(document).ready(function() {
	$("textarea").on("paste", function() {
		return pasteReasonValidation(this);
	});
	$("textarea").on("keypress", function() {
		return imposeMaxLength(this);
	});
});

function imposeMaxLength(Object) {
	var maxLen = Object.getAttribute("maxLength");

	// return (Object.value.length <= maxLen);
	if (Object.value.length < maxLen) {
		return true;
	} else {
		if (document.selection) {
			if (document.selection.createRange().text.length > 0) {
				return true;
			}
		}
		return false;
	}
}

function replaceMaxLength(Object) {
	var maxLen = Object.getAttribute("maxLength");

	var sentence = Object.value;
	if (sentence.length > maxLen) {
		sentence = sentence.substring(0, maxLen);
		Object.value = sentence;
	}

}

function pasteReasonValidation(Object) {
	var maxLen = Object.getAttribute("maxLength");
	var pasteData = window.clipboardData.getData("Text");

	if (document.selection) {
		Object.caretPos = document.selection.createRange().duplicate();
		if (Object.createTextRange && Object.caretPos) {
			Object.caretPos.text = pasteData;
		}
	}

	if (Object.value.length > maxLen) {
		pasteData = Object.value.substring(0, maxLen);
		Object.value = pasteData;
		return false;
	}
}

/*******************************************************************************
 * Allows numerical input only
 ******************************************************************************/
function numbersOnly(e) {
	if (!e)
		e = window.event;
	if (e.keyCode)
		code = e.keyCode;
	else if (e.which)
		code = e.which;
	if (code == 13)
		return true;
	if ((code >= 48) && (code <= 57))
		return true;
	else
		return false;
}

function numbersOnlyOnKeyPress(obj, e, isAllowNegativeValue) {
	var valid = false;
	if(isAllowNegativeValue){
		valid = performFiltering(/[^-?\d]/g, e);
	}else{
		valid = performFiltering(/[^\d]/g, e);
	}
	if (valid) {
		if (!e)
			e = window.event;
		if (e.keyCode)
			code = e.keyCode;
		else if (e.which)
			code = e.which;

		if (valid == false) {
			e.keyCode = 0;
			if (isFirefox == false) {
				if (event)
					(event.preventDefault) ? event.preventDefault()
							: event.returnValue = false;
			} else {
				e.preventDefault();
			}
			return false;
		} else {
			return true;
		}
	} else {
		return false;
	}
}

function numbersWithDecimalOnlyOnKeyPress(obj, decpoint, e, isAllowNegativeValue) {
	var valid = false;
	if(isAllowNegativeValue){
		valid = performFiltering(/[^-?\d\.]/g, e);
	}else{
		valid = performFiltering(/[^\d\.]/g, e);
	}
	if (valid) {
		if (!e)
			e = window.event;
		if (e.keyCode)
			code = e.keyCode;
		else if (e.which)
			code = e.which;

		if (obj.value != null) {
			if (obj.value.indexOf(".") > -1) { // word already contain .
				if (isFirefox) {
					var c = e.charCode;// 46 = . 0 = delete
					var str = String.fromCharCode(c);
					if (str === '.') {
						valid = false;
					}
				} else {
					if (code == 46) {// key-in .
						valid = false;
					}
				}
			} else {
				if (decpoint && decpoint.length && valid) {
					var indexCursor = doGetCaretPosition(obj);
					var objLen = obj.value.length;
					if (isFirefox) {
						var c = e.charCode;// 46 = . 0 = delete
						var str = String.fromCharCode(c);
						if (str === '.') {
							var strAfterDot = obj.value.substr(indexCursor,
									objLen);
							if (strAfterDot) {
								objLen = strAfterDot.length;
								if (objLen > decpoint.length) {
									valid = false;
								}
							}
						}
					} else {
						if (code == 46) {// key-in .
							var strAfterDot = obj.value.substr(indexCursor,
									objLen);
							if (strAfterDot) {
								objLen = strAfterDot.length;
								if (objLen > decpoint.length) {
									valid = false;
								}
							}
						}
					}

				}
			}
		}

		if (decpoint && decpoint.length && valid) {
			var indexCursor = doGetCaretPosition(obj);
			var a = obj.value.split(".");
			if (a.length > 1) {
				decValue = a[1]; // decimal value
				if (decValue && decValue != '') {
					if (decValue.length > decpoint.length) {
						valid = false;
					} else if (decValue.length == decpoint.length) {
						var indexdot = obj.value.indexOf(".");
						if ((code >= 48 && code <= 57)
								&& indexCursor > indexdot) {
							if(doGetSelectedLengthCaretPosition(obj)==0){
								valid = false;
							}
						}
					}
				}
			}
		}
		if (valid == false) {
			e.keyCode = 0;
			if (isFirefox == false) {
				if (event)
					(event.preventDefault) ? event.preventDefault()
							: event.returnValue = false;
			} else {
				e.preventDefault();
			}
			return false;
		} else {
			return true;
		}
	} else {
		return false;
	}
}

function validateDateFormatOnKeyPress(obj, e) {
	var valid = performFiltering(/[^\d]/g, e);
	if (valid) {
		if (!e)
			e = window.event;
		if (e.keyCode)
			code = e.keyCode;
		else if (e.which)
			code = e.which;

		if (isFirefox) {
			// backspace = 8, tab = 9, delete = 46, left = 37, right = 39,
			// paste=118
			if (code === 8 || code === 46) {
				return true;
			}
		}

		if (obj.value) {
			if (obj.value.length == 2 || obj.value.length == 5) {
				obj.value = obj.value + '/';
			}
		}
		return true;
	} else {
		return false;
	}
}

function validateDateFormat(obj) {
	var dateformat = /^(0?[1-9]|[12][0-9]|3[01])[\/](0?[1-9]|1[012])[\/]\d{4}$/;
	var val_date = trimValue($(obj).val());
	var valid = true;
	if (val_date.length > 0) {
		if (val_date.match(dateformat)) {
			var seperator1 = val_date.split('/');

			if (seperator1.length > 1) {
				var splitdate = val_date.split('/');
			}
			var dd = parseInt(splitdate[0], 10);
			var mm = parseInt(splitdate[1], 10);
			var yy = parseInt(splitdate[2]);
			var listofDays = [ 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 ];

			if (mm == 1 || mm > 2) {
				if (dd > listofDays[mm - 1]) {
					valid = false;
					// $(obj).val("");
					// $(obj).focus();
				}
			}
			if (mm == 2) {
				var lyear = false;
				if ((!(yy % 4) && yy % 100) || !(yy % 400)) {
					lyear = true;
				}
				if ((lyear == false) && (dd >= 29)) {
					valid = false;
					// $(obj).val("");
					// $(obj).focus();
				}
				if ((lyear == true) && (dd > 29)) {
					valid = false;
					// $(obj).val("");
					// $(obj).focus();
				}
			}
			if (valid) {
				var ddStr = "0";
				var mmStr = "0";
				if (dd < 10) {
					ddStr = ddStr + dd + "/";
				} else {
					ddStr = dd + "/";
				}
				if (mm < 10) {
					mmStr = mmStr + mm + "/";
				} else {
					mmStr = mm + "/";
				}
				$(obj).val(ddStr + mmStr + yy);
			}
		} else {
			// $(obj).val("");
			// $(obj).focus();
			valid = false;
		}
	}
	if (valid === false) {
		return false;
	} else {
		return true;
	}
}
function validateDateMMMYYFormatOnKeyPress(obj, e) {
	var valid = performFiltering(/[^a-zA-Z0-9*]/g, e);
	if (valid) {
		if (!e)
			e = window.event;
		if (e.keyCode)
			code = e.keyCode;
		else if (e.which)
			code = e.which;

		if (isFirefox) {
			// backspace = 8, tab = 9, delete = 46, left = 37, right = 39,
			// paste=118
			if (code === 8 || code === 46) {
				return true;
			}
		}

		if (obj.value) {
			if (obj.value.length == 3) {
				obj.value = obj.value + '-';
			}
		}
		return true;
	} else {
		return false;
	}
}

function changeCorrectDisplayMMMYY(obj) {
	var value = obj.value.replace(/[^a-zA-Z0-9*]/g, '');
	var yy = '';
	var mmm = '';
	if (value && value != "" && value.length >3) {
		mmm = value.substr(0, 3);
		yy = value.substr(3, obj.value.length);
		
		if (yy && yy != '') {
			yy = yy.replace(/[^\d]/g, '');
			if (yy && yy != '' && yy.length <= 1) {
				yy = '0'+ yy;
			}
		}
	}else{
		mmm = value;
	}
	if(yy==''){
		var theBigDay = new Date();
		yy = theBigDay.getFullYear();
		yy = yy.toString();
		yy = yy.substr(2, yy.length);
	}
	var newMMM = '';
	if (mmm.length <=3) {
		for (var i = 0; i < mmm.length; i++) {
			if(i==0){
				newMMM = mmm.charAt(i).toUpperCase();
			}else{
				newMMM = newMMM + mmm.charAt(i).toLowerCase();
			}
		}
	}
	
	var objRegExp = new RegExp('(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)');
	
	var newMMMYY = '';
	if (!objRegExp.test(newMMM)) {
		newMMMYY = '';
		if(obj.value && obj.value!=''){
			$(obj).focus();
		}
	}else{
		if (newMMM !== "") {				
			if (yy) {
				newMMMYY = newMMM + "-" + yy;
			}else{
				newMMMYY = newMMM + "-";
			}
		}
	}

	obj.value = newMMMYY;
}

function getSubstractOfGetsudoMonths(displayMonth, substract){
	var monthnames = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
	var arraydate = displayMonth.split('-');
	var month = arraydate[0];
    var monthvar = monthnames.indexOf(month);
    monthvar = monthvar+1;
    var year = arraydate[1];
    var day = '01';
    var newdate = new Date(year , monthvar-substract , 1);
    var yearstr;
    if(newdate.getYear() < 10){
    	 yearstr = '0'+newdate.getYear();
    }else{
    	yearstr = newdate.getYear();
    }
    
  	return monthnames[newdate.getMonth()]+'-'+yearstr;
}

function performFiltering(regex, e) {
	if (!e)
		e = window.event;
	if (e.keyCode)
		code = e.keyCode;
	else if (e.which)
		code = e.which;

	if (isFirefox) {
		// backspace = 8, tab = 9, delete = 46, left = 37, right = 39, paste=118
		if (code === 8 || code === 9 || code === 46 || code === 37
				|| code === 39 || code === 118) {
			return true;
		}
	}

	var strkeyin = String.fromCharCode(code);
	strkeyin = strkeyin.replace(regex, '');
	var code = strkeyin.charCodeAt(0);

	if (isNaN(code)) {
		e.keyCode = 0;
		if (isFirefox == false) {
			if (event)
				(event.preventDefault) ? event.preventDefault()
						: event.returnValue = false;
		} else {
			e.preventDefault();
		}
		return false;
	} else {
		return true;
	}
}

function generateComboOption(data, defaultLabel) {
	var html = "";
	if (defaultLabel && trimValue(defaultLabel) !== '') {
		html = '<option value="">&lt;' + defaultLabel + '&gt;</option>';
	}

	var arrSort = [];
	for ( var key in data) {
		arrSort.push({
			key : key,
			val : data[key]
		});
	}

	arrSort = arrSort.sort(function(a, b) {
		return a.val.localeCompare(b.val);
	});

	for (var i = 0, len = arrSort.length; i < len; i += 1) {
		html += '<option value="' + arrSort[i].key + "|" + arrSort[i].val
				+ '">' + arrSort[i].val + '</option>';
	}
	return html;
}
function generateComboOption1Value(data, defaultLabel) {
	var html = "";
	if (defaultLabel && trimValue(defaultLabel) !== '') {
		html = '<option value="">&lt;' + defaultLabel + '&gt;</option>';
	}
	var arrSort = [];
	for ( var key in data) {
		arrSort.push({
			key : key,
			val : data[key]
		});
	}

	arrSort = arrSort.sort(function(a, b) {
		return a.val.localeCompare(b.val);
	});

	for (var i = 0, len = arrSort.length; i < len; i += 1) {
		html += '<option value="' + arrSort[i].key + '">' + arrSort[i].val
				+ '</option>';
	}
	return html;
}


function trimValue(str) {
	str = $.trim(str);
	return (str);
}

/*******************************************************************************
 * get cursor position in specific a textbox/textarea
 ******************************************************************************/
/*******************************************************************************
 * function doGetCaretPosition (ctrl) { var CaretPos = 0; // IE Support
 * 
 * if (document.selection) { ctrl.focus (); var Sel =
 * document.selection.createRange (); Sel.moveStart ('character',
 * -ctrl.value.length); CaretPos = Sel.text.length; } // Firefox support else if
 * (ctrl.selectionStart || ctrl.selectionStart == '0') CaretPos =
 * ctrl.selectionStart; return (CaretPos); }
 ******************************************************************************/

function doGetCaretPosition(ctrl) {
	var el = $(ctrl).get(0);
	var pos = 0;
	if ('selectionStart' in el) {
		pos = el.selectionStart;
	} else if ('selection' in document) {
		el.focus();
		var Sel = document.selection.createRange();
		var SelLength = document.selection.createRange().text.length;
		Sel.moveStart('character', -el.value.length);
		pos = Sel.text.length - SelLength;
	}
	return pos;
}

function doGetSelectedLengthCaretPosition(ctrl) {
	var el = $(ctrl).get(0);
	var SelLength = 0;
	if ('selectionStart' in el) {
		sta = el.selectionStart;
		end = el.selectionEnd;
		SelLength = end - sta;
	} else if ('selection' in document) {
		el.focus();
		var Sel = document.selection.createRange();
		SelLength = document.selection.createRange().text.length;
	}
	return SelLength;
}
/*******************************************************************************
 * end get cursor position in specific a textbox/textarea
 ******************************************************************************/

function removeDashFromPartNo(obj, returnFlag){
	performFiltering(/[^a-zA-Z0-9*]/g,'');
	var objTexts = obj.value;
	if(objTexts != null && objTexts.length > 0){
		for(var i=0;i<objTexts.length;i++){
			if (objTexts.indexOf("-") > 0){
				objTexts = objTexts.replace("-","");
			}else{
				i = objTexts.length;
			}
		}
	}

	if(returnFlag){
		return objTexts;
	}else{
		obj.value = objTexts;
		obj.focus();
		if (document.addEventListener) {
			SetEnd(obj);
		}
	}		
}

function SetEnd(txt) {  
	if (txt.createTextRange) {  
		//IE  
		var FieldRange = txt.createTextRange();  
		FieldRange.moveStart('character', txt.value.length);  
		FieldRange.collapse();  
		FieldRange.select();  
	}else {  
		//Firefox and Opera  
		txt.focus();  
		var length = txt.value.length;  
		txt.setSelectionRange(length, length);  
	}  
}

function changePartNoToDisplayWithDash(obj){
	//ie8
	if (!document.addEventListener) {
		if (obj.value=="") {
			return;
		}
		var objTexts = obj.value.replace("-","");
		if(objTexts != null && objTexts.length > 0){
			var sizeV = objTexts.length;
			if(sizeV <= 5){
				result = objTexts;
			}else if(sizeV > 5 && sizeV <= 10){
				var str1 = objTexts.substring(0,5);
				var str2 = objTexts.substring(5,sizeV);
				result = str1 + '-' + str2;
			}else if(sizeV > 10){
				var str1 = objTexts.substring(0,5);
				var str2 = objTexts.substring(5,10);
				var str3 = objTexts.substring(10, sizeV);
				result = str1 + '-' + str2 + '-' + str3;
			}
		}		
	} else {
		//ie11
		performFiltering(/[^a-zA-Z0-9*]/g,'');
		var result = "";
		var objTexts = removeDashFromPartNo(obj, true);
		if(objTexts != null && objTexts.length > 0){
			var sizeV = objTexts.length;
			if(sizeV <= 5){
				result = objTexts;
			}else if(sizeV > 5 && sizeV <= 10){
				var str1 = objTexts.substring(0,5);
				var str2 = objTexts.substring(5,sizeV);
				result = str1 + '-' + str2;
			}else if(sizeV > 10){
				var str1 = objTexts.substring(0,5);
				var str2 = objTexts.substring(5,10);
				var str3 = objTexts.substring(10, sizeV);
				result = str1 + '-' + str2 + '-' + str3;
			}
		}		
	}
	obj.value = result;
 }

/****************************************
validate MandatoryField
****************************************/
function formValidateError(form){
	var mandaField = form.find( ".MandatoryField" );
	var i=0;
	var istatus = true;
	var focus = false;
	$.each( mandaField, function(key, value) {
		if(mandaField[i].disabled == false){
			str = mandaField[i].value;
			if (!str || str.length === 0) {
				var myLabel = $('label[for="'+ value.name +'"]');
				var msgName = myLabel[0].innerText;
				msgName = msgName.replace("*","");
				msgName = msgName.replace(":","");
				ST3Lib.message.addError(MSTD0031AERR.replace("{0}", msgName));
				ST3Lib.message.show(1);
				if(focus == false){
					mandaField[i].focus();
					focus = true;
				}
				istatus=false;
			}
		}
		i++;
	});
	return istatus;
};

/****************************************
remove - from PartNo 
****************************************/
function removePartNoFormat(obj) {
	$(obj).val($(obj).val().replace(/-/g, '').toUpperCase());
};

function doDateValidation(itemDate){
	if(itemDate){
		if (!isDateValid(itemDate.value)){
			itemDate.value = '';
			return false;
		}	
	}						
	return true;
}	

function validateDate(myTextbox) {
	if(myTextbox && typeof myTextbox != 'undefined'){
		if(myTextbox.addEventListener){
	    	myTextbox.addEventListener( 'keypress', function(event){validateDateFormatOnKeyPress(this,event)}, false );
	    	myTextbox.addEventListener( 'paste', function(event){pasteValidate(date, event)}, false );	
	    	myTextbox.addEventListener( 'blur', function(event){
	    			var flag = doDateValidation(this);
		    		if(flag==false){
		    			$(this).change();
		    		}
	    		}, false );
		}else{			
	    	myTextbox.attachEvent( 'onkeypress', function(event){validateDateFormatOnKeyPress(myTextbox,event)} );
	    	myTextbox.attachEvent( 'onpaste', function(event){pasteValidate(date, event)} );	
	    	myTextbox.attachEvent( 'onblur', function(event){
	    		var flag = doDateValidation(myTextbox);
	    		if(flag==false){
	    			$(this).change();
	    		}
	    	} );
		}
	}
}

function validateDateMMMYY(myTextbox) {
	if(myTextbox && typeof myTextbox != 'undefined'){
		if(myTextbox.addEventListener){
	    	myTextbox.addEventListener( 'keypress', function(event){validateDateMMMYYFormatOnKeyPress(this,event)}, false );
	    	myTextbox.addEventListener( 'paste', function(event){pasteValidate(alphaNumeric, event)}, false );	
	    	myTextbox.addEventListener( 'blur', function(event){changeCorrectDisplayMMMYY(this); $(this).attr("maxlength",6);}, false );
		}else{			
	    	myTextbox.attachEvent( 'onkeypress', function(event){validateDateMMMYYFormatOnKeyPress(myTextbox,event)} );
	    	myTextbox.attachEvent( 'onpaste', function(event){pasteValidate(alphaNumeric, event)} );	
	    	myTextbox.attachEvent( 'onblur', function(event){changeCorrectDisplayMMMYY(myTextbox);$(this).attr("maxlength",6);} );
		}
	}
}

function validateDecimal(myTextbox, maxlen, additionalCommaLen, decpoint, calculateAmountFlag) {
	if (myTextbox != null) {
		if(myTextbox.addEventListener){
			myTextbox.addEventListener( 'keypress', function(event){numbersWithDecimalOnlyOnKeyPress(this,decpoint,event)}, false );
			myTextbox.addEventListener( 'paste', function(event){pasteValidate(decimal, event, decpoint)}, false );
			myTextbox.addEventListener( 'focus', function(event){removeNumberWithCommas(this);$(this).attr("maxlength",maxlen);}, false );
			if(calculateAmountFlag){
				myTextbox.addEventListener( 'blur', function(event){addNumberWithComma(this,decpoint);$(this).attr("maxlength",maxlen+additionalCommaLen);calculateAmount(this);}, false );
			}else{
				myTextbox.addEventListener( 'blur', function(event){addNumberWithComma(this,decpoint);$(this).attr("maxlength",maxlen+additionalCommaLen);}, false );
			}			
		}else{	
			myTextbox.attachEvent( 'onkeypress', function(event){numbersWithDecimalOnlyOnKeyPress(myTextbox,decpoint,event)} );
			myTextbox.attachEvent( 'onpaste', function(event){pasteValidate(decimal, event, decpoint)} );
			myTextbox.attachEvent( 'onfocus', function(event){removeNumberWithCommas(myTextbox);$(this).attr("maxlength",maxlen);} );
			if(calculateAmountFlag){
				myTextbox.attachEvent( 'onblur', function(event){addNumberWithComma(myTextbox,decpoint);$(this).attr("maxlength",maxlen+additionalCommaLen);calculateAmount(this);} );
			}else{
				myTextbox.attachEvent( 'onblur', function(event){addNumberWithComma(myTextbox,decpoint);$(this).attr("maxlength",maxlen+additionalCommaLen);} );
			}
		}
	}
}


function validateDecimalWithMinus(myTextbox, maxlen, additionalCommaLen, decpoint, calculateAmountFlag) {
	if (myTextbox != null) {
		if(myTextbox.addEventListener){
			myTextbox.addEventListener( 'keypress', function(event){numbersWithDecimalOnlyOnKeyPress(this,decpoint,event,true)}, false );
			myTextbox.addEventListener( 'paste', function(event){pasteValidate(decimal, event, decpoint)}, false );
			myTextbox.addEventListener( 'focus', function(event){removeNumberWithCommas(this);$(this).attr("maxlength",maxlen);}, false );
			if(calculateAmountFlag){
				myTextbox.addEventListener( 'blur', function(event){addNumberWithComma(this,decpoint);$(this).attr("maxlength",maxlen+additionalCommaLen);calculateAmount(this);}, false );
			}else{
				myTextbox.addEventListener( 'blur', function(event){addNumberWithComma(this,decpoint);$(this).attr("maxlength",maxlen+additionalCommaLen);}, false );
			}			
		}else{	
			myTextbox.attachEvent( 'onkeypress', function(event){numbersWithDecimalOnlyOnKeyPress(myTextbox,decpoint,event,true)} );
			myTextbox.attachEvent( 'onpaste', function(event){pasteValidate(decimal, event, decpoint)} );
			myTextbox.attachEvent( 'onfocus', function(event){removeNumberWithCommas(myTextbox);$(this).attr("maxlength",maxlen);} );
			if(calculateAmountFlag){
				myTextbox.attachEvent( 'onblur', function(event){addNumberWithComma(myTextbox,decpoint);$(this).attr("maxlength",maxlen+additionalCommaLen);calculateAmount(this);} );
			}else{
				myTextbox.attachEvent( 'onblur', function(event){addNumberWithComma(myTextbox,decpoint);$(this).attr("maxlength",maxlen+additionalCommaLen);} );
			}
		}
	}
}

function validateNumber(myTextbox, maxlen, additionalCommaLen, isAllowNegativeValue) {
	if (myTextbox != null) {
		if(myTextbox.addEventListener){
			myTextbox.addEventListener( 'keypress', function(event){numbersOnlyOnKeyPress(this,event,isAllowNegativeValue)}, false );
			myTextbox.addEventListener( 'paste', function(event){pasteValidate(numeric, event, '')}, false );
			myTextbox.addEventListener( 'focus', function(event){removeNumberWithCommas(this);$(this).attr("maxlength",maxlen);}, false );
			myTextbox.addEventListener( 'blur', function(event){addNumberWithComma(this,'');$(this).attr("maxlength",maxlen+additionalCommaLen);}, false );
		}else{	
			myTextbox.attachEvent( 'onkeypress', function(event){numbersOnlyOnKeyPress(myTextbox,event,isAllowNegativeValue)} );
			myTextbox.attachEvent( 'onpaste', function(event){pasteValidate(numeric, event, '')} );
			myTextbox.attachEvent( 'onfocus', function(event){removeNumberWithCommas(myTextbox);$(this).attr("maxlength",maxlen);} );
			myTextbox.attachEvent( 'onblur', function(event){addNumberWithComma(myTextbox,'');$(this).attr("maxlength",maxlen+additionalCommaLen);} );
		}
	}
}

function validateTextFormat(myTextbox, myFormat, toUpper) {
	if(myTextbox.addEventListener){
		myTextbox.addEventListener( 'keypress', function(event){restrictCharacters(this, event, myFormat, toUpper)}, false );
		myTextbox.addEventListener( 'paste', function(event){pasteValidate(myFormat, event, '')}, false );
	}else{	
		myTextbox.attachEvent( 'onkeypress', function(event){restrictCharacters(myTextbox, event, myFormat, toUpper)}, false );
		myTextbox.attachEvent( 'onpaste', function(event){pasteValidate(myFormat, event, '')} );
	}
}

function validatePartNo(myTextbox, toUpper) {
	if(myTextbox.addEventListener){
		myTextbox.addEventListener( 'keypress', function(event){restrictCharacters(this, event, alphaNumeric, toUpper)}, false );
		myTextbox.addEventListener( 'paste', function(event){pasteValidate(alphaNumeric, event, '')}, false );
		myTextbox.addEventListener( 'focus', function(event){removeDashFromPartNo(this, false); $(this).attr("maxlength",15);}, false );
		myTextbox.addEventListener( 'blur', function(event){changePartNoToDisplayWithDash(this); $(this).attr("maxlength",17);}, false );
	}else{	
		myTextbox.attachEvent( 'onkeypress', function(event){restrictCharacters(myTextbox, event, alphaNumeric, toUpper)}, false );
		myTextbox.attachEvent( 'onpaste', function(event){pasteValidate(alphaNumeric, event, '')} );
		myTextbox.attachEvent( 'onfocus', function(event){removeDashFromPartNo(myTextbox, false);$(this).attr("maxlength",15);} );
		myTextbox.attachEvent( 'onblur', function(event){changePartNoToDisplayWithDash(myTextbox);$(this).attr("maxlength",17);} );
	}
}

function setFormFocus(str) {
	if (str!=null) {
		setTimeout(function() {			
			var cloneOemPartCostID = $("input[name="+str+"]").attr("id");
			if(cloneOemPartCostID){
				$("#"+cloneOemPartCostID).get(0).focus();
			}else{
				$("#"+str).get(0).focus();
			}
		}, 1);
	}
}

function addCommas(nStr) {
    nStr += '';
    x = nStr.split('.');
    x1 = x[0];
    x2 = x.length > 1 ? '.' + x[1] : '';
    var rgx = /(\d+)(\d{3})/;
    while (rgx.test(x1)) {
        x1 = x1.replace(rgx, '$1' + ',' + '$2');
    }
    return x1 + x2;
}

$('#openLogByAppId').live('click',function(e){
	var jsonParams = {
			"module" : $(this).attr("moduleId"),
			"function" : $(this).attr("functionId"),
			"appId" : $(this).attr("appId"),
			"dateFrom" : $(this).attr("dateFrom")==""?" ":$(this).attr("dateFrom"),
			"dateTo" : $(this).attr("dateTo")==""?" ":$(this).attr("dateTo")					
			};			
	
	GWRDSLib.PageOpen(_rootPath + '/common/logMonitoring', "WST33010", jsonParams);
});

$.fn.formatNumberWithcomma = function(){ 
    return this.each(function(){ 
        $(this).text( $(this).text().replace(/(\d)(?=(\d\d\d)+(?!\d))/g, "$1,") ); 
    })
}

function isDateValidDDMMYYYY(dateString){
	var valid = true;
	var date_regex = /^(0[1-9]|1\d|2\d|3[01])\/(0[1-9]|1[0-2])\/(19|20)\d{2}$/ ;
	var isValidateDateError = false;
	if(dateString && dateString != ''){
		if(!(date_regex.test(dateString)))
		{
			valid = false;
			isValidateDateError
		}
		
		if (!isValidateDateError){
		    if (!isDateValid(dateString)){
		    	valid = false;
		    }
		}
	}
	return valid;
}

function isDateValid(text){
    var comp = text.split('/');

    if (comp.length !== 3) {
        return false;
    }

    var d = parseInt(comp[0], 10);
    var m = parseInt(comp[1], 10);
    var y = parseInt(comp[2], 10);
    var date = new Date(y, m - 1, d);
    
    if (isNaN(date)) {
        return false;
    }
    
    return (date.getFullYear() == y && date.getMonth() + 1 == m && date.getDate() == d);
}

