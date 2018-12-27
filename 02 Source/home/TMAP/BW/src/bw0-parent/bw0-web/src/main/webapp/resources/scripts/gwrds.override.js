ST3Lib.dialog =  {
		btn: { ok:'', cancel:''},
		_confirmDefaultOptions: {},
		/** 
		 * Initial method. this method will be automatic execute
		 * when document ready
		 * @public */
		init: function(){
			var st3DialogDiv = $("#st3-dialog").length;
			if (st3DialogDiv == 0) {
				$('body').append(
						'<div id="st3-dialog" style="display:none;padding: 0em;">'+
							'<div class="application-panel">'+
								'<span id="logo" class="toggle-menu">' +
								'<span class="application" style="padding-left:2px;zoom:1;vertical-align:middle;" >Toyota</span></span>'+
								'<span class="application" style="padding-left:5px;zoom:1;vertical-align:middle;" >Standard</span>'+
								'<span class="userinfo"></span>'+
							'</div>'+
							'<div class="below-panel">'+
								'<div class="screenid-panel" style="background-color:rgb(255,171,171);">'+
									'<span class="screenid st3-title-dialog" style="color: black;margin-left: 2px;"></span>'+
									'<span class="time" style="color: black;"></span>'+
								'</div>'+
							'</div>'+
							'<div class="st3-msg" style="overflow:auto;auto;min-width: 340px;min-height: 50px;max-height: 250px;max-width: 380px;padding: 5px 5px 5px 5px;">'+
							'</div>'+
						'</div>'
				);
			}
			
			this._confirmDefaultOptions = {
				modal		: true,
				resizable	: false,
				show		: 'fade',
				hide		: 'fade',
				minHeight	: 150,
				maxHeight	: 450,
				minWidth	: 380,
				maxWidth	: 400,
				dialogClass	: 'modal-shadow',
				autoOpen 	: false,
				open: function(){
					$(this).siblings('.ui-dialog-buttonpane').find('input,button').prop('disabled', false);
				}
			};									 //style="overflow: auto;min-width: 380px;min-height: 50px;max-height: 450px;max-width: 400px;"
			//$confirm = $.getOrCreateDialog('confirm');	 //min-width:380px;min-height:150px;max-height:450px;max-width:400px;
			
			if (window['isIE6']) {
				this._confirmDefaultOptions.show = {effect: 'fade', duration: 0};
				this._confirmDefaultOptions.hide = {effect: 'fade', duration: 0};
				
			}
			//$('#st3-dialog').dialog($.extend({}, defaults, options));
			$('#st3-dialog').dialog(this._confirmDefaultOptions);
			$("#st3-dialog").prev().hide();
		},
		
		/**
		 * Show confirm dialog
		 * @public
		 * 
		 * @param {string} message Message to display
		 * @param {string} [title] Title of dialog
		 * @param {object} [options] JQuery dialog options
		 * @param {function} callback Call back function when click ok or cancel
		 * @example
		 * <b>Example 1</b>
		 *    //ST3Lib.dialog.confirm( message, title, options, callback )
		 *    ST3Lib.dialog.confirm( 'MSTD0003ACFM: Are you sure you want to abort the operation ?'
		 *      , 'MSTD0003ACFM'
		 *      , {minWidth:1000, minHeight:600}, function(result){
		 *          if (result){ 
		 *          	//do something when click OK
		 *          }else{
		 *          	//do something when click Cancel
		 *          }
		 *      });
		 * @example
		 * <b>Example 2</b>
		 * 	  //ST3Lib.dialog.confirm( message, options, callback )
		 *    ST3Lib.dialog.confirm('MSTD0003ACFM: Are you sure you want to abort the operation ?'
		 *      , {minWidth:1000, minHeight:600}, function(result){
		 *          if (result){ 
		 *          	//do something when click OK
		 *          }else{
		 *          	//do something when click Cancel
		 *          }
		 *      })
		 * @example
		 * <b>Example 3</b>
		 *    //ST3Lib.dialog.confirm( message, title, callback )
		 *    ST3Lib.dialog.confirm('MSTD0003ACFM: Are you sure you want to abort the operation ?'
		 *      , function(result){
		 *          if (result){ 
		 *          	//do something when click OK
		 *          }else{
		 *          	//do something when click Cancel
		 *          }
		 *      })
		 * @example
		 * <b>Example 4</b>
		 *    //ST3Lib.dialog.confirm( message, callback )
		 *    ST3Lib.dialog.confirm('MSTD0003ACFM: Are you sure you want to abort the operation ?',
		 *      function(result){
		 *          if (result){ 
		 *          	//do something when click OK
		 *          }else{
		 *          	//do something when click Cancel
		 *          }
		 *      })
		 */
		confirm: function(message,title,options,callback) {
			
			if ( typeof options === 'function' ) {
				callback = options;
				options = null;
			} if ( typeof title === 'object' ) {
				callback = options;
				options = title;
				title = null;
			}
			if ( typeof title === 'function' ) {
				callback = title;
				options = {};
				title = '';
			}
			title = title || (options && options.title) || '';
			
			
			// set message
			var dialog = $('#st3-dialog');
			dialog.find('div.st3-msg').html(message);
			dialog.find('span.st3-title-dialog').html(title);
			// init dialog st3-dialog confirm
			if (options) {
				dialog.dialog($.extend({}, this._confirmDefaultOptions, options));
				dialog.prev().hide();
				dialog.dialog('open');
			}else{
				dialog.dialog('option', 'title', title);
				dialog.dialog('option', {
					buttons		: 
					[{
				        text: ST3Lib.dialog.btn.ok ,
				        click: function() {
				        	var self = $(this);
							self.siblings('.ui-dialog-buttonpane')
								.find('input,button').prop('disabled', true);
							self.dialog('close');
							callback && callback(true);	
				        }},{
				        text: ST3Lib.dialog.btn.cancel ,
				        click: function() {
				        	$(this).dialog('close');
							callback && callback(false);		
				        }
					}]

				});
				dialog.dialog('open');
				$('.ui-button-text-only .ui-button-text').css('padding','0');
				$('.ui-dialog .ui-dialog-buttonpane button').css('width','80');
			}
		},
		
		prompt: function(message,title,options,callback) {
			
			if ( typeof options === 'function' ) {
				callback = options;
				options = null;
			} if ( typeof title === 'object' ) {
				callback = options;
				options = title;
				title = null;
			}
			if ( typeof title === 'function' ) {
				callback = title;
				options = {};
				title = '';
			}
			title = title || (options && options.title) || '';
			
			
			// set message
			var dialog = $('#st3-dialog');
			dialog.find('div.st3-msg').html(message);
			dialog.find('span.st3-title-dialog').html(title);
			// init dialog st3-dialog confirm
			if (options) {
				dialog.dialog($.extend({}, this._confirmDefaultOptions, options));
				dialog.prev().hide();
			}else{
				dialog.dialog('option', 'title', title);
				dialog.dialog('option', {
					buttons		: 
					[{
				        text: ST3Lib.dialog.btn.ok ,
				        
				        click: function() {
				        	$(this).dialog('close');
							callback && callback(false);		
				        }
					}]

				});
				dialog.dialog('open');
				$('.ui-button-text-only .ui-button-text').css('padding','0');
				$('.ui-dialog .ui-dialog-buttonpane button').css('width','80');
			}
		},
		
		/** 
		 * Show loading panel over specify element
		 * @public 
		 * @param {string|JQueryObject} target Selector or JQuery object for single element
		 * @param {object} [options] 
		 * 		<i>autoshow</i>: Default <i>true</i><br/>Show loading panel after initial<br/>
		 *      <i>disabledButton</i>: Default <i>false</i><br/>Disable all button when show loading panel
		 * */
		loading: function(target, options){
			if (ST3Lib._disabledST3Lib) return;
			
			var ret = {
				autoClose: true,
				div: null,
				target: null,
				window: null,
				id: null,
				
				/** 
				 * @method */
				init: function(target){
					this.window = window;
					this.target = $(target);
					this.div = $( '<div class="loading-panel"/>' );
					this.id = 'lid' + parseInt(Math.random() * 10000);
					this.div.attr( 'target', this.target.attr('id') );
					this.div.attr( 'id', this.id );
					this.div.hide();
					this.div.appendTo(this.target.parent());
					if (window['isIE6'] && $.fn.bgiframe) {
						this.div.bgiframe();
					}
				},
				/**
				 *  @method */
				changeTarget: function(target){
					if(this.div == null) return;
					this.target = $(target);
					this.div.appendTo(this.target.parent());
					this.resize();
				},
				/**
				 * @method */
				show: function(){
					if (this.div === null) return;
					this.div.show();
					this.resize();
				},
				/** 
				 * @method */
				resize: function(){
					if (this.div === null) return;
					var position = this.target.position();
					var parent = this.target.parent();
					this.div.css({
						left: position.left,
						top: position.top,
						width: this.target.outerWidth() + parent.scrollLeft(),
						height: this.target.outerHeight() + parent.scrollTop()
					});
					
				},
				/** 
				 * @method */
				close: function(){
					if (this.div === null) return;
					this.div.remove();
					this.div = null;
					if (this.disabledButton)
						ST3Lib.validate.disabledButton(false);
					$(this.window).off('autoheight.' + this.id);
				}
			};
			ret.init(target);
			if( !options || (options && (typeof options.autoshow === 'undefined' || options.autoshow === true)) ) {
				ret.show();
				ret.resize();
			}
			if( !options || (options && (typeof options.disabledButton === 'undefined' || options.disabledButton === true)) ) {
				ret.disabledButton = true;
				ST3Lib.validate.disabledButton(true);
			}
			
			$(window).on('autoheight.' + ret.id, function(){
				ret.resize();
			});
			return ret;
		}
	};

