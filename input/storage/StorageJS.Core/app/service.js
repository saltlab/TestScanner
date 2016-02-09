/// <reference path="provider.js" />

var storageJS = storageJS || {};

(function (ns, provider) {
  function service() {
    this.provider = new provider();
  }

  service.prototype.create = function () {
    return this.provider.request('create');
  };

  service.prototype.get = function () {
    return this.provider.request('get');
  };

  service.prototype.update = function () {
    return this.provider.request('update');
  };  

  service.prototype.destroy = function () {
    return this.provider.request('destroy');
  };

  service.prototype.register = function (identifier, options) {
    if (/(create|get|update|destroy)/i.test(identifier)) {
      this.provider.register(identifier, options);
    }
  };

  ns.service = service;

})(storageJS, storageJS.provider);