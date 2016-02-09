'use strict';
require('6to5/polyfill');

var Utils = {

  keys(obj = {}) {
    if (Array.isArray(obj)) {
      return obj;
    }
    return Object.keys(obj);
  },

  isEmptyObj(obj = {}) {
    return Object.keys(obj).length === 0;
  },

  clone(obj = {}) {
    const isArray = Array.isArray(obj);
    const out = isArray ? [] : {};
    this.keys(obj).forEach((key, i) => {
      const value = isArray ? obj[i] : obj[key];
      out[(isArray ? i : key)] = (typeof value === 'object' && value != null) ? this.clone(value) : value;
    });
    return out;
  },

  eachKey(obj = {}, iteratee) {
    this.keys(obj).forEach((key) => {
      iteratee(key, obj[key], obj);
    });
  },

  defaults(obj = {}, ...defaults) {
    let newObj = this.clone(obj);
    defaults.forEach((source) => {
      this.keys(source).forEach(prop => {
        if (newObj[prop] == null) {
          newObj[prop] = source[prop];
        }
      });
    });
    return newObj;
  },

  extend(...objects) {
    let obj = {};
    objects.forEach((source) => {
      this.eachKey(source, (key, value) => {
        obj[key] = typeof value === 'object' ? this.clone(value) : value;
      });
    });
    return obj;
  },

  uniqueId() {
    let s4 = () => Math.floor((1 + Math.random()) * 0x10000).toString(16).substring(1);
    return s4() + s4() + '-' + s4() + '-' + s4() + '-' + s4() + '-' + s4() + s4() + s4();
  }

}

module.exports = Utils;
