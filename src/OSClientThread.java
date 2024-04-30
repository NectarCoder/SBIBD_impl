import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

//OSClientThread will process the replies from the server
public class OSClientThread implements Runnable {

	public OSClient client = null;
	public OSLogger log = new OSLogger("OSClientThread");

	public OSClientThread(OSClient client) {
		this.client = client;
	}

	public void run() {

		boolean loop = true;

		while (loop) {
			log.debug("Waiting for message from server");

			Message broadcast = null;
			try {
				if (client.objectInputStream == null) {
					client.objectInputStream = new ObjectInputStream(client.socket.getInputStream());
				}
				broadcast = (Message) client.objectInputStream.readObject();

				log.debug("Server message received" + broadcast);
				/*
				log.debug(broadcast.getClientId().toString());
				log.debug(broadcast.getMessageDate().toString());
				log.debug(broadcast.getMessageType().toString());
				log.debug(broadcast.getMessageContent().toString());
				*/

				client.processReply(broadcast);
			} catch (Exception e) {
				e.printStackTrace();
				loop = false;
			}
		}
	}
}
