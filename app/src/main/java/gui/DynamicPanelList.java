package gui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.MatteBorder;

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
        aux.setBackground(Color.BLACK);
        mainList.add(aux,gbc);
        mainList.setBackground(Color.BLACK);
        add(new JScrollPane(mainList));

        JButton add = new JButton("Clear");
        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel panel = new JPanel();
                JLabel label = new JLabel("Hello");
                label.setForeground(Color.WHITE);
                label.setFont(new Font("Display", Font.BOLD, 12));
                panel.add(label);
                panel.setBackground(Color.BLACK);
                panel.setBorder(new MatteBorder(0, 0, 1, 0, Color.WHITE));
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridwidth = GridBagConstraints.REMAINDER;
                gbc.weightx = 1;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                mainList.add(panel, gbc, 0);

                validate();
                repaint();
            }
        });
        add(add, BorderLayout.SOUTH);
    }

    public void addEvent(String event){
        JPanel panel = new JPanel();
        JLabel label = new JLabel("Hello");
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Display", Font.BOLD, 12));
        panel.add(label);
        panel.setBackground(Color.BLACK);
        panel.setBorder(new MatteBorder(0, 0, 1, 0, Color.WHITE));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainList.add(panel, gbc, 0);

        validate();
        repaint();
    }
}
