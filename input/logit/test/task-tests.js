module('Model instance creation');
test( "instance creation without attrs", function(assert) {
	var obj = new LogIt.Task();
	assert.ok(obj);
});
test( "instance creation - log present - invalid value (2hh)", function(assert) {
	var obj = new LogIt.Task({log: '2hh'});
	assert.ok(obj);
	assert.equal(null, obj.get('log'));
	assert.ok(Number.isNaN(obj.get('value')));
	assert.equal('h', obj.get('unit'));
});
test( "instance creation - log present - invalid value(2)", function(assert) {
	var obj = new LogIt.Task({log: '2'});
	assert.ok(obj);
	assert.equal(null, obj.get('log'));
	assert.equal(2, obj.get('value'));
	assert.equal('h', obj.get('unit'));
});
test( "instance creation - log present - invalid value(2h2h)", function(assert) {
	var obj = new LogIt.Task({log: '2'});
	assert.ok(obj);
	assert.equal(null, obj.get('log'));
	assert.ok(obj.get('value'));
	assert.equal('h', obj.get('unit'));
});

module('Model validation');
test( "Model validation - All values are null", function(assert) {
	var obj = new LogIt.Task();
	var msg = obj.isValidModel();
	assert.ok(msg.task);
	assert.ok(msg.date);
	assert.ok(msg.date);
});
test( "Model validation - Invalid value", function(assert) {
	var obj = new LogIt.Task({task: '', date: Date.now(), value: '2hh'});
	var msg = obj.isValidModel();
	assert.ok(msg.log);
});
test( "Model validation - Negative value", function(assert) {
	var obj = new LogIt.Task({task: '', date: Date.now(), value: '-2h'});
	var msg = obj.isValidModel();
	assert.ok(msg.log);
});
test( "Model validation - All values are valid", function(assert) {
	var obj = new LogIt.Task({task: '', date: Date.now(), log: '2h'});
	var msg = obj.isValidModel();
	assert.equal(msg, null);
});

module('Model toHour');
test( "Model toHour default value", function(assert) {

	LogIt.Settings = {
		dayHour : function(){
			return 8;
		}
	};
	
	//days
	var obj = new LogIt.Task({log: '1.25d'});
	var hr = obj.toHour(true);
	assert.equal(10.00, hr);
	//hours
	var obj = new LogIt.Task({log: '1.25h'});
	var hr = obj.toHour(true);
	assert.equal(1.25, hr);
	//mins
	var obj = new LogIt.Task({log: '20m'});
	var hr = obj.toHour(true);
	assert.equal(0.333, hr);
});
test( "Model toHour custom value", function(assert) {

	LogIt.Settings = {
		dayHour : function(){
			return 6;
		}
	};
	
	//days
	var obj = new LogIt.Task({log: '1.25d'});
	var hr = obj.toHour(true);
	assert.equal(7.5, hr);
	//hours
	var obj = new LogIt.Task({log: '1.25h'});
	var hr = obj.toHour(true);
	assert.equal(1.25, hr);
	//mins
	var obj = new LogIt.Task({log: '20m'});
	var hr = obj.toHour(true);
	assert.equal(0.333, hr);
});
