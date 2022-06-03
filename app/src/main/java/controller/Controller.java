package controller;

import models.DisplayInfo;
import figbot.Figbot;
import gui.DisplayGUI;
import gui.InputGUI;

public class Controller {
    
    private Figbot figb0t;
    private DisplayGUI displayGui;
    private String channelName;

    public Controller(){
        figb0t = new Figbot();
        new InputGUI(this);
    }

    public Object getChannelName(){
        return channelName;
    }

    public void loadDisplayGui(String channelName,String timeZone){
        this.channelName = channelName;
        displayGui = new DisplayGUI(channelName);
        figb0t.start(channelName,timeZone);
    }

    public synchronized void displayModerationEvent(DisplayInfo info){
        displayGui.newEvent(info);
    }

    public void closeApp(){
        figb0t.turnOff();
    }
}
