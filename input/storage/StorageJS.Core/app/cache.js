
var storageJS = storageJS || {};

(function (ns) {
  function cache(capacity) {
    this.head = undefined;
    this.tail = undefined;
    this.keymap = {};
    this.capacity = capacity || 3;
    this.size = 0;
  }

  /*
  * Gets an entry from the list and registers its use.
  * @method get
  * @param {string} key
  * @returns {object} The entry.
  */
  cache.prototype.get = function (key) {
    var entry = this.keymap[key];

    if (entry === undefined) { return; }

    if (entry === this.head) { return entry; }

    // orphan the entry
    if (entry === this.tail) {
      this.tail = entry.prev;
      this.tail.next = undefined;
    } else {
      entry.prev.next = entry.next;
      entry.next.prev = entry.prev;
    }

    // move it to the head
    entry.prev = undefined;
    entry.next = this.head;

    this.head.prev = entry;
    this.head = entry;

    return entry;
  };

  /*
  * Finds an entry in the list without registering its use.
  * @method fetch
  * @param {string} key
  * @returns {object} The entry.
  */
  cache.prototype.fetch = function (key) {
    return this.keymap[key];
  };

  /*
  * Adds a new entry to the list.
  * @method push
  * @param {string} key
  * @param {object} value
  */
  cache.prototype.push = function (key, value) {
    var entry = { key: key, value: value };

    if (this.head) {
      this.head.prev = entry;
      entry.next = this.head;
    } else {
      this.tail = entry;
    }

    this.head = entry;
    this.keymap[key] = entry;

    if (this.size === this.capacity) {
      return this.pop();
    } else {
      this.size++;
    }
  };

  /*
  * Removes the oldest entry from the list.
  * @method pop
  * @returns {object} The oldest entry.
  */
  cache.prototype.pop = function () {
    var entry = this.tail;

    if (entry) {
      if (this.tail.prev) {
        this.tail = this.tail.prev;
        this.tail.next = undefined;
      } else {
        this.tail = undefined;
      }

      entry.prev = entry.next = undefined;

      delete this.keymap[entry.key];
    }

    return entry;
  };

  ns.cache = new cache();

})(storageJS);