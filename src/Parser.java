import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Parser {

	public static ProjectMain readConfigFile(String name) throws IOException{
		ProjectMain mySystem = new ProjectMain();
		int count = 0,flag = 0;
		int curNode = 0;
		String curDir = System.getProperty("user.dir");
		String fileName = curDir+"/"+name;
		String line = null;
		try {
			FileReader fileReader = new FileReader(fileName);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			while((line = bufferedReader.readLine()) != null) {
				if(line.length() == 0)
					continue;
				if(!line.startsWith("#")){
					if(line.contains("#")){
						String[] input = line.split("#.*$");
						String[] input1 = input[0].split("\\s+");
						if(flag == 0 && input1.length == 4){
							mySystem.N = Integer.parseInt(input1[0]);
							mySystem.K = Integer.parseInt(input1[3]);
							mySystem.tobSendDelay = Integer.parseInt(input1[1]);
							mySystem.c = Integer.parseInt(input1[2]);
							flag++;
						}
						else if(flag == 1 && count < mySystem.N)
						{							
							mySystem.nodes.add(new Node(Integer.parseInt(input1[0]),input1[1],Integer.parseInt(input1[2])));
							count++;
							if(count == mySystem.N){
								flag = 2;
							}
						}
					}
					else {
						String[] input = line.split("\\s+");
						if(flag == 0 && input.length == 4){
							mySystem.N = Integer.parseInt(input[0]);
							mySystem.K = Integer.parseInt(input[3]);
							mySystem.tobSendDelay = Integer.parseInt(input[1]);
							mySystem.c = Integer.parseInt(input[2]);
							flag++;
						}
						else if(flag == 1 && count < mySystem.N)
						{
							mySystem.nodes.add(new Node(Integer.parseInt(input[0]),input[1],Integer.parseInt(input[2])));
							count++;
							if(count == mySystem.N){
								flag = 2;
							}
						}
					}
				}
			}
			bufferedReader.close();  
		}
		catch(FileNotFoundException ex) {
			System.out.println("Unable to open file '" +fileName + "'");                
		}
		catch(IOException ex) {
			System.out.println("Error reading file '" + fileName + "'");                  
		}
		return mySystem;
	}
}

