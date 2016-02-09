'use strict';
require('6to5/polyfill');
var Utils = require('./utils');

const cidProp = Symbol();
const attributesProp = Symbol();
const idAttributeProp = Symbol();

class Model {

  constructor(attributes = {}, options = {}) {
    const attrs = !options.parse ? attributes : this._parse(attributes);
    this[cidProp] = Utils.uniqueId();
    this[idAttributeProp] = this.getIdAttribute();
    this[attributesProp] = Utils.defaults(attrs, this.getDefaultAttributes());
  }

  getIdAttribute() {
    return 'id';
  }

  getDefaultAttributes() {
    return {};
  }

  _parse(rawData) {
    return this.parse(Utils.clone(rawData)) || {};
  }

  parse(rawData) {
    return rawData;
  }

  toJSON() {
    return Utils.clone(this[attributesProp]);
  }

  validate() {}

  get(attr) {
    return new Promise((resolve, reject) => {
      const value = this[attributesProp][attr];
      if (value != null) {
        return resolve(value);
      }
      return reject(null);
    });
  }

  set(key, value) {
    if (key == null) {
      return new Promise((resolve, reject) => r.reject(this));
    }

    const isObject = typeof key === 'object';
    const attrs = isObject ? key : {[key]: value};

    return new Promise((resolve, reject) => {
      const validation = this.validate(attrs);
      if (!!validation) {
        return reject([validation, this]);
      }
      return resolve(new this.constructor(Utils.extend(this[attributesProp], attrs), {parse: false}));
    });
  }

}

exports.Model = Model;
exports.model = (...args) => new Model(...args);
