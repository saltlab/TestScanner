module("QueryTest");
//This group contains tests about the Charts class as a singleton

var queryFixture = {
	arraysEqual : function(a, b) {
		if (a === b)
			return true;
		if (a === null || b ===  null)
			return false;
		if (a.length != b.length)
			return false;

		// We don't care about the order
		a.sort();
		b.sort();

		for (var i=0; i < a.length; i++) {
			if (a[i] !== b[i])
				return false;
		}
		return true;
	},

	explore : function(array, withProperties, granularity) {
		if (array.length === 0) {
			return {
				"error" : "OK",
				"data": {
					"FR" : {
						"caption" : "French"
					},
					"EN" : {
						"caption" : "English"
					}
				}
			};
		} else if (array.length == 1 && array[0] == "FR") {
			return {
				"error" : "OK",
				"data": {
					"[Traffic]" : {
						"caption" : "Traffic"
					},
					"[Ventes]" : {
						"caption" : "Ventes" //French for "sales". This is in FR cube
					}
				}
			};
		} else if (array.length == 2 && array[0] == "FR" && array[1] == "[Traffic]") {
			return {
				"error" : "OK",
				"data": {
					"[Time]" : {
						"caption" : "Time",
						"type" : "Time"
					},
					"[Zone]" : {
						"caption" : "Zone",
						"type" : "Geometry"
					},
					"[Measures]" : {
						"caption" : "Measures",
						"type" : "Measure"
					}
				}
			};
		} else if (array.length == 3 && array[0] == "FR" && array[1] == "[Traffic]" && array[2] == "[Zone]") {
			return {
				"error" : "OK",
				"data": {
					"[Zone.Reference]" : {
						"caption" : "Reference"
					},
					"[Zone.Name]" : {
						"caption" : "Name"
					}
				}
			};
		} else if (array.length == 3 && array[0] == "FR" && array[1] == "[Traffic]" && array[2] == "[Measures]") {
			return {
				"error" : "OK",
				"data": {
					"[Measures.Mes]" : {
						"caption" : "Measures"
					}
				}
			};
		} else if (array.length == 4 && array[0] == "FR" && array[1] == "[Traffic]" && array[2] == "[Measures]" && array[3] == "[Measures.Mes]" && withProperties === true) {
			return {
				"error" : "OK",
				"data": [
					{
						"id": "[Measures.Mes].[Mes0]",
						"caption" : "Mes0",
						"list-properties" : {
							"unit" : {
								"caption" : "Unit",
								"type" : "Measure"
							}
						}
					}
				]
			};
		} else if (array.length == 4 && array[0] == "FR" && array[1] == "[Traffic]" && array[2] == "[Zone]" && array[3] == "[Zone.Name]" && withProperties === true) {
			return {
				"error" : "OK",
				"data": [
					{
						"id": "[Zone.Name].[Name0]",
						"caption" : "Name0",
						"list-properties" : {
							"Geom" : {
								"caption" : "Geom",
								"type" : "Geometry"
							},
							"surf" : {
								"caption" : "Surface",
								"type" : "Standard"
							}
						}
					},
					{
						"id": "[Zone.Name].[Name1]",
						"caption" : "Name1",
						"list-properties" : {
							"Geom" : {
								"caption" : "Geom",
								"type" : "Geometry"
							},
							"surf" : {
								"caption" : "Surface",
								"type" : "Standard"
							}
						}
					}
				]
			};
		} else if (array.length == 5 && array[0] == "FR" && array[1] == "[Traffic]" && array[2] == "[Measures]" && array[3] == "[Measures.Mes]" && array[4] == "[Measures.Mes].[Mes0]") {
			if (withProperties) {
				return {
					"error" : "OK",
					"data": {
						"[Measures.Mes].[Mes0].[Angleterre]" : {
							"caption" : "Angleterre",
							"unit" : 222
						},
						"[Measures.Mes].[Mes0].[France]" : {
							"caption" : "France",
							"unit" : 333
						},
						"[Measures.Mes].[Mes0].[Espagne]" : {
							"caption" : "Espagne",
							"unit" : 444
						}
					}
				};
			} else {
				return {
					"error" : "OK",
					"data": {
						"[Measures.Mes].[Mes0].[Angleterre]" : {
							"caption" : "Angleterre"
						},
						"[Measures.Mes].[Mes0].[France]" : {
							"caption" : "France"
						},
						"[Measures.Mes].[Mes0].[Espagne]" : {
							"caption" : "Espagne"
						}
					}
				};
			}
		} else if (array.length == 5 && array[0] == "FR" && array[1] == "[Traffic]" && array[2] == "[Zone]" && array[3] == "[Zone.Name]" && array[4] == "[Zone.Name].[Name0]") {
			if (withProperties) {
				return {
					"error" : "OK",
					"data": {
						"[Zone.Name].[Name0].[Angleterre]" : {
							"caption" : "Angleterre",
							"Geom" : "MULTIPOLYGON(0 0, 10 0, 10 10, 0 10, 0 0)",
							"Area" : 120
						},
						"[Zone.Name].[Name0].[France]" : {
							"caption" : "France",
							"Geom" : "MULTIPOLYGON(0 0, 10 0, 10 10, 0 10, 0 0)",
							"Area" : 200
						},
						"[Zone.Name].[Name0].[Espagne]" : {
							"caption" : "Espagne",
							"Geom" : "MULTIPOLYGON(0 0, 10 0, 10 10, 0 10, 0 0)",
							"Area" : 130
						}
					}
				};
			} else {
				return {
					"error" : "OK",
					"data": {
						"[Zone.Name].[Name0].[Angleterre]" : {
							"caption" : "Angleterre"
						},
						"[Zone.Name].[Name0].[France]" : {
							"caption" : "France"
						},
						"[Zone.Name].[Name0].[Espagne]" : {
							"caption" : "Espagne"
						}
					}
				};
			}
		} else if (array.length == 6 && array[0] == "FR" && array[1] == "[Traffic]" && array[2] == "[Zone]" && array[3] == "[Zone.Name]" && array[4] == "[Zone.Name].[Name0]" && array[5] == "[Zone.Name].[Name0].[France]" && withProperties && granularity == 2) {
			return {
				"error" : "OK",
				"data": {
					"[Zone.Name].[Name2].[Pas de Calais]" : {
						"caption" : "Pas de Calais",
						"Geom" : "MULTIPOLYGON(0 0, 10 0, 10 10, 0 10, 0 0)",
						"Area" : 5
					},
					"[Zone.Name].[Name2].[Ile de France]" : {
						"caption" : "Ile de France",
						"Geom" : "MULTIPOLYGON(0 0, 10 0, 10 10, 0 10, 0 0)",
						"Area" : 4
					}
				}
			};
		} else if (array.length == 6 && array[0] == "FR" && array[1] == "[Traffic]" && array[2] == "[Zone]" && array[3] == "[Zone.Name]" && array[4] == "[Zone.Name].[Name0]" && this.arraysEqual(array[5], ["[Zone.Name].[Name2].[Pas de Calais]", "[Zone.Name].[Name2].[Ile de France]"]) && granularity === 0) {
			// Used for getMembersInfos
			if (withProperties) {
				return {
					"error" : "OK",
					"data": {
						"[Zone.Name].[Name2].[Pas de Calais]" : {
							"caption" : "Pas de Calais",
							"Geom" : "MULTIPOLYGON(0 0, 10 0, 10 10, 0 10, 0 0)",
							"Area" : 5
						},
						"[Zone.Name].[Name2].[Ile de France]" : {
							"caption" : "Ile de France",
							"Geom" : "MULTIPOLYGON(0 0, 10 0, 10 10, 0 10, 0 0)",
							"Area" : 4
						}
					}
				};
			} else {
				return {
					"error" : "OK",
					"data": {
						"[Zone.Name].[Name2].[Pas de Calais]" : {
							"caption" : "Pas de Calais"
						},
						"[Zone.Name].[Name2].[Ile de France]" : {
							"caption" : "Ile de France"
						}
					}
				};
			}
		} else if (array.length == 6 && array[0] == "FR" && array[1] == "[Traffic]" && array[2] == "[Zone]" && array[3] == "[Zone.Name]" && array[4] == "[Zone.Name].[Name0]" && array[5] == "[Zone.Name].[Name0].[France]" && !withProperties && granularity == 1) {
			return {
				"error" : "OK",
				"data": {
					"[Zone.Name].[Name1].[Nord]" : {
						"caption" : "Nord"
					},
					"[Zone.Name].[Name1].[Est]" : {
						"caption" : "Est"
					}
				}
			};
		} else {
			return {};
		}
	}
};

