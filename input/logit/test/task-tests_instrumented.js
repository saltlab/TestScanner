task_tests_funcionCallTrace = [];
function task_tests_funcionCallWrapper(functionName, testFunction, functionBody) {
  task_tests_funcionCallTrace.push({functionName: functionName, testFunction: testFunction});
  return functionBody;
}
function task_tests_getFuncionCallTrace() {
  return task_tests_funcionCallTrace;
}
module('Model instance creation');
test("instance creation without attrs", function(assert) {
  var obj = new LogIt.Task();
  assert.ok(obj);
});
test("instance creation - log present - invalid value (2hh)", function(assert) {
  var obj = new LogIt.Task({log: '2hh'});
  assert.ok(obj);
  assert.equal(null, task_tests_funcionCallWrapper("get", "task-tests_Test2", obj.get('log')));
  assert.ok(task_tests_funcionCallWrapper("isNaN", "task-tests_Test2", Number.isNaN(task_tests_funcionCallWrapper("get", "task-tests_Test2", obj.get('value')))));
  assert.equal('h', task_tests_funcionCallWrapper("get", "task-tests_Test2", obj.get('unit')));
});
test("instance creation - log present - invalid value(2)", function(assert) {
  var obj = new LogIt.Task({log: '2'});
  assert.ok(obj);
  assert.equal(null, task_tests_funcionCallWrapper("get", "task-tests_Test3", obj.get('log')));
  assert.equal(2, task_tests_funcionCallWrapper("get", "task-tests_Test3", obj.get('value')));
  assert.equal('h', task_tests_funcionCallWrapper("get", "task-tests_Test3", obj.get('unit')));
});
test("instance creation - log present - invalid value(2h2h)", function(assert) {
  var obj = new LogIt.Task({log: '2'});
  assert.ok(obj);
  assert.equal(null, task_tests_funcionCallWrapper("get", "task-tests_Test4", obj.get('log')));
  assert.ok(task_tests_funcionCallWrapper("get", "task-tests_Test4", obj.get('value')));
  assert.equal('h', task_tests_funcionCallWrapper("get", "task-tests_Test4", obj.get('unit')));
});
module('Model validation');
test("Model validation - All values are null", function(assert) {
  var obj = new LogIt.Task();
  var msg = task_tests_funcionCallWrapper("isValidModel", "task-tests_Test5", obj.isValidModel());
  assert.ok(msg.task);
  assert.ok(msg.date);
  assert.ok(msg.date);
});
test("Model validation - Invalid value", function(assert) {
  var obj = new LogIt.Task({task: '', date: task_tests_funcionCallWrapper("now", "task-tests_Test6", Date.now()), value: '2hh'});
  var msg = task_tests_funcionCallWrapper("isValidModel", "task-tests_Test6", obj.isValidModel());
  assert.ok(msg.log);
});
test("Model validation - Negative value", function(assert) {
  var obj = new LogIt.Task({task: '', date: task_tests_funcionCallWrapper("now", "task-tests_Test7", Date.now()), value: '-2h'});
  var msg = task_tests_funcionCallWrapper("isValidModel", "task-tests_Test7", obj.isValidModel());
  assert.ok(msg.log);
});
test("Model validation - All values are valid", function(assert) {
  var obj = new LogIt.Task({task: '', date: task_tests_funcionCallWrapper("now", "task-tests_Test8", Date.now()), log: '2h'});
  var msg = task_tests_funcionCallWrapper("isValidModel", "task-tests_Test8", obj.isValidModel());
  assert.equal(msg, null);
});
module('Model toHour');
test("Model toHour default value", function(assert) {
  LogIt.Settings = {dayHour: function() {
  return 8;
}};
  var obj = new LogIt.Task({log: '1.25d'});
  var hr = task_tests_funcionCallWrapper("toHour", "task-tests_Test9", obj.toHour(true));
  assert.equal(10.00, hr);
  var obj = new LogIt.Task({log: '1.25h'});
  var hr = task_tests_funcionCallWrapper("toHour", "task-tests_Test9", obj.toHour(true));
  assert.equal(1.25, hr);
  var obj = new LogIt.Task({log: '20m'});
  var hr = task_tests_funcionCallWrapper("toHour", "task-tests_Test9", obj.toHour(true));
  assert.equal(0.333, hr);
});
test("Model toHour custom value", function(assert) {
  LogIt.Settings = {dayHour: function() {
  return 6;
}};
  var obj = new LogIt.Task({log: '1.25d'});
  var hr = task_tests_funcionCallWrapper("toHour", "task-tests_Test10", obj.toHour(true));
  assert.equal(7.5, hr);
  var obj = new LogIt.Task({log: '1.25h'});
  var hr = task_tests_funcionCallWrapper("toHour", "task-tests_Test10", obj.toHour(true));
  assert.equal(1.25, hr);
  var obj = new LogIt.Task({log: '20m'});
  var hr = task_tests_funcionCallWrapper("toHour", "task-tests_Test10", obj.toHour(true));
  assert.equal(0.333, hr);
});
