import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
public class rrScheduling {
	
	public static int globalIndex = -1;
	public static String verbose = "";
	public static int quantum = 0;
	
	static int randomOS(int u) throws FileNotFoundException {
		List<String> fileStream = null;
		try {
			fileStream = Files.readAllLines(Paths.get(System.getProperty("user.dir") + "/random-numbers"),Charset.forName("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int currRandInt = Integer.parseInt(fileStream.get(globalIndex));
		if (verbose.contains("--show-random")) {
			System.out.println("Find burst when choosing ready process to run: " + currRandInt);
		}
		return 1 + (currRandInt % u);
	}
	
	static void printDebugInfo(int cycN, ArrayList<Process> debugList) {
		String debug = String.format("Before cycle%5d:", cycN);
		for (Process a: debugList) {
			String currState = a.getState();
			
			if (currState.equals("unstarted") || currState.equals("ready") || currState.equals("terminated")) {
				debug += String.format("%12s%3d", currState, 0);
			} else if (currState.equals("running")) {
				debug += String.format("%12s%3d", currState, a.getRemainingCPUBurst());
			} 
			else if (currState.equals("blocked")) {
				debug += String.format("%12s%3d", currState, a.getRemainingIOBurst());
			} 
		}
		System.out.println(debug + ".");		
	}
	
	static void executeBlockedP(Queue<Process> bQ) {
		for (Process b: bQ) {
			b.decrRemainingIOBurst();
			//System.out.printf("Process (%d %d %d %d) has remaining IO burst of: %d\n",b.getA(),b.getB(),b.getC(),b.getM(),b.getRemainingIOBurst());
		}
	}
	
	static void executeRunningP(Queue<Process> rngQ) {
		for (Process rng: rngQ) {
			rng.decrRemainingCPUBurst();
			
			if (quantum != 0) {
				rng.decrCPUBurstSET();
				rng.increNumRun();
			}
		}
	}
	
	static void blockedToReady(Queue<Process> bQ, Queue<Process> readyAtSameCycQ, ArrayList<Process> fcfsL, int cycNum) {
		for (int i = 0; i < bQ.size(); i ++) {
			Process b = bQ.poll(); //first pop from the blocked queue
			
			if (b.getRemainingIOBurst() == 0) { //if there's no remaining I/O Burst left
				b.setIOBurst(0); //now IO Burst is set to 0 because it is done with 
				b.setState("ready"); //change the state
				b.setCycleTimeWhenReady(cycNum); //save the cycle time when the process state went to being "ready"
				readyAtSameCycQ.add(b); //add it to the readyQ
				i --;
			} else {
				bQ.add(b); //the process isn't finished with blocking so push back into blocked queue
			}
			fcfsL.set(b.getIndex(), b); //update corresponding fcfsDebug Process info
		}
	}
	
	static void sortQueue(Queue<Process> readyAtSameCycleQ) {
		List<Process> tempRQ = new ArrayList<>();
		tempRQ.add(readyAtSameCycleQ.poll());//add the very first process in the readyQ into the tempRQ
		boolean doSort = false;
		for (int i = 0; i < readyAtSameCycleQ.size(); i++) {
			Process pInReady = readyAtSameCycleQ.poll(); //pop from the readyQ 
			if (pInReady.getCycleTimeWhenReady() == tempRQ.get(i).getCycleTimeWhenReady()) {
				doSort = true;
			}
			tempRQ.add(pInReady); //push the popped queue, "pInReady" into the tempRQ
			i --;		
		}
		
		if (doSort) {
			Collections.sort(tempRQ, new Comparator<Process>() {
				@Override
				public int compare(Process o1, Process o2) {
					//return o1.compareTo(o2);
					
					if (o1.getA() == o2.getA()) {
						if (o1.getIndex() > o2.getIndex()) {
							return 1;
						} else if (o1.getIndex() < o2.getIndex()) {
							return -1;
						} else {
							return 0;
						}
						
						//return 0;
					} else if (o1.getA() > o2.getA()) {
						return 1;
					} else {
						return -1;
					}	
				}		
			});	
		}
		
		for (Process newRP: tempRQ) {
			readyAtSameCycleQ.add(newRP);
		}
	}
	
	static void unstartedToReady(ArrayList<Process> debugQ, Queue<Process> readyAtSameCycQ, int cycNum) {
		//UNSTARTED TO READY: check the original list to see if there's any unstarted process now ready
		for (int i = 0; i < debugQ.size(); i ++) {
			Process p = debugQ.get(i);
			if (p.getState().equals("unstarted") && p.getA() == cycNum) {
				p.setState("ready"); //set the state of Process inside of the fcfsDebug arraylist
				p.setCycleTimeWhenReady(cycNum); //save the cycle time when the process state went to being "ready"
				readyAtSameCycQ.add(p); //push that updated process into readyAtSameCycleQ
			}
		}
	}	
	
	static void addToReadyQ(Queue<Process> readyAtSameCycQ, Queue<Process> rdyQ) {
		//sort the readyAtSameCycleQ
		while (readyAtSameCycQ.size() > 0) {			
			Process p = readyAtSameCycQ.poll();
			if (p!= null) {
				//System.out.printf("Process (%d %d %d %d) is added to readyQ\n", p.getA(),p.getB(),p.getC(),p.getM());
				rdyQ.add(p);
			}		
		}
	}
	
	static void updateWaitingTime(Queue<Process> rdyQ) {
		//update the waiting time for any process in the readyQ
		for (int i = 0; i < rdyQ.size(); i ++) {
			Process rdy = rdyQ.poll(); //pop from the start of the readyQ
			rdy.increTimeInReadyState(); //update the process' time in ready state
			rdyQ.add(rdy); //add back the updated ready process back into the readyQ
		}
	}
	
	static void printSummaryData(double overalFT, double cpuUsedT, double ioUsedT, double throughPut, double totalTurnT, double totalWT, int numPs) {
		String processResult = "Summary Data:\n";
		processResult += String.format("\tFinishing time: %d\n", (int) overalFT);
		processResult += String.format("\tCPU Utilization: %f\n", cpuUsedT/overalFT);
		processResult += String.format("\tI/O Utilization: %f\n", ioUsedT/overalFT);
		processResult += String.format("\tThroughput: %f processes per hundred cycles\n", throughPut);
		processResult += String.format("\tAverage turnaround time: %f\n", totalTurnT/numPs);
		processResult += String.format("\tAverage waiting time: %f\n", totalWT/numPs);
		
		System.out.println(processResult);
	}

	public static void main(String[] args) throws FileNotFoundException {		
		
		File inputFile = null;
		
		//System.out.println("this is args.length: " + args.length);
		
		String url = System.getProperty("user.dir") + "/";		
		//System.out.println(url);
				
		//read input file
		if (0 < args.length) {
			if (args.length == 2) {
				//System.out.println("This is args[1]: " + args[1]);
				url += args[1];
				verbose = args[0];
				//System.out.println(url);
				inputFile = new File(url);
			} else if (args.length == 1){
				//System.out.println("This is args[0]: " + args[0]);
				url += args[0];
				//System.out.println(url);
				inputFile = new File(url);
			}
		} else {
			System.err.println("Invalid number of command line arguments: " + args.length);
			System.exit(0);
		}
				
		//create a list of processes
		List<Process> list = new ArrayList<>(); //what used to be tempList

		//scan the input file
		Scanner sc = new Scanner(inputFile);
		sc.useDelimiter("[\\s\\(\\),]+");
		int numProcesses = 0;
		while (sc.hasNextInt()) {
			//this is the number of processes
			numProcesses = sc.nextInt();
			//System.out.println("This is the number of processes: " + numProcesses);
			
			//for n number of processes, save each process info (quadruple of numbers)
			for (int i = 0; i < numProcesses; i ++) {
				int aInput = 0;
				int bInput = 0;
				int cInput = 0;
				int mInput = 0;
				
				for (int j = 0; j < 4; j ++){
					int next = sc.nextInt();
					if (j == 0) {
						aInput = next;
					} else if (j == 1) {
						bInput = next;
					} else if (j == 2) {
						cInput = next;
					} else {
						mInput = next;
					}
				}
				//create a process based on the info above
				Process curr = new Process(aInput, bInput, cInput, mInput,i);
				//add the process to the list in the order received
				list.add(curr);
			}	
		}			
		sc.close();	
		System.out.println();
		
		//print out the ORIGINAL INPUT before sorting by arrival time
		String originalInput = String.format("The original input was: %d", numProcesses);
		for (Process e: list) {
			originalInput += String.format(" (%d %d %d %d)",e.getA(),e.getB(),e.getC(),e.getM());
		}
		//System.out.printf("%s \n",originalInput);
		
		//if the list size is greater than 1, sort the list by the arrival time
		if (list.size() > 1) {
			Collections.sort(list, new Comparator<Process>() {
				@Override
				public int compare(Process o1, Process o2) {
					return o1.compareTo(o2);
				}		
			});	
		}	
		
		//create a queue of processes sorted by the arrival time
		Queue<Process> sortedQueue = new LinkedList<>();
		
		//create 4 queue of processes for each of the four scheduling alg
		//Queue<Process> queueForFCFS = new LinkedList<>();
		//HashMap<Integer, Process> queueForFCFSDebug = new HashMap<Integer, Process>();	
		ArrayList<Process> fcfsDebug = new ArrayList<Process>();		
		ArrayList<Process> rrDebug = new ArrayList<Process>();
		ArrayList<Process> lcfsDebug = new ArrayList<Process>();
		ArrayList<Process> hprnDebug = new ArrayList<Process>();
				
		//add the Processes from the sorted list to sortedList, listForFCFS, listForRR, listForLCFS & istForHPRN
		for (int z = 0; z < list.size(); z ++) {
			Process current = list.get(z);
			current.setIndex(z);
			
			sortedQueue.add(current);
			fcfsDebug.add(current);			
			rrDebug.add(current);
			lcfsDebug.add(current);
			hprnDebug.add(current);
		}
		
		//print out the SORTED INPUT by the arrival time
		String sortedInput = String.format("The (sorted) input was: %d", numProcesses);
		for (Process ee: sortedQueue) {
			sortedInput += String.format(" (%d %d %d %d)",ee.getA(),ee.getB(),ee.getC(),ee.getM());
		}	
		//System.out.printf("%s \n",sortedInput);
		
		
		/**
		 * THESE ARE UNIVERSAL VARIABLES USED FOR ALL 4 ALGORITHMS
		 * MAKE SURE TO REST THEM TO THES VALUES AT THE BEGINNING OF EVERY ALGORITHM
		 */
		globalIndex = 0; //set globalIndex that will be used to calculate randomOS(B) to 0
		int numPTerminated = 0; //set number of processes terminated to 0
		int cycleNum = 0; //set cycle number to 0
		int cpuUsedTime = 0; //for calculating CPU utilization
		int ioUsedTime = 0; //for calculating I/O utilization
		double overallFinishTime = 0.0;
		double totalTurnaroundT = 0.0;
		double totalWaitingT = 0.0;
		double throughput = 0.0;
		String descriptionForDebuging = "This detailed printout gives the state and remaining burst for each process\n";
		
		//ROUND ROBIN
		System.out.printf("%s \n",originalInput);
		//System.out.printf("%s \n",sortedInput);
		
		String ss = String.format("The (sorted) input was: %d", numProcesses);
		for (Process ee: rrDebug) {
			ss += String.format(" (%d %d %d %d)",ee.getA(),ee.getB(),ee.getC(),ee.getM());
		}	
		System.out.printf("%s \n",ss);
		
		quantum = 2;
		
		/**
		 * THESE ARE UNIVERSAL VARIABLES USED FOR ALL 4 ALGORITHMS
		 * MAKE SURE TO REST THEM TO THES VALUES AT THE BEGINNING OF EVERY ALGORITHM
		 */
		globalIndex = 0; //set globalIndex that will be used to calculate randomOS(B) to 0
		numPTerminated = 0; //set number of processes terminated to 0
		cycleNum = 0; //set cycle number to 0
		cpuUsedTime = 0; //for calculating CPU utilization
		ioUsedTime = 0; //for calculating I/O utilization
		overallFinishTime = 0.0;
		totalTurnaroundT = 0.0;
		totalWaitingT = 0.0;
		throughput = 0.0;
		descriptionForDebuging = "This detailed printout gives the state and remaining burst for each process\n";
		
		Queue<Process> readyRRQ = new LinkedList<>();
		Queue<Process> runningRRQ = new LinkedList<>();
		Queue<Process> blockedRRQ = new LinkedList<>();
		Queue<Process> readyAtSameCycleRRQ = new LinkedList<>();
		
		if (verbose.length() > 0)
			System.out.println(descriptionForDebuging);
		
		while (numPTerminated < numProcesses) {
			//print debug info
			if (verbose.length() > 0) {
				printDebugInfo(cycleNum, rrDebug);
			}
			
			//DO BLOCKS: execute blocked Processes
			executeBlockedP(blockedRRQ); //System.out.println("executed blocked Processes");
			
			//DO RUNNINGS: execute running Process
			executeRunningP(runningRRQ); //System.out.println("executed running Processes");
									
			//BLOCKED TO READY: change any blocked Process to ready Process
			blockedToReady(blockedRRQ, readyAtSameCycleRRQ, rrDebug, cycleNum); //System.out.println("after blocked-to-ready, readyAtSameCycleQ size is: " + readyAtSameCycleQ.size());
			
			//RUNNING TO BLOCKED: change any running Process to blocked Process
			for (int i = 0; i < runningRRQ.size(); i ++) {
				Process r = runningRRQ.poll(); //first pop from the runningQ
				
				if (r.getTotalCPUTimeLeft() == 0 ) { // process should be terminated
					r.setState("terminated"); //change the state
					r.setFinishTime(cycleNum); //calculate the finish time which is equal to the current cycle number
					r.setTurnAroundTime(cycleNum, r.getA()); //calculate the turnaround time: finish time - A value
					numPTerminated ++; //increase the number of processes terminated
				}	

				else if (r.getRemainingCPUBurst() == 0) { //process has no more remaining cpu Burst
					int cpuBurstRemaining = r.getCPUBurst();
					if (cpuBurstRemaining > 0) {
						if (cpuBurstRemaining > 1) {
							r.setRemainingCPUBurst(2);
						} else {
							r.setRemainingCPUBurst(1);
						}	
						r.setState("ready");
						readyAtSameCycleRRQ.add(r);						
					} else {
						r.setState("blocked"); //change the state			
						r.setCPUBurst(0); //now CPU Burst is set to 0 because it is done with 	
						blockedRRQ.add(r); //the process still has CPU time left so we add to the blockedQ
						//System.out.printf("running-to-ready: (%d %d %d %d) at index %d has io burst PRODUCED: %d\n", r.getA(),r.getB(),r.getC(),r.getM(), r.getIndex(), r.getIndex(), r.getIOBurst());
					}							
				} 
				else { //process HAS remaining cpu Burst
					runningRRQ.add(r); //add the popped process back into the runningQ
				}			
				rrDebug.set(r.getIndex(), r); //update corresponding fcfsDebug Process info
			}
			
			//UNSTARTED TO READY: check the original list to see if there's any unstarted process now ready
			unstartedToReady(rrDebug, readyAtSameCycleRRQ, cycleNum);	//System.out.println("after unstarted-toready, readyAtSameCycleQ size is: " + readyAtSameCycleQ.size());
			
			//SORT THE readyAtSameCycleQ && ADD TO THE readyQ
			sortQueue(readyAtSameCycleRRQ);	//sort the readyAtSameCycleQ
			addToReadyQ(readyAtSameCycleRRQ, readyRRQ); //add to the readyQ
					
			//POPULATING RUNNINGQ: 
			//if runningQ is empty && readyQ is NOT empty,
			//pop the 1st process from the readyQ and push it to runningQ
			if ( runningRRQ.isEmpty() && !readyRRQ.isEmpty()) {			
				Process poppedP = readyRRQ.poll(); //pop the 1st process from the readyQ
				
				if (poppedP.getCPUBurst() == 0) { //NEED to calculate CPU Burst
					int cpuB = randomOS(poppedP.getB()); //calculate the new cpuBurst
					globalIndex ++; //increase the globalIndex for correct index when calculating cpuBurst later
					poppedP.setCPUBurst(cpuB); //set the cpu Burst time to newly calculated cpuBurst		
					poppedP.setRemainingCPUBurst(cpuB <= 2 ? cpuB : 2); //set the remaining cpuBurst time to newly calculated cpuBurst
					
					//calculate IO Burst
					int ioB = cpuB * poppedP.getM();
					poppedP.setIOBurst(ioB);
					poppedP.setRemainingIOBurst(ioB);
					//System.out.printf("running-to-ready: (%d %d %d %d) at index %d has pre-made io burst: %d\n", poppedP.getA(),poppedP.getB(),poppedP.getC(),poppedP.getM(), poppedP.getIndex(), poppedP.getIOBurst());
				} //else, if poppedP.getCPUBurst() > 0, don't need to calculate CPU Burst		
				poppedP.setState("running"); //change the state
				runningRRQ.add(poppedP); //add the updated Process (that has been popped from the readyQ) to runningQ
				rrDebug.set(poppedP.getIndex(), poppedP); //update corresponding fcfsDebug Process info
			}
			
			//update the waiting time for any process in the readyQ
			updateWaitingTime(readyRRQ);
			
			//update CPU Utilization time
			if (runningRRQ.size() > 0) {
				cpuUsedTime ++;
			}
			
			//update I/O Utilization time
			if (blockedRRQ.size() > 0) {
				ioUsedTime ++;
			}
			
			cycleNum ++;				
			
		} //end of while loop
		
		overallFinishTime = cycleNum - 1;
				
		//print out each process info after finishing the RR algorithm
		System.out.println("The scheduling algorithm used was Round Robin\n");
		for (Process ee: rrDebug) {
			ee.printProcessInfo();
			totalTurnaroundT += ee.getTurnAroundTime();
			totalWaitingT += ee.getTimeInReadyState();
		}
		
		throughput = (100*numProcesses)/overallFinishTime;
		printSummaryData(overallFinishTime, cpuUsedTime, ioUsedTime, throughput, totalTurnaroundT, totalWaitingT, numProcesses);
		
		
		
		
//		//ROUND ROBIN
//		System.out.printf("%s \n",originalInput);
//		//System.out.printf("%s \n",sortedInput);
//		
//		String ss = String.format("The (sorted) input was: %d", numProcesses);
//		for (Process ee: rrDebug) {
//			ss += String.format(" (%d %d %d %d)",ee.getA(),ee.getB(),ee.getC(),ee.getM());
//		}	
//		System.out.printf("%s \n",ss);
//		
//		quantum = 2;
//		
//		/**
//		 * THESE ARE UNIVERSAL VARIABLES USED FOR ALL 4 ALGORITHMS
//		 * MAKE SURE TO REST THEM TO THES VALUES AT THE BEGINNING OF EVERY ALGORITHM
//		 */
//		globalIndex = 0; //set globalIndex that will be used to calculate randomOS(B) to 0
//		numPTerminated = 0; //set number of processes terminated to 0
//		cycleNum = 0; //set cycle number to 0
//		cpuUsedTime = 0; //for calculating CPU utilization
//		ioUsedTime = 0; //for calculating I/O utilization
//		overallFinishTime = 0.0;
//		totalTurnaroundT = 0.0;
//		totalWaitingT = 0.0;
//		throughput = 0.0;
//		descriptionForDebuging = "This detailed printout gives the state and remaining burst for each process\n";
//		
//		Queue<Process> readyRRQ = new LinkedList<>();
//		Queue<Process> runningRRQ = new LinkedList<>();
//		Queue<Process> blockedRRQ = new LinkedList<>();
//		Queue<Process> readyAtSameCycleRRQ = new LinkedList<>();
//		
//		if (verbose.length() > 0)
//			System.out.println(descriptionForDebuging);
//		
//		while (numPTerminated < numProcesses) {
//			//print debug info
//			if (verbose.length() > 0) {
//				printDebugInfo(cycleNum, rrDebug);
//			}
//			
//			//DO BLOCKS: execute blocked Processes
//			executeBlockedP(blockedRRQ); //System.out.println("executed blocked Processes");
//			
//			//DO RUNNINGS: execute running Process
//			executeRunningP(runningRRQ); //System.out.println("executed running Processes");
//									
//			//BLOCKED TO READY: change any blocked Process to ready Process
//			blockedToReady(blockedRRQ, readyAtSameCycleRRQ, rrDebug, cycleNum); //System.out.println("after blocked-to-ready, readyAtSameCycleQ size is: " + readyAtSameCycleQ.size());
//			
//			//RUNNING TO BLOCKED: change any running Process to blocked Process
//			for (int i = 0; i < runningRRQ.size(); i ++) {
//				Process r = runningRRQ.poll(); //first pop from the runningQ
//				
//				if (r.getTotalCPUTimeLeft() == 0 ) { // process should be terminated
//					r.setState("terminated"); //change the state
//					r.setFinishTime(cycleNum); //calculate the finish time which is equal to the current cycle number
//					r.setTurnAroundTime(cycleNum, r.getA()); //calculate the turnaround time: finish time - A value
//					numPTerminated ++; //increase the number of processes terminated
//				}	
//
//				else if (r.getRemainingCPUBurst() == 0) { //process has no more remaining cpu Burst
//					int cpuBurstRemaining = r.getCPUBurst();
//					if (cpuBurstRemaining > 0) {
//						if (cpuBurstRemaining > 1) {
//							r.setRemainingCPUBurst(2);
//						} else {
//							r.setRemainingCPUBurst(1);
//						}	
//						r.setState("ready");
//						readyAtSameCycleRRQ.add(r);						
//					} else {
//						r.setState("blocked"); //change the state			
//						r.setCPUBurst(0); //now CPU Burst is set to 0 because it is done with 	
//						blockedRRQ.add(r); //the process still has CPU time left so we add to the blockedQ
//						//System.out.printf("running-to-ready: (%d %d %d %d) at index %d has io burst PRODUCED: %d\n", r.getA(),r.getB(),r.getC(),r.getM(), r.getIndex(), r.getIndex(), r.getIOBurst());
//					}							
//				} 
//				else { //process HAS remaining cpu Burst
//					runningRRQ.add(r); //add the popped process back into the runningQ
//				}
//				
//				rrDebug.set(r.getIndex(), r); //update corresponding fcfsDebug Process info
//			}
//			
//			//UNSTARTED TO READY: check the original list to see if there's any unstarted process now ready
//			for (int i = 0; i < rrDebug.size(); i ++) {
//				Process p = rrDebug.get(i);
//				if (p.getState().equals("unstarted") && p.getA() == cycleNum) {
//					p.setState("ready"); //set the state of Process inside of the fcfsDebug arraylist
//					p.setCycleTimeWhenReady(cycleNum); //save the cycle time when the process state went to being "ready"
//					readyAtSameCycleRRQ.add(p); //push that updated process into readyAtSameCycleQ
//				}
//			}
//			//System.out.println("after unstarted-toready, readyAtSameCycleQ size is: " + readyAtSameCycleQ.size());
//			
//			//SORT THE readyAtSameCycleQ && ADD TO THE readyQ
//			sortQueue(readyAtSameCycleRRQ);	//sort the readyAtSameCycleQ
//			while (readyAtSameCycleRRQ.size() > 0) {			
//				Process p = readyAtSameCycleRRQ.poll();
//				if (p!= null) {
//					//System.out.printf("Process (%d %d %d %d) at index %d with cpuB time of %d is added to readyQ\n", p.getA(),p.getB(),p.getC(),p.getM(), p.getIndex(),p.getCPUBurst());
//					readyRRQ.add(p);
//				}		
//			}
//					
//			//POPULATING RUNNINGQ: 
//			//if runningQ is empty && readyQ is NOT empty,
//			//pop the 1st process from the readyQ and push it to runningQ
//			if ( runningRRQ.isEmpty() && !readyRRQ.isEmpty()) {			
//				Process poppedP = readyRRQ.poll(); //pop the 1st process from the readyQ
//				
//				if (poppedP.getCPUBurst() == 0) { //NEED to calculate CPU Burst
//					int cpuB = randomOS(poppedP.getB()); //calculate the new cpuBurst
//					globalIndex ++; //increase the globalIndex for correct index when calculating cpuBurst later
//					poppedP.setCPUBurst(cpuB); //set the cpu Burst time to newly calculated cpuBurst		
//					poppedP.setRemainingCPUBurst(cpuB <= 2 ? cpuB : 2); //set the remaining cpuBurst time to newly calculated cpuBurst
//					
//					//calculate IO Burst
//					int ioB = cpuB * poppedP.getM();
//					poppedP.setIOBurst(ioB);
//					poppedP.setRemainingIOBurst(ioB);
//					//System.out.printf("running-to-ready: (%d %d %d %d) at index %d has pre-made io burst: %d\n", poppedP.getA(),poppedP.getB(),poppedP.getC(),poppedP.getM(), poppedP.getIndex(), poppedP.getIOBurst());
//				} //else, if poppedP.getCPUBurst() > 0, don't need to calculate CPU Burst		
//				poppedP.setState("running"); //change the state
//				runningRRQ.add(poppedP); //add the updated Process (that has been popped from the readyQ) to runningQ
//				rrDebug.set(poppedP.getIndex(), poppedP); //update corresponding fcfsDebug Process info
//			}
//			
//			//update the waiting time for any process in the readyQ
//			for (int i = 0; i < readyRRQ.size(); i ++) {
//				Process rdy = readyRRQ.poll(); //pop from the start of the readyQ
//				rdy.increTimeInReadyState(); //update the process' time in ready state
//				readyRRQ.add(rdy); //add back the updated ready process back into the readyQ
//			}
//			
//			//update CPU Utilization time
//			if (runningRRQ.size() > 0) {
//				cpuUsedTime ++;
//			}
//			
//			//update I/O Utilization time
//			if (blockedRRQ.size() > 0) {
//				ioUsedTime ++;
//			}
//			
//			cycleNum ++;				
//			
//		} //end of while loop
//		
//		overallFinishTime = cycleNum - 1;
//				
//		//print out each process info after finishing the RR algorithm
//		System.out.println("The scheduling algorithm used was Round Robin\n");
//		for (Process ee: rrDebug) {
//			ee.printProcessInfo();
//			totalTurnaroundT += ee.getTurnAroundTime();
//			totalWaitingT += ee.getTimeInReadyState();
//		}
//		
//		throughput = (100*numProcesses)/overallFinishTime;
//		printSummaryData(overallFinishTime, cpuUsedTime, ioUsedTime, throughput, totalTurnaroundT, totalWaitingT, numProcesses);
//		

		
	
	} //end of main() function

}