Query.queryAPI = queryFixture;

test("queryAPI is set with the fixture", function() {
	expect(1);
	equal(Query.queryAPI, queryFixture, "Query should have the queryFixture as queryAPI");	
		
});

test("Metadatas cache operations at schemas level have the expected behaviour", function() {
	expect(5);
	Query.metadatas = {};
	ok(Query.isCacheEmpty(), "The metadatas cache should be empty before first request");
	ok(!Query.isSchemaInCache("FR"), "There should be no schema in the cache when it's empty");
	Query.cacheSchema("FR", "French");
	ok(Query.isSchemaInCache("FR"), "There should be a FR schema in the cache after insert");
	Query.metadatas.schemas["FR"]["foo"] = "bar";
	Query.cacheSchema("FR", "French");
	equal(Query.metadatas.schemas["FR"]["foo"], "bar",
		"A second call to cacheSchema should not override a present schema in the cache");
	Query.clearCache();
	ok(Query.isCacheEmpty(), "The metadatas cache should be empty after clear");
});

test("Metadatas cache operations at cubes level have the expected behaviour", function() {
	expect(6);
	Query.clearCache();
	Query.getSchemas();
	Query.metadatas.schemas["FR"].cubes = {};
	Query.metadatas.schemas["FR"].cubes["id1"] = {};
	Query.metadatas.schemas["FR"].cubes["id1"]["caption"] = "cube1";

	ok(Query.isCubeInCache("FR", "id1"), "There should be a cube in the FR schema with id id1");
	ok(!Query.isCubeInCache("EN", "id1"), "There should not be a cube in the EN schema with id id1");
	ok(!Query.isCubeInCache("FR", "id2"), "There should not be a cube in the FR schema with id id2");

	ok(Query.isCubesListEmpty("EN"), "The EN schema's list of cubes should be empty");
	Query.cacheCube("EN", "id2", "cubeEN2");
	ok(Query.isCubeInCache("EN", "id2"), "There should be the inserted cube in the EN schema with id id2");
	ok(!Query.isCubesListEmpty("EN"), "The EN schema's list of cubes should be empty");

	Query.clearCache();
});

