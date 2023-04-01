package main.models;

import main.Process;

public class ProcessControlBlock {
    public Process cpuProcess;
    public Process ioProcess;

    public void setCPUProcess(Process process) {
        this.cpuProcess = process;
    }

    public void setIOProcess(Process ioProcess) {
        this.ioProcess = ioProcess;
    }

}