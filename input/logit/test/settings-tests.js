module('Settings Tests');
test("Settings object as constant", function(assert) {
	LogIt.Settings.prop = 'value';
	assert.equal(null, LogIt.Settings.prop);
});
test("Callback invocation on init", function(assert) {
	var called = false;
	var value = LogIt.Settings.init(function(){
		called = true;
	});
	assert.ok(called);
});

test("Test defaults", function(assert) {
	var df = LogIt.Settings.dateFormat();
	assert.equal(df, "dd MMM yyyy");

	var wv = LogIt.Settings.weekView();
	assert.equal(wv, "5");

	var dh = LogIt.Settings.dayHour();
	assert.equal(dh, 8);

});
test("Test save", function(assert) {
	var obj = {
		weekView : "7",
		dayHour : 10
	};
	LogIt.Settings.save(obj);

	var wv = LogIt.Settings.weekView();
	assert.equal(wv, "7");

	var dh = LogIt.Settings.dayHour();
	assert.equal(dh, 10);

});
