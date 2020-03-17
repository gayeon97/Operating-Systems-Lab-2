
public class Process implements Comparable {
	
	public boolean run_first_time = false;
	public boolean is_unstarted_to_run = false;
	public boolean is_ready_to_run = false;
	public boolean is_run_to_blocked = false;
	public boolean is_blocked_to_ready = false;
	
	private boolean is_run_to_ready_preemptive = false;
	
	private boolean hasBeenPreempted = false;
	
	private int arrivalTime = 0; // "A"
	private int b_interval = 0; //"B"
	private int totalCPUTimeNeeded = 0; //"C"
	private int m_interval = 0; //"M"
	
	private String state = "";
	private int cpuBurstSet = 0;
	private int ioBurstSet = 0;
	private int totalCPUTimeLeft = 0;
	private int remainingCPUBurst = 0;
	private int remainingIOBurst = 0;
	private int timeInBlocked = 0;
	private int timeInReady = 0;
		
	private int finishTime = 0;
	private int turnaround = 0;
	
	private int index = 0;
	
	private int cycleTimeWhenReady = 0;
	
	private int numRun = 0;
	
	Process (int a, int b, int c, int m, int i) {
		arrivalTime = a;
		b_interval = b;
		totalCPUTimeNeeded = c;
		m_interval = m;
		
		index = i;
		
		state = "unstarted";
		
		totalCPUTimeLeft = c;
	}
	
	public int getA() {
		return arrivalTime;
	}
	
	public int getB() {
		return b_interval;
	}
	
	public int getC() {
		return totalCPUTimeNeeded;
	}
	
	public int getM() {
		return m_interval;
	}
	
	public int getIndex() {
		return index;
	}
	
	public void setIndex(int i) {
		index = i;
	}
	
	public int getCycleTimeWhenReady() {
		return cycleTimeWhenReady;
	}
	
	public void setCycleTimeWhenReady(int cycT) {
		cycleTimeWhenReady = cycT;
	}
	
	public int getNumRun() {
		return numRun;
	}
	
	public void increNumRun() {
		numRun ++;
	}
	
	public void setNumRun(int nR) {
		numRun = nR;
	}
	
	public boolean isRunToReadyPreemptive() {
		return is_run_to_ready_preemptive;
	}
	
	public void setRunToReadyPreemptive(boolean t) {
		is_run_to_ready_preemptive = t;
	}
	
	public boolean hasBeenPreempted() {
		return hasBeenPreempted;
	}
	
	public void setHasBeenPreempted(boolean tt) {
		hasBeenPreempted = tt;
	}
	
	//public void resetNumRun();
	
	public String getState() {
		return state;
	}
	
	public void setState(String current_state) {
		state = current_state;
	}
	
	public int getCPUBurst() {
		return cpuBurstSet;
	}
	
	
	public void setCPUBurst(int cpu_burst) {
		cpuBurstSet = cpu_burst;
	}
	
	public void decrCPUBurstSET() {
		cpuBurstSet --;
	}
	
	
	public int getRemainingCPUBurst() {
		return remainingCPUBurst; 
	}
	
	public void setRemainingCPUBurst(int n) {
		remainingCPUBurst = n; 
	}
	
	public void decrRemainingCPUBurst() {
		if (remainingCPUBurst > 0) {
			remainingCPUBurst --; 
			
			if (totalCPUTimeLeft > 0) {
				totalCPUTimeLeft --;
			}
		}
		
	}
	
	public int getIOBurst() {
		return ioBurstSet;
	}
	
	public void setIOBurst(int io_burst) {
		ioBurstSet = io_burst;
	}
	
	public int getRemainingIOBurst() {
		return remainingIOBurst; 
	}
	
	public void setRemainingIOBurst(int m) {
		remainingIOBurst = m; 
	}
	
	public void decrRemainingIOBurst() {
		if (remainingIOBurst > 0) {
			remainingIOBurst --; 
			
			timeInBlocked ++;
		}
	}
	
	public int getTotalCPUTimeLeft() {
		return totalCPUTimeLeft;
	}
	
	public void decrementTotalCPUTimeLeft() {
		totalCPUTimeLeft -= cpuBurstSet;
	}
	
	public int getTimeInBlockedState() {
		return timeInBlocked;
	}
	
	public void increTimeInBlockedState(int curr_blockT) {
		timeInBlocked += curr_blockT;
	}
	
	public int getTimeInReadyState() {
		return timeInReady;
	}
	
	public void increTimeInReadyState() {
		timeInReady ++;
	}
	
	public int getFinishTime() {
		return finishTime;
	}
	
	public void setFinishTime(int finish_cycle) {
		finishTime = finish_cycle;
	}
	
	public int getTurnAroundTime() {
		return turnaround;
	}
	
	public void setTurnAroundTime(int finishT, int aT) {
		turnaround = finishT - aT;
	}	

	@Override
	public int compareTo(Object o) {
		if (this.getA() == ((Process)o).getA()) {
			return 0;
		} else if (this.getA() > ((Process)o).getA()) {
			return 1;
		}
		return -1;
	}
	
	public void printProcessInfo() {
		String processResult = String.format("Process %d:\n", getIndex());
		processResult += String.format("\t(A,B,C,M) = (%d,%d,%d,%d)\n",arrivalTime, b_interval, totalCPUTimeNeeded, m_interval);
		processResult += String.format("\tFinishing time: %d\n", finishTime);
		processResult += String.format("\tTurnaround time: %d\n", turnaround);
		processResult += String.format("\tI/O time: %d\n", timeInBlocked);
		processResult += String.format("\tWaiting time: %d\n", timeInReady);

		System.out.println(processResult);
	}
	

}
