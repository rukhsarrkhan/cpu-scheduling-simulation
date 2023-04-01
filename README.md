# This project includes:
1. The Simulator.java implements the Simulation of the three algorithms - FCFS, SJF and RR. This also implements the CPU
2. The ProcessControlBlock.java implements the class function for the Process Control Block
3. The Process.java implements the class function for the Processes
4. The States.java stores the states of CPU and I/O

An Output file will be generated that displays the process state, burst time and remaining time at intervals, until all the processes have been processed. 
# Once all processes are finished it gives below values
1. The CPU Utilization
2. The Throughput
3. The Average Turnaround time 
4. The Average Waiting time

# Steps to run the simulation:
1. While you are in the /simulator directory, execute the command:  javac -d ./build/ \
2. Then provide this input:  ./main/*.java
Example is 
rkhan@Rukhsars-MacBook-Air scheduling-programming-project % javac -d ./build/ \                
> ./main/*.java
3. This will create a bin/main directory with all the class files in it
4. Now move into the bin directory by command: cd build
5. Here, you can run main.Simulator to run the simulation for the three algorithms
6. The output file will be created in the bin directory 
7. You may also run the overall command in the Simulator directory
rm -r ./build/ ; javac main/*.java -d build ; cd build ; java main.Simulator > output.txt ; cd ..