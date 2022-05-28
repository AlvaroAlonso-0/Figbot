package models;

import auxiliar.Constants;
import jade.util.leap.Serializable;

public class ActionData implements Serializable{
    
    private static final long serialVersionUID = 123456789L;

    private TwitchMessage message;
    private int action;
    private String argument;
    
    public ActionData() {
        this.message = null;
        this.action = Constants.Code.ERROR;
        this.argument = null;
    }
    public TwitchMessage getMessage() {
        return message;
    }
    public void setMessage(TwitchMessage message) {
        this.message = message;
    }
    public int getAction() {
        return action;
    }
    public void setAction(int action) {
        this.action = action;
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
