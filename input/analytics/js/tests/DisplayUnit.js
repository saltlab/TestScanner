module("DisplayUnit");

QUnit.config.reorder = false;

////////////////////////////////////////////////////////////////
///////////////////////// FIXTURE //////////////////////////////
////////////////////////////////////////////////////////////////

var FactSelector = {

  factSelector : null,

  init : function (factSelector, introCubes, introMeasures) {
    this.factSelector = factSelector;
  },

  setMetadata : function (data) {
    $(this.factSelector).html(JSON.stringify(data));
  },

  setSelectedCube : function (cubeID) {
  },

  setSelectedMeasure : function (measureID) {
  },

  setCallback : function (fct) {
  }

};

$.ajaxSetup({async:false});

////////////////////////////////////////////////////////////////
///////////////////////// TESTS ////////////////////////////////
////////////////////////////////////////////////////////////////


test("Slicing", function() {

  var members0 = {"m1" : {"caption":"member1"}, "m2" : {"caption":"member2"}};
  var members1 = {"m1b" : {"caption":"member1"}, "m2b" : {"caption":"member2"}};

  // test addSlice, getDimensions, getDimensionHierarchy, getDimensionCurrentLevel
  ok(true, "=== ADDING DIMENSION 'GEO' LEVEL 0 TO STACK ===");

  Display.addSliceToStack("geo", "Geography", "hierGeo", 0, members0, false);
  deepEqual(Display.getDimensions(), ["geo"], "has 1 dimension named geo");
  equal(Display.getDimensionHierarchy("geo"), "hierGeo", "has saved the good hierarchy");
  equal(Display.getDimensionCurrentLevel("geo"), 0, "has saved the good level");

  ok(true, "=== ADDING DIMENSION 'GEO' LEVEL 1 TO STACK ===");

  Display.addSliceToStack("geo", "Geography", "hierGeo", 1, members1, false);
  equal(Display.getDimensionCurrentLevel("geo"), 1, "has saved the good level after 2 inserts");

  // test get slice after add

  var slice0 = Display.getSliceFromStack("geo", 0);
  var slice1 = Display.getSliceFromStack("geo", 1);
  var sliceLast = Display.getSliceFromStack("geo");

  equal(slice0.properties, false, "good slice level 0 properties attribute");
  equal(slice0.level, 0, "good slice level 0 level attribute");
  equal(slice0.members, members0, "good slice level 0 members attribute");

  equal(slice1.properties, false, "good slice level 1 properties attribute");
  equal(slice1.level, 1, "good slice level 1 level attribute");
  equal(slice1.members, members1, "good slice level 1 members attribute");

  equal(sliceLast.properties, false, "good slice for last level properties attribute");
  equal(sliceLast.level, 1, "good slice for last level level attribute");
  equal(sliceLast.members, members1, "good slice for last level members attribute");

  // test removeLastSliceFromStack

  ok(true, "=== REMOVING DIMENSION 'GEO' LEVEL 1 FROM STACK ===");

  Display.removeLastSliceFromStack("geo");
  equal(Display.getDimensionCurrentLevel("geo"), 0, "has saved the good level after 2 inserts and 1 remove");

  // test getSliceFromStack after remove

  var slice0 = Display.getSliceFromStack("geo", 0);
  var sliceLast = Display.getSliceFromStack("geo");

  equal(slice0.properties, false, "good slice level 0 properties attribute after add & remove");
  equal(slice0.level, 0, "good slice level 0 level attribute after add & remove");
  equal(slice0.members, members0, "good slice level 0 members attribute after add & remove");

  equal(sliceLast.properties, false, "good slice for last level properties attribute after add & remove");
  equal(sliceLast.level, 0, "good slice for last level level attribute after add & remove");
  equal(sliceLast.members, members0, "good slice for last level members attribute after add & remove");

  // add a dimension

  ok(true, "=== ADDING DIMENSION 'TIME' LEVEL 0 TO STACK ===");

  Display.addSliceToStack("time", "Time", "hierTime", 0, members0, false);
  deepEqual(Display.getDimensions(), ["geo", "time"], "has 2 dimensions named geo and time");

  // remove all dimensions

  ok(true, "=== REMOVING DIMENSIONS 'TIME' AND 'GEO' LEVELS 0 FROM STACK ===");

  Display.removeLastSliceFromStack("geo");
  Display.removeLastSliceFromStack("time");

  deepEqual(Display.getDimensions(), [], "has 0 dimension after all slices removed");

});

