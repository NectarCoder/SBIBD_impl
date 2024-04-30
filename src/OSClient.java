import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Date;
import java.util.Hashtable;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class OSClient implements Runnable {

	public OSLogger log = new OSLogger("OSClient");
	public Integer clientId = null;
	public String groupName = "KRISHNA";
	public int portNumber = 0;
	public String myPrivateKey = null;
	public String myPublicKey = null;
	public PrivateKey privateKey = null;
	public PublicKey publicKey = null;
	public SecretKey myKey = null;
	public String mySecretKey = null;
	public OSEncryption encryption = null;
	public boolean mode = false;

	public String hostname = null;
	public Socket socket = null;
	public BufferedReader in = null;
	public PrintWriter out = null;
	public BufferedWriter fileWriter = null;
	public BufferedReader userInput = null;
	public ObjectInputStream objectInputStream = null;
	public ObjectOutputStream objectOutputStream = null;
	public String userCommand = null;

	public Hashtable myMessages = new Hashtable();
	public Hashtable myResponses = new Hashtable();
	public OSClientThread clientHelper = null;

	public OSClient(int portNumber, String hostname) {
		log.debug("OSClient Constructor");
		this.portNumber = portNumber;
		this.hostname = hostname;
		this.initialize();
	}

	public OSClient(int portNumber, String hostname, boolean mode) {
		log.debug("OSClient Constructor");
		this.portNumber = portNumber;
		this.hostname = hostname;
		this.mode = mode;
		this.initialize();
	}

	// To Do
	public void createKeys() {
		log.debug("Inside createKeys");
	}

	public void initialize() {
		log.debug("Client initializing");
		this.connectToServer();
		log.debug("after connectToServer");
		// this.createObjectStreams();
		// log.debug("after createObjectStreams");
		log.debug("OSClient initiatlized");
		this.clientHelper = new OSClientThread(this);
		Thread helper = new Thread(clientHelper);
		helper.start();

		try {
			this.encryption = new OSEncryption();
			// AES Keys
			this.myKey = encryption.getSecretKey();
			this.mySecretKey = encryption.getSecretKeyStr();

			// RSA Keys
			this.myPrivateKey = encryption.getPrivateKey();
			this.myPublicKey = encryption.getPublicKey();
			this.privateKey = encryption.keypair.getPrivate();
			this.publicKey = encryption.keypair.getPublic();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void processConnection() {
		log.debug("inside processConnection");
		Message message = new Message();
		message.setMessageDate(new Date());
		message.setMessageType(MessageType.START_CONNECTION);
		message.setMessageContent("Initiating connection");
		this.sendMessage(message);
	}

	public void processGetSecretKey() {
		log.debug("inside processGetSecretKey");
		Message message = new Message();
		message.setMessageDate(new Date());
		message.setMessageType(MessageType.GET_SECRET_KEY);
		message.setMessageContent(this.myPublicKey);
		this.sendMessage(message);
	}

	public void processAdministrator() {
		log.debug("inside processAdministrator");
		Message adminMessage = new Message();
		adminMessage.setClientId(clientId);
		adminMessage.setGroupName(this.groupName);
		adminMessage.setMessageDate(new Date());
		adminMessage.setMessageType(MessageType.ADMIN);
		adminMessage.setMessageContent("Creating group " + this.groupName);
		log.debug("Created Message" + adminMessage);
		this.sendMessage(adminMessage);
	}

	public void closeServerConnection() {
		log.debug("inside closeServerConnection");
		Message closeMessage = new Message();
		closeMessage.setGroupName(this.groupName);
		closeMessage.setMessageDate(new Date());
		closeMessage.setMessageType(MessageType.END_CONNECTION);
		closeMessage.setMessageContent("Closing connection, Bye");
		log.debug("Created Message" + closeMessage);
		this.sendMessage(closeMessage);
	}

	public void sendMessage(Message message) {
		log.debug("inside processMessage");
		try {
			if (this.objectOutputStream == null) {
				this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
			}
			this.objectOutputStream.writeObject(message);
			this.objectOutputStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void processReply(Message message) throws Exception {
		log.debug("inside processReply");
		log.debug("message is " + message.getMessageContent());

		if (message.getMessageType() == MessageType.MESSAGE) {
			log.broadcast(message.getMessageContent().toString());
		} else if (message.getMessageType() == MessageType.PROCESS_CONNECTION) {
			this.clientId = message.getClientId();
			log.debug(message.getMessageContent().toString());
		} else if (message.getMessageType() == MessageType.PROCESS_SECRET_KEY) {
			/*
			 * String clientPublicKey = (String) message.getMessageContent();
			 * adminReply.setClientId(server.getAdminClientId());
			 * adminReply.setReplyToclientId(clientId);
			 * adminReply.setMessageContent(clientPublicKey); adminReply.setMessageDate(new
			 * Date()); adminReply.setMessageType(MessageType.PROCESS_SECRET_KEY);
			 */
			Integer replyClientId = message.getReplyToclientId();
			log.debug("Reply to Client Id " + replyClientId);
			String clientPublicKeyStr = (String) message.getMessageContent();
			PublicKey clientPublicKey = new KeyPairFromString().parsePublicKey(clientPublicKeyStr);
			String encryptedSecretKey = encryption.do_RSAEncryptionKey(this.mySecretKey, clientPublicKey);

			Message secretMessage = new Message();
			secretMessage.setReplyToclientId(replyClientId);
			secretMessage.setMessageDate(new Date());
			secretMessage.setMessageType(MessageType.SECRET_KEY);
			secretMessage.setMessageContent(encryptedSecretKey);
			this.sendMessage(secretMessage);
		} else if (message.getMessageType() == MessageType.ACCEPT_SECRET_KEY) {
			/*
			 * Message reply = new Message();
			 * reply.setClientId(message.getReplyToclientId());
			 * reply.setMessageContent(message.getMessageContent());
			 * reply.setMessageDate(new Date());
			 * reply.setMessageType(MessageType.ACCEPT_SECRET_KEY);
			 * messages.put(message.getReplyToclientId(), reply);
			 */
			String encryptedSecretKey = (String) message.getMessageContent();
			log.debug("encryptedSecretKey - " + encryptedSecretKey);
			String tempSecretKey = encryption.do_RSADecryptionKey(encryptedSecretKey, privateKey);
			this.myKey = encryption.getSecretKey(tempSecretKey);
			encryption.setSecretKey(this.myKey);

			log.debug("Secret key has been recevied and processed on the client" + clientId);

			Message secretMessage = new Message();
			secretMessage.setClientId(clientId);
			secretMessage.setMessageDate(new Date());
			secretMessage.setMessageType(MessageType.MESSAGE);
			secretMessage.setMessageContent("Secret key has been recevied");
			this.sendMessage(secretMessage);
		} else if (message.getMessageType() == MessageType.GROUP_MESSAGE) {
			String encryptedMessage = (String) message.getMessageContent();
			log.debug("Processing group message, encrypted message recevied - " + encryptedMessage);
			String decryptedMessage = encryption.decrypt(encryptedMessage);
			log.debug("Processing group message, decrypted message - " + decryptedMessage);
			log.broadcast(decryptedMessage);
		}

	}

	public void run() {
		if (this.mode == true) {
			this.runAuto();
		} else {
			boolean loop = true;
			try {
				if (userInput == null) {
					userInput = new BufferedReader(new InputStreamReader(System.in));
				}
				while (loop) {
					System.out.print("Enter command - ");
					userCommand = userInput.readLine();
					if (userCommand == null) {
						loop = false;
						break;
					}
					if (userCommand.equalsIgnoreCase("CONNECT")) {
						this.processConnection();
					} else if (userCommand.equalsIgnoreCase("ADMIN")) {
						this.processAdministrator();
					} else if (userCommand.equalsIgnoreCase("SECRET")) {
						this.processGetSecretKey();
					} else if (userCommand.equalsIgnoreCase("GROUP")) {
						System.out.print("Enter message - ");
						userCommand = userInput.readLine();
						processBroadcastMessage(userCommand);
					} else if (userCommand.equalsIgnoreCase("MESSAGE")) {
						System.out.print("Enter message - ");
						userCommand = userInput.readLine();
						processBroadcastMessage(userCommand);
					} else if (userCommand.equalsIgnoreCase("END")) {
						closeServerConnection();
						loop = false;
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void runAuto() {
		boolean loop = true;
		try {
			if (userInput == null) {
				userInput = new BufferedReader(new InputStreamReader(System.in));
			}
			this.processConnection();
			this.processGetSecretKey();
			while (loop) {
				System.out.print("Enter command - ");
				userCommand = userInput.readLine();
				if (userCommand.equalsIgnoreCase("CONNECT")) {
					this.processConnection();
				} else if (userCommand.equalsIgnoreCase("ADMIN")) {
					this.processAdministrator();
				} else if (userCommand.equalsIgnoreCase("SECRET")) {
					this.processGetSecretKey();
				} else if (userCommand.equalsIgnoreCase("GROUP")) {
					System.out.print("Enter message - ");
					userCommand = userInput.readLine();
					processBroadcastMessage(userCommand);
				} else if (userCommand.equalsIgnoreCase("MESSAGE")) {
					System.out.print("Enter message - ");
					userCommand = userInput.readLine();
					processBroadcastMessage(userCommand);
				} else if (userCommand.equalsIgnoreCase("END")) {
					closeServerConnection();
					loop = false;
					break;
				}
			}
			
			/*
			while (loop) {
				this.processConnection();
				//Thread.sleep(5000);
				this.processGetSecretKey();
				//Thread.sleep(5000);
				System.out.print("Enter command - ");
				userCommand = userInput.readLine();
				//this.wait();
			}*/
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void processGroupMessage(String messageString) {
		log.debug("inside processGroupMessage");
		Message message = new Message();
		message.setMessageDate(new Date());
		message.setMessageType(MessageType.MESSAGE);
		message.setMessageContent(messageString);
		this.sendMessage(message);
	}

	private void processBroadcastMessage(String messageString) throws Exception {
		log.debug("inside processBroadcastMessage" + messageString);
		Message message = new Message();
		message.setClientId(clientId);
		message.setGroupName(this.groupName);
		message.setMessageDate(new Date());
		message.setMessageType(MessageType.GROUP_MESSAGE);
		String encryptedMessageString = encryption.encrypt(messageString);
		log.debug("ecrypted message using secret key" + encryptedMessageString);

		String decryptedMessage = encryption.decrypt(encryptedMessageString);
		log.debug("decrypted message using secret key" + decryptedMessage);

		message.setMessageContent(encryptedMessageString);
		log.debug("Created Message" + message);
		this.sendMessage(message);
	}

	public static void main(String[] args) {
		if (args.length == 0) {
			try {
				OSClient client = new OSClient(8080, "localhost");
				Thread clientThread = new Thread(client);
				clientThread.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (args.length == 1) {
			int noOfThreads = Integer.parseInt(args[0]);
			try {
				for (int i = 0; i < noOfThreads; i++) {
					OSClient client = new OSClient(8080, "localhost", true);
					Thread clientThread = new Thread(client);
					clientThread.start();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public void connectToServer() {
		try {
			// Establish server-client connection
			socket = new Socket(this.hostname, this.portNumber);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void createObjectStreams() {
		try {
			objectInputStream = new ObjectInputStream(socket.getInputStream());
			objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void closeConnection() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}