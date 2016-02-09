settings_view_tests_funcionCallTrace = [];
function settings_view_tests_funcionCallWrapper(functionName, testFunction, functionBody) {
  settings_view_tests_funcionCallTrace.push({functionName: functionName, testFunction: testFunction});
  return functionBody;
}
function settings_view_tests_getFuncionCallTrace() {
  return settings_view_tests_funcionCallTrace;
}
module('Model instance creation', {setup: function() {
  LogIt.Settings = {weekView: function() {
}, dayHour: function() {
}, save: function(obj) {
  return obj;
}};
  var weekView = settings_view_tests_funcionCallWrapper("stub", "settings-view-tests_TestModule", sinon.stub(LogIt.Settings, "weekView"));
  settings_view_tests_funcionCallWrapper("returns", "settings-view-tests_TestModule", settings_view_tests_funcionCallWrapper("onFirstCall", "settings-view-tests_TestModule", weekView.onFirstCall()).returns("5"));
  var dayHour = settings_view_tests_funcionCallWrapper("stub", "settings-view-tests_TestModule", sinon.stub(LogIt.Settings, "dayHour"));
  settings_view_tests_funcionCallWrapper("returns", "settings-view-tests_TestModule", settings_view_tests_funcionCallWrapper("onFirstCall", "settings-view-tests_TestModule", dayHour.onFirstCall()).returns(10));
  var h = settings_view_tests_funcionCallWrapper("html", "settings-view-tests_TestModule", $('#settingHtml').html());
  settings_view_tests_funcionCallWrapper("html", "settings-view-tests_TestModule", $('#settings').html(h));
}, teardown: function() {
  settings_view_tests_funcionCallWrapper("html", "settings-view-tests_TestModule", $('#settings').html(''));
  LogIt.Settings = null;
}});
test("Initialization", function(assert) {
  var settingsView = new LogIt.SettingsView();
  var value = settings_view_tests_funcionCallWrapper("val", "settings-view-tests_Test1", $('input[name=dayHours]').val());
  assert.equal(10, value);
  var chk = settings_view_tests_funcionCallWrapper("val", "settings-view-tests_Test1", $('input[name=weekView]:checked').val());
  assert.equal("5", chk);
});
test("Test isValidDayHour", function(assert) {
  var settingsView = new LogIt.SettingsView();
  var v = settings_view_tests_funcionCallWrapper("isValidDayHour", "settings-view-tests_Test2", settingsView.isValidDayHour());
  assert.equal(false, v);
  v = settings_view_tests_funcionCallWrapper("isValidDayHour", "settings-view-tests_Test2", settingsView.isValidDayHour(''));
  assert.equal(false, v);
  v = settings_view_tests_funcionCallWrapper("isValidDayHour", "settings-view-tests_Test2", settingsView.isValidDayHour('a'));
  assert.equal(false, v);
  v = settings_view_tests_funcionCallWrapper("isValidDayHour", "settings-view-tests_Test2", settingsView.isValidDayHour('0'));
  assert.equal(false, v);
  v = settings_view_tests_funcionCallWrapper("isValidDayHour", "settings-view-tests_Test2", settingsView.isValidDayHour('25'));
  assert.equal(false, v);
  v = settings_view_tests_funcionCallWrapper("isValidDayHour", "settings-view-tests_Test2", settingsView.isValidDayHour('24'));
  assert.equal(true, v);
});
test("Test onSave valid values", function(assert) {
  LogIt.App = {refresh: function() {
}};
  var refresh = settings_view_tests_funcionCallWrapper("stub", "settings-view-tests_Test3", sinon.stub(LogIt.App, "refresh"));
  var save = settings_view_tests_funcionCallWrapper("stub", "settings-view-tests_Test3", sinon.stub(LogIt.Settings, "save"));
  var settingsView = new LogIt.SettingsView();
  settings_view_tests_funcionCallWrapper("onSave", "settings-view-tests_Test3", settingsView.onSave());
  assert.ok(refresh.calledOnce);
  assert.ok(save.calledOnce);
  LogIt.App = null;
});
test("Test onSave invalid values", function(assert) {
  LogIt.App = {refresh: function() {
}};
  var refresh = settings_view_tests_funcionCallWrapper("stub", "settings-view-tests_Test4", sinon.stub(LogIt.App, "refresh"));
  var save = settings_view_tests_funcionCallWrapper("stub", "settings-view-tests_Test4", sinon.stub(LogIt.Settings, "save"));
  var settingsView = new LogIt.SettingsView();
  settings_view_tests_funcionCallWrapper("val", "settings-view-tests_Test4", $('input[name=dayHours]').val(50));
  settings_view_tests_funcionCallWrapper("onSave", "settings-view-tests_Test4", settingsView.onSave());
  assert.ok(refresh.notCalled);
  assert.ok(save.notCalled);
  LogIt.App = null;
});
