module("FactSelectorTest");
  // This group contains tests about the FactSelector class

  test("Initialization", function() {
    FactSelector.init("#qunit-fixture", 'Cubes available', 'Measures available');
    equal(FactSelector.cubes.intro, 'Cubes available',"The text to introduce cubes available is the good one");
    equal(FactSelector.measures.intro,'Measures available', "The text to introduce measures available is the good one");
  });
  
  test("Div containing cubes and measures are correctly filled", function() {
    FactSelector.init("#qunit-fixture", 'Cubes available', 'Measures available');
    var data = {
       "12" :
       {
         "caption" : "cubeCaption12",
         "measures" :
         {
           "measureID" : {"caption" : "measureCaption"}, 
           "measureID2" : {"caption" : "measureCaption2"},
         }
      },
      "13" : {
         "caption" : "cubeCaption13",
         "measures" :
         {
           "measureID" : {"caption" : "measureCaption"}, 
           "measureID2" : {"caption" : "measureCaption2"},
           "measureID3" : {"caption" : "measureCaption3"},
         }
     }
    };
/*
    FactSelector.setCallback(function(cubeCallback, measureCallback) { return that.setCubeAndMeasureCallback(cubeCallback, measureCallback); });
    FactSelector.setSelectedCube(cube);
    FactSelector.setSelectedMeasure(measure);
    */

    FactSelector.setMetadata(data);
    //FactSelector.showCubes();
    FactSelector.setSelectedCube("12");
    equal($("#qunit-fixture div:first li").length,'2',"The div contains all the cubes in the data");
    equal($("#qunit-fixture div:last li").length,'2',"The div of cube 12 contains the 2 measures");
    FactSelector.setSelectedCube("13")
    equal($("#qunit-fixture div:last li").length,'3',"The div of cube 13 contains the 3 measures");
  });
  
  test("The Callback should set the good values", function() {
    FactSelector.init("#qunit-fixture", 'Cubes available', 'Measures available');
    var data = {
       "12" :
       {
         "caption" : "cubeCaption12",
         "measures" :
         {
           "measureID" : {"caption" : "measureCaption"}, 
           "measureID2" : {"caption" : "measureCaption2"},
         }
      },
      "13" : {
         "caption" : "cubeCaption13",
         "measures" :
         {
           "measureID" : {"caption" : "measureCaption"}, 
           "measureID2" : {"caption" : "measureCaption2"},
         }
     }
    };
    var testA = null;
    var testB = null;
    FactSelector.setMetadata(data);
    FactSelector.setCallback(function(a,b) {
			      testA = a;
			      testB= b;
    });
    FactSelector.selectCube('12');
    FactSelector.selectMeasure('13');
    equal(testA, '12', "The selected cube has been set with the callback function");
    FactSelector.clearAll();
  });
  
  test("The clearAll function erases everything", function() {
    FactSelector.init("#qunit-fixture", 'Cubes available', 'Measures available');
    var data = {
       "12" :
       {
         "caption" : "cubeCaption12",
         "measures" :
         {
           "measureID" : {"caption" : "measureCaption"}, 
           "measureID2" : {"caption" : "measureCaption2"},
         }
      },
      "13" : {
         "caption" : "cubeCaption13",
         "measures" :
         {
           "measureID" : {"caption" : "measureCaption"}, 
           "measureID2" : {"caption" : "measureCaption2"},
         }
     }
    };
    FactSelector.setMetadata(data);
    equal(FactSelector.introCubes,'Cubes available', "The fields are not empty after initialization");
    FactSelector.clearAll();
    equal(FactSelector.container, null, "The container is empty after clearAll()");
    equal(FactSelector.cube, null, "The cube id is empty after clearAll()");
    equal(FactSelector.cubes, null, "The div containing cubes list is empty after clearAll()");
    equal(FactSelector.measures, null, "The div containing measures list is empty after clearAll()");
    equal(FactSelector.introCubes, null, "The string to introduce list of cubes is empty after clearAll()");
    equal(FactSelector.introMeasures, null, "The string to introdice list of measures is empty after clearAll()");
    equal(FactSelector.data, null, "The object containing cubes and measures is empty after clearAll()");
    equal(FactSelector.callback, null, "The callback object is empty after clearAll()");  
  });