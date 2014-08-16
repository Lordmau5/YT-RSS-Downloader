package com.lordmau5.ytrssdl.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lordmau5 on 16.08.2014.
 */
public class Category {

    private String name;
    private List<YTChannel> channels = new ArrayList<>();

    public Category(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }

    public boolean addChannel(YTChannel channel) {
        for(YTChannel oChannel : channels)
            if(channel.channelName.toLowerCase().equals(oChannel.channelName.toLowerCase()))
                return false;
        channels.add(channel);
        return true;
    }

    public boolean removeChannel(String channelName) {
        for(YTChannel oChannel : channels)
            if(channelName.toLowerCase().equals(oChannel.channelName.toLowerCase())) {
                channels.remove(oChannel);
                return true;
            }
        return false;
    }

    public List<YTChannel> getChannels() {
        return channels;
    }

}
