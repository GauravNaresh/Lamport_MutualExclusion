AOS Project 2 - Implementation of Lamport Mutual Exclusion Protocol
Members: Manoj Vijay (MXV180000) Gaurav Naresh (GXN180004)

The project consists of the following files:
launcher.sh, cleanup1.sh
Config.txt
Test.java - Testing script used to verify the generated output file (CS_time.out)
Report.pdf- Report which contains the graphs for Message Complexity, Response time after several runs.
bin - contains the class files
src - contains the required source files

INSTRUCTIONS:
Unzip the folder in a suitable working directory
copy the config.txt file and paste it in the home directory
Log in to dc01 through terminal and cd to the directory where the other files are present 
Run launcher.sh using the command - ./launcher.sh
Class files are already present in the bin directory. In case you need to compile, first compile Message.java, followed by ProjectMain.java. And copy the generated files to bin folder.
Now, make necessary changes to the launcher.sh script and run the file.
After completion, Run cleanup1.sh using the command - ./cleanup1.sh

Output files will be generated in the home directory, named - config-X.out (where X is the node Id). These contain the timestamps of Induvidual processes and marks when they've entered and left the critical section.
Additionally, The MAIN OUTPUT FILE is CS_time.out, which contains the information regarding when each process has entered and exited the critical section. This file is used to test the correctness of the program.

TESTING:
Copy the CS_time.out file from the home directory and paste it in the project directory.
Compile and Run the Test.java file. This checks for critical section overlapping.
This proves that the implementation is correct.

The report contains the Experimental Evaluation which is based on several trials. 



