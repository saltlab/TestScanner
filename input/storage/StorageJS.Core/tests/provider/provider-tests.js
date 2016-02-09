/// <reference path="../qunit-1.13.0.js" />
/// <reference path="../../app/provider.js" />

module('Provider Unit Tests');

QUnit.config.testTimeout = 10000;

var provider = new storageJS.provider(),
    testData = ['test data'],
    options = function () { return testData; };

test('Initialization', function (assert) {
  assert.strictEqual(typeof provider, 'object', 'Provider object instantiated.');
});

asyncTest('Register and execute request', function (assert) {
  provider.register('getData', options);
  assert.strictEqual(typeof provider.keymap['getData'], 'function', 'Request is registered');

  provider.request('getData').done(function (data) {
    assert.ok(true, 'Request executed');
    assert.strictEqual(testData, data, 'Expected data returned')
    start();
  });
});

test('Remove request', function (assert) {
  provider.remove('getData');
  assert.strictEqual(provider.keymap['getData'], undefined, 'Request is removed');
});