var cube = function (CubeId, Caption) {
  var _cubeId = CubeId;
  var _caption = Caption;

  var _currentDimension;
  var _currentHierarchy;

  var _dimensions = {
    "_measures": {
      caption: "Measures",
      type: "Measure",
      hierarchies: {
        "_measures": {
          caption: "Measures",
          levels: [
            {id: "M1", caption: "Measures",
            members: {}}
          ]
        }
      }
    }
  };

  var _cube = {};

  function copyObject(obj) {
    return JSON.parse(JSON.stringify(obj));
  }

  function except(obj, keys) {
    var filteredObj = {};
    for (var k in obj) {
      if (keys.indexOf(k) < 0) {
        filteredObj[k] = obj[k];
      }
    }
    return filteredObj;
  }

  _cube.id = function () {
    return CubeId;
  };

  _cube.caption = function () {
    return Caption;
  };

  _cube.size = function () {
    var n = 1;
    for (var dim in _dimensions) {
      var d = _dimensions[dim].hierarchies[Object.keys(_dimensions[dim].hierarchies)[0]];
      // Get the finest level of each dimension
      n *= Object.keys(d.levels[d.levels.length - 1].members).length;
    }
    return n;
  };


  _cube.dimensions = function () {
    var dims = {};
    for (var d in _dimensions) {
      dims[d] = except(_dimensions[d], ["hierarchies"]);
    }
    return dims;
  };


  _cube.dimensionFromHierarchy = function (hierarchy) {
    for (var dim in _dimensions) {
      if (_dimensions[dim].hierarchies[hierarchy] !== undefined) {
        return dim;
      }
    }
  };

  _cube.hierarchies = function (dim) {
    var hierarchies = {};
    for (var hie in _dimensions[dim].hierarchies) {
      hierarchies[hie] = except(_dimensions[dim].hierarchies[hie], ["levels"]);
    }
    return hierarchies;
  };

  _cube.members = function (dim, hie, lev, properties) {
    if (typeof lev == "string") {
      _dimensions[dim].hierarchies[hie].levels.map(function (level, index) {
        if (level.id === lev)
          lev = index;
      });
    }
    properties = properties || false;
    return _dimensions[dim].hierarchies[hie].levels[lev].members;
  };

  _cube.childs = function (dim, hie, lev, member) {
    if (typeof lev == "string") {
      _dimensions[dim].hierarchies[hie].levels.map(function (level, index) {
        if (level.id === lev)
          lev = index;
      });
    }
    var childMembers = {};
    _dimensions[dim].hierarchies[hie].levels[lev].members[member].children.forEach(function (c) {
      childMembers[c] = copyObject(_dimensions[dim].hierarchies[hie].levels[lev + 1].members[c]);
    });
    return childMembers;
  };

  _cube.levels = function (dim, hie) {
    return _dimensions[dim].hierarchies[hie].levels.map(function (l) { return except(l, ["members"]); });
  };

  _cube.measures = function () {
    return _dimensions._measures.hierarchies._measures.levels[0].members;
  };

  // Cube building functions

  /**
   *
   * Adds a measure to the cube
   *
   * @param {String} id
   *  The id of the measure
   * @param {String} caption
   *  A caption describing the measure
   * @param {Function} fun
   *  A function to generate the data for the measure, this function should take a number
   *  (integer) and return a number.
   */
  _cube.measure = function (id, caption, fun) {
    _dimensions._measures.hierarchies._measures.levels[0].members[id] = {caption: caption, fun: fun};
    return _cube;
  };

  /**
   *
   * Adds a dimension to the cube
   *
   * @param {String} dimId
   *  The id of the dimension
   * @param {String} caption
   *  A caption describing the dimension
   * @param {String} type
   *  The type of the dimension (Geometry, Time, Standard)
   */
  _cube.dimension = function (dimId, caption, type) {
    _currentDimension = dimId;
    _dimensions[dimId] = {caption: caption, type: type, hierarchies: {}};
    return _cube;
  };

  /**
   * Adds a level to the last added hierarchy
   *
   * @param {String} hierarchyId
   *  The id of the hierarchy
   *
   * @param {String} caption
   *  A caption describing the hierarchy
   */
  _cube.hierarchy = function (hierarchyId, caption) {
    _currentHierarchy = hierarchyId;
    _dimensions[_currentDimension].hierarchies[hierarchyId] = {caption: caption, levels: []};
    return _cube;
  };

  /**
   * Adds a level to the last added dimension
   *
   * @param {String} levelId
   *  The id of the level
   *
   * @param {String} caption
   *  A caption describing the level
   */
  _cube.level = function (levelId, caption) {
    _dimensions[_currentDimension].hierarchies[_currentHierarchy]
      .levels.push({id: levelId, caption: caption, "list-properties" : {}, members: {}});
    return _cube;
  };

  /**
   * adds a property to the last added level in the last added dimension
   *
   * @param {string} propertyId
   *  the id of the property
   * @param {string} caption
   *  a caption describing the property
   * @param {string} type
   *  the type of the property (geometry, standard)
   */
  _cube.property = function (propertyId, caption, type) {
    var hie = _dimensions[_currentDimension].hierarchies[_currentHierarchy];
    hie.levels[hie.levels.length - 1]["list-properties"][propertyId] = {caption: caption, type: type};
    return _cube;
  };

  /**
   * Adds a member to the last added level in the last added dimension
   *
   * @param {String} memberId
   *  The id of the member
   * @param {String} caption
   *  A caption describing the member
   * @param {Object} properties
   *  The object containing the properties associated with the member
   *  {propertyId: value, ...}
   * @param {Array} children
   *  The list of the members id of the children members of this member
   */
  _cube.member = function (memberId, caption, properties, children) {
    var hie = _dimensions[_currentDimension].hierarchies[_currentHierarchy];
    var lev = hie.levels[hie.levels.length - 1];
    lev.members[memberId] = {"caption": caption, "children": children};
    for (var property in properties) {
      lev.members[memberId][property] = properties[property];
    }
    return _cube;
  };

  return _cube;
};

