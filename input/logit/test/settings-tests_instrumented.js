settings_tests_funcionCallTrace = [];
function settings_tests_funcionCallWrapper(functionName, testFunction, functionBody) {
  settings_tests_funcionCallTrace.push({functionName: functionName, testFunction: testFunction});
  return functionBody;
}
function settings_tests_getFuncionCallTrace() {
  return settings_tests_funcionCallTrace;
}
module('Settings Tests');
test("Settings object as constant", function(assert) {
  LogIt.Settings.prop = 'value';
  assert.equal(null, LogIt.Settings.prop);
});
test("Callback invocation on init", function(assert) {
  var called = false;
  var value = settings_tests_funcionCallWrapper("init", "settings-tests_Test2", LogIt.Settings.init(function() {
  called = true;
}));
  assert.ok(called);
});
test("Test defaults", function(assert) {
  var df = settings_tests_funcionCallWrapper("dateFormat", "settings-tests_Test3", LogIt.Settings.dateFormat());
  assert.equal(df, "dd MMM yyyy");
  var wv = settings_tests_funcionCallWrapper("weekView", "settings-tests_Test3", LogIt.Settings.weekView());
  assert.equal(wv, "5");
  var dh = settings_tests_funcionCallWrapper("dayHour", "settings-tests_Test3", LogIt.Settings.dayHour());
  assert.equal(dh, 8);
});
test("Test save", function(assert) {
  var obj = {weekView: "7", dayHour: 10};
  settings_tests_funcionCallWrapper("save", "settings-tests_Test4", LogIt.Settings.save(obj));
  var wv = settings_tests_funcionCallWrapper("weekView", "settings-tests_Test4", LogIt.Settings.weekView());
  assert.equal(wv, "7");
  var dh = settings_tests_funcionCallWrapper("dayHour", "settings-tests_Test4", LogIt.Settings.dayHour());
  assert.equal(dh, 10);
});
