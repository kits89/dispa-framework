/*
 *  Script for AOL logs
 *  IIIA - CSIC
 */

/*
 * Import dependences
 */
const {components} = require("chrome");
const Socket = require("socket");						// Module to establish the connection
const tabs = require('tabs');							// Tabs' utilities
const widgets = require("widget");						// Widget
const Data = require("self").data;						// Module to access addon's URLs
const notifications = require("notifications");			// Module to display notifications
const io = require('io.js');
var history = require('history');

//Find add-on's directory
const dirsvc = components.classes["@mozilla.org/file/directory_service;1"]
		.getService(components.interfaces.nsIProperties);
const abs = dirsvc.get("ProfD", components.interfaces.nsIFile);
abs.append('DisPA');

var QRY = 0;
var VST = 1;
var RES = 2;
var ERR = 3;
var OFF = 4;


exports.main = function() {
	var w = new widgets.Widget({
		id: "dispa-link",
		label: "DisPA add-on",
		contentURL: Data.url("img/iiia.png"),
		onClick: function() {			
			// On click, open a tab with the search page
			tabs.open({
				 url: Data.url('search.html'),
				 onReady: function() {
					 // Attach a worker that handles the event of sending the query
					 var worker = this.attach({
						 contentScriptFile: [Data.url('js/jquery.js'),
						 					 Data.url('js/handleQuery.js')],
					 });				 
					 // When the event is triggered:
					 worker.port.on('querySent', function(query) {						 						 	
						// Make connection to classifier and send query

						var buffer = [];
						// Push opcode
						buffer.push(QRY.toString(2));
						
						// Push message contents
						 for(var i = 0, n = query.length; i < n; i++) {
        					var char = query.charCodeAt(i);
        					buffer.push(char >>> 8, char & 0xFF);
    					}					
																
						Socket.send(buffer, function(rcv) {
							var sepIndex = 1,
							  opcode = rcv.slice(0, sepIndex), 
							  contents = rcv.slice(sepIndex+1, rcv.length);
							console.log(opcode);
													
							switch (parseInt(opcode)) {
								case RES:
	  							  worker.port.emit('loadResponse', contents);
								  break;
								case ERR:
								  console.log("[DisPA] - There was an error in the server.");
								  break;
								default:
								  console.log("[DisPA] - ERROR: invalid opcode.");
							}
						});												 
					});
				 }
			}); 	 
		}
	});
}

// Reason is one of the following strings describing the reason your 
// add-on was unloaded: uninstall, disable, shutdown, upgrade, or downgrade.
exports.onUnload = function (reason) {
  Socket.send(OFF)	;	
  console.log('[DisPA] was unloaded: ' + reason);
};
