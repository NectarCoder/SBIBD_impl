import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;

public class OSServer extends Thread {

	private static int portNumber = 8080;
	private static Integer clientId = 1;
	private static Hashtable threads = new Hashtable();
	private static Hashtable messages = new Hashtable();
	private static Hashtable publicKeys = new Hashtable();
	private static OSLogger log = new OSLogger("OSServer");
	private static Integer adminClientId = 0;

	public static void setAdminClientId(Integer clientId) {
		adminClientId = clientId;
	}

	public static Integer getAdminClientId() {
		return adminClientId;
	}

	public void run() {
		ServerSocket serverSocket = null;
		try {
			// Start the server with appropriate port number
			serverSocket = new ServerSocket(portNumber);
			log.debug("Server started successfully"); // Log message

			while (true) {
				Socket clientSocket = serverSocket.accept();
				OSServerThread serverThread = new OSServerThread(clientSocket, threads, messages, clientId, this,
						publicKeys);
				Thread processRequest = new Thread(serverThread);
				threads.put(clientId, serverThread);
				processRequest.start();
				log.info("Client " + clientId + " started");
				clientId++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void processMessages() {
		log.info("inside processMessages");
		if (messages.size() > 0) {
			log.info("Messages size = " + messages.size());
			Enumeration keys = messages.keys();
			while (keys.hasMoreElements()) {
				Integer clientId = (Integer) keys.nextElement();
				log.info("Processing message for clientId:" + clientId);
				OSServerThread thread = (OSServerThread) threads.get(clientId);
				thread.sendMessageToClient((Message) messages.get(clientId));
				messages.remove(clientId);
			}
		}
	}

	public static void main(String[] args) {
		OSServer server = new OSServer();
		server.start();
	}
}