tests_funcionCallTrace = [];
function tests_funcionCallWrapper(functionName, testFunction, functionBody) {
  tests_funcionCallTrace.push({functionName: functionName, testFunction: testFunction});
  return functionBody;
}
function tests_getFuncionCallTrace() {
  return tests_funcionCallTrace;
}
var clearStorage = function() {
  tests_funcionCallWrapper("clear", "tests_", webStorage.local.clear());
  tests_funcionCallWrapper("clear", "tests_", webStorage.session.clear());
};
QUnit.module("web-storage", {setup: clearStorage, teardown: clearStorage});
var tomorrow = function() {
  var date = new Date();
  tests_funcionCallWrapper("setDate", "tests_TestModule", date.setDate(tests_funcionCallWrapper("getDate", "tests_TestModule", date.getDate()) + 1));
  return date;
};
var oneSecondAgo = function() {
  var date = new Date();
  return new Date(tests_funcionCallWrapper("getTime", "tests_TestModule", date.getTime()) - 1000);
};
QUnit.test("local.getItem unset item returns null", function(assert) {
  assert.ok(tests_funcionCallWrapper("getItem", "tests_Test1", webStorage.local.getItem("key")) === null, "unset item not found");
});
QUnit.test("local.setItem, local.getItem basic test", function(assert) {
  var data = {abc: "def"};
  tests_funcionCallWrapper("setItem", "tests_Test2", webStorage.local.setItem("key", data, {expires: tests_funcionCallWrapper("tomorrow", "tests_Test2", tomorrow())}));
  assert.deepEqual(tests_funcionCallWrapper("getItem", "tests_Test2", webStorage.local.getItem("key")), data, "data retrieved equals data set");
});
QUnit.test("local.clear test", function(assert) {
  tests_funcionCallWrapper("setItem", "tests_Test3", webStorage.local.setItem("key", {abc: "def"}, {expires: tests_funcionCallWrapper("tomorrow", "tests_Test3", tomorrow())}));
  tests_funcionCallWrapper("clear", "tests_Test3", webStorage.local.clear());
  assert.ok(tests_funcionCallWrapper("getItem", "tests_Test3", webStorage.local.getItem("key")) === null, "cleared storage no longer contains previously set item");
});
QUnit.test("local.removeItem", function(assert) {
  tests_funcionCallWrapper("setItem", "tests_Test4", webStorage.local.setItem("key", {abc: "def"}, {expires: tests_funcionCallWrapper("tomorrow", "tests_Test4", tomorrow())}));
  var i = tests_funcionCallWrapper("key", "tests_Test4", webStorage.local.key(0));
  tests_funcionCallWrapper("removeItem", "tests_Test4", webStorage.local.removeItem("key"));
  assert.ok(tests_funcionCallWrapper("getItem", "tests_Test4", webStorage.local.getItem("key")) === null, "removed item no longer available");
});
QUnit.test("local.key", function(assert) {
  tests_funcionCallWrapper("setItem", "tests_Test5", webStorage.local.setItem("key", {abc: "def"}, {expires: tests_funcionCallWrapper("tomorrow", "tests_Test5", tomorrow())}));
  assert.ok(tests_funcionCallWrapper("key", "tests_Test5", webStorage.local.key(0)) === "key", "Index's key found");
});
QUnit.test("local.setItem without options is unsuccessful", function(assert) {
  tests_funcionCallWrapper("setItem", "tests_Test6", webStorage.local.setItem("key", {abc: "def"}));
  assert.ok(tests_funcionCallWrapper("getItem", "tests_Test6", window.localStorage.getItem("key")) === null, "No item found.");
});
QUnit.test("local.setItem without options.expires is unsuccessful", function(assert) {
  tests_funcionCallWrapper("setItem", "tests_Test7", webStorage.local.setItem("key", {abc: "def"}, {}));
  assert.ok(tests_funcionCallWrapper("getItem", "tests_Test7", window.localStorage.getItem("key")) === null, "No item found.");
});
QUnit.test("local.setItem with wrong type for options.expires is unsuccessful", function(assert) {
  tests_funcionCallWrapper("setItem", "tests_Test8", webStorage.local.setItem("key", {abc: "def"}, {expires: "tomorrow"}));
  assert.ok(tests_funcionCallWrapper("getItem", "tests_Test8", window.localStorage.getItem("key")) === null, "No item found.");
});
QUnit.test("local.setItem with expired object is unsuccessful", function(assert) {
  tests_funcionCallWrapper("setItem", "tests_Test9", webStorage.local.setItem("key", {abc: "def"}, {expires: tests_funcionCallWrapper("oneSecondAgo", "tests_Test9", oneSecondAgo())}));
  assert.ok(tests_funcionCallWrapper("getItem", "tests_Test9", window.localStorage.getItem("key")) === null, "No item found.");
});
QUnit.test("local.setItem with wrong type for options.dependencies is unsuccessful", function(assert) {
  tests_funcionCallWrapper("setItem", "tests_Test10", webStorage.local.setItem("key", {abc: "def"}, {expires: tests_funcionCallWrapper("tomorrow", "tests_Test10", tomorrow()), dependencies: "key2"}));
  assert.ok(tests_funcionCallWrapper("getItem", "tests_Test10", window.localStorage.getItem("key")) === null, "No item found.");
});
QUnit.test("local.setItem with empty options.dependencies array is successful", function(assert) {
  tests_funcionCallWrapper("setItem", "tests_Test11", webStorage.local.setItem("key", {abc: "def"}, {expires: tests_funcionCallWrapper("tomorrow", "tests_Test11", tomorrow()), dependencies: []}));
  assert.ok(tests_funcionCallWrapper("getItem", "tests_Test11", window.localStorage.getItem("key")) !== null, "Item found.");
});
QUnit.test("local.setItem with available dependencies successful", function(assert) {
  tests_funcionCallWrapper("setItem", "tests_Test12", webStorage.local.setItem("key", {abc: "def"}, {expires: tests_funcionCallWrapper("tomorrow", "tests_Test12", tomorrow()), dependencies: []}));
  tests_funcionCallWrapper("setItem", "tests_Test12", webStorage.local.setItem("key2", {abc: "def"}, {expires: tests_funcionCallWrapper("tomorrow", "tests_Test12", tomorrow()), dependencies: ["key"]}));
  assert.ok(tests_funcionCallWrapper("getItem", "tests_Test12", window.localStorage.getItem("key2")) !== null, "Item found.");
});
QUnit.test("local.setItem with unavailable dependencies unsuccessful", function(assert) {
  tests_funcionCallWrapper("setItem", "tests_Test13", webStorage.local.setItem("key", {abc: "def"}, {expires: tests_funcionCallWrapper("oneSecondAgo", "tests_Test13", oneSecondAgo()), dependencies: []}));
  tests_funcionCallWrapper("setItem", "tests_Test13", webStorage.local.setItem("key2", {abc: "def"}, {expires: tests_funcionCallWrapper("tomorrow", "tests_Test13", tomorrow()), dependencies: ["key"]}));
  assert.ok(tests_funcionCallWrapper("getItem", "tests_Test13", window.localStorage.getItem("key2")) === null, "Item not found.");
});
