$(document).ready(function() {
	$("textarea").on("paste", function() { return  pasteReasonValidation(this); });
	$("textarea").on("keypress", function() { return  imposeMaxLength(this); });
	
	$(document).find("input:text").blur(function() {
    	$(this).val($.trim($(this).val()));
    });
	
	$(".upper").blur(function() {
		$(this).val($(this).val().toUpperCase());
	});
	
});
var GWRDSLib = {};

GWRDSLib.window = window;
GWRDSLib.$ = $;

GWRDSLib.clearST3Message = function() {
	ST3Lib.message.setAppendMode(false);
	ST3Lib.message.clear(true);
	ST3Lib.message.setAppendMode(false);
}

GWRDSLib.slideToggle = function(obj, elem){
		if ((elem) && (obj)) {
			 $('#'+elem).slideToggle(200);
			 var current = obj.querySelectorAll(".arrow")[0].innerHTML.charCodeAt(0);;
			if (current == "9660") {
				obj.querySelectorAll(".arrow")[0].innerHTML = String.fromCharCode(9650);
			} else {
				obj.querySelectorAll(".arrow")[0].innerHTML = String.fromCharCode(9660);
			}
			//ST3Lib.sizing.autoHeight();
		} else {
			alert('Please specific element id. (GWRDSLib.slideToggle.elementId = "mydialog";)');
		}
};

GWRDSLib.dialog = {
	elementId: "",
	urlPage: "http://www.google.com",
	autoOpen: false,
	height: 600,
	width: 1000,
	title: "GWRDS",
	isShowBtnClose: false,
	closeOnEscape: true,
	$dialog: null,
	$output: null,
	init: function(){
		//IF element not exists, generate div
		if (!$('#'+this.elementId).length) $('body').append("<div id='"+this.elementId+"' style='display:none' class='genDlg'></div>");

		if(this.elementId != ""){
			var style = "<style>";
			style += '.frameObjectDataStyle .ui-widget-header {border: 1px solid #E60000;background: #E60000;font-weight: bold;color: white; }';
			style += '.noclose .ui-dialog-titlebar-close {display:none;}';
			style += '</style>';
			this.$dialog = $('#'+this.elementId)
					        .html('<iframe id="frameObjectData" style="border: 0px; " src="' + this.urlPage + '" width="100%" height="100%"></iframe>' + style)
					        .dialog({
					            autoOpen: this.autoOpen,
					            modal: true,
					            height: this.height,
					            width: this.width,
					            title: this.title,
					            closeOnEscape: this.closeOnEscape,
					            dialogClass: "frameObjectDataStyle noclose",
					            show: {
					                effect: 'fade',
					                duration: 500
					            },
					            hide: {
					                effect: 'fade',
					                duration: 300
					            }
					        });
		}else{
			alert('Please specific element id. (GWRDSLib.dialog.elementId = "mydialog";)');
		}
	},
	open: function(id, url, title, width, height, closeOnEscape){
		if(id != null) this.elementId = id;
		if(url != null) this.urlPage = url;
		if(title != null) this.title = title;
		if(width != null) this.width = width;
		if(height != null) this.height = height;
		if(closeOnEscape != null) this.closeOnEscape = closeOnEscape;
		this.init();
		this.$dialog.dialog('open');
		return this;
	},
	close: function(){
		this.$dialog.dialog('close');
	},
	showCloseButton: function(){
		this.isShowBtnClose = true;
		this.$dialog.dialog({dialogClass:'frameObjectDataStyle'});
	},
	hideCloseButton: function(){
		this.isShowBtnClose = false;
		this.$dialog.dialog({dialogClass:'frameObjectDataStyle noclose'});
	},
	_callback: function(data) {
		this.$dialog.dialog('close');
		this.$output = data;
	}
};

GWRDSLib.numberWithCommas = function(x, isDecimal) {
	
	isDecimal = typeof isDecimal !== 'undefined' ? isDecimal : true;
	
	if (x == null) return;
	
	if($.isNumeric(x)) {
	    var parts = x.toString().split(".");
	    parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
	    // don't have decimal set to 2 decimal ".00"
	    
	    if (isDecimal == true) {	    
		    if (parts.length == 0) {
		    	parts[0] = "0.00";
		    } else if(parts.length == 1){
		    	parts[1] = "00";
		    } else if (parts.length > 1 && (parts[1] || '').length == 1){
		    	parts[1] = (parts[1] || '') + "0";
		    }    
	    	return parts.join(".");
	    } else {
	    	return parts[0];
	    }
	}else{
		return x;
	}
};

