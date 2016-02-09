/*! MockGeolocation - v0.0.1 - 2014-12-07
* https://github.com/srizzo/mockgeolocation
* Copyright (c) 2014 Samuel Rizzo; Licensed MIT */
window.MockGeolocation = {
  
  PERMISSION_DENIED: { 
    PERMISSION_DENIED: 1,
    POSITION_UNAVAILABLE: 2,
    TIMEOUT: 3,
    code: 1, 
    message: "Failed with error PERMISSION_DENIED" 
  },
  
  POSITION_UNAVAILABLE: { 
    PERMISSION_DENIED: 1,
    POSITION_UNAVAILABLE: 2,
    TIMEOUT: 3,
    code: 2, 
    message: "Failed with error POSITION_UNAVAILABLE" 
  },
  
  TIMEOUT: { 
    PERMISSION_DENIED: 1,
    POSITION_UNAVAILABLE: 2,
    TIMEOUT: 3,
    code: 3, 
    message: "Failed with error TIMEOUT" 
  },
  
  _SUCCESS: 0,
  _ERROR: 1,
  
  _watchers: [],
  _currentError: null,
  _currentPosition: null,
  
  _askedForCurrentPositionBeforeInitialized: [],
  
  
  _reset: function () {
    this._currentError = null
    this._currentPosition = null
    this._askedForCurrentPositionBeforeInitialized = []
    this._watchers = []
  },
  
  _wasInitialized: function () {
    return this._currentError != null || this._currentPosition != null
  },
  
  _asyncExec: function (callback, args) {
    setTimeout(function () {
      callback(args)
    }, 0);
  },
  
  _execCallback: function (errorSuccessArray) {
    if (this._currentError && errorSuccessArray[this._ERROR]) {
      this._asyncExec(errorSuccessArray[this._ERROR], this._currentError)
    } else if (this._currentPosition && errorSuccessArray[this._SUCCESS]) {
      this._asyncExec(errorSuccessArray[this._SUCCESS], this._currentPosition)
    }
  },
  
  _callbackWatchers: function () {
    for (var i = 0; i < this._askedForCurrentPositionBeforeInitialized.length; i++) {
      this._execCallback(this._askedForCurrentPositionBeforeInitialized[i])
    }
    this._askedForCurrentPositionBeforeInitialized = []
    
    for (var j = 0; j < this._watchers.length; j++) {
      if (this._watchers[j])
        this._execCallback(this._watchers[j])
    }
  },
  
  setCurrentError: function (currentError) {
    this._currentPosition = null
    this._currentError = currentError
    if (currentError)
      this._callbackWatchers()
  },
  
  setCurrentPosition: function (currentPosition) {
    this._currentError = null
    this._currentPosition = currentPosition
    if (currentPosition)
      this._callbackWatchers()
  },
  
  setCurrentLatLng: function (lat, lng) {
    this.setCurrentPosition({
      coords: {
        latitude: lat,
        longitude: lng,
        accuracy: 65
      }
    })
  }
}

if (!navigator.geolocation) {
  window.navigator.geolocation = {}
}  
navigator.geolocation.getCurrentPosition = function(success, error) {
  if (MockGeolocation._wasInitialized()) {
    MockGeolocation._execCallback([success, error])
  } else {
    MockGeolocation._askedForCurrentPositionBeforeInitialized.push([success, error])
  }
}
navigator.geolocation.watchPosition = function(success, error) {
  if (MockGeolocation._wasInitialized()) {
    MockGeolocation._execCallback([success, error])
  }
  return MockGeolocation._watchers.push([success, error]) - 1
}
navigator.geolocation.clearWatch = function(id) {
  MockGeolocation._watchers[id] = null
}
