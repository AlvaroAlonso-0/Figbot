package gui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import auxiliar.Constants;
import auxiliar.Utils;
import models.DisplayInfo;

public class DynamicPanelList extends JPanel{

    private JPanel mainList;
    
    public DynamicPanelList() {
        setLayout(new BorderLayout());
        mainList = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.ABOVE_BASELINE;
        JPanel aux = new JPanel();
        aux.setBackground(Constants.Colors.BACKGROUND);
        mainList.add(aux,gbc);
        mainList.setBackground(Constants.Colors.BACKGROUND);
        add(new JScrollPane(mainList));

        JButton clear = new JButton("Clear");
        clear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                mainList.removeAll();
                mainList.add(aux,gbc);
                validate();
                repaint();
            }
        });
        add(clear, BorderLayout.SOUTH);
    }

    public void addEvent(DisplayInfo info){
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        EmptyBorder border1 = new EmptyBorder(10, 10, 10, 0);
        JPanel panel = new JPanel(new GridBagLayout());
        JLabel label = new JLabel("\t" + info.getMessage());
        Color panelColor;
        label.setForeground(Color.BLACK);
        label.setFont(new Font("Display", Font.BOLD, 14));
        label.setBorder(border1);
        panel.add(label,gbc);
        switch(info.getAction()%900){
            case 0:
                panelColor = Constants.Colors.BAN ; break;
            case 1:
                panelColor = Constants.Colors.UNBAN ; break;
            case 2:
                panelColor = Constants.Colors.TIMEOUT ; break;
            default:
                panelColor = Constants.Colors.INFO ; break;
        }
        panel.setBackground(panelColor);
        if (info.hasArgument()){
            EmptyBorder border2 = new EmptyBorder(0, 40, 10, 0);
            JLabel labelArgument = new JLabel(Utils.getDisplayFormat(info.getArgument()));
            labelArgument.setFont(new Font("Arial", Font.ITALIC, 14));
            labelArgument.setBackground(panelColor.brighter());
            labelArgument.setForeground(Color.BLACK);
            labelArgument.setBorder(border2);
            panel.add(labelArgument,gbc);
        }
        panel.setBorder(new MatteBorder(0, 0, 1, 0, Color.WHITE));
        mainList.add(panel, gbc, 0);
        validate();
        repaint();
    }
}
