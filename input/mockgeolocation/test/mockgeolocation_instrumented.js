mockgeolocation_funcionCallTrace = [];
function mockgeolocation_funcionCallWrapper(functionName, testFunction, functionBody) {
  mockgeolocation_funcionCallTrace.push({functionName: functionName, testFunction: testFunction});
  return functionBody;
}
function mockgeolocation_getFuncionCallTrace() {
  return mockgeolocation_funcionCallTrace;
}
Geolocations = {IRELAND: {coords: {latitude: 53.4152431, longitude: -8.2390307, accuracy: 65}}, GERMANY: {coords: {latitude: 51.1642292, longitude: 10.4541193, accuracy: 65}}, UNITED_STATES: {coords: {latitude: 37.6, longitude: -95.665, accuracy: 65}}};
mockgeolocation_funcionCallWrapper("extend", "mockgeolocation_", QUnit.extend(QUnit.assert, {currentGeolocationEquals: function(expected) {
  var assert = this.test.assert;
  var done = mockgeolocation_funcionCallWrapper("async", "mockgeolocation_", assert.async());
  mockgeolocation_funcionCallWrapper("getCurrentPosition", "mockgeolocation_", navigator.geolocation.getCurrentPosition(function(actual) {
  assert.deepEqual(expected, actual);
  mockgeolocation_funcionCallWrapper("done", "mockgeolocation_", done());
}));
}}));
mockgeolocation_funcionCallWrapper("extend", "mockgeolocation_", QUnit.extend(QUnit.assert, {currentGeolocationErrorEquals: function(expected) {
  var assert = this.test.assert;
  var done = mockgeolocation_funcionCallWrapper("async", "mockgeolocation_", assert.async());
  mockgeolocation_funcionCallWrapper("getCurrentPosition", "mockgeolocation_", navigator.geolocation.getCurrentPosition(null, function(error) {
  assert.equal(error, MockGeolocation.PERMISSION_DENIED);
  mockgeolocation_funcionCallWrapper("done", "mockgeolocation_", done());
}));
}}));
mockgeolocation_funcionCallWrapper("testStart", "mockgeolocation_", QUnit.testStart(function(details) {
  mockgeolocation_funcionCallWrapper("_reset", "mockgeolocation_", MockGeolocation._reset());
}));
QUnit.module("navigator.geolocation.getCurrentPosition");
QUnit.test("is triggered by first call to setCurrentPosition", function(assert) {
  mockgeolocation_funcionCallWrapper("currentGeolocationEquals", "mockgeolocation_Test1", assert.currentGeolocationEquals(Geolocations.IRELAND));
  mockgeolocation_funcionCallWrapper("setCurrentPosition", "mockgeolocation_Test1", MockGeolocation.setCurrentPosition(Geolocations.IRELAND));
});
QUnit.test("remembers previously called setCurrentPosition", function(assert) {
  mockgeolocation_funcionCallWrapper("setCurrentPosition", "mockgeolocation_Test2", MockGeolocation.setCurrentPosition(Geolocations.IRELAND));
  mockgeolocation_funcionCallWrapper("currentGeolocationEquals", "mockgeolocation_Test2", assert.currentGeolocationEquals(Geolocations.IRELAND));
  mockgeolocation_funcionCallWrapper("currentGeolocationEquals", "mockgeolocation_Test2", assert.currentGeolocationEquals(Geolocations.IRELAND));
});
QUnit.test("setCurrentLatLng shortcuts to setCurrentPosition with default accuracy", function(assert) {
  mockgeolocation_funcionCallWrapper("setCurrentLatLng", "mockgeolocation_Test3", MockGeolocation.setCurrentLatLng(Geolocations.IRELAND.coords.latitude, Geolocations.IRELAND.coords.longitude));
  mockgeolocation_funcionCallWrapper("currentGeolocationEquals", "mockgeolocation_Test3", assert.currentGeolocationEquals(Geolocations.IRELAND));
});
QUnit.test("is triggered by first call to setCurrentError", function(assert) {
  mockgeolocation_funcionCallWrapper("setCurrentError", "mockgeolocation_Test4", MockGeolocation.setCurrentError(MockGeolocation.PERMISSION_DENIED));
  mockgeolocation_funcionCallWrapper("currentGeolocationErrorEquals", "mockgeolocation_Test4", assert.currentGeolocationErrorEquals(MockGeolocation.PERMISSION_DENIED));
  mockgeolocation_funcionCallWrapper("currentGeolocationErrorEquals", "mockgeolocation_Test4", assert.currentGeolocationErrorEquals(MockGeolocation.PERMISSION_DENIED));
});
QUnit.test("remembers previously called setCurrentError", function(assert) {
  mockgeolocation_funcionCallWrapper("currentGeolocationErrorEquals", "mockgeolocation_Test5", assert.currentGeolocationErrorEquals(MockGeolocation.PERMISSION_DENIED));
  mockgeolocation_funcionCallWrapper("currentGeolocationErrorEquals", "mockgeolocation_Test5", assert.currentGeolocationErrorEquals(MockGeolocation.PERMISSION_DENIED));
  mockgeolocation_funcionCallWrapper("setCurrentError", "mockgeolocation_Test5", MockGeolocation.setCurrentError(MockGeolocation.PERMISSION_DENIED));
});
QUnit.module("navigator.geolocation.watchPosition");
QUnit.test("is triggered by each call to setCurrentPosition", function(assert) {
  var done = {};
  positions = [Geolocations.IRELAND, Geolocations.GERMANY, Geolocations.UNITED_STATES];
  dones = [mockgeolocation_funcionCallWrapper("async", "mockgeolocation_Test6", assert.async()), assert.async(), assert.async()];
  mockgeolocation_funcionCallWrapper("watchPosition", "mockgeolocation_Test6", navigator.geolocation.watchPosition(function(position) {
  assert.ok(true, "called");
  mockgeolocation_funcionCallWrapper("indexOf(position)]", "mockgeolocation_Test6", dones[mockgeolocation_funcionCallWrapper("indexOf", "mockgeolocation_Test6", positions.indexOf(position))]());
}));
  mockgeolocation_funcionCallWrapper("setCurrentPosition", "mockgeolocation_Test6", MockGeolocation.setCurrentPosition(Geolocations.IRELAND));
  mockgeolocation_funcionCallWrapper("setCurrentPosition", "mockgeolocation_Test6", MockGeolocation.setCurrentPosition(Geolocations.GERMANY));
  mockgeolocation_funcionCallWrapper("setCurrentPosition", "mockgeolocation_Test6", MockGeolocation.setCurrentPosition(Geolocations.UNITED_STATES));
});
QUnit.test("remembers previously called setCurrentPosition", function(assert) {
  var done = mockgeolocation_funcionCallWrapper("async", "mockgeolocation_Test7", assert.async());
  mockgeolocation_funcionCallWrapper("setCurrentPosition", "mockgeolocation_Test7", MockGeolocation.setCurrentPosition(Geolocations.IRELAND));
  mockgeolocation_funcionCallWrapper("watchPosition", "mockgeolocation_Test7", navigator.geolocation.watchPosition(function(actual) {
  mockgeolocation_funcionCallWrapper("currentGeolocationEquals", "mockgeolocation_Test7", assert.currentGeolocationEquals(actual, Geolocations.IRELAND));
  mockgeolocation_funcionCallWrapper("done", "mockgeolocation_Test7", done());
}));
});
QUnit.module("navigator.geolocation.clearWatch");
QUnit.test("clears watch", function(assert) {
  var done = mockgeolocation_funcionCallWrapper("async", "mockgeolocation_Test8", assert.async());
  var watch = mockgeolocation_funcionCallWrapper("watchPosition", "mockgeolocation_Test8", navigator.geolocation.watchPosition(function(actual) {
  mockgeolocation_funcionCallWrapper("currentGeolocationEquals", "mockgeolocation_Test8", assert.currentGeolocationEquals(actual, Geolocations.IRELAND));
  mockgeolocation_funcionCallWrapper("done", "mockgeolocation_Test8", done());
}));
  mockgeolocation_funcionCallWrapper("setCurrentPosition", "mockgeolocation_Test8", MockGeolocation.setCurrentPosition(Geolocations.IRELAND));
  mockgeolocation_funcionCallWrapper("clearWatch", "mockgeolocation_Test8", navigator.geolocation.clearWatch(watch));
  mockgeolocation_funcionCallWrapper("setCurrentPosition", "mockgeolocation_Test8", MockGeolocation.setCurrentPosition(Geolocations.IRELAND));
});