test("Metadatas cache operations at dimensions level have the expected behaviour", function() {
	expect(14);
	Query.clearCache();
	Query.getCubes("FR");
	ok(Query.isDimensionsListEmpty("FR", "[Traffic]"), "The FR[Traffic] cube's list of dimensions should be empty");

	Query.metadatas.schemas["FR"].cubes["[Traffic]"].dimensions = {};
	Query.metadatas.schemas["FR"].cubes["[Traffic]"].dimensions["[Zone]"] = {"caption" : "Zone", "type" : "Geometry"};
	ok(!Query.isDimensionsListEmpty("FR", "[Traffic]"), "The FR[Traffic] cube's list of dimensions shouldn't be empty after insert");
	ok(Query.isDimensionsListEmpty("FR", "[Ventes]"), "The FR[Ventes] cube's list of dimensions should not be afected by insertion");

	ok(Query.isDimensionInCache("FR", "[Traffic]", "[Zone]"), "The [Zone] dimension of the FR[Traffic] cube should be in cache");
	ok(!Query.isDimensionInCache("FR", "[Traffic]", "[Carrots]"), "The [Carrots] dimension of the FR[Traffic] cube should not be in cache");
	//Insertion in a cube where we already have one dimension
	ok(!Query.isDimensionInCache("FR", "[Traffic]", "[Time]"), "The [Time] dimension of the FR[Traffic] cube should not be in cache");
	Query.cacheDimension("FR", "[Traffic]", "[Time]", "Time", "TimeTitle");
	ok(Query.isDimensionInCache("FR", "[Traffic]", "[Time]"), "The [Time] dimension of the FR[Traffic] cube should be in cache after insertion");

	//Insertion in a cube where we have no dimension
	ok(Query.isDimensionsListEmpty("FR", "[Ventes]"), "The FR[Ventes] cube's list of dimensions should be empty before insertion");
	ok(!Query.isDimensionInCache("FR", "[Ventes]", "[Time]"), "The [Time] dimension of the FR[Ventes] cube should not be in cache");
	Query.cacheDimension("FR", "[Ventes]", "[Time]", "Time", "TimeTitle");
	ok(Query.isDimensionInCache("FR", "[Ventes]", "[Time]"), "The [Time] dimension of the FR[Ventes] cube should be in cache after insertion");
	ok(!Query.isDimensionsListEmpty("FR", "[Ventes]"), "The FR[Ventes] cube's list of dimensions shouldn't be empty after insertion");

	var expectedForTraffic = {
		"[Zone]" : {
			"caption" : "Zone",
			"type" : "Geometry"
		},
		"[Time]" : {
			"caption" : "TimeTitle",
			"type" : "Time"
		}
	};

	var expectedForVentes = {
		"[Time]" : {
			"caption" : "TimeTitle",
			"type" : "Time"
		}
	};

	// Retrieve list of dimensions from cache
	deepEqual(Query.getDimensionsFromCache("FR", "[Traffic]"), expectedForTraffic, "The cache should provide the expected map for dimensions on FR[Traffic]");
	deepEqual(Query.getDimensionsFromCache("FR", "[Ventes]"), expectedForVentes, "The cache should provide the expected map for dimensions on FR[Ventes]");

	Query.clearCache();
	Query.getCubes("FR");

	deepEqual(Query.getDimensionsFromCache("FR", "[Traffic]"), {}, "The cache should provide an empty map for dimensions on FR[Traffic]");
});

