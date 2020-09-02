import java.io.Serializable;

public class Message implements Serializable{
	
}


class LamportRequestMessage extends Message implements Serializable{
	int nodeId;
}

class LamportReplyMessage extends Message implements Serializable{
	
}

class LamportReleaseMessage extends Message implements Serializable{
	
}


class PrivilegeTOBToken extends Message implements Serializable{
	long seqNum;
	
	PrivilegeTOBToken(long seqNum){
		this.seqNum = seqNum;
	}
}

class PrivilegeTOBMessage extends Message implements Serializable{
	long broadcastId;
	String content;
	
	PrivilegeTOBMessage(long broadcastId, String content){
		this.broadcastId = broadcastId; 
		this.content = content;
	}
}

class MessageContainer implements Serializable {

	Object message;
	long clock;
	int senderId;
	
	MessageContainer(){
	}
	
	MessageContainer(Object message, long clock, int senderId){
		this.message = message;
		this.clock = clock;
		this.senderId = senderId;
	}
}

class FinishMessageContainer extends MessageContainer implements Serializable{
	
}

