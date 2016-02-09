'use strict';
require('6to5/polyfill');

var tco = require('./tco');

//check out
//https://rawgit.com/mauriciosantos/buckets/master/doc/symbols/buckets.LinkedList.html
const headProp = Symbol();
const tailProp = Symbol();

class List {
  constructor(head, tail) {
    this[headProp] = head;
    this[tailProp] = tail;
  }

  getHead() {
    return this[headProp];
  }

  getTail() {
    return this[tailProp];
  }

  isEmpty() {
    return this.getHead() == null;
  }

  tail() {
    if (this.isEmpty()) {
      return this;
    }
    const next = this.getTail();
    return new this.constructor(next.getHead(), next.getTail());
  }

  append(value) {
    let go = (list, value) => {
      if (list.isEmpty()) {
        return value;
      }
      const head = list.getHead();
      const tail = list.getTail();
      return new this.constructor(head, go(tail, value));
    }

    return go(this, value);
  }

  toString() {
    function go(list) {
      if (list.isEmpty()) {
        return 'null';
      }
      return list.getHead().toString() + ', ' + go(list.getTail());
    }

    return 'List(' + go(this) + ');';
  }

}

var list = tco(function(...args) {
  if (args[0] == null) {
    return new List(null);
  }
  const rest = args.slice(1);
  return new List(args[0], list(...rest));
});

module.exports = list;