test("metadatas cache operations at hierarchies level have the expected behaviour", function() {
	expect(14);
	Query.clearCache();

	Query.getDimensions("FR", "[Traffic]");
	ok(Query.isHierarchiesListEmpty("FR", "[Traffic]", "[Zone]"), "The FR[Traffic][Zone] dimension's list of hierarchies should be empty");
	Query.metadatas.schemas["FR"].cubes["[Traffic]"].dimensions["[Zone]"].hierarchies = {};
	Query.metadatas.schemas["FR"].cubes["[Traffic]"].dimensions["[Zone]"].hierarchies["[Zone.Name]"]  = {"caption" : "Name"};
	ok(!Query.isHierarchiesListEmpty("FR", "[Traffic]", "[Zone]"), "The FR[Traffic][Zone] dimension's list of hierarchies shouldn't be empty after insert");
	ok(Query.isHierarchiesListEmpty("FR", "[Traffic]", "[Time]"), "The FR[Traffic][Time] dimension's list of hierarchies should not be afected by insertion");
	ok(Query.isHierarchyInCache("FR", "[Traffic]", "[Zone]", "[Zone.Name]"), "The [Zone.Name] hierarchy of the FR[Traffic][Zone] dimension should be in cache");
	ok(!Query.isHierarchyInCache("FR", "[Traffic]", "[Zone]", "[Zone.Foo]"), "The [Zone.Foo] hierarchy of the FR[Traffic][Zone] dimension should not be in cache");
	//Insertion in a dimension where we already have one hierarchy
	ok(!Query.isHierarchyInCache("FR", "[Traffic]", "[Zone]", "[Zone.Reference]"), "The [Zone.Reference] hierarchy of the FR[Traffic][Zone] dimension should not be in cache");
	Query.cacheHierarchy("FR", "[Traffic]", "[Zone]", "[Zone.Reference]", "Reference");
	ok(Query.isHierarchyInCache("FR", "[Traffic]", "[Zone]", "[Zone.Reference]"), "The [Zone.Reference] hierarchy of the FR[Traffic][Zone] dimension should be in cache after insertion");
	//Insertion in a cube where we have no dimension
	ok(Query.isHierarchiesListEmpty("FR", "[Traffic]", "[Time]"), "The FR[Traffic][Time] dimension's list of hierarchies should be empty before insertion");
	ok(!Query.isHierarchyInCache("FR", "[Traffic]", "[Time]", "[Time.Instant]"), "The [Time.Instant] hierarchy of the FR[Traffic][Time] dimension should not be in cache");
	Query.cacheHierarchy("FR", "[Traffic]", "[Time]", "[Time.Instant]", "Instant");
	ok(Query.isHierarchyInCache("FR", "[Traffic]", "[Time]", "[Time.Instant]"), "The [Time.Instant] hierarchy of the FR[Traffic][Time] dimension should be in cache after insertion");
	ok(!Query.isHierarchiesListEmpty("FR", "[Traffic]", "[Time]"), "The FR[Traffic][Time] dimension's list of hierarchies should not be empty after insertion");

	var expectedForZone = {
		"[Zone.Name]" : "Name",
		"[Zone.Reference]" : "Reference"
	};
	var expectedForTime = {
		"[Time.Instant]" : "Instant"
	};
	// Retrieve list of hierarchies from cache
	deepEqual(Query.getHierarchiesFromCache("FR", "[Traffic]", "[Zone]"), expectedForZone, "The cache should provide the expected map for hierarchies on FR[Traffic][Zone]");
	deepEqual(Query.getHierarchiesFromCache("FR", "[Traffic]", "[Time]"), expectedForTime, "The cache should provide the expected map for hierarchies on FR[Traffic][Time]");

	Query.clearCache();
	Query.getDimensions("FR", "[Traffic]");
	deepEqual(Query.getHierarchiesFromCache("FR", "[Traffic]", "[Zone]"), {}, "The cache should provide a void map for hierarchies on FR[Traffic][Zone] if no hierarchy has been cached yet");
});

test("Exceptions are raised when operations at cubes level are misused", function() {
	expect(3);
	Query.clearCache();
	throws(
		function() { Query.isCubeInCache("US", "id2"); },
		Query.SchemaNotInDatabaseError,
		"There should not be a schema with id US"
	);

	throws(
		function() { Query.isCubesListEmpty("US"); },
		Query.SchemaNotInDatabaseError,
		"There should not be a schema with id US"
	);

	Query.getSchemas();
	Query.getCubes("FR");
	throws(
		function() { Query.getCubesFromCache("US"); },
		Query.SchemaNotInDatabaseError,
		"There should not be a schema with id US"
	);

});