GWRDSLib.concatFullName = function (title,firstName,lastName) {
		var otitle = '';
		var ofirstName = '';
		var olastName = '';
		if(title != null && title != '') 
			otitle = title + ' ';
		
		if(firstName != null && firstName != '') 
			ofirstName = firstName + ' ';
		
		if(lastName != null && lastName != '') 
			olastName = lastName;
		
		return otitle + ofirstName + olastName;
	};

	jQuery.fn.load = function( url, params, callback ) {
	if ( typeof url !== "string" && _load ) {
		return _load.apply( this, arguments );
	}
	var selector, response, type,
		self = this,
		off = url.indexOf(" ");
	if ( off >= 0 ) {
		selector = url.slice( off, url.length );
		url = url.slice( 0, off );
	}
	// If it's a function
	if ( jQuery.isFunction( params ) ) {
		callback = params;
		params = undefined;
	} else if ( params && typeof params === "object" ) {
		type = "POST";
	}

	if ( self.length > 0 ) {
		jQuery.ajax({
			url: url,
			type: type,
			dataType: "html",
			cache: false,
			headers: {
			      'Cache-Control': 'no-cache' 
			    },
			beforeSend: function(xhr) {
			    xhr.setRequestHeader("cache-control", "no-cache");
			  },
			data: params
		}).done(function( responseText ) {
			response = arguments;
			self.html( selector ?
				jQuery("<div>").append( jQuery.parseHTML( responseText ) ).find( selector ) :
				responseText );

		}).complete( callback && function( jqXHR, status ) {
			self.each( callback, response || [ jqXHR.responseText, status, jqXHR ] );
		});
	}

	return this;
};

var toUpper = true;
var numericOneToNine			= "[1-9]";
var numeric 					= "[0-9]";
var numericSign 				= "[-0-9]";
var decimal 					= "[0-9.]";
var decimalWithMinus			= "[0-9.,-]";
var numericComma				= "[0-9,]";
var decimalComma				= "[0-9.,]";
var date 						= "[0-9/]";
var alpha 						= "[a-zA-Z]";
var alphaNumeric 				= "[0-9a-zA-Z]";
var alphaWildcard 				= "[a-zA-Z*]";
var alphaNumericWildcard 		= "[0-9a-zA-Z*]";
var alphaSpace 					= "[a-zA-Z ]";
var alphaNumericSpace 			= "[0-9a-zA-Z ]";
var alphaSpaceWildcard 			= "[a-zA-Z *]";
var alphaNumericSpaceWildcard 	= "[0-9a-zA-Z *]";
var alphaNumericSlashDashUnder		= "[a-zA-Z0-9-_\/]";
var alphaNumericSlashDashUnderWildcard = "[a-zA-Z0-9-*_\/]";
var allCharacter 				= "all";
var entryCar = "";

function restrictCharacters(myfield, e, restrictionType, upperCase) {
	if (!e) var e = window.event;
	if (e.keyCode) code = e.keyCode;
	else if (e.which) code = e.which;
	var character = String.fromCharCode(code);
	entryCar=character;

	if (restrictionType==decimal){
		var valueOfField = myfield.value;
		if (valueOfField.indexOf('.') >= 0 && character == '.') {
			e.keyCode=0;
			event.preventDefault();
		}
	}
	if (code==13) {
		return true;
	}
	if(upperCase){
		character=character.toUpperCase();
		entryCar = character;
		e.keyCode=character.charCodeAt(0);
	}
	if(restrictionType==allCharacter){
		return;
	}
	// if they pressed esc... remove focus from field...
	if (code==27) {
		this.blur();
		(event.preventDefault) ? event.preventDefault() : event.returnValue = false;
	}
	// ignore if they are press other keys
	// strange because code: 39 is the down key AND ' key...
	// and DEL also equals .
	if (!e.ctrlKey && code!=9 && code!=8 && /*code!=36 && code!=37 && code!=38 && code!=40 &&*/ (code!=39 || (code==39 && character=="'")) )
	{
		if (!character.match(restrictionType)) {
			e.keyCode=0;
			(event.preventDefault) ? event.preventDefault() : event.returnValue = false;
		}
	}
	return true;
}


