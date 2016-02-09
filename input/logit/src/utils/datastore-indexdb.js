var LogIt = LogIt || {};
(function(){
	
	var db;
	var version = 1;
	
	var init = function(){
		var request = indexedDB.open("LogIt", version);
		request.onsuccess = function(e) {
			db = e.target.result;
			console.log('DB ready');
			fetchAll();
		};
	
		request.onupgradeneeded = function(e) {
			var db = e.target.result;
			if(db.objectStoreNames.contains("LogIt")) {
				db.deleteObjectStore("LogIt");
			}
			db.createObjectStore("LogIt", {keyPath: "timestamp"});
		};
	};
	
	var sync = function(method, obj, callback, context){
		var trans = db.transaction(["LogIt"], "readwrite");
		var store = trans.objectStore("LogIt");
		var r;
		switch (method){
			case 'put':
				r = store.add(obj);
				break;
			case 'get':
				r = store.get(obj);
				break;
			case 'post':
				r = store.put(obj);
				break;
			case 'remove':
				r = store.delete(obj);
				break;
		}
		r.onsuccess = function(e) {
			console.log(e.target.result);
			callback.apply(context, [e.target.result]);
			LogIt.ds.load();
		};
		r.onerror = function(e) {
			console.log(e.value);
		};
	};
	
	var fetchAll = function(callback){
		var trans = db.transaction(["LogIt"], "readwrite");
		var store = trans.objectStore("LogIt");
		var r;
		var keyRange = IDBKeyRange.lowerBound(0);
		var cursorRequest = store.openCursor(keyRange);
		
		var items = [];
		cursorRequest.onsuccess = function(e) {
			var result = e.target.result;
			if (result){
				items.push(result.value);
				result.continue();
			} else {
				LogIt.Tasks.reset(items);
			}
		};
	};
	
	LogIt.ds = {
		load: fetchAll,
		sync: sync,
		init: init
	};
})();