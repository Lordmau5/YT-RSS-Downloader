package com.lordmau5.ytrssdl.util;

/**
 * Created by Lordmau5 on 15.08.2014.
 */
public class YTFeedMsg {

    public String guid;
    public DateFormat pubDate;
    public String title;
    public String link;

    public YTFeedMsg(String guid, String pubDate, String title, String link) {
        this.guid = guid;
        this.pubDate = new DateFormat(pubDate.substring(pubDate.indexOf(' ') + 1));
        this.title = title;
        this.link = link;
    }

    @Override
    public String toString() {
        return "YTFeedMsg [title=" + title + ", pubDate=" + pubDate.toString()
                + ", link=" + link + ", guid=" + guid
                + "]";
    }

}
