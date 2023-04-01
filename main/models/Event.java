package main.models;

import main.Process;

public class Event {
    //0 - io interrupt, 1 - io completion, 2 - rr interrupt
    public int type = 0;
    // defines the ID of the process for the specific event
    public Process process = null;

    public Event(int type, Process process) {
        this.type = type;
        this.process = process;
    }
}
