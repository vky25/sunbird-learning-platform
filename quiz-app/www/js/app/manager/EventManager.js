EventManager = {
	appEvents: ['enter', 'exit', 'remove', 'add', 'replace', 'show', 'hide'],
	registerEvents: function(plugin, data) {
		var events = undefined;
		if(data.events) {
			events = data.events.event
		} else {
			events = data.event;
		}

		if(_.isArray(events)) {
			events.forEach(function(e) {
				EventManager.registerEvent(e, plugin);
			});
		} else if(events) {
			EventManager.registerEvent(events, plugin);
		}
	},
	registerEvent: function(evt, plugin) {
		plugin.events.push(evt.type);
		if(_.contains(EventManager.appEvents, evt.type) || _.contains(plugin.appEvents, evt.type)) { // Handle app events
			plugin.on(evt.type, function() {
				EventManager.handleActions(evt, plugin);
			});
		} else { // Handle mouse events
			plugin._self.cursor = 'pointer';
			plugin._self.on(evt.type, function() {
				EventManager.handleActions(evt, plugin);
			});
		}
	},
	dispatchEvent: function(id, event) {
		var plugin = PluginManager.getPluginObject(id);
		if(_.contains(EventManager.appEvents, event) || _.contains(plugin.appEvents, event)) { // Dispatch app events
			plugin.dispatchEvent(event);
		} else { // Dispatch touch events
			plugin._self.dispatchEvent(event);
		}
	},
	handleActions: function(evt, plugin) {
		if(_.isArray(evt.action)) {
			evt.action.forEach(function(action) {
				EventManager.handleAction(action, plugin);
			});
		} else if(evt.action) {
			EventManager.handleAction(evt.action, plugin);
		}
	},
	handleAction: function(action, plugin) {
		if(action.param) {
			action.value = plugin._stage.params[action.param] || '';
		}
		if(action.type === 'animation') {
			AnimationManager.handle(action, plugin);
		} else {
			CommandManager.handle(action);
		}
	}
}