// st3.script.js
window.__initScriptFinish__ = false;
window.__disabledMinSize__ = false;
(function($, undefined) {
	$(function() {
		if (ST3Lib._disabledST3Lib) return;
		
		var objVar = {};
		objVar.leftAreaWidth = $('#logo-panel').width();
		objVar.cntResize = 1;
		objVar.cnt = 0;
		objVar.IECnt = 0;
		objVar.$header = $( '#header' );
		objVar.$main = $( '#main' );
		objVar.$grayLine = $('#grayline-logo-panel');
		objVar.$contentPanel = $('#content-panel');
		objVar.$leftMenuPanel = $( "#left-menu-panel" );
		objVar.$footer = $('#footer');
		objVar.$headerMobile = $('#header-mobile');
		objVar.$screenId = $('.screenid');
		objVar.$window = $(window);
		
		
		$(window).resize(function(e){
			if (ST3Lib._disabledST3Lib) return;
			
			if (window['isIE6']) {
				
				if (__disabledMinSize__ === false) {
					if ($('.ui-widget-overlay,#st3-dialog:visible').length > 0) return;
					ST3Lib.suppressEvent.resize(true);
					if (objVar.$window.width() < 950) {
						objVar.$main.css('width', 950)
							.addClass('disabled-autoheight');
					}else{
						objVar.$main.css('width', '')
							.removeClass('disabled-autoheight');
					}
					
					if (objVar.$window.height() < 500) {
						objVar.$main.css('height', 500)
							.addClass('disabled-autoheight');
					}else{
						objVar.$main.css('height', '')
							.removeClass('disabled-autoheight');
					}
					ST3Lib.suppressEvent.resize(false);
				}
			}
			__windowResize(objVar);
			
		});//resize

		$( '.button' ).button();
		
		ST3Lib.dataTable.initHookeDataTable();
		
		setTimeout(__afterInit, 100);
		
	});
	
	function __windowResize(objVar){
		var isLeftMenuHide = objVar.$grayLine.hasClass('toggle');
		if ( ST3Lib.menu._hide === false  ) {
			objVar.$contentPanel.css( 'margin-left', (isLeftMenuHide?0:5 + objVar.leftAreaWidth ) );
		}else{ objVar.$contentPanel.css( 'margin-left', '0px' ); }
		
		ST3Lib.sizing.autoHeight();
		
		if ( objVar.$leftMenuPanel.length > 0 ) {
			objVar.$leftMenuPanel.resizable( { maxWidth: objVar.$main.width() * 0.7 })
				.css( 'left', objVar.$header.offset().left - 5 )
				.css( 'top', objVar.$grayLine.offset().top + objVar.$grayLine.height() );
			if ( objVar.$footer.is(':visible') )
				objVar.$leftMenuPanel.height( objVar.$footer.offset().top - objVar.$leftMenuPanel.offset().top );
			else if (objVar.$headerMobile.is(':visible'))
				objVar.$leftMenuPanel.height( objVar.$main.height() - objVar.$headerMobile.height() )
					.css('left', 0).css('top', objVar.$headerMobile.height() );
			else objVar.$leftMenuPanel.height( objVar.$main.height() - objVar.$leftMenuPanel.offset().top );
		}
	}

	function __afterInit(){
		ST3Lib.dataTable.fixedTableHeader();
		ST3Lib.dataTable.fixedTableColumn();
		__initScriptFinish__ = true;
	}
	
})(jQuery);

