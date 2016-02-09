/// <reference path="../qunit-1.13.0.js" />
/// <reference path="../../app/cache.js" />

module('Cache Unit Tests');

var cache = storageJS.cache;
cache.push('A', 1);
cache.push('B', 2);
cache.push('C', 3);

test('Store and retrieve value', function (assert) {
  assert.strictEqual(cache.size, 3, 'Cache size equal to 3, {"C", "B", "A"}.')
  assert.strictEqual(cache.fetch('A').key, 'A', 'Entry "A" retrieved.');
  assert.strictEqual(cache.fetch('B').key, 'B', 'Entry "B" retrieved.');
  assert.strictEqual(cache.fetch('C').key, 'C', 'Entry "C" retrieved.');
});

test('LRU cache behavior', function (assert) {
  assert.strictEqual(cache.fetch('A').key, cache.tail.key, 'Entry "A" is the least recently used, {"C", "B", "A"}.');
  assert.strictEqual(cache.fetch('C').key, cache.head.key, 'Entry "C" is the most recently used.');

  var entry = cache.push('D', 4);
  assert.strictEqual(cache.size, 3, 'Cache size still equal to 3 after addition of "D" entry, {"D", "C", "B"}.');
  assert.strictEqual(entry.key, 'A', 'Least used entry "A" removed from cache and returned.');

  assert.strictEqual(cache.fetch('B').key, cache.tail.key, 'Entry "B" is the least recently used, {"D", "C", "B"}.');
  entry = cache.get('B');
  assert.strictEqual(cache.fetch('B').key, cache.head.key, 'Entry "B" is now the most recently used, {"B", "D", "C"}.');
  assert.strictEqual(cache.fetch('C').key, cache.tail.key, 'Entry "C" is now the least recently used.');
});