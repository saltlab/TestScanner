/// <reference path="../qunit-1.13.0.js" />
/// <reference path="../../app/storage.js" />

module('Storage Unit Tests');

var session = storageJS.storage.session,
    obj = { data: [1, 2, 3, 4] };

test('Initialization', function (assert) {
  assert.strictEqual(typeof session, 'object', 'Storage object instantiated.');
  assert.strictEqual(session.store instanceof Storage, true, 'Storage object is an instance of window.Storage.');
  assert.strictEqual(session.store === sessionStorage, true, 'Storage object is equal to window.sessionStorage.');
});

test('Store and retrieve value', function (assert) {
  session.setVal('name', 'Tenkiller');
  assert.strictEqual(session.getVal('name'), 'Tenkiller', 'Value stored and retrieved.');
});

test('Store and retrieve object', function (assert) {
  session.setObj('data', obj);
  assert.deepEqual(session.getObj('data'), obj, 'Object stored and retrieved.');
});

test('Store and retrieve invalid objects', function (assert) {
  session.setVal('func', function () { });
  session.setVal('obj', function () { });
  assert.strictEqual(session.getVal('func'), undefined, 'Invalid function value not stored.');
  assert.strictEqual(session.getVal('obj'), undefined, 'Invalid object value not stored.');
  assert.strictEqual(session.getVal({}), undefined, 'Invalid object key not found.');
  assert.strictEqual(session.getVal(function () { }), undefined, 'Invalid function key not found.');
});

test('Get number of elements', function (assert) {
  assert.strictEqual(session.length(), 2, 'Correct number of elements found.');
});

test('Clear all elements', function (assert) {
  session.clear();
  assert.strictEqual(session.length(), 0, 'All elements cleared.');
});