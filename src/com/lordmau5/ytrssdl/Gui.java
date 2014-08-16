package com.lordmau5.ytrssdl;

import com.lordmau5.ytrssdl.util.DateFormat;
import com.lordmau5.ytrssdl.util.YTChannel;

import javax.swing.*;
import javax.swing.text.DateFormatter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

/**
 * Created by Lordmau5 on 15.08.2014.
 */
public class Gui extends JFrame {
    public JButton fetchUpdates;
    public JComboBox channels;
    public JButton addChannel;
    public JButton removeChannel;
    public JPanel panel1;
    public JButton fetchSelectedChannelButton;

    public Gui() {
        super("YT RSS Downloader");

        setContentPane(panel1);

        pack();
        setSize(400, 200);
        setResizable(false);
        setLocation(50, 50);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        addChannel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String n = JOptionPane.showInputDialog(panel1, "Enter the name of the Youtube Channel you want to add:");
                if(n == null)
                    return;

                if(!Main.isProperYoutubeChannel(n)) {
                    JOptionPane.showMessageDialog(panel1, "The channel you entered was not found.");
                    return;
                }

                boolean cool = Main.addChannel(n);
                if(!cool)
                    return;

                YTChannel channel = new YTChannel(n, new DateFormat(Main.calendar.get(Calendar.DAY_OF_MONTH), Main.calendar.get(Calendar.MONTH) + 1, Main.calendar.get(Calendar.YEAR), Main.calendar.get(Calendar.HOUR_OF_DAY), Main.calendar.get(Calendar.MINUTE), Main.calendar.get(Calendar.SECOND)));
                channels.addItem(channel);
                channels.setSelectedIndex(channels.getItemCount() - 1);

                channels.setEnabled(true);
                removeChannel.setEnabled(true);
                fetchSelectedChannelButton.setEnabled(true);
                fetchUpdates.setEnabled(true);
            }
        });

        removeChannel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object[] options = {"Yes", "No"};
                int n = JOptionPane.showOptionDialog(panel1, "Are you sure you want to remove this channel?", "Remove Channel", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
                if(n == 0) {
                    boolean cool = Main.removeChannel(((YTChannel) channels.getSelectedItem()).channelName);
                    if(!cool)
                        return;

                    channels.removeItem(channels.getSelectedItem());

                    if(channels.getItemCount() == 0) {
                        channels.setEnabled(false);
                        removeChannel.setEnabled(false);
                        fetchSelectedChannelButton.setEnabled(false);
                        fetchUpdates.setEnabled(false);
                    }
                }
            }
        });
        fetchUpdates.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.fetchUpdates();
            }
        });
        fetchSelectedChannelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String update = Main.fetchUpdate((YTChannel) channels.getSelectedItem());
                if(update.equals("NaN"))
                    update = "No new videos from \"" + ((YTChannel)channels.getSelectedItem()).channelName + "\"\nCheck back later!";

                JOptionPane.showMessageDialog(panel1, update);
            }
        });
    }

    private void createUIComponents() {
        panel1 = new JPanel();
    }
}
