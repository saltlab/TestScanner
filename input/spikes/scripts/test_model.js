'use strict';

var Utils = require('./utils');
var Model = require('./model').Model;

class User extends Model {
  getIdAttribute() {
    return '_id';
  }

  getDefaultAttributes() {
    return {
      first_name: 'Mirk'
    };
  }

  validate(attrs) {
    if (attrs.age && attrs.age > 30) {
      return 'Too old!';
    }
  }

}

window.UserModel = User;

var mirko = new User({first_name: 'Mirko', last_name: 'Mariani'});

mirko
  .set('age', 27)
  .then(function(newInstance) {
    console.log('mirko ', mirko.toJSON());
    console.log('newInstance ', newInstance.toJSON());
    return newInstance.set('friends', 100);
  })
  .then(function(anotherNew) {
    console.log('anotherNew ', anotherNew.toJSON());
    return anotherNew.set('age', 31);
  })
  .then(function(impossible) {
    console.log('impossible!', impossible.toJSON());
  }, function(rejected) {
    var error = rejected[0];
    var oldInstance = rejected[1];
    console.log(error);
    console.log('oldInstance', oldInstance.toJSON());
  });
