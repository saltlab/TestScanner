/**
 * Class that represents a request.
 *
 * @class
 */

QueryAPI = function() {

    /**
     * Cube
     * @private
     * @type String
     */
    var from;
    var onRows;
    var onColumns;
    var where;

    /**
     * Returns the from attribute of the QueryAPI.
     * @returns {String}
     */
    this.getFrom = function() {
        return from;
    };

    /**
     * Returns the onRows attribute of the QueryAPI.
     * @returns {String}
     */
    this.getOnRows = function() {
        return onRows;
    };

    /**
     * Returns the onColumns attribute of the QueryAPI.
     * @returns {String}
     */
    this.getOnColumns = function() {
        return onColumns;
    };

    /**
     * Returns the where attribute of the QueryAPI.
     * @returns {String}
     */
    this.getWhere = function() {
        return where;
    };

    /**
     * Defines the from attribute of the QueryAPI.
     * @param {String} cube The cube of the QueryAPI.
     */
    this.drill = function(cube) {
            from = cube;
    };

    /**
     * Adds a measure dimension on the columns, if it's not already there.
     * @param {String} measure The measure to add on onColumns.
     */
    this.push = function(measure) {

        if (onColumns.indexOf(measure) < 0) {
            onColumns.push(measure);
        }
    };

    /**
     * Withdraws a measure dimension of he columns, if it is on the columns.
     * @param {String} measure The measure to withdraw on onColumns.
     */
    this.pull = function(measure) {

        index = onColumns.indexOf(measure);
        if (index != -1) {
            onColumns.splice(index, 1);
        }
    };

    /**
     * Adds members of a dimension on the rows. If the dimension of the members
     * is not already in the rows, it is added as well.
     * @param {String} hierarchy The hierarchy of the members to add.
     * @param {String} members The members to add.
     * @param {Boolean} range indicates if the members beetween
     *  the members are added or not.
     */
    this.slice = function(hierarchy, members, range) {

        if (!(hierarchy in onRows)) {
            onRows[hierarchy] = {};
        }
        if (range === undefined) {
          range = false;
        }
        onRows[hierarchy].members = members;
        onRows[hierarchy].range = range;
        onRows[hierarchy].dice = false;
    };

    /**
     * Add dice behavior to a list of hierarchies, that is to say those hierarchies
     * won't be completely aggregated.
     * @param {Array<String>} hierarchies
     */
    this.dice = function (hierarchies) {
        for (var i = 0; i < hierarchies.length; i++) {
            if (typeof onRows[hierarchies[i]] != "undefined")
                onRows[hierarchies[i]].dice = true;
        }
    }

    /**
     * Withdraws a hierarchy selected of a dimension of the rows, if it is in the rows.
     *
     * @param {String} hierarchy The hierarchy to withdraw.
     * */
    this.project = function(hierarchy) {

        if (hierarchy in onRows) {
            delete onRows[hierarchy];
        }
    };
/*
    this.switch = function(hierarchies) {
        var tmp = new Object();
        for (var i = 0, hierarchy; hierarchy = hierarchies[i]; i++) {
           if(onRows.hasOwnProperty(hierarchy))
             tmp[hierarchy] = onRows[hierarchy];
        }
        onRows = tmp;
    };
*/

    /**
     * Adds members of a dimension in the where of the request QueryAPI. If the
     * dimension of the members is not already in the where, it is added as well.
     * @param {String} hierarchy The hierarchy of the members to add.
     * @param {String} members The members to add.
     * @param {Boolean} range indicates if the members beetween
     *  the members are added or not.
    */
    this.filter = function(hierarchy, members, range) {

        if (!(hierarchy in where)) {
            where[hierarchy] = {};
        }
        where[hierarchy].members = members;
        where[hierarchy].range = range;
        where[hierarchy].dice = false;
    };

    this.rank = function(hierarchy) {

    };

    /**
     * Sends the QueryAPI to be executed and returns a data result.
     *
     * @returns {String}
     */
    this.execute = function() {
        var data = {
            "from" : from,
            "onColumns" : onColumns,
            "onRows" : onRows,
            "where" : where
        };
        return send("data", data);
    };

    /**
     * Clears the QueryAPI. It becomes empty.
     */
    this.clear = function() {
        from = null;
        onRows = {};
        onColumns = [];
        where = {};
    };

    /**
     * Sends the QueryAPI to be executed and returns a metadata result.
     * @param {String[]} root Array that contains the elements of which
     *  we want to recover the metadata, it can contain 6 elements max.
     * @param {Boolean} withProperties Boolean to recover the properties
     *  of the elements of the root or not.
     * @param {Int} granularity Enables to choose the granularity of the
     *  members in the root. It is only necessary if there are 6
     *  elements in the root.
     *
     */
    this.explore = function(root, withProperties, granularity) {

        var data = { "root": root, "withProperties": withProperties, "granularity": granularity };
        return send("metadata", data);
    };

    /**
     * Format the queryType and data to be in the JSON result. Then sends the JSON
     * results to the user.
     *
     * @param {String} queryType Type of data (data or metadata).
     * @param {String} data Data in a JSON fomat.
     *
     * @returns {JSON}
     */
    var send = function(queryType, data) {

        var query = {
            "queryType" : queryType,
            "data" : data
        };

        var api_data;
        $.ajax({
            url: "/analytics/api/",
            type: "POST",
            dataType: 'json',
            data: JSON.stringify(query),
            async: false,
            success: function(data) {
              api_data=data;
            }
        });
        return api_data;
    };

    this.clear();
};