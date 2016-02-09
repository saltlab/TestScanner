/*!
 *  crossfilter-server 1.1
 *  https://github.com/loganalysis/analytics-js
 *  Copyright 2014
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

(function(exports){

  exports.crossfilterServer = crossfilterServer;

  ////////////////////////////////////////
/**

## Crossfilter Server

Everything in Crossfilter Server is scoped under the `crossfilterServer`
namespace, which is also the [constructor](#crossfilterServer).

### crossfilterServer(*metadata*)

Constructs a new crossfilterServer. A CrossfilterServer represents a
multi-dimensional dataset **stored and queried server side**.

It is initialized with metadata describing the cube we will query,
following this format:

```js
metadata = {
  api : APIObject,
  schema : "<schemaId>",
  cube : "<cubeId>",
  measures : ["<defaultMesureId>","<mesure2Id>",..],
  dimensions : {
    <dim1Id> : {
      hierarchy : "<hierarchyId>",
      level : <levelIntId>,
      members : ["<member1Id>", "<member2Id>", "<member3Id>", ...]
    },
    <dim2Id> : {
      ...
    }
  }
}
```

Note that you will pass a reference to an API Object that will allow
`crossfilterServer` to query the server. This API should implement the
specifications described at
[Javascript Query API for OLAP](https://github.com/loganalysis/mandoline/wiki/Javascript-Query-API-for-OLAP)
for this class to work. You can also rewrite the function `getData()`
to adapt it to your own API.

**/

