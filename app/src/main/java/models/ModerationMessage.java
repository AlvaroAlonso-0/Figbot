package models;

import jade.util.leap.Serializable;

public class ModerationMessage implements Serializable{
    
    private static final long serialVersionUID = 12383294L;

    private String createdBy;
    private String reason;
    private String target;
    private int timeoutDuration;

    public ModerationMessage() {
        createdBy = null;
        reason = null;
        target = null;
        timeoutDuration = -1;
    }

    public String getCreatedBy() {
        return createdBy;
    }
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    public String getReason() {
        return reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }
    public String getTarget() {
        return target;
    }
    public void setTarget(String target) {
        this.target = target;
    }
    public int getTimeoutDuration() {
        return timeoutDuration;
    }
    public void setTimeoutDuration(int timeoutDuration) {
        this.timeoutDuration = timeoutDuration;
    }

    @Override
    public String toString() {
        return "ModerationMessage [createdBy=" + createdBy + ", reason=" + reason + ", target=" + target
                + ", timeoutDuration=" + timeoutDuration + "]";
    }
    
}
