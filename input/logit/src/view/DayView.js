var LogIt = LogIt || {};
LogIt.DayView = Backbone.View.extend({

	tagName:  'div',
	className: 'column panel panel-info',
//	_tpl: _.template($('#day-template').html()),
	
	initialize: function (options) {
	},

	render: function() {
		var h = this._evaluate(this.model);
//		var h = this._tpl(this.model);
		this.$el.html(h);
		return this;
    },
	
	showTasks: function(tasks){
		_.each(tasks, this._renderTask, this);
	},
	
	markToday: function(){
		$(this.el).addClass('panel-warning');
	},
	
	_renderTask: function(task){
		var view = new LogIt.TaskView({model: task});
		this.$('.list-group').append(view.render().el);
	},

	_evaluate: function(model){
		var html = "<div class='panel-heading'>";
		html += "<h4>" + model.weekDay + "</h4>";
		html += "<h5>" + model.dateString + "</h5>";
		html += "<span>" + model.totalHrs + "</span>";
		html += "</div>";
		html += "<div class='panel-body'>";
		html += "<div class='list-group'></div>";
		html += "</div>";
		
		return html;
	}

});