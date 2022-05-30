package models;

public class DisplayInfo {
    
    private int action;
    private String message;
    private String argument;
    
    public DisplayInfo(int action, String message) {
        this.action = action;
        this.message = message;
        this.argument = null;
    }

    public DisplayInfo(int action, String message, String argument) {
        this.action = action;
        this.message = message;
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
