QUnit.test("Mocky", function(assert) {
	var x = {a: 1};
	new Mocky(x, function(mocky) {
		assert.equal(x.a, 1, "Does not modify x by default");
		mocky.mock({a: 2});
		assert.equal(x.a, 2, "Does modify x on request");
		assert.ok(!x.hasOwnProperty("b"), "Does not add properties by default");
		mocky.mock({b: 3});
		assert.equal(x.b, 3, "Does add properties on request");
		assert.equal(x.a, 2, "Modifying one property does not drop another");
	});
	assert.equal(x.a, 1, "Reverts x on completion");
	assert.ok(!x.hasOwnProperty("b"), "Deletes properties when done");
	QUnit.test("Async mode", function(assert) {
		var x = {a: 1};
		var mocky = new Mocky(x);
		assert.equal(x.a, 1, "Does not modify x by default");
		mocky.mock({a: 2});
		assert.equal(x.a, 2, "Does modify x on request");
		mocky.unmock();
		assert.equal(x.a, 1, "Reverts x on completion");
	});
	QUnit.test("Bad args", function(assert) {
		var seen_exception;
		try {
			new Mocky();
		} catch(e) {
			seen_exception = true;
		}
		ok(seen_exception, "Mocky() [no args] raises an exception");
	});
	QUnit.test("Warn on unmock", function(assert) {
		var seen_log;
		new Mocky(console, function(mocky) {
			mocky.mock({log: function() {seen_log = true}});
			mocky.unmock(); // This should log a warning.
			assert.ok(seen_log, "Warn on explicit unmock in function mode");
		});
	});
	QUnit.test("Operate on classes", function(assert) {
		var y = function() {};
		y.prototype = x;
		var z = new y();
		new Mocky(y.prototype, function(mocky) {
			assert.equal(z.a, 1, "Does not modify class y by default");
			mocky.mock({a: 2});
			assert.equal(z.a, 2, "Does modify class y on request");
		});
		assert.equal(z.a, 1, "Reverts class y on completion");
	});
	QUnit.test("Example usage", function(assert) {
		new Mocky(document, function(mocky) {
			var seen_expected_value;
			mocky.mock({querySelector: function(selector) {
				if(selector == "#expected-value") {
					seen_expected_value = true;
					return undefined;
				} else {
					return mocky.mocked.querySelector.call(this, selector);
				}
			}});
			document.querySelector("#expected-value");
			ok(seen_expected_value, "querySelector override works");
			ok(document.querySelector("body") === document.body, "Fallback via 'mocked' works");
		});
	});
});
