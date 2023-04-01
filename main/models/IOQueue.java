package main.models;

import main.Process;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class IOQueue {
    private Queue<Process> queue;


    public IOQueue(SchedulerAlgorithms type) {
        if(type == SchedulerAlgorithms.FIRST_COME_FIRST_SERVED
                || type == SchedulerAlgorithms.ROUND_ROBIN) {
            queue = new LinkedList<>();
        } else {
            queue = new PriorityQueue<>(Comparator.comparingInt((Process a) -> a.ioTimeRemaining));
        }
    }

    public Queue<Process> getQueue() {
        return queue;
    }
}