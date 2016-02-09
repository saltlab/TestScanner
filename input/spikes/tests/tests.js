QUnit.test('Utils.keys', function(assert) {
  var arr = [1, 2, 3];
  var obj = {a: 1, b: 2};
  var keys = Utils.keys(obj);
  assert.ok(Utils.keys(arr) === arr, 'pass');
  assert.ok(keys.indexOf('a') !== -1 && keys.indexOf('b') !== -1, 'pass');
});

QUnit.test('Utils.isEmptyObj', function(assert) {
  assert.ok(Utils.isEmptyObj({}) === true, 'pass');
  assert.ok(Utils.isEmptyObj() === true, 'pass');
  assert.ok(Utils.isEmptyObj([]) === true, 'pass');
  assert.ok(Utils.isEmptyObj({aProp: 'aValue'}) === false, 'pass');
  assert.ok(Utils.isEmptyObj([1, 2, 3]) === false, 'pass');
});

QUnit.test('Utils.clone', function(assert) {
  var arr = [1,2,3];
  var obj = {d: 3, e: null};
  var other = {a:1, b: arr, c: obj};
  var ref = other;
  var cloned = Utils.clone(other);
  arr.push(4);
  obj.f = 'hey';
  other.a = 3;
  assert.ok(cloned.b.length === 3, 'pass');
  assert.ok(typeof cloned.c.f === 'undefined', 'pass');
  assert.ok(cloned.c.e === null, 'pass');
  assert.ok(cloned.a === 1, 'pass');
});

QUnit.test('Utils.eachKey', function(assert) {
  var obj = {a:1, b:2};
  var iter = false;
  Utils.eachKey(obj, function(key, value, obj) {
    assert.ok(typeof key === 'string', 'pass');
    assert.ok(obj[key] === value, 'pass');
    assert.ok(obj === obj, 'pass');
  });
  Utils.eachKey({}, function() {
    iter = true;
  });
  assert.ok(iter === false, 'pass');
});

QUnit.test('Utils.defaults', function(assert) {
  var user = {first_name: 'Mirko'};
  var defaults1 = {last_name: 'Mariani'};
  var defaults2 = {first_name: 'Mickey'};
  var defaults3 = {age: null};
  var defaults4 = {age: 27};
  var obj = Utils.defaults(user, defaults1, defaults2, defaults3, defaults4);
  assert.ok(obj.first_name === 'Mirko', 'pass');
  assert.ok(obj.last_name === 'Mariani', 'pass');
  user.first_name = defaults2.first_name;
  assert.ok(obj.first_name === 'Mirko', 'pass');
  assert.ok(obj.age === 27, 'pass');
});

QUnit.test('Utils.extend', function(assert) {
  var obj = {age: 25};
  var user = {first_name: 'Mickey', last_name: 'Mouse', info: obj};
  var ext = Utils.extend(user, {first_name: 'Mirko'});
  assert.ok(ext.first_name === 'Mirko', 'pass');
  assert.ok(ext.last_name === 'Mouse', 'pass');
  obj.age = 27;
  assert.ok(ext.info.age === 25, 'pass');
  var ext2 = Utils.extend(ext, {info: {age: 27}});
  assert.ok(ext2.info.age === 27, 'pass');
  assert.ok(ext.info.age === 25, 'pass');
});

QUnit.test('Utils.uniqueId', function(assert) {
  assert.ok(Utils.uniqueId() !== Utils.uniqueId(), 'pass');
  assert.ok(Utils.uniqueId() !== Utils.uniqueId(), 'pass');
  assert.ok(Utils.uniqueId() !== Utils.uniqueId(), 'pass');
})
