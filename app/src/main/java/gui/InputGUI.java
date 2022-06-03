package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.*;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;
import java.util.function.Predicate;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.MouseInputAdapter;

import auxiliar.Constants;
import controller.Controller;

public class InputGUI {

    private static final int MAX_WIDTH = 400;
    private static final int MAX_HEIGHT = 180;
    private static final Color BACKGROUND_COLOR = new Color(149, 108, 224);

    private JFrame frame;
    private JPanel background;
    private JLabel channelNameLabel;
    private JLabel channelIdLabel;
    private JTextField channelNameText;
    private JComboBox<String> timeZone;
    private JButton accept;

    private Controller controller;

    public InputGUI (Controller controller){
        setupFrame();
        setupComponents();
        frame.pack();
        this.controller = controller;
    }

    private void setupFrame() {
        frame = new JFrame("Figbot");
        frame.setSize(new Dimension(MAX_WIDTH, MAX_HEIGHT));
        frame.getContentPane().setPreferredSize(new Dimension(MAX_WIDTH, MAX_HEIGHT));            
        frame.setLocationRelativeTo(null);         
        frame.setLayout(null);  
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
    }

    private void setupComponents(){
        background = new JPanel();
        background.setBounds(0,0, MAX_WIDTH, MAX_HEIGHT);
        background.setBackground(BACKGROUND_COLOR);
        background.setLayout(null);
        channelNameLabel = new JLabel("Channel's name");
        channelNameLabel.setFont(new Font("Calibri", Font.BOLD, 20));
        channelNameLabel.setForeground(Color.black);
        channelNameText = new JTextField();
        channelIdLabel = new JLabel("Timezone");
        channelIdLabel.setFont(new Font("Calibri", Font.BOLD, 20));
        channelIdLabel.setForeground(Color.black);
        accept = new JButton("Accept");
        timeZone = new JComboBox<>(getTimeZones());
        timeZone.setFont(new Font("Dialog", Font.PLAIN, 12));
        channelNameLabel.setBounds(25, 27, 175, 25);
        channelNameText.setBounds(215, 25, 160, 25);
        channelIdLabel.setBounds(25, 77, 160, 25);
        timeZone.setBounds(215, 75, 160, 25);
        accept.setBounds(MAX_WIDTH/4, 127, MAX_WIDTH/2, 25);
        background.add(channelNameLabel);
        background.add(channelNameText);
        background.add(channelIdLabel);
        background.add(timeZone);
        background.add(accept);
        System.out.println(channelNameText.getFont());
        frame.getContentPane().add(background);
        accept.addMouseListener(new MouseInputAdapter() {
           public void mouseReleased(MouseEvent e){
                controller.loadDisplayGui(channelNameText.getText(),timeZone.getSelectedItem().toString());
                frame.setVisible(false);
                frame.dispose();
           }
        });
    }

    private String [] getTimeZones(){
        List<String> forbidden = Arrays.asList(Constants.TIMES.FORBIDDEN);
        String[] posibleLabels = Arrays.asList(TimeZone.getAvailableIDs()).stream().filter(new Predicate<String>() {
            @Override
            public boolean test(String t) {
                return !t.contains("GMT") && !t.startsWith("Etc") && !t.startsWith("System") && !forbidden.contains(t);
            }
        }).toArray(String[]::new);
        return Arrays.copyOfRange(posibleLabels, 0, posibleLabels.length - 28);
    }

    public static void main(String [] args){
        new InputGUI(null);
    }
}