test("Exceptions are raised when operations at dimensions level are misused", function() {
	expect(9);
	Query.clearCache();

	throws(
		function() { Query.isDimensionsListEmpty("US", "[Fret]"); },
		Query.SchemaNotInDatabaseError,
		"There should not be a schema with id FR"
	);
	Query.getSchemas();
	throws(
		function() { Query.isDimensionsListEmpty("FR", "[Fret]"); },
		Query.CubeNotInDatabaseError,
		"There should not be a cube with id [Fret]"
	);

	Query.clearCache();
	throws(
		function() { Query.isDimensionInCache("US", "[Fret]", "[Carrots]"); },
		Query.SchemaNotInDatabaseError,
		"There should not be a schema with id FR"
	);
	Query.getSchemas();
	throws(
		function() { Query.isDimensionInCache("FR", "[Fret]", "[Carrots]"); },
		Query.CubeNotInDatabaseError,
		"There should not be a cube with id [Fret]"
	);

	throws(
		function() { Query.cacheDimension("FR", "[Fret]", "[Carrots]", "RandomType", "Carrots"); },
		Query.IllegalDimensionTypeError,
		"RandomType should be considered as an illegal dimension type"
	);

	Query.clearCache();
	throws(
		function() { Query.cacheDimension("US", "[Foo]", "[Carrots]", "Time", "Carrots"); },
		Query.SchemaNotInDatabaseError,
		"There should not be a schema with id FR"
	);
	Query.getSchemas();
	throws(
		function() { Query.cacheDimension("FR", "[Foo]", "[Carrots]", "Time", "Carrots"); },
		Query.CubeNotInDatabaseError,
		"There should not be a cube with id [Foo]"
	);

	Query.clearCache();
	throws(
		function() { Query.getDimensionsFromCache("US", "[Ventes]"); },
		Query.SchemaNotInDatabaseError,
		"There should not be a schema with id US"
	);
	Query.getSchemas();
	throws(
		function() { Query.getDimensionsFromCache("FR", "[Foo]"); },
		Query.CubeNotInDatabaseError,
		"There should not be a cube with id [Foo]"
	);

});

test("Exceptions are raised when operations at hierarchies level are misused", function() {
	expect(12);
	Query.clearCache();

	throws(
		function() { Query.isHierarchiesListEmpty("US", "[Traffic]", "[Zone]"); },
		Query.SchemaNotInDatabaseError,
		"There should not be a schema with id US"
	);
	Query.getSchemas();
	throws(
		function() { Query.isHierarchiesListEmpty("FR", "[Foo]", "[Zone]"); },
		Query.CubeNotInDatabaseError,
		"There should not be a cube with id [Foo]"
	);
	Query.getCubes("FR");
	throws(
		function() { Query.isHierarchiesListEmpty("FR", "[Traffic]", "[Foo]"); },
		Query.DimensionNotInDatabaseError,
		"There should not be a dimension with id [Foo]"
	);
	Query.clearCache();
	//Cache methods
	throws(
		function() { Query.isHierarchyInCache("US", "[Traffic]", "[Zone]", "[Zone.Name]"); },
		Query.SchemaNotInDatabaseError,
		"There should not be a schema with id US"
	);
	Query.getSchemas();
	throws(
		function() { Query.isHierarchyInCache("FR", "[Foo]", "[Zone]", "[Zone.Name]"); },
		Query.CubeNotInDatabaseError,
		"There should not be a cube with id [Foo]"
	);
	Query.getCubes("FR");
	throws(
		function() { Query.isHierarchyInCache("FR", "[Traffic]", "[Foo]", "[Zone.Name]"); },
		Query.DimensionNotInDatabaseError,
		"There should not be a dimension with id [Foo]"
	);
	//-------------
	Query.clearCache();
	throws(
		function() { Query.cacheHierarchy("US", "[Traffic]", "[Zone]", "[Zone.Name]", "Name"); },
		Query.SchemaNotInDatabaseError,
		"There should not be a schema with id US"
	);
	Query.getSchemas();
	throws(
		function() { Query.cacheHierarchy("FR", "[Foo]", "[Zone]", "[Zone.Name]", "Name"); },
		Query.CubeNotInDatabaseError,
		"There should not be a cube with id [Foo]"
	);
	Query.getCubes("FR");
	throws(
		function() { Query.cacheHierarchy("FR", "[Traffic]", "[Foo]", "[Zone.Name]", "Name"); },
		Query.DimensionNotInDatabaseError,
		"There should not be a dimension with id [Foo]"
	);
	//-------------
	Query.clearCache();
	throws(
		function() { Query.getHierarchiesFromCache("US", "[Traffic]", "[Zone]"); },
		Query.SchemaNotInDatabaseError,
		"There should not be a schema with id US"
	);
	Query.getSchemas();
	throws(
		function() { Query.getHierarchiesFromCache("FR", "[Foo]", "[Zone]"); },
		Query.CubeNotInDatabaseError,
		"There should not be a cube with id [Foo]"
	);
	Query.getCubes("FR");
	throws(
		function() { Query.getHierarchiesFromCache("FR", "[Traffic]", "[Foo]"); },
		Query.DimensionNotInDatabaseError,
		"There should not be a dimension with id [Foo]"
	);
});

