package controller;

import models.DisplayInfo;
import gui.DisplayGUI;
import gui.InputGUI;

public class Controller {
    
    private DisplayGUI displayGui;
    private boolean started;
    private boolean moderation;
    private String channelName;

    public Controller(){
        new InputGUI(this);
        started = false;
    }

    public boolean getStarted(){
        return started;
    }

    public boolean getModeration(){
        return moderation;
    }

    public Object getChannelName(){
        return channelName;
    }

    public void loadDisplayGui(String channelName, boolean moderation){
        this.moderation = moderation;
        this.channelName = channelName;
        started = true;
        System.out.printf("Moderation -> %s, ChannelName -> %s, Started -> %s\n", moderation, channelName, getStarted());//TODO
        displayGui = new DisplayGUI(channelName, moderation);
    }

    public synchronized void displayModerationEvent(DisplayInfo info){ 
        String event = String.format("%s - %s - %s", info.getAction(), info.getMessage(), info.getArgument());
        displayGui.newEvent(event);
    }

    //TODO borrar?
    public static void main (String [] args){
        new Controller();
    }
    
}
