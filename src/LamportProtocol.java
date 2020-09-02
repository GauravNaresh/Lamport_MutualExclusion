import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.Random;


public class LamportProtocol implements MESProvider {
	Messenger messenger;	
	ProjectMain use;
	HashMap<Integer, Long> lastMessageTimes = new HashMap<Integer, Long>();
	PriorityQueue<CSRequest> pq = new PriorityQueue<CSRequest>();
	int N;
	int nodeId; 
	long temp_clk;

	Object lock = new Object();

	boolean inCriticalSection = false;
	CSRequest currentRequest = null;
	ArrayList<Integer> requestsWhenInCS = new ArrayList<Integer>();

	public LamportProtocol(Messenger messenger, int nodeId,  int N){
		this.use = (ProjectMain)messenger;
		this.messenger = messenger;
		this.nodeId = nodeId;
		this.N = N;
	}

	public void initialize(){
		currentRequest = null;
		inCriticalSection = false;
		lastMessageTimes.clear();
		requestsWhenInCS.clear();
	}


	public void process(Object message, int senderId, long sendTime, long receiveTime){
		synchronized(this){
			if(inCriticalSection){
				if(message instanceof LamportRequestMessage){
					System.out.println("received a request message in cs");
					pq.add(new CSRequest(senderId, sendTime));
					requestsWhenInCS.add(senderId);
				} else if (message instanceof LamportReplyMessage){
				} else if (message instanceof LamportReleaseMessage){
					;
				} else {
					;
				}
			} else {
				if(message instanceof LamportRequestMessage){
					CSRequest nextRequest = new CSRequest(senderId, sendTime);
					if(currentRequest != null &&  (currentRequest.compareTo(nextRequest) < 0)){
						pq.add(nextRequest);
					} else {
						pq.add(nextRequest);
						messenger.send(new LamportReplyMessage(), senderId);						
					}
				} else if (message instanceof LamportReplyMessage){
					;
				} else if (message instanceof LamportReleaseMessage){
					pq.poll();					
				} else {
					;
				}
				if(currentRequest != null){
					lastMessageTimes.put(senderId, sendTime);
					if(l1() && l2()){
						inCriticalSection = true;
						notify();
					}
				} 
			}
		}

	}

	public void csEnter() {

		synchronized(this){
			initialize();
			currentRequest = new CSRequest(nodeId, -1);
			currentRequest.clock = messenger.broadcast(new LamportRequestMessage(), false);
			
			temp_clk = currentRequest.clock;
			String nodex = "Node " + Integer.toString(nodeId) + " Entered CS: " + Long.toString(currentRequest.clock);
					System.out.println(nodex);
			this.use.output.add(nodex);
			this.use.cat.add(nodex);
			pq.add(currentRequest);
			while(!inCriticalSection){
				try{
					wait(this.use.getCSdelay());
					System.out.println("done waiting!!" + inCriticalSection);
 
				} catch (InterruptedException iex){
					;
				}
			} 
			return;
		}
	}

	public void csExit() {
		long temp;
		synchronized(this){
			System.out.println("got the lock!");
			messenger.broadcast(new LamportReleaseMessage(), false);
			pq.poll();
			for(int i:requestsWhenInCS){
				messenger.send(new LamportReplyMessage(), i);
			}
			initialize();
			requestsWhenInCS.clear();
			inCriticalSection = false;
			temp= temp_clk+ this.use.getCSdelay();
			String nodey = "Node " + Integer.toString(nodeId) + " exits CS: " + Long.toString(temp);
			System.out.println(nodey);
			this.use.output.add(nodey);
			this.use.cat.add(nodey);
						
			 
		}

	}

	public boolean l1(){
		if(currentRequest!=null && lastMessageTimes.size() >= (N - 1)){
			for(Integer i: lastMessageTimes.keySet()){
				if(i!=nodeId){
					if(lastMessageTimes.get(i) <= currentRequest.clock){
						return false;
					}
				}
			}
			return true;
		}
		return false;
	}

	public boolean l2(){
		if(!pq.isEmpty()){
			CSRequest next = pq.peek();
			if(next.nodeId == this.nodeId){
				return true;
			} else {
				return false;
			}
		}
		return false;
	} 
}


class CSRequest implements Comparable<CSRequest>{
	int nodeId;
	long clock;

	CSRequest(int nodeId, long clock){
		this.nodeId = nodeId;
		this.clock = clock;
	}

	public int compareTo(CSRequest o) {
		if(this.clock == o.clock){
			return this.nodeId - o.nodeId;
		} else if(this.clock > o.clock){
			return 1;
		} else {
			return -1;
		}
	}
}

