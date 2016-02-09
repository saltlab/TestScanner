module('Model instance creation',	{

	setup: function() {
		LogIt.Settings = {
			weekView : function(){},
			dayHour : function(){},
			save : function(obj){return obj;}
		};
		var weekView = sinon.stub(LogIt.Settings, "weekView");
		weekView.onFirstCall().returns("5");
		var dayHour = sinon.stub(LogIt.Settings, "dayHour");
		dayHour.onFirstCall().returns(10);
		
		var h = $('#settingHtml').html();
		$('#settings').html(h);
    },

    teardown: function() {
		$('#settings').html('');
		LogIt.Settings = null;
    }
});
test( "Initialization", function(assert) {
	var settingsView = new LogIt.SettingsView();
	
	var value = $('input[name=dayHours]').val();
	assert.equal(10, value);
	var chk = $('input[name=weekView]:checked').val();
	assert.equal("5", chk);
});
test( "Test isValidDayHour", function(assert) {
	var settingsView = new LogIt.SettingsView();
	
	var v = settingsView.isValidDayHour();
	assert.equal(false, v);
	
	v = settingsView.isValidDayHour('');
	assert.equal(false, v);

	v = settingsView.isValidDayHour('a');
	assert.equal(false, v);

	v = settingsView.isValidDayHour('0');
	assert.equal(false, v);

	v = settingsView.isValidDayHour('25');
	assert.equal(false, v);

	v = settingsView.isValidDayHour('24');
	assert.equal(true, v);

});
test( "Test onSave valid values", function(assert) {
	LogIt.App = {
		refresh : function(){}
	}
	var refresh = sinon.stub(LogIt.App, "refresh");
	var save = sinon.stub(LogIt.Settings, "save");

	var settingsView = new LogIt.SettingsView();
	settingsView.onSave();
	
	assert.ok(refresh.calledOnce);
	assert.ok(save.calledOnce);
	
	LogIt.App = null;
});
test( "Test onSave invalid values", function(assert) {
	LogIt.App = {
		refresh : function(){}
	}
	var refresh = sinon.stub(LogIt.App, "refresh");
	var save = sinon.stub(LogIt.Settings, "save");

	var settingsView = new LogIt.SettingsView();
	
	$('input[name=dayHours]').val(50);
	settingsView.onSave();
	
	assert.ok(refresh.notCalled);
	assert.ok(save.notCalled);
	
	LogIt.App = null;
});
