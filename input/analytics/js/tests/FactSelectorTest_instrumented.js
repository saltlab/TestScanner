FactSelectorTest_funcionCallTrace = [];
function FactSelectorTest_funcionCallWrapper(functionName, testFunction, functionBody) {
  FactSelectorTest_funcionCallTrace.push({functionName: functionName, testFunction: testFunction});
  return functionBody;
}
function FactSelectorTest_getFuncionCallTrace() {
  return FactSelectorTest_funcionCallTrace;
}
module("FactSelectorTest");
test("Initialization", function() {
  FactSelectorTest_funcionCallWrapper("init", "FactSelectorTest_Test1", FactSelector.init("#qunit-fixture", 'Cubes available', 'Measures available'));
  equal(FactSelector.cubes.intro, 'Cubes available', "The text to introduce cubes available is the good one");
  equal(FactSelector.measures.intro, 'Measures available', "The text to introduce measures available is the good one");
});
test("Div containing cubes and measures are correctly filled", function() {
  FactSelectorTest_funcionCallWrapper("init", "FactSelectorTest_Test2", FactSelector.init("#qunit-fixture", 'Cubes available', 'Measures available'));
  var data = {"12": {"caption": "cubeCaption12", "measures": {"measureID": {"caption": "measureCaption"}, "measureID2": {"caption": "measureCaption2"}}}, "13": {"caption": "cubeCaption13", "measures": {"measureID": {"caption": "measureCaption"}, "measureID2": {"caption": "measureCaption2"}, "measureID3": {"caption": "measureCaption3"}}}};
  FactSelectorTest_funcionCallWrapper("setMetadata", "FactSelectorTest_Test2", FactSelector.setMetadata(data));
  FactSelectorTest_funcionCallWrapper("setSelectedCube", "FactSelectorTest_Test2", FactSelector.setSelectedCube("12"));
  equal($("#qunit-fixture div:first li").length, '2', "The div contains all the cubes in the data");
  equal($("#qunit-fixture div:last li").length, '2', "The div of cube 12 contains the 2 measures");
  FactSelectorTest_funcionCallWrapper("setSelectedCube", "FactSelectorTest_Test2", FactSelector.setSelectedCube("13"));
  equal($("#qunit-fixture div:last li").length, '3', "The div of cube 13 contains the 3 measures");
});
test("The Callback should set the good values", function() {
  FactSelectorTest_funcionCallWrapper("init", "FactSelectorTest_Test3", FactSelector.init("#qunit-fixture", 'Cubes available', 'Measures available'));
  var data = {"12": {"caption": "cubeCaption12", "measures": {"measureID": {"caption": "measureCaption"}, "measureID2": {"caption": "measureCaption2"}}}, "13": {"caption": "cubeCaption13", "measures": {"measureID": {"caption": "measureCaption"}, "measureID2": {"caption": "measureCaption2"}}}};
  var testA = null;
  var testB = null;
  FactSelectorTest_funcionCallWrapper("setMetadata", "FactSelectorTest_Test3", FactSelector.setMetadata(data));
  FactSelectorTest_funcionCallWrapper("setCallback", "FactSelectorTest_Test3", FactSelector.setCallback(function(a, b) {
  testA = a;
  testB = b;
}));
  FactSelectorTest_funcionCallWrapper("selectCube", "FactSelectorTest_Test3", FactSelector.selectCube('12'));
  FactSelectorTest_funcionCallWrapper("selectMeasure", "FactSelectorTest_Test3", FactSelector.selectMeasure('13'));
  equal(testA, '12', "The selected cube has been set with the callback function");
  FactSelectorTest_funcionCallWrapper("clearAll", "FactSelectorTest_Test3", FactSelector.clearAll());
});
test("The clearAll function erases everything", function() {
  FactSelectorTest_funcionCallWrapper("init", "FactSelectorTest_Test4", FactSelector.init("#qunit-fixture", 'Cubes available', 'Measures available'));
  var data = {"12": {"caption": "cubeCaption12", "measures": {"measureID": {"caption": "measureCaption"}, "measureID2": {"caption": "measureCaption2"}}}, "13": {"caption": "cubeCaption13", "measures": {"measureID": {"caption": "measureCaption"}, "measureID2": {"caption": "measureCaption2"}}}};
  FactSelectorTest_funcionCallWrapper("setMetadata", "FactSelectorTest_Test4", FactSelector.setMetadata(data));
  equal(FactSelector.introCubes, 'Cubes available', "The fields are not empty after initialization");
  FactSelectorTest_funcionCallWrapper("clearAll", "FactSelectorTest_Test4", FactSelector.clearAll());
  equal(FactSelector.container, null, "The container is empty after clearAll()");
  equal(FactSelector.cube, null, "The cube id is empty after clearAll()");
  equal(FactSelector.cubes, null, "The div containing cubes list is empty after clearAll()");
  equal(FactSelector.measures, null, "The div containing measures list is empty after clearAll()");
  equal(FactSelector.introCubes, null, "The string to introduce list of cubes is empty after clearAll()");
  equal(FactSelector.introMeasures, null, "The string to introdice list of measures is empty after clearAll()");
  equal(FactSelector.data, null, "The object containing cubes and measures is empty after clearAll()");
  equal(FactSelector.callback, null, "The callback object is empty after clearAll()");
});
