package com.minios.models;

import javafx.beans.property.*;

public class OSProcess {
    private final StringProperty name;
    private final IntegerProperty arrivalTime;
    private final IntegerProperty burstTime;
    private final IntegerProperty priority;
    private final IntegerProperty completionTime;
    private final IntegerProperty waitingTime;
    private final IntegerProperty turnaroundTime;
    private final StringProperty status;

    public OSProcess(String name, int arrivalTime, int burstTime, int priority) {
        this.name = new SimpleStringProperty(name);
        this.arrivalTime = new SimpleIntegerProperty(arrivalTime);
        this.burstTime = new SimpleIntegerProperty(burstTime);
        this.priority = new SimpleIntegerProperty(priority);
        this.completionTime = new SimpleIntegerProperty(0);
        this.waitingTime = new SimpleIntegerProperty(0);
        this.turnaroundTime = new SimpleIntegerProperty(0);
        this.status = new SimpleStringProperty("Ready");
    }

    // Getters and Properties for JavaFX TableView
    public String getName() { return name.get(); }
    public StringProperty nameProperty() { return name; }
    
    public int getArrivalTime() { return arrivalTime.get(); }
    public IntegerProperty arrivalTimeProperty() { return arrivalTime; }
    
    public int getBurstTime() { return burstTime.get(); }
    public IntegerProperty burstTimeProperty() { return burstTime; }
    
    public int getPriority() { return priority.get(); }
    public IntegerProperty priorityProperty() { return priority; }
    
    public int getCompletionTime() { return completionTime.get(); }
    public IntegerProperty completionTimeProperty() { return completionTime; }
    public void setCompletionTime(int ct) { this.completionTime.set(ct); }
    
    public int getWaitingTime() { return waitingTime.get(); }
    public IntegerProperty waitingTimeProperty() { return waitingTime; }
    public void setWaitingTime(int wt) { this.waitingTime.set(wt); }
    
    public int getTurnaroundTime() { return turnaroundTime.get(); }
    public IntegerProperty turnaroundTimeProperty() { return turnaroundTime; }
    public void setTurnaroundTime(int tat) { this.turnaroundTime.set(tat); }

    public String getStatus() { return status.get(); }
    public StringProperty statusProperty() { return status; }
    public void setStatus(String status) { this.status.set(status); }
}
