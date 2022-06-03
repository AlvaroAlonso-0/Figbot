package models;

import java.time.LocalTime;

public class DisplayInfo {
    
    private int action;
    private String message;
    private String argument;
    
    public DisplayInfo(int action, String message) {
        LocalTime time = LocalTime.now();
        this.action = action;
        this.message = String.format("[%2d:%2d:%2d] - %s", time.getHour(), time.getMinute(), time.getSecond(), message);
        
    }

    public DisplayInfo(int action, String message, String argument) {
        this(action,message);
        this.argument = argument;
    }

    public int getAction() {
        return action;
    }

    public String getMessage() {
        return message;
    }

    public boolean hasArgument() {
        return argument != null;
    }

    public String getArgument() {
        return argument;
    }
}
