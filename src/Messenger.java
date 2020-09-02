public interface Messenger {

	public long send(Object message, int nodeId);
	public long broadcast(Object message, boolean includeSelf);
}
