/** 
 * @module ST3Lib
 */
/** @class 
 * Main Object for ST3Lib
 * 
 * */
var ST3Lib = {};

ST3Lib.window = window;

ST3Lib.$ = $;

if ( !window.console )
	window.console = { log: function(){} };

if ( !window.console.time ) {
	window.console.time = function(){};
	window.console.timeEnd = function(){};
}
(function($, undefined) {
	
	ST3Lib.id = {
		ID_LOGO_PANEL: '#logo-panel'
		,ID_LEFT_MENU_PANEL: '#left-menu-panel'
		,ID_RIGHT_AREA: '#right-area'
		,ID_TIME_LABEL: '#right-area .time'
		,ID_TIMESEC_LABEL: '#right-area .timeSec'
		,ID_TIMEMOBILE_LABEL: '#grayline-logo-panel-mobile .time'
		,ID_MENU_CONTAINER: '#menu-container'
		,ID_HEADER_TEMPLATE: '#headertemplate'
		,ID_ERRORMSG: '#messageBar'
		
		,ID_BUTTON_PANEL: '.button-panel'
		,ID_CONTENT_PANEL: '#content-panel'
	};
	
	ST3Lib.static = {
		ANIMATE_DURATION: 300
		
	};
	
	ST3Lib.area = {
		getLeftAreaWidth: function(){
			return $(ST3Lib.id.ID_LOGO_PANEL).width();
		}
	};
	
	/** @class */
	ST3Lib.menu = {
		_nomenu: false,
		_lock: false
		,_hide: false
		/** 
		 * Initial method, this method will be automatic execute when document ready
		 * @public
		 *  */
		,init: function(){
			var leftPanel = $(ST3Lib.id.ID_LEFT_MENU_PANEL);
			
			if ( this._nomenu || leftPanel.length === 0 ) {
				ST3Lib.menu.hide(1, true);
				ST3Lib.menu._hide = true;
				$('#toggle-menu-right').addClass('hide');
				$('#toggle-menu-left').addClass('hide');
				leftPanel.addClass('toggle');
			}else {
				ST3Lib.menu._hide = false;
				$('#toggle-menu-right').removeClass('hide');
				$('#toggle-menu-left').removeClass('hide');
				
				if ( this.getMenuCookieVisible() ) {
					ST3Lib.menu.show(1);
					leftPanel.show();
				} else {
					leftPanel.css( 'visibility', 'hidden' );
					$(ST3Lib.id.ID_LEFT_MENU_PANEL).show(); 
					ST3Lib.menu.hide(0);
					setTimeout(function(){leftPanel.css( 'visibility', 'visible' );}, 1000 );
				}
			}
			setTimeout(function(){
				$(window).resize( function(){
					if ( $(ST3Lib.id.ID_LEFT_MENU_PANEL).hasClass( 'transition' ) ) return;
					if ( $(window).width() <= 800 ) {
							ST3Lib.menu.hide(1, true);
					}else if ( ST3Lib.menu.getMenuCookieVisible() ) ST3Lib.menu.show();
				});	
			}, 3000);
		},
		setActiveMenuToCurrentPage: function(){
			var menu = $('#menu');
			if (menu.accordion) {
				var item = $(ST3Lib.id.ID_MENU_CONTAINER).find('.item a[href=' + window.location.pathname.replace(/\//g, '\\/').replace(/;.*/g, '') + ']');
				if (item.length) {
					var index = item.closest('.ui-accordion-content').prev('H3').index('H3');
					menu.accordion('option', 'active', index);
				}
			}
		},
		lock: function(){
			ST3Lib.menu.show();
			ST3Lib.menu._lock = true;
			
		}
		,unlock: function(){
			ST3Lib.menu._lock = false;
			
		}
		,toggleLock: function(){
			ST3Lib.menu._lock?ST3Lib.menu.unlock():ST3Lib.menu.lock();
			return ST3Lib.menu._lock;
		}
		
		,_has3d : null
		,has3d: function () {
			return false;
			var ST3 = ST3Lib;
			if ( ST3Lib.menu._has3d !== null ) return ST3Lib.menu._has3d;
			var el = document.createElement('p'), 
	        has3d,
	        transforms = {
	            'webkitTransform':'-webkit-transform',
	            'OTransform':'-o-transform',
	            'msTransform':'-ms-transform',
	            'MozTransform':'-moz-transform',
	            'transform':'transform'
	        };
			
			// Add it to the body to get the computed style.
			document.body.insertBefore(el, null);
			
			for (var t in transforms) {
				if (el.style[t] !== undefined) {
					el.style[t] = "translate3d(1px,1px,1px)";
					has3d = window.getComputedStyle(el).getPropertyValue(transforms[t]);
				}
			}
			
			document.body.removeChild(el);
			
			return ST3Lib.menu._has3d = (has3d !== undefined && has3d.length > 0 && has3d !== "none");
		}
		,setMenuCookieVisible: function(visible){
			return;
			if ( $.cookie ) {
				$.cookie( 'menu-visible', visible?'True':'False' );
			}
		}
		,getMenuCookieVisible: function() {
			return true;
			if ( $.cookie ) {
				if ( $.cookie( 'menu-visible') )
					return $.cookie( 'menu-visible') === 'True';
				else return true;
			}else return true;
		}
		/** 
		 * Show menu
		 * @public
		 * @param {number} [duration] Animation time
		 * @param {boolean} [notSetToCookie] not used
		 *  */
		,show : function(duration, notSetToCookie){
			if ( ST3Lib.menu._hide ) return;
			var leftPanel = $(ST3Lib.id.ID_LEFT_MENU_PANEL);
			if ( leftPanel.hasClass('toggle') ) {
				if (!notSetToCookie)
					this.setMenuCookieVisible( true );
				$('#grayline-logo-panel').removeClass('toggle');
				var leftAreaWidth = ST3Lib.area.getLeftAreaWidth();
				var menuContainer = $(ST3Lib.id.ID_MENU_CONTAINER);
				
				leftPanel.removeClass('toggle');
				if ( ST3Lib.menu.has3d() == false ) {
					leftPanel.addClass( 'transition' );
					leftPanel.stop().animate({
						width: leftAreaWidth
					}, duration || ST3Lib.static.ANIMATE_DURATION, function(){ 
								setTimeout( function(){leftPanel.removeClass( 'transition' );}, 500); 
							});
					menuContainer.stop().animate({
						left:0
					}, duration || ST3Lib.static.ANIMATE_DURATION);
				}
				
				setTimeout( function(){ ST3Lib.content.resize(); }, duration || ST3Lib.static.ANIMATE_DURATION );
			}
		}
		/** 
		 * Hide menu
		 * @public
		 * @param {number} [duration] Animation time
		 * @param {boolean} [notSetToCookie] not used
		 *  */
		,hide: function(duration, notSetToCookie) {
			if ( ST3Lib.menu._hide ) return;
			if ( ST3Lib.menu._lock ) return false;
			var leftPanel = $(ST3Lib.id.ID_LEFT_MENU_PANEL);
			if ( ! leftPanel.hasClass('toggle') ) {
				if (!notSetToCookie)
					this.setMenuCookieVisible( false );
				$('#grayline-logo-panel').addClass('toggle');
				var leftAreaWidth = ST3Lib.area.getLeftAreaWidth();
				var menuContainer = $(ST3Lib.id.ID_MENU_CONTAINER);
				
				leftPanel.addClass('toggle');
				if ( ST3Lib.menu.has3d() == false ) {
					leftPanel.addClass( 'transition' );
					leftPanel.stop().animate({
						width: 0
					}, duration || ST3Lib.static.ANIMATE_DURATION, function(){ 
							setTimeout( function(){leftPanel.removeClass( 'transition' );}, 500); 
						});
					menuContainer.stop().animate({
						left:-leftAreaWidth
					},duration || ST3Lib.static.ANIMATE_DURATION);
				}
				setTimeout( function(){ ST3Lib.content.resize(); }, duration || ST3Lib.static.ANIMATE_DURATION );
			}
		}
	};
	
	/** @class
	 * Manage error/info/warning message
	 *  */
	ST3Lib.message = {
		_changeTarget: false,
		/**
		 * Initial function. this function will be automatic execute when document is ready
		 *  @public */
		init: function(){
			this.panel = $(ST3Lib.id.ID_ERRORMSG);
			var self = this;
			//errormsg.css('min-height', 12 * 1.7);
			this.panel.find('div.toggle-icon').click(function(){
				self.toggle();
				return false;
			});
		},
		/** 
		 * Change output message to display on specify target
		 * @public 
		 * @param {string|JQueryObject} target Selector or JQuery object for target element
		 * */
		changeTarget: function(target){
			if (target) {
				this.panel = $(target);
				if (this.panel.attr('id') !== ST3Lib.id.ID_ERRORMSG) 
					this._changeTarget = true;
				else this._changeTarget = false;
			}else {
				this.panel = $(ST3Lib.id.ID_ERRORMSG);
				this._changeTarget = false;
			}
		},
		_lastHeight: 0,
		/** 
		 * Show error message
		 * @public 
		 * @param {number} [duration=500] Duration of animation when show message
		 * 
		 * */
		show: function(duration){
			if (this.count() === 0) {
				this.hide(1);
				return;
			}
			if (this.panel.is(':visible') && this.panel.hasClass('toggle') === false) {
				var lastHeight = this.panel.height();
				if (lastHeight !== this._lastHeight) {
					this._lastHeight = lastHeight;
					this.panel.removeClass('toggle');
					ST3Lib.sizing.autoHeight(ST3Lib.id.ID_CONTENT_PANEL);
				}
			}else{
				if ( duration === true || duration === 1) {
					var self = this;
					this.panel.css({'height':'', display:'block'});
					ST3Lib.sizing.autoHeight(ST3Lib.id.ID_CONTENT_PANEL);
					self._lastHeight = self.panel.height();
					this.panel.removeClass('toggle');
				}else{
					var self = this;
					var errormsg = this.panel;
					var miniHeight = errormsg.attr('lastheight') || errormsg.height();
					
					errormsg.stop().animate({
						height: miniHeight, display:'block'
					}, duration|| 500, function(){ 
						errormsg.css( 'display', 'block' ); 
						errormsg.css('height','');
						errormsg.css('overflow','hidden');
						errormsg.removeClass('toggle');
						ST3Lib.sizing.autoHeight(ST3Lib.id.ID_CONTENT_PANEL);
						self._lastHeight = errormsg.height();
					});
				}
			}
		}
		/** 
		 * Hide error message
		 * @public 
		 * @param {number} [duration=500] Duration of animation when hide message
		 * 
		 * */
		,hide: function(duration){
			if (this._changeTarget === false && this.panel.is(':hidden')) return;
			var errormsg = this.panel;
			if ( duration === true || duration === 1) {
				this.panel.hide();
				ST3Lib.sizing.autoHeight(ST3Lib.id.ID_CONTENT_PANEL);
				this._lastHeight = 0;
				errormsg.addClass('toggle');
			}else{
				this.panel.attr('lastheight', this.panel.height() );
				this.panel.stop().animate({
					height: 12* 1.5, display:'block'
				}, duration || 500, function(){ 
					errormsg.css( 'display', 'block' );
					errormsg.addClass('toggle');
					ST3Lib.sizing.autoHeight(ST3Lib.id.ID_CONTENT_PANEL); 
					this._lastHeight = 0;
				});
			}
		}
		/**
		 * Toggle show/hide of error message
		 *  @public
		 *  */
		,toggle: function(){
			if ( this.panel.hasClass('toggle') ) {
				this.show();
			}else{
				this.hide();
			}
		}
		,
		/**
		 * Prevent other opeation clear error message
		 * @public
		 * @param {boolean} append enable/disable append mode
		 *  
		 *  */
		setAppendMode: function(append){
			this.append = append;
		},
		/** @private 
		 *  @param {string} msh Message to add to message panel
		 * */
		add: function( type, msg ){
			if ($.trim(msg).charAt(msg.length - 1) !== '.'){
				msg += '.';
			}
			this.panel.append( '<div class="message ' + type + '">' + msg + '</div>' );
		},
		/** 
		 * Add info message
		 * @public 
		 * @param {string or string[]} msg Message to add to message panel
		 * */
		addInfo: function( msg ){
			if ($.type(msg) === 'array') { //is array
				for(var i = 0; i < msg.length; ++i) {
					this.add( 'inf', msg[i] );
				}
			}else{
				this.add( 'inf', msg );
			}
		},
		/** 
		 * Add warning message
		 * @public 
		 * @param {string or string[]} msg Message to add to message panel
		 * */
		addWarning: function( msg ){
			if ($.type(msg) === 'array') { //is array
				for(var i = 0; i < msg.length; ++i) {
					this.add( 'warn', msg[i] );
				}
			}else{
				this.add( 'warn', msg );
			}
		},
		/** 
		 * Add error message
		 * @public
		 * @param {string or string[]} msg Message to add to message panel
		 *  */
		addError: function( msg ){
			if ($.type(msg) === 'array') { //is array
				for(var i = 0; i < msg.length; ++i) {
					this.add( 'err', msg[i] );
				}
			}else{
				this.add( 'err', msg );
			}
		},
		/** 
		 * Check message contains specify string or not.
		 * @public
		 * @param {string} str String to check
		 *  */
		hasMessage: function(str){
			if ( !str ) return false;
			if ($.type(str) === 'array') { //is array
				var html = this.panel.html();
				for(var i = 0; i < str.length; ++i){
					var reg = new RegExp(str[i], 'i');
					if (reg.test(html))
						return true;
				}
				return false;
			}else
				return (new RegExp(str, 'i')).test(this.panel.html());
		},
		/**
		 * Clear all message and hide message panel
		 *  @public 
		 *  @param {boolean} [hide=true] hide message panel
		 *  */
		clear: function(hide){
			if (this.append) return;
			this.panel.find( 'div.message' ).remove();
			if (hide === true || typeof hide === 'undefined')
				this.hide(true);
		},
		/** 
		 * Count number of message in message panel
		 * @public */
		count: function(){
			return this.panel.find( 'div.message' ).length;
		},
		/** 
		 * Count number of error message in message panel
		 * @public */
		countError: function(){
			return this.panel.find( 'div.err' ).length;
		},
		/**
		 * Count number of warning message in message panel
		 *  @public */
		countWarning: function(){
			return this.panel.find( 'div.warn' ).length;
		},
		/** 
		 * Count number of info message in message panel
		 * @public */
		countInfo: function(){
			return this.panel.find( 'div.inf' ).length;
		},
		setTime: function(time, timeSec){
			$(ST3Lib.id.ID_TIME_LABEL).text(time || timeSec);
			$(ST3Lib.id.ID_TIMESEC_LABEL).text(timeSec || time);
			//window.clearInterval(ST3Lib.compareTime.interval);
			//ST3Lib.compareTime.getNewTime = true;
			//ST3Lib.compareTime.init();
		},
		setPayload: function(payload, notShowMsg){
			if ( !payload ) return;
			
			if (payload.focusId) {
				setTimeout(function(){
					$(payload.focusId).focus();
				},1);//steal focus
			}
			
			if ( payload.dateTimeNow ) {
				this.setTime(payload.dateTimeNow, payload.dateTimeNowSec);
			}
			
			if (notShowMsg || !payload.haveMessages ) return;
			this.clear(true);
			if ( payload.errorMessages ) {
				for(var i = 0; i < payload.errorMessages.length; ++i) {
					this.addError(payload.errorMessages[i]);
				}
			}
			if ( payload.warningMessages ) {
				for(var i = 0; i < payload.warningMessages.length; ++i) {
					this.addWarning(payload.warningMessages[i]);
				}
			}
			if ( payload.infoMessages ) {
					for(var i = 0; i < payload.infoMessages.length; ++i) {
					this.addInfo(payload.infoMessages[i]);
				}
			}
			this.show(1);
		}
			
	};
	
	
	
	/*
		Using: <input type="text" class="{uppercase|lowercase}" filter="{filtername}" onfiltererror="{code}">
		filter: attribute for assign specific filter to input element
		onfiltererror: attribute for custom javascript code, it will invoked after input text does not pass the regex test
		{uppercase}: apply class 'uppercase' to convert string to uppercase automatically
		{lowercase}: apply class 'lowercase' to convert string to lowercase automatically
		{filtername}: name of filter (see:  ST3Lib.filter._masks)
		{code} : 
			this = DOM object of Input element
			this.errorText or errorText = text that not pass regex test
		Ex.     <input type="text" name="textboxname" filter="number" onfiltererror="alert( this.name + ': Not allow invalid text ' + this.errorText  )">
		same as <input type="text" name="textboxname" filter="number" onfiltererror="alert( this.name + ': Not allow invalid text ' + errorText  )">
	*/
	ST3Lib.filter = {
		_masks: {
			numeric:	"[0-9]",
			numericWildcard: "[0-9*]",
			numericComma: "[0-9,]",
			numericUnderScoreWildcard: "[0-9_*]",
			decimal: "[0-9.]",
			decimalComma: "[0-9.,]",
			date: "[0-9/]",
			alpha: "[a-zA-Z]",
			alphaNumeric: "[0-9a-zA-Z]",
			alphaNumericWithDash: "[0-9a-zA-Z-]",
			alphaWildcard: "[a-zA-Z*]",
			alphaNumericWildcard: "[0-9a-zA-Z*]",
			alphaNumericUnderScore: "[0-9a-zA-Z_]",
			alphaNumericUnderScoreWildcard: "[0-9a-zA-Z_*]",
			alphaSpace: "[a-zA-Z ]",
			alphaNumericSpace: "[0-9a-zA-Z ]",
			alphaSpaceWildcard: "[a-zA-Z *]",
			alphaNumericSpaceWildcard: "[0-9a-zA-Z *]",
			alphaNumericWithDashSlashSpace: "[0-9a-zA-Z-/ ]",
			alphaNumericWithDashSlashSpaceWildcard: "[0-9a-zA-Z-/ *]",
			alphaNumericWithDashSlashSpaceUnderscore: "[0-9a-zA-Z-/ _]",
			alphaNumericWithDashSlash: "[0-9a-zA-Z-/]",
			excludeForbiddenSpecialChar: "[^~%\\\\;?]",
			all: ".",
			description: "[0-9a-zA-Z.,& -]"
		},
		/** @public */
		init: function() {
			if ( ! $.fn.filter_input ) return;
			$( 'form' ).filter_input({
				selector: 'input[type=text][filter],input[type=password][filter],textarea[filter]',
				masks: this._masks,
				live:true
			});
		},
		/** @public */
		addFormat: function( format, strRegex ){
			this._masks[ format ] = strRegex;
		}
		
	};
	
	/** @class */
	ST3Lib.sizing = {
		_debug: false,
		_nocache: false,
		_except: {'BODY':true, 'HTML': true, '#document': true},
		_cache: {},
		_cacheList: [],
		$document: null,
		$window: null,
		$body: null,
		
		/** 
		 * Initial method. this method will be automatic execute when document ready
		 * @public */
		init: function(){
			if (document.styleSheets[0] && document.styleSheets[0].addRule) {
				document.styleSheets[0].addRule('.sizing-overflow-hidden', 'overflow:hidden !important;');
			}else if (document.styleSheets[0] && document.styleSheets[0].insertRule) {
				document.styleSheets[0].insertRule('.sizing-overflow-hidden{ overflow:hidden !important; }', 0);
			}
			
			this.$document = $(document);
			this.$window = $(window);
			this.$body = $(document.body);
			this.reset(this.$body);
		},
		refreshCache: function(selector){
			for(var i = 0; i < this._cacheList.length; ++i) {
				var cache = this._cacheList[i];
				var selectorType = typeof selector;
				var cacheType = typeof cache.seelctor;
				var nodeList = null;
				if (cache.selector === selector) {
					this._getNodeList(selector, true);
				} else if (selectorType === cacheType === 'string' &&
						cache.selector === selector) {
					this._getNodeList(selector, true);
				} else if (selectorType === cacheType === 'object' &&
						cache.selector[0] === selector[0]){
					this._getNodeList(selector, true);
				}
				if (nodeList) {
					break;
				}
			}
			//this._getNodeList(selector, true);
		},
		/** @private */
		saveState: function($obj){
			if ($obj.length === 0) return;
			var dom = $obj[0];
			dom._state = {
				position: $obj.position(),
				width: $obj.width(),
				height: $obj.height()
			};
		},
		/** @private */
		checkState: function($obj){
			if ($obj.length === 0) return false;
			var state = $obj[0]._state;
			if (!state) return true;
			
			var position = $obj.position();
			var width = $obj.width();
			var height = $obj.height();
			
			var ret = state.width !== width ||
				state.height !== height ||
				state.position.left !== position.left ||
				state.position.top !== position.top;
			
			return ret;
		},
		/** @private */
		_setCache: function(selector, $nodeList){
			var selectedCache = null;
			for(var i = 0; i < this._cacheList.length; ++i) {
				var cache = this._cacheList[i];
				var selectorType = typeof selector;
				var cacheType = typeof cache.seelctor;
				if (cache.selector === selector) {
					selectedCache = cache;
					break;
				} else if (selectorType === cacheType === 'string' &&
						cache.selector === selector) {
					selectedCache = cache;
					break;
				} else if (selectorType === cacheType === 'object' &&
						cache.selector[0] === selector[0]){
					selectedCache = cache;
					break;
				}
			}
			
			if (selectedCache)
				selectedCache.nodeList = $nodeList;
			else{
				this._cacheList.push(selectedCache = {
					selector: selector,
					nodeList: $nodeList
				});
			}
			return selectedCache;
		},
		/** @private */
		_getCache: function(selector){
			for(var i = 0; i < this._cacheList.length; ++i) {
				var cache = this._cacheList[i];
				var selectorType = typeof selector;
				var cacheType = typeof cache.seelctor;
				if (cache.selector === selector) {
					return cache.nodeList;
				} else if (selectorType === cacheType === 'string' &&
						cache.selector === selector) {
					return cache.nodeList;
				} else if (selectorType === cacheType === 'object' &&
						cache.selector[0] === selector[0]){
					return cache.nodeList;
				}
			}
			return null;
		},
		/** @private */
		_getNodeList: function(selector, forceUpdate){
			selector = selector || '';
			var $selector = null;
			var root = $(selector || document);
			if (root.length === 0) return null;
			
			if (this._nocache === false && !forceUpdate)
					$selector = this._getCache(selector);
			
			if (forceUpdate || $selector == null) {
				if (window['isIE6'])
					$selector = root.find( 'div.autoheight,form.autoheight' );
				else $selector = root.find( '.autoheight' );
				if (root.hasClass('autoheight'))
					Array.prototype.splice.call($selector,0, -1, root[0]);
				
				if (this._nocache === false)
					this._setCache(selector, $selector);
			}
			return $selector;
		},
		/**
		 * Make element height fit to availiable height
		 * @public 
		 * @param {string|JQueryObject} [selector=document] Selector or JQuery object
		 * @param {boolean} [forceUpdate=false] Ignore element cache and force update all element that specify class 'autoheight'
		 * @example
		 * Usage: add class 'autoheight' to element
		 * and method ST3Lib.sizing.autoHeight() will be call on window resize
		 * Ex. <div class="autoheight"></div>
		 * 
		 * Or. Call directly to force update element size
		 * ST3Lib.sizing.autoHeight();
		 * */
		autoHeight: function(selector, forceUpdate) {
			if (document.getElementsByClassName) //modern browser
				forceUpdate = true;
			
			selector = selector || '';
			var $selector = this._getNodeList(selector, forceUpdate);
			if ($selector == null) return;
			
			var dummy = $(document);
			var items = new Array();
			var change = false;
			var changeIndex = 0;
			for(var i = 0; i < $selector.length; ++i) {
				dummy[0] = $selector[i];
				if (dummy.hasClass('disabled-autoheight'))
					continue;
				if (forceUpdate || this.checkState(dummy)) {
					if (change === false) {
						changeIndex = i - 1;
						if (changeIndex < 0) changeIndex = 0;
						change = true;
					}
				}
				items.push($selector[i]);
			}
			if (change) {
				for(var i = changeIndex; i < items.length; ++i) {
					dummy[0] = items[i];
					dummy.addClass('sizing-overflow-hidden');
				}
				for(var i = changeIndex; i < items.length; ++i) {
					dummy[0] = items[i];
					//dummy.addClass('sizing-overflow-hidden');
					this.setHeight(dummy, forceUpdate);
					this.saveState(dummy);
				}
				for(var i = changeIndex; i < items.length; ++i) {
					dummy[0] = items[i];
					dummy.removeClass('sizing-overflow-hidden');
				}
				$(window).trigger('autoheight'); //trigger custom event
			}
		},
		/** @private */
		setHeight: function($obj, forceUpdate){
			if ($obj.length == 0 || $obj[0].nodeName in this._except) return;
			var height = this.getHeight($obj, forceUpdate);
			var max = parseInt($obj.attr('max-autoheight')) || 99999999;
			if (height > max) height = max;
			$obj.height( height );
		},
		/** @private */
		getHeight: function($obj, forceUpdate){
			var parseFloat = window.parseFloat; //prevent function lookup to global
			var parseInt = window.parseInt; //prevent function lookup to global
			if (window['isIE']) {
				parseFloat = parseInt; //prevent convert to floating point in IE6 that may be slow
			}
			
			var parent = $obj.parent();
			var strZIndex = $obj.css('z-index');
			var zIndex = parseInt(strZIndex) || 0;
			var height = 0, parentHeight = 0;
			
			if (parent[0].nodeName === 'BODY')
				parent = this.$document;
			if ( parent == null ) return;
			if (parent[0].nodeName !== '#document') { //Thanawut 2015/08/31
				if ( ! parent[0]._fixedPosition ) {
					parent[0]._fixedPosition = true;
					var cssPos = parent.css('position');
					if (cssPos !== 'relative' && cssPos !== 'absolute')
						parent.css( 'position', 'relative' );
					if (window['isIE6']) { //prevent invisible layout in IE6
						if (parent.css('zoom') != '1')
							parent.css('zoom', '1');
					}
				}
				
				parentHeight = parent.height();
			}else parentHeight = (this.$window.height() || parent.height() );
			
			var position = $obj.position();
			position.top = Math.ceil(position.top);
			position.left = Math.ceil(position.left);
		
			height = parentHeight - position.top;
			position.right = position.left + $obj.outerWidth();
			
			var minTop = 999999999;
			var maxBottom = 0;
			var nextAll = $obj.nextAll(':visible');
			var e = $(document); //dummy object
			for (var i = 0; i < nextAll.length; ++i) {
				e[0] = nextAll[i];
				if ( e[0].tagName === 'SCRIPT' || e[0].tagName === 'STYLE' ) 
					continue;
				if (e.outerHeight(true) === 0) continue;
				if (! e[0]._mpb || forceUpdate){
					var e_zIndex = e.css('z-index');
					e[0]._mpb = {
							'marginLeft': (parseFloat(e.css('margin-left')) || 0 ),
							'marginBottom': (parseFloat(e.css('margin-bottom')) || 0),
							'marginTop': parseFloat(e.css('margin-top')) || 0,
							'position': e.css('position'),
							'zIndex': e_zIndex === 'auto'?e_zIndex: (parseInt(e_zIndex) || 0),
							'diffHeight': $obj.outerHeight(true) - $obj.height()
					};
				}
				var e_mpb = e[0]._mpb;
				
				if ('absolute,fixed'.indexOf(e_mpb.position) >= 0) {
					//var e_zIndex = e_mpb.zIndex;
					//if (e_zIndex === 'auto' || e_zIndex > zIndex) continue;
					continue;
				}
				
				var otherObjPos = e.position();
				otherObjPos.top = Math.ceil(otherObjPos.top);
				otherObjPos.left = Math.ceil(otherObjPos.left);
			
				otherObjPos.left += e_mpb.marginLeft;
				otherObjPos.right = otherObjPos.left + e.outerWidth();
				otherObjPos.bottom = otherObjPos.top + e.outerHeight() + e_mpb.marginBottom;
				otherObjPos.top -= e_mpb.marginTop;
				if ( !(position.left > otherObjPos.right || position.right < otherObjPos.left)) {
					if (otherObjPos.top < minTop) minTop = otherObjPos.top;
					if (otherObjPos.bottom > maxBottom) maxBottom = otherObjPos.bottom;
				}
			}
			if (minTop != 999999999) height -= maxBottom - minTop;
			if ( ! $obj[0]._mpb || forceUpdate) {
				
				$obj[0]._mpb = {
						'marginLeft': (parseFloat($obj.css('margin-left')) || 0 ),
						'marginTop': (parseFloat($obj.css( 'margin-top' )) || 0),
						'marginBottom': (parseFloat($obj.css( 'margin-bottom' )) || 0),
						'borderTopWidth': (parseFloat($obj.css( 'border-top-width' )) || 0),
						'borderBottomWidth': (parseFloat($obj.css( 'border-bottom-width' )) || 0),
						'paddingTop': (parseFloat($obj.css( 'padding-top' )) || 0),
						'position': $obj.css('position'),
						'paddingBottom': (parseFloat($obj.css( 'padding-bottom' )) || 0),
						'zIndex': strZIndex === 'auto'?strZIndex:zIndex,
						'diffHeight': $obj.outerHeight(true) - $obj.height()
				};
			
			}
			var mpb = $obj[0]._mpb;
			/*
			height -= mpb.marginTop + mpb.marginBottom;
			height -= mpb.borderTopWidth + mpb.borderBottomWidth;
			height -= mpb.paddingTop + mpb.paddingBottom;
			*/
			height -= mpb.diffHeight;
			return height;
		},
		/** @private */
		reset: function(selector){
			if(selector[0]._reset) return;
			var m = ( parseFloat(selector.css('margin-left')) || 0 )
					+ ( parseFloat(selector.css('margin-top')) || 0 )
					+ ( parseFloat(selector.css('margin-right')) || 0 )
					+ ( parseFloat(selector.css('margin-bottom')) || 0 )
			
					+ ( parseFloat(selector.css('padding-left')) || 0 )
					+ ( parseFloat(selector.css('padding-top')) || 0 )
					+ ( parseFloat(selector.css('padding-right')) || 0 )
					+ ( parseFloat(selector.css('padding-bottom')) || 0);
			if (m) {
				selector.css({'padding': '0px', 'margin': '0px'});
				selector[0]._reset = true;
			}
			
		},
		/** @private */
		autoRelateWidth: function(selector, forceUpdate){
			selector = selector || '';
			var $selector = null;
			
			if (!forceUpdate && this._nocache === false) {
				if (typeof selector === 'string') {
					if ( !this._cache[selector] )
						this._cache[selector] = $(selector || document).find( '.autowidth' ).addBack( '.autowidth' );
					$selector = this._cache[selector];
				} else {
					$selector = $(document).find( '.autowidth' ).addBack( '.autowidth' );
				}
			}else
				$selector = $(document).find( '.autowidth' ).addBack( '.autowidth' );
			var e = $(document);
			for(var i = 0; i < $selector.length; ++i) {
				e[0] = $selector[i];
				this.relateWidth(e);
			}
		},
		/** @private */
		relateWidth: function($obj){
			var target = null;
			if (!$obj[0]._relateWidth) {
				target = $obj.attr('relatewidth');
				$obj[0]._relateWidth = $(target);
			}
			if ($obj[0]._relateWidth.length === 0) return;
			target = $obj[0]._relateWidth;
			$obj.outerWidth(target.outerWidth());
		}
		
		
	};
	
	/** @class */
	ST3Lib.content = {
		/** 
		 * Initial method. this method will be automatic execute when document ready
		 * @public */
		init: function(){
			this.initMoreButton();
		},
		/** @private */
		initMoreButton: function(){
			var btnPanel = $(ST3Lib.id.ID_BUTTON_PANEL);
			if ( btnPanel.size() > 0 ) { //contentPanel.size() > 0 && 
				var groupButton = btnPanel.find('div.button-group');
				if ( groupButton.size() === 0 ) return;
				
				var groupButtonParent = groupButton.parent();
				var moreButton = $('<div class="more-panel"/>').appendTo(btnPanel);
				groupButton.appendTo(moreButton);
		
				btnPanel.css({ 'background-color': 'white'});
				btnPanel.find('div.button_more')
					.css({'width':'100%'});
				
				var anchor = $('<a />');
					
				anchor.on('focus', function(){
								$('input,select,teatarea,button').filter(':tabbable:first').focus();
								$('div.button-group.ui-dialog-content').each(function(){
									$(this).dialog('close');
								});
						})
						.attr('href', '#')
						.attr('style', "display:inline-block;position:absolute;left:-100px;top:-100px;z-index:10000;")
						.html('&nbsp');
				
				groupButton.append(anchor);
				groupButton.find('input[type=button], input[type=submit], input[type=reset]')
					.css({'display':'block', 'width':'100%', 'margin-bottom':'10px'})
					.wrap('<div/>');
				groupButton.css('padding', '10px');
				
				var btnMore = $('<input type="button" id="more-button" value="' + _I18N_['lbl.More'] + '" class="nomore button generalBtn" style="width:80px;" />');
				var position = {
						my: 'right top', at:'right bottom', of: btnMore
						,collision: 'none'
					};
				btnMore.click(function(){  //center-78% bottom-15%
						groupButton.dialog('open'); 
						groupButton.dialog('widget').position(position);
						return false; 
					});
				
				//btnMore.find('input').button();
				groupButtonParent.append(btnMore);
				groupButton.click(function(){
						return false;
					});
				groupButton.dialog({resizable: false, modal: false, autoOpen:false,
					closeOnEscape:true, zIndex:10000,
					open: function(){
						ST3Lib.validate.disabledButton(true);
						ST3Lib.validate.disabledButton(groupButton, false);
					},
					close:function(){
						ST3Lib.validate.disabledButton(false);
					}
				});
				$(document).click(function(){
					groupButton.dialog('close');
				});
				$(window).on('autoheight', function(){
					groupButton.dialog('widget').position(position);
				});
			
			}
		},
		/** @private */
		resize: function(){
			$(window).resize();
		},
		/** 
		 * Disable/Enabled control within specify element
		 * @public 
		 * 
		 * @param {string|JQueryObject} selector Selector or JQuery object that contains html control
		 * @param {boolean} disabled True/False to enabled/disabled control
		 * */
		disabled: function(selector, disabled){
			selector = $(selector).find('input,textarea, select').addBack('input,textarea, select').prop('disabled', disabled);
			
			if ( disabled ) {
				selector.filter( 'input:not(.MandatoryField),select:not(.MandatoryField),textarea:not(.MandatoryField)' )
					.addClass( 'ajax-disabled' );
			}else{
				selector.filter( 'input:not(.MandatoryField),select:not(.MandatoryField),textarea:not(.MandatoryField)' )
					.removeClass( 'ajax-disabled' );
			}
		},
		/** @private */
		execLayoutState: function (layout_state, name){
			var state = layout_state[name];
			if (!state){
				alert('State "' + name + '" not found');
				return;
			}
			
			state.hide && $(state.hide.join(',')).addClass('hide');
			state.show && $(state.show.join(',')).removeClass('hide');
			state.disabled && ST3Lib.content.disabled(state.disabled.join(','), true);
			state.enabled && ST3Lib.content.disabled(state.enabled.join(','), false);
			state.focus && $(state.focus).focus();
			state.screenid && $('#right-area .screenid').html(state.screenid);
			ST3Lib.sizing.autoHeight(ST3Lib.id.ID_CONTENT_PANEL);
		}
		
	};
	
	/** @class */
	ST3Lib.dataTable = {
		_scrollBarWidth: 0,
		/*
		 * Hook Data Table 
		 */
		/** @private */
		initHookeDataTable: function(){
			this._scrollBarWidth = this.getScrollbarWidth();
			$.fn.__dataTable = $.fn.dataTable;
			$.fn.dataTable = function(options) {
				
				if (options) {
					if (this[0].style.width) {
						this.attr('table-width', this[0].style.width);
						this[0].style.width = '';
					} else this.attr('table-width', '');
					if (this[0].style.height) {
						this.attr('table-height', this[0].style.height); //support only px
						this[0].style.height = '';
					} else this.attr('table-height', '');
					
					if (window['isIE6']) { //fix bug in IE6, checkbox not checked when change page
						this.on('change click', 'input:checkbox', function(){
							this._checked = $(this).prop('checked');
						});
						this.on('change click', 'input:radio', function(){
							this._checked = true;
						});
					}
					this.on('change', 'input:radio', function(e){
						var nNodes = $(e.delegateTarget).dataTable().fnGetNodes( );
						var dummy = $(this);
						for(var i = 0; i < nNodes.length; ++i) {
							dummy[0] = nNodes[i];
							var radio = dummy.find('input:radio');
							if (radio.length && radio[0] !== this) {
								radio[0]._checked = false;
								radio.prop('checked', false);
							}
						}
					});
					if (options) {
						options.oLanguage = { sSearch: _I18N_['ST3.lbl.Search']
							, sEmptyTable : _I18N_['ST3.lbl.EmptyTable']
							, oPaginate: {sFirst:'', sLast: '', sNext: '', sPrevious: ''  } 
							, sLengthMenu: _I18N_['ST3.lbl.Show']+" _MENU_ "+_I18N_['ST3.lbl.Entries']
						};
						var $window = $(window);
						options.fnInitComplete = function(){
							var parent = this.parent();
							this._parent = parent;
							parent.find('div.dataTables_paginate a').removeAttr('tabindex');
							
							parent.addClass('overflow-hidden autoheight').css('width', '100%');
							if ( this.attr('table-height')) // if not specify custom height
								parent.attr('max-autoheight', this.attr('table-height'));
							
							var wraper = $('<div class="dataTable-wrapper autoheight overflow" tabindex="-1" style="position:relative;width:100%;"/>');
							this.wrap(wraper);
							$('<div class="clear"/>').insertBefore(this.parent());
							
							var div = $('<div class="clear"/>');
							div.insertBefore(parent.find('div.dataTables_info'));
							
							
							this.css('width', this.attr('table-width') || '100%');
							var ths = this.children('thead').find('th');
							for (var n = 0; n < ths.length; ++n) {
								ths[n].style.width = '';
							}
							
							//ST3Lib.sizing.refreshCache();
							ST3Lib.sizing.refreshCache('#content-panel');
						};
						
						options.fnInfoCallback = function(oSettings, iStart, iEnd, iMax, iTotal, sPre){
							if (iMax && iStart > iMax){
								var max = iMax - 1;
								iStart = (max - (max % oSettings._iDisplayLength));
								iEnd = iMax;
								var self = this;
								setTimeout(function(){
									self.fnPageChange('last');
								}, 1);
								//return;
								oSettings._iDisplayStart = iStart;
							}
							var bServerSide = oSettings.oFeatures.bServerSide;
							var rowNumTDIndex = this.children('thead').find('th.rownum:first').index();
							for ( var i = iStart - 1; i < iEnd; ++i )
							{
								if(iMax <= i) break;
								var index = -1;
								if (bServerSide) {
									index = oSettings.aiDisplay[i - iStart + 1];
								}else{
									index = oSettings.aiDisplay[i];
								}
								if (!oSettings.aoData[index]) continue;
								var tr = oSettings.aoData[index].nTr;
								var td = tr.childNodes[rowNumTDIndex];
								if (td){
									td.innerHTML = i+1;
									oSettings.aoData[index]._aData[rowNumTDIndex] = i+1;
								}
							}
							
							if (window['isIE6']) {
								ST3Lib.dataTable._setPadding(this.parent());
							}
							
							var MAX_ROW_PER_PAGE = oSettings._iDisplayLength;
							if ( iMax === 0 ) iStart = 0;
							var page = Math.ceil(iStart / MAX_ROW_PER_PAGE);
							var maxpage = Math.ceil(iMax / MAX_ROW_PER_PAGE);
							var str = _I18N_['lbl.Pagination.PageInfo'] || 'Page {PAGE} of {MAX}';
							return str.replace('{PAGE}', page).replace('{MAX}', maxpage);
						};
						options.fnHeaderCallback = function(nHead, aData, iStart, iEnd, aiDisplay) {
							if (this.hasClass('fixedcolumn')) {
								$window.trigger('fixedcolumn', [this]);
							}
							if (this.hasClass('fixedheader')) {
								var $nHead = $(nHead);
								var ths = $nHead.find('th');
								var hcount = ths.length;
								if (nHead._lastHeaderCount != null && nHead._lastHeaderCount !== hcount) {
									this.css('width', this.attr('table-width') || '100%');
									for (var n = 0; n < ths.length; ++n) {
										ths[n].style.width = '';
										var th = $(ths[n]);
										if (th.find('div.fixed-wrapper').length === 0)
											th.wrapInner('<div class="fixed-wrapper" style="width:100%;"/>');
									}
									ST3Lib.dataTable.initFixedTableHeader(this);
								}
								nHead._lastHeaderCount = hcount;
								$window.trigger('fixedheader', [this, true]);
								
							}
							
							
							
							
						};
						
						options.fnDrawCallback = function(oSettings ){
							if (window['isIE6']) { //fix bug in IE6, checkbox not checked when change page
								this.find('input:checkbox, input:radio').each(function(){
									var $this = $(this);
									if (!!this._checked != $this.prop('checked'))
										$(this).prop('checked', this._checked);
								});
							}
							
							$window.trigger('fixedcolumn', [this, true]);
							$window.trigger('fixedheader', [this, true]);
						};//options.fnDrawCallback
					}
					var st = this.__dataTable(options).fnSettings();
					return st;
				}else
					return this.__dataTable(options);
			};
		},
		_setPadding: function ($ctrl){
			var table = $ctrl.children('table');
			if (table.attr('table-width')) return;
			var scrollWidth = ST3Lib.dataTable._scrollBarWidth;
			if (table.height() > $ctrl.height()) {
				table.css('width', $ctrl.width() - scrollWidth);
			}else table.css('width', '100%');
		},
		/** 
		 * Move pagination to the page that contain specify ID
		 * @public 
		 * 
		 * @param {string|JQueryObject} tableSelector Selector or JQuery object of Datatable
		 * @param {string} id Unique ID of input control(radio or checkbox)
		 * 
		 * */
		moveToLastSelectId: function (tableSelector, id){
			if (!id) return false;
			var objIds = {};
			if (typeof id === 'string') {
				objIds[id] = true;
			}else objIds = id;
			
			var self = this;
			var nNodes = $( tableSelector ).dataTable().fnGetNodes( );
			var aiDisplay = $( tableSelector ).dataTable().fnSettings().aiDisplay;
			var success = false;
			var dummy = $(document);
			var isRadio = null;
			for(var i = 0; i < nNodes.length; ++i) {
				dummy[0] = nNodes[i];
				var radio = dummy.find('input:radio:first, input:checkbox:first');
				if (radio.length === 0) continue;
				if (isRadio === null)
					isRadio = radio.is(':radio');
				if (radio.val() in objIds) {
					//map to sort data
					for(var displayIndex = 0; displayIndex < aiDisplay.length; ++displayIndex) {
						var dataIndex = aiDisplay[displayIndex];
						if (dataIndex === i) {
							self.setCurrentPage(tableSelector, displayIndex);
							radio.click();
							if (radio.is(':not(:checked)'))
								radio.click();
							success = true;
							if (isRadio)
								break;
						}	
					}
					if (isRadio)
						break;
				}
			}
			return success;
		},
		/** 
		 * Get next row id
		 * @public
		 * 
		 * @param {string|JQueryObject} tableSelector Selector or JQuery object of Datatable
		 * @param {string} id Unique ID of input control(radio or checkbox)
		 * @param {number} [step=1] internal use only
		 * @param {function} [fnMatch] internal use only
		 *  */
		getNextSelectId: function (tableSelector, id, step, fnMatch){
			step = step || 1;
			var nNodes = $( tableSelector ).dataTable().fnGetNodes( );
			var selectId = null;
			for(var i = 0; i < nNodes.length; ++i) {
				var e = nNodes[i];
				var radio = $(e).find('input:radio:first, input:checkbox:first');
				var match = false;
				if (fnMatch) match = fnMatch(radio, id);
				else match = radio.val() === id;
				if (match) {
					//map to sort data
					var aiDisplay = $( tableSelector ).dataTable().fnSettings().aiDisplay;
					var selectDisplayIndex = 0;
					for(var displayIndex = 0; displayIndex < aiDisplay.length; ++displayIndex) {
						var dataIndex = aiDisplay[displayIndex];
						if (dataIndex === i) {
							selectDisplayIndex = displayIndex;
							break;
						}	
					}
					var selectDataIndex = -1;
					var found = false;
					var nextStep = step;
					while(selectDisplayIndex + nextStep < aiDisplay.length){
						selectDataIndex = aiDisplay[selectDisplayIndex + nextStep];
						if ( typeof selectDataIndex !== 'undefined' 
							&& $(nNodes[selectDataIndex]).find('input:radio:first:not(:checked), input:checkbox:first:not(:checked)').length > 0) {
							found = true;
							break;
						}	
						nextStep++;
					}
					if (found === false) {
						nextStep = -step;
						while(selectDisplayIndex + nextStep >= 0){
							selectDataIndex = aiDisplay[selectDisplayIndex + nextStep];
							if ( typeof selectDataIndex !== 'undefined' 
								&& $(nNodes[selectDataIndex]).find('input:radio:first:not(:checked), input:checkbox:first:not(:checked)').length > 0) {
								found = true;
								break;
							}	
							nextStep--;
						}
					}
					if (found)
						selectId = $(nNodes[selectDataIndex]).find('input:radio:first, input:checkbox:first').val();
					else selectId = null;
					break;
				}
			}
			return selectId;
		},
		/** 
		 * Get table row element of the specify ID
		 * @public
		 * 
		 * @param {string|JQueryObject} tableSelector Selector or JQuery object of Datatable
		 * @param {string} id Unique ID of input control(radio or checkbox)
		 * @param {function} [fnMatch] callback function for test matching string
		 *  
		 *  */
		getRowOfSelectId: function (tableSelector, id, fnMatch){
			var nNodes = $( tableSelector ).dataTable().fnGetNodes( );
			for(var i = 0; i < nNodes.length; ++i) {
				var e = nNodes[i];
				var radio = $(e).find('input:radio:first, input:checkbox:first');
				var match = false;
				if (fnMatch) match = fnMatch(radio, id);
				else match = radio.val() === id;
				if (match) {
					return e;
				}
			}
			return null;
		},
		/** 
		 * Get row ID by specify index
		 * @public 
		 * @param {string|JQueryObject} tableSelector Selector or JQuery object of Datatable
		 * @param {number} index Row index
		 *  */
		getSelectId: function (tableSelector, index){
			var nNodes = $( tableSelector ).dataTable().fnGetNodes( );
			var aiDisplay = $( tableSelector ).dataTable().fnSettings().aiDisplay;
			var selectDataIndex = aiDisplay[index];
			if( typeof selectDataIndex === 'undefined')
				selectDataIndex = 0;
			var selectId = $(nNodes[selectDataIndex]).find('input:radio:first, input:checkbox:first').val();
			return selectId;
		},
		/** 
		 * Get current page number
		 * @public
		 * @param {string|JQueryObject} tableSelector Selector or JQuery object of Datatable
		 *  */
		getCurrentPage: function (tableSelector){
			var oDataTable = $(tableSelector).dataTable();
			var oSettings = oDataTable.fnSettings();
			return oSettings._iDisplayStart;
		},
		/** 
		 * Set current page number
		 * @public 
		 * @param {string|JQueryObject} tableSelector Selector or JQuery object of Datatable
		 * @param {number} start Row index
		 * */
		setCurrentPage: function (tableSelector, start){
			var oDataTable = $(tableSelector).dataTable();
			var oSettings = oDataTable.fnSettings();
			if (oSettings._iDisplayStart == start)
				return false;
			var bServerSide = oSettings.oFeatures.bServerSide;
			
			if (bServerSide === false) {
				if (start > oSettings.aoData.length)
					start = Math.floor(oSettings._iRecordsTotal / oSettings._iDisplayLength) * oSettings._iDisplayLength;
				start = Math.floor(start / oSettings._iDisplayLength) * oSettings._iDisplayLength;
			}
			oSettings._iDisplayStart = start;
			oDataTable._fnCalculateEnd( oSettings );
			oDataTable._fnDraw( oSettings );
			return true;
		},
		_getWidth: function($obj){
			if (window.getComputedStyle) {
				var compute = window.getComputedStyle($obj[0]);
				var w = parseFloat(compute.width) || 0;
				var pl = parseFloat(compute['padding-left']) || 0;
				var pr = parseFloat(compute['padding-right']) || 0;
				var bl = parseFloat(compute['border-left-width']) || 0;
				var br = parseFloat(compute['border-right-width']) || 0;
				return w - (pl + pr + bl + br);
			}else return $obj.width();
		},
		_getHeight: function($obj){
			if (window.getComputedStyle) {
				var compute = window.getComputedStyle($obj[0]);
				var w = parseFloat(compute.height) || 0;
				var pt = parseFloat(compute['padding-top']) || 0;
				var pb = parseFloat(compute['padding-bottom']) || 0;
				var bt = parseFloat(compute['border-top-width']) || 0;
				var bb = parseFloat(compute['border-bottom-width']) || 0;
				return w - (pt + pb + bt + bb);
			} else return $obj.height();
		},
		_scrollbarWidth: -1,
		getScrollbarWidth: function () {
		    if (this._scrollbarWidth !== -1)
		    	return this._scrollbarWidth;
			var outer = document.createElement("div");
		    outer.style.position = 'absolute';
		    outer.style.visibility = "hidden";
		    outer.style.width = "100px";
		    document.body.appendChild(outer);

		    var widthNoScroll = outer.offsetWidth;
		    // force scrollbars
		    outer.style.overflow = "scroll";

		    // add innerdiv
		    var inner = document.createElement("div");
		    //inner.style.width = "100%";
		    outer.appendChild(inner);        

		    var widthWithScroll = inner.offsetWidth;

		    // remove divs
		    outer.parentNode.removeChild(outer);

		    return this,_scrollbarWidth = widthNoScroll - widthWithScroll;
		},
		initFixedTableHeader: function(selector){
			var tables = null;
			if (selector)
				tables = $(selector);
			else
				tables = $( 'table.fixedheader' );
			
			tables.closest('div.overflow')
			.each(function(){
					var tables = $(this).children('table.fixedheader');
					var parent = $(this);
					tables.each(function(i, e){
						var table = $(e);
						var thead = null;
						var cloneid = table.attr('cloneid');
						
						if (!cloneid) {
							cloneid = "hid-" + parseInt(Math.random() * 100000);
							var parentId = parent.attr('id');
							if (!parentId) {
								parentId = "pid-" + parseInt(Math.random() * 100000);
								parent.attr('id', parentId);
							}
							if (!table[0]._wrapDiv) {
								table.find('th').wrapInner('<div class="fixed-wrapper" style="width:100%;"/>');
								table[0]._wrapDiv = true;
							}
							table.attr('cloneid', cloneid);
							table.attr('parentid', parentId);
							
							thead = table.clone().css('width', '100%');
							thead.addClass('fixed-header');
							thead.children('tfoot, tbody').remove();
							thead.removeAttr('aria-describedby');
							thead.removeClass('fixedheader');
							thead.removeClass('fixedcolumn');
							thead.attr('id', cloneid);
							
							thead = thead.wrap($('<div class="table-wrap" style="position:relative"/>')).parent(); //wrap div
							thead = thead.wrap($('<div class="width-wrap"/>')).parent(); //wrap div for limit visibility width
							thead.css({'display': 'none', position:'absolute', 'overflow': 'hidden', 'zoom':'1'});
							parent.parent().append(thead);
							thead = thead.children('div').css('position', 'relative');
							//$('<div/>').insertBefore(table.parent()).height(thead.height());
							
							
							if ( 'relative,absolute'.indexOf(parent.attr('position')) < 0 )
								parent.attr('position', 'relative');
							
							table.data('thead', thead);
							table.data('parent', parent);
							thead.data('table', table);
							
							(function (table, thead, self){
								var fnEvent = function(e){
									
									if (e.data !== 'thead' && e.data !== 'table') return;
									var index = $(this).index();
									var path = [];
									var item = $(e.currentTarget);
									var parent = item.parent();
									path.push(item[0].nodeName + ':eq(' + item.index() + ')');
									while(parent && parent.length && parent[0].nodeName !== 'TABLE') {
										if (parent[0].nodeName === 'TH') isTH = true;
										if(parent[0].nodeName === 'TBODY' || parent[0].nodeName === 'THEAD')
											path.splice(0, -1, parent[0].nodeName);
										else path.splice(0, -1, parent[0].nodeName + ':eq(' + parent.index() + ')');
										parent = parent.parent();
									}
									var strPath = path.join(' > ');
									var target = null;
									
									if (e.data === 'table') {
										target = thead.find('table').find(strPath);
									}else{
										target = table.find(strPath);
									}
									
									ST3Lib.dataTable._passEvent(e, item, target);
								};
								//thead.find('*').off();
								
								thead.children('table').on('click focus change scroll', '*', 'thead', fnEvent);
								table.on('click focus change scroll', '*', 'table', fnEvent);
							})(table, thead, self);
						}else{
							var thead = table.data('thead');
							thead.find('thead').replaceWith(table.find('thead').clone());
							setTimeout(function(){
								table.parent().trigger('scroll');
							}, 1);
						}
						
					});
					
					
			});
			return tables;
		},
		fixedTableHeader: function(){
			var tables = this.initFixedTableHeader();
			var self = this;
			function setSize(table, thead, parent, resize){
				var left = -parent.scrollLeft();
				var top = parent.scrollTop();
				//var tableTop = table.position().top;
				
				if ( top > 0  || thead._showHeader ) {
					thead._showHeader = true;
					if( !thead._parent) thead._parent = thead.parent();
				
					var parentOffset = parent.position();
					thead._parent.css({left: parentOffset.left, top: parentOffset.top});
					
					if (typeof thead._isHide === 'undefined' || thead._isHide === true) {
						thead._parent.show();
						thead._isHide = false;
					}
					
						
					var tableThs = table.find('thead').find('div');
					var theadThs = thead.find('thead').find('div');
					var size = 0;
					for(var i = 0; i < tableThs.length; ++i) {
						var th = $(theadThs[i]);
						tableThs[i] = $(tableThs[i]);
						var tableTHW = tableThs[i].width();
						if (th.width() !== tableTHW)
							th.width(tableTHW).parent().width('width', tableTHW);
					}
					
					size = parent.width() - self._scrollBarWidth;
					if (size !== thead._parent.width())
						thead._parent.width(size);
					size = table.width();
					if (size !== thead.width())
						thead.width(size);
					
					
					if (thead._lastLeft !== left)
						thead[0].style.left = left + 'px';
					thead._lastLeft = left;
					
					
				}else{
					//if (thead.is(':visible'))
					//	thead.parent().hide();
				}
			}
			
			tables.closest('div.overflow').off('scroll.fixedheader')
			.on('scroll.fixedheader', function(){
					if (!this._tablesFixheader) this._tablesFixheader = $(this).children('table.fixedheader');
					var parent = $(this);
					this._tablesFixheader.each(function(i, e){
						var table = $(e);
						var thead = null;
						thead = table.data('thead');
						setSize(table, thead, parent);
					});
			}).scroll();
			
			
			$(window).off('autoheight.fixedheader fixedheader.fixedheader')
				.on('autoheight.fixedheader fixedheader.fixedheader',function(e, obj, forceChange){
				var tb = tables;
				if (obj) 
					tb = obj;
				else if (typeof obj === 'boolean')
					forceChange = obj;
				var type = e.type;
				tb.each(function(i, e){
					var table = $(e);
					var cloneid = table.attr('cloneid');
					var parentid = table.attr('parentid');
					if (!cloneid || !parentid) return;
					var thead = table.data('thead');
					var parent = table.data('parent');
					if (parent && thead && thead.is(':visible')) {
						setSize(table, thead, parent, type === 'autoheight' || forceChange);
					}
				});
			});
		},
		/** @public */
		initFixedTableColumn: function(selector){
			var tables = null;
			if (selector)
				tables = $(selector);
			else
				tables = $( 'table.fixedcolumn' );
			var self = this;
			tables.each(function(){
				var table = $(this);
				var parent = table.parent();
				 
				var thfixed = table.children('thead').find('th.fixed:first');
				var fixedIndex = thfixed.index();
				var columncloneid = table.attr('columncloneid');
				
				if (!columncloneid) {
					columncloneid = "hid-" + parseInt(Math.random() * 100000);
					var parentId = parent.attr('id');
					if (!parentId) {
						parentId = "pid-" + parseInt(Math.random() * 100000);
						parent.attr('id', parentId);
					}
					if (!table[0]._wrapDiv) {
						table.children('thead').find('th').wrapInner('<div class="fixed-wrapper" style="width:100%;"/>');
					}
					table.attr('columncloneid', columncloneid);
					table.attr('parentid', parentId);
					
					thead = table.clone();
					thead.addClass('fixed-header fixed-column');
					thead.children('tbody').empty();
					thead.attr('fixedindex', fixedIndex + 1);
					thead.removeAttr('aria-describedby');
					thead.removeClass('fixedheader');
					thead.removeClass('fixedcolumn');
					thead.attr('id', columncloneid);
					thead._scrollX = 0;
					thead._scrollY = 0;
					//thead.children('thead').hide();
					if (window['isIE6']) {
						thead.css('border-top', '0px solid white');
						thead.css('border-bottom', '0px solid white');
					}
					//thead.find('th:gt(' + fixedIndex + '),td:gt(' + fixedIndex + ')').remove();
					//thead.css('border-right', '1px solid white');
					
					var thead2 = table.clone();
					thead2.addClass('fixed-header fixed-column');
					thead2.children('tfoot').remove();
					//thead2.children('tbody').empty();
					thead2.attr('fixedindex', fixedIndex + 1);
					thead2.removeAttr('aria-describedby');
					thead2.removeClass('fixedheader');
					thead2.removeClass('fixedcolumn');
					thead2.attr('id', columncloneid + '_2');
					thead2.css('position', 'relative');
					thead2.css('border-bottom', '0px solid white');
					if (window['isIE6']) {
						thead2.find('th').css('border-bottom', '0px solid white');
					}
					
					thead = thead.wrap($('<div class="table-column-wrap"/>')).parent(); //wrap div
					thead2 = thead2.wrap($('<div class="table-column-wrap"/>')).parent(); //wrap div
					//thead.prepend(thead2);
					
					//thead.css('border-right', '1px solid #D3D6FF');
					//thead2.css('border-right', '1px solid #D3D6FF');
					thead.css({'display': 'none', 'position':'absolute', 'overflow': 'hidden'});
					thead2.css({'display': 'none', 'position':'absolute', 'overflow': 'hidden'});
					thead.wrapInner('<div class="inner-wrap" style="position:relative;height:100%;" />');
					thead2.wrapInner('<div class="inner-wrap" style="position:relative;height:100%;" />');
					parent.append(thead);
					parent.parent().append(thead2);
					//parent.parent().append(thead);
					
					if ( 'relative,absolute'.indexOf(parent.attr('position')) < 0 )
						parent.attr('position', 'relative');
					
					thead._thead2 = thead2;
					
					table.data('columnthead', thead);
					table.data('parent', parent);
					thead.data('table', table);
					
					
					thead.on('scroll', function(){
						table.parent().scrollTop(thead.scrollTop());
					});
					
					(function (table, thead, thead2, self, fixedIndex){
						var fnEvent = function(e){
							if (e.type === 'keydown' && e.keyCode !== 9)
								return;
							if (e.data !== 'thead' && e.data !== 'table') return;
							var index = $(this).index();
							var path = [];
							var item = $(e.currentTarget);
							var parent = item.parent();
							
							if (e.target === e.currentTarget) {
									if ( ( (item[0].nodeName === 'TD' || item[0].nodeName === 'TH') && item.index() > fixedIndex)
										|| item.closest('TD, TH').index() > fixedIndex)
										return;
							}
							path.push(item[0].nodeName + ':eq(' + item.index() + ')');
							while(parent && parent.length && parent[0].nodeName !== 'TABLE') {
								if (parent[0].nodeName === 'TH') isTH = true;
								if(parent[0].nodeName === 'TBODY' || parent[0].nodeName === 'THEAD')
									path.splice(0, -1, parent[0].nodeName);
								else path.splice(0, -1, parent[0].nodeName + ':eq(' + parent.index() + ')');
								parent = parent.parent();
							}
							var strPath = path.join(' > ');
							var target = null;
							
							if (e.data === 'table') {
								target = thead.find('table').find(strPath);
							}else{
								target = table.find(strPath);
							}
							
							ST3Lib.dataTable._passEvent(e, item, target);
						};
						//thead.find('*').off();
						//thead2.find('*').off();
						
						thead.children('div').children('table').on('click focus change scroll keydown', 'a, input, button, textarea', 'thead', fnEvent);
						thead2.children('div').children('table').on('click focus change scroll', 'a, input, button, textarea', 'thead', fnEvent);
						table.on('click focus change scroll', 'a, input, button, textarea', 'table', fnEvent);
						self._generateFixedColumnContent(table);
					})(table, thead, thead2, self, fixedIndex);
					
				}
				
			});
			return tables;
		},
		_trigger: function($selector, type){
			$selector.trigger(type);
		},
		_passTab: function(item, target, type, shiftKey){
			if (type === 'table') {
				if (target.length === 0) return;
				if (target.is(':enabled')) {
					item.blur();
					setTimeout(function(){
						target.focus();
					}, 1); //fixed for IE cause by IE trigger other event and target lost focus
					
				}else{
					var tabbable = $('input,select,teatarea,button,a').filter(':tabbable');
					var currentIndex = tabbable.index(item);
					if (currentIndex >= 0) {
						currentIndex++;
						if (currentIndex >= tabbable.length )
							currentIndex = 0;
						$(tabbable[currentIndex]).focus();
					}
				}
			}else if (type === 'thead') {
				var inc = shiftKey?-1:1;
				var table = target.closest('table');
				if (table.length == 0)
					return;
				var tabbable = table.find('input,select,teatarea,button,a').filter(':tabbable');
				var currentIndex = -1;
				if (tabbable.length == 0 || (currentIndex = tabbable.index(target)) >= tabbable.length) {
					tabbable = $('input,select,teatarea,button').filter(':tabbable');
					currentIndex = tabbable.index(target);
				}
				
				if (target[0].nodeName === 'INPUT' && target.attr('type') === 'radio') {
					if (currentIndex >= 0) {
						for(var retry = 0; retry < 2; ++retry) {
							var dummy = $(document);
							var targetName = target.attr('name');
							var found = false;
							for(var i = currentIndex + inc; i < tabbable.length; ++i) {
								if (i < 0) break;
								dummy[0] = tabbable[i];
								if (dummy[0].nodeName === 'INPUT' && dummy.attr('type') === 'radio' 
									&& dummy.attr('name') === targetName) {
									currentIndex += inc;
									continue;
								}else{
									found = true;
									break;
								}
							}
							if (!found) {
								tabbable = $('input,select,teatarea,button,a').filter(':tabbable');
								currentIndex = tabbable.index(target);
							}
						}
					}
				}
				if (currentIndex >= 0) {
					currentIndex += inc;
					if (currentIndex >= tabbable.length || currentIndex <= 0){
						if (tabbable.context.nodeName === 'TABLE') {
							tabbable = $('input,select,teatarea,button,a').filter(':tabbable');
							currentIndex = tabbable.index(target);
							currentIndex += inc;
							if (currentIndex >= tabbable.length || currentIndex === 0)
								currentIndex = 0;
						}else
							currentIndex = 0;
					}
					$(tabbable[currentIndex]).focus();
				}
			}
		},
		_passEvent: function(e, item, target){
			if (e.currentTarget && e.currentTarget._working) {
				if (e.type === 'focusin' || e.type === 'focus') {
					if (e.data === 'table') {
						this._passTab(item, target, e.data, e.shiftKey);
					}
				}
				return;
			}else if (e.target !== e.currentTarget) {
				this._trigger(target, e.type);
				return;
			}else if (target == null || target.length === 0) return;
			
			target[0]._working = true;
			item[0]._working = true;
			
			if (item[0].nodeName === 'INPUT') {
				var inputType = item.attr('type').toLowerCase();
				if (inputType === 'checkbox') {
					if (e.type === 'click') {
						if (target.prop('checked') === item.prop('checked')) //toggle
							target.prop('checked', !item.prop('checked'));
						this._trigger(target, e.type);
						if (e.data === 'thead') {
							item.focus();
						}
					}else if (e.type === 'change') {
						this._trigger(target, e.type);
						if (target.prop('checked') !== item.prop('checked')) {
							this._trigger(target, 'click');
						}
						if (e.data === 'thead')
							item.focus();
					}else if (e.type === 'focusin' || e.type === 'focus') {
						if (e.data === 'table') {
							target.prop('checked', item.prop('checked'));
							this._passTab(item, target, e.data, e.shiftKey);
						}
					}else if (e.type === 'keydown') {
						if (e.data === 'thead') {
							this._passTab(item, target, e.data, e.shiftKey);
							e.preventDefault();
						}
					}
				}else if (inputType === 'radio') {
					if (e.type === 'click') {
						target.hide(); //prevent page scroll in IE
						this._trigger(target, e.type);
						target.prop('checked', true);
						target.show();
						if (e.data === 'thead')
							item.focus();
					}else if (e.type === 'change') {
						this._trigger(target, e.type);
						target.prop('checked', true);
						if (e.data === 'thead')
							item.focus();
					}else if (e.type === 'focusin' || e.type === 'focus') {
						if (e.data === 'table') {
							target.prop('checked', item.prop('checked'));
							this._passTab(item, target, e.data, e.shiftKey);
						}
					}else if (e.type === 'keydown') {
						if (e.data === 'thead') {
							this._passTab(item, target, e.data, e.shiftKey);
							e.preventDefault();
						}
					}
				}else{
					if (e.type === 'focusin' || e.type === 'focus') {
						if (e.data === 'table') {
							this._passTab(item, target, e.data, e.shiftKey);
						}
					}else{
						this._trigger(target, e.type);
					}
				}
			}else if (item[0].nodeName === 'A') {
				if (e.type === 'click') {
					if (e.data === 'thead') {
						e.preventDefault();
						this._trigger(target, e.type);
					}
				}else if (e.type === 'focusin' || e.type === 'focus') {
					if (e.data === 'table') {
						this._passTab(item, target, e.data, e.shiftKey);
					}
				}else{
					this._trigger(target, e.type);
				}
			}else{
				this._trigger(target, e.type);
			}
			target[0]._working = false;
			item[0]._working = false;
		
		
		},
		/** @private */
		fixedTableColumn: function(){
			var tables = this.initFixedTableColumn();
			var self = this;
			function setSize(table, thead, parent, resize){
				if (table.height() <= 0) return;
				var top = parent.scrollTop();
				var left = parent.scrollLeft();
				var leftChange = false, topChange = false;
				if(thead._parent == null) thead._parent = thead.parent();
				
				if (thead._scrollY !== top) {
					topChange = true;
					thead._scrollY = top;
					thead._currentChange = 'top';
				}
				if (thead._scrollX !== left) {
					leftChange = true;
					thead._scrollX = left;
					thead._currentChange = 'left';
				}
				
				if (topChange === false && leftChange === false) {
					if (thead._lastChange === 'top') topChange = true;
					else leftChange = true;
				}
				
				
				if (resize || thead._currentChange !== thead._lastChange) {
					var parentOffset = 0;
					if (topChange || (resize && thead._lastChange === 'top' )) {
						thead.show();
						
						if (thead.parent()[0] !== parent[0]) {
							thead.appendTo(parent);
						}
						parentOffset = table.position();
						thead.css({left: left, top: parentOffset.top + top});
						thead.css('height', 'auto');
						
					}else if (leftChange || (resize && thead._lastChange === 'left' )){
						thead.show();
						
						if (thead.parent()[0] !== parent.parent()[0]) {
							thead.insertBefore(thead._thead2);
						}
						parentOffset = parent.position();
						thead.css({left: parentOffset.left, top: parentOffset.top});
						thead.css('height', Math.min(table.parent().outerHeight() - self._scrollBarWidth, table.outerHeight() + 2));
						
					}
					parentOffset = parent.position();
					
					thead._thead2.css({left: parentOffset.left, top: parentOffset.top});
					
					var tableTemp = thead.children('div').children('table');
					var tableTemp2 = thead._thead2.children('div').children('table');
					tableTemp[0].style.width = '100%';
					tableTemp2[0].style.width = '100%';
					thead._thead2.show();
				
					//////////////
					var fixedindex = parseInt(thead.find('table').attr('fixedindex'));
					var theadThs = thead.find('thead').find('th').find('div');
					var theadThs2 = thead._thead2.find('thead').find('th').find('div');
					var tableThs = table.find('thead').find('th:lt(' + theadThs.length + ')').find('div');
					
					var sumWidth = 0;
					var dummyTh = $(document);
					var dummyTableTh = $(document);
					for(var i = 0; i < fixedindex; ++i) {
						dummyTh[0] = theadThs[i];
						dummyTableTh[0] = tableThs[i];
						var tableTHW = self._getWidth(dummyTableTh);
						dummyTh.width(tableTHW);
						dummyTh[0] = theadThs2[i];
						dummyTh.width(tableTHW);
						
						
						
					}
					theadThs = thead.find('thead').find('th');
					for(var i = 0; i < fixedindex; ++i) {
						dummyTh[0] = theadThs[i];
						sumWidth += dummyTh.outerWidth();
					}
					if (window['isIE6']) {
						thead.width(sumWidth + 2);
						thead._thead2.width(sumWidth + 2);
					}else{
						thead.width(sumWidth + 1);
						thead._thead2.width(sumWidth + 1);
					}
					///////////
					
					
				}
				
				
				if (topChange || (resize && thead._lastChange === 'top' )) {
					thead.css('height', 'auto');
				}else if (leftChange || (resize && thead._lastChange === 'left' )){
					thead.css('height', Math.min(table.parent().outerHeight() - self._scrollBarWidth, table.outerHeight() + 2));
				}
				
				if (!thead._childDiv) thead._childDiv = thead.children('div');
				if (topChange || (resize && thead._lastChange === 'top')) {
					thead._childDiv[0].style.top = '0px';
				}else if (leftChange || (resize && thead._lastChange === 'top' )){
					thead._childDiv[0].style.top = -top + 'px';
				}
				
				if (!thead._thead2._table) thead._thead2._table = thead._thead2.children('div').children('table');
				if (!thead._thead2)
					thead._thead2[0].style.top = top + 'px';
					//thead._table0.css('z-index', 1000);
				thead._lastChange = thead._currentChange;
				
				
			}

			tables.closest('div.overflow').off('scroll.fixedcolumn')
			.on('scroll.fixedcolumn', function(){
					var tables = $(this).find('table.fixedcolumn');
					var parent = $(this);
					tables.each(function(i, e){
						var table = $(e);
						var thead = null;
						thead = table.data('columnthead');
						setSize(table, thead, parent);
					});
			}).scroll();
			
			
			$(window).off('autoheight.fixedcolumn fixedcolumn.fixedcolumn')
			.on('autoheight.fixedcolumn fixedcolumn.fixedcolumn',function(e, obj, forceChange){
				var tb = tables;
				if (obj)
					tb = obj;
				tb.each(function(i, e){
					var table = $(e);
					self._generateFixedColumnContent(table);
					
					var cloneid = table.attr('columncloneid');
					var parentid = table.attr('parentid');
					if (!cloneid || !parentid) return;
					var thead = table.data('columnthead');
					var parent = table.data('parent');
					if (parent) {
						setSize(table, thead, parent, e.type === 'autoheight' || forceChange);
					}
				});
			});
		},
		_generateFixedColumnContent: function(table){
			if (table.dataTable === null) return;
			var thead = table.data('columnthead');
			if (thead == null) return;
			
			
			thead.find('tbody').remove();
			fixedTh = thead.find('th');
			var fixedIndex = thead.find('th.fixed').index();
			
			thead.children('div').width(table.outerWidth());
			var cloneBody = table.find('tbody').clone();
			cloneBody.find('tr').find('td:gt(' + fixedIndex + ')')
				.find('a, input, select, textarea, button')
				.prop('disabled', true)
				.prop('tabindex', -1)
				.css('visibility', 'hidden');
			var rnd = Math.ceil(Math.random() * 10000); 
			var dummy = $(document);
			cloneBody.find('input, select, textarea').each(function(i,e){
				dummy[0] = this;
				dummy.attr({
					'name': (dummy.attr('name') || 'name') +'ctrl1',
					//'id': (dummy.attr('id') || 'id') + rnd + i,
					//Modified by thanapon 5-Nov-2018
					'id': dummy.attr('id')+'ctrl1',
					//Add by Sira , set class clone
					//'class' : 'Clone',
					'tabindex': '-1',
					disabled: dummy.attr('disabled'),
					readonly: dummy.attr('readonly')
				});
				dummy.addClass("Clone");
			});
			if (window['isIE6']) { //fix 'name' attribute bug when create element dynamically
				cloneBody.find('input:radio, input:checkbox').each(function(){
					dummy[0] = this;
					var attr = {
							id: dummy.attr('id'),
							'class': dummy.attr('class'),
							style: dummy.attr('style'),
							value: dummy.prop('value'),
							disabled: dummy.attr('disabled'),
							readonly: dummy.attr('readonly')
						};
					var newElement = $('<input tabindex="-1" type="' + dummy.attr('type')  + '" name="' + dummy.attr('name') + '" />')
					.attr(attr);
					newElement.prop('checked', dummy.prop('checked'));
					dummy.replaceWith(newElement);
				});
			}
			thead.find('table').append(cloneBody);
			
			
			thead._thead2.children('div').width(table.outerWidth());
//			Modify by Sira, Fix header.
			//thead._thead2.height(table.children('thead').find('tr').outerHeight());
			thead._thead2.children('div').width(table.outerWidth());
			var totalHeaderTr = table.children('thead').find('tr').size();
			var trHeight = table.children('thead').find('tr').get(0).clientHeight * totalHeaderTr;
			thead._thead2.height(trHeight);
		}
	};
	
	
	/** 
	 * Make table editable
	 * @param {string|JQueryObject} id selector or JQuery Object for a single element
	 * @param {object} options Object that contains callback funcion
	 * @public 
	 *
	 * @example
	 * <b>Usage:</b> 
	 * var table = ST3Lib.roweditor( '#tableid', { success:fn, edit:fn, save:fn } );
	 * table.editRow( index ); //edit select row
	 * table.addRow(firstRow); //if firstRow == true then add new row to first otherwise add new row to end
	 * table.save(); save all editable row
	 *
	 * Option: 
	 * success: call when operation success
	 * edit: call before assign value to each input box. return false to skip normal assign value operation
	 * save: call before assign value to each column. return false to skip normal assign value operation
	 * */
	ST3Lib.roweditor = function (id, options) {
		options = options || {};
		var table = $(id);
		var template = null, trAdd = null, trEdit = null;
		if ( options.template ) {
			template = $(options.template);
			trAdd = template.find('.template.add');
			trEdit = template.find('.template.edit');
			//trView = template.find('.template.view');
		}
		function innerXHTML($source,$string,$appendage) {
			// (v0.4) Written 2006 by Steve Tucker, http://www.stevetucker.co.uk
			if (typeof($source) == 'string') $source = document.getElementById($source);
			if (!($source.nodeType == 1)) return false;
			var $children = $source.childNodes;
			var $xhtml = '';
			if (!$string) {
				for (var $i=0; $i<$children.length; $i++) {
					if ($children[$i].nodeType == 3) {
						var $text_content = $children[$i].nodeValue;
						$text_content = $text_content.replace(/</g,'&lt;');
						$text_content = $text_content.replace(/>/g,'&gt;');
						$xhtml += $text_content;
					}
					else if ($children[$i].nodeType == 8) {
						$xhtml += '<!--'+$children[$i].nodeValue+'-->';
					}
					else {
						$xhtml += '<'+$children[$i].nodeName.toLowerCase();
						var $attributes = $children[$i].attributes;
		 				for (var $j=0; $j<$attributes.length; $j++) {
							var $attName = $attributes[$j].nodeName.toLowerCase();
							var $attValue = $attributes[$j].nodeValue;
							if ($attName == 'style' && $children[$i].style.cssText) {
								$xhtml += ' style="'+$children[$i].style.cssText.toLowerCase()+'"';
							}
							else if (typeof $attValue  === 'string' && $attName != 'contenteditable') {
								$xhtml += ' '+$attName+'="'+ $attValue +'"';
							}
						}
						$xhtml += '>'+innerXHTML($children[$i]);
						$xhtml += '</'+$children[$i].nodeName.toLowerCase()+'>';
					}
				}
			}
			else {
				if (!$appendage) {
					while ($children.length>0) {
						$source.removeChild($children[0]);
					}
					$appendage = false;
				}
				$xhtml = $string;
				while ($string) {
					var $returned = translateXHTML($string);
					var $elements = $returned[0];
					$string = $returned[1];
					if ($elements) {
						if (typeof($appendage) == 'string') $appendage = document.getElementById($appendage);
						if (!($appendage.nodeType == 1)) $source.appendChild($elements);
						else $source.insertBefore($elements,$appendage);
					}
				}
			}
			return $xhtml;
		}
		function disableSort(disable){
			$('table.dataTable:not(.fixed-header)').each(function(i,e){
				e = $(e);
				var settings = e.dataTable().fnSettings();
				for(var i = 0; i < settings.aoColumns.length; ++i){
					settings.aoColumns[i].bSortable = !disable;
				}
			});
			
		}
		function hideComponent(hide){
			if ( hide ) {
				$('div.dataTables_info, div.dataTables_paginate').css('visibility', 'hidden');
				$('div.dataTables_filter').css('visibility', 'hidden');
				$('table.dataTable:not(.fixed-header)').addClass('nosortimage');
			}else {
				$('div.dataTables_info, div.dataTables_paginate, div.dataTables_filter').css('visibility', 'visible');
				$('div.dataTables_filter').css('visibility', 'visible');
				$('table.dataTable:not(.fixed-header)').removeClass('nosortimage');
			}
		}
		return {
			
			editRow: function(rowIndex){
				var selectRow = table.find( '> tbody > tr:eq(' + rowIndex + ')' );
				if ( selectRow.length === 0 ) return;
				var trNew = trEdit.clone().show().addClass( 'editable' );
				selectRow.find( '> td' ).each(function( i, e ){
					var td = trNew.find( '> td:eq(' + i + ')' );
					var html = innerXHTML(td[0]);
					td.attr( 'data', $(e).html() );
					if ( ! options.edit || ( options.edit && options.edit( i, $(e), td) !== false ) ) {
						html = html.replace( /@data/g, $(e).html() );
						td.html(html);
					}
				});
				selectRow.replaceWith( trNew );
				hideComponent(true);
				disableSort(true);
				if ( options.success ) options.success.call(this, 'edit', trNew );
				$(window).trigger('fixedcolumn', [null, true]);
			},
			
			addRow: function(firstRow) {
				var trNew = null;
				if ( trAdd && trAdd.length > 0 ) {
					trNew = trAdd.clone().show().addClass( 'editable new' );
					if(firstRow){
						table.find( '> tbody' ).prepend( trNew );
					}else{
						table.find( '> tbody' ).append( trNew );
					}
				}
				hideComponent(true);
				disableSort(true);
				if ( options.success ) options.success.call(this, 'add', trNew );
				$(window).trigger('fixedcolumn', [null, true]);
			},
			deleteRow: function( rowIndex ) {
				var selectRow = table.find( '> tbody > tr:eq(' + rowIndex + ')' );
				if ( selectRow.length === 0 ) return;
				selectRow.remove();
				if ( options.success ) options.success.call(this, 'delete', selectRow );
				$(window).trigger('fixedcolumn', [null, true]);
			},
			save: function() {
				var trs = table.find( '> tbody > tr.editable' );
				trs.each( function( i, e ) {
					var row = $( e );
					row.removeClass( 'template editable new add edit' );
					row.find( '> td' ).each(function( i, e ){
						var td = $( e );
						var children = td.find('input:text,select,textarea');
						if ( children.length === 0 ) return;
						var val = children.val();
						if ( ! options.save || ( options.save && options.save.call(this, i, val, td) !== false ) ) {					
							if (!td.children().is('a')) {
								td.empty();
								td.html( val );
							}
						}
					});
				} );
				hideComponent(false);
				disableSort(false);
				if ( options.success ) options.success.call(this, 'save', trs );
				$(window).trigger('fixedcolumn', [null, true]);
			},
			cancel: function(){
				var trs = table.find( '> tbody > tr.editable' );
				trs.each( function( i, e ) {
					var row = $( e );
					if ( row.hasClass( 'new' ) ) row.remove();
					else{
						row.find( '> td' ).each(function( i, e ){
							var td = $( e );
							var val = td.attr( 'data' );
							td.html( val );
							td.attr( 'data', null );
						
						});
					}
					row.removeClass( 'template editable new add edit' );
					
				} );
				hideComponent(false);
				disableSort(false);
				if ( options.success ) options.success.call(this, 'cancel', trs );
				$(window).trigger('fixedcolumn', [null, true]);
			}
			
		};
	};
	
	/** @class 
	 * 
	 * 
	 * */
	ST3Lib.dialog =  {
		btn: { ok:'', cancel:''},
		_confirmDefaultOptions: {},
		/** 
		 * Initial method. this method will be automatic execute
		 * when document ready
		 * @public */
		init: function(){
			$('body').append(
					'<div id="st3-dialog" style="display:none;padding: 0em;">'+
						'<div class="application-panel">'+
							'<span id="logo" class="toggle-menu">' +
							'<span class="application" style="padding-left:2px;zoom:1;vertical-align:middle;">Toyota</span></span>'+
							'<span class="application" style="padding-left:5px;zoom:1;vertical-align:middle;">Standard </span>'+
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
				        id: 'WST3Ok' ,
				        click: function() {
				        	var self = $(this);
							self.siblings('.ui-dialog-buttonpane')
								.find('input,button').prop('disabled', true);
							self.dialog('close');
							callback && callback(true);	
				        }},{
				        text: ST3Lib.dialog.btn.cancel ,
				        id: 'WST3Cancel' ,
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
				        id: 'WST3Ok' ,
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
	
	/** @deprecated */
	ST3Lib.dropDown = {
			dropDownChange : function(formAction,formId,fnName){
				var formvalue = $("#"+formId).serialize();
				formvalue += '&isajax=1';
				var ajaxopt = {url: formAction, global: false,
						type: "POST", data: formvalue,
						//dataType: "html",
						async:true, cache :false };
				ajaxopt.success = function(data){
					var fn = new Function( 'return ' + fnName);
					if(!eval("typeof(" + fnName + ") == typeof(Function)")){
						ST3Lib.message.addError( "Please Create Funtion For dropDownChange." );
						ST3Lib.message.show(1);
						return false;
					}
					var ret = fn.call(self, data);
					if (typeof ret === 'function' )
						ret.call( self, data );
				};
				ajaxopt.error = function (xhr, ajaxOptions, thrownError) {
					ST3Lib.message.addError( xhr.status + " "+ thrownError );
					ST3Lib.message.show(1);
					if (loading) loading.close();
				};
				$.ajax(ajaxopt);
			}
	};
	
	
	/** 
	 * Wrap function of $.ajax but this method will be retry if session timeout
	 * and auto redirect to login page when error can not resolve
	 * @public
	 * 
	 * @param {string} url
	 * @param {string} method HTTP method (GET/POST/PUT/DELETE)
	 * @param {string} parameter Send data to server
	 * @param {boolean} [showMsg=false] Show message on screen when error occur
	 * @param {function} callback Callback function when connection success or failed
	 *
	 * @example
	 * Send parameter "param1" and "param2" to server
	 * ST3Lib.ajax('url/to/server', 'POST', 'param1=value1&param2=value2',
	 * 	 function(data, result){
	 * 		if (result === false){
	 * 			alert('Error');
	 * 		}else{
	 * 			alert(data);
	 * 		}
	 *   })
	 * 
	 * */
	ST3Lib.ajax = function(url, method, parameter, notShowMsg, callback){
		if (ST3Lib._disabledST3Lib) return;
		if (typeof notShowMsg === 'function') {
			callback = notShowMsg;
			notShowMsg = false;
		}
		var self = this;
		if (callback && typeof callback === 'string') {
			if(typeof window[callback] !== 'function'){
				ST3Lib.message.addError( "Please Create Funtion For Ajax." );
				ST3Lib.message.show(1);
				return false;
			}
			callback = (new Function( 'return ' + callback))();
		}
		
		var ajaxopt = {url: url, global: false, 
				type: method, data: parameter,
				async:true, cache :false,
				xhrFields: {
					withCredentials: true
				}
		};
		if (window['isIE6']) {
			delete ajaxopt.xhrFields;
		}
		
		ajaxopt._retryCnt = 0;
		ajaxopt.success = function(data){
			if ($.type(data) === 'string') {
				if ((/login-dialog|WSC20000/).test(data)){ //is login screen
					if (retryAjax(ajaxopt) === false) {
						if (ajaxopt._retryCnt === 2) {
							(window.top || window).location.href = _casLogin + '?service=' 
								+ escape((window.top || window).location.href) + '&parammessage=MSC29101AERR';
							//ST3Lib.message.addError('Request was canceled before it completes. Please try again.');
							//ST3Lib.message.show(1);
							//callback && callback.call(self, data, true);
						}
					}
					ST3Lib.validate.disabledButton(false);
					return;
				}
			}
			
			ST3Lib.message.setPayload( data, notShowMsg );//set message from payload
			
			if ( ST3Lib.message.count() > 0 )
				ST3Lib.message.show(1);
			ST3Lib.validate.disabledButton(false);
			
			callback && callback.call(self, data, true);
		};
		ajaxopt.error = function (xhr, ajaxOptions, thrownError) {
			var retry = true;
			if (xhr.status === 0 && (retry = retryAjax(ajaxopt)) === false) {
				if (ajaxopt._retryCnt === 2) {
					(window.top || window).location.href = _casLogin + '?service=' 
						+ escape((window.top || window).location.href) + '&parammessage=MSC29101AERR';
				}
				//ST3Lib.message.addError('Request was canceled before it completes. Please try again.');
				//console.log('Request was canceled before it completes. Please try again.');
			}else if (xhr.status !== 0)
				ST3Lib.message.addError( xhr.status + " " + thrownError );
		
			if (xhr.status !== 0 || retry === false) {
				ST3Lib.message.show(1);
				ST3Lib.validate.disabledButton(false);
				callback && callback.call(self, null, false, xhr);
			}
		};
		
		ST3Lib.validate.disabledButton(true);
		
		function runAjax(ajaxopt){
			try{
				(ST3Lib.$2 || $).ajax(ajaxopt);
			}catch(e){
				ST3Lib.message.addError(e.toString());
				ST3Lib.message.show(1);
				return false;
			}
		}
		function retryAjax(ajaxopt){
			if (ajaxopt._retryCnt === 2) return false;
			ajaxopt._retryCnt++;
			
			var url = window.location.href;
			if (url.indexOf('ajaxIframe') >= 0) return false;
	 		
			var _window = window;
			var iframe = $('#iframeRetryAjax');
			if (iframe.length === 0) {
			
				iframe = $('<iframe name="iframeRetryAjax" />');
				iframe.attr('id', 'iframeRetryAjax');
				iframe.css({border:'0px', margin: '0px', padding: '0px'
					, position:'absolute', left: '-10px', top: '0px'
					, 'z-index': 10, width: '1px', height: '1px'
					, 'background-color': 'white'});
				iframe.hide();
				iframe.appendTo(document.body);
				iframe.show();
			}
			
			url = url.replace(/&ajaxIframe=1&rnd=[0-9]+/, '');
			if(url.indexOf('?') < 0) url += '?';
			
			iframe.off('load').on('load', function(){
				try{
					ST3Lib.$2 = _window.iframeRetryAjax.ST3Lib.$;
					var messages = _window.iframeRetryAjax.ST3Lib.message.panel.find('.message');
					for(var i = 0; i < messages.length; ++i) {
						var div = $(messages[i]);
						if (div.hasClass('warn')) {
							ST3Lib.message.addWarning(div.text());
						}else if (div.hasClass('err')) {
							ST3Lib.message.addError(div.text());
						}else if (div.hasClass('inf')) {
							ST3Lib.message.addInfo(div.text());
						}
					}
					runAjax(ajaxopt);
				}catch(e){
					ST3Lib.validate.disabledButton(false);
					runAjax(ajaxopt);
				}
			});
			iframe.attr('src', url + '&ajaxIframe=1&rnd=' + parseInt(Math.random() * 10000));
			return true;
		}
		runAjax(ajaxopt);
		return true;
	};
	
	
	/**
	 * Jquery Extension use for submit form by specify url
	 * @public
	 *
	 * @param {string|JqueryObject} selector JQuery selector or JQuery object that point to form element
	 * @param {string} method HTTP Method (GET/POST/PUT/DELETE)
	 * @param {function} callback Callback function when success or failed
	 * 
	 * @example
	 * ST3Lib.submitform('#myform', 'new/url', 'PUT', function(data){
	 * 
	 * });
	 * 
	 * Or
	 * 
	 * $('#myform').submitForm('/new/url', 'PUT', function(data){
	 * 		alert(data);//popup data
	 * });
	 *  */
	ST3Lib.submitform = function(selector, url, method, callback){
		ST3Lib.$(selector).submitForm(url, method, callback);
	};
	ST3Lib.$.fn.submitForm = function(url, method, callback){
		var self = this;
		var oriUrl = this.attr('action');
		var oriMethod = this.attr('_method') || this.attr('method');
		var oriCallback = this.attr('ajax');
		
		self[0]._invokeBySubmitForm = true;
		if (typeof method === 'function') {
			callback = method;
			method = oriMethod || 'GET';
		}else if (typeof url === 'function') {
			callback = url;
			method = oriMethod || 'GET';
			url = oriUrl || window.location.href;
		}
		
		callback = callback || oriCallback;
		
		
		var fnName = 'fn_rnd_' + Math.ceil(Math.random() * 10000);
		window[fnName] = function(data, loading, status){
			finalize();
			
			if (callback && typeof callback === 'function') {
				callback(data, loading, status);
			}else if (callback){
				if (window[callback]) {
					window[callback].call(null, data, loading, status);
				}
			}
		};
		
		
		this.attr('action', url);
		this.attr('method', method);
		this.attr('_method', method);
		this.attr('ajax', fnName);
		
		
		this.submit();
		
		
		function finalize() {
			self[0]._invokeBySubmitForm = false;
			self.attr('action', oriUrl);
			self.attr('method', oriMethod);
			self.attr('_method', method);
			self.attr('ajax', oriCallback);
			try {
				delete window[fnName];
			} 
			catch(e) {
				window[fnName] = undefined; 
			}
			
		}
		return this;
	};
	
	/** 
	 * @class
	 * This class will be automatic initial when document ready
	 * it will be attach event to every form on page.
	 * If form has attribute "ajax" it will be disable 
	 * default form mechanism and send form data to server by using ST3Lib.ajax()
	 * 
	 * if control (textbox, dropdownlist, ...) was validate failed 
	 * it will be stop process and show error message on screen
	 * 
	 *  */
	ST3Lib.validate = {
		_validateServer: false,
		/**
		 * Initial attach onsubmit event 
		 * @public
		 *  */
		init: function(){
				if( window.location.href.indexOf('?test') > -1 || window.location.href.indexOf('&test') > -1 ){ //window.location.href.indexOf("test") > -1) {
					this._validateServer = true;
			    }
				this._initAttachSubmitButton();
				this._initAttachResetButton();
				
		},
		restoreFormSubmit: function(selector){
			$(selector || 'form').each(function(i, form){ //fix for manual call form.submit()
				if (form._submit)
					form.submit = form._submit;
			});
		},
		_initAttachResetButton: function(){
			$(document.body).on('click', 'form input[type=reset]', function(e){
				//$(this).closest('form').not(':button, :submit, :reset, :hidden').val('').removeAttr('checked').removeAttr('selected');
				var form = $(this).closest('form');
				var formArr = form.serializeArray();
			    jQuery.each(formArr , function(i, field) {
			    	var obj = form.find('[name=' + field.name + ']' );
				    var val = obj.attr('default') || '';
				    obj.val(val).change();
				    //if (obj.is(':checkbox')) {
				    	
				    //}
			    });
				
				
				ST3Lib.message.clear(true);
				e.preventDefault();
			});
		},
		_disabledButtonCache: null,
		/**
		 * disabled/enabled all buttons that specify by selector 
		 * @public 
		 * 
		 * 
		 * @param {(String|jQueryObject)} [selector=window] Parent of the buttons if not specify it mean window (all button will be disabled/enabled)
		 * @param {Boolean} disabled true - enabled, false - disabled
		 * */
		disabledButton: function(selector, disabled){
			if ($.type(selector) === 'boolean') {
				disabled = selector;
				selector = null;
			}else if ($.type(selector) === 'string') {
				selector = $(selector);
			}
			
			if (this._disabledButtonCache === null)
				this._disabledButtonCache = $('input[type=submit], input[type=button], input[type=reset]');
			if (selector == null)
				selector = this._disabledButtonCache;
			else selector = selector.find('input[type=submit], input[type=button], input[type=reset]');
			selector.each(function(i, e){
				e = $(e);
				var inc = parseInt(e.attr('disabled-inc')) || 0;
				if (disabled) {
					inc++;
					if (e.is(':enabled')) {
						e.addClass('ajax-disabled');
						if (e.hasClass('ui-button'))
							e.button('option', 'disabled', true);
						else e.prop('disabled', true);
					}
				}else{
					inc--;
					if (inc <= 0 && e.hasClass('ajax-disabled')) {
						e.removeClass('ajax-disabled');
						if (e.hasClass('ui-button'))
							e.button('option', 'disabled', false);
						else e.prop('disabled', false);
					}
				}
				if (inc <= 0)
					e.removeAttr('disabled-inc');
				else
					e.attr('disabled-inc', inc);
				
			});
		},
		/**
		 * disabled/enabled button that specify by selector 
		 * @public 
		 * 
		 * 
		 * @param {(String|jQueryObject)} specify button will be disabled/enabled
		 * @param {Boolean} disabled true - enabled, false - disabled
		 * */
		disabledButtonSpecify: function(selector, disabled){
			if ($.type(selector) === 'string') {
				selector = $(selector);
				selector.each(function(i, e){
					e = $(e);
					var inc = parseInt(e.attr('disabled-inc')) || 0;
					if (disabled) {
						inc++;
						if (e.is(':enabled')) {
							e.addClass('ajax-disabled');
							if (e.hasClass('ui-button'))
								e.button('option', 'disabled', true);
							else e.prop('disabled', true);
						}
					}else{
						inc--;
						if (e.hasClass('ajax-disabled')) {
							e.removeClass('ajax-disabled');
							if (e.hasClass('ui-button'))
								e.button('option', 'disabled', false);
							else e.prop('disabled', false);
						}
					}
					if (inc <= 0)
						e.removeAttr('disabled-inc');
					else
						e.attr('disabled-inc', inc);
					
				});
			}
		},
		_initAttachSubmitButton: function(){
			var validateSelf = this;
			
			$('form').each(function(i, form){ //fix for manual call form.submit()
				form._submit = form.submit;
				form.defaultsubmit = function(){ form._submit(); form.submit = form.newsubmit; };
				form.newsubmit = function(){ $(this).submit(); };
				form.submit = form.newsubmit;
			});
			$(document.body).on('submit', 'form', function(ev){
				validateSelf.disabledButton(true);
				if ( this.defaultsubmit )
					this.submit = this.defaultsubmit;
				
				var self = $( this );
			    if ( ev.originalEvent ) //auto clear message when event trigger by user only.
			    	ST3Lib.message.clear();
				
			    var forms = $(this);
			    if (forms.attr('disabled-clear') && typeof forms.attr('disabled-clear') === 'string'){
				    $(this).find('.readonly').prop('disabled',false);//:disabled
				    var arrayDisabled = [];
					var formArr = $(this).serializeArray();
				    jQuery.each(formArr , function(i, field) {
				    	var obj = forms.find('[name=' + field.name + ']' );
					    if (obj.hasClass('readonly')) {
					    	arrayDisabled.push(field.name);
					    }
				    });
			    }
			    
			    
			    //validateSelf._trimFormValue( self );
			    
			    //var lstMandatory = self.find('input[type=text].MandatoryField,input[type=password].MandatoryField,input[type=checkbox].MandatoryField,textarea.MandatoryField,select.MandatoryField');
				//selector: 'input[type=text][filter],input[type=password][filter],textarea[filter]',	
			    var lstInput = self.find('input[type=text],input[type=password],input[type=checkbox],input[type=file],textarea,select');
			    
			    //////////// check validate ///////////////////////////
			    var options = { count:0, form: self };
			    if ( ! validateSelf._validateServer ) {
					lstInput.each( function( i, e ) {
						var $e = $(e);
						if ($e.css('text-transform') === 'uppercase') { //auto uppercase when set stylesheet 'text-transform: uppercase;'
							$e.val($e.val().toUpperCase());
						}
						validateSelf._processMessage(i, e, options);
					});
			    }
				///////////// end check validate /////////////////////////
				
				
				var allowDefaultAction = false;
				if(options.count === 0) {
					// For some browsers, `attr` is undefined; for others,
					// `attr` is false.  Check for both.
					var ajaxAttr = self.attr('ajax');
					if (typeof ajaxAttr !== 'undefined' && ajaxAttr !== false) {
						validateSelf._processAjax(options);
					}else allowDefaultAction = true;					
				}else{
					ST3Lib.message.show(1);
					validateSelf.disabledButton(false);
					var validateErrorCallback = self.attr('validate-error');
					if (validateErrorCallback) {
						try{
							var fnValidateError = new Function('return ' + validateErrorCallback);
							var ret = fnValidateError.call(self);
							if (typeof ret === 'function' )
								ret.call( self );
						}catch(ex){ console.log(ex.toString()); }
					}
										
				}
				if ( allowDefaultAction === false ) {
					ev.preventDefault();
					if ( this.newsubmit )
						this.submit = this.newsubmit;
				}else{
					validateSelf.disabledButton(false);
				}
				
				if (forms.attr('disabled-clear') && typeof forms.attr('disabled-clear') === 'string'){
					formArr = forms.serializeArray();
					jQuery.each(formArr , function(i, field) {
						
						var obj = forms.find('[name=' + field.name + ']' );
					    if (obj.hasClass('readonly')) {
					    	obj.prop('disabled',true);
					    }
					});
				}
			});
		},
		_trimFormValue: function( form ){
			var formArr = form.serializeArray();
		    jQuery.each(formArr , function(i, field) {
		     // console.log($('[name='+formArr[i].name+']').val());
		      var obj = form.find('input[name=' + field.name + '],' 
		    		  + 'select[name=' + field.name + '],' 
		    		  + 'textarea[name=' + field.name + '],'
		    		  + 'button[name=' + field.name + ']');
		      if ( obj.length > 0 ){
		    	  obj.each(function(i,e){
		    		 e = $(e);
		    		 e.val( $.trim(e.val()) );
		    	  });
		      }else
		    	  obj.val($.trim(field.value));
		    });
		    return formArr;
		},
		_processAjax: function(options){
			var validateSelf = this;
			var ajaxAttr = options.form.attr('ajax');
			var formMethod = (options.form.attr('_method') || options.form.attr('method') || 'POST').toUpperCase();
			var formAction = (typeof options.form.attr('action') !== 'undefined' && options.form.attr('action') !== false) ? options.form.attr('action') : "";
			var loadingTarget = options.form.attr('ajax-loading-target');
			var loading = null;
			var ret = false;
			var callback = null;
			
			if ( loadingTarget )
				loading = ST3Lib.dialog.loading(loadingTarget);
			
			if (ajaxAttr && typeof ajaxAttr === 'string') {
				if(typeof window[ajaxAttr] !== 'function'){
					ST3Lib.message.addError( "Please Create Funtion For Ajax." );
					ST3Lib.message.show(1);
				}else callback = (new Function( 'return ' + ajaxAttr))();
			}
			
			if (callback) {
				var formvalue = options.form.serialize();
				formAction = formAction + (formAction.indexOf('?')>=0?'&isajax=1':'?isajax=1');
				if ( formMethod !== 'POST') {
					formAction += '&' + formvalue;
					formvalue = null;
				}
				ret = ST3Lib.ajax(formAction, formMethod, formvalue, function(data, status){
					validateSelf.disabledButton(false);
					
					callback.call(options.form, data, loading, status);
					if (loading && loading.autoClose ) loading.close();
				});
				
				
			}
			if (ret === false) {
				validateSelf.disabledButton(false);
				if (loading && loading.autoClose ) loading.close();
			}
			return ret;
		},
		_processMessage:function(i, e, options) {
			e = $(e);
			if (e.is(':disabled') || e.hasClass('disabled-validate')) return;
			if (typeof e.attr('name') === 'undefined') return;
				
			for( var name in ST3Lib.validate._validate_action ) {
				var action = ST3Lib.validate._validate_action[name];
				if ( action ) {
					var msgOption = { name: name, form: options.form };
					
					if ( action.call( e, msgOption ) === false ) {
						if(typeof msgOption.message !== 'undefined'){
							options.count++;
							if(options.count === 1)
								e.focus();
							ST3Lib.message.addError( msgOption.message );
						}
					}
				}
			}
		},
		getControlLabel: function($form, $obj){
			return ($.trim($obj.attr('title')) || $.trim($form.find('label[for=' + $obj.attr('name')+']').text())).replace("*", "").replace(/:$/, '');
		},
		/**
		 * list of validation that use to validate control 
		 * @member */
		_validate_action: {
			
			/** 
			 * @this refer to control
			 * @desc Mandatory check. When control has class "MandatoryField"
			 * 		it will be check if control is empty value or not.
			 *  */
			'mandatory-msg': function(options){
				if ( ! this.hasClass( 'MandatoryField' ) || this.hasClass('Clone') )
					return true;
				var msgCode = this.attr('mandatory-msg');
				var msgDetail = ST3Lib.validate.msgcode[ msgCode ];
				var name = '';
				if ( ! ( msgCode && msgCode.length > 0 ) )  {
					msgCode =  'MSTD0031AERR';
					msgDetail = ST3Lib.validate.msgcode[ msgCode ] || "{0} should not be empty.";
					name = ' ' + ST3Lib.validate.getControlLabel(options.form, this) + ' ';
				}
				if ( $.trim( this.val() ) === '' ) {
					options.message = msgDetail.format(name);//msgCode + ': ' + name + msgDetail;
					return false;
				}
				return true;
			}
			/** @this refer to control */
			,'format-msg': function(options){
				var value = $.trim( this.val() );
				if ( this.hasClass( 'MandatoryField' ) &&  value === '' )
					return;
				var msgCode = this.attr('format-msg');
				var msgObj =  ST3Lib.validate.format_msg[ msgCode ];
				if ( ! msgCode ) return true;
				if ( ! msgObj ) throw new Error( 'No format_msg' );
				var ret = false;
				if(msgObj.fn){
					if (typeof msgObj.fn === 'function' ) ret = true;//msgObj.fn.call(this,aa);
				}else{
					ret = msgObj['regEx'].test( value ) ;
				}
				if ( ! ret ){
					var msgDetail = ST3Lib.validate.msgcode[msgObj['ErrorCode']];
					if (!msgDetail) msgDetail = msgObj['msg'];
					var name = ST3Lib.validate.getControlLabel(options.form, this);
					if (msgObj.params) {
						var params = msgObj.params;
						if (typeof params === 'function')
							params = msgObj.params(this);
						options.message = msgDetail.format.apply(msgDetail, params);
					}else
						options.message = msgDetail.format(name);
				}else{
					if(msgObj.fn){
						var name = ST3Lib.validate.getControlLabel(options.form, this);
						if( msgObj.fn(this,options,name,options.form) === false ){
							var msgDetail = ST3Lib.validate.msgcode[msgObj['ErrorCode']];
							if (!msgDetail) msgDetail = msgObj['msg'];
							if (msgObj.params) {
								var params = msgObj.params;
								//if (typeof params === 'function')
								//	params = msgObj.params(this);
								options.message = msgDetail.format.apply(msgDetail, params);
							}else
								options.message = msgDetail.format(name);
							//options.message = msgDetail.format(name);
							return false;
						}
					}else{
						return true;
					}
				}
				return false;
			}
			/** @this refer to control */
			,'format-msg-non-mandatory': function(options){
				var value = $.trim( this.val() );
				if(value == ""){
					return;
				}
				var msgCode = this.attr('format-msg-non-mandatory');
				if(msgCode == undefined){
					return	;
				}
				var msgObj =  ST3Lib.validate.format_msg[ msgCode ];
				if ( ! msgCode ) return true;
				if ( ! msgObj ) throw new Error( 'No format_msg' );
				var ret = false;
				if(msgObj.fn){
					if (typeof msgObj.fn === 'function' ) ret = true;//msgObj.fn.call(this,aa);
				}else{
					ret = msgObj['regEx'].test( value ) ;
				}
				if ( ! ret ){
					var msgDetail = ST3Lib.validate.msgcode[msgObj['ErrorCode']];
					if (!msgDetail) msgDetail = msgObj['msg'];
					var name = ST3Lib.validate.getControlLabel(options.form, this);
					if (msgObj.params) {
						var params = msgObj.params;
						if (typeof params === 'function')
							params = msgObj.params(this);
						options.message = msgDetail.format.apply(msgDetail, params);
					}else
						options.message = msgDetail.format(name);
				}else{
					if(msgObj.fn){
						var name = ST3Lib.validate.getControlLabel(options.form, this);
						if( msgObj.fn(this,options,name,options.form) === false ){
							var msgDetail = ST3Lib.validate.msgcode[msgObj['ErrorCode']];
							if (!msgDetail) msgDetail = msgObj['msg'];
							if (msgObj.params) {
								var params = msgObj.params;
								//if (typeof params === 'function')
								//	params = msgObj.params(this);
								options.message = msgDetail.format.apply(msgDetail, params);
							}else
								options.message = msgDetail.format(name);
							//options.message = msgDetail.format(name);
							return false;
						}
					}else{
						return true;
					}
				}
				return false;
			}
			/** @this refer to control */
			,'minlength': function(options){
				var value = this.val();
				if ( this.hasClass( 'MandatoryField' ) &&  value === '' )
					return;
				if (this.is("textarea")) {
					value = value.replace(/\n|\r/ig,'  ');
			    }
				var ret = true;
				var minlength = this.attr('minlength');
				var maxlength = this.attr('maxlength');
				if ( !minlength ) return true;
				
				ret = value.length >= minlength;
				ret = ret && value.length <=  maxlength;
				if (ret) return true;
				
				var msgCode = 'MSTD0053AERR';
				var name = ST3Lib.validate.getControlLabel(options.form, this);
				var msgDetail = ST3Lib.validate.msgcode[msgCode];
				options.message = msgDetail.format(name, this.attr('minlength'), this.attr('maxlength'));
				//var msgDetail = 'Invalid length of ' + name + '. The length must be between ' 
				//	+ this.attr('minlength') + " and " + this.attr('maxlength') + " digits";
				//options.message = msgCode + ': ' + msgDetail;
				return false;
			}
			
			/** @this refer to control */
			,'maxlength': function(options){
				if (this.is("textarea")) {
					var value = this.val();
					if ( this.hasClass( 'MandatoryField' ) &&  value === '' )
						return;
					if (this.is("textarea")) {
						value = value.replace(/\n|\r/ig,'  ');
				    }
					var ret = true;
					var maxlength = this.attr('maxlength');
					if ( !maxlength ) return true;
					var addlength = this.attr('addlength');
					if ( !addlength ){
						addlength = 0;
					}					
					
					ret = ret && value.length <=  parseInt(maxlength)+parseInt(addlength);
					if (ret) return true;
					
					var msgDetail = "MSTD0051AERR: Invalid length of {0}. The length can not be more than {1}.";
					var name = ST3Lib.validate.getControlLabel(options.form, this);
					options.message = msgDetail.format(name, this.attr('maxlength'));
					return false;
				}else{
					return;
				}
			}

			/** @this refer to control */
			,'same-val': function(options){
				var value = $.trim( this.val() );
				var matchvalue = this.attr('same-val');
				var same_valCode = this.attr('same_valCode');
				var same_valDetail = ST3Lib.validate.same_valCode[ same_valCode ];
				if ( ! matchvalue ) return true;
				if ( ! same_valDetail ) throw new Error( 'No same_valCode' );
				
				if(value != $.trim($('input[name='+matchvalue+']').val())){
					options.message = same_valDetail; //same_valCode + ": "+ 
				}
				return false;
			}

			/** @this refer to control */
			,'unsame-val': function(options){
				var value = $.trim( this.val() );
				var matchvalue = this.attr('unsame-val');
				var same_valCode = this.attr('unsame_valCode');
				var same_valDetail = ST3Lib.validate.unsame_valCode[ same_valCode ];
				if ( ! matchvalue ) return true;
				if ( ! same_valDetail ) throw new Error( 'No unsame_valCode' );
				
				if(value == $.trim($('input[name='+matchvalue+']').val())){
					options.message = same_valDetail; //same_valCode + ": "+ 
				}
				return false;
			}

			/** @this refer to control */
			,'file-type': function(options){
				var fileName = $.trim( this.val() );
				var fileType = this.attr('file-type');
				if(!fileType) return true;
				fileType = fileType.toLowerCase();
				var arrayType = fileType.split(",");
				if(!arrayType) return true;
				if(arrayType.length<1){
					return true;
				}else{
					var dots = fileName.split(".");
					//get the part AFTER the LAST period.
					var fileTypes = "." + dots[dots.length-1];
					fileTypes = fileTypes.toLowerCase();
					if((arrayType.join(".").indexOf(fileTypes) != -1)){
						return true;
					}else{
						options.message = "Please only upload files that end in types: \n\n" + (arrayType.join(" .")) + "\n\nPlease select a new file and try again.";
					}
				}
				return false;
			}
			,/** 
			 * @this refer to control
			 * @desc invalid-format-msg. When control have class "CheckDecimalFormat" and attribute "display-format"
			 *  */			
			'invalid-decimal-msg': function(options){
				if ( ! this.hasClass( 'CheckDecimalFormat' ))
					return true;
				var msgformat = MSTD0050AERR;
				var displayformat = this.attr('display-format');
				if(displayformat !== ''){
					
					var name = ' ' + ST3Lib.validate.getControlLabel(options.form, this) + ' ';
					var displayformatarr = displayformat.split('.');
					
					/*
					 * check if the format value is numeric or decimal
					 * if it is decimal then check or else
					 */
					if(displayformatarr.length > 1){						
						var val = this.val();			
						if(val != ''){
							var valarr = val.split('.');
							if(valarr.length > 1){
								if (valarr[0].length > displayformatarr[0].length || valarr[1].length > displayformatarr[1].length ) {
									msgformat = msgformat.replace("{0}", name);//msgCode + ': ' + name + msgDetail;
									options.message = msgformat.replace("{1}",displayformat); //msgcode +': '+name + msgDetail+lastday
									return false;
								}
							}else{							
//								msgformat = msgformat.replace("{0}", name);//msgCode + ': ' + name + msgDetail;
//								options.message = msgformat.replace("{1}",displayformat); //msgcode +': '+name + msgDetail+lastday
								return true;
							}
						}
					}else{
						//this validation suppose to numeric only						
						var val = this.val();			
						if(val != ''){
							var valarr = val.split('.');
							if (valarr.length > 1 ) {
								msgformat = msgformat.replace("{0}", name);//msgCode + ': ' + name + msgDetail;
								options.message = msgformat.replace("{1}",displayformat); //msgcode +': '+name + msgDetail+lastday
								return false;
							}
						}
					}
				}
				
				return true;
			}			
			,
			/** 
			 * @this refer to control
			 * @desc invalid-value-msg. When control have class "InvalidValues", attribute "nextvalue" and "msg-format"
			 * 		it will compare between  control value and next value and will return based on msg-format which have two parameters {0} and{1}
			 * 		for example : format validation or invalid day value
			 * this controll called when user click submit button
			 *  */			
			'invalid-value-msg': function(options){
				if ( ! this.hasClass( 'InvalidValues' ))
					return true;
				var msgformat = this.attr('msg-format');
				var nextvalue = this.attr('nextvalue');	
				var name = ' ' + ST3Lib.validate.getControlLabel(options.form, this) + ' ';
				var val = this.val();				
				if (parseInt(this.val()) > parseInt(nextvalue)) {
					msgformat = msgformat.replace("{0}", name);//msgCode + ': ' + name + msgDetail;
					options.message = msgformat.replace("{1}",nextvalue); //msgcode +': '+name + msgDetail+lastday
					return false;
				}
				return true;
			}						
		},
		format_msg: {
			email: {
				regEx: /^[\w\-\.]+@([\w-]+\.)+[\w-]{2,4}$/,
				msg: "MSTD0043AERR: Invalid {0}.",
				ErrorCode: "MSTD0043AERR"
				//params: [] //or function
			}
		},
		msgcode: {},
		same_valCode: {},
		unsame_valCode: ""
		
	};
	
	/** 
	 *
	 * Use for suppress event (internal use only)
	 * */
	ST3Lib.suppressEvent = {
		stopEvent: function(e){
			e.preventDefault();
			e.stopPropagation();
			e.stopImmediatePropagation();
			return false;
		},
		/** @public */
		init: function(){
			$(window).resize(function(e){
				if (ST3Lib.suppressEvent._resize) {
					return ST3Lib.suppressEvent.stopEvent(e);
				}
			});
		},
		
		_resize: false,
		resize: function(suppress){
			ST3Lib.suppressEvent._resize = suppress;
		}
	};
	
	/** @class 
	 * For modern browser when tab was inactive 
	 * javascript will be run slower than normal
	 * and also function setInterval will be execute
	 * callback function slower than interval parameter
	 * .
	 * This class will be check if time lag happen
	 * it will be try to get time from server
	 * for syncronize with client again
	 * */
	ST3Lib.compareTime = {
			timeLocale: ''
			,increaseTimeSec: ''
			,diffTime: ''
			,realTimeLocale: ''
			,dateServer: ''
			,timeServer: ''
			,_h: ''
			,_m: ''
			,_s: ''
			,_getTimeServer: ''
			,getNewTime : true,
			interval: '',
			
			/**
			 * Initial time syncronize with server
			 * this function will be automatic call when document ready
			 * @public 
			 * */
			init: function(){
				this.timeLocale = new Date();
				this.serverTime = new Date($(ST3Lib.id.ID_TIMESEC_LABEL).text());
				this.realTimeLocale = new Date();
				if (!this.timeLabel)
					this.timeLabel = $(ST3Lib.id.ID_TIME_LABEL);
				if (!this.timeMobileLabel)
					this.timeMobileLabel = $(ST3Lib.id.ID_TIMEMOBILE_LABEL);
				//var strTime = this.timeMobileLabe.text().split(" ");
				//if(strTime.length<4)
				//	return;
				//var time = strTime[3].split(":");
				//this._h = parseInt(time[0]);
				//this._m = parseInt(time[1]);
				//this.dateServer = strTime[0]+" "+strTime[1]+" "+strTime[2];
				//this.month = strTime[1];
				//this.timeServer = this._h+":"+this._m;
				//window.setTimeout("ST3Lib.compareTime.checkTime()", 0);// start immediately
				//this.interval = window.setInterval("ST3Lib.compareTime.checkTime()", 1000);// update every second
			},
			/**
			 * This function will be automatic execute every 1000 ms
			 * and will be invoke ajax to get time from server 
			 * when time difference from server more than 5 sec
			 *  @public */
			checkTime: function(){
				var diff = new Date() - this.timeLocale;
				var diffSec = new Date().getTime() - this.realTimeLocale.getTime();
				var newTime = new Date(this.serverTime.getTime() + diff);
				
				if( Math.abs(diffSec/1000) > 5 ){
					window.clearInterval(ST3Lib.compareTime.interval);//
					ST3Lib.ajax(this._getTimeServer, 'POST', '' ,true, function(data,status){});
					return;
				}
				
				//var sec =  zeroLead(newTime.getSeconds(),2);
				var min =  zeroLead(newTime.getMinutes(),2);
				var hour = zeroLead(newTime.getHours(),2);
				
				var strTime = newTime.format('dd M yy {0}:{1}'.format(hour,min));
				this.timeLabel.text(strTime);
				this.timeMobileLabel.text(strTime);
				this.realTimeLocale = new Date();
			}
	};
	
	ST3Lib.app = {
		init: function(){
			ST3Lib.suppressEvent.init();
			if (window.location.href.indexOf('ajaxIframe') >= 0) {
				ST3Lib._disabledST3Lib = true;
				ST3Lib.suppressEvent.resize(true);
				ST3Lib.message.init();
				return false; //this content is ajaxIframe, so do not cal ajax by itself
			}
			ST3Lib.sizing.init();
			ST3Lib.message.init();
			ST3Lib.content.init();
			(window.location.href.indexOf("test") > -1)?"":ST3Lib.filter.init();
			ST3Lib.dialog.init();
			ST3Lib.validate.init();
			ST3Lib.menu.init();	
			ST3Lib.compareTime.init();
		}
		
		
		
		
	};
	
	
})(jQuery);


$(function(){
	ST3Lib.app.init();
});
