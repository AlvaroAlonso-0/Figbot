package gui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import models.DisplayInfo;

public class DisplayGUI {

    private JFrame frame;
    private DynamicPanelList events;
    

    public DisplayGUI(String channelName, boolean moderation){
        frame = new JFrame("Figbot - " + channelName);
        events = new DynamicPanelList();
        frame.setSize(new Dimension(900, 900));
        frame.getContentPane().setPreferredSize(new Dimension(900, 900));            
        frame.setLocationRelativeTo(null);         
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        events.setBackground(Color.BLACK);
        frame.add(events);
        
    }
    
    public void newEvent(String event){
        events.addEvent(event);
    }

    //TODO borrar?
    public static void main (String [] args){
        new DisplayGUI("rayo106", true);
    }
}
