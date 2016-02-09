
var storageJS = storageJS || {};

(function (ns) {
  /*
  * @class storage
  * @constructor
  * @param {string} type
  */
  var storage = function (type) {
    this.store = (type === 'local') ? localStorage : sessionStorage;
  };

  /*
  * @method getVal
  * @param {string} key
  * @returns {string}
  */
  storage.prototype.getVal = function (key) {
    return this.store[key];
  };

  /*
  * @method getObj
  * @param {string} key
  * @returns {object}
  */
  storage.prototype.getObj = function (key) {
    return JSON.parse(this.getVal(key));
  };

  /*
  * @method setVal
  * @param {string} key
  * @param {string|number} value
  */
  storage.prototype.setVal = function (key, value) {
    if (typeof value === 'object' || typeof value === 'function') return;
    this.store[key] = value;
  };

  /*
  * @method setObj
  * @param {string} key
  * @param {object} value
  */
  storage.prototype.setObj = function (key, value) {
    if (typeof value === 'function') return;
    this.setVal(key, JSON.stringify(value));
  };

  /*
  * @method length
  * @returns {number}
  */
  storage.prototype.length = function () {
    return this.store.length;
  };

  /*
  * @method clear
  */
  storage.prototype.clear = function () {
    this.store.clear();
  };

  ns.storage = {
    local: new storage('local'),
    session: new storage()
  };

})(storageJS);