test("getSchemas", function() {
	expect(6);
	var expectedMap = {
		"FR" : "French",
		"EN" : "English"
	};
	deepEqual(Query.getSchemas(), expectedMap, "getSchemas should return a map of id:caption sets");
	deepEqual(Query.getSchemasFromCache(), expectedMap, "The cache should be able to give the expected map");
	deepEqual(Query.getSchemas(), expectedMap, "Multiple calls to getSchemas should not cause problems and use cache");
	ok(Query.isSchemaInCache("FR"), "There should be a FR schema in the cache after insert");
	ok(Query.isSchemaInCache("EN"), "There should be a EN schema in the cache after insert");
	equal(Query.metadatas.schemas["EN"]["caption"], "English", "The caption should be set properly");

	Query.clearCache();
});

test("getCubes", function() {
	expect(5);
	var expectedMap = {
		"[Traffic]" : "Traffic",
		"[Ventes]" : "Ventes"
	};
	Query.clearCache();

	deepEqual(Query.getCubes("FR"), expectedMap, "getCubes in the FR schema should return a map of id:caption sets");
	deepEqual(Query.getCubesFromCache("FR"), expectedMap, "The cache should be able to give the expected map");
	deepEqual(Query.getCubes("FR"), expectedMap, "Multiple calls to getCubes should not cause problems and use cache");
	ok(Query.isCubeInCache("FR", "[Traffic]"), "There should be a Traffic cube in a FR schema in the cache after insert");
	equal(Query.metadatas.schemas["FR"].cubes["[Traffic]"]["caption"], "Traffic", "The caption should be set properly");

	Query.clearCache();
});

test("getDimensions", function() {
	expect(6);
	var expectedMapFromCache = queryFixture.explore(new Array("FR", "[Traffic]")).data;
	var expectedMap = {
		"[Time]" : {
			"caption" : "Time",
			"type" : "Time"
		},
		"[Zone]" : {
			"caption" : "Zone",
			"type" : "Geometry"
		}
	};
	Query.clearCache();

	deepEqual(Query.getDimensions("FR", "[Traffic]"), expectedMap, "getDimensions in the FR[Traffic] cube should return a map of id:{caption, type} sets");
	deepEqual(Query.getDimensionsFromCache("FR", "[Traffic]"), expectedMapFromCache, "The cache should be able to give the expected map");
	deepEqual(Query.getDimensions("FR", "[Traffic]"), expectedMap, "Multiple calls to getDimensions should not cause problems and use cache");
	ok(Query.isDimensionInCache("FR", "[Traffic]", "[Zone]"), "There should be a Zone dimension in a FR[Traffic] cube in the cache after insert");
	equal(Query.metadatas.schemas["FR"].cubes["[Traffic]"].dimensions["[Zone]"]["caption"], "Zone", "The caption should be set properly");
	equal(Query.metadatas.schemas["FR"].cubes["[Traffic]"].dimensions["[Zone]"]["type"], "Geometry", "The type should be set properly");

	Query.clearCache();
});

test("getGeoDimension", function() {
	expect(1);
	var expected = "[Zone]";
	Query.clearCache();

	equal(Query.getGeoDimension("FR", "[Traffic]"), expected, "The geographic dimension should be called [Zone]");

	Query.clearCache();
});

test("getTimeDimension", function() {
	expect(1);
	var expected = "[Time]";
	Query.clearCache();

	equal(Query.getTimeDimension("FR", "[Traffic]"), expected, "The time dimension should be called [Time]");

	Query.clearCache();
});

test("getHierarchies", function() {
	expect(3);
	var expectedMap = {
		"[Zone.Reference]" : "Reference",
		"[Zone.Name]" : "Name"
	};
	Query.clearCache();

	deepEqual(Query.getHierarchies("FR", "[Traffic]", "[Zone]"), expectedMap, "getHierarchies in the FR[Traffic][Zone] dimension should return a map of id:caption sets");
	deepEqual(Query.getHierarchiesFromCache("FR", "[Traffic]", "[Zone]"), expectedMap, "The cache should be able to give the expected map");
	deepEqual(Query.getHierarchies("FR", "[Traffic]", "[Zone]"), expectedMap, "Multiple calls to getHierarchies should not cause problems and use cache");

	Query.clearCache();
});