function crossfilterServer(metadata) {

  var api = metadata.api;
  var dimensions = metadata.dimensions;

  // check validity of metadata
  if (typeof api               != "object" ||
      typeof metadata.schema   != "string" ||
      typeof metadata.cube     != "string" ||
      typeof metadata.measures != "object" ||
      typeof dimensions        != "object" ||
      Object.keys(dimensions).length < 1)
  {
    throw "Metadata are malformed";
  }

  var crossfilterServerObj = {
    dimension: dimension,
    groupAll: groupAll,
    size: size
  };

  // this will store the filters
  var filters = {};

  // this will store the datasets for each group
  var datasets = {};

  /**
   * Empty the stored datasets
   * @private
   */
  function emptyDatasets() {
    for (var dim in datasets) {
      delete datasets[dim];
    }
  }

  /**
   * Get current slice for a dimension
   * @private
   */
  function getSlice(dimensionName) {
    if (typeof filters[dimensionName] != "undefined") {
      return filters[dimensionName];
    }
    else {
      return dimensions[dimensionName].members;
    }
  }

  /*
   * get data for this dimension
   * @param {String} [dimensionName=null] - dimension that won't be filtered
   * @param {Boolean} [dice=true] - dice the dimension `dimensionName`
   * @param {Array<String>} [measures] - list of measures to keep
   * @private
   */
  function getData(dimensionName, dice, measures) {
    dimensionName = (dimensionName === undefined || dimensionName === null) ? "_all" : dimensionName;
    dice          = (dice          === undefined) ? true : dice;
    measures      = (measures      === undefined) ? [metadata.measures[0]] : measures;
    datasetKey    = measures.sort().join(',');

    if (typeof datasets[dimensionName] == "undefined" || typeof datasets[dimensionName][datasetKey] == "undefined") {

      // init query
      api.clear();
      api.drill(metadata.cube);
      for (var i in measures)
        api.push(measures[i]);

      // Slice cube according to current slices + filters (exect current dim. filters)
      for (var dim in dimensions) {
        if (dim == dimensionName)
          api.slice(dimensions[dim].hierarchy, dimensions[dimensionName].members);
        else
          api.slice(dimensions[dim].hierarchy, getSlice(dim));
      }

      // Dice on current dimension
      if (dimensionName != "_all" && dice)
        api.dice([dimensions[dimensionName].hierarchy]);

      // run query & format data like CF does & sort by key
      var data = api.execute().map(function(d) {
        var out = {
          "key" : d[dimensionName]
        };
        if (measures.length == 1) {
          out.value = d[measures[0]];
        }
        else {
          out.value = {};
          for (var i in measures)
            out.value[measures[i]] = d[measures[i]];
        }
        return out;
      }).sort(function (a, b) {
        if (a.key > b.key) {
          return 1;
        } else {
          return -1;
        }
      });

      if (typeof datasets[dimensionName] == "undefined")
        datasets[dimensionName] = {};
      datasets[dimensionName][datasetKey] = data;
    }

    return datasets[dimensionName][datasetKey];
  }


  /**
  ### crossfilterServer.size()

  Returns the number of records in the crossfilterServer, independent of
  any filters.
  **/
  function size() {
    out = 1;
    for (var dim in dimensions) {
      out *= dimensions[dim].members.length;
    }
    return out;
  }

  /**
  ### crossfilterServer.groupAll()

  A convenience function for grouping all records and reducing to a single
  value. See **dimension.groupAll** for details.

  **Note:** unlike a dimension's groupAll, this grouping observes all current
  filters.

  **Not implemented yet**
  **/
  function groupAll() {

    // reducing does nothing because currently we can't choose how the database will aggregate data
    var groupAllObj = {
      reduce:      function () { return groupAllObj; },
      reduceCount: function () { return groupAllObj; },
      reduceSum:   function () { return groupAllObj; },
      dispose:     function () {},
      value:       function () { return getData(null, false)[0].value; }
    };

    return groupAllObj;
  }

  ////////////////////////////////////////
/**

## Dimension

### crossfilterServer.dimension(*dimensionFct*)

Creates and return a dimension object from the string `dimensionFct` passed as
parameter.

For example, to create a dimension of countries:

```js
var countries = payments.dimension(function(d) { return d["countries"]; });
```

Dimensions are stateful, recording the associated dimension-specific filters, if
any. Initially, no filters are applied to the dimension: all records are selected.
Since creating dimensions is expensive, you should be careful to keep a reference
to whatever dimensions you create.

**/
function dimension(dimensionFct) {

  // get dimension name
  var dimensionName = getDimensionName();

  var hierarchy = dimensions[dimensionName].hierarchy;
  var level     = dimensions[dimensionName].level;
  var members   = dimensions[dimensionName].members;

  // check existence in metadata
  if (typeof dimensionName != "string" ||
      typeof hierarchy     != "string" ||
      typeof level         != "number" ||
      !Array.isArray(members)          ||
      members.length <= 0)
  {
    throw "Dimension do not exist or malformed in declared metadata";
  }

  // returned object
  var dimensionObj = {
    filter: filter,
    filterExact: filterExact,
    filterRange: filterRange,
    filterFunction: filterFunction,
    filterAll: filterAll,
    top: top,
    bottom: bottom,
    group: group,
    groupAll: groupAll,
    dispose: dispose
  };

  /**
   * Guess the dimension name from the dimension function
   */
  function getDimensionName() {
    var dummyRecord = {};
    Object.keys(dimensions).forEach(function(d) {
      dummyRecord[d] = d;
    });

    return dimensionFct(dummyRecord);
  }

  /**
  ### crossfilterServer.dimension.filter(*value*)

  Filters records such that this dimension's value matches *value*, and returns this
  dimension. The specified *value* may be:

  * null, equivalent to **filterAll**;
  * an array, equivalent to **filterRange**;
  * a function, equivalent to **filterFunction**;
  * a single value, equivalent to **filterExact**.

  For example:

  ```js
  countries.filter(["Belgium", "France"]); // selects countries beetween "Belgium" and "France"
  countries.filter("France"); // selects records for France only
  countries.filter(function(d) { return d[0] == "A"; }); // countries starting with "A"
  countries.filter(null); // selects all countries
  ```

  Calling filter replaces the existing filter for this dimension, if any.
  **/
  function filter(range) {
    if (range === null)
      return filterAll();
    else if (Array.isArray(range))
      return filterRange(range);
    else if (typeof range === "function")
      return filterFunction(range);
    else
      return filterExact(range);
  }

  /**
  ### crossfilterServer.dimension.filterExact(*value*)

  Filters records such that this dimension's value equals *value*, and returns this
  dimension. For example:

  ```js
  countries.filterExact("France"); // selects records for France only
  ```

  Note that exact comparisons are performed using the ordering operators (`<`, `<=`,
  `>=`, `>`). For example, if you pass an exact value of null, this is equivalent to
  0; filtering does not use the `==` or `===` operator.

  Calling filterExact replaces the existing filter on this dimension, if any.
  **/
  function filterExact(value) {
    emptyDatasets();
    return filterFunction(function(d) {
      return d == value;
    });
  }

  /**
  ### crossfilterServer.dimension.filterRange(*range*)

  Filters records such that this dimension's value is greater than or equal to *range[0]*,
  and less than *range[1]*, returning this dimension. For example:

  ```js
  countries.filterRange(["Belgium", "France"]); // selects countries beetween "Belgium" and "France"
  ```

  Calling filterRange replaces the existing filter on this dimension, if any.
  **/
  function filterRange(range) {
    emptyDatasets();
    return filterFunction(function(d) {
      return d >= range[0] && d <= range[1];
    });
  }

  /**
  ### crossfilterServer.dimension.filterFunction(*function*)

  Filters records such that the specified *function* returns truthy when called with this
  dimension's value, and returns this dimension. For example:

  ```js
  countries.filterFunction(function(d) { return d[0] == "A"; }); // countries starting with "A"
  ```

  This can be used to implement a UNION filter, e.g.

  ```js
  // Selects countries with name stating by "Ab" or "Ac"
  countries.filterFunction(function(d) { return d[0] == "A" && d[1] == "b" || d[1] == "c"; });
  ```
  **/
  function filterFunction(f) {
    emptyDatasets();
    filters[dimensionName] = [];
    for (i = 0; i < members.length; i++)
      if (f(members[i]))
        filters[dimensionName].push(members[i]);
    return dimensionObj;
  }

  /**
  ### crossfilterServer.dimension.filterAll()

  Clears any filters on this dimension, selecting all records and returning this dimension.
  For example:

  ```js
  countries.filterAll(); // selects all countries
  ```
  **/
  function filterAll() {
    emptyDatasets();
    filters[dimensionName] = members;
    return dimensionObj;
  }

  /**
  ### crossfilterServer.dimension.top(*k*)

  **Not implemented yet**

  Returns a new array containing the top *k* records, according to the natural order of this
  dimension. The returned array is sorted by descending natural order. This method intersects
  the crossfilter's current filters, returning only records that satisfy every active filter
  (including this dimension's filter). For example, to retrieve the top 4 payments by total:

  ```js
  var topPayments = paymentsByTotal.top(4); // the top four payments, by total
  topPayments[0]; // the biggest payment
  topPayments[1]; // the second-biggest payment
  // etc.
  ```

  If there are fewer than *k* records selected according to all of the crossfilter's filters,
  then an array smaller than *k* will be returned. For example, to retrieve all selected
  payments in descending order by total:

  ```js
  var allPayments = paymentsByTotal.top(Infinity);
  ```
  **/
  // TODO implement and rewrite doc
  function top(k) {
    throw "Not implemented yet";
  }

  /**
  ### crossfilterServer.dimension.bottom(*k*)

  **Not implemented yet**

  Returns a new array containing the bottom *k* records, according to the natural order of this
  dimension. The returned array is sorted by ascending natural order. This method intersects the
  crossfilter's current filters, returning only records that satisfy every active filter (including
  this dimension's filter). For example, to retrieve the bottom 4 payments by total:

  ```js
  var bottomPayments = paymentsByTotal.bottom(4); // the bottom four payments, by total
  bottomPayments[0]; // the smallest payment
  bottomPayments[1]; // the second-smallest payment
  // etc.
  ```
  **/
  // TODO implement and rewrite doc
  function bottom(k) {
    throw "Not implemented yet";
  }

  /**
  ### crossfilterServer.dimension.groupAll()

  This is a convenience function for grouping all records into a single group. he returned
  object is similar to a standard **group**, except it has no **top** or **order** methods.
  Instead, use **value** to retrieve the reduce value for all matching records.

  Note: a grouping intersects the crossfilter's current filters, **except for the associated
  dimension's filter**. Thus, group methods consider only records that satisfy every filter
  except this dimension's filter. So, if the crossfilter of countries is filtered by all
  dimensions of your cube other than country.

  #### crossfilterServer.dimension.groupAll.reduce(), reduceCount(), reduceSum()

  As for the groups, we can't currently choose how data is aggregated, it's the default agregate
  done by the server. So these functions does nothing.

  #### crossfilterServer.dimension.groupAll.value()

  Return the agregated value for the group.
  **/
  function groupAll() {

    // reducing does nothing because currently we can't choose how the database will aggregate data
    var groupAllObj = {
      reduce:      function () { return groupAllObj; },
      reduceCount: function () { return groupAllObj; },
      reduceSum:   function () { return groupAllObj; },
      dispose:     function () {},
      value:       function () { return getData(dimensionName, false)[0].value; }
    };

    return groupAllObj;
  }

  /**
  ### crossfilterServer.dimension.dispose()

  Removes this dimension (and its groups) from its crossfilter.
  **/
  function dispose() {
    delete filters[dimensionName];
    delete datasets[dimensionName];
  }

  ////////////////////////////////////////
/**
## Group

### crossfilterServer.dimension.group()

Constructs a new grouping for the given dimension, that is to say a group in which you have
one value per element of the dimension.

The group's reduce function will sum the value of the OLAP mesure selected in the metadata
passed to crossfilterServer (this reduce operation will be done server side).

For example the group that will give a datum for each country is:

```js
var countriesGroup = countries.group();
```

In the current version of crossfilter server, you can't choose how the elements are grouped,
and by default they are grouped with the identity function (that is you say same elements
are grouped together).

Note: a grouping intersects the crossfilter's current filters, **except for the associated
dimension's filter**. Thus, group methods consider only records that satisfy every filter
except this dimension's filter. So, if the crossfilter of payments is filtered by type and
total, then group by total only observes the filter by type.
**/
function group() {

  var reduceMeasures = [];

  // returned object with function accessors
  var groupObj = {
    top: top,
    all: all,
    reduce: reduce,
    reduceCount: reduceCount,
    reduceSum: reduceSum,
    order: order,
    orderNatural: orderNatural,
    size: size,
    dispose: dispose
  };
groupObj.getDataAndSort = getDataAndSort;
groupObj.sortFunc = sortFunc;
groupObj.reduceMeasures = reduceMeasures;



  var sortFunc = null;

  /*
   * Get a copy of the data sorted on value accoring to the ket returned by `sortFunc`
   * (if no function defined, using identity as sort key function)
   * @private
   */
  function getDataAndSort() {
    if (sortFunc === null)
      sortFunc = function(d) { return d; };

    var out = [];
    var data = all();

    // copy data
    for (var i = 0; i < data.length; i++) {
      out[i] = data[i];
    }

    // sort copy
    out.sort(function(a, b) {
      if (sortFunc(a.value) > sortFunc(b.value))
        return -1;
      else
        return 1;
    });

    return out;
  }

  /**
  ### crossfilterServer.dimension.group.all()

  Returns the array of all groups, in ascending natural order by key. Like **top**, the returned
  objects contain `key` and `value` attributes. The returned array may also contain empty groups,
  whose value is the return value from the group's reduce *initial* function. For example, to
  count payments by type:

  ```js
  var countriesValues = countriesGroup.all();
  ```
  **/
  function all() {
    if (reduceMeasures.length > 0)
      return getData(dimensionName, true, reduceMeasures);
    else
      return getData(dimensionName, true);
  }

  /**
  ### crossfilterServer.dimension.group.top(*k*)

  Returns a new array containing the top *k* groups, according to the **group order** of the
  associated reduce value. The returned array is in descending order by reduce value. For example,
  to retrieve the top payment type by count:

  ```js
  var paymentsByType = payments.dimension(function(d) { return d.type; }),
      paymentCountByType = paymentsByType.group(),
      topTypes = paymentCountByType.top(1);
  topTypes[0].key; // the top payment type (e.g., "tab")
  topTypes[0].value; // the count of payments of that type (e.g., 8)
  ```

  If there are fewer than *k* groups according to all of the crossfilter's filters, then an array
  smaller than *k* will be returned. If there are fewer than *k* non-empty groups, this method may
  also return empty groups (those with zero selected records).
  **/
  function top(k) {
    return getDataAndSort().slice(0, k);
  }

  /**
  ### crossfilterServer.dimension.group.reduce(add, remove, initial), reduceCount(), reduceSum()

  These functions are here for compatibility with crossfilter interfaces but actually does nothing
  because currently we can't choose how the database will aggregate data.

  Currently, the agregate function is the one your API use.
  **/
  function reduce(add, remove, initial) {
    reduceMeasures = Object.keys(initial());
    return groupObj;
  }
  function reduceCount() {
    return groupObj;
  }
  function reduceSum() {
    return groupObj;
  }

  /**
  ### crossfilterServer.dimension.group.order(*sortFunction*)


  Specifies the order value for computing the **top-K** groups. The default
  order is the identity function, which assumes that the reduction values are naturally-ordered
  (such as simple counts or sums). For example, you can get the least value with:

  ```js
  var topCountry = countriesGroup.order(function (d) { return -d; }).top(1);
  topCountry[0].key;   // last country
  topCountry[0].value; // last value
  ```
  **/
  function order(sortFunction) {
    sortFunc = sortFunction;
    return groupObj;
  }

  /**
  ### crossfilterServer.dimension.group.orderNatural()

  This technique can likewise be used to compute the number of unique values in each group, by
  storing a map from value to count within each group's reduction, and removing the value from
  the map when the count reaches zero.
  **/
  function orderNatural() {
    sortFunc = null;
    return groupObj;
  }

  /**
  ### crossfilterServer.dimension.group.size()

  Returns the number of distinct values in the group, independent of any filters;
  the cardinality.
  **/
  function size() {
    return members.length;
  }

  /**
  ### crossfilterServer.dimension.group.dispose()

  Removes this group from its dimension. This group will no longer update when new filters
  are applied to the crossfilter, and it may be garbage collected if there are no other
  references to it remaining.
  **/
  function dispose() {
  }


  return groupObj;
}
  ////////////////////////////////////////

dimensionObj.dimensionName = dimensionName;
dimensionObj.getDimensionName = getDimensionName;
dimensionObj.getFilters = function() { return filters[dimensionName]; };


  return dimensionObj;
}

  ////////////////////////////////////////

crossfilterServerObj.api = api;
crossfilterServerObj.dimensions = dimensions;
crossfilterServerObj.metadata = metadata;
crossfilterServerObj.filters = filters;
crossfilterServerObj.datasets = datasets;
crossfilterServerObj.emptyDatasets = emptyDatasets;
crossfilterServerObj.getSlice = getSlice;
crossfilterServerObj.getData = getData;


  return crossfilterServerObj;
}

  ////////////////////////////////////////

})(typeof exports !== 'undefined' && exports || this);