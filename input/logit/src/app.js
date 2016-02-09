var LogIt = LogIt || {};
(function(){
	
	var editView, summaryView, settingsView;

	LogIt.Tasks = new LogIt.TaskCollection();
	LogIt.Settings.init(function(){
		editView = new LogIt.EditView();
		summaryView = new LogIt.SummaryView();
		settingsView = new LogIt.SettingsView();
		LogIt.ds.init();
	});

	var refresh = function(){
		summaryView.renderAll();
	};

	LogIt.App = {
		refresh : refresh
	};
})();
