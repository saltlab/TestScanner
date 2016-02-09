module("DisplayIntegration");

QUnit.config.reorder = false;

test("initMeasure", function() {

  // add sub fixture divs
  jQuery("#qunit-fixture").html("<div id=\"qunit-fixture1\"></div> <div id=\"qunit-fixture2\"></div> <div id=\"qunit-fixture3\"></div> <div id=\"qunit-fixture4\"></div> <div id=\"qunit-fixture5\"></div> <div id=\"qunit-fixture6\"></div> <div id=\"qunit-fixture7\"></div> <div id=\"qunit-fixture8\"></div> <div id=\"qunit-fixture9\"></div> <div id=\"qunit-fixture10\"></div>  <div id=\"qunit-fixture11\"></div>");

  Display.setOptions({
    "factSelector" : "#qunit-fixture1",
    "charts" : {
      "map" : "#qunit-fixture2",
      "timeline" : "#qunit-fixture3",
      "rightChart" : "#qunit-fixture4",
      "table" : "#qunit-fixture5"
    },
    "factCubesIntro" : "#qunit-fixture6",
    "factMeasuresIntro" : "#qunit-fixture7",
    "colors" : "#qunit-fixture8",
    "cloudsSelector" : "#qunit-fixture9",
    "zoomSelector" : "#qunit-fixture10",
    "resetSelector" : "#qunit-fixture11"
  });

  // init measures

  Display.initMeasure();

  var expectedList = JSON.stringify({
      "aCube" : {
        "caption": "Goods Quantity",
        "measures" : {
          'Raised' : 'Loaded',
          'unloaded' : 'Unloaded'
        }
      }
    });

  equal($("#qunit-fixture1").html(), expectedList, "intialize measures and fact selector effectively set to list of cubes and measures");
  notEqual(Display.schema, null, "A schema is selected");
  notEqual(Display.cube, null, "A cube is selected");
  notEqual(Display.measure, null, "A measure is selected");

});

test("initMetadata", function () {

  // add sub fixture divs
  jQuery("#qunit-fixture").html("<div id=\"qunit-fixture1\"></div> <div id=\"qunit-fixture2\"></div> <div id=\"qunit-fixture3\"></div> <div id=\"qunit-fixture4\"></div> <div id=\"qunit-fixture5\"></div> <div id=\"qunit-fixture6\"></div> <div id=\"qunit-fixture7\"></div> <div id=\"qunit-fixture8\"></div> <div id=\"qunit-fixture9\"></div> <div id=\"qunit-fixture10\"></div>  <div id=\"qunit-fixture11\"></div>");

  Display.setOptions({
    "factSelector" : "#qunit-fixture1",
    "charts" : {
      "map" : "#qunit-fixture2",
      "timeline" : "#qunit-fixture3",
      "rightChart" : "#qunit-fixture4",
      "table" : "#qunit-fixture5"
    },
    "factCubesIntro" : "#qunit-fixture6",
    "factMeasuresIntro" : "#qunit-fixture7",
    "colors" : "#qunit-fixture8",
    "cloudsSelector" : "#qunit-fixture9",
    "zoomId" : "#qunit-fixture10",
    "resetSelector" : "#qunit-fixture11"
  });

  Display.initMetadata();

  var sliceGeo = Display.getSliceFromStack("geo");
  var sliceTime = Display.getSliceFromStack("RoundClassDescr");

  var testGeoMembers = ["AT", "BE", "BG", "CH", "CY", "CZ", "DE", "DK", "EE", "ES", "FI", "FR", "GR", "HR", "HU", "IE", "IS", "IT", "LI", "LT", "LU", "LV", "MT", "NL", "NO", "PL", "PT", "RO", "SE", "SI", "SK", "TR", "UK", "MK"];
  var testTimeMembers = ["First Round", "Seed Round", "Later Stage", "Second Round", "Corporate", "Individual", "ACQ Financing", "Restart", "Unclassified"] ;

  equal(sliceGeo.properties, true, "good geo slice properties attribute");
  equal(sliceGeo.level, 0, "good geo slice level attribute");
  deepEqual(Object.keys(sliceGeo.members), testGeoMembers, "good geo slice members attribute");

  equal(sliceTime.properties, false, "good time slice properties attribute");
  equal(sliceTime.level, 0, "good time slice level attribute");
  deepEqual(Object.keys(sliceTime.members), testTimeMembers, "good time slice members attribute");

  equal(Display.charts.map.options.nbLevels, 1, "map knows the number of levels in geo dimension");

});

