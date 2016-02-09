var LogIt = LogIt || {};
LogIt.SettingsView = Backbone.View.extend({

	el:  '#settings',
	startDate: null,
	
	events: {
		'click button.save': 'onSave',
	},
	
	initialize: function (options) {
		var w = LogIt.Settings.weekView();
		var h = LogIt.Settings.dayHour();
		var s = 'input[name=weekView][value=' + w + ']';
		$(s).prop("checked", true);
		$('input[name=dayHours]').val(h);
	},

	onSave: function(){
		var w = $('input[name=weekView]:checked').val();
		var h = $('input[name=dayHours]').val();
		if (!this.isValidDayHour(h)){
			$('input[name=dayHours]').parent().addClass('has-error');
			return;
		} else {
			$('input[name=dayHours]').parent().removeClass('has-error');
			var obj = {
				weekView : w,
				dayHour : parseInt(h)
			};
			LogIt.Settings.save(obj);
			$('#settings').modal('hide');
			LogIt.App.refresh();
		}
	},
	
	isValidDayHour: function(h){
		var valid = false;
		if (h && h !== ''){
			var v = parseInt(h);
			return v > 0 && v <= 24;
		}
		return valid;
	}
	
});