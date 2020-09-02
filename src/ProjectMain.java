
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

class Node {
	int nodeId;
	String host;
	int port;
	public Node(int nodeId, String host, int port) {
		super();
		this.nodeId = nodeId;
		this.host = host;
		this.port = port;
	}
}

public class ProjectMain implements Messenger{
		
	static String outputFileName;
	int id;
    int N,K,tobSendDelay,c,csdelay,reqdelay;
	long clock;
	String configurationFileName;
	
	ArrayList<Node> nodes = new ArrayList<Node>();
	HashMap<Integer,Node> store = new HashMap<Integer,Node>();
	HashMap<Integer,Socket> channels = new HashMap<Integer,Socket>();
	HashMap<Integer,ObjectOutputStream> oStream = new HashMap<Integer,ObjectOutputStream>();
	ArrayList<String> output = new ArrayList<String>();	
	ArrayList<String> cat = new ArrayList<String>();

	MESProvider mesProvider;
	TOBProvider tobProvider;
	
	
	
	public ProjectMain() {
		super();
	}

	public static String getOutputFileName() {
		return outputFileName;
	}

	public static void setOutputFileName(String outputFileName) {
		ProjectMain.outputFileName = outputFileName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getN() {
		return N;
	}

	public void setN(int n) {
		N = n;
	}

	public int getK() {
		return K;
	}

	public void setK(int k) {
		K = k;
	}

	public int getTobSendDelay() {
		return tobSendDelay;
	}

	public void setTobSendDelay(int tobSendDelay) {
		this.tobSendDelay = tobSendDelay;
	}

	public int getCSdelay() {
		return csdelay;
	}

	public void setCSdelay(int csdelay) {
		this.csdelay = csdelay;
	}
	
	public int getReqdelay() {
		return reqdelay;
	}

	public void setReqdelay(int reqdelay) {
		this.reqdelay = reqdelay;
	} 

	public int getC() {
		return c;
	}

	public void setC(int c) {
		this.c = c;
	}

	public long getClock() {
		return clock;
	}

	public void setClock(long clock) {
		this.clock = clock;
	}

	public String getConfigurationFileName() {
		return configurationFileName;
	}

	public void setConfigurationFileName(String configurationFileName) {
		this.configurationFileName = configurationFileName;
	}

	public ArrayList<Node> getNodes() {
		return nodes;
	}

	public void setNodes(ArrayList<Node> nodes) {
		this.nodes = nodes;
	}

	public HashMap<Integer, Node> getStore() {
		return store;
	}

	public void setStore(HashMap<Integer, Node> store) {
		this.store = store;
	}

	public HashMap<Integer, Socket> getChannels() {
		return channels;
	}

	public void setChannels(HashMap<Integer, Socket> channels) {
		this.channels = channels;
	}

	public HashMap<Integer, ObjectOutputStream> getoStream() {
		return oStream;
	}

	public void setoStream(HashMap<Integer, ObjectOutputStream> oStream) {
		this.oStream = oStream;
	}

	public ArrayList<String> getOutput() {
		return output;
	}

	public void setOutput(ArrayList<String> output) {
		this.output = output;
	}

	public ArrayList<String> getCat() {
		return cat;
	}

	public void setCat(ArrayList<String> cat) {
		this.cat = cat;
	}

	public MESProvider getMesProvider() {
		return mesProvider;
	}

	public void setMesProvider(MESProvider mesProvider) {
		this.mesProvider = mesProvider;
	}

	public TOBProvider getTobProvider() {
		return tobProvider;
	}

	public void setTobProvider(TOBProvider tobProvider) {
		this.tobProvider = tobProvider;
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		ProjectMain mainObj = Parser.readConfigFile("config.txt");

		Random r = new Random();
	        mainObj.csdelay = (int)(Math.log(r.nextInt())/((mainObj.c)));
	        //System.out.println(mainObj.csdelay);
	        mainObj.reqdelay = (int)(Math.log(r.nextInt())/((mainObj.tobSendDelay)));

		HashMap<Integer,Node> store = new HashMap<Integer,Node>();
		mainObj.id = Integer.parseInt(args[0]);
		int curNode = mainObj.id;
		mainObj.configurationFileName = "config.txt";
		ProjectMain.outputFileName = mainObj.configurationFileName.substring(0, mainObj.configurationFileName.lastIndexOf('.'));
		for(int i=0;i<mainObj.nodes.size();i++){
			mainObj.store.put(mainObj.nodes.get(i).nodeId, mainObj.nodes.get(i));
		}
		int serverPort = mainObj.nodes.get(mainObj.id).port;
		ServerSocket listener = new ServerSocket(serverPort);
		Thread.sleep(10000);
		for(int i=0;i<mainObj.N;i++){
			String hostName = mainObj.store.get(i).host;
			int port = mainObj.store.get(i).port;
			InetAddress address = InetAddress.getByName(hostName);
			Socket client = new Socket(address,port);
			//System.out.println("Attempting to connect to port:"+port);
			mainObj.channels.put(i, client);
			ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
			mainObj.oStream.put(i, oos);		
		}
		mainObj.mesProvider = new LamportProtocol(mainObj, mainObj.id, mainObj.N);
		mainObj.tobProvider = new ApplicationModule(mainObj.mesProvider, mainObj, mainObj.id, mainObj.N, mainObj.K);
		
		try {
		    	for(int i=0;i<mainObj.N;i++){
				Socket socket = listener.accept();
				new ClientThread(socket,mainObj).start();
				}
}
		finally {
			listener.close();
		}
		
		new ApplicationSendThread(mainObj).start();
		new ApplicationReceiveThread(mainObj).start();
	}
	
	public long send(Object message, int nodeId) {
		long sendTime = -1;
		synchronized(this){
			MessageContainer piggybackedMessage = new MessageContainer(message, this.clock, this.id);
			try{
			       	this.oStream.get(nodeId).writeObject(piggybackedMessage);
			    			} catch (IOException ioex){
				ioex.printStackTrace();
			}
			sendTime = this.clock++;
		}
		return sendTime;
	}
	
	public long broadcast(Object message, boolean includeSelf) {
		long sendTime = -1;
		synchronized(this){
			MessageContainer piggybackedMessage = new MessageContainer(message, this.clock, this.id);
			for(int i=0;i<this.N;i++){
				if(includeSelf || (i != this.id)){
					try{	
					    	this.oStream.get(i).writeObject(piggybackedMessage);
					} catch (IOException ioex){
						ioex.printStackTrace();
					}
				}
			}
			sendTime = this.clock++;
		}
		return sendTime;
	}
}

class ClientThread extends Thread {
	Socket cSocket;
	ProjectMain mainObj;

