var webStorage = (function(localStorage, sessionStorage) {
    var expires = "__expires__";
    var dependencies = "__dependencies__";

    var clear = function(storage) {
        storage.clear();
    };
    var getItem = function(storage, key, reviver) {
        var item = storage.getItem(key);
        if(!item) {
            return null;
        }
        item = JSON.parse(item, function (key, value) {
            //Note that when reviving a data object, any containing expires and dependencies property is not revived.
            if(reviver && key !== expires && key !== dependencies) {
                reviver(key, value);
            }
            if(key === expires) {
                return new Date(value);
            }
            return value;
        });
        if(item[expires] < new Date() || (item[dependencies] && !dependenciesAvailable(storage, item[dependencies]))) {
            removeItem(storage, key);
            return null;
        }

        return item.data;
    };
    var key = function(storage, index) {
        //TODO: Check expires and dependencies.
        return storage.key(index);
    };
    var removeItem = function(storage, key) {
        //When removing an item, actually we could also remove items that depend on it.
        //However, this would require us to JSON.parse the full collection of items in storage,
        //which is a route I'm not willing to take, unless it is required by lack of web storage space.
        storage.removeItem(key);
    };
    var setItem = function(storage, key, data, options) {
        if(!options || Object.prototype.toString.call(options.expires) !== "[object Date]" ||
            options.expires < new Date()) {
            return;
        }
        if(options.dependencies && (Object.prototype.toString.call(options.dependencies) !== "[object Array]" ||
            !dependenciesAvailable(storage, options.dependencies))) {
            return;
        }
        var item = { data: data };
        item[expires] = options.expires;
        item[dependencies] = options.dependencies;
        //TODO: Handle exceptions.
        storage.setItem(key, JSON.stringify(item));
    };
    var dependenciesAvailable = function(storage, dependencies) {
        for(var i = 0; i < dependencies.length; i++) {
            if(getItem(storage, dependencies[i]) === null) {
                return false;
            }
        }

        return true;
    };

    var local = {
        clear: function() {
            clear(localStorage);
        },
        getItem: function(key, reviver) {
            return getItem(localStorage, key, reviver);
        },
        key: function(index) {
            return key(localStorage, index);
        },
        removeItem: function(key) {
            removeItem(localStorage, key);
        },
        setItem: function(key, data, options) {
            setItem(localStorage, key, data, options);
        }
    };

    var session = {
        clear: function() {
            clear(sessionStorage);
        },
        getItem: function(key, reviver) {
            return getItem(sessionStorage, key, reviver);
        },
        key: function(index) {
            return key(sessionStorage, index);
        },
        removeItem: function(key) {
            removeItem(sessionStorage, key);
        },
        setItem: function(key, data, options) {
            setItem(sessionStorage, key, data, options);
        }
    };

    return {
        local: local,
        session: session
    }
}(window.localStorage, window.sessionStorage));