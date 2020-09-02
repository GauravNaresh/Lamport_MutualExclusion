import java.util.*;

public class ApplicationModule implements TOBProvider{
	MESProvider mesProvider;
	Messenger messenger;
	int nodeId;
	int N;
	int K;
	long nextDeliver;

	LinkedList<String> messagesToSend = new LinkedList<String>();
	Hashtable<Long, String> receivedMessages = new Hashtable<Long, String>();

	public ApplicationModule(MESProvider mesProvider, Messenger messenger, int nodeId, int N, int K) {
		this.mesProvider = mesProvider;
		this.messenger = messenger;
		this.nodeId = nodeId;
		this.N = N;
		this.K = K;
		nextDeliver = 1;
		if(this.nodeId == 0){
			messenger.send(new PrivilegeTOBToken(1), (this.nodeId + 1) % this.N);
		}
	}

	public void tobSend(String m) {
		synchronized(this){
			messagesToSend.add(m);
		}
	}

	public String tobReceive() {
		synchronized(this){
			String ret = null;
			while(!receivedMessages.containsKey(nextDeliver)){
				try{
					wait();
				} catch(InterruptedException iex){
					iex.printStackTrace();
				}
			}
			ret = receivedMessages.get(nextDeliver);
			receivedMessages.remove(nextDeliver);
			nextDeliver++;
			return ret;
		}
	}

	public void process(Object message, int nodeId, long sendTime, long receiveTime){
		if(message instanceof PrivilegeTOBMessage){
			synchronized(this){
				PrivilegeTOBMessage tobMessage = (PrivilegeTOBMessage)message;
				
				receivedMessages.put(tobMessage.broadcastId, tobMessage.content);
				if(receivedMessages.containsKey(nextDeliver)){
					this.notify();
				}
			}
		} else if (message instanceof PrivilegeTOBToken) {
			System.out.println("Asking for the critical section!!!");
			new CSThread(this, (PrivilegeTOBToken) message).start();
		}
	}
}

class CSThread extends Thread{
	ApplicationModule tobProvider;
	PrivilegeTOBToken token;

	CSThread(ApplicationModule tobProvider, PrivilegeTOBToken token){
		this.tobProvider = tobProvider;
		this.token = token;
	}

	public void run(){
		tobProvider.mesProvider.csEnter();
		synchronized(tobProvider){
			for(String m: tobProvider.messagesToSend){
				tobProvider.messenger.broadcast(new PrivilegeTOBMessage(token.seqNum, m), true);
				token.seqNum++;
			}
			tobProvider.messagesToSend.clear();
			if(token.seqNum <= (tobProvider.N * tobProvider.K) ){
				tobProvider.messenger.send(token, (tobProvider.nodeId + 1) % tobProvider.N);
			}
		}
		tobProvider.mesProvider.csExit();
	}
}