test("Crossfilter", function() {

  var testData = [
    {date: "2011-11-14T16:17:54Z", quantity: 2, total: 190, tip: 100, type: "tab"},
    {date: "2011-11-14T16:20:19Z", quantity: 2, total: 190, tip: 100, type: "tab"},
    {date: "2011-11-14T16:28:54Z", quantity: 1, total: 300, tip: 200, type: "visa"},
    {date: "2011-11-14T16:30:43Z", quantity: 2, total: 90, tip: 0, type: "tab"},
    {date: "2011-11-14T16:48:46Z", quantity: 2, total: 90, tip: 0, type: "tab"},
    {date: "2011-11-14T16:53:41Z", quantity: 2, total: 90, tip: 0, type: "tab"},
    {date: "2011-11-14T16:54:06Z", quantity: 1, total: 100, tip: 0, type: "cash"},
    {date: "2011-11-14T16:58:03Z", quantity: 2, total: 90, tip: 0, type: "tab"},
    {date: "2011-11-14T17:07:21Z", quantity: 2, total: 90, tip: 0, type: "tab"},
    {date: "2011-11-14T17:22:59Z", quantity: 2, total: 90, tip: 0, type: "tab"},
    {date: "2011-11-14T17:25:45Z", quantity: 2, total: 200, tip: 0, type: "cash"},
    {date: "2011-11-14T17:29:52Z", quantity: 1, total: 200, tip: 100, type: "visa"}
  ];

  var testCF = crossfilter(testData);

  ok(true, "=== SETTING MEASURE AND DIMENSIONS FOR THE TEST (NEEDED) ===");

  Display.setMeasure("total");
  Display.addSliceToStack("quantity", "Quantity", "hier", 0, {}, false);
  Display.addSliceToStack("type", "Type", "hier", 0, {}, false);

  // test setCrossfilterData

    ok(true, "=== SETTING CROSSFILTER DATA ===");

  Display.setCrossfilterData(testData);

  propEqual(Display.dataCrossfilter, crossfilter(testData), "crossfilter dataset has been create");
  equal(Display.dataCrossfilter.groupAll().value(), testCF.groupAll().value(), "the global groupAll is good so data are probably ok");

  // getCrossfilterDimensionAndGroup

  ok(true, "=== GETTING DIMENSION AND GROUP ===");

  var testDim = testCF.dimension(function(d) { return d["quantity"]; });
  var testGroup = testDim.group().reduceSum(function(d) { return d["total"]; });

  var CFDimAndGroup = Display.getCrossfilterDimensionAndGroup("quantity");
  propEqual(CFDimAndGroup.dimension, testDim, "we got a dimension");
  deepEqual(CFDimAndGroup.dimension.top(Infinity), testDim.top(Infinity), "the dimension has the good values");
  propEqual(CFDimAndGroup.group, testGroup, "we got a group");
  deepEqual(CFDimAndGroup.group.top(Infinity), testGroup.top(Infinity), "the group has the good values");

});

test("Tools", function() {

  ok(true, "=== TESTING TRANSFORMSPATIALMETADATA ===");

  // transformSpatialMetadata

  var input = {
    "myId" : {
      "caption" : "First",
      "geom" : {"type" : "GeomTest", "arcs" : "myarcs"}
    },
   "myId2" : {
      "caption" : "Second",
      "geom" : {"type" : "GeomTest", "arcs" : "myarcs"}
    }
  };

  var outputExpect = [
    {"id" : "myId", "type" : "GeomTest", "arcs" : "myarcs", "properties" : {"name" : "First"}},
    {"id" : "myId2", "type" : "GeomTest", "arcs" : "myarcs", "properties" : {"name" : "Second"}}
  ];

  var output = Display.transformSpatialMetadata(input, "geom");

  deepEqual(output, outputExpect, "transform DB metadata to geoJSON metadata")

});


test("Set options", function() {

  ok(true, "=== SETTING ALL POSSIBLE OPTIONS ===");

  Display.setOptions({
    "factSelector" : "test1",
    "factCubesIntro" : "test2",
    "factMeasuresIntro" : "test3",
    "charts" : {
      "map" : "test4",
      "timeline" : "test5",
      "rightChart" : "test6",
      "table" : "test7"
    },
    "colors" : "test8",
    "cloudsSelector" : "test9",
    "zoomId" : "test10",
    "resetSelector" : "test11"
  });

  equal(Display.options.factSelector, "test1", "set option factSelector ok");
  equal(Display.options.factCubesIntro, "test2", "set option factCubesIntro ok");
  equal(Display.options.factMeasuresIntro, "test3", "set option factMeasuresIntro ok");
  equal(Display.charts.map.selector, "test4", "set option map selector ok");
  equal(Display.charts.timeline.selector, "test5", "set option timeline selector ok");
  equal(Display.charts.rightChart.selector, "test6", "set option rightChart selector ok");
  equal(Display.charts.table.selector, "test7", "set option table selector ok");
  equal(Display.options.colors, "test8", "set option colors ok");
  equal(Display.options.cloudsSelector, "test9", "set option cloudsSelector ok");
  equal(Display.options.zoomId, "test10", "set option zoomId ok");
  equal(Display.options.resetSelector, "test11", "set option resetSelector ok");

});