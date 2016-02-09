MockGeolocation
=============

MockGeolocation is a library for mocking the html 5 geolocation api.

## Usage


    // simulate a geolocation
    MockGeolocation.setCurrentLatLng(53.4152431, -8.2390307)
    
    // get current position
    navigator.geolocation.getCurrentPosition(function (location) {
      alert('lat: ' + location.coords.latitude + ', lng: ' + location.coords.longitude)
    })
    
    // watch current position
    var watch = navigator.geolocation.watchPosition(function (location) {
      alert('lat: ' + location.coords.latitude + ', lng: ' + location.coords.longitude)
    })
    
    // simulate a geolocation change
    MockGeolocation.setCurrentLatLng(51.1642292, 10.4541193)
    
    // clear the watch
    navigator.geolocation.clearWatch(watch)
    
    // simulate a geolocation error
    MockGeolocation.setCurrentError(MockGeolocation.TIMEOUT)
    
    navigator.geolocation.getCurrentPosition(null, function (error) {
      alert('error: ' + error.message)
    })
    


## Download

Download the latest, minified version at [http://rawgit.com/srizzo/mockgeolocation/master/dist/mockgeolocation.min.js](http://rawgit.com/srizzo/mockgeolocation/master/dist/mockgeolocation.min.js).

