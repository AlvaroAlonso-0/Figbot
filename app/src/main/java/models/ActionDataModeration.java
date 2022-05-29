package models;

public class ActionDataModeration extends ActionData{
    
    private ModerationMessage message;

    public ModerationMessage getMessage() {
        return message;
    }

    public void setMessage(ModerationMessage message) {
        this.message = message;
    }
}
