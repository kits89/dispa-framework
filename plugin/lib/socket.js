const {components} = require("chrome");
const {NetUtil} = components.utils.import("resource://gre/modules/NetUtil.jsm");

const SERVER = 'localhost';
const PORT = 6666;

exports.send = function(message, callback) {
    try {
        var dataListener = {
            data: [],
            onStartRequest: function(request, context) {
            	console.log("[DisPA] - Connected to DisPAServer");
            },
            onStopRequest: function(request, context, status) {
                inputstream.close();
                outputstream.close();
                callback(this.data);
            },
            onDataAvailable: function(request, context, inputStream, offset, count) {
                logMessage("Data avaiable ["+count+"] long");
                try {
	                var reply = scriptible.read(count);
	                console.log("Reading ["+count+"]["+offset+"]["+reply+"]");
	                this.data.push(reply);
                } catch(ex) {
                    console.log("Error on Data Avaialbel ["+ex+"]");
                }
            }
        };

        var socketTransportService = components.classes["@mozilla.org/network/socket-transport-service;1"]
			.getService(components.interfaces.nsISocketTransportService)
        var socketTransport = socketTransportService
        	.createTransport(["udp"], 1, SERVER, PORT, null);
        var inputStream = socketTransport.openInputStream(0, 0, 0);
        var scriptible = components.classes["@mozilla.org/scriptableinputstream;1"]
			.createInstance(components.interfaces.nsIScriptableInputStream);
			
        scriptible.init(inputStream);

        var pump = components.classes["@mozilla.org/network/input-stream-pump;1"]
	        .createInstance(components.interfaces.nsIInputStreamPump);
        pump.init(inputStream, -1, -1, 0, 0, false);
        pump.asyncRead(dataListener, null);

        var outputStream = socketTransport.openOutputStream(0,0,0);
        outputStream.write(message, message.length);
        console.log("Sent ["+message+"]");
    } catch(ex) {
        console.log(ex);
    }
}
