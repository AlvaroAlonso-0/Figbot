package controller;

import models.DisplayInfo;
import figbot.Figbot;
import gui.DisplayGUI;
import gui.InputGUI;

public class Controller {
    
    private Figbot figb0t;
    private DisplayGUI displayGui;
    private boolean moderationON;
    private String channelName;

    public Controller(){
        figb0t = new Figbot();
        new InputGUI(this);
    }

    public boolean isModerating(){
        return moderationON;
    }

    public Object getChannelName(){
        return channelName;
    }

    public void loadDisplayGui(String channelName, boolean moderationON){
        this.moderationON = moderationON;
        this.channelName = channelName;
        displayGui = new DisplayGUI(channelName, moderationON);
        //TODO
        figb0t.start(channelName, "98803007",moderationON);
    }

    public synchronized void displayModerationEvent(DisplayInfo info){

        String event = String.format("%s - %s - %s", info.getAction(), info.getMessage(), info.getArgument());
        displayGui.newEvent(event);
    }

    //TODO
    public void closeApp(){
        figb0t.turnOff();
    }
}
