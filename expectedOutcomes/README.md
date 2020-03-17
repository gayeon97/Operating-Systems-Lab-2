The Scheduling.java program reads its input from a file, whose name is given as a command line argument. The program send its output to the screen as System.out in Java.

The file describes n processes (i.e., n quadruples of numbers) and then simulate the n processes until they all terminate. Here, we do this by keeping track of the state of each process (e.g., ready, running, blocked) and advance time, making any state transitions needed.

At the end of the run, we first print an identification of the run including the scheduling algorithm used, any parameters (e.g. the quantum for RR), and the number of processes simulated.
You then print for each process
* A, B, C, and M
* Finishing time.
* Turnaround time (i.e., finishing time - A).
* I/O time (i.e., time in Blocked state).
* Waiting time (i.e., time in Ready state).


You then print the following summary data.
* Finishing time (i.e., when all the processes have finished).
* CPU Utilization (i.e., percentage of time some job is running).
* I/O Utilization (i.e., percentage of time some job is blocked).
* Throughput, expressed in processes completed per hundred time units.
* Average turnaround time.
* Average waiting time.

***
The outputs differ depending on the type of the algorithm used.

There are 4 algorithms used:
* First Come First Serve (FCFS)
* Round Robin (RR)
* Last Come First Serve (LCFS)
* Highest Penalty Ratio Next (HPRN)



There are 7 inputs: input-1 to input-7.\
Each of these inputs will have four different outputs for each of the algorithms.\
There are 3 ways to display an output:
* Regular version that displays the following:
  * The original list
  * The sorted list by the arrival time
  * The name of the algorithm used
  * Each process information
  * Summary Data
* Verbose version
  * same as regular version but
  * displays additional information on the state and remaining burst for each process
* Show-random version
  * same as verbose version but
  * displays additional information on the random number chosen for calculating the CPU burst when choosing a ready process to run
  

