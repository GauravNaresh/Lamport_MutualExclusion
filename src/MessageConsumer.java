
public interface MessageConsumer {
	public void process(Object message, int nodeId, long sendTime, long receiveTime);
}