test("getData", function () {

  // add sub fixture divs
  jQuery("#qunit-fixture").html("<div id=\"qunit-fixture1\"></div> <div id=\"qunit-fixture2\"></div> <div id=\"qunit-fixture3\"></div> <div id=\"qunit-fixture4\"></div> <div id=\"qunit-fixture5\"></div> <div id=\"qunit-fixture6\"></div> <div id=\"qunit-fixture7\"></div> <div id=\"qunit-fixture8\"></div> <div id=\"qunit-fixture9\"></div> <div id=\"qunit-fixture10\"></div>  <div id=\"qunit-fixture11\"></div>");

  Display.setOptions({
    "factSelector" : "#qunit-fixture1",
    "charts" : {
      "map" : "#qunit-fixture2",
      "timeline" : "#qunit-fixture3",
      "rightChart" : "#qunit-fixture4",
      "table" : "#qunit-fixture5"
    },
    "factCubesIntro" : "#qunit-fixture6",
    "factMeasuresIntro" : "#qunit-fixture7",
    "colors" : "#qunit-fixture8",
    "cloudsSelector" : "#qunit-fixture9",
    "zoomSelector" : "#qunit-fixture10",
    "resetSelector" : "#qunit-fixture11"
  });

  var CFdata = Display.getData();
  var testCF = crossfilter([{date: "2011-11-14T16:17:54Z", quantity: 2, total: 190, tip: 100, type: "tab"}]);

  notEqual(CFdata, null, "data are not null");
  propEqual(CFdata, testCF, "dataset is a crossfilter dataset");

});


test("init, displayCharts, drillDown, rollUp", function () {

  // add sub fixture divs
  jQuery("#qunit-fixture").empty().html("<div id=\"qunit-fixture1\"></div> <div id=\"colums\"></div> <div id=\"qunit-fixture2\"></div> <div id=\"qunit-fixture3\"></div> <div id=\"qunit-fixture4\"></div> <div id=\"qunit-fixture5\"></div> <div id=\"qunit-fixture6\"></div> <div id=\"qunit-fixture7\"></div> <div id=\"qunit-fixture8\"></div> <div id=\"qunit-fixture9\"></div> <div id=\"qunit-fixture10\"></div>  <div id=\"qunit-fixture11\"></div>");

  Display.setOptions({
    "factSelector" : "#qunit-fixture1",
    "charts" : {
      "map" : "#qunit-fixture2",
      "timeline" : "#qunit-fixture3",
      "rightChart" : "#qunit-fixture4",
      "table" : "#qunit-fixture5"
    },
    "factCubesIntro" : "#qunit-fixture6",
    "factMeasuresIntro" : "#qunit-fixture7",
    "colors" : "#qunit-fixture8",
    "cloudsSelector" : "#qunit-fixture9",
    "zoomSelector" : "#qunit-fixture10",
    "resetSelector" : "#qunit-fixture11"
  });

  // init & displayCharts

  Display.init();

  notEqual($("#qunit-fixture2").html(), "", "HTML of chart 1 not empty");
  notEqual($("#qunit-fixture3").html(), "", "HTML of chart 2 not empty");
  notEqual($("#qunit-fixture4").html(), "", "HTML of chart 3 not empty");
  notEqual($("#qunit-fixture5").html(), "", "HTML of chart 4 not empty");
  notEqual(Display.charts.map.element, null, "dc.js element representing chart 1 exists");
  notEqual(Display.charts.timeline.element, "", "dc.js element representing chart 2 exists");
  notEqual(Display.charts.rightChart.element, "", "dc.js element representing chart 3 exists");
  notEqual(Display.charts.table.element, "", "dc.js element representing chart 4 exists");


  // drillDown

  var slice0 = Display.getSliceFromStack("geo");
  var html01 = $("#qunit-fixture2").html();
  var html02 = $("#qunit-fixture3").html();
  var html03 = $("#qunit-fixture4").html();
  var html04 = $("#qunit-fixture5").html();

  Display.drillDown("geo", "FR");

  var slice1 = Display.getSliceFromStack("geo");
  var html11 = $("#qunit-fixture2").html();
  var html12 = $("#qunit-fixture3").html();
  var html13 = $("#qunit-fixture4").html();
  var html14 = $("#qunit-fixture5").html();

  notDeepEqual(slice0, slice1, "Drill-down added a slice");
  notEqual(html01, html11, "Chart 1 changed");
  notEqual(html02, html12, "Chart 2 changed");
  notEqual(html03, html13, "Chart 3 changed");
  notEqual(html04, html14, "Chart 4 changed");

  // get state
  var state1 = Display.getState();

  // rollUp

  var slice0b = Display.getSliceFromStack("geo");

  html01 = $("#qunit-fixture2").html();
  html02 = $("#qunit-fixture3").html();
  html03 = $("#qunit-fixture4").html();
  html04 = $("#qunit-fixture5").html();

  Display.rollUp("geo");

  html11 = $("#qunit-fixture2").html();
  html12 = $("#qunit-fixture3").html();
  html13 = $("#qunit-fixture4").html();
  html14 = $("#qunit-fixture5").html();

  notDeepEqual(slice0b, slice0, "Roll-up returned to first slice");
  notEqual(html01, html11, "Chart 1 changed");
  notEqual(html02, html12, "Chart 2 changed");
  notEqual(html03, html13, "Chart 3 changed");
  notEqual(html04, html14, "Chart 4 changed");

  // get state
  var state2 = Display.getState();

  notDeepEqual(state1, state2, "state has changed");

  // reset fixture
  //Display.reset();
  //Display.setState(state1);
  //Display.init();

  //var state1b = Display.getState();
  //deepEqual(state1b, state1, "state has been restored to first one with success");

  // set state to old one

});
