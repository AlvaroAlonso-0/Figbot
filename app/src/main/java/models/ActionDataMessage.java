package models;

public class ActionDataMessage extends ActionData{
    
    private TwitchMessage message;
    private String argument;

    public ActionDataMessage(){
        message = null;
        argument = null;
    }

    public TwitchMessage getMessage() {
        return message;
    }
    public void setMessage(TwitchMessage message) {
        this.message = message;
    }

    public String getArgument() {
        return argument;
    }
    public void setArgument(String argument) {
        if (argument != null && argument.length() > 0 && argument.charAt(0) == '@'){
            argument = argument.substring(1);
        }
        this.argument = argument;
    }
}
