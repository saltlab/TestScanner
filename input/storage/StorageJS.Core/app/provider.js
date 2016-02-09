/// <reference path="../vendor/jquery-2.0.3.min.js" />

var storageJS = storageJS || {};

(function (ns, $) {
  function provider() {
    this.keymap = {};
  }

  /*
  * @method register
  * @param {string} identifier
  * @param {object} options
  */
  provider.prototype.register = function (identifier, options) {
    if (identifier && typeof identifier === 'string') {
      this.keymap[identifier] = options;
    }
  };

  /*
  * @method request
  * @param {string} identifier
  * @param {object} data
  * @param {function} callback
  */
  provider.prototype.request = function (identifier, data, callback) {
    var options = this.keymap[identifier];

    if (typeof options === 'function') {
      return $.Deferred(function (dfd) {
        dfd.resolve(options());
      }).promise();
    } else {
      if (data) {
        options.data = data;
      }

      if (callback) {
        options.success = callback;
      }

      return $.ajax(options);
    }
  };

  /*
  * @method remove
  * @param {string} identifier
  */
  provider.prototype.remove = function (identifier) {
    delete this.keymap[identifier];
  };

  ns.provider = provider;

})(storageJS, jQuery);