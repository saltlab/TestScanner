var LogIt = LogIt || {};
LogIt.EditView = Backbone.View.extend({

	el:  '.editor',
	
	events: {
		'click #addLog': 'submit'
	},

	initialize: function (options) {
		this.options = options || {};
		this.task = this.$('#task');
		this.log = this.$('#log');
		this.date = this.$('#date');
		this.$('.datePicker').datepicker({
			autoclose: true,
			todayHighlight: true,
			endDate: "today",
			format: 'd M yyyy'
		});
		$('#task').typeahead({
			minLength: 1,
			highlight: true,
		},
		{
			name: 'tasks',
			displayKey: function(item){
				return item;
			},
			source: this._filter
		});
		this._reset();
	},

	submit: function() {
		var tk = this.task.val() !== "" ? this.task.val() : null;
		var tm = this.log.val() !== "" ? this.log.val() : null;
		var date = Date.today();
		var d = this.date.val() !== "" ? this.date.val() : null;
		if (d){
			date = Date.parse(d, LogIt.Settings.dateFormat());
		}
		
		this.$('.form-group').removeClass('has-error');
		this.$('p.text-danger').html('');
		var m = new LogIt.Task({'task': tk, 'log': tm, date: date});
		var r = m.isValidModel();
		if (r){
			var h = "";
			for (var s in r){
				var q = 'p.text-danger.' + s;
				this.$(q).html(r[s]);
				this.$(q).parent().addClass('has-error');
			}
		} else {
			LogIt.Tasks.create(m);
			this._reset();
		}
		return false;
	},
	
	_reset: function(){
		this.task.val('');
		this.log.val('');
		this.date.val('');
		this.task.focus();
	},
	
	_filter: function(q, cb){
		var results = LogIt.Tasks.match(q);
		cb(results);
	}
	
});

