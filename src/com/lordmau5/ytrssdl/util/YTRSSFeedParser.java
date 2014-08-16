package com.lordmau5.ytrssdl.util;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Lordmau5 on 15.08.2014.
 */
public class YTRSSFeedParser {
    static final String ITEM = "item";
    static final String GUID = "guid";
    static final String PUB_DATE = "pubDate";
    static final String TITLE = "title";
    static final String LINK = "link";

    final URL url;

    public YTRSSFeedParser(String feedUrl) {
        try {
            this.url = new URL(feedUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public YTFeed readFeed() throws Exception {
        YTFeed feed = null;
        try {
            boolean isFeedHeader = true;
            // Set header values intial to the empty string
            String title = "";
            String link = "";
            String dato = "";
            String guid = "";

            // First create a new XMLInputFactory
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            // Setup a new eventReader
            InputStream in = read();
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
            // read the XML document
            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();
                if (event.isStartElement()) {
                    String localPart = event.asStartElement().getName()
                            .getLocalPart();
                    if (localPart.equals(ITEM)) {
                        if (isFeedHeader) {
                            isFeedHeader = false;
                            feed = new YTFeed();
                        }
                    }
                    else if(localPart.equals(GUID)) {
                        guid = eventReader.getElementText();
                    }
                    else if(localPart.equals(PUB_DATE)) {
                        dato = eventReader.getElementText();
                    }
                    else if (localPart.equals(TITLE)) {
                        title = eventReader.getElementText();
                    }
                    else if (localPart.equals(LINK)) {
                        link = eventReader.getElementText();
                    }
                } else if (event.isEndElement()) {
                    if (event.asEndElement().getName().getLocalPart() == (ITEM)) {
                        YTFeedMsg msg = new YTFeedMsg(guid, dato, title, link);
                        feed.getMessages().add(msg);
                    }
                }
            }
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        return feed;
    }

    private InputStream read() {
        try {
            return url.openStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}