test("getLevels", function() {
	expect(3);
	var expected= [
		"Name0",
		"Name1"
	];
	Query.clearCache();

	deepEqual(Query.getLevels("FR", "[Traffic]", "[Zone]", "[Zone.Name]"), expected, "getLevels in the FR[Traffic][Zone][Zone.Name] hierarchy should return an array of captions");
	deepEqual(Query.getLevelsFromCache("FR", "[Traffic]", "[Zone]", "[Zone.Name]"), expected, "The cache should be able to give the expected array");
	deepEqual(Query.getLevels("FR", "[Traffic]", "[Zone]", "[Zone.Name]"), expected, "Multiple calls to getLevels should not cause problems and use cache");

	Query.clearCache();
});

test("getProperties", function() {
	expect(4);
	var expected= {
		"Geom" : {
			"caption" : "Geom",
			"type" : "Geometry"
		},
		"surf" : {
			"caption" : "Surface",
			"type" : "Standard"
		}
	};
	Query.clearCache();

	ok(!Query.isPropertiesListEmpty("FR", "[Traffic]", "[Zone]", "[Zone.Name]", 0), "The properties should have been loaded in cache with the parent level");
	deepEqual(Query.getProperties("FR", "[Traffic]", "[Zone]", "[Zone.Name]", 0), expected, "getProperties in the given level should return a list of objects");
	deepEqual(Query.getPropertiesFromCache("FR", "[Traffic]", "[Zone]", "[Zone.Name]", 0), expected, "The cache should be able to give the expected object");
	deepEqual(Query.getProperties("FR", "[Traffic]", "[Zone]", "[Zone.Name]", 0), expected, "Multiple calls to getProperties should not cause problems and use cache");

	Query.clearCache();
});

test("getMembers on first members of a level", function() {
	expect(2);
	var expectedWithProps = {
		"[Zone.Name].[Name0].[Angleterre]" : {
			"caption" : "Angleterre",
			"Geom" : {
				'coordinates': new Array(
					new Array(
					new Array(
						new Array(0, 0),
						new Array(10, 0),
						new Array(10, 10),
						new Array(0, 10),
						new Array(0, 0)
					)
					)
				),
				'type' : 'MultiPolygon'
			},
			"Area" : 120
		},
		"[Zone.Name].[Name0].[France]" : {
			"caption" : "France",
			"Geom" : {
				'coordinates': new Array(
					new Array(
					new Array(
						new Array(0, 0),
						new Array(10, 0),
						new Array(10, 10),
						new Array(0, 10),
						new Array(0, 0)
					)
					)
				),
				'type' : 'MultiPolygon'
			},
			"Area" : 200
		},
		"[Zone.Name].[Name0].[Espagne]" : {
			"caption" : "Espagne",
			"Geom" : {
				'coordinates': new Array(
					new Array(
					new Array(
						new Array(0, 0),
						new Array(10, 0),
						new Array(10, 10),
						new Array(0, 10),
						new Array(0, 0)
					)
					)
				),
				'type' : 'MultiPolygon'
			},
			"Area" : 130
		}
	};

	var expectedWithoutProps = queryFixture.explore(new Array("FR", "[Traffic]", "[Zone]", "[Zone.Name]", "[Zone.Name].[Name0]"), false).data;

	Query.clearCache();

	deepEqual(Query.getMembers("FR", "[Traffic]", "[Zone]", "[Zone.Name]", 0, true), expectedWithProps, "getMembers in the given level should return a list of objects with properties");
	deepEqual(Query.getMembers("FR", "[Traffic]", "[Zone]", "[Zone.Name]", 0), expectedWithoutProps, "getMembers in the given level should return a list of objects without properties");

	Query.clearCache();
});

