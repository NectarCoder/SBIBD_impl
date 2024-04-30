import java.util.Date;

public class OSLogger {

	private String className = null;
	private Integer clientId = null;
	private static String delimiter = " - ";
	private static String noClientId = " N/A ";

	public OSLogger(String className) {
		this.className = className;
	}

	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}

	public StringBuffer createMessage(String logMessage, LogType logType) {
		StringBuffer message = new StringBuffer();
		message.append(className);
		message.append(delimiter);
		message.append(new Date().toString());
		message.append(delimiter);
		message.append(logType.toString());
		message.append(delimiter);
		if (this.clientId != null) {
			message.append(clientId.toString());
		} else {
			message.append(noClientId);
		}
		message.append(delimiter);
		message.append(logMessage);

		return (message);
	}

	public static void main(String[] args) {
		OSLogger log = new OSLogger("OSLogger");
		log.info("Hare Krishna");
		log.debug("Hare Krishna");
		log.warn("Hare Krishna");
		log.error("Hare Krishna");
		log.critical("Hare Krishna");
		log.setClientId(1);
		log.critical("Hare Rama");
	}

	public void info(String logMessage) {
		StringBuffer message = this.createMessage(logMessage, LogType.INFO);
		System.out.println(message);
	}

	public void broadcast(String logMessage) {
		System.out.println("BROADCAST - " + logMessage);
	}

	public void debug(String logMessage) {
		StringBuffer message = this.createMessage(logMessage, LogType.DEBUG);
		System.out.println(message);
	}

	public void warn(String logMessage) {
		StringBuffer message = this.createMessage(logMessage, LogType.WARN);
		System.out.println(message);
	}

	public void error(String logMessage) {
		StringBuffer message = this.createMessage(logMessage, LogType.ERROR);
		System.out.println(message);
	}

	public void critical(String logMessage) {
		StringBuffer message = this.createMessage(logMessage, LogType.CRITICAL);
		System.out.println(message);
	}

	private void print(String logMessage) {

	}
}

enum LogType {
	DEBUG, INFO, WARN, CRITICAL, ERROR, BROADCAST;
}
