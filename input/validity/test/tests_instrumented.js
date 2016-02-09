tests_funcionCallTrace = [];
function tests_funcionCallWrapper(functionName, testFunction, functionBody) {
  tests_funcionCallTrace.push({functionName: functionName, testFunction: testFunction});
  return functionBody;
}
function tests_getFuncionCallTrace() {
  return tests_funcionCallTrace;
}
var element = null;
module("processValidation-Test Success", {setup: function() {
  element = {required: {value: {length: '1'}}, email: {value: 'xyz@we.com'}, url: {value: 'http://www.github.com/'}};
}});
test("Test 'required'", function() {
  ok(tests_funcionCallWrapper("processValidation", "tests_Test1", Validity.processValidation('required', element.required)), "validated!");
});
test("Test 'email'", function() {
  ok(tests_funcionCallWrapper("processValidation", "tests_Test2", Validity.processValidation('email', element.email)), "validated!");
});
test("Test 'url'", function() {
  ok(tests_funcionCallWrapper("processValidation", "tests_Test3", Validity.processValidation('URL', element.url)), "validated");
});
module("processValidation-Test Failure", {setup: function() {
  element = {required: {value: {length: '0'}}, email: {value: 'xyzwe.com'}, url: {value: 'wwwgithubcom'}};
}});
test("Test 'required'", function() {
  strictEqual(tests_funcionCallWrapper("processValidation", "tests_Test4", Validity.processValidation('required', element.required)), false, "validated");
});
test("Test 'email'", function() {
  strictEqual(tests_funcionCallWrapper("processValidation", "tests_Test5", Validity.processValidation('email', element.email)), false, "validated");
});
test("Test 'url'", function() {
  strictEqual(tests_funcionCallWrapper("processValidation", "tests_Test6", Validity.processValidation('URL', element.url)), false, "validated");
});
