var LogIt = LogIt || {};
LogIt.TaskView = Backbone.View.extend({

	tagName:  'a',
	className: 'list-group-item',
//	_tpl: _.template($('#item-template').html()),
	
	events: {
		'click .remove': 'remove',
		'mouseover ': 'onmouseover',
		'mouseout ': 'onmouseout',
	},

	initialize: function (options) {
	},

	render: function() {
		var obj = {
			task: this.model.get('task'),
			time: this.model.getLogString()
		};
//		this.$el.html(this._tpl(obj));
      this.$el.html(this._evaluate(obj));
		return this;
    },
	
	onmouseover: function(){
		this.$el.addClass('active');
	},
	
	onmouseout: function(){
		this.$el.removeClass('active');
	},

	remove: function(){
		this.model.destroy();
	},
	
	_evaluate: function(model){
		var html = "<h4 class='list-group-item-heading'>" + model.task + "</h4>";
		html += "<p class='list-group-item-text'>" + model.time + "</p>";
		html += "<button type='button' class='btn btn-default btn-xs pull-right remove'>";
		html += "<span class='glyphicon glyphicon glyphicon-trash'></span>";
		html += "</button>";
		return html;
	}
	
});