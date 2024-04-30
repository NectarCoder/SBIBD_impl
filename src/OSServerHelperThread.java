import java.util.Enumeration;
import java.util.Hashtable;

public class OSServerHelperThread implements Runnable {

	private Hashtable threads = null;
	private Hashtable messages = null;
	private OSLogger log = null;

	public OSServerHelperThread(Hashtable threads, Hashtable messages) {
		this.messages = messages;
		this.threads = threads;
		this.log = new OSLogger("OSServerHelperThread");
	}

	@Override
	public void run() {
		while (true) {
			log.info("Checking to see if we have new messages");
			if (messages.size() > 0) {
				log.info("Messages size = " + messages.size());
				Enumeration keys = messages.keys();
				while (keys.hasMoreElements()) {
					Integer clientId = (Integer) keys.nextElement();
					log.info("Processing message for clientId:" + clientId);
					OSServerThread thread = (OSServerThread) threads.get(clientId);
					synchronized (thread) {
						thread.notify();
					}
				}
			}
			try {
				log.info("Sleeping ... to wait for more messages");
				synchronized (this) {
					this.wait();
				}
				log.info("Woke up...to process more messages");
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

}