function pasteValidate(restrictionType, e) {
	if (window.clipboardData && window.clipboardData.getData) { // IE
		pasteData = window.clipboardData.getData('Text');
    } else if (e.clipboardData && e.clipboardData.getData) {
    	pasteData = e.clipboardData.getData('text/plain');
    }

	if (decimal == restrictionType) {
		var arrData = pasteData.split('.');
		
		if (arrData.length > 2) {
			(event.preventDefault) ? event.preventDefault() : event.returnValue = false;
		}
	}
	
	var modData = pasteData.replace(new RegExp("[\\s-]+","g"), "");
	var stError = modData.replace(new RegExp("^" + restrictionType + "+", "g"), "");
	if (stError=="") {
		return;
	} else {
		(event.preventDefault) ? event.preventDefault() : event.returnValue = false;
	}
	
}

jQuery.fn.serializeObject = function() {
	  var arrayData, objectData;
	  arrayData = this.serializeArray();
	  objectData = {};

	  $.each(arrayData, function() {
	    var value;

	    if (this.value != null) {
	      value = this.value;
	    } else {
	      value = '';
	    }

	    if (objectData[this.name] != null) {
	      if (!objectData[this.name].push) {
	        objectData[this.name] = [objectData[this.name]];
	      }

	      objectData[this.name].push(value);
	    } else {
	      objectData[this.name] = value;
	    }
	  });

	  return objectData;
	};


GWRDSLib.OpenOperationCommon = function(docNo, revision, mode) {
	var jsonParams = {"docNo" : docNo,
			  "revision" : revision,
			  "mode" : mode};
	
	if (mode === undefined) {
		jsonParams = {"docNo" : docNo,
				  "mode" : revision};
	}
	
	GWRDSLib.PageOpen(_rootPath+"/prs/CommonOperation", "operationCommon", jsonParams);
};
/*
 * Function GWRDSLib.PageOpen()
 * Parameters:
 * 		url 		= url of page to open on new window
 * 		windowName 	= Window name
 * 		jsonParams	= Parameter for open page
 * 					  Ex. { "Key1" : "Value", "Key2" : "Value" }
 */
GWRDSLib.PageOpen = function(url, windowName, jsonParams) {
	// Create Form
	var mapForm = document.createElement("form");
	mapForm.target = windowName;
    mapForm.method = "POST";
    mapForm.action = url;
    
    // Create input Element
    for(k in jsonParams) {
    	var v = jsonParams[k];
    	var mapInput = document.createElement("input");
        mapInput.type = "hidden";
        mapInput.name = k;
        mapInput.value = v;
        // Append element to form
        mapForm.appendChild(mapInput);
    }
    
    // Append form to page
    document.body.appendChild(mapForm);
    
    // Open new window
    map = window.open('', windowName);
    if (map) {
        try
        {
        	mapForm.submit();
        } 
        catch (ex)
        {
            if (ex.message.indexOf('Unspecified') == -1)
            {
                throw ex;
            }
        }
	    
	} else {
	    alert('You must allow popups for this map to work.');
	}
};

/*
 * Function GWRDSLib.selectOneCheckboxMode()
 * Parameters:
 * 		id 		= id of element that want to use select only one checkbox in a group
 * 		Sample call GWRDSLib.selectOneCheckboxMode('#result');
 * 		***NOTE: Checkbox must be generate before call this function.***
 */
GWRDSLib.selectOneCheckboxMode = function selectOneCheckboxMode(id){
	// the selector will match all input controls of type :checkbox
	// and attach a click event handler 
	$(id+" input:checkbox").on('click', function() {
	  // in the handler, 'this' refers to the box clicked on
	  var $box = $(this);
	  if ($box.is(":checked")) {
	    // the name of the box is retrieved using the .attr() method
	    // as it is assumed and expected to be immutable
	    var group = "input:checkbox[name='" + $box.attr("name") + "']";
	    // the checked state of the group/box on the other hand will change
	    // and the current value is retrieved using .prop() method
	    $(group).prop("checked", false);
	    $box.prop("checked", true);
	  } else {
	    $box.prop("checked", false);
	  }
	});
}

function imposeMaxLength(Object)
{
	var maxLen = Object.getAttribute("maxLength");

	//return (Object.value.length <= maxLen);
	if (Object.value.length < maxLen) {
		return true;
	}
	else {
		if (document.selection.createRange().text.length > 0) {
				return true;
			}
		return false;
	}
}

function replaceMaxLength(Object)
{
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
			   Object.caretPos.text=pasteData;
			}
	}

	if (Object.value.length > maxLen) {
		pasteData = Object.value.substring(0, maxLen);
		Object.value = pasteData;
		return false;
	}
}