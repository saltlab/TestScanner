'use strict';
require('6to5/polyfill');
let Utils = require('./utils');

let Profile = (target) => {
  let obj = {};
  Object.keys(target).forEach((prop) => {
    let tmp = target[prop];
    let index = 0;
    obj[prop] = function() {
      index = index + 1;
      let label = prop + '_' + index;
      console.time(label);
      let ret = tmp.apply(obj, arguments);
      console.timeEnd(label);
      return ret;
    };
  });
  return obj;
};

module.exports = Profile;
