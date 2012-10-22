package dispa.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class PluginConnection {

	/**
	 * @uml.property  name="port"
	 */
	private int port = 6666;
	/**
	 * @uml.property  name="serverSocket"
	 */
	private ServerSocket serverSocket = null;
	/**
	 * @uml.property  name="clientSocket"
	 */
	Socket clientSocket = null;
	/**
	 * @uml.property  name="in"
	 */
	BufferedReader in = null;
	/**
	 * @uml.property  name="out"
	 */
	PrintWriter out = null;
	
	public PluginConnection(int newPort) throws IOException {
		serverSocket = new ServerSocket(this.port);
	}
	
	public void send(String message) {
		if (this.clientSocket != null) {
			out.println(message);
		} else {
			System.err.println("ERROR: message could not be sent because "
					+ "connection is not established.");
		}
	}


	public void open() {		
		try {
			clientSocket = serverSocket.accept();			
		} catch (IOException e) {
			System.err.println("ERROR: PIRServer could not open connection.");
			e.printStackTrace();
		} 		
	}

	public String getRequest() {
		try {
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			out = new PrintWriter(clientSocket.getOutputStream(), true);
		} catch (IOException e) {
			System.err.println("ERROR: PIRServer could not open streams.");
			e.printStackTrace();
		}
		
		String query = null;
		try {
			query = in.readLine();
		} catch (IOException e) {
			System.err.println("ERROR: could not open parse query.");
			e.printStackTrace();
			out.println("-1");
		}
		return query;
	}
	
	public void close() {
		try {
			if (in != null) {
				in.close();				
			}			
			if (out != null) {
				out.close();				
			}			
			if (clientSocket != null) {
				clientSocket.close();
			}
			if (serverSocket != null) {
				serverSocket.close();
			}
		} catch (IOException e) {
		} catch (Exception e) { 
			e.printStackTrace();
			System.exit(-1);
		}
	}
}
