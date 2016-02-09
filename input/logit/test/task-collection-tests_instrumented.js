task_collection_tests_funcionCallTrace = [];
function task_collection_tests_funcionCallWrapper(functionName, testFunction, functionBody) {
  task_collection_tests_funcionCallTrace.push({functionName: functionName, testFunction: testFunction});
  return functionBody;
}
function task_collection_tests_getFuncionCallTrace() {
  return task_collection_tests_funcionCallTrace;
}
module('Task Collection test cases');
test("Test adding hours of a day - default dayhour", function(assert) {
  LogIt.Settings = {dayHour: function() {
  return 8;
}};
  var t1 = new LogIt.Task({log: '20m'});
  var t2 = new LogIt.Task({log: '.5d'});
  var t3 = new LogIt.Task({log: '1.25h'});
  var col = new LogIt.TaskCollection();
  var res = task_collection_tests_funcionCallWrapper("sum", "task-collection-tests_Test1", col.sum([t1, t2, t3]));
  assert.equal("string", typeof res);
  assert.equal("5.58", res);
});
test("Test adding hours of a day - custom dayhour", function(assert) {
  LogIt.Settings = {dayHour: function() {
  return 8.5;
}};
  var t1 = new LogIt.Task({log: '20m'});
  var t2 = new LogIt.Task({log: '.5d'});
  var t3 = new LogIt.Task({log: '1.25h'});
  var col = new LogIt.TaskCollection();
  var res = task_collection_tests_funcionCallWrapper("sum", "task-collection-tests_Test2", col.sum([t1, t2, t3]));
  assert.equal("string", typeof res);
  assert.equal("5.83", res);
});
test("Test filtering based on day", function(assert) {
  var yrday = new LogIt.Task({date: task_collection_tests_funcionCallWrapper("addDays", "task-collection-tests_Test3", task_collection_tests_funcionCallWrapper("today", "task-collection-tests_Test3", Date.today()).addDays(-1))});
  var today1 = new LogIt.Task({date: task_collection_tests_funcionCallWrapper("today", "task-collection-tests_Test3", Date.today())});
  var today2 = new LogIt.Task({date: task_collection_tests_funcionCallWrapper("today", "task-collection-tests_Test3", Date.today())});
  var tmrww = new LogIt.Task({date: task_collection_tests_funcionCallWrapper("addDays", "task-collection-tests_Test3", task_collection_tests_funcionCallWrapper("today", "task-collection-tests_Test3", Date.today()).addDays(1))});
  var col = new LogIt.TaskCollection();
  task_collection_tests_funcionCallWrapper("add", "task-collection-tests_Test3", col.add([yrday, today1, today2, tmrww]));
  var todayResults = task_collection_tests_funcionCallWrapper("day", "task-collection-tests_Test3", col.day());
  assert.equal(2, todayResults.length);
  assert.equal(today1, todayResults[0]);
  assert.equal(today2, todayResults[1]);
  var yesResults = task_collection_tests_funcionCallWrapper("day", "task-collection-tests_Test3", col.day(task_collection_tests_funcionCallWrapper("addDays", "task-collection-tests_Test3", task_collection_tests_funcionCallWrapper("today", "task-collection-tests_Test3", Date.today()).addDays(-1))));
  assert.equal(1, yesResults.length);
  assert.equal(yrday, yesResults[0]);
  var tomResults = task_collection_tests_funcionCallWrapper("day", "task-collection-tests_Test3", col.day(task_collection_tests_funcionCallWrapper("addDays", "task-collection-tests_Test3", task_collection_tests_funcionCallWrapper("today", "task-collection-tests_Test3", Date.today()).addDays(1))));
  assert.equal(1, tomResults.length);
  assert.equal(tmrww, tomResults[0]);
});
test("Test match for type ahead", function(assert) {
  var t1 = new LogIt.Task({date: task_collection_tests_funcionCallWrapper("today", "task-collection-tests_Test4", Date.today()), task: 'Task A1'});
  var t2 = new LogIt.Task({date: task_collection_tests_funcionCallWrapper("today", "task-collection-tests_Test4", Date.today()), task: 'Task A2'});
  var t3 = new LogIt.Task({date: task_collection_tests_funcionCallWrapper("today", "task-collection-tests_Test4", Date.today()), task: 'Task A3'});
  var col = new LogIt.TaskCollection();
  task_collection_tests_funcionCallWrapper("add", "task-collection-tests_Test4", col.add([t1, t2, t3]));
  var ignoreCaseMatches = task_collection_tests_funcionCallWrapper("match", "task-collection-tests_Test4", col.match('task'));
  assert.equal(3, ignoreCaseMatches.length);
  assert.equal('Task A1', ignoreCaseMatches[0]);
  assert.equal('Task A2', ignoreCaseMatches[1]);
  assert.equal('Task A3', ignoreCaseMatches[2]);
  var wordMatch = task_collection_tests_funcionCallWrapper("match", "task-collection-tests_Test4", col.match('a'));
  assert.equal(3, wordMatch.length);
  var exactMatch = task_collection_tests_funcionCallWrapper("match", "task-collection-tests_Test4", col.match('A3'));
  assert.equal(1, exactMatch.length);
  assert.equal('Task A3', exactMatch[0]);
});