	public ClientThread(Socket csocket,ProjectMain mainObj) {
		this.cSocket = csocket;
		this.mainObj = mainObj;
	}

	public void run() {
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(cSocket.getInputStream());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		while(true){
			try {
				MessageContainer piggyBackedMessage = (MessageContainer)ois.readObject();
				if(piggyBackedMessage instanceof FinishMessageContainer){
					break;
				}
				long receiveTime = -1;
				synchronized(mainObj){
					mainObj.clock = Math.max(mainObj.clock, piggyBackedMessage.clock) + 1;
					receiveTime = mainObj.clock;
					String rectime = "Time elapsed " + Long.toString(receiveTime);
					mainObj.output.add(rectime);


				}
				mainObj.mesProvider.process(piggyBackedMessage.message, piggyBackedMessage.senderId, piggyBackedMessage.clock, receiveTime);
				mainObj.tobProvider.process(piggyBackedMessage.message, piggyBackedMessage.senderId, piggyBackedMessage.clock, mainObj.clock);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} 
		}
	}
}

class ApplicationSendThread extends Thread{
	ProjectMain mainObj;
	
	public ApplicationSendThread(ProjectMain mainObj) {
		this.mainObj = mainObj;
	}
	
	public void run() {
		for(int i=0;i<mainObj.K;i++){
			try{
				Thread.sleep(mainObj.tobSendDelay);
			} catch (InterruptedException iex){
				iex.printStackTrace();
			}
			mainObj.tobProvider.tobSend("Message");
			System.out.println("Sent message"+ i);
		}
	}
}

class ApplicationReceiveThread extends Thread{
	ProjectMain mainObj;
	
	ApplicationReceiveThread(ProjectMain mainObj){
		this.mainObj = mainObj;
	}
	
	public void run() {
		for(int i=0;i<mainObj.K * mainObj.N;i++){
			String messageReceived = mainObj.tobProvider.tobReceive();
		}
		synchronized(mainObj){
			for(int i=0;i<mainObj.N;i++){
				try{
					mainObj.oStream.get(i).writeObject(new FinishMessageContainer());
					mainObj.oStream.get(i).close();
					mainObj.channels.get(i).close();
				} catch (IOException ioex){
					;
				}
			}
				new OutputWriter(mainObj).writeToFile();
			System.exit(0);
		}
	}
}

class OutputWriter {
	ProjectMain mainObj;

	public OutputWriter(ProjectMain mainObj) {
		this.mainObj = mainObj;
	}


	public void writeToFile() {
		String fileName = ProjectMain.outputFileName+"-"+mainObj.id+".out";
		String outname = "CS_time.out";
		synchronized(mainObj.output){
			try {
				File file = new File(fileName);
				FileWriter fileWriter;
				if(file.exists()){
					fileWriter = new FileWriter(file,true);
				}
				else
				{
					fileWriter = new FileWriter(file);
				}
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
				for(int i=0;i<mainObj.output.size();i++){
					bufferedWriter.write(mainObj.output.get(i) + "\n");
				}			
				mainObj.output.clear();
				bufferedWriter.close();
			}
			catch(IOException ex) {
				System.out.println("Error writing to file '" + fileName + "'");
			}
		}
		synchronized(mainObj.cat){
			try {
				File files = new File(outname);
				FileWriter fileWrite;
				if(files.exists()){
					fileWrite = new FileWriter(files,true);
				}
				else
				{
					fileWrite = new FileWriter(files);
				}
				BufferedWriter buffer = new BufferedWriter(fileWrite);
				for(int i=0;i<mainObj.cat.size();i++){
					buffer.write(mainObj.cat.get(i) + "\n");
				}			
				mainObj.cat.clear();
				buffer.close();
			}
			catch(IOException ex) {
				System.out.println("Error writing to file '" + fileName + "'");
			}
		}
	}
}
