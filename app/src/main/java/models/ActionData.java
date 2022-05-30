package models;

import jade.util.leap.Serializable;

public abstract class ActionData implements Serializable{
    
    private static final long serialVersionUID = 123456789L;

    private int action;
    
    public int getAction() {
        return action;
    }
    public void setAction(int action) {
        this.action = action;
    }
}
