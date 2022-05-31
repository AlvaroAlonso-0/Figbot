package models;

public class TwitchMessageHolder {

    private TwitchMessage message;

    public TwitchMessageHolder() {
        message = null;
    }

    public TwitchMessage getTwitchMessage() {
        return message;
    }

    public void setTwitchMessage(TwitchMessage message) {
        this.message = message;
    }
    
}
