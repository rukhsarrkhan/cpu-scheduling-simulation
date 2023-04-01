package main;

import main.models.Event;

import java.util.*;

import static main.Simulator.eventList;

public class Process {
    int processId;
    public int cpuBurstTime;
    public int ioTimeRemaining;
    public int waitingTime;
    public int completedAt;
    public int meanIOInterval;
    private long ioInterval;
    private final Random rand = new Random();
    private final Integer rrquantum;
    private Integer remainingRRQuantum;

    public void resetrrquantum() {
        this.remainingRRQuantum = this.rrquantum;
    }

    public void setCompletedAt(int completedAt) {
        this.completedAt = completedAt;
    }

    void nextIOInterval() {
        while (this.ioInterval < 1)
            // randomly generated i/o interval
            this.ioInterval = Math.round(-1 * (Math.log(1 - rand.nextDouble()) * (this.meanIOInterval)));
    }

    Process(int processId, int meanIOInterval, Integer rrquantum) {
        this.processId = processId;
        this.ioTimeRemaining = 0;
        this.waitingTime = 0;
        this.meanIOInterval = meanIOInterval;
        // random execution time with lengths uniformly distributed between 2 and 4
        // minutes
        this.cpuBurstTime = getRandomBurstTime(120000, 180000);
        this.nextIOInterval();
        this.rrquantum = rrquantum;
    }

    private static int getRandomBurstTime(int min, int max) {
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    boolean isFinished() {
        if (cpuBurstTime <= 0) {
            return true;
        }
        return false;
    }

    boolean ioNeeded() {
        if (ioTimeRemaining > 0) {
            return true;
        }
        return false;
    }

    boolean isrrquantumFinished() {
        if (rrquantum == null) {
            return false;
        }
        return rrquantum <= 0;
    }

    void ioTimeReduction() {
        ioTimeRemaining--;
    }

    void increaseWaitTime() {
        waitingTime++;
    }

    void execute() {
        this.ioInterval--;
        this.cpuBurstTime--;
        if (this.remainingRRQuantum != null)
            this.remainingRRQuantum--;
        if (this.ioInterval <= 0) {
            triggerIOInterruptEvent(this);
        }
    }

    private void triggerIOInterruptEvent(Process p) {
        Event event = new Event(0, p);
        eventList.add(event);
    }

    public void triggerIOCompletionEvent() {
        Event event = new Event(1, this);
        eventList.add(event);
    }

    public void triggerRRInterrupt() {
        Event event = new Event(2, this);
        eventList.add(event);
    }

    public void printState(String name) {
        System.out.printf("| %-15s | %-15s | %-15s | %-15s |%n", processId, cpuBurstTime, ioTimeRemaining, name);
    }
}