function fileContent (filepath) {
  var data;
  $.ajax({
    url: filepath,
    async: false,
    success: function (res) {
      data = res;
    },
  });

  return data;
}

function getData(cube, measures, hierarchies) {
  crossmembers = [{}];
  Object.keys(hierarchies).forEach(function (hierarchy) {
    var dimension = cube.dimensionFromHierarchy(hierarchy);
    crossmembers = cross(crossmembers, hierarchies[hierarchy].members, dimension);
  });

  var cubeSize = cube.size();
  measures.forEach(function (measure, index) {
    crossmembers.forEach(function (crossmember, indexcm) {
      crossmember[measure] = cube.measures()[measure].fun(indexcm);
    });
  });

  return crossmembers;
}

function cross(objects, newMembers, dimension) {
  var result = [];
  newMembers.forEach(function (newMember) {
    objects.forEach(function (object) {
      var obj = copyObject(object);
      obj[dimension] = newMember;
      result.push(obj);
    });
  });
  return result;
}

function copyArray(arr) {
  return arr.slice(0, Infinity);
}

function copyObject(obj) {
  return JSON.parse(JSON.stringify(obj));
}

function generateAPI(cubes) {
  var _cubes = {};
  cubes.forEach(function (c) {
    _cubes[c.id()] = c;
  });
  return {
    cube : null,
    measures : [],
    hierarchies : {},

    drill : function(idCube) {
      this.cube = idCube;
      return this;
    },
    push : function(idMeasure) {
      if (this.measures.indexOf(idMeasure) < 0)
        this.measures.push(idMeasure);
      return this;
    },
    pull : function(idMeasure) {
      index = this.measures.indexOf(idMeasure);
      if (index != -1) {
        this.measures.splice(index, 1);
      }
      return this;
    },
    slice : function(idHierarchy, members, range) {
      range = range || false;
      this.hierarchies[idHierarchy] = {"members" : members, "range" : range, "dice" : false};
      return this;
    },
    dice : function(hierarchies) {
      for (var i = 0; i < hierarchies.length; i++) {
        if (typeof this.hierarchies[hierarchies[i]] != "undefined")
          this.hierarchies[hierarchies[i]].dice = true;
      }
      return this;
    },
    project : function(idHierarchie) {
      delete this.hierarchies[idHierarchie];
      return this;
    },
    execute : function() {
      return {error: "OK", data: getData(_cubes[this.cube], this.measures, this.hierarchies)};
    },
    clear : function() {
      this.cube = null;
      this.measures = [];
      this.hierarchies = {};
      return this;
    },

    explore : function(root, withProperties, granularity) {
        switch (root.length) {
          case 0:
            return {error: "OK", data: {"Olap": {caption: "Olap Schema"}}};
          case 1:
            var cubes = {};
            for (var cube in _cubes) {
              cubes[_cubes[cube].id()] = {caption: _cubes[cube].caption()};
            }
            return {error: "OK", data: cubes};
          case 2:
            return {error: "OK", data: _cubes[root[1]].dimensions()};
          case 3:
            return {error: "OK", data: _cubes[root[1]].hierarchies(root[2])};
          case 4:
            return {error: "OK", data: _cubes[root[1]].levels(root[2], root[3])};
          case 5: // getMembers
            return {error: "OK", data: _cubes[root[1]].members(root[2], root[3], root[4])};
          case 6: // getMembers
            var allMembers = _cubes[root[1]].members(root[2], root[3], root[4]);
            var result = {};
            if (typeof root[5] == "object") {
              // If it is a list of members
              for (var member in allMembers) {
                if (root[5].indexOf(member) >= 0) {
                  result[member] = allMembers[member];
                }
              }
            } else {
              // If it is a given member
              result = _cubes[root[1]].childs(root[2], root[3], root[4], root[5]);
            }
            return {error: "OK", data: result};
        }
    },
  };
}
