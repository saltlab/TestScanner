'use strict';
require('6to5/polyfill');

var valueProp = Symbol();

class Maybe {
  constructor(value) {
    this[valueProp] = value;
  }

  ret() {
    return this[valueProp];
  }

  bind(fn) {
    if (this[valueProp] != null) {
      return fn(this[valueProp]);
    }
    return this[valueProp];
  }

  static lift(fn) {
    return (val) => new Maybe(fn(val));
  }

  toString() {
    return 'Maybe(' + this[valueProp] + ')';
  }
}

exports.Maybe = Maybe;
exports.maybe = (...args) => new Maybe(...args);
