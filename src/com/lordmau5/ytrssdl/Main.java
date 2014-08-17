package com.lordmau5.ytrssdl;

import com.lordmau5.ytrssdl.util.*;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.Timer;

/**
 * Created by Lordmau5 on 15.08.2014.
 */
public class Main {

    public static Map<String, Category> categories = new HashMap<>();

    public static Calendar calendar;

    private static Gui gui;

    private boolean hasCategory(String categoryName) {
        return categories.containsKey(categoryName);
    }

    public static void main(String[] args) {
        new Main();
    }

    public Main() {
        calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        //-----------------------------------------------------------------------------------------------------

        gui = new Gui();
        gui.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                if(gui.getDefaultCloseOperation() == JFrame.EXIT_ON_CLOSE)
                    saveChannels();
            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });
        gui.setVisible(true);

        File channelFolder = new File(System.getProperty("user.dir") + "\\channels");
        if(!channelFolder.exists())
            channelFolder.mkdir();

        File channelList = new File(channelFolder.getAbsolutePath() + "\\channels.txt");
        if(channelList.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(channelList));
                String line;
                String firstCategory = null;
                while((line = br.readLine()) != null) {
                    if(!line.contains("&"))
                        continue;

                    String[] channelInfo = line.split("&");
                    if(!hasCategory(channelInfo[0].toLowerCase())) {
                        categories.put(channelInfo[0].toLowerCase(), new Category(channelInfo[0]));

                        gui.categories.addItem(channelInfo[0]);
                        gui.categories.setEnabled(true);
                        gui.removeCategory.setEnabled(true);
                        gui.fetchCategory.setEnabled(true);
                        gui.addChannel.setEnabled(true);

                        if(firstCategory == null)
                            firstCategory = channelInfo[0];
                    }

                    if(!isProperYoutubeChannel(channelInfo[1]))
                        continue;

                    YTChannel channel = new YTChannel(channelInfo[1], channelInfo[2]);

                    categories.get(channelInfo[0].toLowerCase()).addChannel(channel);
                    if(channelInfo[0].equals(firstCategory)) {
                        gui.channels.addItem(channel);
                        gui.channels.setEnabled(true);
                        gui.removeChannel.setEnabled(true);
                        gui.fetchSelectedChannelButton.setEnabled(true);
                        gui.fetchAllCats.setEnabled(true);
                    }
                    System.out.println("Channel \"" + line + "\" added.");
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.labelThing.setText("");
        gui.buttonFunc = true;
    }

    public static void saveChannels() {
        File channelFolder = new File(System.getProperty("user.dir") + "\\channels");
        File channelList = new File(channelFolder.getAbsolutePath() + "\\channels.txt");
        if(categories.isEmpty()) {
            channelList.delete();
        }
        try {
            FileWriter writer = new FileWriter(channelList, false);
            for(Map.Entry<String, Category> entry : categories.entrySet()) {
                for(YTChannel channel : entry.getValue().getChannels())
                    writer.write(entry.getValue() + "&" + channel.channelName + "&" + channel.lastChecked.saveFormat() + "\n");
            }
            writer.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public static void setReturnText(String text) {
        returnText = text;
    }

    public static String getReturnText() {
        return returnText;
    }

    public static boolean isWorking = false;
    public static String returnText = null;
    public static void fetchUpdate(final YTChannel channel, final boolean isSelectedOnly) {
        returnText = null;
        Thread thread = new Thread(){
            @Override
            public synchronized void run() {
                super.run();

                List<String> newVideos = new ArrayList<>();
                DateFormat checked = new DateFormat(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
                YTRSSFeedParser parser = null;
                YTFeed feed = null;

                int num = 1;
                int max_Res = 50;
                boolean more = true;
                boolean startFresh = false;

                while(more) {
                    startFresh = false;

                    String url = "http://gdata.youtube.com/feeds/base/users/" + channel.channelName + "/uploads?client=ytapi-youtube-rss-redirect&alt=rss&v=2&max-results=" + max_Res + "&start-index=" + num;
                    if(!containsItem(url)) {
                        break;
                    }
                    parser = new YTRSSFeedParser(url);
                    try {
                        feed = parser.readFeed();
                    } catch (Exception e) {
                        if(e.getMessage().contains("400 for URL")) {
                            startFresh = true;
                            num -= 1;
                            if(num == 0)
                                break;
                        }
                    }
                    if(startFresh || feed == null)
                        continue;

                    int sizeBefore = newVideos.size();
                    for(YTFeedMsg msg : feed.getMessages()) {
                        if(channel.lastChecked.isEarlier(msg.pubDate)) {
                            if(!newVideos.contains(msg.link)) {
                                newVideos.add(msg.link);
                                gui.loadingSub.setText("Fetching " + newVideos.size() + " videos...");
                            }
                        }
                        else {
                            more = false;
                            break;
                        }
                    }
                    if(sizeBefore == newVideos.size()) {
                        break;
                    }

                    if(more)
                        num += 50;
                }

                File newvids = new File(System.getProperty("user.dir") + "\\channels\\" + channel.channelName + ".txt");
                if(newVideos.isEmpty()) {
                    if(newvids.exists())
                        newvids.delete();
                }
                else {
                    try {
                        FileWriter writer = new FileWriter(newvids, false);
                        for(String link : newVideos) {
                            writer.write(link + "\n");
                        }
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                channel.lastChecked = checked;

                if(!newVideos.isEmpty())
                    returnText = newVideos.size() + " new videos from " + channel.channelName + "\n";
                else
                    returnText = "NaN";

                isWorking = false;
                gui.loadingSub.setText("");
            }
        };
        thread.start();
        if(isSelectedOnly) {
            gui.buttonFunc = false;
            gui.categories.setEnabled(false);
            gui.channels.setEnabled(false);
            gui.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        }
        isWorking = true;
    }

    public static void fetchCategory(final String categoryName) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();

                Category category = categories.get(categoryName.toLowerCase());
                String updateString = "";
                int fetched = 0;
                int size = category.getChannels().size();
                gui.labelThing.setText("Fetching channel 0/" + size);
                for(YTChannel channel : category.getChannels()) {
                    fetched++;
                    gui.labelThing.setText("Fetching channel " + fetched + "/" + size);

                    fetchUpdate(channel, false);
                    while(isWorking) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if(!returnText.equals("NaN"))
                        updateString = updateString + returnText;
                }

                if(!updateString.isEmpty()) {
                    JOptionPane.showMessageDialog(gui.panel1, updateString);
                }
                else {
                    JOptionPane.showMessageDialog(gui.panel1, "No new videos from your channels.\nCheck back later!");
                }
                gui.labelThing.setText("");
                gui.loadingSub.setText("");
                gui.buttonFunc = true;
                gui.categories.setEnabled(true);
                gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            }
        };
        gui.buttonFunc = false;
        gui.categories.setEnabled(false);
        gui.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        thread.start();
    }

    public static void fetchAllCategories() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();

                String updateString = "";
                int fetched_cat = 0;
                int fetched_chan = 0;
                int size_cat = categories.size();
                int size_chan;
                for(Map.Entry<String, Category> entry : categories.entrySet()) {
                    fetched_cat++;

                    fetched_chan = 0;
                    List<YTChannel> channels = categories.get(entry.getValue().toString().toLowerCase()).getChannels();
                    size_chan = channels.size();
                    for (YTChannel channel : channels) {
                        fetched_chan++;
                        gui.labelThing.setText("Fetching category " + fetched_cat + "/" + size_cat + " - Channel " + fetched_chan + "/" + size_chan);

                        fetchUpdate(channel, false);
                        while(isWorking) {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        if(!returnText.equals("NaN"))
                            updateString = updateString + returnText;
                    }
                }
                if(!updateString.isEmpty()) {
                    JOptionPane.showMessageDialog(gui.panel1, updateString);
                }
                else {
                    JOptionPane.showMessageDialog(gui.panel1, "No new videos from your channels.\nCheck back later!");
                }
                gui.labelThing.setText("");
                gui.loadingSub.setText("");
                gui.buttonFunc = true;
                gui.categories.setEnabled(true);
                gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            }
        };
        gui.buttonFunc = false;
        gui.categories.setEnabled(false);
        gui.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        thread.start();
    }

    public static boolean addChannel(String channelName) {
        return categories.get(gui.categories.getSelectedItem().toString().toLowerCase()).addChannel(new YTChannel(channelName, new DateFormat(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND))));
    }

    public static boolean removeChannel(String channelName) {
        return categories.get(gui.categories.getSelectedItem().toString().toLowerCase()).removeChannel(channelName);
    }

    public static boolean addCategory(String name) {
        if(categories.containsKey(name.toLowerCase()))
            return false;

        categories.put(name.toLowerCase(), new Category(name));
        return true;
    }

    public static boolean removeCategory(String name) {
        if(!categories.containsKey(name.toLowerCase()))
            return false;

        categories.remove(name.toLowerCase());
        return true;
    }

    public static boolean isProperYoutubeChannel(String channelName) {
        try {
            URL url = new URL("http://gdata.youtube.com/feeds/base/users/" + channelName + "/uploads");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(url.openStream()));

            String inputLine = in.readLine();
            if(inputLine.contains("Invalid value"))
                return false;
            in.close();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            if(e.getMessage().contains("400 for URL"))
                return false;
        }
        return true;
    }

    public static boolean containsItem(String parserURL) {
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(new URL(parserURL).openStream()));

            String text = "";
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                text = text + inputLine + "\n";
            in.close();

            if(text.contains("<item>"))
                return true;
            else
                return false;
        } catch (IOException e) {
            if(e.getMessage().contains("403 for URL")) {
                return containsItem(parserURL);
            }
        }
        return false;
    }
}
