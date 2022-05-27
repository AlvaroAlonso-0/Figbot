package models;

public class TwitchMessageHolder {

    private TwitchMessage message;

    public TwitchMessageHolder() {
        message = null;
    }

    public TwitchMessage getMessage() {
        return message;
    }

    public void setMessage(TwitchMessage message) {
        this.message = message;
    }
    
}
