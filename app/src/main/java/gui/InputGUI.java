package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.*;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.MouseInputAdapter;

import controller.Controller;

public class InputGUI {

    private static final int MAX_WIDTH = 400;
    private static final int MAX_HEIGHT = 180;
    private static final Color BACKGROUND_COLOR = new Color(149, 108, 224);

    private JFrame frame;
    private JPanel background;
    private JLabel channelNameLabel;
    private JLabel channelIdLabel;
    private JLabel moderatorLabel;
    private JTextField channelNameText;
    private JTextField channelIdText;
    private JCheckBox moderatorCheckBox;
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
        channelIdLabel = new JLabel("Channel's ID");
        channelIdLabel.setFont(new Font("Calibri", Font.BOLD, 20));
        channelIdLabel.setForeground(Color.black);
        channelIdText = new JTextField();
        moderatorLabel = new JLabel("Moderation");
        moderatorLabel.setFont(new Font("Calibri", Font.BOLD, 20));
        moderatorLabel.setForeground(Color.black);
        moderatorCheckBox = new JCheckBox();
        accept = new JButton("Accept");
        channelNameLabel.setBounds(25, 27, 175, 25);
        channelNameText.setBounds(215, 25, 160, 25);
        channelIdLabel.setBounds(25, 77, 160, 25);
        channelIdText.setBounds(215, 75, 160, 25);
        moderatorLabel.setBounds(25, 127, 160, 25);
        moderatorCheckBox.setBackground(BACKGROUND_COLOR);
        moderatorCheckBox.setBounds(140, 127, 18, 18);
        accept.setBounds(232, 127, 127, 25);
        background.add(channelNameLabel);
        background.add(channelNameText);
        background.add(channelIdLabel);
        background.add(channelIdText);
        background.add(moderatorLabel);
        background.add(moderatorCheckBox);
        background.add(accept);
        frame.getContentPane().add(background);
        accept.addMouseListener(new MouseInputAdapter() {
           public void mouseReleased(MouseEvent e){
                controller.loadDisplayGui(channelNameText.getText(), moderatorCheckBox.isSelected());
                frame.setVisible(false);
                frame.dispose();
           }
        });
    }



    public static void main (String [] args){
        //new InputGUI();
    }
}
