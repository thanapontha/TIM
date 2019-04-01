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
	
		$( ".menu" ).accordion({ navigation: false, collapsible:true, heightStyle:'content', autoHeight:true })
			.on( "accordionbeforeactivate", function( event, ui ) {
				if( ui.newHeader && ui.newHeader.attr('data-enabled') == 'false') {
					event.preventDefault();
				}
				if( ui.oldHeader && ui.oldHeader.attr('data-enabled') == 'false') {
					event.preventDefault();
				}
			} )
			.each(function(i, e){
				var selected = $(e).find( '> h3[selected=true]:first' );
				if ( selected.size() )
					$(this).accordion( { active: selected.index() - 1 } );
			});
		ST3Lib.menu.setActiveMenuToCurrentPage();
		
		$( "#left-menu-panel" )
			.resizable( { minWidth: objVar.leftAreaWidth, maxWidth: $('#main').width() * 0.7, handles: "e" , iframeFix:false  })
			.find('*').attr('tabindex', '-1'); //prevent steal tableindex
		
		setTimeout( function() {
			ST3Lib.content.resize();
		}, 1);
		
		
		$('.toggle-menu, #grayline-logo-panel').click(function(){
			if ( $('#left-menu-panel').hasClass('toggle') ) {
				$('#grayline-logo-panel').removeClass('toggle');
				ST3Lib.menu.show();
			}else{
				$('#grayline-logo-panel').addClass('toggle');
				ST3Lib.menu.hide();
			}
			return false;
		});
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
			else objVar.$leftMenuPanel.height( objVar.$main.height() - objVar.$leftMenuPanel.offset().top -10);
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
//dummy function. it do nothing. but DO NOT REMOVE!!
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