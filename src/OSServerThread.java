import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

public class OSServerThread implements Runnable {

	public Socket clientSocket;
	public Hashtable threads = null;
	public Hashtable messages = null;
	public Hashtable publicKeys = null;
	public Integer clientId = null;
	public OSLogger log = null;
	public Thread helper = null;
	public OSServer server = null;

	public ObjectInputStream objectInputStream = null;
	public ObjectOutputStream objectOutputStream = null;
	public boolean loop = true;

	public OSServerThread(Socket socket, Hashtable threads, Hashtable messages, Integer clientId, OSServer server,
			Hashtable publicKeys) {
		this.clientSocket = socket;
		this.threads = threads;
		this.messages = messages;
		this.clientId = clientId;
		this.log = new OSLogger("OSServerThread");
		this.log.setClientId(clientId);
		this.server = server;
		this.publicKeys = publicKeys;
	}

	public void createObjectStreams() {
		try {
			objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
			objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void endConnections() {
		try {
			this.objectInputStream.close();
			this.objectOutputStream.close();
			this.clientSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		this.createObjectStreams();

		while (loop) {
			Message request = null;
			Message reply = null;
			log.info("Starting to process the request");
			try {
				request = (Message) this.objectInputStream.readObject();
				log.info("request received from clientId:" + clientId + " message is" + request);
				processMessage(request);
			} catch (Exception e) {
				e.printStackTrace();
				log.critical("Error connecting to the client, stopping the server side helper thread");
				loop = false;
			}
		}
	}

	public void sendMessageToClient(Message message) {
		log.debug("inside sendMessageToClient");
		try {
			if (this.objectOutputStream == null) {
				this.objectOutputStream = new ObjectOutputStream(this.clientSocket.getOutputStream());
			}
			this.objectOutputStream.writeObject(message);
			this.objectOutputStream.flush();
			log.debug("message sent to client");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void processMessage(Message message) {
		log.info("Inside processMessage processing message " + message);

		if (message.getMessageType() == MessageType.START_CONNECTION) {
			Message reply = new Message();
			reply.setClientId(clientId);
			reply.setMessageContent("Connection accepted");
			reply.setMessageDate(new Date());
			reply.setMessageType(MessageType.PROCESS_CONNECTION);
			messages.put(clientId, reply);
			server.processMessages();
		} else if (message.getMessageType() == MessageType.ADMIN) {
			Message reply = new Message();
			reply.setClientId(clientId);
			reply.setMessageContent("Admin acknowledged");
			reply.setMessageDate(new Date());
			reply.setMessageType(MessageType.MESSAGE);
			messages.put(clientId, reply);
			server.setAdminClientId(clientId);
			server.processMessages();
		} else if (message.getMessageType() == MessageType.GET_SECRET_KEY) {
			Message reply = new Message();
			Message adminReply = new Message();

			String clientPublicKey = (String) message.getMessageContent();
			adminReply.setClientId(server.getAdminClientId());
			adminReply.setReplyToclientId(clientId);
			adminReply.setMessageContent(clientPublicKey);
			adminReply.setMessageDate(new Date());
			adminReply.setMessageType(MessageType.PROCESS_SECRET_KEY);
			messages.put(server.getAdminClientId(), adminReply);

			reply.setClientId(clientId);
			reply.setMessageContent("Request accepted for processing");
			reply.setMessageDate(new Date());
			reply.setMessageType(MessageType.MESSAGE);
			messages.put(clientId, reply);
			server.processMessages();
		} else if (message.getMessageType() == MessageType.SECRET_KEY) {
			Message reply = new Message();
			reply.setClientId(message.getReplyToclientId());
			reply.setMessageContent(message.getMessageContent());
			reply.setMessageDate(new Date());
			reply.setMessageType(MessageType.ACCEPT_SECRET_KEY);
			messages.put(message.getReplyToclientId(), reply);

			Message adminReply = new Message();
			adminReply.setClientId(clientId);
			adminReply.setMessageContent("Secret key sent to clientID:" + message.getReplyToclientId());
			adminReply.setMessageDate(new Date());
			adminReply.setMessageType(MessageType.MESSAGE);
			messages.put(clientId, adminReply);
			server.processMessages();
		} else if (message.getMessageType() == MessageType.END_CONNECTION) {
			Message reply = new Message();
			log.info("End connection received from client, closing connections");
			this.loop = false;
			reply.setClientId(clientId);
			reply.setMessageContent("Connection will be closed, request honored");
			reply.setMessageDate(new Date());
			reply.setMessageType(MessageType.MESSAGE);
		} else if (message.getMessageType() == MessageType.GROUP_MESSAGE) {
			Message reply = new Message();
			log.info("Message recevied, creating message for all the clients in the group");
			reply.setClientId(clientId);
			reply.setMessageContent("Processed message successfully");
			reply.setMessageDate(new Date());
			reply.setMessageType(MessageType.MESSAGE);
			messages.put(clientId, reply);
			server.processMessages();

			// String encryptedMessageString = (String) message.getMessageContent();
			// message.setMessageContent(encryptedMessageString);
			Enumeration keys = threads.keys();
			while (keys.hasMoreElements()) {
				Integer clientId = (Integer) keys.nextElement();
				log.info("Inserting message for clientId:" + clientId);
				messages.put(clientId, message.getDuplicate(clientId));
			}
			server.processMessages();
		}
	}
}