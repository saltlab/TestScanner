var clearStorage = function() {
    webStorage.local.clear();
    webStorage.session.clear();
};
QUnit.module("web-storage", {
    setup: clearStorage,
    teardown: clearStorage
});

var tomorrow = function() {
    var date = new Date();
    date.setDate(date.getDate() + 1);
    return date;
};
var oneSecondAgo = function() {
    var date = new Date();
    return new Date(date.getTime() - 1000);
};
QUnit.test("local.getItem unset item returns null", function(assert) {
    assert.ok(webStorage.local.getItem("key") === null, "unset item not found");
});
QUnit.test("local.setItem, local.getItem basic test", function(assert) {
    var data = {abc: "def"};
    webStorage.local.setItem("key", data, { expires: tomorrow() });

    assert.deepEqual(webStorage.local.getItem("key"), data, "data retrieved equals data set");
});
QUnit.test("local.clear test", function(assert) {
    webStorage.local.setItem("key", {abc: "def"}, { expires: tomorrow() });
    webStorage.local.clear();

    assert.ok(webStorage.local.getItem("key") === null, "cleared storage no longer contains previously set item");
});
QUnit.test("local.removeItem", function(assert) {
    webStorage.local.setItem("key", {abc: "def"}, { expires: tomorrow() });
    var i = webStorage.local.key(0);
    webStorage.local.removeItem("key");

    assert.ok(webStorage.local.getItem("key") === null, "removed item no longer available");
});
QUnit.test("local.key", function(assert) {
    webStorage.local.setItem("key", {abc: "def"}, { expires: tomorrow() });

    assert.ok(webStorage.local.key(0) === "key", "Index's key found");
});

// local.setItem
QUnit.test("local.setItem without options is unsuccessful", function(assert) {
    webStorage.local.setItem("key", {abc: "def"});

    assert.ok(window.localStorage.getItem("key") === null, "No item found.")
});
QUnit.test("local.setItem without options.expires is unsuccessful", function(assert) {
    webStorage.local.setItem("key", {abc: "def"}, {});

    assert.ok(window.localStorage.getItem("key") === null, "No item found.")
});
QUnit.test("local.setItem with wrong type for options.expires is unsuccessful", function(assert) {
    webStorage.local.setItem("key", {abc: "def"}, {expires: "tomorrow"});

    assert.ok(window.localStorage.getItem("key") === null, "No item found.")
});
QUnit.test("local.setItem with expired object is unsuccessful", function(assert) {
    webStorage.local.setItem("key", {abc: "def"}, {expires: oneSecondAgo()});

    assert.ok(window.localStorage.getItem("key") === null, "No item found.")
});
QUnit.test("local.setItem with wrong type for options.dependencies is unsuccessful", function(assert) {
    webStorage.local.setItem("key", {abc: "def"}, {expires: tomorrow(), dependencies: "key2"});

    assert.ok(window.localStorage.getItem("key") === null, "No item found.")
});
QUnit.test("local.setItem with empty options.dependencies array is successful", function(assert) {
    webStorage.local.setItem("key", {abc: "def"}, {expires: tomorrow(), dependencies: []});

    assert.ok(window.localStorage.getItem("key") !== null, "Item found.")
});
QUnit.test("local.setItem with available dependencies successful", function(assert) {
    webStorage.local.setItem("key", {abc: "def"}, {expires: tomorrow(), dependencies: []});
    webStorage.local.setItem("key2", {abc: "def"}, {expires: tomorrow(), dependencies: ["key"]});

    assert.ok(window.localStorage.getItem("key2") !== null, "Item found.")
});
QUnit.test("local.setItem with unavailable dependencies unsuccessful", function(assert) {
    webStorage.local.setItem("key", {abc: "def"}, {expires: oneSecondAgo(), dependencies: []});
    webStorage.local.setItem("key2", {abc: "def"}, {expires: tomorrow(), dependencies: ["key"]});

    assert.ok(window.localStorage.getItem("key2") === null, "Item not found.")
});