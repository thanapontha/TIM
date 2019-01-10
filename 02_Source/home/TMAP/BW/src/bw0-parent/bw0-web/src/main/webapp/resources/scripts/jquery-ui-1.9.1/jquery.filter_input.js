/*

  Author - Rudolf Naprstek
  Website - http://www.thimbleopensource.com/tutorials-snippets/jquery-plugin-filter-text-input
  Version - 1.5.1
  Release - 6th June 2013

  Thanks to Niko Halink from ARGH!media for bugfix!
 
  Remy Blom: Added a callback function when the filter surpresses a keypress in order to give user feedback

  Don Myers: Added extension for using predefined filter masks

  Richard Eddy: Added extension for using negative number
  

*/

(function($){  
  
    $.fn.extend({   

        filter_input: function(options) {  

          var defaults = {  
              regex:".",
              negkey: false, // use "-" if you want to allow minus sign at the beginning of the string
              live:false,
              events:'keyup keypress paste'
          }  
                
          var options =  $.extend(defaults, options);  
          
//          var regex = new RegExp(options.regex);
          
			function getCaretPosition(node) {
				if(!node){
					return false;
				}else{
					var caretPos = 0;
					if (document.selection) {
						var textRange = document.selection.createRange ();
						textRange.moveStart ('character', -node.value.length);
						caretPos = textRange.text.length;
					}
					else if (node.selectionStart || node.selectionStart == '0')
						caretPos = node.selectionStart;
					return caretPos;
				}
			}
			function setCaretPosition(node,pos){

				if(node.setSelectionRange){
					node.setSelectionRange(pos,pos);
				}else if(node.createTextRange){
					var textRange = node.createTextRange();
					textRange.collapse(true);
					textRange.moveEnd('character', pos);
					textRange.moveStart('character', pos);
					textRange.select();
				}
			}
          function filter_input_function(event, valueBeforePaste) {
			var input = $(this);
			var strRegex = options.regex;
			if ( options.live ) {
				strRegex = input.data('filter') || ( input.data('filter', input.attr( 'filter' )) && input.data('filter') );
				if (!strRegex) return;
				if ( options.masks ) {
					if ( ! options.masks[ strRegex ] ) {
						var list = [];
						for ( var k in options.masks ) {
							list.push( k + ' = ' + options.masks[k] );
						}
						alert( 'Invalid filter name "' + strRegex + '". Filter should exist in below list\r\n' + list.join('\n') );
					}else strRegex = options.masks[ strRegex ] || strRegex;
				}
				var onerror = input.data('onfiltererror') || ( input.data('onfiltererror', input.attr( 'onfiltererror' )) && input.data('onfiltererror') ) || '';
				var uppercase = input.data('uppercase') || ( input.data('uppercase', input.hasClass( 'uppercase' )) && input.data('uppercase') ) || false;
				var lowercase = input.data('lowercase') || ( input.data('lowercase', input.hasClass( 'lowercase' )) && input.data('lowercase') ) || false;
				
			}
			if (event.type=='keypress') {

              var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;

              // 8 = backspace, 9 = tab, 13 = enter, 35 = end, 36 = home, 37 = left, 39 = right, 46 = delete
              if (key == 8 || key == 9 || key == 13 || key == 35 || key == 36|| key == 37 || key == 39 || key == 46) {

                // if charCode = key & keyCode = 0
                // 35 = #, 36 = $, 37 = %, 39 = ', 46 = .
         
                if (event.charCode == 0 && event.keyCode == key) {
                  return true;                                             
                }
              }
              var string = String.fromCharCode(key);
              // if they pressed the defined negative key
              if (options.negkey && string == options.negkey) {
                // if there is already one at the beginning, remove it
                if (input.val().substr(0, 1) == string) {
                  input.val(input.val().substring(1, input.val().length)).change();
                } else {
                  // it isn't there so add it to the beginning of the string
                  input.val(string + input.val()).change();
                }
                return false;
              }
              var regex = new RegExp(strRegex);
			}else if (event.type=='keyup') {
			  var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
			  if ( window.event.shiftKey || window.event.ctrlKey || window.event.altKey )
				return true;
			  // 8 = backspace, 9 = tab, 13 = enter, 35 = end, 36 = home, 37 = left, 39 = right, 46 = delete
              if (key == 8 || key == 9 || key == 13 || key == 35 || key == 36|| key == 37 || key == 39 || key == 46
				|| key == 16 || key == 17 || key == 18 || key == 20) {

                // if charCode = key & keyCode = 0
                // 35 = #, 36 = $, 37 = %, 39 = ', 46 = .
         
                return true;                                             
              }
			  var pos = getCaretPosition(this);
			  if ( uppercase )  input.val(input.val().toUpperCase());
			  else if ( lowercase ) input.val(input.val().toLowerCase());
			  setCaretPosition(this, pos);
              return true;
			} else if (event.type=='paste') {
			  //input.data('value_before_paste', event.target.value);
			  var prevData = event.target.value;
			  var self = this;
              setTimeout(function(){
                filter_input_function.call( self, {type:'after_paste'}, prevData );
              }, 1);
              return true;
            } else if (event.type=='after_paste') {
			  var clipboard = window.clipboardData || (window.event && window.event.clipboardData);
			  if ( clipboard ) clipboard = clipboard.getData( 'Text' );
              var string = clipboard || input.val();
              var regex = new RegExp('^('+strRegex+')+$');
            } else {
              return false;
            }
			
			if (regex.test(string)) {
			  var pos = getCaretPosition(this);
			  if ( uppercase )  input.val(input.val().toUpperCase());
			  else if ( lowercase ) input.val(input.val().toLowerCase());
			  setCaretPosition(this, pos);
			  return true;
            } else if (typeof(options.feedback) == 'function') {
              options.feedback.call(this, string);
            }
            if (event.type=='after_paste') {
				input.val(valueBeforePaste);
				var pos = getCaretPosition(this);
				if ( uppercase )  input.val(input.val().toUpperCase());
				else if ( lowercase ) input.val(input.val().toLowerCase());
				setCaretPosition(this, pos);
			}
			if ( onerror && onerror !== '' ) {
				this.errorText = string;
				(function(errorText){
					eval( onerror );
				}).call( this, string );
			}
			  
            return false;
          }
		  
          var input = $(this);
          var jquery_version = parseFloat(jQuery.fn.jquery.split('.')[0]+'.'+jQuery.fn.jquery.split('.')[1]);
          if (options.live) {
        	  $(this).on(options.events, options.selector, filter_input_function); 
            /*if (jquery_version >= 1.7) {
              $(this).on(options.events, options.selector, filter_input_function); 
            } else {
              $(this).live(options.events, filter_input_function); 
            }
            */
          } else {
            return this.each(function() {  
            	input.off(options.events).on(options.events, filter_input_function);
              var input = $(this);
              /*if (jquery_version >= 1.7) {
                input.off(options.events).on(options.events, filter_input_function);
              } else {
                input.unbind(options.events).bind(options.events, filter_input_function);
              }*/
            });  
          }
          
        }  
    });  
      
})(jQuery); 

