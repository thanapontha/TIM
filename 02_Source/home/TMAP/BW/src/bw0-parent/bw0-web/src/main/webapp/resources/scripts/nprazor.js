if ( typeof module === 'undefined' )
	module = void 0;
	
if (!Function.prototype.bind) {
  Function.prototype.bind = function (oThis) {
    if (typeof this !== "function") {
      // closest thing possible to the ECMAScript 5 internal IsCallable function
      throw new TypeError("Function.prototype.bind - what is trying to be bound is not callable");
    }

    var aArgs = Array.prototype.slice.call(arguments, 1), 
        fToBind = this, 
        fNOP = function () {},
        fBound = function () {
          return fToBind.apply(this instanceof fNOP && oThis
                                 ? this
                                 : oThis,
                               aArgs.concat(Array.prototype.slice.call(arguments)));
        };

    fNOP.prototype = this.prototype;
    fBound.prototype = new fNOP();

    return fBound;
  };
}

var Helper = (function() {
	function Helper(){}
	Helper.prototype._disabledWrite = false;
	Helper.prototype.WHITESPACE = /^[^\n\S]+/g;
	Helper.prototype.skipWhitespace = function(str) {
		this.WHITESPACE.lastIndex = 0;
		this.WHITESPACE.exec(str);
		str.substr(this.WHITESPACE.lastIndex);
	}
	
	Helper.prototype.htmlEscape = function(text) {
	   if ( !text ) return '';
	   if(!text.replace) text = text.toString();
	   return text.replace(/&/g, '&amp;')
	     .replace(/</g, '&lt;')
	     .replace(/>/g, '&gt;')
	     .replace(/"/g, '&quot;')
	     .replace(/'/g, '&#039;')
	     .replace(/\r\n|\n/g, '<br/>')
	     ;
	}
	
	Helper.prototype.tokenizeString = function(str, tag) {
		var length = str.size();
		var sb = '';
		var c = '',prev = '';
		var i = 0;
		for ( ; i < length; ++i ) {
			var c = str.charAt(i);
			if ( c === tag && prev !== '\\' ) break;
			prev = c;
			sb += c;
		}
		if ( c === tag )
			++i;
		str.substr( i );
		return sb;
	}
	
	
	
	
	Helper.prototype.tokenizeComment = function(str) {
		var length = str.size();
		var sb = '';
		var c = '',prev = '';
		var i = 0;
		for ( ; i < length; ++i ) {
			var c = str.charAt(i);
			if ( c === '/' && prev === '*' ) break;
			prev = c;
			sb = sb + c;
		}
		sb = sb.substr( 0, sb.length - 1 );
		str.substr( i + 1 );
		return sb;
	}
	
	
	Helper.prototype.getToken = function(tokens, str, options, preSign) {
		for ( var i = 0; i < tokens.length; ++i ) {
			var token = tokens[i];
			if(token.prototype.preSign != preSign)
				continue;
			if ( token.prototype.test(str, options) )
				return new token();
		}
		return null;
	}
	
	Helper.prototype.disabledWrite = function(value){
		Helper.prototype._disabledWrite = value;
	};
	
	Helper.prototype.writer = function() {
		this.buffer = '';
	}
	Helper.prototype.writer.prototype.buffer = null;
	Helper.prototype.writer.prototype.disabled = false;
	Helper.prototype.writer.prototype.write = function( str ){
		if ( Helper.prototype._disabledWrite ) return;
		if ( str !== null && str !== undefined )
			this.buffer = this.buffer + str;
	}
	Helper.prototype.writer.prototype.toString = function(){
		return this.buffer;
	}
	return new Helper();
})();

if (module !== void 0) {
  module.exports = Helper;
}


var Token = (function() {
	var helper;
	if (module !== void 0) {
		helper = require('./helper');
	}else{
		helper = Helper;
	}
	
	
	var SCOPE = {
		AUTO: 0,
		SCRIPT: 1,
		TEXT: 2
	};
	
	var TrimWhiteSpace = /[ \t]{2,}/g;
	
	var undefined = void 0;
	function Token(name) { this.name = name; }
	Token.prototype.name = '';
	Token.prototype.sign = false;
	Token.prototype.REGEX = null;
	Token.prototype.value = null;
	Token.prototype.initChild = false;
	Token.prototype.children = [];
	Token.prototype.prev = null;
	Token.prototype.renderIgnoreParentValue = false;
	Token.prototype.renderQuote = false;
	Token.prototype.renderEscapeText = false;
	Token.prototype.renderEscapeHTML = false;
	Token.prototype.renderParentChildSpace = false;
	Token.prototype.renderWrapWrite = false;
	Token.prototype.renderWrapWriteEscape = false;
	Token.prototype.renderInternalContext = false;
	Token.prototype.renderNewLine = false;
	Token.prototype.renderIndent = false;
	Token.prototype.preSign = false;
	Token.prototype.nodeLevel = 0;
	Token.prototype.add = function(child){
		if ( !this.initChild ) { this.children = []; this.initChild = true; }
		child.prev = this;
		child.nodeLevel = this.nodeLevel + 1;
		this.children.push( child );
	}
	Token.prototype.writeIndent = function(buffer, level){
		if ( Token.prototype.beautify === false ) return;
		level = level || this.nodeLevel;
		for( var i = 0; i < level; ++i )
			buffer.push('  ');
	}
	Token.prototype.test = function(str, options) {
		this.REGEX.lastIndex = 0;
		return str.test(this.REGEX);
	}
	Token.prototype.pre = function(str, options) {
		this.REGEX.lastIndex = 0;
		var regObj = str.exec( this.REGEX );
		if ( regObj ) {
			this.value = regObj[1];
			str.substr( regObj[0].length );
		}
		return !!regObj;
	}
	Token.prototype.parse = function(str, options) {}
	Token.prototype.exec = function(str, options) {
		var ret = this.pre( str, options );
		if ( ret ) {
			if ( options && options.sign ) options.sign = false;
			var retParse = this.parse( str, options );
			if ( retParse === false )
				ret = false;
			else{
				this.value = this.value.replace(TrimWhiteSpace, ' ');
			}
		}
		return ret;
	
	}
	Token.prototype.render = function(buffer) {
		if (this.renderIndent) this.writeIndent(buffer);
		if (this.renderWrapWrite) buffer.push('write(');
		if (this.renderWrapWriteEscape) buffer.push('__escape(');
		if (this.renderQuote) buffer.push('"');
		
		if ( this.renderIgnoreParentValue === false ) {
			var value = this.value;
			if ( this.renderEscapeHTML )
				value = helper.htmlEscape( value );
			if ( this.renderEscapeText )
				value = value.replace(/\\/g, '\\\\').replace(/\r\n|\n/g, '\\r\\n').replace(/\t/g, '\\t').replace(/"/g, '\\"');
			
			buffer.push( value );
		}
		
		if ( this.renderParentChildSpace ) buffer.push( ' ' );
		if ( this.renderInternalContext ) {
			buffer.push('var __out = new writer(), write = __out.write.bind(__out);');
		}
		
		if ( this.children.length > 0 ) {
			for ( var i = 0; i < this.children.length; ++i ) {
				this.children[i].render( buffer );
			}
		}
		
		if ( this.renderInternalContext ) {
				buffer.push('\r\n return __out.toString();');
		}
		
		if (this.renderQuote) buffer.push('"');
		if (this.renderWrapWriteEscape) buffer.push(')');
		if (this.renderWrapWrite) buffer.push(');');
		if (this.renderNewLine)
			buffer.push('\r\n');
		return buffer;
	}
	Token.prototype.toString = function(){
		return this.render([]).join('');
	}
	
	function SignCheckToken(){}
	SignCheckToken.prototype = new Token('@');
	SignCheckToken.prototype.REGEX = /^(@)/g;
	
	function SignToken(){}
	SignToken.prototype = new Token('@');
	SignToken.prototype.REGEX = /^\s*(@)/g;
	
	function TextToken(block){ 
		this.inBlock = !!block; 
		if ( this.inBlock ) {
			this.pre = this.inBlockPre;
		}
	}
	TextToken.prototype = new Token('TEXT');
	TextToken.prototype.REGEX = /([^@]+)/g;
	TextToken.prototype.REGEX2 = /^([^@{}]+)/g;
	TextToken.prototype.REGEX_TEXTSCOPE = /^([^{}]+)/g;
	TextToken.prototype.inBlockPre = function(str, options) {
		var length = str.size();
		var sb = '', blockCount = options ? (options.lastBlockCount || 0):0;
		var textScope = !!options && !!options.textScope;
		while( true ) {
			this.REGEX2.lastIndex = 0;
			var regObj = str.exec(textScope?this.REGEX_TEXTSCOPE:this.REGEX2);
			if ( !regObj ) break;
			
			str.substr( regObj[0].length );
			sb += regObj[1];
			if ( '{' === str.charAt(0) === str.charAt(1) ) {
				sb += '{';
				str.substr( 2 );
			}
			if ( '}' === str.charAt(0) === str.charAt(1) ) {
				sb += '}';
				str.substr( 2 );
			}
			if ( '@' === str.charAt(0) === str.charAt(1) ) {
				sb += '@';
				str.substr( 2 );
			}
			
			if ( '{' === str.charAt(0) ) {
				++blockCount;
				sb += '{';
				str.substr( 1 );
			}
			if ( '}' === str.charAt(0) ) {
				--blockCount;
				if ( blockCount < 0 )
					break;
				else {
					sb += '}';
					str.substr( 1 );
				}
				
			}
			if ( textScope === false && '@' === str.charAt(0) ) {
				break;
			}
			
			
		}
		this.value = sb;
		this.blockCount = blockCount;
		return sb.length > 0;
	}
	
	function IdenfierToken(){}
	IdenfierToken.prototype = new Token('ID');
	IdenfierToken.prototype.REGEX = /^\s*([$A-Za-z_\x7f-\uffff][$\w\x7f-\uffff]*)/g;
	IdenfierToken.prototype.renderNewLine = false;
	IdenfierToken.prototype.parse = function(str, options){
		var tokens =[
			 MemberOperatorToken, OpenGroupToken, OpenBracketToken
		];
		for( var i = 0; i < tokens.length; ++i ) {
			var token = tokens[i];
			if ( token.prototype.test( str, options ) ) {
				token = new token();
				token.exec( str, options )
				this.add( token );
				break;
			}
		}
		
		if ( this.sign ) {
			this.renderNewLine = true;
			if ( AssignToken.prototype.test( str, options ) ) { //assign value; @a=b OR @a+=b
				var assign = new AssignToken();
				assign.exec( str, options );
				this.add( assign );
				
				// find other script token until end of line or found ';'
				var token = null;
				do {
					
					if ( NewLineToken.prototype.test( str, options ) ) {
						var op = new SemicolonToken();
						op.exec( str, options );
						op.value = ';'
						this.add( op );
						break;
					}else{
						token = str.tokenize( true );
						if ( token )
							this.add( token );
					}		
				}while( token && !( token.name === 'OP' && token.value === ';') );
				//////////////////
				
			}else{
				this.renderWrapWrite = true;
				if ( SemicolonToken.prototype.test( str, options ) ) {
					var op = new SemicolonToken();
					op.exec( str, options ); //remove ; for inline write()
				}
			}
		}
		
		
	}
	
	
	function NumberToken(){}
	NumberToken.prototype = new Token('ID');
	NumberToken.prototype.REGEX = /^\s*([0-9\.]+)/g;
	
	function MemberOperatorToken(){}
	MemberOperatorToken.prototype = new Token('.');
	//MemberOperatorToken.prototype.REGEX = /^\s*(\.)/g;
	MemberOperatorToken.prototype.REGEX = /^(\.)/g;
	MemberOperatorToken.prototype.parse = function(str, options){
		if ( IdenfierToken.prototype.test( str, options ) ) {
			var token = new IdenfierToken();
			token.exec( str, options );
			this.add( token );
		}
	}
	
	function BlankScopeToken(){}
	BlankScopeToken.prototype = new Token('@ ');
	BlankScopeToken.prototype.REGEX = /^(@ +)/g;
	BlankScopeToken.prototype.preSign = true;
	BlankScopeToken.prototype.renderIgnoreParentValue = true;
	BlankScopeToken.prototype.parse = function(str, options){
		var token = null;
		do {
			
			if ( NewLineToken.prototype.test( str ) ) {
				var op = new SemicolonToken();
				op.exec( str ); 
				op.value = ';';
				this.add( op );
				break;
			}else{
				token = str.tokenize( true );
				if ( token )
					this.add( token );
			}		
		}while( token && !( token.name === 'OP' && token.value === ';') );
	}
	
	function OpenGroupToken(){}
	OpenGroupToken.prototype = new Token('(');
	OpenGroupToken.prototype.REGEX = /^\s*(\()/g;
	OpenGroupToken.prototype.parse = function(str, options){
		var sign = this.sign;
		if ( sign ) { //if use "@(   )" will be parse as script scope and render as write() output
			this.renderWrapWrite = true;
		}
		
		var token = null;
		do {
			token = str.tokenize( true, options );
			if ( token ) {
				if ( sign && token.name === ')' )
					break;
				else this.add( token );
			}
		}while( token && token.name !== ')');
		
		if (!sign) {
			this.nextChild = [];
			var tokens = [ MemberOperatorToken, OpenBracketToken, OpenGroupToken ];
			var found = true;
			while( found ) {
				found = false;
				for( var i = 0; i < tokens.length; ++i ) {
					var token = tokens[i];
					if ( token.prototype.test( str, options ) ) {
						found = true;
						token = new token();
						token.exec( str, options )
						this.nextChild.push( token );
					}
				}
			}
		}
		
	}
	OpenGroupToken.prototype.render = function(buffer) { 
		if ( this.renderWrapWrite ) buffer.push('write(');
		//if (this.renderWrapWriteEscape) buffer.push('__escape(');
		
		if ( this.sign === false || this.nextChild && this.nextChild.length > 0 )
			buffer.push( this.value );
		
		if ( this.children.length > 0 ) {
			var len = this.children.length;
			for ( var i = 0; i < len; ++i ) {
				this.children[i].render( buffer );
			}
			
			if ( this.renderWrapWrite === false && this.renderWrapWriteEscape === false ) {
				if ( len > 0 && this.sign && this.children[ len - 1 ].value !== ';' )
					buffer.push(';');
			}
		}

		
		if ( this.nextChild && this.nextChild.length > 0 ) {
			var len = this.nextChild.length;
			for ( var i = 0; i < len; ++i ) {
				this.nextChild[i].render( buffer );
			}
			
			if ( this.renderWrapWrite === false && this.renderWrapWriteEscape === false ) {
				if ( len > 0 && this.sign && this.nextChild[ len - 1 ].value !== ';' )
					buffer.push(';');
			}
		}
		
		
		
		
		//if (this.renderWrapWriteEscape) buffer.push(')');
		if ( this.renderWrapWrite ) buffer.push(');');
	
		return buffer;
	}
	
	
	
	function CloseGroupToken(){}
	CloseGroupToken.prototype = new Token(')');
	CloseGroupToken.prototype.REGEX = /^\s*(\))/g;
	
	
	
	function OpenBlockToken(){}
	OpenBlockToken.prototype = new Token('{');
	OpenBlockToken.prototype.REGEX = /^\s*(\{)/g;
	OpenBlockToken.prototype.parse = function(str, options){
		var optScopt = options.scope;
		var optBlockScriptScope = options.blockScriptScope;
		var optInBlock = options.inBlock;
		options.inBlock = true;
		if (this.sign) options.blockScriptScope = true;
		
		if (this.sign || optBlockScriptScope){
			options.scope = SCOPE.SCRIPT;
		}else if (this.inFunction || this.inKeyword) {
			options.scope = SCOPE.AUTO;
		}else{ //object
			options.scope = SCOPE.SCRIPT;
		}
		
		var token = null;
		var lastBlockCount = 0;
		var scriptScope = options.scope == SCOPE.SCRIPT; 
		do {
			options.lastBlockCount = lastBlockCount;
			token = str.tokenize( scriptScope, options);
			if ( token ) {
				if (token.name === '}' )
					break;
				else {
					if ( token.name === 'TEXT' )
						lastBlockCount = token.blockCount;
					this.add( token );
				}
			}
		}while( token && token.name !== '}');
		delete options.lastBlockCount;
		
		if ( OpenBracketToken.prototype.test( str ) ) {
			var bracket = new OpenBracketToken();
			bracket.exec( str );
			this.add( bracket );
		}
		
		options.scope = optScopt;
		options.inBlock = optInBlock;
		options.blockScriptScope = optBlockScriptScope;
	}
	OpenBlockToken.prototype.render = function(buffer) { 
		if ( this.renderWrapWrite ) buffer.push('write(');
		if (this.renderWrapWriteEscape) buffer.push('__escape(');
		
		if ( this.sign === false || this.inKeyword ){
			buffer.push( this.value );
			buffer.push('\r\n');
		}
		if ( this.children.length > 0) {
			if ( this.renderInternalContext ) {
				buffer.push('var __out = new writer(), write = __out.write.bind(__out);');
			}
			
			var len = this.children.length;
			for ( var i = 0; i < len; ++i ) {
				this.children[i].render( buffer );
			}
			
			if ( this.renderWrapWrite === false && this.renderWrapWriteEscape === false ) {
				if ( len > 0 && this.sign && this.children[ len -1 ].value !== ';' )
					buffer.push(';');
			}
				
			if ( this.renderInternalContext ) {
				buffer.push('\r\nreturn __out.toString();');
			}
			
		}
		if ( this.sign === false  || this.inKeyword )
				buffer.push( '\r\n}' );
		if (this.renderWrapWriteEscape) buffer.push(')');
		if ( this.renderWrapWrite ) buffer.push(');');
		
		return buffer;
	}
	
	
	
	function CloseBlockToken(){}
	CloseBlockToken.prototype = new Token('}');
	CloseBlockToken.prototype.REGEX = /^\s*(\})/g;
	CloseBlockToken.prototype.renderNewLine = true;
	
	function OpenBracketToken(){}
	OpenBracketToken.prototype = new Token('[');
	OpenBracketToken.prototype.REGEX = /^\s*(\[)/g;
	OpenBracketToken.prototype.parse = function(str, options){
		//helper.skipWhitespace(str);
		var token = null;
		do {
			token = str.tokenize( true, options );
			if ( token )
				this.add( token );
		}while( token && token.name !== ']');
		
		var tokens = [ MemberOperatorToken, OpenBracketToken, OpenGroupToken ];
		var found = true;
		while( found ) {
			found = false;
			for( var i = 0; i < tokens.length; ++i ) {
				token = tokens[i];
				if ( token.prototype.test( str, options ) ) {
					found = true;
					token = new token();
					token.exec( str, options );
					this.add( token );
				}
			}
		}
	}
	
	function CloseBracketToken(){}
	CloseBracketToken.prototype = new Token(']');
	CloseBracketToken.prototype.REGEX = /^\s*(\])/g;
	 
	function QuoteToken(){}
	QuoteToken.prototype = new Token("'");
	QuoteToken.prototype.REGEX = /^\s*([\'\"])/g;
	QuoteToken.prototype.parse = function(str, options){
		var _str = helper.tokenizeString( str, this.value );
		var token = new TextToken();
		token.value = _str;
		this.add( token );
		
		var tokens =[
			 MemberOperatorToken, OpenBracketToken
		];
		for( var i = 0; i < tokens.length; ++i ) {
			token = tokens[i];
			if ( token.prototype.test( str, options ) ) {
				token = new token();
				token.exec( str, options );
				this.add( token );
				break;
			}
		}
		
		if ( this.sign ) {	
			this.renderWrapWrite = true;
			if ( SemicolonToken.prototype.test( str ) ) {
				var op = new SemicolonToken();
				op.exec( str ); //remove ; for inline write()
			}
		}
		
	}
	QuoteToken.prototype.render = function(buffer) {
		if ( this.sign ) {
			if ( this.renderWrapWrite ) buffer.push('write(');
			if (this.renderWrapWriteEscape) buffer.push('__escape(');
		}
		
		buffer.push( this.value );
		if ( this.children.length > 0 ) {
			this.children[0].render( buffer );
		}
		buffer.push( this.value );
		
		
		if ( this.children.length > 0 ) {
			for ( var i = 1; i < this.children.length; ++i ) {
				this.children[i].render( buffer );
			}
		}
	
		if ( this.sign ) {
			if (this.renderWrapWriteEscape) buffer.push(')');
			if ( this.renderWrapWrite ) buffer.push(');');
		}
		return buffer;
	}
	
	
	function FunctionToken(){}
	FunctionToken.prototype = new Token('FUNCTION');
	FunctionToken.prototype.REGEX = /^\s*(function)(?=(?:\s+([$A-Za-z_\x7f-\uffff][$\w\x7f-\uffff]*))?\s*\()/g;
	FunctionToken.prototype.renderParentChildSpace = true;
	FunctionToken.prototype.parse = function(str, options){
		
		if ( IdenfierToken.prototype.test( str, options ) ) {
			var idToken = new IdenfierToken();
			idToken.inFunction = true;
			idToken.inKeyword = true;
			idToken.exec( str, options );
			this.add( idToken );
		}else if ( OpenGroupToken.prototype.test( str, options ) ) {
			var group = new OpenGroupToken();
			group.inKeyword = true;
			group.inFunction = true;
			group.exec( str, options );
			this.add( group );
		}
		
		var scriptScope = false;
		if ( SignToken.prototype.test( str, options ) ) {
			scriptScope = true;
			var sign = new SignToken();
			sign.exec( str, options ); //remove
		}
		if(scriptScope === false) {
			scriptScope = options.blockScriptScope || false;
		}
		
		if ( OpenBlockToken.prototype.test( str, options ) ) {
			var block = new OpenBlockToken();
			block.sign = scriptScope;
			block.inFunction = true;
			block.inKeyword = true;
			block.exec( str, options );
			if ( scriptScope === false )
				block.renderInternalContext = true;
			this.add( block );
		}
	
		
	}
	FunctionToken.prototype.render = function(buffer) {
		buffer.push( this.value );
		buffer.push( ' ' );
		if ( this.children.length > 0 ) {
			for ( var i = 0; i < this.children.length; ++i ) {
				var child = this.children[i];
				child.render( buffer );
			}
		}
		
		return buffer;
	}
	
	
	
	function IfToken(){}
	IfToken.prototype = new Token('IF');
	IfToken.prototype.REGEX = /^\s*(if)(?=\s*\()/g;
	IfToken.prototype.parse = function(str, options){
		if ( OpenGroupToken.prototype.test( str, options ) ) {
			var group = new OpenGroupToken();
			group.inKeyword = true;
			group.exec( str, options );
			this.add( group );
			
			var scriptScope = false;
			if ( SignToken.prototype.test( str, options ) ) {
				scriptScope = true;
				var sign = new SignToken();
				sign.exec( str, options ); //remove
			}
			
			if ( OpenBlockToken.prototype.test( str, options )  ) {
				var block = new OpenBlockToken();
				block.sign = scriptScope;
				block.inKeyword = true;
				block.exec( str, options );
				this.add( block );
			}else{
				var token = null;
				var foundSemi = false;
				do {
					token = str.tokenize( true, options );
					if ( token && !( token.name === 'OP' && token.value === ';') ){
						token.inKeyword = true;
						this.add( token );
					}
					if (token && token.children.length && token.children[token.children.length - 1].value === ';') {
						foundSemi = true;
						break;
					}
				}while( token && !( token.name === 'OP' && token.value === ';') );
				if (foundSemi === false) {
					var op = new SemicolonToken();
					op.exec( str, options );
					op.value = ';'
					this.add( op );
				}
			}
			
			if ( ElseToken.prototype.test( str, options )  ) {
				var els = new ElseToken(false);
				els.inKeyWord = true;
				els.exec( str, options );
				this.add( els );
			}
		}
		
	}
	
	function ElseToken(){}
	ElseToken.prototype = new Token('ELSE');
	ElseToken.prototype.REGEX = /^\s*(else)(?=\s*)/g;
	ElseToken.prototype.renderParentChildSpace = true;
	ElseToken.prototype.parse = function(str, options){
		if ( IfToken.prototype.test( str, options ) ) {
			var ifToken = new IfToken();
			ifToken.inKeyword = true;
			ifToken.exec( str, options );
			this.add( ifToken );
		}else {
			var scriptScope = false;
			if ( SignToken.prototype.test( str, options ) ) {
				scriptScope = true;
				var sign = new SignToken();
				sign.exec( str, options ); //remove
			}
			if ( OpenBlockToken.prototype.test(str, options) ) { 
				var block = new OpenBlockToken();
				block.sign = scriptScope;
				block.inKeyword = true;
				block.exec( str, options );
				this.add(block);
			}else {
				var token = null;
				var foundSemi = false;
				do {
					token = str.tokenize( true, options );
					if ( token && !( token.name === 'OP' && token.value === ';') ){
						token.inKeyword = true;
						this.add( token );
					}
					if (token && token.children.length && token.children[token.children.length - 1].value === ';') {
						foundSemi = true;
						break;
					}
				}while( token && !( token.name === 'OP' && token.value === ';') );
				if (foundSemi === false) {
					var op = new SemicolonToken();
					op.exec( str, options );
					op.value = ';'
					this.add( op );
				}
			}
		}
	}
	
	
	function ForToken(){}
	ForToken.prototype = new Token('FOR');
	ForToken.prototype.REGEX = /^\s*(for)(?=\s*\()/g;
	ForToken.prototype.renderNewLine = true;
	ForToken.prototype.parse = function(str, options){
		if ( OpenGroupToken.prototype.test( str, options )  ) {
			var group = new OpenGroupToken();
			group.inKeyword = true;
			group.exec( str, options );
			this.add( group );
			
			var scriptScope = false;
			if (!scriptScope && SignToken.prototype.test( str, options ) ) {
				scriptScope = true;
				var sign = new SignToken();
				sign.exec( str, options ); //remove
			}
			if ( OpenBlockToken.prototype.test( str, options )  ) {
				var block = new OpenBlockToken();
				block.sign = scriptScope;
				block.inKeyword = true;
				block.exec( str, options );
				this.add( block );
			}else{
				var token = null;
				do {
					token = str.tokenize( true, options );
					if ( token && !( token.name === 'OP' && token.value === ';') ){
						token.inKeyword = true;
						this.add( token );
					}
				}while( token && !( token.name === 'OP' && token.value === ';') );
				var op = new SemicolonToken();
				op.exec( str, options );
				op.value = ';'
				this.add( op );
			}
		}
	}
	
	function WhileToken(){}
	WhileToken.prototype = new Token('WHILE');
	WhileToken.prototype.REGEX = /^\s*(while)(?=\s*\()/g;
	WhileToken.prototype.parse = function(str, options){
		if ( OpenGroupToken.prototype.test( str, options )  ) {
			var group = new OpenGroupToken();
			group.inKeyword = true;
			group.exec( str, options );
			this.add( group );
			
			var scriptScope = false;
			if ( SignToken.prototype.test( str, options ) ) {
				scriptScope = true;
				var sign = new SignToken();
				sign.exec( str, options ); //remove
			}
			if ( OpenBlockToken.prototype.test( str, options )  ) {
				var block = new OpenBlockToken();
				block.sign = scriptScope;
				block.inKeyword = true;
				block.exec( str, options );
				this.add( block );
			}else{
				var token = null;
				do {
					token = str.tokenize( true, options );
					if ( token && !( token.name === 'OP' && token.value === ';') ){
						token.inKeyword = true;
						this.add( token );
					}
				}while( token && !( token.name === 'OP' && token.value === ';') );
				var op = new SemicolonToken();
				op.exec( str, options );
				op.value = ';'
				this.add( op );
			}
		}
	}
	
	
	function DoWhileToken(){}
	DoWhileToken.prototype = new Token('DO');
	DoWhileToken.prototype.REGEX = /^\s*(do)(?=\s*\{)/g;
	DoWhileToken.prototype.parse = function(str, options){
		var scriptScope = false;
		if ( SignToken.prototype.test( str, options ) ) {
			scriptScope = true;
			var sign = new SignToken();
			sign.exec( str, options ); //remove
		}
		if ( OpenBlockToken.prototype.test( str, options )  ) {
			var block = new OpenBlockToken();
			block.sign = scriptScope;
			block.inKeyword = true;
			block.exec( str, options );
			this.add( block );
		}else{
			var token = null;
			do {
				token = str.tokenize( true, options );
				if ( token && !( token.name === 'OP' && token.value === ';') ){
					token.inKeyword = true;
					this.add( token );
				}
			}while( token && !( token.name === 'OP' && token.value === ';') );
			var op = new SemicolonToken();
			op.exec( str, options );
			op.value = ';'
			this.add( op );
		}
		
		if ( WhileToken.prototype.test( str, options )  ) {
			var whileToken = new WhileToken();
			whileToken.exec( str, options );
			this.add( whileToken );
			
			if ( whileToken.children[ whileToken.children.length - 1 ].value !== ';' ) {
				var op = new SemicolonToken();	
				op.value = ';'
				this.add( op );
			}
		}
		
		
		
	}
	
	
	
	function ContinueToken(){}
	ContinueToken.prototype = new Token('CONTINUE');
	ContinueToken.prototype.REGEX = /^(continue)/g;
	ContinueToken.prototype.parse = function(str, options){
		var op = new SemicolonToken();
		op.exec( str, options );
		op.value = ';'
		this.add( op );
	}
	
	
	function BreakToken(){}
	BreakToken.prototype = new Token('BREAK');
	BreakToken.prototype.REGEX = /^(break)/g;
	BreakToken.prototype.parse = function(str, options){
		var op = new SemicolonToken();
		op.exec( str, options );
		op.value = ';'
		this.add( op );
	}
	
	
	function OperatorToken(){}
	OperatorToken.prototype = new Token('OP');
	OperatorToken.prototype.REGEX = /^\s*(;|[\\+\-*\/^%=.,|&!<>:?]+)/g;
	
	function SemicolonToken(){}
	SemicolonToken.prototype = new Token(';');
	SemicolonToken.prototype.REGEX = /^\s*(;)/g;
	
	
	function WriteExpressionToken(){}
	WriteExpressionToken.prototype = new Token('@=');
	WriteExpressionToken.prototype.REGEX = /^(@:=|@=)/g;
	WriteExpressionToken.prototype.renderIgnoreParentValue = true;
	WriteExpressionToken.prototype.renderWrapWrite = true;
	WriteExpressionToken.prototype.preSign = true;
	WriteExpressionToken.prototype.parse = function(str, options) {
		if ( this.value[0] === ':' ) this.renderWrapWriteEscape = true;
		var token = null;
		do {
			
			if ( NewLineToken.prototype.test( str ) ) {
				break;
			}else{
				token = str.tokenize( true );
				if ( token && !( token.name === 'OP' && token.value === ';') )
					this.add( token );
			}		
		}while( token && !( token.name === 'OP' && token.value === ';') );
	}
	
	
	function VarToken(){}
	VarToken.prototype = new Token('VAR');
	VarToken.prototype.REGEX = /^\s*(var)(?=\s*)/g;
	VarToken.prototype.renderParentChildSpace = true;
	VarToken.prototype.parse = function(str, options){
		var token = null;
		do {
			
			if ( NewLineToken.prototype.test( str, options ) ) {
				var op = new SemicolonToken();
				op.exec( str, options );
				op.value = ';'
				this.add( op );
				break;
			}else if ( InToken.prototype.test( str, options ) ) {
				break;
			}else{
				token = str.tokenize( true, options );
				if ( token )
					this.add( token );
			}		
		}while( token && !( token.name === 'OP' && token.value === ';') );
	
	}
	
	
	function ReturnToken(){}
	ReturnToken.prototype = new Token('RETURN');
	ReturnToken.prototype.REGEX = /^\s*(return)(?=\s+|\s*\{|\s*\(|\s*;)/g;
	ReturnToken.prototype.renderParentChildSpace = true;
	ReturnToken.prototype.parse = function(str, options){
		var token = null;
		do {
			
			if ( NewLineToken.prototype.test( str, options ) ) {
				var op = new SemicolonToken();
				op.exec( str, options );
				op.value = ';'
				this.add( op );
				break;
			}else{
				token = str.tokenize( true, options );
				if ( token )
					this.add( token );
			}
		}while( token && !( token.name === 'OP' && token.value === ';') );
		
	}
	
	function WriteEscapeValueToken(){}
	WriteEscapeValueToken.prototype = new Token('ESCAPE');
	WriteEscapeValueToken.prototype.REGEX = /^(@\:)/g;
	WriteEscapeValueToken.prototype.renderWrapWrite = true;
	WriteEscapeValueToken.prototype.renderWrapWriteEscape = true;
	WriteEscapeValueToken.prototype.preSign = true;
	WriteEscapeValueToken.prototype.parse = function(str, options){
		options.sign = false;
		if ( IdenfierToken.prototype.test( str, options ) ) {
			var id = new IdenfierToken();
			id.exec( str, options );
			id.renderWrapWrite = false;
			id.renderWrapWriteEscape = false;
			this.value = "";
			this.add( id );
		}
		else if ( OpenGroupToken.prototype.test( str, options ) ) {
			var group = new OpenGroupToken();
			group.exec( str, options );
			group.renderWrapWrite = false;
			group.renderWrapWriteEscape = false;

			this.value = "";
			this.add( group );
		}else{
			if ( QuoteToken.prototype.test( str, options ) ) {
				var quote = new QuoteToken();
				quote.exec( str, options );
				this.value = "";
				this.add( quote );
			}
		}
		
		if ( SemicolonToken.prototype.test( str ) ) {
			var op = new SemicolonToken();
			op.exec( str ); //remove ; for inline write()
		}
		
	}
	
	
	function EscapeSignToken(){}
	EscapeSignToken.prototype = new Token('ESCAPE');
	EscapeSignToken.prototype.REGEX = /^(@)@/g;
	EscapeSignToken.prototype.renderWrapWrite = true;
	EscapeSignToken.prototype.renderQuote = true;
	EscapeSignToken.prototype.preSign = true;
	
	function NewLineToken(){}
	NewLineToken.prototype = new Token('NEWLINE');
	NewLineToken.prototype.REGEX = /^[ \t]*(\r\n|\n)/g;
	
	
	function AssignToken(){}
	AssignToken.prototype = new Token('=');
	AssignToken.prototype.REGEX = /^\s*([ +\-*\/%]*=)/g;
	
	
	function WithToken(){}
	WithToken.prototype = new Token('WITH');
	WithToken.prototype.REGEX = /^\s*(with)(?=\s*\()/g;
	WithToken.prototype.parse = function(str, options){
		if ( OpenGroupToken.prototype.test( str, options )  ) {
			var group = new OpenGroupToken();
			group.inKeyword = true;
			group.exec( str, options );
			this.add( group );
			
			var scriptScope = false;
			if ( SignToken.prototype.test( str, options ) ) {
				scriptScope = true;
				var sign = new SignToken();
				sign.exec( str, options ); //remove
			}
			
			if ( OpenBlockToken.prototype.exec( str, options )  ) {
				var block = new OpenBlockToken();
				block.sign = scriptScope;
				block.inKeyWord = true;
				block.exec( str, options );
				this.add( block );
			}else{
				var token = null;
				do {
					token = str.tokenize( true, options );
					if ( token && !( token.name === 'OP' && token.value === ';') ){
						token.inKeyword = true;
						this.add( token );
					}
				}while( token && !( token.name === 'OP' && token.value === ';') );
				var op = new SemicolonToken();
				op.exec( str, options );
				op.value = ';'
				this.add( op );
			}
		}
	}
	
	function SwitchToken(){}
	SwitchToken.prototype = new Token('SWITCH');
	SwitchToken.prototype.REGEX = /^\s*(switch)(?=\s*\()/g;
	SwitchToken.prototype.parse = function(str, options){
		if ( OpenGroupToken.prototype.test( str, options )  ) {
			var group = new OpenGroupToken();
			group.inKeyword = true;
			group.exec( str, options );
			this.add( group );
			
			var scriptScope = false;
			if ( SignToken.prototype.test( str, options ) ) {
				scriptScope = true;
				var sign = new SignToken();
				sign.exec( str, options ); //remove
			}
			
			if ( OpenBlockToken.prototype.test( str, options )  ) {
				var block = new OpenBlockToken();
				block.sign = scriptScope;
				block.inKeyword = true;
				block.exec( str, options );
				this.add( block );
			}else{
				var token = null;
				do {
					token = str.tokenize( true, options );
					if ( token && !( token.name === 'OP' && token.value === ';') ){
						token.inKeyword = true;
						this.add( token );
					}
				}while( token && !( token.name === 'OP' && token.value === ';') );
				var op = new SemicolonToken();
				op.exec( str, options );
				op.value = ';'
				this.add( op );
			}
		}
	}
	
	
	function CaseToken(){}
	CaseToken.prototype = new Token('CASE');
	CaseToken.prototype.REGEX = /^\s*(case)(?=\s+)/g;
	CaseToken.prototype.parse = function(str, options){
		do {
			var token = str.getToken(options);
			if ( token ) {
				if ( token.exec( str, options ) )
					this.add( token );
			}
		}while ( token && !( token.name === 'OP' && token.value === ':' ) )
		
		var scriptScope = false;
		if ( SignToken.prototype.test( str, options ) ) {
			scriptScope = true;
			var sign = new SignToken();
			sign.exec( str, options ); //remove
		}
		
		if ( OpenBlockToken.prototype.test( str, options ) ) {
			var block = new OpenBlockToken();
			block.sign = scriptScope;
			block.inKeyword = true;
			block.exec( str, options );
			this.add( block );
		}
		
		
	}
	CaseToken.prototype.render = function(buffer){
		buffer.push( ' ' );
		buffer.push( this.value );
		buffer.push( ' ' );
		
		if ( this.children.length > 0 )
			for( var i = 0; i < this.children.length; ++i )
				this.children[i].render(buffer);
				
		return buffer;
	}
	
	
	function DefaultToken(){}
	DefaultToken.prototype = new Token('DEFAULT');
	DefaultToken.prototype.REGEX = /^\s*(default)(?=\s*)/g;
	DefaultToken.prototype.parse = function(str, options){
		var scriptScope = false;
		if ( SignToken.prototype.test( str, options ) ) {
			scriptScope = true;
			var sign = new SignToken();
			sign.exec( str, options ); //remove
		}
		if ( OpenBlockToken.prototype.test( str, options ) ) {
			var block = new OpenBlockToken();
			block.sign = scriptScope;
			block.inKeyword = true;
			block.exec( str, options );
			this.add( block );
		}
		
	}
	DefaultToken.prototype.render = function(buffer){
		buffer.push( ' ' );
		buffer.push( this.value );
		buffer.push( ' ' );
		
		if ( this.children.length > 0 )
			for( var i = 0; i < this.children.length; ++i )
				this.children[i].render(buffer);
				
		return buffer;
	}
	
	
	function TryToken(){}
	TryToken.prototype = new Token('TRY');
	TryToken.prototype.REGEX = /^\s*(try)(?=\s*\{)/g;
	TryToken.prototype.parse = function(str, options){		
		var scriptScope = false;
		if ( SignToken.prototype.test( str, options ) ) {
			scriptScope = true;
			var sign = new SignToken();
			sign.exec( str, options ); //remove
		}
		if ( OpenBlockToken.prototype.test( str, options )  ) {
			var block = new OpenBlockToken();
			block.sign = scriptScope;
			block.inKeyword = true;
			block.exec( str, options );
			this.add( block );
		}
		
		var tokens = [CatchToken, FinallyToken];
		do {
			var found = false;
			for ( var i = 0; i < tokens.length; ++i ) {
				var token = tokens[i];
				if ( token.prototype.test( str, options ) ) {
					token = new token();
					token.exec( str, options );
					this.add( token );
					found = true;
				}
			}
		}while( found );
		
	}
	
	function CatchToken(){}
	CatchToken.prototype = new Token('CATCH');
	CatchToken.prototype.REGEX = /^\s*(catch)(?=\s*\(|\s*\{)/g;
	CatchToken.prototype.parse = function(str, options){
		if ( OpenGroupToken.prototype.test( str, options ) ) {
			var group = new OpenGroupToken();
			group.inKeyword = true;
			group.exec( str, options );
			this.add( group );
		}
		
		var scriptScope = false;
		if ( SignToken.prototype.test( str, options ) ) {
			scriptScope = true;
			var sign = new SignToken();
			sign.exec( str, options ); //remove
		}
		if ( OpenBlockToken.prototype.test( str, options )  ) {
			var block = new OpenBlockToken();
			block.sign = scriptScope;
			block.inKeyword = true;
			block.exec( str, options );
			this.add( block );
		}
	}
	
	
	function FinallyToken(){}
	FinallyToken.prototype = new Token('FINALLY');
	FinallyToken.prototype.REGEX = /^\s*(finally)(?=\s*\{)/g;
	FinallyToken.prototype.parse = function(str, options){
		var scriptScope = false;
		if ( SignToken.prototype.test( str, options ) ) {
			scriptScope = true;
			var sign = new SignToken();
			sign.exec( str, options ); //remove
		}
		if ( OpenBlockToken.prototype.test( str, options )  ) {
			var block = new OpenBlockToken();
			block.sign = scriptScope;
			block.inKeyword = true;
			block.exec( str, options );
			this.add( block );
		}
	}
	
	
	function InToken(){}
	InToken.prototype = new Token('IN');
	InToken.prototype.REGEX = /^\s+(in)(?=\s+)/g;
	InToken.prototype.render = function(buffer){
		buffer.push( ' ' );
		buffer.push( this.value );
		buffer.push( ' ' );
		return buffer;
	}
	
	function NewToken(){}
	NewToken.prototype = new Token('NEW');
	NewToken.prototype.REGEX = /^\s*(new)(?=\s+)/g;
	NewToken.prototype.parse = function(str, options){
		var token = str.getToken(options);
		if ( token && token.exec( str, options ) ) {
			this.add( token );
		}
	}
	NewToken.prototype.render = function(buffer){
		buffer.push( ' ' );
		buffer.push( this.value );
		buffer.push( ' ' );
		
		if ( this.children.length > 0 ) {
			for ( var i = 0; i < this.children.length; ++i ) {
				this.children[i].render( buffer );
			}
		}
		
		return buffer;
	}
	
	
	function InstanceofToken(){}
	InstanceofToken.prototype = new Token('INSTANCEOF');
	InstanceofToken.prototype.REGEX = /^\s*(instanceof)(?=\s+)/g;
	InstanceofToken.prototype.render = function(buffer){
		buffer.push( ' ' );
		buffer.push( this.value );
		buffer.push( ' ' );
		return buffer;
	}
	
	function DeleteToken(){}
	DeleteToken.prototype = new Token('DELETE');
	DeleteToken.prototype.REGEX = /^\s*(delete)(?=\s+)/g;
	DeleteToken.prototype.parse = function(str, options){
		var token = str.tokenize( true, options );
		if ( token ) this.add( token );
		
	
		var op = new SemicolonToken();
		op.exec( str, options );
		op.value = ';'
		this.add( op );
		
	}
	DeleteToken.prototype.render = function(buffer){
		buffer.push( ' ' );
		buffer.push( this.value );
		buffer.push( ' ' );
		
		if ( this.children.length > 0 ) {
			for ( var i = 0; i < this.children.length; ++i ) {
				this.children[i].render( buffer );
			}
		}
		
		return buffer;
	}
	
	
	
	function ThrowToken(){}
	ThrowToken.prototype = new Token('THROW');
	ThrowToken.prototype.REGEX = /^\s*(throw)(?=\s+|\s*\{|\s*\()/g;
	ThrowToken.prototype.renderParentChildSpace = true;
	ThrowToken.prototype.parse = function(str, options){
		var token = null;
		do {
			token = str.tokenize( true, options );
			if ( token )
				this.add( token );
		}while( token && !( token.name === 'OP' && token.value === ';') );
		
	}
	
	
	function TypeOfToken(){}
	TypeOfToken.prototype = new Token('TYPEOF');
	TypeOfToken.prototype.REGEX = /^\s*(typeof)(?=\s+)/g;
	TypeOfToken.prototype.parse = function(str, options){
		var token = str.getToken(options);
		if ( token && token.exec( str, options ) ) {
			this.add( token );
		}
	}
	TypeOfToken.prototype.render = function(buffer){
		buffer.push( ' ' );
		buffer.push( this.value );
		buffer.push( ' ' );
		
		if ( this.children.length > 0 ) {
			for ( var i = 0; i < this.children.length; ++i ) {
				this.children[i].render( buffer );
			}
		}
		
		return buffer;
	}
	
	
	function InLineCommentToken(){}
	InLineCommentToken.prototype = new Token('//');
	InLineCommentToken.prototype.REGEX = /^\s*(\/\/)([^\r\n]*)/g;
	InLineCommentToken.prototype.pre = function(str, options) {
		this.REGEX.lastIndex = 0;
		var regObj = str.exec( this.REGEX );
		if ( regObj ) {
			this.value = regObj[1];
			var text = new TextToken();
			text.value = regObj[2] + '\r\n';
			this.add( text );
			str.substr( regObj[0].length );
		}
		return !!regObj;
	}
	
	
	function BlockCommentToken(){}
	BlockCommentToken.prototype = new Token('/*');
	BlockCommentToken.prototype.REGEX = /^\s*(\/\*)/g;
	BlockCommentToken.prototype.pre = function(str) {
		this.REGEX.lastIndex = 0;
		var regObj = str.exec( this.REGEX );
		if ( regObj ) {
			this.value = regObj[1];
			str.substr( regObj[0].length );
			
			var text = new TextToken();
			text.value = helper.tokenizeComment( str );
			this.add( text );
		}
		return !!regObj;
	}
	BlockCommentToken.prototype.render = function(buffer) {
		buffer.push( this.value );
		if ( this.children.length > 0 ) {
			for ( var i = 0; i < this.children.length; ++i ) {
				this.children[i].render( buffer );
			}
		}
		buffer.push( '*/' );
		return buffer;
	}
	
	function DebuggerToken(){}
	DebuggerToken.prototype = new Token('debugger');
	DebuggerToken.prototype.REGEX = /^\s*(debugger)/g;
	DebuggerToken.prototype.parse = function(str, options){
		var op = new SemicolonToken();
		op.exec( str, options );
		op.value = ';'
		this.add( op );
		
	}
	
	function TextScopeToken(){}
	TextScopeToken.prototype = new Token('@<text>');
	TextScopeToken.prototype.REGEX = /^\s*(@<text>)(\r\n|\r|\n){0,1}/g;
	TextScopeToken.prototype.END_REGEX = /([\s\S]*?)<\/text>/g;
	TextScopeToken.prototype.renderWrapWrite = true;
	TextScopeToken.prototype.renderEscapeText = true;
	TextScopeToken.prototype.renderQuote = true;
	TextScopeToken.prototype.preSign = true;
	TextScopeToken.prototype.parse = function(str, options){
		this.END_REGEX.lastIndex = 0;
		var regObj = str.exec( this.END_REGEX );
		if ( regObj ) {
			this.value = regObj[1];
			str.substr( regObj[0].length );
		}
	}

	
	var Tokens = [
		  BlankScopeToken, FunctionToken, IfToken, ElseToken, ForToken
		, WhileToken, DoWhileToken,ContinueToken, VarToken, WithToken
		, SwitchToken, CaseToken, DefaultToken, BreakToken
		, TryToken, CatchToken, FinallyToken, InToken, NewToken
		, DeleteToken, ReturnToken, InstanceofToken, ThrowToken
		, TypeOfToken, DebuggerToken, TextScopeToken
		
		, EscapeSignToken, IdenfierToken, NumberToken, WriteExpressionToken, WriteEscapeValueToken
		
		, OpenGroupToken, CloseGroupToken, OpenBlockToken
		, CloseBlockToken, OpenBracketToken, CloseBracketToken
		, QuoteToken, InLineCommentToken, BlockCommentToken
		
		, OperatorToken
	];
	
	var ret = {
		SignToken : SignToken
		, FunctionToken : FunctionToken
		, IfToken : IfToken
		, ElseToken : ElseToken
		, ForToken : ForToken
		, WhileToken : WhileToken
		, DoWhileToken : DoWhileToken
		, IdenfierToken : IdenfierToken
		, NumberToken : NumberToken
		, OpenGroupToken : OpenGroupToken
		, CloseGroupToken : CloseGroupToken
		, OpenBlockToken : OpenBlockToken
		, CloseBlockToken : CloseBlockToken
		, OpenBracketToken : OpenBracketToken
		, CloseBracketToken : CloseBracketToken
		, QuoteToken : QuoteToken
		, OperatorToken : OperatorToken
		, TextToken : TextToken
		, VarToken : VarToken
		, WriteEscapeValueToken : WriteEscapeValueToken
		, EscapeSignToken : EscapeSignToken
		, WithToken : WithToken
		, SwitchToken : SwitchToken
		, CaseToken : CaseToken
		, TryToken : TryToken
		, CatchToken : CatchToken
		, FinallyToken : FinallyToken
		, ContinueToken : ContinueToken
		, SemicolonToken : SemicolonToken
		, InToken : InToken
		, NewToken : NewToken
		, DeleteToken : DeleteToken
		, BreakToken : BreakToken
		, DefaultToken : DefaultToken
		, ReturnToken : ReturnToken
		, InstanceofToken : InstanceofToken
		, ThrowToken : ThrowToken
		, TypeOfToken : TypeOfToken
		, InLineCommentToken : InLineCommentToken
		, BlockCommentToken : BlockCommentToken
		, NewLineToken : NewLineToken
		, AssignToken : AssignToken
		, WriteExpressionToken : WriteExpressionToken
		, BlankScopeToken : BlankScopeToken
		, DebuggerToken : DebuggerToken
		, TextScopeToken : TextScopeToken
		
		, SignCheckToken: SignCheckToken
		, Tokens: Tokens 
	};
	return ret;
})();
if (module !== void 0) {
	module.exports = Token;
}


var Tokenizer = (function(){
	var nptoken, helper, Tokens;
	if (module !== void 0) {
		nptoken = require('./token');
		helper = require('./helper');
		Tokens = nptoken.Tokens;
	}else{
		var nptoken = Token;
		var Tokens = Token.Tokens;
		var helper = Helper;
	}
	
	function StringProxy(str) {
		this.str = str;
		this.toString = function(){ return this.str; };
		this.substr = function(start, cnt){ 
			this.str = this.str.substr( start, cnt );
			return this;
		};
		this.charAt = function(index){ return this.str.charAt( index ); };
		this.test = function(regex){
			return regex.test( this.str );
		};
		this.exec = function(regex) {
			return regex.exec( this.str );
		};
		
		this.tokenize = function ( isScriptScope, options ){
			return Tokenizer.tokenize( this, isScriptScope, options );
		};
		
		this.getToken = function(options, preSign){
			return helper.getToken( Tokens, this, options , preSign || false);
		}
		
	}
	
	StringProxy.prototype.size = function(){
		return this.str.length;
	} 
	//Object.defineProperty (StringProxy.prototype, 'length', { get: function(){ return this.str.length; } } );
	
	var SignCheckToken = new nptoken.SignCheckToken();
	
	var Tokenizer = new (function(){
		this.tokenize = function (str, isScriptScope, options) {
			options = options || {};
			var success = false;
			var sign = SignCheckToken.test( str );
			if ( sign || isScriptScope ) {
				var	token = str.getToken( options, true );
				if (token == null) {
					SignCheckToken.exec( str );
					token = str.getToken( options, false );
					if (token) token.sign = sign;
				}
				if ( token && token.exec( str, options ) ) {
					return token;
				}
			}else{
				var inBlock = options?options.inBlock:false;
				if ( inBlock ) {
					var closeBlockToken = new nptoken.CloseBlockToken();
					if ( closeBlockToken.exec( str, options ) ) {
						return closeBlockToken;
					}
				}
			
				var textToken = new nptoken.TextToken( inBlock );
				if ( textToken.exec(str, options) ){
					textToken.renderQuote = true;
					textToken.renderEscapeText = true;
					textToken.renderWrapWrite = true;
					textToken.renderNewLine = true;
					return textToken;
				}
			
			}
		
			return null;
		}
		
		
		
		
		this.parse = function(str){
			var tokens = [];
			var str = new StringProxy(str);
			while ( str.size() ) {
				var token = this.tokenize( str );
				if ( token ) {
					tokens.push( token );
				}
			}
			return tokens;
		}
	})();
	return Tokenizer;
})();

if (module !== void 0) {
	module.exports = Tokenizer;
}

var Writer = (function(){
	
	function Writer(options) {
		this.options = options;
		this.buffer = [];
		this.parameters = options?(options.params || []) : [];
	}

	Writer.prototype.write = function(tokens) {
		var begin = 0;
		//find parameter
		for( var i = 0; i < tokens.length; ++i ) {
			if ( tokens[i].name !== 'TEXT' ) { //check only first script token
				if ( tokens[i].name === '(' ) {
					var children = tokens[i].children;
					if ( children ) {
						var params = [];
						for ( var j = 0; j < children.length; ++j ) {
							if ( children[j].value === ',' || children[j].value === ')' ) continue;
							if ( children[j].name === 'ID' && children[j].children.length === 0 ) { //must be ID and no child
								params.push( children[j].value );
							}else {
								params = [];
								break;
							}
						}//for
						this.parameters = this.parameters.concat( params );
						if (params.length ) begin = i + 1;
					}//if
				}//if
				break;
			}
		}
		
		//render
		for( var i = begin; i < tokens.length; ++i ) {
			tokens[i].render( this.buffer );
			this.buffer.push('\r\n');
		}
		return this.buffer;
	};
	Writer.prototype.source = function(context) {
		var ctx, k, v;
	    if (context == null) {
	      context = {};
	    }
	    ctx = [];
	    for (k in context) {
	      v = context[k];
	      ctx.push(',');
	      ctx.push(k);
	      ctx.push('=this.');
	      ctx.push(k)
	      if (typeof v === 'function')
	    	  ctx.push('.bind(this)');
	    }
	    
	    var code = [
			  	  'var __out= new this.writer()', ctx.join(''), ';'
			  	,  'var write = __out.write.bind(__out);'
			  	,  this.buffer.join('')
		  	].join('');
		 
	    if ( this.options && this.options.beautify  ) {
			try{
				var jsp = require("uglify-js").parser;
				var pro = require("uglify-js").uglify;
				
			    var ast  = jsp.parse( code );
				code = pro.gen_code(ast, { beautify:true });
			}catch(e) {
				this.options.code = code;
				throw e;
			}
		}
		return code + '\r\n return __out.toString();';
	}
	
	return Writer;
})();

if (module !== void 0) {
	module.exports = Writer;
}

var NPRazor = (function(){
	var fs, nptokenizer, writer, helper;
	if (module !== void 0) {
		fs = require('fs');
		nptokenizer = require('./tokenizer');
		writer = require('./writer');
		helper = require('./helper');
	}else{
		nptokenizer = Tokenizer;
		helper = Helper;
		writer = Writer;
		
	}
	
	var __slice = [].slice;
	var cache = {};
	
	//function defaults from Bliss
	function defaults() {
		var k, object, objects, result, v, _i, _len, _ref;
		objects = 1 <= arguments.length ? __slice.call(arguments, 0) : [];
		result = {};
		for (_i = 0, _len = objects.length; _i < _len; _i++) {
			object = objects[_i];
			if (object != null) {
				for (k in object) {
					v = object[k];
					if ((_ref = result[k]) == null) {
						result[k] = v;
					}
				}
			}
		}
		return result;
	};
	
	function NPRazor(options){
		this.options = options;
	    this.cache = {};
	    this.options = defaults(this.options, {
	      ext: '.js.html',
	      cacheEnabled: true,
	      context: {},
	      debug: false,
	      params: []
	    });
	}
	NPRazor.prototype.disabledWrite = function(value){
		helper.disabledWrite( value );
	};
	
	NPRazor.prototype.prepareContext = function(context){
		if( ! context['writer'] ) {
			context['writer'] = helper.writer;
		}
		if( ! context['__escape'] ) {
			context['__escape'] = helper.htmlEscape;
		}
		
	}
	
	NPRazor.prototype.compile = function(source, options){
		var func, tmpl;
		options = defaults( options, this.options );
		var _writer = new writer(options);
		this.prepareContext( options.context );
		try {
			_writer.write( nptokenizer.parse( source ) );
			var retSource = _writer.source(options.context);
			
			func = new Function( _writer.parameters, retSource );
			tmpl = func.bind( options.context );
			tmpl.toString = func.toString.bind(func);
			tmpl.filepath = options.filepath;
			if ( options.debug ) {	
				console.log('------------------------Log--------------------------');
				console.log( tmpl.toString() );
				console.log('------------------------Log--------------------------');
			}
		} catch (error) {
			error.code = retSource || options.code;
			if ( options.debug ) {	
				console.error('--------------------Error----------------------------');
				console.error('Template: ' + options.filename);
				console.error( retSource || options.code );
				console.error('--------------------Error----------------------------');
				console.error( error.toString() );
				console.error( error.stack );
			}
			throw error;
		}
		return tmpl;
	};

	NPRazor.prototype.compileFile = function(filename, options) {
		var p = null, filepath, stat = null;
		options = defaults(options, this.options, {
	    	filename: filename,
	    	ext: (p = filename.indexOf('.')) >= 0 ? filename.slice(p) : ''
	    });
	    
	    filepath = filename;
	    options.filepath = filepath;
	    if ( options.cacheEnabled ) {
	    	if ( filepath in _NPRazor.cache )
	    		return _NPRazor.cache[ filepath ];
	    }
	    
		try {
			stat = fs.statSync(filepath);
		} catch (thrown) {
			try {
				filepath = filepath + options.ext;
				stat = fs.statSync(filepath);
			} catch (thrown) {
				throw thrown;
			}
		}
		if ( options.debug )
			console.log( "Load template: " + filepath );
		var source = fs.readFileSync(filepath, 'utf8');
		if (options.raw) {
			return source;
		}else{
		    var output = this.compile(source, options);
		    if ( options.cacheEnabled ) {
		    	_NPRazor.cache[ filepath ] = output;
		    }
		    return output;
		}
	}
	
	NPRazor.cache = {};
	NPRazor.helper = helper;
	return NPRazor;
})();
if (module !== void 0) {
	module.exports = NPRazor;
}else{
	if ( jQuery ) {
		var __slice = [].slice;
		jQuery.fn.renderTo = function(id) {
			if( !NPRazor ) return;
			var _self = $( this );
			if ( ! _self.data('template') ) {
				var render = new NPRazor();
				_self.data('template', render.compile( _self.html() ) );
			}
			var fn = _self.data('template');
			var text = fn.apply( null, __slice.call( arguments, 1 ) );
			$(id).html( text );
		};
		jQuery.fn.render = function() {
			if( !NPRazor ) return;
			var _self = $( this );
			if ( ! _self.data('template') ) {
				var render = new NPRazor();
				_self.data('template', render.compile( _self.html() ) );
			}
			var fn = _self.data('template');
			var text = fn.apply( null, arguments);
			return text;
		};
		jQuery.fn.renderAppendTo = function(id) {
			if( !NPRazor ) return;
			var _self = $( this );
			if ( ! _self.data('template') ) {
				var render = new NPRazor();
				_self.data('template', render.compile( _self.html() ) );
			}
			var fn = _self.data('template');
			var text = fn.apply( null, __slice.call( arguments, 1 ) );
			$(id).append( text );
		};
	}
}

