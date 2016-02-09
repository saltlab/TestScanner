'use strict';
var Utils = require('./utils');
var List = require('./list');

window.Utils = Utils;
window.List = List;

var i = 0;
var args = Array(1000).fill(0).map(() => i++);

console.time('test');
console.log(List(...args));
console.timeEnd('test');

var lst = List(1, 2, 3);
var another = List(4, 5, 6);

console.log(lst.append(List(4)).toString());
console.log(lst.toString());
console.log(lst.append(another).toString());
console.log(List().tail().toString());

//buggy case
var mmm = List(1, null, 3);
console.log(mmm.toString());
