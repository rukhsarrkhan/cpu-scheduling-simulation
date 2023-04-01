package main;

import main.models.*;

import java.util.*;

public class Simulator {
    private final ReadyQueue readyQ;
    private final IOQueue ioQ;
    private final Queue<Process> completedQ;
    private int clock = 0;
    private cpuStates cpuState = cpuStates.IDLE;
    private IoStates ioState = IoStates.IDLE;
    private int cpuUtil = 0;
    private ProcessControlBlock pcb = new ProcessControlBlock();

    public static LinkedList<Event> eventList = new LinkedList<Event>();

    // initiates queues
    public Simulator(int processes, SchedulerAlgorithms scheduler, Integer rrquantum) {
        this.readyQ = new ReadyQueue(scheduler);
        this.ioQ = new IOQueue(scheduler);
        if (scheduler == SchedulerAlgorithms.FIRST_COME_FIRST_SERVED) {
            rrquantum = null;
        } else if (scheduler == SchedulerAlgorithms.SHORTEST_JOB_FIRST) {
            rrquantum = null;
        }
        this.completedQ = new LinkedList<>();

        int[] meanIOInterval = new int[] { 30, 35, 40, 45, 50, 55, 60, 65, 70, 75 };
        for (int i = 0; i < processes; i++) {
            Process p = new Process(i + 1, meanIOInterval[i], rrquantum);
            readyQ.getQueue().add(p);
        }
    }

    public static void main(String[] args) {
        System.out.println("___________________________FCFS________________________________");
        System.out.println("\n");
        runSimulation(SchedulerAlgorithms.FIRST_COME_FIRST_SERVED);
        System.out.println("___________________________SJF_________________________________");
        System.out.println("\n");
        runSimulation(SchedulerAlgorithms.SHORTEST_JOB_FIRST);
        System.out.println("___________________________RR__________________________________");
        System.out.println("\n");
        runSimulation(SchedulerAlgorithms.ROUND_ROBIN);

    }

    public static void runSimulation(SchedulerAlgorithms ps) {
        // Simulator runs thee FCFS, SJF and RR algorithm one after the other
        Simulator cpu = new Simulator(10, ps, 20);
        System.out.println("Initial Process state");
        cpu.executeSimulation();
        cpu.printState();
        cpu.outputStats();
    }

    // This function checks for events, io and ready queue states and decides on
    // process actions
    public void executeSimulation() {
        // the code executes until all the process are completed
        while (completedQ.size() != 10) {
            // this prints the process state grid after every 1 minute
            this.clock++;
            if (this.clock % 60000 == 0)
                printState();
            // checking for events for any interrupts
            checkForEvents();
            if (this.ioState == IoStates.IDLE) {
                // if I/O queue is not empty, assign I/O to the process
                if (ioQ.getQueue().size() > 0) {
                    this.ioState = IoStates.RUNNING;
                    Process p = ioQ.getQueue().poll();
                    pcb.setIOProcess(p);
                    p.ioTimeReduction();
                }
            } else if (this.ioState == IoStates.RUNNING) {
                Process p = pcb.ioProcess;
                if (p.ioNeeded()) {
                    p.ioTimeReduction();
                } else {
                    p.triggerIOCompletionEvent();
                    checkForEvents();
                }
            }
            this.increaseWaitTime(ioQ.getQueue());
            // when cpu is in Idle state
            if (this.cpuState == cpuStates.IDLE) {
                // and the ready queue is not emptu
                if (readyQ.getQueue().size() > 0) {
                    // assign process to cpu
                    Process p = readyQ.getQueue().poll();
                    this.cpuState = cpuStates.RUNNING;
                    pcb.setCPUProcess(p);
                    p.execute();
                    this.cpuUtil++;
                }
                // when cpu is running
            } else if (this.cpuState == cpuStates.RUNNING) {
                Process p = pcb.cpuProcess;
                // and process completes excution, change cpu state
                if (p.isFinished()) {
                    this.cpuState = cpuStates.IDLE;
                    completedQ.add(p);
                    p.setCompletedAt(clock);
                    pcb.setCPUProcess(null);
                    // and i/o is needed, add request in i/o queue and update cpu state
                } else if (p.ioNeeded()) {
                    ioQ.getQueue().add(p);
                    pcb.setCPUProcess(null);
                    cpuState = cpuStates.IDLE;
                    // and quantum is finished trigger round robin interrupt
                } else if (p.isrrquantumFinished()) {
                    p.triggerRRInterrupt();
                    checkForEvents();
                } else {
                    p.execute();
                    this.cpuUtil++;
                }
            }
            this.increaseWaitTime(readyQ.getQueue());
        }
    }

    private void checkForEvents() {
        if (eventList.size() > 0) {
            Event event = eventList.poll();
            // when i/o completion
            if (event.type == 0) {
                event.process.ioTimeRemaining = 60;
                // when i/o completion
            } else if (event.type == 1) {
                this.ioState = IoStates.IDLE;
                readyQ.getQueue().add(event.process);
                event.process.nextIOInterval();
                pcb.setIOProcess(null);
                // when rr interrupt
            } else if (event.type == 2) {
                this.cpuState = cpuStates.IDLE;
                readyQ.getQueue().add(event.process);
                event.process.resetrrquantum();
                pcb.setCPUProcess(null);
            }
        }
    }

    private void increaseWaitTime(Queue<Process> q) {
        for (Process p : q) {
            p.increaseWaitTime();
        }
    }

    public void printState() {
        System.out.println("Processes States at time: " + this.clock);
        System.out.println(
                "__________________________________________________________________________");
        System.out.printf("| %-15s | %-15s | %-15s | %-15s |%n", "Process ID", "Burst Time",
                "I/O Time Remaining", "Queue");
        System.out.println(
                "__________________________________________________________________________ ");
        this.printQueueState(readyQ.getQueue(), "Ready Queue");
        this.printQueueState(ioQ.getQueue(), "I/O Queue");
        this.printQueueState(completedQ, "Completed Queue");
        System.out.println("\n\n");
    }

    private void printQueueState(Queue<Process> q, String name) {
        for (Process p : q) {
            p.printState(name);
        }
    }

    public void outputStats() {
        System.out.printf("\nCPU Utilization: %s", (((double) cpuUtil / clock) * 100 + " %"));
        System.out.printf("\nThroughput: %s", ((double) clock / completedQ.size()));

        double avgTurnaround = 0.0;
        double avgWaitTime = 0.0;
        for (Process p : completedQ) {
            avgTurnaround = avgTurnaround + p.completedAt;
            avgWaitTime = avgWaitTime + p.waitingTime;
        }
        System.out.printf("\nAverage Turnaround time: %s", (avgTurnaround / completedQ.size()) + " ms");
        System.out.printf("\nAverage Waiting time: %s", (avgWaitTime / completedQ.size()) + " ms");
        System.out.println("\n\n");

    }
}
