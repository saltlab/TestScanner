Geolocations = {
  IRELAND: {
    coords: {
      latitude: 53.4152431,
      longitude: -8.2390307,
      accuracy: 65
    }
  },
  GERMANY: {
    coords: {
      latitude: 51.1642292,
      longitude: 10.4541193,
      accuracy: 65
    }
  },
  UNITED_STATES: {
    coords: {
      latitude: 37.6,
      longitude: -95.665,
      accuracy: 65
    }
  }
}


QUnit.extend(QUnit.assert, {
  currentGeolocationEquals: function(expected){
    var assert = this.test.assert
    var done = assert.async()
    navigator.geolocation.getCurrentPosition(function (actual) {
      assert.deepEqual( expected, actual )
      done()
    })
  }
});

QUnit.extend(QUnit.assert, {
  currentGeolocationErrorEquals: function(expected){
    var assert = this.test.assert
    var done = assert.async()
    navigator.geolocation.getCurrentPosition(null, function (error) {
      assert.equal( error, MockGeolocation.PERMISSION_DENIED )
      done()
    })
  }
});


QUnit.testStart(function( details ) {
  MockGeolocation._reset()
})

QUnit.module( "navigator.geolocation.getCurrentPosition" )

QUnit.test( "is triggered by first call to setCurrentPosition", function( assert ) {
  assert.currentGeolocationEquals(Geolocations.IRELAND)
  MockGeolocation.setCurrentPosition(Geolocations.IRELAND)
})

QUnit.test( "remembers previously called setCurrentPosition", function( assert ) {
  MockGeolocation.setCurrentPosition(Geolocations.IRELAND)
  assert.currentGeolocationEquals(Geolocations.IRELAND)
  assert.currentGeolocationEquals(Geolocations.IRELAND)
})

QUnit.test( "setCurrentLatLng shortcuts to setCurrentPosition with default accuracy", function( assert ) {
  MockGeolocation.setCurrentLatLng(Geolocations.IRELAND.coords.latitude, Geolocations.IRELAND.coords.longitude)
  assert.currentGeolocationEquals(Geolocations.IRELAND)
})

QUnit.test( "is triggered by first call to setCurrentError", function( assert ) {
  MockGeolocation.setCurrentError(MockGeolocation.PERMISSION_DENIED)
  assert.currentGeolocationErrorEquals(MockGeolocation.PERMISSION_DENIED)  
  assert.currentGeolocationErrorEquals(MockGeolocation.PERMISSION_DENIED)  
})

QUnit.test( "remembers previously called setCurrentError", function( assert ) {
  assert.currentGeolocationErrorEquals(MockGeolocation.PERMISSION_DENIED)  
  assert.currentGeolocationErrorEquals(MockGeolocation.PERMISSION_DENIED)  
  MockGeolocation.setCurrentError(MockGeolocation.PERMISSION_DENIED)
})

QUnit.module( "navigator.geolocation.watchPosition" )

QUnit.test( "is triggered by each call to setCurrentPosition", function( assert ) {
  var done = {}
  positions = [Geolocations.IRELAND, Geolocations.GERMANY, Geolocations.UNITED_STATES]
  dones = [assert.async(), assert.async(), assert.async()] 
  
  navigator.geolocation.watchPosition(function (position) {
    assert.ok(true, "called")
    dones[positions.indexOf(position)]()
  })
  MockGeolocation.setCurrentPosition(Geolocations.IRELAND)
  MockGeolocation.setCurrentPosition(Geolocations.GERMANY)
  MockGeolocation.setCurrentPosition(Geolocations.UNITED_STATES)
})

QUnit.test( "remembers previously called setCurrentPosition", function( assert ) {
  var done = assert.async()
  MockGeolocation.setCurrentPosition(Geolocations.IRELAND)
  navigator.geolocation.watchPosition(function (actual) {
    assert.currentGeolocationEquals(actual, Geolocations.IRELAND)
    done()
  })
})


QUnit.module( "navigator.geolocation.clearWatch" )
QUnit.test( "clears watch", function( assert ) {
  var done = assert.async()
  var watch = navigator.geolocation.watchPosition(function (actual) {
    assert.currentGeolocationEquals(actual, Geolocations.IRELAND)
    done()
  })
  MockGeolocation.setCurrentPosition(Geolocations.IRELAND)
  navigator.geolocation.clearWatch(watch)
  MockGeolocation.setCurrentPosition(Geolocations.IRELAND)
})