function zeroLead(str, size) {
	str = '00' + str;
	return str.substr(str.length - size);
}
function showLoading (){}

Object.keys = Object.keys || function(o) {  
    var result = [];  
    for(var name in o) {  
        if (o.hasOwnProperty(name))  
          result.push(name);  
    }  
    return result;  
};

String.format = function() {
	var s = arguments[0];
	var reg = new RegExp("\\{([0-9]+)\\}", "gm");
	var args = arguments;
	return s.replace(reg, function(str, group){
		return args[~~group + 1];
	});
};
String.prototype.format = function(){
	[].splice.call(arguments, 0, 0, this);
	return String.format.apply(null, arguments);
};

Date.prototype.format = function(format){
	return ST3Lib.$.datepicker.formatDate(format, this);
};

Array.prototype.forEach = Array.prototype.forEach || function(fn, scope) {
	for(var i = 0, len = this.length; i < len; ++i) {
		if (i in this) {
			fn.call(scope, this[i], i, this);
		}
	}
};

Array.prototype.filter = Array.prototype.filter || function(fun /*, thisp */){
	"use strict";
	if (this == null)
		throw new TypeError();
	
	var t = Object(this);
	var len = t.length >>> 0;
	if (typeof fun != "function")
		throw new TypeError();
	
	var res = [];
	var thisp = arguments[1];
	for (var i = 0; i < len; i++) {
		if (i in t) {
			var val = t[i]; // in case fun mutates this
			if (fun.call(thisp, val, i, t))
				res.push(val);
		}
	}

	return res;
};

Array.prototype.indexOf = Array.prototype.indexOf || function (searchElement , fromIndex) {
	var i,
	pivot = (fromIndex) ? fromIndex : 0,
	length;

	if (!this) {
		throw new TypeError();
	}

	length = this.length;

	if (length === 0 || pivot >= length) {
		return -1;
	}

	if (pivot < 0) {
		pivot = length - Math.abs(pivot);
	}

	for (i = pivot; i < length; i++) {
		if (this[i] === searchElement) {
			return i;
		}
	}
	return -1;
};

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
				if(myLabel[0] == null){
					var msgName = value.title;
				}else{
					var msgName = myLabel[0].innerText;
				}
				
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
// End st3.script.js