var LogIt = LogIt || {};
(function(){
	'use strict';
	
	var props = ['dateFormat', 'dayHour', 'weekView'];
	
	var defaults = {
		dateFormat : 'dateFirst',
		dayHour: 8,
		weekView : 5
	};
	
	var values = {
	};
	
	var init = function(callback){
		if (storageAvailable()){
			chrome.storage.local.get(props, function(items){
				for(var key in items){
					values[key] = items[key];
				}
				callback();
			});
		} else {
			callback();
		}
	};
	
	var get = function(key){
		var val;
		if (key === null){
			return null;
		}
		
		//First, get from local storage
		val = values[key];
		//If not found, use default.
		if (typeof val === 'undefined'){
			val = defaults[key];
		}
		
		return val;
	};
	
	var set = function(obj){
		for (var key in obj){
			values[key] = obj[key];
		}
		if (storageAvailable()){
			chrome.storage.local.set(obj);
		}
	};
	
	var dateFormat = function(options){
		var formats = {
			dateFirst : 'dd MMM yyyy',
			monthFirst: 'MMM dd yyyy'
		};
		var form = get('dateFormat');
		return formats[form];
	};
	
	var weekView = function(){
		return get('weekView');
	};

	var dayHour = function(){
		return get('dayHour');
	};
	
	var storageAvailable = function(){
		return window.chrome && window.chrome.storage;
	};
	
	LogIt.Settings = {
		dateFormat: dateFormat,
		weekView: weekView,
		dayHour: dayHour,
		save: set,
		init: init
	};
	
	Object.freeze(LogIt.Settings);
	
})();