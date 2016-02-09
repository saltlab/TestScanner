var Query = {

  init : function (queryApi) {
  },

  getSchemas : function () {
    return {"fr" : "Fr"};
  },


  getCubes : function(idSchema) {
    return {
      "aCube" : "Cube"
    };
  },

  getMesures : function (idSchema, idCube) {
    return {
      'Raised' : 'Loaded',
      'unloaded' : 'Unloaded'
    };
  },

  getCubesAndMeasures : function (idSchema) {
    return {
      "aCube" : {
        "caption": "Goods Quantity",
        "measures" : {
          'Raised' : 'Loaded',
          'unloaded' : 'Unloaded'
        }
      }
    };
  },

  getDimensions : function (idSchema, idCube) {
    return {
      "geo" : {
        "caption" : "Zone",
        "type" : "Geo"
      },
      "RoundClassDescr" : {
        "caption" : "Time",
        "type" : "Time"
      }
    };
  },

  getGeoDimension : function (idSchema, idCube) {
    return "geo";
  },

  getTimeDimension : function (idSchema, idCube) {
    return "RoundClassDescr";
  },

  getGeoProperty : function (idSchema, idCube, idDimension, idHierarchy) {
    return "geom";
  },

  getHierarchies : function (idSchema, idCube, idDimension) {
    return {"hier1" : "Hier 1" , "hier2" : "Hier 2"};
  },

  getLevels : function (idSchema, idCube, idDimension, idHierarchy) {
    return {"a" : "b", "b" : "c"};
  },

  getMembers : function (idSchema, idCube, idDimension, idHierarchy, indexLevel, withProperties, parentMember, descendingLevel) {
    if (idDimension == 'RoundClassDescr') {
      return {
        "First Round" : { "caption" : "Jan  2014" },
        "Seed Round" : { "caption" : "Fev  2014" },
        "Later Stage" : { "caption" : "Mar  2014" },
        "Second Round" : { "caption" : "Avr  2014" },
        "Corporate" : { "caption" : "Mai  2014" },
        "Individual" : { "caption" : "Juin 2014" },
        "ACQ Financing" : { "caption" : "Juil 2014" },
        "Restart" : { "caption" : "Aout 2014" },
        "Unclassified" : { "caption" : "Sept 2014" }
      };
    } else {
      var data;

      if (parentMember === undefined) {
        $.get("fixture/europe.geo.json", function (data2) {data = data2;});
      }
      else {
        $.get("fixture/france.geo.json", function (data2) {data = data2;});
      }
      return data;
    }
  },

  getMembersInfos : function (idSchema, idCube, idDimension, idHierarchy, indexLevel, membersIds, withProperties) {
    if (idDimension == 'RoundClassDescr') {
      return {
        "First Round" : { "caption" : "Jan  2014" },
        "Seed Round" : { "caption" : "Fev  2014" },
        "Later Stage" : { "caption" : "Mar  2014" },
        "Second Round" : { "caption" : "Avr  2014" },
        "Corporate" : { "caption" : "Mai  2014" },
        "Individual" : { "caption" : "Juin 2014" },
        "ACQ Financing" : { "caption" : "Juil 2014" },
        "Restart" : { "caption" : "Aout 2014" },
        "Unclassified" : { "caption" : "Sept 2014" }
      };
    } else {
      var data;

      if (indexLevel === 0) {
        $.get("fixture/europe.geo.json", function (data2) {data = data2;});
      }
      else {
        $.get("fixture/france.geo.json", function (data2) {data = data2;});
      }
      return data;
    }
  },

  getProperties : function (idSchema, idCube, idDimension, idHierarchy, indexLevel) {
    return {
      "geom" : {
        "caption" : "Geom",
        "type" : "Geometry"
      },
      "surf" : {
        "caption" : "Surface",
        "type" : "Standard"
      }
    }
  },

  dataset : "dataVC.json",

  drill : function(idCube) {
  },

  push : function(idMeasure) {
  },

  pull : function(idMeasure) {
  },

  slice : function(idHierarchy, members, range) {
    if ($.inArray("FR1", members) >= 0) {
      this.dataset = "dataVCFr.json";
    }

    if ($.inArray("FR", members) >= 0) {
      this.dataset = "dataVC.json";
    }
  },

  project : function(idHierarchy) {
  },

  switch : function(hierarchies) {
  },

  filter : function(idHierarchy, members, range) {
  },

  rank : function(hierarchy) {
  },

  execute : function() {
    var data;
    $.get("fixture/"+this.dataset, function (data2) {data = data2;});
    return data;
  },

   dice : function(hierarchies) {
  },

  clear : function() {
  }

};
