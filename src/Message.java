import java.util.Date;

public class Message implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2815974622156019387L;
	private Integer clientId = null;
	private Integer replyToclientId = null;
	private String groupName = null;
	private Date messageDate = new java.util.Date();
	private MessageType messageType = null;
	private Object messageContent = null;

	public String toString() {
		return (this.messageContent.toString());
		/*
		 * return ( "clientId:" + this.clientId + "\n" + "groupName:" + this.groupName +
		 * "\n" + "messageDate:" + this.messageDate + "\n" + "messageType:" +
		 * this.messageType + "\n" + "messageContent:" + this.messageContent );
		 */
	}

	public Message() {

	}

	public Message getDuplicate(Integer clientId) {
		Message duplicateMessage = new Message();
		duplicateMessage.setClientId(clientId);
		duplicateMessage.setReplyToclientId(replyToclientId);
		duplicateMessage.setGroupName(groupName);
		duplicateMessage.setMessageContent(this.getMessageContent());
		duplicateMessage.setMessageDate(messageDate);
		duplicateMessage.setMessageType(messageType);
		return (duplicateMessage);
	}

	public Integer getClientId() {
		return clientId;
	}

	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}

	public Integer getReplyToclientId() {
		return replyToclientId;
	}

	public void setReplyToclientId(Integer replyToclientId) {
		this.replyToclientId = replyToclientId;
	}

	
	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Date getMessageDate() {
		return messageDate;
	}

	public void setMessageDate(Date messageDate) {
		this.messageDate = messageDate;
	}

	public MessageType getMessageType() {
		return messageType;
	}

	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}

	public Object getMessageContent() {
		return messageContent;
	}

	public void setMessageContent(Object messageContent) {
		this.messageContent = messageContent;
	}

}