test("getMembers with parent members", function() {
	expect(2);
	var expectedWithProps = {
		"[Zone.Name].[Name2].[Pas de Calais]" : {
			"caption" : "Pas de Calais",
			"Geom" : {
				'coordinates': new Array(
					new Array(
					new Array(
						new Array(0, 0),
						new Array(10, 0),
						new Array(10, 10),
						new Array(0, 10),
						new Array(0, 0)
					)
					)
				),
				'type' : 'MultiPolygon'
			},
			"Area" : 5
		},
		"[Zone.Name].[Name2].[Ile de France]" : {
			"caption" : "Ile de France",
			"Geom" : {
				'coordinates': new Array(
					new Array(
					new Array(
						new Array(0, 0),
						new Array(10, 0),
						new Array(10, 10),
						new Array(0, 10),
						new Array(0, 0)
					)
					)
				),
				'type' : 'MultiPolygon'
			},
			"Area" : 4
		}
	};

	var expectedWithoutProps = queryFixture.explore(new Array("FR", "[Traffic]", "[Zone]", "[Zone.Name]", "[Zone.Name].[Name0]", "[Zone.Name].[Name0].[France]"), false, 1).data;

	Query.clearCache();

	deepEqual(Query.getMembers("FR", "[Traffic]", "[Zone]", "[Zone.Name]", 0, true, "[Zone.Name].[Name0].[France]", 2), expectedWithProps, "getMembers in the given level should return a list of objects with properties");
	deepEqual(Query.getMembers("FR", "[Traffic]", "[Zone]", "[Zone.Name]", 0, false, "[Zone.Name].[Name0].[France]"), expectedWithoutProps, "getMembers in the given level should return a list of objects without properties");

	Query.clearCache();
});

test("getMembersInfos with properties", function() {
	expect(1);
	var membersWanted = ["[Zone.Name].[Name2].[Pas de Calais]", "[Zone.Name].[Name2].[Ile de France]"];
	var expected = {
		"[Zone.Name].[Name2].[Pas de Calais]" : {
			"caption" : "Pas de Calais",
			"Geom" : {
				'coordinates': new Array(
					new Array(
					new Array(
						new Array(0, 0),
						new Array(10, 0),
						new Array(10, 10),
						new Array(0, 10),
						new Array(0, 0)
					)
					)
				),
				'type' : 'MultiPolygon'
			},
			"Area" : 5
		},
		"[Zone.Name].[Name2].[Ile de France]" : {
			"caption" : "Ile de France",
			"Geom" : {
				'coordinates': new Array(
					new Array(
					new Array(
						new Array(0, 0),
						new Array(10, 0),
						new Array(10, 10),
						new Array(0, 10),
						new Array(0, 0)
					)
					)
				),
				'type' : 'MultiPolygon'
			},
			"Area" : 4
		}
	};

	Query.clearCache();

	deepEqual(Query.getMembersInfos("FR", "[Traffic]", "[Zone]", "[Zone.Name]", 0, membersWanted, true), expected, "getMembersInfos on the given set of IDs should return a list of objects with members having properties");

	Query.clearCache();
});

test("getMembersInfos without properties", function() {
	expect(1);
	var membersWanted = ["[Zone.Name].[Name2].[Pas de Calais]", "[Zone.Name].[Name2].[Ile de France]"];
	var expected = {
		"[Zone.Name].[Name2].[Pas de Calais]" : {
			"caption" : "Pas de Calais"
		},
		"[Zone.Name].[Name2].[Ile de France]" : {
			"caption" : "Ile de France"
		}
	};

	Query.clearCache();

	deepEqual(Query.getMembersInfos("FR", "[Traffic]", "[Zone]", "[Zone.Name]", 0, membersWanted, false), expected, "getMembersInfos on the given set of IDs should return a list of objects with members without properties");

	Query.clearCache();
});

test("getMeasures", function() {
	expect(1);
	var expected= queryFixture.explore(new Array("FR", "[Traffic]", "[Measures]", "[Measures.Mes]", "[Measures.Mes].[Mes0]"), false).data;

	Query.clearCache();

	deepEqual(Query.getMesures("FR", "[Traffic]"), expected, "getMesures should return the expected list of objects without properties");

	Query.clearCache();
});

test("getGeoProperty", function() {
	expect(2);
	var expected= "Geom";

	Query.clearCache();

	deepEqual(Query.getGeoProperty("FR", "[Traffic]", "[Zone]", "[Zone.Name]"), expected, "getGeoProperty should return the ID of the first property with type to Geometry");
	deepEqual(Query.getGeoProperty("FR", "[Traffic]", "[Zone]", "[Zone.Name]"), expected, "Multiple calls to getGeoProperty should not cause problems and use cache");

	Query.clearCache();
});

test("Only legal dimension types are allowed", function() {
	expect(5);
	ok(Query.isAllowedDimensionType('Time'), "Time should be a legal dimension type");
	ok(Query.isAllowedDimensionType("Measure"), "Measure should be a legal dimension type");
	ok(Query.isAllowedDimensionType("Standard"), "Standard should be a legal dimension type");
	ok(Query.isAllowedDimensionType("Geometry"), "Geometry should be a legal dimension type");

	ok(!Query.isAllowedDimensionType("Geome"), "Geome should be a legal dimension type");
});

