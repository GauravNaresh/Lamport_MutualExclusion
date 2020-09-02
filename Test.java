import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Test {
	ArrayList<Integer> x = new ArrayList<Integer>();
	ArrayList<Integer> enter = new ArrayList<Integer>();
	ArrayList<Integer> exit = new ArrayList<Integer>();
	ArrayList<Boolean> status = new ArrayList<Boolean>();
	boolean check = true;

    public static void main(String[] args) {
	Test system = new Test();
	try (FileReader reader = new FileReader("CS_time.out");
	     BufferedReader br = new BufferedReader(reader)) {
    
		String line;
		while ((line =br.readLine()) != null) {
		    String[] input = line.split("\\s+");
			system.x.add(Integer.parseInt(input[4]));
		}
	    } 
		catch (IOException e) {
	    System.err.format("IOException %s%n", e);
	}
 	int count = 0;
	int count1 = 1;
	while(count<system.x.size()) {
		int tran = system.x.get(count);
		int tran1 = system.x.get(count1);
	    system.enter.add(tran);
		system.exit.add(tran1);
  		count = count +2;
		count1 = count1+2;
	}
	for (int i=0;i<	system.enter.size();i++) {
	int m = system.enter.get(i);
for (int j=i+1;j<system.enter.size();j++) {
	int n_enter = system.enter.get(j);
	int n_exit = system.exit.get(j);
	if (m<n_enter || m>n_exit) {
system.status.add(true);
}
	else {
	    system.status.add(false);
	}
}
}

for(int i=0;i<system.status.size();i++) {
system.check = (system.status.get(i))&&(system.check);
}
if (system.check == true) {
System.out.println("CRITICAL SECTIONS DON'T OVERLAP. PROGRAM IS CORRECT");
}
else {
System.out.println("OVERLAPPPING CRITICAL SECTIONS");
}
}
    }

