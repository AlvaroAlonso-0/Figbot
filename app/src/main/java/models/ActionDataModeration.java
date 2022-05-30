package models;

public class ActionDataModeration extends ActionData{
    
    private ModerationMessage message;

    public ModerationMessage getModeration() {
        return message;
    }

    public void setModeration(ModerationMessage message) {
        this.message = message;
    }
}
