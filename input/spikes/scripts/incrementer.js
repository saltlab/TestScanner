'use strict';
require('6to5/polyfill');

var counterProp = Symbol();

class Incrementer {
  constructor(start = 0) {
    this[counterProp] = start;
  }
  increment() {
    return new Incrementer(this[counterProp] + 1);
  }
  getCounter() {
    return this[counterProp];
  }
}

module.exports = function (...args) {
  return new Incrementer(...args);
};
