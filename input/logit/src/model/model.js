var LogIt = LogIt || {};
LogIt.Task = Backbone.Model.extend({

	idAttribute : 'timestamp',
	
	methodMap : {
		'create': 'put',
		'update': 'post',
		'delete': 'remove',
		'read':   'get'
	},
	
	initialize: function(attrs){
		var log, u, l;
		if (attrs){
			log = attrs.log;
		}
		if (log){
			l = log.slice(0, -1);
			u = log.slice(-1);
			if (!u.match('h|d|m')){
				u = 'h';
				l = log;
			}
			l = Number(l);
			this.set('value', l);
			this.set('unit', u);
			this.unset('log');
		}
	},
	
	isValidModel: function(){
		var v = this.get('value'), u = this.get('unit'), t = this.get('task'), d = this.get('date');
		var msg = {};
		if (typeof(t) === 'undefined' || t === null){
			msg.task = "Task description is mandatory";
		}
		if (typeof(d) === 'undefined'){
			msg.date = "Date is mandatory";
		}
		if (typeof(v) === 'undefined'){
			msg.log = "Time is mandatory";
		} else {
			if (!isFinite(v)){
				msg.log = 'Incorrect syntax. Use like 1.5d, 1.5h, 30m';
			}
			if (v <=0 ){
				msg.log = 'Incorrect value. Use positive numbers';
			}
		}
		
		if (!_.isEmpty(msg)){
			return msg;
		}
	},

	sync: function(method, model, options){
		var m = this.methodMap[method];
		if (m == 'put'){
			model.set('timestamp', Date.now());
		}
		var obj = model.toJSON();
		if (m == 'remove'){
			obj = obj.timestamp;
		}
		
		//Possibility of mocking datastore to test sync function.
		var ds = options.ds || LogIt.ds;
		ds.sync(m, obj, function(result){
		}, this);
	},
	
	toHour: function(precise){
		var h, log = this.get('value'), unit = this.get('unit');
		switch (unit){
			case 'd':
				h = log * LogIt.Settings.dayHour();
				break;
			case 'h':
				h = log;
				break;
			case 'm':
				h = log / 60;
				break;
		}
		if (precise){
			h = h.toPrecision(3);
		}
		return h;
	},
	
	getLogString: function(){
		var str, log = this.get('value'), unit = this.get('unit');
		switch (unit){
			case 'd':
				str = 'days';
				break;
			case 'h':
				str = 'hours';
				break;
			case 'm':
				str = 'mins';
				break;
		}
		return log + " " + str;
	}

});

LogIt.TaskCollection = Backbone.Collection.extend({
	
	model: LogIt.Task,
	
	day: function(d){
		var refDate = d || Date.today();
		var result = this.filter(function(item){
			var date = item.get('date');
			return refDate.compareTo(date) === 0;
		});
		return result;
	},
	
	sum: function(items){
		var sum = 0;
		_.each(items, function(item){
			sum += item.toHour();
		});
		return sum.toPrecision(3);
	},
	
	match: function(q){
		q = q.toLowerCase();
		var r = new RegExp('\\b' + q, 'i');
		var result = this.chain().filter(function(item){
			var t = item.get('task');
			return r.test(t);
		}).map(function(item){
			return item.get('task');
		}).value();
		
		result = _.uniq(result);
		return result;
	}
	
});