/*
  Author - Don Myers
  Version - 0.1.0
  Release - March 1st 2013
*/

  /*
  use any of these filters or regular expression in some cases the regular expression is shorter but for some people the "names" might be easier

  ie.
   <input type="text" name="first_name" value="" data-mask="[a-zA-Z ]" placeholder="eg. John"/>
   <input type="text" name="last_name" value="" data-mask="int" placeholder="eg. Smith"/>

*/

/*
jQuery(document).ready(function() {

  var masks = {
    'int':     /[\d]/,
    'float':     /[\d\.]/,
    'money':    /[\d\.\s,]/,
    'num':      /[\d\-\.]/,
    'hex':      /[0-9a-f]/i,
    'email':    /[a-z0-9_\.\-@]/i,
    'alpha':    /[a-z_]/i,
    'alphanum': /[a-z0-9_]/i,
    'alphanumlower':/[a-z0-9_]/,
    'alphaspace':    /[a-z ]/i,
    'alphanumspace': /[a-z0-9_ ]/i,
    'alphanumspacelower':/[a-z0-9_ ]/
  };

  jQuery('input[data-mask]').each(function(idx) {
    var mask = jQuery(this).data('mask');
    var regex = (masks[mask]) ? masks[mask] : mask;

    jQuery(this).filter_input({ regex: regex, live: true }); 
  });
});

*/