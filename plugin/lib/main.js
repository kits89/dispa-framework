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

const QRY = 0;
const VST = 1;
const ERR = 2;
const OFF = 3;
const RES = 4;
const SERVER = 'localhost';
const PORT = 6666;

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
						var msg = QRY + '|' + query;
						Socket.connect(SERVER, PORT, msg, function(rcv) {
							var sepIndex = rcv.indexOf('|'),
							  opcode = parseInt(rcv.slice(0, sepIndex)), 
							  contents = rcv.slice(sepIndex+1, rcv.length);
							console.log("msg: " + opcode);						
							switch (opcode) {
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
  Socket.connect(SERVER, PORT, OFF);	
  console.log('[DisPA] was unloaded: ' + reason);
};
