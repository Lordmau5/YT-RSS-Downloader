package com.lordmau5.ytrssdl.util;

/**
 * Created by Lordmau5 on 15.08.2014.
 */
public class YTChannel {

    public String channelName;
    public DateFormat lastChecked;

    public YTChannel(String channelName, String lastChecked) {
        this.channelName = channelName;
        this.lastChecked = new DateFormat(lastChecked);
    }

    public YTChannel(String channelName, DateFormat lastChecked) {
        this.channelName = channelName;
        this.lastChecked = lastChecked;
    }

    public String toString() {
        return channelName;
    }

}
